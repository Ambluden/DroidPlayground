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
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.luksprog.playground.R;

/**
 * @see http 
 *      ://stackoverflow.com/questions/17593066/how-to-align-one-image-to-another
 *      -after-sliding-with-a-finger
 * 
 */
public class SnappingScroll extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_snappingscroll);
		final TryHorizontalScrollView thsv = (TryHorizontalScrollView) findViewById(R.id.searchButtonBar);
		thsv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(int newState) {
				if (newState == OnScrollListener.SCROLL_ENDED) {
					thsv.snapContent();
				}
			}
		});
	}

	/**
	 * A basic interface to output events like scroll started and ended.
	 * 
	 */
	public interface OnScrollListener {

		public static final int SCROLL_STARTED = 0x01;
		public static final int SCROLL_ENDED = 0x02;

		void onScrollStateChanged(int newState);
	}

	/**
	 * Custom class to handle all the event dispatching and snap ability of
	 * children.
	 * 
	 */
	public static class TryHorizontalScrollView extends HorizontalScrollView {

		private Paint mPaint = new Paint();
		private OnScrollListener mListener;
		/**
		 * The center of the HorizontalScrollView(assuming that it will be set
		 * to fill the screen).
		 */
		private int mCenterX = 0;
		// two boolean values to handle the scroll events
		private boolean mScrollObserver = false;
		private boolean mIsRealScroll = true;

		public TryHorizontalScrollView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
		}

		public TryHorizontalScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
			mPaint.setColor(Color.GREEN);
		}

		public TryHorizontalScrollView(Context context) {
			super(context);
		}

		public void setOnScrollListener(OnScrollListener listener) {
			mListener = listener;
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			// reset mCenterX if the size changes
			if (w != 0) {
				mCenterX = w / 2;
			}
		}

		protected void onScrollChanged(int x, int y, int oldx, int oldy) {
			super.onScrollChanged(x, y, oldx, oldy);
			if (!mScrollObserver && mIsRealScroll) {
				mScrollObserver = true;
				if (mListener != null) {
					mListener
							.onScrollStateChanged(OnScrollListener.SCROLL_STARTED);
				}
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			final View leftMargin = ((ViewGroup) getChildAt(0)).getChildAt(0);
			final View rightMargin = ((ViewGroup) getChildAt(0)).getChildAt(2);
			final ViewGroup contentChild = (ViewGroup) (((ViewGroup) getChildAt(0))
					.getChildAt(1));

			if (contentChild != null && contentChild.getChildCount() > 0) {
				// as we use layout_weight, our ImageButtons are pretty much
				// guaranteed to be equal in width so we only calculate the
				// target margin using one of the children, otherwise we would
				// need to calculate the left and right margin based on the
				// first and last ImageButton
				final View firstChildContent = contentChild.getChildAt(0);
				leftMargin.getLayoutParams().width = rightMargin
						.getLayoutParams().width = mCenterX
						- firstChildContent.getWidth() / 2;
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP
					|| event.getAction() == MotionEvent.ACTION_CANCEL) {
				mScrollObserver = false;
				if (mListener != null) {
					mListener
							.onScrollStateChanged(OnScrollListener.SCROLL_ENDED);
				}
			}
			return super.onTouchEvent(event);
		}

		public void snapContent() {
			final ViewGroup contentChild = (ViewGroup) ((ViewGroup) getChildAt(0))
					.getChildAt(1);
			// this uses for easy calculation the position of the child view
			// relative to the screen knowing that the HorizontalScrollView fill
			// the entire width of the screen.
			final int count = contentChild.getChildCount();
			int coords[] = new int[2];
			View closestChild = null;
			for (int i = 0; i < count; i++) {
				final View child = contentChild.getChildAt(i);
				child.getLocationOnScreen(coords);
				if (coords[0] != 0 && coords[0] + child.getWidth() != 0) {
					if (coords[0] <= mCenterX
							&& mCenterX <= coords[0] + child.getWidth()) {
						closestChild = child;
						break;
					}
				}
			}
			if (closestChild == null) {
				// would never happen
				return;
			}
			int childCenter = coords[0] + closestChild.getWidth() / 2;
			int scrollOffset = mCenterX - childCenter;
			mIsRealScroll = false;
			scrollBy(-scrollOffset, 0);
			mIsRealScroll = true;
		}
	}

	public void engineClicked(View v) {
		Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT)
				.show();
	}

}
