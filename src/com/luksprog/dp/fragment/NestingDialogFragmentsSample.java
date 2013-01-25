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
package com.luksprog.dp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Sample for starting a DialogFragment from a normal Fragment, followed by
 * starting a new custom DialogFragment.
 * 
 * @author luksprog
 * 
 */
public class NestingDialogFragmentsSample extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, new Fragment1()).commit();
	}

	public static class Fragment1 extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Button b = new Button(getActivity());
			b.setText("Click me!");
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Fragment2 fr2 = new Fragment2();
					fr2.setfrag(Fragment1.this);
					fr2.show(getFragmentManager(), "firstDialog");
				}
			});
			return b;
		}

		/**
		 * This will show our last DialogFragment.
		 */
		public void show3() {
			Fragment3 fr3 = new Fragment3();
			fr3.show(getFragmentManager(), "lastDialog");
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// if the first DialogFragment is shown then register this new
			// instance in that dialog with the setFrag() method.
			Fragment2 fr2 = (Fragment2) getFragmentManager().findFragmentByTag(
					"firstDialog");
			if (fr2 != null) {
				fr2.setfrag(this);
			}
		}

	}

	/**
	 * Our first DialogFragment.
	 * 
	 * @author luksprog
	 * 
	 */
	public static class Fragment2 extends DialogFragment {

		private Fragment1 mStarter;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
			b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mStarter.show3();
				}
			});
			return b.create();
		}

		@Override
		public void onDestroyView() {
			if (getDialog() != null && getRetainInstance())
				getDialog().setDismissMessage(null);
			super.onDestroyView();
		}

		/**
		 * Register the first fragment as a listener again, otherwise we'll have
		 * NullPointerException.
		 * 
		 * @param fragment1
		 *            The fragment from which we'll add the last fragment.
		 */
		public void setfrag(Fragment1 fragment1) {
			mStarter = fragment1;
		}

	}

	/**
	 * The last DialogFragment.
	 * 
	 * @author luksprog
	 * 
	 */
	public static class Fragment3 extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			ProgressDialog pd = new ProgressDialog(getActivity());
			pd.setMessage("It works!");
			return pd;
		}

	}
}
