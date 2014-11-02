package com.luksprog.playground.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderWithJoinsContract {

	public static final String AUTHORITHY = "com.luksprog.providerwithjoins";

	private ProviderWithJoinsContract() {
		// no instantiation
	}

	/**
	 * This class holds the references corresponding to the clients table in the
	 * database
	 * 
	 * @author Luksprog
	 *
	 */
	public static class Clients {

		private Clients() {
			// no instantiation
		}

		public static final String TABLE_NAME = "clients";
		/**
		 * This Uri will be used to retrieve data stored in the clients table
		 * from the provider
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITHY + "/" + TABLE_NAME);
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.luksprog.providerwithjoins.clients";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.luksprog.providerwithjoins.clients";
		public static final String CLIENT_ID = BaseColumns._ID;
		public static final String NAME = "client_name";
		public static final String ADRESS = "client_adress";
	}

	/**
	 * This class holds the references corresponding to the orders table in the
	 * database
	 * 
	 * @author Luksprog
	 *
	 */
	public static class Orders {

		private Orders() {
			// no instantiation
		}

		public static final String TABLE_NAME = "orders";
		/**
		 * This Uri will be used to retrieve data stored in the orders table
		 * from the provider
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITHY + "/" + TABLE_NAME);
		/**
		 * This Uri will be used to retrieve all the data stored in the clients table
		 * from the provider by doing a join between the two tables.
		 */
		public static final Uri CONTENT_URI_JOINED = Uri.parse("content://"
				+ AUTHORITHY + "/" + TABLE_NAME + "/join");
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.luksprog.providerwithjoins.orders";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.luksprog.providerwithjoins.orders";
		public static final String ORDER_ID = BaseColumns._ID;
		public static final String CLIENT_ID = "client_id";
		public static final String PRODUCT = "product_ordered";

	}

}
