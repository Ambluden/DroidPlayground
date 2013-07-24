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
package com.luksprog.playground.adapter;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.luksprog.playground.R;

/**
 * @see http://stackoverflow.com/questions/17815137/disable-a-item-in-the-adapter-of-an-adapterview
 *
 */
public class PreferenceLikeAdapter extends ListActivity {

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// check if this is a master position, if it is then update the CheckBox
		// status and call notifyDataSetChanged on the adapter
		if (((CustomAdapter) l.getAdapter()).currentStatus.get(position) != null) {
			CheckBox ckb = (CheckBox) v.findViewById(R.id.checkBox1);
			((CustomAdapter) l.getAdapter()).currentStatus.put(position,
					!ckb.isChecked());
			((CustomAdapter) l.getAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// dummy data
		String[] data = { "Enable push notification",
				"Push notification settings", "Other settings" };
		setListAdapter(new CustomAdapter(this,
				R.layout.adapt_preferencelike_row, R.id.textView1, data));
	}

	private static class CustomAdapter extends ArrayAdapter<String> {

		/**
		 * This will map a row position to a boolean value, representing the
		 * status of the CheckBox from that row. A value present for a row here
		 * also means that the next row is considered dependent on this row's
		 * CheckBox status
		 */
		SparseArray<Boolean> currentStatus = new SparseArray<Boolean>();

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
			// simulate that we start with the first row checked.(we could start
			// with the CheckBox unselected in which case we would call
			// currentStatus(0, false)
			currentStatus.put(0, true);
		}

		@Override
		public boolean isEnabled(int position) {
			// if we aren't at the first position and this is a master
			// preference(indicating by an entry being present in currentStatus)
			// then return the stored value for the CheckBox indicating that the
			// item is enabled or not
			if (position != 0 && currentStatus.get(position - 1) != null) {
				return currentStatus.get(position - 1);
			}
			return true;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			CheckBox ckb = (CheckBox) row.findViewById(R.id.checkBox1);
			ckb.setFocusable(false);
			// first restore the CheckBox status
			if (currentStatus.get(position) != null) {
				ckb.setChecked(currentStatus.get(position));
			}
			// if a previous row is a master then check it's CheckBox status and
			// update the visual appearance of this row to disable or enable it
			if (position != 0 && currentStatus.get(position - 1) != null) {
				if (currentStatus.get(position - 1)) {
					((TextView) row.findViewById(R.id.textView1))
							.setTextColor(Color.BLACK);
					ckb.setEnabled(true);
				} else {
					((TextView) row.findViewById(R.id.textView1))
							.setTextColor(Color.GRAY);
					ckb.setEnabled(false);
				}
			}
			return row;
		}

	}

}
