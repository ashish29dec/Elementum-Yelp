package com.elementum.yelp.search.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroupOverlay;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.elementum.yelp.search.Constants;
import com.elementum.yelp.search.YelpSearchApplication;
import com.elementum.yelp.search.api.YelpAPI;
import com.elementum.yelp.search.utils.Request;
import com.elementum.yelp.search.utils.Response;
import com.elementum.yelp.search.utils.Response.Business;
import com.elementum.yelp.search.R;
import com.google.gson.Gson;

public class MainActivity extends FragmentActivity {

	private static final int MAX_RESULT = 10;
	private ListView listView;
	private View loadingView;
	private View errorView;
	private ResultsAdapter adapter;
	
	private boolean requestInProgress;
	private SearchTask searchTask;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_main);
		requestInProgress = false;
		
		Log.i("Elementum", "onCreate");
		
		listView = (ListView) findViewById(R.id.list);
		final EditText what = (EditText) findViewById(R.id.id_what);
		final EditText where = (EditText) findViewById(R.id.id_where);
		loadingView = findViewById(R.id.loading);
		errorView = findViewById(R.id.no_results);
		adapter = new ResultsAdapter(getApplicationContext(), null);
		listView.setAdapter(adapter);
		
		Button button = (Button) findViewById(R.id.id_search);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard(v);
				search(what.getText().toString(), where.getText().toString());
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.i("Elementum", "onSaveInstanceState");
		
		// Saving the list
		Response response = YelpSearchApplication.instance.getResponse();
		if (response != null && response.businesses != null && response.businesses.size() > 0) {
			outState.putInt(Constants.KEY_NUM_RESULTS, response.businesses.size());
			for (int i = 0; i < response.businesses.size(); i++) {
				outState.putParcelable(Constants.KEY_BUSINESS_INDEX + i, response.businesses.get(i));
			}
		} else {
			outState.putInt(Constants.KEY_NUM_RESULTS, 0);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.i("Elementum", "onRestoreInstanceState");
		
		// Read the saved list
		int num = savedInstanceState.getInt(Constants.KEY_NUM_RESULTS);
		Log.i("Elementum", "Num results: " + num);
		if (num > 0) {
			Response response = new Response();
			response.businesses = new ArrayList<Response.Business>();
			for (int i = 0; i < num; i++) {
				Business business = savedInstanceState.getParcelable(Constants.KEY_BUSINESS_INDEX + i);
				response.businesses.add(business);
			}
			setResult(response);
			listView.setVisibility(View.VISIBLE);
		}
	}

	protected void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	protected void search(String what, String where) {

		if (what.trim().length() == 0 || where.trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.input_error,
					Toast.LENGTH_LONG).show();
			return;
		}

		Request request = new Request();
		request.what = what;
		request.where = where;
		request.max = MAX_RESULT;
		
		// Making sure that no previous search request is in progress
		if (searchTask != null && requestInProgress) {
			searchTask.cancel(true);
			searchTask = null;
		}
		
		// Check Connectivity Status
		if (isConnectedToInternet()) {
			searchTask = new SearchTask();
			requestInProgress = true;
			searchTask.execute(request);
		} else {
			Toast.makeText(getApplicationContext(), R.string.not_connected,
					Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	private boolean isConnectedToInternet() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return (info != null) && info.isConnected();
	}

	protected void setLoading(boolean isLoading) {
		if (isLoading) {
			loadingView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			errorView.setVisibility(View.GONE);
		} else {
			loadingView.setVisibility(View.GONE);
		}
	}

	protected void onResponse() {
		setLoading(false);
		if (adapter.getCount() == 0) {
			listView.setVisibility(View.GONE);
			errorView.setVisibility(View.VISIBLE);
		} else {
			listView.setVisibility(View.VISIBLE);
			errorView.setVisibility(View.GONE);
		}
	}
	
	public void setResult(Response response) {
		YelpSearchApplication.instance.setResponse(response);
		adapter.setResult(response);
	}

	private class SearchTask extends
			AsyncTask<Request, Void, Response> {

		@Override
		protected void onPreExecute() {
			setLoading(true);
		}

		@Override
		protected Response doInBackground(Request... params) {

			if (params == null || params.length == 0)
				return null;

			Request request = params[0];

			YelpAPI api = new YelpAPI(Constants.CONSUMER_KEY,
					Constants.CONSUMER_SECRET, Constants.TOKEN,
					Constants.TOKEN_SECRET);
			Response response = null;
			try {
				String searchResponse = api.searchForBusinesses(request);
				response = new Gson().fromJson(searchResponse,
						Response.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(Response result) {
			if (isFinishing() || isCancelled()) {
				requestInProgress = false;
				return;
			}
			if (result == null) {
				// Error, do not erase previous result.
				Toast.makeText(getApplicationContext(), R.string.search_error,
						Toast.LENGTH_LONG).show();
				setLoading(false);
			} else {
				requestInProgress = false;
				setResult(result);
				onResponse();
			}
		}
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			requestInProgress = false;
		}
	}
}
