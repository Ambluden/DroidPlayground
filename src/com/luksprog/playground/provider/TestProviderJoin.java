package com.luksprog.playground.provider;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.luksprog.playground.provider.ProviderWithJoinsContract.Clients;
import com.luksprog.playground.provider.ProviderWithJoinsContract.Orders;

/**
 * See
 * http://stackoverflow.com/questions/26615289/android-content-provider-with-
 * sqliteopenhelper-and-multiple-tables
 * 
 * @author Luksprog
 *
 */
public class TestProviderJoin extends Activity {

	private LinearLayout mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView scroll = new ScrollView(this);
		setContentView(scroll);
		mContent = new LinearLayout(this);
		mContent.setOrientation(LinearLayout.VERTICAL);
		scroll.addView(mContent);
		// print the clients data
		Cursor data = getContentResolver().query(Clients.CONTENT_URI, null,
				null, null, null);
		if (data.moveToFirst()) {
			do {
				String output = "Client id: "
						+ data.getLong(data.getColumnIndex(Clients.CLIENT_ID))
						+ " | Client name: "
						+ data.getString(data.getColumnIndex(Clients.NAME))
						+ " | Client adress: "
						+ data.getString(data.getColumnIndex(Clients.ADRESS));
				addTextEntry(output);
			} while (data.moveToNext());
		} else {
			addTextEntry("No data in the clients table?!?!");
		}
		addSeparator();
		data = getContentResolver().query(Orders.CONTENT_URI, null, null, null,
				null);
		if (data.moveToFirst()) {
			do {
				String output = "Order id: "
						+ data.getLong(data.getColumnIndex(Orders.ORDER_ID))
						+ " | Order product: "
						+ data.getString(data.getColumnIndex(Orders.PRODUCT))
						+ " | Client id for order: "
						+ data.getString(data.getColumnIndex(Orders.CLIENT_ID));
				addTextEntry(output);
			} while (data.moveToNext());
		} else {
			addTextEntry("No data in the clients table?!?!");
		}
		addSeparator();
		String[] projection = { Orders.TABLE_NAME + "." + Orders.ORDER_ID,
				Orders.PRODUCT, Clients.NAME, Clients.ADRESS };
		data = getContentResolver().query(Orders.CONTENT_URI_JOINED,
				projection, null, null, null);
		if (data.moveToFirst()) {
			do {
				String output = "Order id: "
						+ data.getLong(data.getColumnIndex(Orders.ORDER_ID))
						+ " | Order product: "
						+ data.getString(data.getColumnIndex(Orders.PRODUCT))
						+ " | for client: "
						+ data.getString(data.getColumnIndex(Clients.NAME))
						+ " | with adress: "
						+ data.getString(data.getColumnIndex(Clients.ADRESS));
				addTextEntry(output);
			} while (data.moveToNext());
		} else {
			addTextEntry("No data for the joined table?!");
		}
		addSeparator();		
	}

	private void addTextEntry(String data) {
		TextView text = new TextView(this);
		text.setText(data);
		text.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mContent.addView(text);
	}

	private void addSeparator() {
		View s = new View(this);
		s.setBackgroundColor(Color.GREEN);
		s.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 5));
		mContent.addView(s);
	}

}
