package com.luksprog.playground.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
		setContentView(list);
		// creating dummy data for testing
		MatrixCursor mc = new MatrixCursor(new String[] { "_id", "title",
				"content", "image" });
		for (int i = 0; i < 40; i++) {
			mc.addRow(new Object[] {
					i,
					"Item no." + i,
					"Lorem Ipsum is simply dummy text of the printing "
							+ "and typesetting industry. Lorem Ipsum has been the industry's"
							+ " standard dummy text ever since the 1500s, when an unknown"
							+ " printer took a galley of type and scrambled it to make a"
							+ " type specimen book. It has survived not only five centuries,"
							+ " but also the leap into electronic typesetting, remaining essentially unchanged.",
					R.drawable.ic_launcher });
		}
		list.setAdapter(new ForTabletAdapter(this, mc, list));
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
	private static class ForTabletAdapter extends CursorAdapter {

		private static final int NUM_TYPES = 3;

		private static final class Types {
			public static final int FIRST_HEADLINE = 0;
			public static final int OTHER_HEADLINE = 1;
			public static final int ALTERNATE_HEADLINE = 2;
		}

		private LayoutInflater mInflater;
		private ListView mListHandle;

		ForTabletAdapter(Context context, Cursor cursor, ListView list) {
			super(context, cursor, 0);
			mInflater = LayoutInflater.from(context);
			mListHandle = list;
		}

		@Override
		public int getCount() {
			// calculate the real number of rows taking in consideration that
			// there will be two items per row starting with the second row of
			// the ListView
			int size = getCursor().getCount();
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
			// we can't use it because we can't return two values for the same
			// position
			return null;
		}

		@Override
		public long getItemId(int position) {
			// we can't use it because we can't return two values for the same
			// position
			return -1;
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
				Holder holder, Cursor cursor) {
			// offset the position to match the real position
			final int position = 2 * cursor.getPosition() - 1;
			// move the cursor to the right position
			cursor.moveToPosition(position);
			holder.titles[0].setText(cursor.getString(cursor
					.getColumnIndex("title")));
			holder.contents[0].setText(cursor.getString(cursor
					.getColumnIndex("content")));
			holder.images[0].setImageResource(cursor.getInt(cursor
					.getColumnIndex("image")));
			// set as tag the position so we can forward the proper event data
			convertView.getChildAt(0).setTag(Integer.valueOf(position));
			convertView.getChildAt(0).setOnClickListener(mClickDispatcher);
			// check if we have two items per row, this might not happen at the
			// end of the list if we have an even number of items in the Cursor
			int nextPosition = position + 1;
			// if we are within the proper bounds
			if (cursor.getPosition() < cursor.getCount() - 1) {
				cursor.moveToNext();
				holder.titles[1].setText(cursor.getString(cursor
						.getColumnIndex("title")));
				holder.contents[1].setText(cursor.getString(cursor
						.getColumnIndex("content")));
				holder.images[1].setImageResource(cursor.getInt(cursor
						.getColumnIndex("image")));
				convertView.getChildAt(1).setVisibility(View.VISIBLE); 
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

		@Override
		public void bindView(View rowView, Context context, Cursor cursor) {
			final int type = getItemViewType(cursor.getPosition());
			Holder holder = (Holder) rowView.getTag();
			switch (type) {
			case Types.FIRST_HEADLINE:
				holder.titles[0].setText(cursor.getString(cursor
						.getColumnIndex("title")));
				holder.contents[0].setText(cursor.getString(cursor
						.getColumnIndex("content")));
				holder.images[0].setImageResource(cursor.getInt(cursor
						.getColumnIndex("image")));
				break;
			case Types.ALTERNATE_HEADLINE:
				populateAleternativeRows((ViewGroup) rowView, holder, cursor);
				break;
			case Types.OTHER_HEADLINE:
				populateAleternativeRows((ViewGroup) rowView, holder, cursor);
				break;
			default:
				throw new IllegalStateException("Unknow type of row!");
			}

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			Holder holder = new Holder();
			View rowView = null;
			final int type = getItemViewType(cursor.getPosition());
			switch (type) {
			case Types.FIRST_HEADLINE:
				rowView = mInflater.inflate(R.layout.adapter_mrtl_first,
						parent, false);
				holder.titles[0] = (TextView) rowView.findViewById(R.id.title);
				holder.contents[0] = (TextView) rowView
						.findViewById(R.id.content);
				holder.images[0] = (ImageView) rowView.findViewById(R.id.image);
				break;
			case Types.ALTERNATE_HEADLINE:
				rowView = mInflater.inflate(R.layout.adapter_mrtl_alternative,
						parent, false);
				holder.titles[0] = (TextView) rowView.findViewById(R.id.title);
				holder.contents[0] = (TextView) ((ViewGroup) rowView)
						.getChildAt(0).findViewById(R.id.content);
				holder.images[0] = (ImageView) ((ViewGroup) rowView)
						.getChildAt(0).findViewById(R.id.image);
				holder.titles[1] = (TextView) ((ViewGroup) rowView).getChildAt(
						1).findViewById(R.id.title);
				holder.contents[1] = (TextView) ((ViewGroup) rowView)
						.getChildAt(1).findViewById(R.id.content);
				holder.images[1] = (ImageView) ((ViewGroup) rowView)
						.getChildAt(1).findViewById(R.id.image);
				break;
			case Types.OTHER_HEADLINE:
				rowView = mInflater.inflate(R.layout.adapter_mrtl_other,
						parent, false);
				holder.titles[0] = (TextView) rowView.findViewById(R.id.title);
				holder.contents[0] = (TextView) ((ViewGroup) rowView)
						.getChildAt(0).findViewById(R.id.content);
				holder.images[0] = (ImageView) ((ViewGroup) rowView)
						.getChildAt(0).findViewById(R.id.image);
				holder.titles[1] = (TextView) ((ViewGroup) rowView).getChildAt(
						1).findViewById(R.id.title);
				holder.contents[1] = (TextView) ((ViewGroup) rowView)
						.getChildAt(1).findViewById(R.id.content);
				holder.images[1] = (ImageView) ((ViewGroup) rowView)
						.getChildAt(1).findViewById(R.id.image);
				break;
			default:
				throw new IllegalStateException("Unknow type of row!");
			}
			rowView.setTag(holder);
			return rowView;
		}

	}
}
