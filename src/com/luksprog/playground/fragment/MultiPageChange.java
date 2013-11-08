package com.luksprog.playground.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.luksprog.playground.DataStore;
import com.luksprog.playground.MainCategories;
import com.luksprog.playground.R;
import com.luksprog.playground.fragment.MultiPageChange.NavigationFragment.NavigationCallback;

/**
 * See http://stackoverflow.com/questions/19743685/actionbar-tabs-and-multi-fragment-layout-with-viewpager
 * 
 * @author Luksprog
 *
 */
public class MultiPageChange extends ActionBarActivity {

	private static final int PAGER_ID = 1000;
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(PAGER_ID);
		SimplePageAdapter adapter = new SimplePageAdapter(
				getSupportFragmentManager(), mViewPager, getSupportActionBar());
		mViewPager.setAdapter(adapter);
		// setup the tab navigation, using 5 tabs
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().addTab(
				getSupportActionBar().newTab().setText("Tab1")
						.setTabListener(adapter));
		getSupportActionBar().addTab(
				getSupportActionBar().newTab().setText("Tab2")
						.setTabListener(adapter));
		getSupportActionBar().addTab(
				getSupportActionBar().newTab().setText("Tab3")
						.setTabListener(adapter));
		getSupportActionBar().addTab(
				getSupportActionBar().newTab().setText("Tab4")
						.setTabListener(adapter));
		getSupportActionBar().addTab(
				getSupportActionBar().newTab().setText("Tab5")
						.setTabListener(adapter));
		setContentView(mViewPager);
	}

	public static class MultiPageHandleFragment extends Fragment implements
			NavigationCallback {

		private static final String TAG_NAV = "tag_nav";
		private static final String TAG_DETAILS = "tag_details";
		private static final String SAVED_POSITION = "saved_position";
		private boolean mIsMultiPane = false;
		private NavigationFragment mNavigation;
		private DetailsFragment mDetails;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// two possible layouts(a layout that contains the details
			// FrameLayout is a multi pane layout holding both fragments)
			View content = inflater.inflate(R.layout.frag_multipage, container,
					false);
			FrameLayout detailsWrapper = (FrameLayout) content
					.findViewById(R.id.details);
			mNavigation = (NavigationFragment) getChildFragmentManager()
					.findFragmentByTag(TAG_NAV);
			if (mNavigation == null) {
				// we're starting fresh so make the list navigation fragment
				mNavigation = new NavigationFragment();
				getChildFragmentManager().beginTransaction()
						.add(R.id.navigation, mNavigation, TAG_NAV).commit();
			} else {
				// we already have this fragment in the FragmentManager, so we
				// most likely are dealing with a restore so update the selected
				// position if available
				if (savedInstanceState != null
						&& savedInstanceState.getInt(SAVED_POSITION) != AdapterView.INVALID_POSITION) {
					mNavigation.updatePosition(savedInstanceState
							.getInt(SAVED_POSITION));
				}
			}
			// is multi pane?
			mIsMultiPane = detailsWrapper != null;
			// if yes then do as we did for the NavigationFragment
			if (mIsMultiPane) {
				mDetails = (DetailsFragment) getChildFragmentManager()
						.findFragmentByTag(TAG_DETAILS);
				if (mDetails == null) {
					mDetails = new DetailsFragment();
					getChildFragmentManager().beginTransaction()
							.add(R.id.details, mDetails, TAG_DETAILS).commit();
				} else {
					if (savedInstanceState != null
							&& savedInstanceState.getInt(SAVED_POSITION) != AdapterView.INVALID_POSITION) {
						mDetails.updateDetails(DataStore.CHEESE[savedInstanceState
								.getInt(SAVED_POSITION)]);
					}
				}
			}
			return content;
		}

		@Override
		public void onItemSelected(int position, String theItem) {
			// handle the selection, either showing the details in the right
			// fragment, or by starting a new activity(or something else)
			if (mIsMultiPane) {
				mDetails.updateDetails(theItem);
			} else {
				Intent intent = new Intent(getActivity(), MainCategories.class);
				startActivity(intent);
			}
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			// save the current selected position to be ale to restore it as the
			// user plays with its phone.
			outState.putInt(SAVED_POSITION,
					mNavigation.isInLayout() ? mNavigation.getPosition()
							: AdapterView.INVALID_POSITION);
		}

	}

	public static class NavigationFragment extends ListFragment {

		private int mSelectedPosition = AdapterView.INVALID_POSITION;

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			((NavigationCallback) getParentFragment()).onItemSelected(position,
					DataStore.CHEESE[position]);
		}

		/**
		 * Retrieve the current selected position.
		 * 
		 * @return an int representing the current selection.
		 */
		public int getPosition() {
			if (getListView() != null) {
				return getListView().getCheckedItemPosition();
			}
			return AdapterView.INVALID_POSITION;
		}

		public void updatePosition(int position) {
			mSelectedPosition = position;
			// the null check is required because we might call this update
			// method before the view of the Fragment was even created!
			// if the view hasn't yet been created then simply update the stored
			// position and let the fragment handle the update process when the
			// view becomes available
			if (getView() != null) {
				getListView().setSelection(position);
			}
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, DataStore.CHEESE));
			if (mSelectedPosition != AdapterView.INVALID_POSITION) {
				getListView().setItemChecked(mSelectedPosition, true);
			}
		}

		/**
		 * Callback interface to signal selection events. I used this to let the
		 * parent fragment to handle the events to avoid having this fragment
		 * searching on its own the other available fragments.
		 * 
		 * @author Luksprog
		 * 
		 */
		public interface NavigationCallback {

			void onItemSelected(int position, String theItem);

		}

	}

	/**
	 * This fragment is responsible for showing the details of the selection if
	 * we are in the multi pane mode.
	 * 
	 * @author Luksprog
	 * 
	 */
	public static class DetailsFragment extends Fragment {

		private static final int DETAILS_VIEW_ID = 1000;
		private String mWhichItem;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView details = new TextView(getActivity());
			details.setId(DETAILS_VIEW_ID);
			details.setText("Showing details for : " + mWhichItem);
			return details;
		}

		public void updateDetails(String whichItem) {
			mWhichItem = whichItem;
			// the null check is required because we might call this update
			// method before the view of the Fragment was even created!
			// if the view hasn't yet been created then simply update the stored
			// position and let the fragment handle the update process when the
			// view becomes available
			if (getView() != null) {
				((TextView) getView().findViewById(DETAILS_VIEW_ID))
						.setText("Showing details for : " + mWhichItem);
			}
		}

	}

	public static class BasicFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView tv = new TextView(getActivity());
			tv.setText("This is a basic fragment!");
			return tv;
		}

	}

	/**
	 * Simple subclass of FragmentPagerAdapter which implements all the required
	 * interfaces to properly handle the ViewPager-ActionBar tabs
	 * synchronization.
	 * 
	 * @author Luksprog
	 * 
	 */
	private static class SimplePageAdapter extends FragmentPagerAdapter
			implements ActionBar.TabListener, OnPageChangeListener {

		private ViewPager mViewPager;
		private ActionBar mActionBar;

		public SimplePageAdapter(FragmentManager fm, ViewPager pager,
				ActionBar actionBar) {
			super(fm);
			mViewPager = pager;
			mViewPager.setOnPageChangeListener(this);
			mActionBar = actionBar;
		}

		@Override
		public Fragment getItem(int position) {
			// for simplicity the first fragment of the ViewPager is the one
			// handling the multi pane layout, all aother fragments are basic
			// fragments
			if (position == 0) {
				return new MultiPageHandleFragment();
			} else {
				return new BasicFragment();
			}
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// nothing
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// a tab has been selected so move the ViewPager to the proper page
			mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			// the user swiped to another page so update the ActionBar tab
			// references.
			mActionBar.setSelectedNavigationItem(position);
		}

	}
}
