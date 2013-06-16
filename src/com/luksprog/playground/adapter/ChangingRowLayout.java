package com.luksprog.playground.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.luksprog.playground.R;

/**
 * @see http
 *      ://stackoverflow.com/questions/17130119/how-to-put-custom-adapter-code
 *      -in-a-method
 * 
 * @author Luskprog
 * 
 */
public class ChangingRowLayout extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<String> data = new ArrayList<String>();
		for (int i = 0; i < 40; i++) {
			data.add("Item no." + i);
		}
		setListAdapter(new CustomAdapter(this, data));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		((CustomAdapter) l.getAdapter()).changeRowLayout(position);
	}

	private static class CustomAdapter extends BaseAdapter {

		public static final int INITIAL_TYPE = 0;
		public static final int CHANGE_TYPE = 1;
		private List<String> mData;
		private LayoutInflater mInflater;
		private ArrayList<Integer> mChangedRows = new ArrayList<Integer>();

		public CustomAdapter(Context context, List<String> data) {
			mData = data;
			mInflater = LayoutInflater.from(context);
		}

		public void changeRowLayout(int rowPosition) {
			if (mChangedRows.contains(rowPosition)) {
				mChangedRows.remove(mChangedRows.indexOf(rowPosition));
			} else {
				mChangedRows.add(rowPosition);
			}
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			if (mChangedRows.size() == 0 || !mChangedRows.contains(position)) {
				return INITIAL_TYPE;
			} else {
				return CHANGE_TYPE;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
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
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			int type = getItemViewType(position);
			if (convertView == null) {
				holder = new ViewHolder();
				switch (type) {
				case INITIAL_TYPE:
					convertView = mInflater.inflate(
							R.layout.adapter_changerow1, parent, false);
					holder.mText = (TextView) convertView
							.findViewById(R.id.textView1);
					break;
				case CHANGE_TYPE:
					convertView = mInflater.inflate(
							R.layout.adapter_changerow2, parent, false);
					holder.mText = (TextView) convertView
							.findViewById(R.id.textView1);
					holder.mPressMe = (Button) convertView
							.findViewById(R.id.button1);
					holder.mCheckMe = (CheckBox) convertView
							.findViewById(R.id.checkBox1);
					break;
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			switch (type) {
			case INITIAL_TYPE:
				holder.mText.setText(getItem(position));
				break;
			case CHANGE_TYPE:
				holder.mText.setText(getItem(position));
				break;
			}
			return convertView;
		}
	}

	static class ViewHolder {
		TextView mText;
		Button mPressMe;
		CheckBox mCheckMe;
	}

}
