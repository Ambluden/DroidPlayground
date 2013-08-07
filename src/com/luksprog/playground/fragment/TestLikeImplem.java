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

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *@see http://stackoverflow.com/questions/18097567/multiple-layout-viewpager-with-one-fragment#comment26502420_18097567 
 *
 */
public class TestLikeImplem extends FragmentActivity {

	private static final int PAGER_ID = 0x5555;
	private static final int Q_TYPE_1 = 0x1111;
	private static final int Q_TYPE_2 = 0x2222;
	private static final int Q_TYPE_3 = 0x3333;
	private static final int[] QUESTION_TYPES = { Q_TYPE_1, Q_TYPE_2, Q_TYPE_3 };
	private ViewPager mViewPager;
	private static int mPageCount = 0;
	private HashMap<Integer, QuestionData> mData = new HashMap<Integer, QuestionData>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getData();
		setupViewPager();
	}

	private void setupViewPager() {
		mViewPager = new ViewPager(this);
		mViewPager.setAdapter(new CustomAdapter(this
				.getSupportFragmentManager(), mData));
		mViewPager.setId(PAGER_ID);
		setContentView(mViewPager);
	}

	/**
	 * Simulate getting some data from the database
	 */
	private void getData() {
		// simulate getting some data
		for (int i = 0; i < 15; i++) {
			QuestionData qd = new QuestionData();
			qd.text = "Very important question no. " + i;
			qd.type = QUESTION_TYPES[i % 3];
			mData.put(i, qd);
		}
		mPageCount = mData.size();
	}

	/**
	 * This method will return the data from the
	 * 
	 * @param whichData
	 * @return a QuestionData object representing the data for the fragment with
	 *         the position specified by whichData
	 */
	public QuestionData getDataForFragment(int whichData) {
		return mData.get(whichData);
	}

	private static class CustomAdapter extends FragmentPagerAdapter {

		HashMap<Integer, QuestionData> mData;

		public CustomAdapter(FragmentManager fm,
				HashMap<Integer, QuestionData> data) {
			super(fm);
			mData = data;
		}

		@Override
		public Fragment getItem(int position) {
			// get the question's data and see which type it is and inflate the
			// proper fragment
			QuestionData dataForThisFragment = mData.get(position);
			int type = dataForThisFragment.type;
			// I'm also passing the position to use it to retrieve the
			// fragment's data
			Bundle args;
			switch (type) {
			case Q_TYPE_1:
				FragmentA fraga = new FragmentA();
				args = new Bundle();
				args.putInt("frag_pos", position);
				args.putInt("frag_type", type);
				fraga.setArguments(args);
				return fraga;
			case Q_TYPE_2:
				FragmentB fragb = new FragmentB();
				args = new Bundle();
				args.putInt("frag_pos", position);
				args.putInt("frag_type", type);
				fragb.setArguments(args);
				return fragb;
			case Q_TYPE_3:
				FragmentC fragc = new FragmentC();
				args = new Bundle();
				args.putInt("frag_pos", position);
				args.putInt("frag_type", type);
				fragc.setArguments(args);
				return fragc;
			}
			return null;
		}

		@Override
		public int getCount() {
			return mPageCount;
		}

	}

	// we have three types of fragments, for simplicity mine are all the same
	// as you pass the type to the fragment there should be no problem in simply
	// having one fragment and inflating the proper layout based on the passed
	// type
	public static class FragmentA extends Fragment {

		private TestLikeImplem mActivity;
		private int mFragPosition;
		private int mFragType;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mActivity = (TestLikeImplem) activity;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mFragPosition = getArguments().getInt("frag_pos");
			mFragType = getArguments().getInt("frag_type");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView tv = new TextView(getActivity());
			tv.setText("Type: " + returnType(mFragType)
					+ mActivity.getDataForFragment(mFragPosition).text);
			return tv;
		}

		private String returnType(int type) {
			if (type == Q_TYPE_1) {
				return "type-A";
			} else if (type == Q_TYPE_2) {
				return "type-B";
			} else {
				return "type-C";
			}
		}

	}

	public static class FragmentB extends Fragment {

		private TestLikeImplem mActivity;
		private int mFragPosition;
		private int mFragType;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mActivity = (TestLikeImplem) activity;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mFragPosition = getArguments().getInt("frag_pos");
			mFragType = getArguments().getInt("frag_type");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView tv = new TextView(getActivity());
			tv.setText("Type: " + returnType(mFragType)
					+ mActivity.getDataForFragment(mFragPosition).text);
			return tv;
		}

		private String returnType(int type) {
			if (type == Q_TYPE_1) {
				return "type-A";
			} else if (type == Q_TYPE_2) {
				return "type-B";
			} else {
				return "type-C";
			}
		}

	}

	public static class FragmentC extends Fragment {

		private TestLikeImplem mActivity;
		private int mFragPosition;
		private int mFragType;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mActivity = (TestLikeImplem) activity;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mFragPosition = getArguments().getInt("frag_pos");
			mFragType = getArguments().getInt("frag_type");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView tv = new TextView(getActivity());
			tv.setText("Type: " + returnType(mFragType)
					+ mActivity.getDataForFragment(mFragPosition).text);
			return tv;
		}

		private String returnType(int type) {
			if (type == Q_TYPE_1) {
				return "type-A";
			} else if (type == Q_TYPE_2) {
				return "type-B";
			} else {
				return "type-C";
			}
		}
	}

	/*
	 * Our dummy data class, it has the question's type to inflate the proper
	 * fragment and the actual question
	 */
	private static class QuestionData {
		String text;
		int type;
	}

}
