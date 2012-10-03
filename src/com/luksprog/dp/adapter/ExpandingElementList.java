package com.luksprog.dp.adapter;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luksprog.dp.R;

public class ExpandingElementList extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<String> items = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			items.add("Row no." + i);
		}
		setListAdapter(new CustomAdapter(this, items));
	}

	@Override
	protected void onResume() {
		super.onResume();
		// set the position to the desired element
		((CustomAdapter) getListView().getAdapter()).setExpandedPosition(15);
		// set the selection to that element so we can actually see it
		// this isn't required but has the advantage that it will move the
		// ListView to the desired
		// position if not visible
		getListView().setSelection(15);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// set the expanded(or collapsed if it's a click on the same row that
		// was previously expanded) row in the adapter
		((CustomAdapter) getListView().getAdapter())
				.setExpandedPosition(position);
	}

	private static class CustomAdapter extends BaseAdapter {

		// the data
		private ArrayList<String> mData;

		// an int pointing to a position that has an expanded layout,
		// for simplicity I assume that you expand only one item(otherwise use
		// an array or list)
		private int mExpandedPosition = -1; // -1 meaning no expanded item
		private LayoutInflater mInflater;

		public CustomAdapter(Context context, ArrayList<String> items) {
			mInflater = LayoutInflater.from(context);
			mData = items;
		}

		public void setExpandedPosition(int position) {
			// if the position equals mExpandedPosition then we have a click on
			// the same row so simply toggle the row
			if (position == mExpandedPosition) {
				mExpandedPosition = -1;
			} else {
				// else change position of the row that was expanded
				mExpandedPosition = position;
			}
			// notify the adapter
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public String getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.ad_expandedelement,
						parent, false);
			}
			((TextView) convertView.findViewById(R.id.textView1))
					.setText(getItem(position));
			// see if there is an expanded position and if we are at that
			// position
			if (mExpandedPosition != -1 && mExpandedPosition == position) {
				// if yes simply expand the layout
				convertView.findViewById(R.id.button1).setVisibility(
						View.VISIBLE);
			} else {
				// this is required, we must revert any possible changes
				// otherwise the recycling mechanism will hurt us
				convertView.findViewById(R.id.button1).setVisibility(View.GONE);
			}
			return convertView;
		}

	}

}
