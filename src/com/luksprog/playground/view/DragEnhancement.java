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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.luksprog.playground.R;

@SuppressLint("NewApi")
public class DragEnhancement extends Activity {

	private View mTargetView, mDraggedView, mDragCanvas;
	private Rect mTargetArea = new Rect();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_dragenhancement);
		mTargetView = findViewById(R.id.target);
		mDraggedView = findViewById(R.id.dragMe);
		mDragCanvas = new FrameLayout(DragEnhancement.this);
		mDraggedView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(final View v) {
				ClipData.Item item = new ClipData.Item((CharSequence) v
						.getTag());
				ClipData dragData = new ClipData((CharSequence) v.getTag(),
						new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
						item);
				View.DragShadowBuilder shadowBuilder = new ShadowBuilderModified(
						v);
				attachDragCanvas();
				mDragCanvas.setOnDragListener(new OnDragListener() {

					@Override
					public boolean onDrag(View v, DragEvent event) {
						final int action = event.getAction();
						switch (action) {
						case DragEvent.ACTION_DRAG_STARTED:
							obtainTargetArea(mTargetView, mTargetArea);
							break;
						case DragEvent.ACTION_DRAG_LOCATION:
							break;
						case DragEvent.ACTION_DRAG_ENDED:
							detachDragCanvas();
							break;
						case DragEvent.ACTION_DROP:
							Point p = translateDragPoint(event.getX(),
									event.getY());
							if (mTargetArea.contains(p.x, p.y)) {
								Toast.makeText(
										DragEnhancement.this,
										"The dropped was on the target view!!!",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										DragEnhancement.this,
										"The dropped was outside of the target view!!!",
										Toast.LENGTH_SHORT).show();
							}
							detachDragCanvas();
							break;
						case DragEvent.ACTION_DRAG_ENTERED:
							break;
						}
						return true;
					}
				});
				v.startDrag(dragData, shadowBuilder, null, 0);
				return false;
			}
		});
	}

	private void obtainTargetArea(View forWhom, Rect out) {
		int[] coords = new int[2];
		mTargetView.getLocationInWindow(coords);
		out.left = coords[0];
		out.top = coords[1];
		out.right = coords[0] + forWhom.getWidth();
		out.bottom = coords[1] + forWhom.getHeight();
	}

	private Point translateDragPoint(float x, float y) {
		Point p = new Point();
		int[] coords = new int[2];
		mDragCanvas.getLocationInWindow(coords);

		p.x = (int) (x + coords[0]);
		y += coords[1];
		p.y = (int) (y - ShadowBuilderModified.EXTENSION - (mDraggedView
				.getHeight() / 2));
		return p;
	}

	private void attachDragCanvas() {
		ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		content.addView(mDragCanvas);
	}

	private void detachDragCanvas() {
		ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		content.removeView(mDragCanvas);
	}

	static class ShadowBuilderModified extends View.DragShadowBuilder {

		static final int EXTENSION = 80;
		private View mDragged;
		private Path mArrowPath = new Path();
		private Paint mPaint = new Paint();

		public ShadowBuilderModified(View draggedView) {
			mDragged = draggedView;
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Style.FILL_AND_STROKE);
			mArrowPath.moveTo((3 * mDragged.getWidth() / 4), 0);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) + 10, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) + 3, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) + 3, EXTENSION);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) - 3, EXTENSION);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) - 3, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) - 10, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4), 0);
			mArrowPath.close();
		}

		@Override
		public void onProvideShadowMetrics(Point shadowSize,
				Point shadowTouchPoint) {
			shadowSize.x = mDragged.getWidth();
			shadowSize.y = mDragged.getHeight() + EXTENSION;
			shadowTouchPoint.x = (3 * mDragged.getWidth() / 4);
			shadowTouchPoint.y = EXTENSION + mDragged.getHeight() / 2;
		}

		@Override
		public void onDrawShadow(Canvas canvas) {
			canvas.save();
			canvas.translate(0, EXTENSION);
			mDragged.draw(canvas);
			canvas.restore();
			canvas.drawPath(mArrowPath, mPaint);
		}
	}
}

