package com.luksprog.playground.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderWithJoinsContract {

	public static final String AUTHORITHY = "com.luksprog.providerwithjoins";

	private ProviderWithJoinsContract() {
		// no instantiation
	}

	public static class Clients {

		private Clients() {
			// no instantiation
		}

		public static final String TABLE_NAME = "clients";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITHY + "/" + TABLE_NAME);
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.luksprog.providerwithjoins.clients";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.luksprog.providerwithjoins.clients";
		public static final String CLIENT_ID = BaseColumns._ID;
		public static final String NAME = "client_name";
		public static final String ADRESS = "client_adress";
	}

	public static class Orders {

		private Orders() {
			// no instantiation
		}

		public static final String TABLE_NAME = "orders";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITHY + "/" + TABLE_NAME);
		public static final Uri CONTENT_URI_JOINED = Uri.parse("content://"
				+ AUTHORITHY + "/" + TABLE_NAME + "/join");
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.luksprog.providerwithjoins.orders";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.luksprog.providerwithjoins.orders";
		public static final String ORDER_ID = BaseColumns._ID;
		public static final String CLIENT_ID = "client_id";
		public static final String PRODUCT = "product_ordered";

	}

}
