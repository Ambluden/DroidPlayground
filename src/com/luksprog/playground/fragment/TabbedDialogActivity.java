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
package com.luksprog.playground.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.luksprog.playground.R;

/**
 * @see http
 *      ://stackoverflow.com/questions/16806716/tabbed-dialog-with-fragments-
 *      in-widget
 * 
 * @author Luksprog
 * 
 */
public class TabbedDialogActivity extends FragmentActivity implements
		ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener,
		TabContentFactory {

	private static final int NUMBER_OF_PAGES = 4;
	private ViewPager mViewPager;
	private TabHost mTabHost;
	private PagerContent mPagerContent;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frag_tabbeddialogactivity);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		// TabHost initialization
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		for (int i = 0; i < NUMBER_OF_PAGES; i++) {
			mTabHost.addTab(mTabHost.newTabSpec("tab" + i).setContent(this)
					.setIndicator("Tab no." + i));
		}
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(0);
		// ViewPager initialization
		mPagerContent = new PagerContent(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerContent);
		mViewPager.setOnPageChangeListener(this);
	}

	static class PagerContent extends FragmentPagerAdapter {

		public PagerContent(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return PageFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return NUMBER_OF_PAGES;
		}

	}

	public static class PageFragment extends Fragment {

		public static final String PAGE_IDENTIFIER = "page_identifier";

		public static Fragment newInstance(int pageNumber) {
			PageFragment page = new PageFragment();
			Bundle args = new Bundle();
			args.putInt(PAGE_IDENTIFIER, pageNumber);
			page.setArguments(args);
			return page;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			FrameLayout content = new FrameLayout(getActivity());
			TextView tv = new TextView(getActivity());
			tv.setText("Tab no." + getArguments().getInt(PAGE_IDENTIFIER));
			content.addView(tv, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
			return content;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		mTabHost.setCurrentTab(position);
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.e("XXX", tabId);
		mViewPager.setCurrentItem(Integer.parseInt(tabId.substring(tabId
				.length() - 1)));
	}

	@Override
	public View createTabContent(String tag) {
		View dummyContent = new View(this);
		dummyContent.setMinimumHeight(0);
		return dummyContent;
	}

}
