package com.luksprog.playground.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.luksprog.playground.DataStore;

/**
 * See:
 * http://stackoverflow.com/questions/25946509/how-to-control-viewpages-pages
 * -from-another-page
 * 
 * @author Luksprog
 *
 */
@SuppressLint("NewApi")
public class ListPagerOffset extends FragmentActivity {

	private ViewPager mPager;
	private static final int ID = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPager = new ViewPager(this);
		mPager.setAdapter(new CustomAdapter(getSupportFragmentManager()));
		mPager.setId(ID);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float offset,
					int pixelsOffset) {
				// hacky way of getting a reference to the current Fragment in a
				// ViewPager
				final TestFragment tf = (TestFragment) getSupportFragmentManager()
						.findFragmentByTag(
								"android:switcher:" + ID + ":" + position);
				final View[] rows = tf.getRows();
				final int count = rows.length;
				if (offset == 0) {
					tf.clear(); // return to the initial state
					return;
				}
				for (int i = 0; i < count; i++) {
					final View row = rows[i];
					// this will offset each row in relation to the pixels
					// offset, rows at the bottom
					// getting a higher offset value.
					int step = (int) (pixelsOffset * (i * 0.05));
					row.setX(-step);
				}
			}

			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		setContentView(mPager);
	}

	private static class CustomAdapter extends FragmentPagerAdapter {

		public CustomAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			return new TestFragment();
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	/**
	 * Some dummy list fragment to showcase the effect.
	 * 
	 * @author Luksprog
	 *
	 */
	public static class TestFragment extends ListFragment {

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, DataStore.CHEESE) {

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// overridden to shift to the right the row content for
					// better visibility
					View row = super.getView(position, convertView, parent);
					TextView text = (TextView) row
							.findViewById(android.R.id.text1);
					text.setGravity(Gravity.RIGHT);
					text.setBackgroundColor(Color.parseColor("#0099cc"));
					return row;
				}
			});
		}

		/**
		 * Offer a reference to the currently visible rows in the ListView.
		 * 
		 * @return an array containing the current visible row views
		 */
		public View[] getRows() {
			View[] rows = null;
			ListView lv = getListView();
			if (lv == null) {
				rows = new View[0];
			} else {
				rows = new View[lv.getChildCount()];
				for (int i = 0; i < lv.getChildCount(); i++) {
					rows[i] = lv.getChildAt(i);
				}
			}
			return rows;
		}

		/**
		 * Shift all the visible rows to the initial 0 position
		 */
		public void clear() {
			final View[] rows = getRows();
			for (View v : rows) {
				v.setX(0.0f);
			}
		}
	}

}
