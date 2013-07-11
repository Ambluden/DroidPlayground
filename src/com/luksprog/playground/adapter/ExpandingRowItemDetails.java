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
package com.luksprog.playground.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luksprog.playground.R;

/**
 * @see http 
 *      ://stackoverflow.com/questions/17475721/add-view-between-2-rows-gridview
 * 
 */
public class ExpandingRowItemDetails extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// dummy data to test stuff
		List<Item> data = new ArrayList<Item>();
		for (int i = 0; i < 40; i++) {
			Item it = new Item();
			it.itemDrawable = R.drawable.ic_launcher;
			it.itemText = "Item no." + i;
			data.add(it);
		}
		setListAdapter(new CustomAdapter(this, data));
		getListView().setDividerHeight(0); // hide the dividers because a
											// GridView doesn't have any
											// dividers
	}

	/**
	 * If you want to replicate the GridView's OnItemClick callback.
	 * 
	 * @param v
	 *            the item view that was clicked(not the row of the ListView!!!)
	 * @param position
	 *            the position in the set of data(not the row!)
	 */
	public void listItemClick(View v, int position) {
		Toast.makeText(getApplicationContext(),
				"Item clicked! for position " + position, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * The adapter where the magic happens.
	 */
	private static class CustomAdapter extends BaseAdapter {

		/**
		 * The number of items per row.
		 */
		private static final int ITEMS_PER_ROW = 3;
		/**
		 * Variable to hold the current expanded item. My example assumes that
		 * only one row will have a description available to see at any
		 * time.(Also, if you click the same row the description goes away)
		 */
		private int mCurrentExpanded = -1;
		private Context mContext;
		private LayoutInflater mInflater;
		private List<Item> mData;
		/**
		 * A cache item to return the data for the three items that would be one
		 * a row.
		 */
		private SparseArray<Item> mRowItems = new SparseArray<Item>();
		/**
		 * The listener that will bring the description, trigger events, hide
		 * stuff etc.
		 */
		private OnClickListener mListener = new OnClickListener() {

			@Override
			public void onClick(final View item) {
				ItemIdentifier tag = (ItemIdentifier) item.getTag();
				// trigger the "item listener"
				((ExpandingRowItemDetails) mContext).listItemClick(item,
						tag.ownPosition);
				// if we aren't clicking on the same position then show the
				// description and hide the previous one(if any)
				if (mCurrentExpanded != tag.rowPosition) {
					LinearLayout details = (LinearLayout) ((RelativeLayout) (item
							.getParent().getParent())).getChildAt(2);
					ExpandCollapseAnimation inAnim = new ExpandCollapseAnimation(
							details, 1000, 0);
					details.startAnimation(inAnim);
					inAnim.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							ArrowView indicator = (ArrowView) ((RelativeLayout) (item
									.getParent().getParent())).getChildAt(1);
							indicator.setVisibility(View.VISIBLE);
							indicator.setTargetItem(item);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {

						}
					});
					if (mCurrentExpanded != -1) {
						final int firstVisible = ((ListActivity) mContext)
								.getListView().getFirstVisiblePosition();
						final View child = ((ListActivity) mContext)
								.getListView().getChildAt(
										mCurrentExpanded - firstVisible);
						child.findViewById(R.id.extra).setVisibility(View.GONE);
						child.findViewById(R.id.indicator).setVisibility(
								View.GONE);
					}
					mCurrentExpanded = tag.rowPosition;
				} else {
					// we've clicked on the same position so hide the
					// description. keep in mind that the current code doesn't
					// differentiate between items being from the same row being
					// clicked TODO !
					LinearLayout description = (LinearLayout) ((RelativeLayout) (item
							.getParent().getParent())).getChildAt(2);
					ExpandCollapseAnimation inAnim = new ExpandCollapseAnimation(
							description, 1000, 1);
					description.startAnimation(inAnim);
					inAnim.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							((RelativeLayout) (item.getParent().getParent()))
									.getChildAt(1).setVisibility(View.GONE);
						}
					});
					mCurrentExpanded = -1;
				}
			}

		};

		CustomAdapter(Context context, List<Item> data) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mData = data;
		}

		@Override
		public int getCount() {
			// we have 3 items per row!
			int rows = mData.size() / ITEMS_PER_ROW;
			int leftovers = mData.size() % ITEMS_PER_ROW;
			if (leftovers != 0) {
				rows++;
			}
			return rows;
		}

		@Override
		public SparseArray<Item> getItem(int position) {
			// we put in our SparseArray the three posible data for this
			// ListView row
			mRowItems.clear();
			for (int i = position * ITEMS_PER_ROW; i < position * ITEMS_PER_ROW
					+ 3; i++) {
				if (i < mData.size()) {
					mRowItems
							.append(i - position * ITEMS_PER_ROW, mData.get(i));
				} else {
					mRowItems.append(i - position * ITEMS_PER_ROW, null);
				}
			}
			return mRowItems;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.adapter_expandingrowitemdescription, parent,
						false);
				holder.descriptionExtra = (LinearLayout) convertView
						.findViewById(R.id.extra);
				holder.indicator = convertView.findViewById(R.id.indicator);
				holder.itemWrapper[0] = (FrameLayout) convertView
						.findViewById(R.id.rowItem1);
				ItemIdentifier ii = new ItemIdentifier();
				holder.itemWrapper[0].setTag(ii);
				holder.itemWrapper[1] = (FrameLayout) convertView
						.findViewById(R.id.rowItem2);
				ii = new ItemIdentifier();
				holder.itemWrapper[1].setTag(ii);
				holder.itemWrapper[2] = (FrameLayout) convertView
						.findViewById(R.id.rowItem3);
				ii = new ItemIdentifier();
				holder.itemWrapper[2].setTag(ii);
				holder.images[0] = (ImageView) convertView
						.findViewById(R.id.rowImage1);
				holder.images[1] = (ImageView) convertView
						.findViewById(R.id.rowImage2);
				holder.images[2] = (ImageView) convertView
						.findViewById(R.id.rowImage3);
				holder.texts[0] = (TextView) convertView
						.findViewById(R.id.rowText1);
				holder.texts[1] = (TextView) convertView
						.findViewById(R.id.rowText2);
				holder.texts[2] = (TextView) convertView
						.findViewById(R.id.rowText3);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final SparseArray<Item> data = getItem(position);
			for (int i = 0; i < ITEMS_PER_ROW; i++) {
				final Item item = data.get(i);
				if (item != null) {
					holder.images[i].setImageResource(item.itemDrawable);
					holder.images[i].setVisibility(View.VISIBLE);
					holder.texts[i].setText(item.itemText);
					holder.texts[i].setVisibility(View.VISIBLE);
					holder.itemWrapper[i].setOnClickListener(mListener);
				} else {
					holder.images[i].setVisibility(View.INVISIBLE);
					holder.texts[i].setVisibility(View.INVISIBLE);
					holder.itemWrapper[i].setEnabled(false);
				}
				final ItemIdentifier itemTag = (ItemIdentifier) holder.itemWrapper[i]
						.getTag();
				itemTag.rowPosition = position;
				itemTag.ownPosition = position * ITEMS_PER_ROW + i;
			}
			if (position == mCurrentExpanded) {
				holder.descriptionExtra.setVisibility(View.VISIBLE);
				holder.indicator.setVisibility(View.VISIBLE);
				// do extra work to set the description
			} else {
				holder.descriptionExtra.setVisibility(View.GONE);
				holder.indicator.setVisibility(View.GONE);
			}
			return convertView;
		}

		@Override
		public boolean isEnabled(int position) {
			// we don't want our ListView rows to be clickable!
			return false;
		}

		/**
		 * The holder class holding the views that will be needed.
		 * 
		 */
		static class ViewHolder {
			ImageView[] images = new ImageView[3];
			TextView[] texts = new TextView[3];
			FrameLayout[] itemWrapper = new FrameLayout[3];
			LinearLayout descriptionExtra;
			View indicator;
		}

		/**
		 * I used this class to pass directly the data needed so I don't have to
		 * calculate it each time, but can be done that way too.
		 * 
		 */
		static class ItemIdentifier {
			int ownPosition;
			int rowPosition;
		}

	}

	/**
	 * Dummy data class to replicate a more(possible heavy class)
	 */
	private static class Item {
		int itemDrawable = -1;
		String itemText = "N/A";
	}

}
