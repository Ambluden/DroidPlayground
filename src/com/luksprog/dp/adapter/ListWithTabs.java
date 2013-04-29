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
package com.luksprog.dp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.luksprog.dp.R;

/**
 * A simple class demonstrating a ListView which simulate the presence of Tabs
 * at a certain level.
 * 
 * @author Luksprog
 * 
 */
public class ListWithTabs extends Activity {

	private ArrayList<TestData> mData = new ArrayList<TestData>();
	private ListView mList;
	private WrapperAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adapter_listwithtabs);
		// dummy data build up
		String[] aString = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
				"w", "x", "y", "z" };
		for (int i = 0; i < aString.length; i++) {
			mData.add(new TestData(i, aString[i]));
		}
		mList = (ListView) findViewById(R.id.mainlist);
		List<String> tab1data = new ArrayList<String>();
		for (int i = 0; i < 15; i++) {
			tab1data.add("Tab 1 row no." + i);
		}
		List<String> tab2data = new ArrayList<String>();
		for (int i = 0; i < 15; i++) {
			tab2data.add("Tab 1 row no." + i);
		}
		mAdapter = new WrapperAdapter(this, mData, tab1data, tab2data);
		mList.setAdapter(mAdapter);
	}

	private static class WrapperAdapter extends BaseAdapter implements
			OnTabChangeListener, TabContentFactory {

		// different row types
		private static final int NORMAL_ROW = 0;
		private static final int TAB_ROW = 1;
		private static final int TAB1_TYPE = 2;
		private static final int TAB2_TYPE = 3;
		// identifiers for our tabs
		private static final String TAB_ONE = "Tab 1";
		private static final String TAB_TWO = "Tab 2";
		private LayoutInflater mInflater;
		// the data for the main ListView and the tabs
		private List<TestData> mData;
		private List<String> mTab1Data;
		private List<String> mTab2Data;
		// current selected tab
		private int mCurrentSelectedTab = 0;
		private Context mContext;

		public WrapperAdapter(Context context, List<TestData> data,
				List<String> tab1data, List<String> tab2data) {
			mInflater = LayoutInflater.from(context);
			mData = data;
			mTab1Data = tab1data;
			mTab2Data = tab2data;
			mContext = context;
		}

		@Override
		public int getCount() {
			// you'll need extra calculations if the two tab list don't have the
			// same size.
			return mData.size()
					+ 1
					+ (mCurrentSelectedTab == 0 ? mTab1Data.size() : mTab2Data
							.size());
		}

		@Override
		public int getItemViewType(int position) {
			if (position < mData.size()) {
				// we are in the initial list's range so it's a normal row
				return NORMAL_ROW;
			} else if (position == mData.size()) {
				// we are at the tab level, so it's a tab row
				return TAB_ROW;
			} else {
				// based on the current selected tab(0 by default) we have
				// either a tab 1 row type or tab 2 row type
				return mCurrentSelectedTab == 0 ? TAB1_TYPE : TAB2_TYPE;
			}

		}

		@Override
		public int getViewTypeCount() {
			// if your list-tabs need different row types then you'll need to
			// make the calculations here and update appropriately
			return 4;
		}

		@Override
		public Object getItem(int position) {
			// get the data item for the appropriate position(for the tab list
			// we need to offset the position to have a valid indexes in the tab
			// data lists)
			if (position < mData.size()) {
				// we are in the initial list's range
				return mData.get(position);
			} else if (position == mData.size()) {
				// we are at the tab level
				return null;
			} else {
				int offset = position - mData.size() - 1;
				if (mCurrentSelectedTab == 0) {
					return mTab1Data.get(offset);
				} else {
					return mTab2Data.get(offset);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			int type = getItemViewType(position);
			if (convertView == null) {
				holder = new ViewHolder();
				switch (type) {
				case NORMAL_ROW:
					// this is the main ListView row
					convertView = mInflater.inflate(
							R.layout.adapter_listviewithtabs_normalrow, parent,
							false);
					holder.dataTitle = (TextView) convertView
							.findViewById(R.id.dataTitle);
					holder.dataValue = (TextView) convertView
							.findViewById(R.id.dataValue);
					holder.dataValueString = (TextView) convertView
							.findViewById(R.id.dataValueString);
					break;
				case TAB_ROW:
					// this is the row with the tabs. Build them and set the
					// listener
					convertView = mInflater
							.inflate(R.layout.adapter_listwithtabs_tabrow,
									parent, false);
					TabHost tabs = (TabHost) convertView
							.findViewById(android.R.id.tabhost);
					tabs.setup();
					TabSpec tab1 = tabs.newTabSpec(TAB_ONE);
					TabSpec tab2 = tabs.newTabSpec(TAB_TWO);
					tab1.setContent(this);
					tab2.setContent(this);
					tab1.setIndicator("One");
					tab2.setIndicator("two");
					tabs.addTab(tab1);
					tabs.addTab(tab2);
					tabs.setup();
					tabs.setOnTabChangedListener(this);
					break;
				case TAB1_TYPE:
					// I'm assuming that each of your tab's list will have a
					// different row type(if they are the same even better
					// you'll just concatenate thispieces of code)
					convertView = mInflater.inflate(
							R.layout.adapter_listwithtabs_tab1row, parent,
							false);
					holder.tabData = (TextView) convertView
							.findViewById(R.id.textView1);
					break;
				case TAB2_TYPE:
					convertView = mInflater.inflate(
							R.layout.adapter_listwithtabs_tab2row, parent,
							false);
					holder.tabData = (TextView) convertView
							.findViewById(R.id.textView1);
					break;
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// set the data based on the row type. For the ListView main row set
			// the TextViews, for the tab rows again set the text(no work neede
			// for our tabs row unless you want to change them
			switch (type) {
			case NORMAL_ROW:
				TestData td = (TestData) getItem(position);
				holder.dataTitle.setText(td.mText);
				holder.dataValue.setText(String.valueOf(td.mPosition));
				break;
			case TAB_ROW:
				break;
			case TAB1_TYPE:
			case TAB2_TYPE:
				String tabText = (String) getItem(position);
				holder.tabData.setText(tabText);
			}
			return convertView;
		}

		@Override
		public void onTabChanged(String tabId) {
			/*
			 * Handle the selection on our tabs. to avoid unnecessary work if
			 * the user selects the same tab do nothing otherwise update the
			 * mother ListView and its adapter.
			 */
			int oldSelection = mCurrentSelectedTab;
			if (tabId.equals(TAB_ONE)) {
				mCurrentSelectedTab = 0;
			} else {
				mCurrentSelectedTab = 1;
			}
			if (oldSelection != mCurrentSelectedTab) {
				notifyDataSetChanged();
			}
		}

		/**
		 * The implements the holder pattern. To have just a class you would
		 * normally put View references to all views from all the rows and only
		 * use them as needed on a per row basis(with no performance problems as
		 * the holder will only have initialized the proepr fields for that
		 * row).
		 * 
		 * @author Luksprog
		 * 
		 */
		private static class ViewHolder {
			// views for normal row
			TextView dataTitle;
			TextView dataValue;
			TextView dataValueString;
			// tab data
			TextView tabData;
		}

		@Override
		public View createTabContent(String tag) {
			/*
			 * our tabs need content so we implement this to make a dummy
			 * content, a view that does absolutely nothing
			 */
			View v = new View(mContext);
			return v;
		}

	}

	/**
	 * Dummy data class.
	 * 
	 * @author Luksprog
	 * 
	 */
	private static class TestData {

		private int mPosition;
		private String mText;

		public TestData(int position, String text) {
			mPosition = position;
			mText = text;
		}

	}
}
