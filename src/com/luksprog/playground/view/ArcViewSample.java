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

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

/**
 * 
 * @see http://stackoverflow.com/questions/17668685/android-create-circle-arc
 *
 */
@SuppressLint("NewApi")
public class ArcViewSample extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout fl = new FrameLayout(this);
		ArcView arcView = new ArcView(this);
		//arcView.setFillValue(250);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(300, 300, Gravity.CENTER);
		fl.addView(arcView, lp);
		setContentView(fl);
		ObjectAnimator oa = ObjectAnimator.ofInt(arcView, "fillValue", 500);
		oa.setDuration(7000);
		oa.start();
	}

}
