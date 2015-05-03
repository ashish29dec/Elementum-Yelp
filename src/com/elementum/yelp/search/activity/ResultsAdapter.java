package com.elementum.yelp.search.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.elementum.yelp.search.YelpSearchApplication;
import com.elementum.yelp.search.utils.Response;
import com.elementum.yelp.search.utils.Response.Business;
import com.elementum.yelp.search.R;

public class ResultsAdapter extends BaseAdapter {

	private Context context;
	private Response response;

	public ResultsAdapter(Context context,
			Response searchResponse) {

		this.context = context;
		this.response = searchResponse;
	}

	@Override
	public int getCount() {
		if (isEmpty())
			return 0;
		return response.businesses.size();
	}

	@Override
	public Object getItem(int position) {
		if (isEmpty() || response.businesses.size() <= position)
			return null;
		return response.businesses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.search_item, null);
			holder = new ViewHolder();
			holder.thumbnail = (NetworkImageView) convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.rating = (NetworkImageView) convertView.findViewById(R.id.rattingImage);
			holder.reviews = (TextView) convertView.findViewById(R.id.reviews);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			holder.category = (TextView) convertView.findViewById(R.id.category);
			convertView.setTag(holder);
		}
		
		holder = (ViewHolder) convertView.getTag();
		final Business business = response.businesses.get(position);
		ImageLoader imageLoader = YelpSearchApplication.instance.getImageLoader();
		holder.thumbnail.setImageUrl(business.image_url, imageLoader);
		holder.name.setText((position + 1) + ". " + business.name);
		holder.rating.setImageUrl(business.rating_img_url_large,
				imageLoader);
		holder.reviews.setText(business.review_count + " "
				+ convertView.getContext().getString(R.string.reviews));
		String address = business.location.display_address.get(0) + ", "
				+ business.location.city;
		holder.address.setText(address);
		if (business.categories != null && business.categories.size() > 0) {
			holder.category.setText(business.categories.get(0)[0]);
		} else {
			holder.category.setText("");
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(business.mobile_url));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				v.getContext().startActivity(intent);
			}
		});
		return convertView;
	}

	public boolean isEmpty() {
		return response == null || response.businesses == null
				|| response.businesses.size() == 0;
	}

	private class ViewHolder {

		NetworkImageView thumbnail;
		TextView name;
		NetworkImageView rating;
		TextView reviews;
		TextView address;
		TextView category;
	}

	public void setResult(Response response) {
		this.response = response;
		notifyDataSetChanged();
	}
}
