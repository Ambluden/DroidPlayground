package com.luksprog.playground.provider;

import java.util.Random;

import com.luksprog.playground.provider.ProviderWithJoinsContract.Orders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import static com.luksprog.playground.provider.ProviderWithJoinsContract.*;

public class ProviderWithJoins extends ContentProvider {

	private static final int CLIENT = 1;
	private static final int CLIENTS = 2;
	private static final int ORDER = 3;
	private static final int ORDERS = 4;
	private static final int JOINED_ORDER = 5;
	private static final int JOINED_ORDERS = 6;
	private Database mDatabase;
	/**
	 * The UriMatcher will match the Uri we get in the provider's methods to our
	 * above defined codes, based on the codes we get we will query specific
	 * tables and do extra operations like joins etc
	 */
	private static UriMatcher mMatcher;

	static {
		mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mMatcher.addURI(AUTHORITHY, "/" + Clients.TABLE_NAME, CLIENTS);
		mMatcher.addURI(AUTHORITHY, "/" + Clients.TABLE_NAME + "/#", CLIENT);
		mMatcher.addURI(AUTHORITHY, "/" + Orders.TABLE_NAME, ORDERS);
		mMatcher.addURI(AUTHORITHY, "/" + Orders.TABLE_NAME + "/#", ORDER);
		mMatcher.addURI(AUTHORITHY, "/" + Orders.TABLE_NAME + "/join",
				JOINED_ORDERS);
		mMatcher.addURI(AUTHORITHY, "/" + Orders.TABLE_NAME + "/join/#",
				JOINED_ORDER);
	}

	@Override
	public boolean onCreate() {
		mDatabase = new Database(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String table = null;
		Cursor result;
		String extraQuery;
		switch (mMatcher.match(uri)) {
		case CLIENTS:
			table = Clients.TABLE_NAME;
			extraQuery = null;
			break;
		case CLIENT:
			table = Clients.TABLE_NAME;
			// this corresponds to a single entry in the clients table so we
			// need to append this extra query to only select a single
			// client(the same goes for the orders and joined orders Uris0
			extraQuery = Clients.CLIENT_ID + "=" + uri.getLastPathSegment();
			break;
		case ORDERS:
			table = Orders.TABLE_NAME;
			extraQuery = null;
			break;
		case ORDER:
			table = Orders.TABLE_NAME;
			extraQuery = Orders.CLIENT_ID + "=" + uri.getLastPathSegment();
			break;
		// when we get one of the JOINED_ORDERS and JOINED_ORDER Uris it's a
		// signal that
		// we should do a join between the two tables we have
		case JOINED_ORDERS:
			table = Orders.TABLE_NAME + "," + Clients.TABLE_NAME;
			extraQuery = Clients.TABLE_NAME + "." + Clients.CLIENT_ID + "="
					+ Orders.TABLE_NAME + "." + Orders.CLIENT_ID;
			break;
		case JOINED_ORDER:
			// instead of this we could implement a direct rawQuery against the
			// database if we want.
			table = Orders.TABLE_NAME + "," + Clients.TABLE_NAME;
			extraQuery = Clients.TABLE_NAME + "." + Clients.CLIENT_ID + "="
					+ Orders.TABLE_NAME + "." + Orders.CLIENT_ID + " AND "
					+ Orders.CLIENT_ID + "=" + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Wrong uri: " + uri);
		}
		if (TextUtils.isEmpty(selection)) {
			selection = extraQuery;
		} else {
			selection += " AND " + extraQuery;
		}
		final SQLiteDatabase db = mDatabase.getReadableDatabase();
		result = db.query(table, projection, selection, selectionArgs, null,
				null, sortOrder);
		return result;
	}

	@Override
	public String getType(Uri uri) {
		switch (mMatcher.match(uri)) {
		case CLIENT:
			return Clients.CONTENT_ITEM_TYPE;
		case CLIENTS:
			return Clients.CONTENT_TYPE;
		case ORDER:
		case JOINED_ORDER:
			return Orders.CONTENT_ITEM_TYPE;
		case ORDERS:
		case JOINED_ORDERS:
			return Orders.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Wrong uri: " + uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static class Database extends SQLiteOpenHelper {

		private static final String DB_NAME = "providerwithjoins.db";
		private static final int DB_VERSION = 1;

		public Database(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + Clients.TABLE_NAME + "("
					+ Clients.CLIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Clients.NAME + " TEXT," + Clients.ADRESS + " TEXT)";
			db.execSQL(sql);
			sql = "CREATE TABLE " + Orders.TABLE_NAME + "(" + Orders.ORDER_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Orders.PRODUCT
					+ " TEXT," + Orders.CLIENT_ID + " INTEGER)";
			db.execSQL(sql);
			addDummyData(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO should be implemented
		}

		private void addDummyData(SQLiteDatabase db) {
			// add some data in the clients table
			String[][] clientsToInsert = { { "John Doe", "John's adress" },
					{ "Brian Doe", "Brian's adress" },
					{ "Emily Doe", "Emily's adress" },
					{ "Victor Doe", "Victor's adress" } };
			long[] insertedIds = new long[clientsToInsert.length];

			for (int i = 0; i < clientsToInsert.length; i++) {
				String[] person = clientsToInsert[i];
				ContentValues cv = new ContentValues();
				cv.put(Clients.NAME, person[0]);
				cv.put(Clients.ADRESS, person[1]);
				insertedIds[i] = db.insert(Clients.TABLE_NAME, null, cv);
				if (insertedIds[i] < 0) {
					throw new IllegalStateException();
				}
			}
			Random rand = new Random();
			String[] ordersToInsert = { "car", "toaster", "computer", "food",
					"clothes", "books" };
			for (String s : ordersToInsert) {
				ContentValues cv = new ContentValues();
				cv.put(Orders.PRODUCT, s);
				// get a random value from the above ids
				cv.put(Orders.CLIENT_ID,
						insertedIds[rand.nextInt(insertedIds.length)]);
				db.insert(Orders.TABLE_NAME, null, cv);
			}
		}

	}

}
