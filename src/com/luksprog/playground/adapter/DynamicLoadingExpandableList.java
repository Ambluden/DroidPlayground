package com.luksprog.playground.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Code sample for
 * http://stackoverflow.com/questions/21493902/expandable-listview
 * -child-count-dynamically This example stores the retrieved data at the
 * Activity level
 * 
 * @author Luksprog
 * 
 */
public class DynamicLoadingExpandableList extends Activity implements
		DataObserver {

	private FrameLayout mContainer;
	// this stores the group info retrieved
	private List<String> mParentInfo = new ArrayList<String>();
	// this stores the child info retrieved
	private SparseArray<List<String>> mChildInfo = new SparseArray<List<String>>();
	// this holds the tasks used to retrieve data for a group's children
	private SparseArray<ChildRetrievalTask> mFetchTasks = new SparseArray<ChildRetrievalTask>();
	private ExpandableListView mExpandList;
	private CustomAdapter mAdapter; // our custom adapter

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// build the initial layout
		setContentView(buildInitialLayout());
		// if we don't have the data for the groups yet we start
		// GroupRetrievalTask in which we call the webservice for the groups to
		// get the data and store it in mParentInfo
		if (mParentInfo.isEmpty()) {
			new GroupRetrievalTask(this).execute();
		}
	}

	/**
	 * A simple layout with a loading text on it while we (fake) call the
	 * webservice to return data for the groups
	 * 
	 * @return the content view
	 */
	private View buildInitialLayout() {
		mContainer = new FrameLayout(this);
		TextView loading = new TextView(this);
		loading.setText("Loading initial data...");
		mContainer.addView(loading, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		return mContainer;
	}

	/**
	 * The group data is available so build the adapter and the
	 * ExpandableListView
	 * 
	 * @param data
	 *            dummy group data
	 */
	private void bindAdapter(SparseArray<String> data) {
		mContainer.removeAllViews();
		mExpandList = new ExpandableListView(this);
		mAdapter = new CustomAdapter(this, data);
		mExpandList.setAdapter(mAdapter);
		mContainer.addView(mExpandList);
		mExpandList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// if the user clicks a group we check the mChildInfo to see if
				// the data isn't already available and mFetchTasks to see if
				// we don't already have a retrieval task in progress(in which
				// case we do nothing as the loading view is already showing),
				// if the data isn't available start a new task
				if (mChildInfo.get(groupPosition) == null
						&& mFetchTasks.get(groupPosition) == null) {
					ChildRetrievalTask task = new ChildRetrievalTask(
							DynamicLoadingExpandableList.this, groupPosition);
					mFetchTasks.put(groupPosition, task);
					task.execute((Void) null); // fire up the task
				}
				return false;
			}
		});
	}

	/**
	 * One of the ChildRetrievalTask has finished its job and it has retrieved
	 * its data so remove it from the running task list and make the data
	 * avilable through mChildInfo
	 * 
	 * @param groupData
	 *            the group for which the data was retrieved
	 * @param data
	 *            the actual dummy data obtained from the webservice
	 */
	private void updateAdapter(int groupData, List<String> data) {
		mFetchTasks.put(groupData, null);
		mChildInfo.put(groupData, data);
		mAdapter.notifyDataSetChanged();
	}

	private static class CustomAdapter extends BaseExpandableListAdapter {

		// two types of rows because the loading view will probably not use the
		// same layout, meaning we could get in trouble if we use single row:
		// the list could recycle the loading view and give us that view when a
		// normal row should be presented
		private static final int TYPE_NORMAL = 0;
		private static final int TYPE_LOADING = 1;
		private SparseArray<String> mGroupData;
		private LayoutInflater mInflater;
		private DataObserver mObserver; // I've used this to abstract the
										// connection between the adapter and
										// the Activity

		public CustomAdapter(Context context, SparseArray<String> groupData) {
			mGroupData = groupData;
			mInflater = LayoutInflater.from(context);
			mObserver = (DataObserver) context;
		}

		@Override
		public String getChild(int groupPosition, int childPosition) {
			if (isDataAvailable(groupPosition)) {
				return mObserver.getChild(groupPosition, childPosition);
			}
			return "Loading data...";
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// ignoring
			return 0;
		}

		@Override
		public int getChildType(int groupPosition, int childPosition) {
			if (isDataAvailable(groupPosition)) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getChildTypeCount() {
			return 2;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final int type = getChildType(groupPosition, childPosition);
			if (convertView == null) {
				switch (type) {
				case TYPE_LOADING:
					convertView = new LinearLayout((Context) mObserver);
					TextView tv = new TextView((Context) mObserver);
					tv.setId(1000);
					((ViewGroup) convertView).addView(tv);
					break;
				case TYPE_NORMAL:
					convertView = mInflater.inflate(
							android.R.layout.simple_list_item_1, parent, false);
					break;
				}
			}

			switch (type) {
			case TYPE_LOADING:
				((TextView) convertView.findViewById(1000)).setText(getChild(
						groupPosition, childPosition));
				break;
			case TYPE_NORMAL:
				((TextView) convertView.findViewById(android.R.id.text1))
						.setText(getChild(groupPosition, childPosition));
				break;
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (isDataAvailable(groupPosition)) {
				return mObserver.getChildrenCount(groupPosition);
			}
			return 1;
		}

		@Override
		public String getGroup(int groupPosition) {
			return mGroupData.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mGroupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						android.R.layout.simple_list_item_1, parent, false);
			}
			((TextView) convertView.findViewById(android.R.id.text1))
					.setText(getGroup(groupPosition));
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private boolean isDataAvailable(int groupPosition) {
			return mObserver.getCurrentState(groupPosition) == Status.AVAILABLE;
		}

	}

	private static class ChildRetrievalTask extends
			AsyncTask<Void, Void, List<String>> {

		private static Random mRand = new Random();
		private int mGroupIdentif;
		private DynamicLoadingExpandableList mAct;

		public ChildRetrievalTask(Context context, int forWhichGroup) {
			mGroupIdentif = forWhichGroup;
			mAct = (DynamicLoadingExpandableList) context;
		}

		@Override
		protected List<String> doInBackground(Void... params) {
			// simulate the work needed for retireving the children
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final int count = mRand.nextInt(5) + 1;
			List<String> results = new ArrayList<String>();
			// dummy data
			for (int i = 0; i < count; i++) {
				results.add("Child info no. " + i);
			}
			return results;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			mAct.updateAdapter(mGroupIdentif, result);
		}

	}

	private static class GroupRetrievalTask extends
			AsyncTask<Void, Void, SparseArray<String>> {

		private DynamicLoadingExpandableList mAct;

		public GroupRetrievalTask(Context context) {
			mAct = (DynamicLoadingExpandableList) context;
		}

		@Override
		protected SparseArray<String> doInBackground(Void... params) {
			// simulate web service call
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SparseArray<String> results = new SparseArray<String>();
			// dummy data
			for (int i = 0; i < 2; i++) {
				results.put(i, "Group info no." + i);
			}
			return results;
		}

		@Override
		protected void onPostExecute(SparseArray<String> result) {
			// we have the group data so build the adapter with only the groups
			// and a loading view for the children
			mAct.bindAdapter(result);
		}

	}

	@Override
	public Status getCurrentState(int groupPosition) {
		// if we have data in mChildInfo it means the task has retrieved the
		// group's data so we return AVAILABLE to notify the adapter, otherwise
		// set PROCESSING to keep showing the loading view
		if (mChildInfo.get(groupPosition) != null) {
			return Status.AVAILABLE;
		}
		return Status.PROCESSING;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (mChildInfo.get(groupPosition) != null) {
			return mChildInfo.get(groupPosition).size();
		}
		return -1;
	}

	@Override
	public String getChild(int groupPosition, int childPosition) {
		if (mChildInfo.get(groupPosition) != null) {
			return mChildInfo.get(groupPosition).get(childPosition);
		}
		return null;
	}

}

interface DataObserver {

	enum Status {
		PROCESSING, AVAILABLE;
	}

	Status getCurrentState(int groupPosition);

	int getChildrenCount(int groupPosition);

	String getChild(int groupPosition, int childPosition);

}
