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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

import com.luksprog.playground.R;
import com.luksprog.playground.view.DragAwareRelativeLayout.DragEventsListener;
import com.luksprog.playground.view.DragAwareRelativeLayout.DragShadowBuilderExtension;

@SuppressLint("NewApi")
public class DragDisplacement extends Activity {

	private View mDraggedView;
	private DragEventsListener mListener = new DragEventsListener() {

		@Override
		public void onDragViewEntered(int idOfEnteredView) {
			if (idOfEnteredView == R.id.target1) {
				Log.e("TAG", "The dragged view entered target 1!");
			} else if (idOfEnteredView == R.id.target2) {
				Log.e("TAG", "The dragged view entered target 2!");
			}
		}

		@Override
		public void onDragViewDropped(View onWhichView) {
			if (onWhichView == null) {
				Toast.makeText(DragDisplacement.this,
						"The dragged view wasn't dropped on a target view!",
						Toast.LENGTH_SHORT).show();
			} else if (onWhichView.getId() == R.id.target1) {
				Toast.makeText(DragDisplacement.this,
						"The dragged view was dropped on  target 1!",
						Toast.LENGTH_SHORT).show();
			} else if (onWhichView.getId() == R.id.target2) {
				Toast.makeText(DragDisplacement.this,
						"The dragged view was dropped on target 2!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_dragdisplacement);
		final DragAwareRelativeLayout darl = (DragAwareRelativeLayout) findViewById(R.id.dragLayer);
		darl.setDragEventsListener(mListener);
		mDraggedView = findViewById(R.id.dragMe);
		mDraggedView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				ClipData.Item item = new ClipData.Item((CharSequence) v
						.getTag());
				ClipData dragData = new ClipData((CharSequence) v.getTag(),
						new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
						item);
				View.DragShadowBuilder shadowBuilder = new DragShadowBuilderExtension(
						v);
				// register our two targets.
				darl.registerNextDragTargets(findViewById(R.id.target1),
						findViewById(R.id.target2));
				v.startDrag(dragData, shadowBuilder, null, 0);
				return true;
			}
		});
	}

}
