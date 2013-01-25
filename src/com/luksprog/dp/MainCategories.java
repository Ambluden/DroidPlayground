/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
