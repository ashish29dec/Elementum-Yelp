package com.elementum.yelp.search;

import android.app.Application;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.elementum.yelp.search.utils.BitmapLruCache;
import com.elementum.yelp.search.utils.Response;

public class YelpSearchApplication extends Application {

	public static YelpSearchApplication instance;
	private ImageLoader imageLoader;
	private Response searchResponse;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		ImageLoader.ImageCache imageCache = new BitmapLruCache(
				BitmapLruCache.getDefaultLruCacheSize());
		imageLoader = new ImageLoader(Volley.newRequestQueue(this), imageCache);
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	
	public void setResponse(Response response) {
		searchResponse = response;
	}
	
	public Response getResponse() {
		return searchResponse;
	}
}
