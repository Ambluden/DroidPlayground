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
package com.luksprog.playground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainCategories extends ListActivity {

	private static final String[] CATEGORIES = { "Views:", "Fragments",
			"Adapters:", "App:" };
	private static final String[] CATEGORIES_DESCRIPTIONS = {
			"View related code, custom components,"
					+ " view creation/changes, special usess",
			"Fragment use samples",
			"Sample code related to building, changing and special usess of adapters",
			"Sample code related to Activity related apis" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < CATEGORIES.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("cat", CATEGORIES[i]);
			map.put("cat_desc", CATEGORIES_DESCRIPTIONS[i]);
			data.add(map);
		}
		setListAdapter(new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_2, new String[] { "cat",
						"cat_desc" }, new int[] { android.R.id.text1,
						android.R.id.text2 }));
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, CategoryActivities.class);
		i.putExtra("the_position", position);
		startActivity(i);
	}

}
