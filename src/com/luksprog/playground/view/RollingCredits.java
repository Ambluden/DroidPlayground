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
package com.luksprog.playground.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.luksprog.playground.R;

/**
 * @see http 
 *      ://stackoverflow.com/questions/17260073/how-to-make-closing-credits-page
 *      -animate-tablelayout-without-being-cut-at-disp
 * 
 * @author Luksprog
 * 
 */
public class RollingCredits extends Activity {

	private static final int SCROLLING_STEP = 5; // in pixels
	private TableLayout mTable;
	private ScrollView mScroll;
	private View mExtraPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_rollingcredits);
		mTable = (TableLayout) findViewById(R.id.creditstable);
		mScroll = (ScrollView) findViewById(R.id.scroll);
		mExtraPage = findViewById(R.id.extra);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mExtraPage.getLayoutParams().height = dm.heightPixels;
		mScroll.post(new Runnable() {

			@Override
			public void run() {
				final int count = mTable.getChildCount();
				int totalHeight = 0;
				for (int i = 0; i < count; i++) {
					final View child = mTable.getChildAt(i);
					totalHeight += child.getMeasuredHeight();
				}
				/**
				 * this would be the steps required to scroll by to scroll the
				 * needed height
				 */
				int steps = totalHeight / SCROLLING_STEP;
				if (totalHeight % SCROLLING_STEP != 0) {
					steps++; // add one if it doesn't add up
				}
				/**
				 * This is required because the CountDownTimer doesn't guarantee
				 * that will make all the ticks(it could abort a few because it
				 * doesn't have the time(and does that from small
				 * observations)). Because of this we're adding 25% more ticks
				 * then the original number to balance any lost ticks.
				 */
				steps += steps * 0.25;
				// to change the time/speed of the animation you would change
				// either/both SCROLLING_STEP and the 50 value for the tick
				// interval
				// beware what values you use
				new CountDownTimer(50 * steps, 50) {

					@Override
					public void onTick(long millisUntilFinished) {

						mScroll.scrollBy(0, SCROLLING_STEP);
					}

					@Override
					public void onFinish() {
						onFinishScrolling();
					}

					private void onFinishScrolling() {
						Toast.makeText(getApplicationContext(),
								"That's all the credits you'll need!",
								Toast.LENGTH_SHORT).show();
					}
				}.start();
			}

		});
	}
}
