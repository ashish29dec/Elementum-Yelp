package com.elementum.yelp.search.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Response {

	public List<Business> businesses;
	public Region region;
	public int total;

	public static class Business implements Parcelable {

		public String id;
		public boolean is_claimed;
		public boolean is_closed;
		public String name;
		public String image_url;
		public String url;
		public String mobile_url;
		public String phone;
		public String display_phone;
		public int review_count;
		public List<String[]> categories;
		public float distance;
		public float rating;
		public String rating_img_url;
		public String rating_img_url_small;
		public String rating_img_url_large;
		public String snippet_text;
		public String snippet_image_url;
		public Location location;
		public List<Deals> deals;
		public List<GiftCertificate> gift_certificates;
		public String menu_provider;
		public long menu_date_updated;

		public static class Location {

			public List<String> address;
			public List<String> display_address;
			public String city;
			public String state_code;
			public String postal_code;
			public String country_code;
			public String cross_streets;
			public List<String> neighborhoods;
		}

		public class Deals {

			public String id;
			public String title;
			public String url;
			public String image_url;
			public String currency_code;
			public long time_start;
			public long time_end;
			public boolean is_popular;
			public String what_you_get;
			public String important_restrictions;
			public String additional_restrictions;
			public List<Options> options;

			public class Options {

				public String title;
				public String purchase_url;
				public int price;
				public String formatted_price;
				public int original_price;
				public String formatted_original_price;
				public boolean is_quantity_limited;
				public int remaining_count;
			}
		}

		public class GiftCertificate {

			public String id;
			public String url;
			public String image_url;
			public String currency_code;
			public String unused_balances;
			public List<Options> options;

			public class Options {

				public int price;
				public String formatted_price;
			}
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeString(image_url);
			dest.writeString(name);
			dest.writeString(rating_img_url_large);
			dest.writeInt(review_count);
			dest.writeString(location.display_address.get(0));
			dest.writeString(location.city);
			if (categories != null && categories.size() > 0) {
				dest.writeString(categories.get(0)[0]);
			} else {
				dest.writeString("");
			}
			dest.writeString(mobile_url);
		}
		
		public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Response.Business>() {

			@Override
			public Business createFromParcel(Parcel source) {
				// TODO Auto-generated method stub
				Business business = new Business();
				business.image_url = source.readString();
				business.name = source.readString();
				business.rating_img_url_large = source.readString();
				business.review_count = source.readInt();
				business.location = new Location();
				business.location.display_address = new ArrayList<String>();
				business.location.display_address.add(source.readString());
				business.location.city = source.readString();
				String[] categories = new String[] {source.readString()};
				business.categories = new ArrayList<String[]>();
				business.categories.add(categories);
				business.mobile_url = source.readString();
				return business;
			}

			@Override
			public Business[] newArray(int size) {
				// TODO Auto-generated method stub
				return new Business[size];
			}
			
		};
	}
	
	class Region {

		public Span span;
		public Center center;

		class Span {

			public double latitude_delta;
			public double longitude_delta;
		}

		class Center {

			public double latitude;
			public double longitude;
		}
	}
}
