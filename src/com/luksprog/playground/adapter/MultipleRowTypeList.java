package com.luksprog.playground.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luksprog.playground.R;

/**
 * See http://stackoverflow.com/questions/20058491/how-do-i-call-2-consecutive-
 * items-in-a-list-in-a-table-form-next-to-each-other
 * 
 * @author Luksprog
 * 
 */
public class MultipleRowTypeList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView list = new ListView(this);
		List<Item> data = new ArrayList<Item>();
		setContentView(list);
		// creating dummy data for testing
		for (int i = 0; i < 40; i++) {
			Item it = new Item();
			it.itemDrawable = R.drawable.ic_launcher;
			it.itemTitle = "Item no." + i;
			it.itemContent = "Lorem Ipsum is simply dummy text of the printing "
					+ "and typesetting industry. Lorem Ipsum has been the industry's"
					+ " standard dummy text ever since the 1500s, when an unknown"
					+ " printer took a galley of type and scrambled it to make a"
					+ " type specimen book. It has survived not only five centuries,"
					+ " but also the leap into electronic typesetting, remaining essentially unchanged.";
			data.add(it);
		}
		list.setAdapter(new ForTabletAdapter(this, list, data));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View view,
					int position, long id) {
				// the listener could be used for both phone and tablet as the
				// position will be the position from the list of data
				Toast.makeText(getApplicationContext(),
						"Selected item " + position, Toast.LENGTH_SHORT).show();
			}
		});
		// for simplicity
		list.setSelector(android.R.drawable.list_selector_background);
	}

	/**
	 * This would be the adapter used for tablet target layouts.
	 * 
	 * @author Luksprog
	 * 
	 */
	private static class ForTabletAdapter extends BaseAdapter {

		private static final int NUM_TYPES = 3;

		private static final class Types {
			public static final int FIRST_HEADLINE = 0;
			public static final int OTHER_HEADLINE = 1;
			public static final int ALTERNATE_HEADLINE = 2;
		}

		private LayoutInflater mInflater;
		private List<Item> mData;
		private ListView mListHandle;

		ForTabletAdapter(Context context, ListView list, List<Item> data) {
			mData = data;
			mInflater = LayoutInflater.from(context);
			mListHandle = list;
		}

		@Override
		public int getCount() {
			// calculate the real number of rows taking in consideration that
			// there will be two items per row starting with the second row of
			// the ListView
			int size = mData.size();
			if (size == 0) {
				return size;
			} else {
				int count = 1;
				size = size - 1;
				count += size / 2; // two elements per row
				int remainder = size % 2;
				if (remainder != 0) { // check if we have a stranded element
					count++;
				}
				return count;
			}
		}

		@Override
		public Object getItem(int position) {
			// we don't use it, we'll access the data directly
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int type = getItemViewType(position);
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				switch (type) {
				case Types.FIRST_HEADLINE:
					convertView = mInflater.inflate(
							R.layout.adapter_mrtl_first, parent, false);
					holder.titles[0] = (TextView) convertView
							.findViewById(R.id.title);
					holder.contents[0] = (TextView) convertView
							.findViewById(R.id.content);
					holder.images[0] = (ImageView) convertView
							.findViewById(R.id.image);
					break;
				case Types.ALTERNATE_HEADLINE:
					convertView = mInflater.inflate(
							R.layout.adapter_mrtl_alternative, parent, false);
					holder.titles[0] = (TextView) convertView
							.findViewById(R.id.title);
					holder.contents[0] = (TextView) ((ViewGroup) convertView)
							.getChildAt(0).findViewById(R.id.content);
					holder.images[0] = (ImageView) ((ViewGroup) convertView)
							.getChildAt(0).findViewById(R.id.image);
					holder.titles[1] = (TextView) ((ViewGroup) convertView)
							.getChildAt(1).findViewById(R.id.title);
					holder.contents[1] = (TextView) ((ViewGroup) convertView)
							.getChildAt(1).findViewById(R.id.content);
					holder.images[1] = (ImageView) ((ViewGroup) convertView)
							.getChildAt(1).findViewById(R.id.image);
					break;
				case Types.OTHER_HEADLINE:
					convertView = mInflater.inflate(
							R.layout.adapter_mrtl_other, parent, false);
					holder.titles[0] = (TextView) convertView
							.findViewById(R.id.title);
					holder.contents[0] = (TextView) ((ViewGroup) convertView)
							.getChildAt(0).findViewById(R.id.content);
					holder.images[0] = (ImageView) ((ViewGroup) convertView)
							.getChildAt(0).findViewById(R.id.image);
					holder.titles[1] = (TextView) ((ViewGroup) convertView)
							.getChildAt(1).findViewById(R.id.title);
					holder.contents[1] = (TextView) ((ViewGroup) convertView)
							.getChildAt(1).findViewById(R.id.content);
					holder.images[1] = (ImageView) ((ViewGroup) convertView)
							.getChildAt(1).findViewById(R.id.image);
					break;
				default:
					throw new IllegalStateException("Unknow type of row!");
				}
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			Item it;
			switch (type) {
			case Types.FIRST_HEADLINE:
				it = mData.get(position);
				holder.titles[0].setText(it.itemTitle);
				holder.contents[0].setText(it.itemContent);
				holder.images[0].setImageResource(R.drawable.ic_launcher);
				break;
			case Types.ALTERNATE_HEADLINE:
				populateAleternativeRows((ViewGroup) convertView, holder,
						position);
				break;
			case Types.OTHER_HEADLINE:
				populateAleternativeRows((ViewGroup) convertView, holder,
						position);
				break;
			default:
				throw new IllegalStateException("Unknow type of row!");
			}
			return convertView;
		}

		/**
		 * Populate our two items per row normal row.
		 * 
		 * @param convertView
		 *            the ENTIRE row view(containing at maximum two items)
		 * @param holder
		 *            the holder with the references to the row views
		 * @param position
		 *            the position in the full list, this would need to be
		 *            offset to map to the proper position in the mData
		 */
		private void populateAleternativeRows(ViewGroup convertView,
				Holder holder, int position) {
			// offset the position to match the list position
			position = 2 * position - 1;
			// if we get here we have at least one element so set it up with
			// data.
			Item current = mData.get(position);
			holder.titles[0].setText(current.itemTitle);
			holder.contents[0].setText(current.itemContent);
			holder.images[0].setImageResource(current.itemDrawable);
			// set as tag the position so we can forward the proper event data
			convertView.getChildAt(0).setTag(Integer.valueOf(position));
			convertView.getChildAt(0).setOnClickListener(mClickDispatcher);
			// check if we have two items per row, this might not happen at the
			// end of the list if we
			// have an even number of data items
			int nextPosition = position + 1;
			// if we actually have another element
			if (nextPosition < mData.size()) {
				current = mData.get(nextPosition);
				holder.titles[1].setText(current.itemTitle);
				holder.contents[1].setText(current.itemContent);
				holder.images[1].setImageResource(current.itemDrawable);
				convertView.getChildAt(1).setVisibility(View.VISIBLE); // remember
																		// to
																		// show
																		// me
			} else {
				// we need to hide this if an item is not available so we don't
				// end up with ghost elements on the last row
				convertView.getChildAt(1).setVisibility(View.INVISIBLE);
				return;
			}
			convertView.getChildAt(1).setTag(Integer.valueOf(nextPosition));
			convertView.getChildAt(1).setOnClickListener(mClickDispatcher);
		}

		/**
		 * This listener will dispatch the events from one of the two possible
		 * items in a normal row to the actual OnItemCLickListener of the
		 * ListView to have a unified listener for the entire list.
		 */
		private OnClickListener mClickDispatcher = new OnClickListener() {

			@Override
			public void onClick(View view) {
				Integer position = (Integer) view.getTag();
				mListHandle.getOnItemClickListener().onItemClick(mListHandle,
						view, position, -1);
			}

		};

		// as the first row is a normal row we make it enabled, any other row
		// will be disabled and it will handle the click event on its own and
		// forward it to ListView's OnItemClickListener
		@Override
		public boolean isEnabled(int position) {
			return position == 0;
		}

		/**
		 * Holder class. As we have two items per row, we have an array of two
		 * elements in the holder for the views(the first row will only use the
		 * first element)
		 * 
		 * @author Luksprog
		 * 
		 */
		private static class Holder {
			TextView[] titles = new TextView[2];
			TextView[] contents = new TextView[2];;
			ImageView[] images = new ImageView[2];
		}

		@Override
		public int getItemViewType(final int position) {
			if (position == 0) {
				return Types.FIRST_HEADLINE;
			}
			if (position % 2 == 0 && position != 0) {
				return Types.ALTERNATE_HEADLINE;
			} else {
				return Types.OTHER_HEADLINE;
			}
		}

		@Override
		public int getViewTypeCount() {
			return NUM_TYPES;
		}

	}

	/**
	 * Dummy data class to replicate a more(possible heavy class).
	 */
	private static class Item {
		String itemContent;
		int itemDrawable = -1;
		String itemTitle = "N/A";
	}
}
