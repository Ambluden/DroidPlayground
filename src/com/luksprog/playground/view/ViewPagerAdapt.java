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
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Simple class to show a list of items stack together in a 8 size page in a
 * ViewPager. Each new item introduced in the middle of the List pushes the
 * other items.
 * 
 * @author Luksprog
 * 
 */
public class ViewPagerAdapt extends FragmentActivity {

	/**
	 * Constant for the number of items a ViewPager's page should hold.
	 */
	private static final int ITEMS_PER_PAGE = 8;
	/**
	 * The list of data.
	 */
	private List<MenuItem> mMenuItems = new ArrayList<MenuItem>();

	/**
	 * Simple holder class for our data. It holds a title, price and a boolean
	 * flag indicating that this is or isn't a title item.
	 * 
	 * @author Luksprog
	 * 
	 */
	class MenuItem {
		String name;
		String price;
		boolean isTitle;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		final ViewPager vp = new ViewPager(this);
		vp.setId(1000);
		setContentView(vp);
		vp.setAdapter(new CustomAdapter(this));
		// create our list of data
		for (int i = 0; i < 12; i++) {
			MenuItem mi = new MenuItem();
			if (i == 0) {
				// insert a title
				mi.isTitle = true;
				mi.name = "MEAT TITLE";
			} else {
				mi.isTitle = false;
				mi.name = "Meat no." + i;
				mi.price = "Meat price no. " + i;
			}
			mMenuItems.add(mi);
		}
		for (int i = 0; i < 12; i++) {
			MenuItem mi = new MenuItem();
			if (i == 0) {
				// insert a title
				mi.isTitle = true;
				mi.name = "FISH TITLE";
			} else {
				mi.isTitle = false;
				mi.name = "Fish no." + i;
				mi.price = "Fish price no. " + i;
			}
			mMenuItems.add(mi);
		}
		// example of updating the data, in this case with an extra Meat item.
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				MenuItem mi = new MenuItem();
				mi.name = "Meat extra";
				mi.price = "Meat price";
				mMenuItems.add(12, mi);
				vp.getAdapter().notifyDataSetChanged();
			}
		}, 7000);
	}

	/**
	 * This method will extract the set of data corresponding to the page
	 * identified by the position number.
	 * 
	 * @param position
	 *            the page for which the set is requested
	 * @return a list of MenuItems
	 */
	public List<MenuItem> getPage(int position) {
		List<MenuItem> mi = new ArrayList<MenuItem>();
		final int count = mMenuItems.size();
		final int start = position * ITEMS_PER_PAGE;
		final int end = Math.min(count, (position * ITEMS_PER_PAGE)
				+ ITEMS_PER_PAGE);
		for (int i = start; i < end; i++) {
			mi.add(mMenuItems.get(i));
		}
		return mi;
	}

	/**
	 * Method used to determine the number of pages of our ViewPager.
	 * 
	 * @return the number of pages.
	 */
	public int getSize() {
		final int count = mMenuItems.size();
		int size = count / ITEMS_PER_PAGE;
		final int extraPage = count % ITEMS_PER_PAGE;
		if (extraPage != 0) {
			size++;
		}
		return size;
	}

	/**
	 * Our custom adapter for the ViewPager.
	 * 
	 * @author Luksprog
	 * 
	 */
	private class CustomAdapter extends PagerAdapter {

		private ViewPagerAdapt mContext;

		public CustomAdapter(ViewPagerAdapt context) {
			mContext = context;
		}

		@Override
		public void destroyItem(View view, int arg1, Object object) {
			((ViewPager) view).removeView((LinearLayout) object);
		}

		@Override
		public int getCount() {
			return mContext.getSize();
		}

		@Override
		public Object instantiateItem(View view, int position) {
			// build the page, maybe you could have a look at using a ListView
			LinearLayout ll = new LinearLayout(mContext);
			ll.setOrientation(LinearLayout.VERTICAL);
			final List<MenuItem> data = mContext.getPage(position);
			for (int i = 0; i < data.size(); i++) {
				final MenuItem item = data.get(i);
				LinearLayout itemRow = new LinearLayout(mContext);
				itemRow.setOrientation(LinearLayout.HORIZONTAL);
				if (item.isTitle) {
					// for fun differentiate the page title with a blue color
					itemRow.setBackgroundColor(Color.BLUE);
				}
				TextView title = new TextView(mContext);
				title.setText(item.name);
				TextView price = new TextView(mContext);
				price.setText(item.price);
				itemRow.addView(title);
				itemRow.addView(price);
				ll.addView(itemRow);
			}
			((ViewPager) view).addView(ll);
			return ll;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}

}
