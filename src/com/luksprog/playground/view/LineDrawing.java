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
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * See http://stackoverflow.com/questions/19604968/drawing-a-line-following-
 * finger-motionevent-getx-and-gety-incorrect-update
 * @author Luksprog
 *
 */
/**
 * See
 * http://stackoverflow.com/questions/19604968/drawing-a-line-following-finger
 * -motionevent-getx-and-gety-incorrect-update
 * 
 * @author Luksprog
 * 
 */
public class LineDrawing extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RelativeLayout wrapper = new RelativeLayout(this);
		TableLayout tl = new TableLayout(this);
		tl.setId(1111);
		wrapper.addView(tl, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT));
		// a table of 3 rows x 4 columns
		for (int i = 0; i < 3; i++) {
			TableRow tr = new TableRow(this);
			for (int j = 0; j < 4; j++) {
				TableRow.LayoutParams trp = new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.WRAP_CONTENT);
				DotView dv = new DotView(this);
				tr.addView(dv, trp);
			}
			tl.addView(tr, new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.WRAP_CONTENT));
		}
		// the LineView will match exactly the table so every coordinate in the
		// TableLayout's space is the same for the LineView
		final LineView line = new LineView(this);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.ALIGN_BOTTOM, 1111);
		rlp.addRule(RelativeLayout.ALIGN_LEFT, 1111);
		rlp.addRule(RelativeLayout.ALIGN_TOP, 1111);
		rlp.addRule(RelativeLayout.ALIGN_RIGHT, 1111);
		wrapper.addView(line, rlp);
		setContentView(wrapper);
		// the touch listener where the magic happens
		tl.setOnTouchListener(new OnTouchListener() {

			private int mStartX = -1;
			private int mStartY = -1;
			private DotView mStartView;
			private int mXOffset = -1;
			private int mYOffset = -1;
			private final int[] mCoords = new int[2];

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// IMPORTANT this is an example to show that the
				// getLocationOnScreen(mCoords) method works! we could very
				// simple use the x,y touch coordinates in the TableLayout
				// itself. The reason for requiring an offset is that, although
				// the getLocationOnScreen(mCoords) returns the same coordinates
				// for the TableLayout and LineView, drawing using this
				// coordinates in the LineView will fail because the drawing
				// will be offset with the value of the y value(when drawing in
				// a view its top-left point translates to 0 and not the (x,y)
				// pair returned by getLocationOnScreen(mCoords))
				if (mXOffset == -1 || mYOffset == -1) {
					v.getLocationOnScreen(mCoords);
					mXOffset = mCoords[0];
					mYOffset = mCoords[1];
				}
				final float x = event.getX();
				final float y = event.getY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// get the child that corresponds to the touch
					mStartView = getChildForTouch((TableLayout) v, x, y);
					// get its coordinates in the screen
					mStartView.getLocationOnScreen(mCoords);
					// snap the values to the center of the DotView, also
					// translate the coordinates in the system coordinates of
					// the LineView
					mStartX = mCoords[0] + mStartView.getCenterX() - mXOffset;
					mStartY = mCoords[1] + mStartView.getCenterY() - mYOffset;
					mStartView.enlargeCircle();// make the circle bigger
					return true;
				case MotionEvent.ACTION_MOVE:
					// just update positions
					line.setCoords(mStartX, mStartY, (int) x, (int) y);
					break;
				case MotionEvent.ACTION_UP:
					// we have a release so do the same as the start view for
					// the final DotView on which the user released its finger
					DotView endView = getChildForTouch((TableLayout) v, x, y);
					endView.getLocationOnScreen(mCoords);
					int endX = mCoords[0] + endView.getCenterX() - mXOffset;
					int endY = mCoords[1] + endView.getCenterY() - mYOffset;
					line.setCoords(mStartX, mStartY, endX, endY);
					mStartView.squeezeCircle();
					break;
				}
				return false;
			}

			/**
			 * Uses the coordinates of the touch to find out which DotView was
			 * actually touched and returns it
			 */
			private DotView getChildForTouch(TableLayout table, float x, float y) {
				final int childWidth = ((TableRow) table.getChildAt(0))
						.getChildAt(0).getWidth();
				final int childHeight = ((TableRow) table.getChildAt(0))
						.getChildAt(0).getHeight();
				// find out the row of the child
				int row = 0;
				do {
					if (y > childHeight) {
						row++;
						y -= childHeight;
					} else {
						break;
					}
				} while (y > childHeight);
				int column = 0;
				do {
					if (x > childWidth) {
						column++;
						x -= childWidth;
					} else {
						break;
					}
				} while (x > childWidth);
				return (DotView) ((TableRow) table.getChildAt(row))
						.getChildAt(column);
			}
		});
	}

	private static class DotView extends View {

		private static final int DEFAULT_SIZE = 100;
		private Paint mPaint = new Paint();
		private Rect mBorderRect = new Rect();
		private Paint mCirclePaint = new Paint();
		private int mRadius = DEFAULT_SIZE / 4;

		public DotView(Context context) {
			super(context);
			mPaint.setStrokeWidth(2.0f);
			mPaint.setStyle(Style.STROKE);
			mPaint.setColor(Color.RED);
			mCirclePaint.setColor(Color.CYAN);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.parseColor("#0099cc"));
			mBorderRect.left = 0;
			mBorderRect.top = 0;
			mBorderRect.right = getMeasuredWidth();
			mBorderRect.bottom = getMeasuredHeight();
			canvas.drawRect(mBorderRect, mPaint);
			canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
					mRadius, mCirclePaint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(DEFAULT_SIZE, DEFAULT_SIZE);
		}

		public int getCenterX() {
			return getMeasuredWidth() / 2;
		}

		public int getCenterY() {
			return getMeasuredHeight() / 2;
		}

		public void enlargeCircle() {
			mRadius = DEFAULT_SIZE / 3;
			invalidate();
		}

		public void squeezeCircle() {
			mRadius = DEFAULT_SIZE / 4;
			invalidate();
		}

	}

	private static class LineView extends View {
		private int mXStart = -1, mYStart = -1, mXEnd = -1, mYEnd = -1;
		private Paint mPaint = new Paint();

		public LineView(Context context) {
			super(context);
			mPaint.setColor(Color.parseColor("#bb9933"));
			mPaint.setStrokeWidth(3.0f);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// if the coordinates are valid draw the line.
			if (mXStart != -1 && mYStart != -1 && mXEnd != -1 && mYEnd != -1) {
				canvas.drawLine(mXStart, mYStart, mXEnd, mYEnd, mPaint);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// just use the dimensions passed in as the view is set to fill the
			// parent overlaying the TableLayout. A much better choice would be
			// to let the TableLayout also do the line drawing.
			setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
					MeasureSpec.getSize(heightMeasureSpec));
		}

		/**
		 * Sets the coordinates of the line drawn.
		 */
		public void setCoords(int xStart, int yStart, int xEnd, int yEnd) {
			mXStart = xStart;
			mYStart = yStart;
			mXEnd = xEnd;
			mYEnd = yEnd;
			invalidate();
		}

	}

}
