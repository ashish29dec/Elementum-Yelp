package com.elementum.yelp.search.utils;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public final class BitmapLruCache extends LruCache<String, Bitmap> implements
		ImageCache {

	public BitmapLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	public int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024; // convert byte
																// to MB
	}

	@Override
	public Bitmap getBitmap(String url) {
		return (Bitmap) this.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		this.put(url, bitmap);
	}

	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		return cacheSize;
	}

}
