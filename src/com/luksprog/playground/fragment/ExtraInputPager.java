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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.luksprog.playground.R;

/**
 * A simple sample for having nested fragments in a fragment placed in a
 * ViewPager. The sample it's not complete, more work is required to keep the
 * state of the nested fragments as the user swipes away and to the nested
 * fragment page.
 * 
 * @author Luksprog
 * 
 */
public class ExtraInputPager extends FragmentActivity {

	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mViewPager = new ViewPager(this);
		mViewPager.setId(7777);
		mViewPager.setAdapter(new PAdapter(getSupportFragmentManager()));
		setContentView(mViewPager);
	}

	/**
	 * A simple adapter for our ViewPager. The first fragment(position 0) is the
	 * fragment that requires the new input view to be present.
	 * 
	 * @author Luksprog
	 * 
	 */
	private static class PAdapter extends FragmentPagerAdapter {

		public PAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new ExtraInputFragment();
			}
			return SimpleFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return 5;
		}

	}

	public static class ExtraInputFragment extends Fragment implements
			OnNewInputHandler {

		private static final String INPUT_CONTENT_TAG = "inputContent";
		private static final String INITIAL_CONTENT_TAG = "initialContent";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			FrameLayout wrapper = (FrameLayout) inflater.inflate(
					R.layout.frag_extrainputfrag, container, false);
			// see if the initial content isn't already in the layout, otherwise
			// add it.
			if (getChildFragmentManager()
					.findFragmentByTag(INITIAL_CONTENT_TAG) == null) {
				InitialContent ic = new InitialContent();
				getChildFragmentManager().beginTransaction()
						.add(R.id.realContent, ic, INITIAL_CONTENT_TAG)
						.commit();
			}

			return wrapper;
		}

		@Override
		public void onInputReceived(String data) {
			InputContent inc = (InputContent) getChildFragmentManager()
					.findFragmentByTag(INPUT_CONTENT_TAG);
			getChildFragmentManager().beginTransaction().remove(inc).commit();
			final InitialContent ic = (InitialContent) getChildFragmentManager()
					.findFragmentByTag(INITIAL_CONTENT_TAG);
			ic.update(data);
		}

		@Override
		public void onNewInputRequired() {
			InputContent ic = new InputContent();
			getChildFragmentManager().beginTransaction()
					.add(R.id.newContent, ic, INPUT_CONTENT_TAG).commit();
		}

	}

	/**
	 * This will be used as a nested fragment and will always exist in the
	 * ExtraInputFragment. To simulate needing a new view to input addition
	 * data, this fragment has a Button to request a view for entering
	 * additional data.
	 * 
	 * @author Luksprog
	 * 
	 */
	public static class InitialContent extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final OnNewInputHandler eif = (OnNewInputHandler) getParentFragment();
			Button b = new Button(getActivity());
			b.setText("Get more info! I want more info which should update my text!");
			b.setGravity(Gravity.CENTER);
			b.setId(300);
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// we require new data to be inputed so let the parent know
					// this.
					eif.onNewInputRequired();
				}
			});
			return b;
		}

		/**
		 * Simulate getting the new data and updating stuff.
		 * 
		 * @param data
		 */
		public void update(String data) {
			((Button) getView().findViewById(300)).setText(data);
		}
	}

	/**
	 * The new input nested fragment. By clicking the Button we simulate gtting
	 * the user's inputed data and returning it to the parent to do what it is
	 * required.
	 * 
	 * @author Luksprog
	 * 
	 */
	public static class InputContent extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final ExtraInputFragment eif = (ExtraInputFragment) getParentFragment();
			Button b = new Button(getActivity());
			b.setText("Simulate getting more info!");
			b.setBackgroundColor(Color.GREEN);
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					eif.onInputReceived("Very important data received!");
				}
			});
			return b;
		}

	}

	/**
	 * Interface to handle events(like showing and getting data) related to the
	 * new input view.
	 * 
	 * @author Luksprog
	 * 
	 */
	public interface OnNewInputHandler {

		/**
		 * Callback called when the user enters new data which should be
		 * returned.
		 * 
		 * @param data
		 *            the data(for simplicity I assumed the data is a simple
		 *            String)
		 */
		void onInputReceived(String data);

		/**
		 * Callback called when a view for inputing new content is requested.
		 */
		void onNewInputRequired();

	}

	/**
	 * A simple fragment.
	 * 
	 * @author Luksprog
	 * 
	 */
	public static class SimpleFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView tv = new TextView(getActivity());
			tv.setGravity(Gravity.CENTER);
			tv.setText("Simple Fragment no."
					+ getArguments().getInt("position")
					+ "\n I'm not doing anything!");
			return tv;
		}

		public static Fragment newInstance(int position) {
			SimpleFragment sf = new SimpleFragment();
			Bundle args = new Bundle();
			args.putInt("position", position);
			sf.setArguments(args);
			return sf;
		}

	}

}
