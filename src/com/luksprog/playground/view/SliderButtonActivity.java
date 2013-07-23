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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.luksprog.playground.R;
import com.luksprog.playground.view.SliderButtonActivity.SliderButton.OnValueUpdateListener;



/**
 * @see http
 *      ://stackoverflow.com/questions/17747945/how-to-increase-or-decrease-
 *      counter -using-horizontally-scrolling-button
 * 
 * 
 */
public class SliderButtonActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout fl = new FrameLayout(this);
		final TextView tv = new TextView(this);
		fl.addView(tv, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		final SliderButton sb = new SliderButton(this);
		tv.setText(String.valueOf(sb.getCurrentValue()));
		sb.setOnvalueUpdateListener(new OnValueUpdateListener() {

			@Override
			public void onValueIncreased() {
				tv.setText(String.valueOf(sb.getCurrentValue()));
			}

			@Override
			public void onValueDecreased() {
				tv.setText(String.valueOf(sb.getCurrentValue()));
			}

		});
		fl.addView(sb, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT));
		setContentView(fl);
	}

	/**
	 * Our custom slider button.
	 * 
	 */
	public static class SliderButton extends View {

		/**
		 * The value after which a user scroll is considered a increase/decrease
		 * in value.
		 */
		private static final float STEP_SIZE = 50;
		/**
		 * Our pattern bitmap.
		 */
		private Bitmap mBitmap;
		/**
		 * The default height value of the pattern bitmap.
		 */
		private int mDefaultHeight = 0;
		/**
		 * The default width value of the pattern bitmap.
		 */
		private int mDefaultWidth = 0;
		/**
		 * The position of the first bitmap tile. I've used -10 to not start
		 * quite from the beginning of the tile Bitmap.
		 */
		private int mLeftTilePos = -10;
		/**
		 * The total number of tiles relative to the current width of the view.
		 */
		private int mTotalNrOfTiles = 0;
		private Paint mPaint = new Paint();
		/**
		 * The first x position of a user touch.
		 */
		private float mInitialX;
		/**
		 * The current x position of a user touch.
		 */
		private float mCurrentX;
		private OnValueUpdateListener mListener;
		/**
		 * The current value.
		 */
		private int mCurrentValue = 0;

		public interface OnValueUpdateListener {

			void onValueIncreased();

			void onValueDecreased();
		}

		public SliderButton(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public int getCurrentValue() {
			return mCurrentValue;
		}

		public SliderButton(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public SliderButton(Context context) {
			super(context);
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.slider_background);
			mDefaultHeight = mBitmap.getHeight();
			mDefaultWidth = mBitmap.getWidth();
		}

		public void setOnvalueUpdateListener(OnValueUpdateListener listener) {
			mListener = listener;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			int heightSize = MeasureSpec.getSize(heightMeasureSpec);
			int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			// if we don't have an exact width value make it the maximum value
			// between the proposed value(in case of MeasureSpec.AT_MOST) and
			// 5(arbitrary value) * tile bitmap width
			if (widthMode != MeasureSpec.EXACTLY) {
				widthSize = Math.max(widthSize, 5 * mDefaultWidth);
			}
			// if we don't have an exact height proposed then try to make at
			// least as tall as the tile bitmap
			if (heightMode != MeasureSpec.EXACTLY) {
				heightSize = Math.min(heightSize, mDefaultHeight);
			}
			setMeasuredDimension(widthSize, heightSize);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// the method doesn't handle flinging
			final int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mCurrentX = mInitialX = event.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				offsetBackground(mCurrentX, event.getX());
				mCurrentX = event.getX();
				if (mListener != null) {
					final float difference = mCurrentX - mInitialX;
					if (Math.abs(difference) >= STEP_SIZE) {
						if (difference > 0) {
							callListener(true);
							mInitialX += STEP_SIZE;
						} else {
							mInitialX -= STEP_SIZE;
							callListener(false);
						}
					}
				}
				break;
			}
			return true;
		}

		/**
		 * Helper method that offsets the mLeftPosition to take in consideration
		 * of user touches and also make sure that mLeftPosition is in certain x
		 * value so we always have only one Bitmap partially out of the screen
		 * in the left.
		 * 
		 * @param oldX
		 *            the old x value for which the onTouchEvent was called
		 * @param newX
		 *            the current x value
		 */
		private void offsetBackground(float oldX, float newX) {
			final float difference = newX - oldX;
			if (difference == 0) {
				return;
			} else if (difference > 0) {
				mLeftTilePos += difference;
				// we are swiping to the right and the first tile it's fully
				// visible so offset the mLeftPosition with mDefaultWidth to
				// offset it out of the screen so we have from where to continue
				// scrolling right
				if (mLeftTilePos >= 0) {
					mLeftTilePos = -mDefaultWidth;
				}
			} else if (difference < 0) {
				mLeftTilePos += difference;
				// we are scrolling left and we are about to scroll the first
				// tile completely out of the screen so bring mLeftPosition to
				// 0(fully visible) so that we have from were to continue
				// scrolling left
				if (mLeftTilePos <= -mDefaultWidth) {
					mLeftTilePos = 0;
				}
			}
			invalidate();
		}

		/**
		 * Notify our listener about a change
		 * 
		 * @param whichWay
		 *            true if it's an increase of value (user swipes to the
		 *            right), false otherwise.
		 */
		private void callListener(boolean whichWay) {
			if (whichWay) {
				mCurrentValue++;
				mListener.onValueIncreased();
			} else {
				mCurrentValue--;
				mListener.onValueDecreased();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.GREEN);
			// draw the number of tiles starting from mLeftPosition, each value
			// after that will be mLeftValue + multiple of mDefaultWidth
			for (int i = 0; i < mTotalNrOfTiles; i++) {
				canvas.drawBitmap(mBitmap, mLeftTilePos + mDefaultWidth * i, 0,
						mPaint);
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			// calculate the number of tiles according with our width, also
			// increase this value by one(or two) to make sure we are covering
			// the whole screen
			mTotalNrOfTiles = (w + mDefaultWidth) / mDefaultWidth;
			if ((w + mDefaultWidth) % mDefaultWidth != 0) {
				mTotalNrOfTiles++;
			}
		}

	}

}
