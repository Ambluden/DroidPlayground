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
package com.luksprog.playground.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.luksprog.playground.R;

public class ExpandableListViewExample extends Activity {

	// the keys mapping to the data in the HashMaps for groups and child rows
	private static final String GROUP_TEXT = "group_text";
	private static final String GROUP_STATUS = "group_status";
	private static final String CHILD_IMAGE = "child_image";
	private static final String CHILD_STATUS = "child_status";
	private static final String CHILD_TEXT = "child_text";
	private ExpandableListView mExpandableList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mExpandableList = new ExpandableListView(this);
		setContentView(mExpandableList);
		mExpandableList.setAdapter(new CustomExpandableAdapter(this,
				makeGroupData(), makeChildData()));
	}

	/**
	 * Our custom adapter. In this adapter all groups have a layout containing a
	 * TextView and a CheckBox. Child rows have two possible layouts: the first
	 * child row of each group has a layout containing a ImageView and a
	 * CheckBox, any other child rows contain only a TextView.
	 * 
	 * @author Luksprog
	 * 
	 */
	private static class CustomExpandableAdapter extends
			BaseExpandableListAdapter {

		// identifiers for our two types of rows, if the child rows are the same
		// this aren't required.
		private static final int FIRST_CHILD = 0;
		private static final int OTHER_CHILD = 1;

		private LayoutInflater mInflater;
		private List<HashMap<String, Object>> mGroupData;
		private List<ArrayList<HashMap<String, Object>>> mChildData;

		public CustomExpandableAdapter(Context context,
				List<HashMap<String, Object>> makeGroupData,
				List<ArrayList<HashMap<String, Object>>> makeChildData) {
			mInflater = LayoutInflater.from(context);
			mGroupData = makeGroupData;
			mChildData = makeChildData;
		}

		@Override
		public int getChildType(int groupPosition, int childPosition) {
			if (childPosition == 0) {
				return FIRST_CHILD; // this is the first child row so return
									// FIRST_CHILD as the type of row
			}
			return OTHER_CHILD;
		}

		@Override
		public int getChildTypeCount() {
			return 2; // two types of children rows
		}

		@Override
		public HashMap<String, Object> getChild(int groupPosition,
				int childPosition) {
			return mChildData.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// if we don't have a recycled row available inflate one BASED on
			// the type of row this child should have.
			int type = getChildType(groupPosition, childPosition);
			ChildViewHolder holder;
			if (convertView == null) {
				holder = new ChildViewHolder();
				switch (type) {
				case FIRST_CHILD:
					convertView = mInflater.inflate(
							R.layout.view_expandlistchild, parent, false);
					holder.image = (ImageView) convertView
							.findViewById(R.id.imageViewChild);
					holder.check = (CheckBox) convertView
							.findViewById(R.id.checkBoxChild);
					break;
				case OTHER_CHILD:
					convertView = mInflater.inflate(
							android.R.layout.simple_list_item_1, parent, false);
					holder.text = (TextView) convertView
							.findViewById(android.R.id.text1);
					break;
				}
				convertView.setTag(holder);
			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			final HashMap<String, Object> item = getChild(groupPosition,
					childPosition);
			// we set the data on the row based on the type of the row(so we
			// access only the views we do have in the layout)
			switch (type) {
			case FIRST_CHILD:
				holder.image.setImageResource((Integer) item.get(CHILD_IMAGE));
				// pass in the checked listener this as a tag so we can identify
				// the proper data position and update it
				holder.check.setTag(new PositionsWrapper(groupPosition,
						childPosition));
				holder.check
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// set the new status of the checked item
								// otherwise the status will be erased as the
								// user scrolls down and up
								PositionsWrapper pw = (PositionsWrapper) buttonView
										.getTag();
								mChildData.get(pw.groupPosition)
										.get(pw.childPosition)
										.put(CHILD_STATUS, isChecked);
							}
						});
				holder.check.setChecked((Boolean) item.get(CHILD_STATUS));
				break;
			case OTHER_CHILD:
				holder.text.setText((CharSequence) item.get(CHILD_TEXT));
				break;
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mChildData.get(groupPosition).size();
		}

		@Override
		public HashMap<String, Object> getGroup(int groupPosition) {
			return mGroupData.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mGroupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// normal row building like in any custom adapter
			GroupViewHolder holder;
			if (convertView == null) {
				holder = new GroupViewHolder();
				convertView = mInflater.inflate(R.layout.view_expandlistgroup,
						parent, false);
				holder.text = (TextView) convertView
						.findViewById(R.id.textGroup);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkBoxGroup);
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			final HashMap<String, Object> item = getGroup(groupPosition);
			holder.text.setText((CharSequence) item.get(GROUP_TEXT));
			holder.check.setTag(Integer.valueOf(groupPosition));
			holder.check
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// again, save the new status in the data list so we
							// keep the status as the user scrolls
							Integer groupPosition = (Integer) buttonView
									.getTag();
							mGroupData.get(groupPosition).put(GROUP_STATUS,
									isChecked);
						}
					});
			holder.check.setChecked((Boolean) item.get(GROUP_STATUS));
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		/**
		 * Simple class that wraps two integers representing the group and child
		 * row position.
		 * 
		 * @author Luksprog
		 * 
		 */
		private static class PositionsWrapper {

			int groupPosition;
			int childPosition;

			PositionsWrapper(int groupPosition, int childPosition) {
				this.groupPosition = groupPosition;
				this.childPosition = childPosition;
			}

		}

		// basic ViewHolder classes
		private static class GroupViewHolder {
			TextView text;
			CheckBox check;
		}

		private static class ChildViewHolder {
			ImageView image;
			CheckBox check;
			TextView text;
		}

	}

	/**
	 * Create dummy data. This will be the data for a single group which has
	 * some text and a CheckBox(initially unchecked). Each HashMap represents
	 * the data for a group.
	 * 
	 * @return the data
	 */
	private List<HashMap<String, Object>> makeGroupData() {
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 20; i++) {
			HashMap<String, Object> group = new HashMap<String, Object>();
			group.put(GROUP_TEXT, "Group no." + i);
			group.put(GROUP_STATUS, false);
			data.add(group);
		}
		return data;
	}

	/**
	 * Create dummy data. Each list item(from the outer list) represents the
	 * entire data for a group and each HashMap represents the data for a single
	 * child row. Children are of two types the first child of a group has an
	 * ImageView and a CheckBox in the layout, any other child has only a
	 * textView.
	 * 
	 * @return the data
	 */
	private List<ArrayList<HashMap<String, Object>>> makeChildData() {
		List<ArrayList<HashMap<String, Object>>> data = new ArrayList<ArrayList<HashMap<String, Object>>>();
		for (int i = 0; i < 20; i++) {
			ArrayList<HashMap<String, Object>> row = new ArrayList<HashMap<String, Object>>();
			for (int j = 0; j < 4; j++) {
				HashMap<String, Object> child = new HashMap<String, Object>();
				if (j == 0) {
					child.put(CHILD_IMAGE, R.drawable.ic_launcher);
					child.put(CHILD_STATUS, false);
				} else {
					child.put(CHILD_TEXT, "Child text no." + j);
				}
				row.add(child);
			}
			data.add(row);
		}
		return data;
	}
}
