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

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.luksprog.playground.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SimpleCABFragment extends Fragment implements Callback {

	private static final boolean POST_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_simplecabfragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		RadioGroup rg = (RadioGroup) getView().findViewById(R.id.radioGroup1);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (POST_HONEYCOMB) {
					// this could be improved so we don't need to create the
					// option
					// menu if it is already available
					getActivity().startActionMode(SimpleCABFragment.this);
				} else {
					// something else
				}
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if (item.getItemId() == R.id.itemradio) {
			Toast.makeText(getActivity(), "CAB for Radiogroup!",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		getActivity().getMenuInflater().inflate(R.menu.simplecab_menu, menu);
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}
}