package com.luksprog.dp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainCategories extends ListActivity {

	private static final String[] CATEGORIES = { "ANIMATIONS", "SERVICES",
			"RESOURCES", "FRAGMENTS", "MISCELANOUS", "ADAPTERS", "THREADS",
			"APP", "VIEWS", "PROVIDERS" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, CATEGORIES));		
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, CategoryActivities.class);
		i.putExtra("the_position", position);
		startActivity(i);
	}

	
}
