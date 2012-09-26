package com.luksprog.dp;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoryActivities extends ListActivity {

	private static final String[] CHALLENGE_INTENTS = {
			"com.luksprog.intent.ANIMATION_INTENT",
			"com.luksprog.intent.SERVICE_INTENT",
			"com.luksprog.intent.RESOURCE_INTENT",
			"com.luksprog.intent.FRAG_INTENT",
			"com.luksprog.intent.MISC_INTENT",
			"com.luksprog.intent.ADAPTER_INTENT",
			"com.luksprog.intent.THREAD_INTENT",
			"com.luksprog.intent.APP_INTENT",
			"com.luksprog.intent.VIEW_INTENT",
			"com.luksprog.intent.PROVIDER_INTENT" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int sentPosition = getIntent().getIntExtra("the_position", -1);
		if (sentPosition == -1) {
			return; // show nothing, what happened
		}
		PackageManager pm = getPackageManager();
		Intent challengeIntent = new Intent();
		challengeIntent.setAction(CHALLENGE_INTENTS[sentPosition]);
		List<ResolveInfo> resolvedActivities = pm.queryIntentActivities(
				challengeIntent, 0);
		ArrayAdapter<ActivityData> adapter = new ArrayAdapter<ActivityData>(
				this, android.R.layout.simple_list_item_1);
		for (ResolveInfo ra : resolvedActivities) {
			final ActivityInfo ai = ra.activityInfo;
			String activityName = ai.name;
			ActivityData dObj = new ActivityData();
			Intent i = new Intent();
			i.setClassName(ai.packageName, ai.name);
			dObj.activityName = activityName;
			dObj.activityIntent = i;
			adapter.add(dObj);
		}
		setListAdapter(adapter);		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ActivityData ad = (ActivityData) l.getItemAtPosition(position);
		startActivity(ad.activityIntent);
	}

	private static class ActivityData {
		String activityName;
		Intent activityIntent;

		@Override
		public String toString() {
			int lastIndex = activityName.lastIndexOf(".");
			return activityName.substring(lastIndex + 1, activityName.length());
		}
	}

}