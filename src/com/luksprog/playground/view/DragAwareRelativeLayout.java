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
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class DragAwareRelativeLayout extends RelativeLayout {

	private SparseArray<Rect> mTargetAreas = new SparseArray<Rect>();
	private int[] mNextTargets;

	private DragEventsListener mListener;

	/**
	 * Callback for listening od drag events on registered target views.
	 * 
	 * @author Luksprog
	 * 
	 */
	public static interface DragEventsListener {

		/**
		 * Callback triggered when the pointer enters the target view.
		 * 
		 * @param idOfEnteredView
		 *            the id of the target view into which the pointer is
		 *            currently in.
		 */
		void onDragViewEntered(int idOfEnteredView);

		/**
		 * Callback triggered when the dragged view is dropped.
		 * 
		 * @param onWhichView
		 *            the view on which the dragged view was dropped, null if no
		 *            target was touched.
		 */
		void onDragViewDropped(View onWhichView);

	}

	public DragAwareRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnDragListener(mDragListener);
	}

	/**
	 * Set a listener for the drag events that will be thrown by
	 * DragWareRelativeLayout.
	 * 
	 * @param listener
	 */
	public void setDragEventsListener(DragEventsListener listener) {
		mListener = listener;
	}

	@SuppressLint("NewApi")
	private OnDragListener mDragListener = new OnDragListener() {

		private int[] mTemp = new int[2];

		@Override
		public boolean onDrag(View v, DragEvent event) {
			final int action = event.getAction();
			switch (action) {
			case DragEvent.ACTION_DRAG_STARTED:
				// not implemented
				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				translateDragPoint(mTemp, event.getX(), event.getY());
				int id = checkAgainstTargets(mTemp);
				if (id != -1) {
					if (mListener != null) {
						mListener.onDragViewEntered(id);
					}
				}
				// at this level, with a flag we could trigger another callback
				// in
				// DragEventsListener to signal for an ACTION_DRAG_EXITED on a
				// target that previously triggered onDragViewEntered()
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				// not implemented
				break;
			case DragEvent.ACTION_DROP:
				translateDragPoint(mTemp, event.getX(), event.getY());
				int droppedId = checkAgainstTargets(mTemp);
				if (droppedId != -1) {
					if (mListener != null) {
						mListener.onDragViewDropped(findViewById(droppedId));
					}
				} else {
					mListener.onDragViewDropped(null);
				}
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				// not implemented
				break;
			}
			return true;
		}
	};

	/**
	 * Check the registered targets stored positions against the offset point of
	 * the dragged view.
	 * 
	 * @param what
	 *            an int array storing the coordinates of the dragged shadow
	 *            pointer.
	 * @return an int representing the id of the target view touched by the
	 *         dragged shadow pointer or -1 for an failed check.
	 */
	private int checkAgainstTargets(int[] what) {
		final int count = mNextTargets.length;
		for (int i = 0; i < count; i++) {
			final Rect current = mTargetAreas.get(mNextTargets[i]);
			if (current.contains(what[0], what[1])) {
				return mNextTargets[i];
			}
		}
		return -1;
	}

	/**
	 * Registers the view targets for the next drag operation.
	 * 
	 * @param views
	 *            the target views.
	 */
	public void registerNextDragTargets(View... views) {
		if (views == null) {
			return;
		}
		int[] viewsIds = new int[views.length];
		for (int i = 0; i < views.length; i++) {
			if (views[i].getId() == View.NO_ID) {
				throw new IllegalArgumentException(
						"Can't register as a drag target a view without a valid id!");
			}
			viewsIds[i] = views[i].getId();
		}
		registerNextDragTargets(viewsIds);
	}

	/**
	 * Registers the view targets for the next drag operation.
	 * 
	 * @param ids
	 *            the ids of the target views.
	 */
	public void registerNextDragTargets(int... ids) {
		if (ids == null) {
			return;
		} else if (mNextTargets == null) {
			mNextTargets = new int[ids.length];
		}
		for (int i = 0; i < ids.length; i++) {
			mNextTargets[i] = ids[i];
		}
	}

	public void removeDragTarget(int... ids) {
		// to be implemented
	}

	/**
	 * We need to translate the shadowTouchPoint set in the
	 * onProvideShadowMetrics() method to the actual tip of the arrow.
	 * 
	 * @param out
	 *            a int[] array where the offset values will be stored(for
	 *            performance as this will be called a lot in the
	 *            DragEvent.ACTION_DRAG_LOCATION action)
	 * @param x
	 *            the x coordinate of the shadowTouchPoint
	 * @param y
	 *            the y coordinate of the shadowTouchPoint
	 */
	private void translateDragPoint(int[] out, float x, float y) {
		out[0] = (int) x;
		// cheating, I know that the touch point will be placed at half the
		// height and 3/4 of the width, but we shouldn't rely on the
		// draggedViewHeight!
		out[1] = (int) (y - DragShadowBuilderExtension.DEFAULT_EXTENSION - (DragShadowBuilderExtension.draggedViewHeight / 2));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initTargetAreas();
	}

	/**
	 * If there is a change in the layout, re-register the Rect areas of the
	 * targets.
	 */
	private void initTargetAreas() {
		Rect r = null;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getId() == View.NO_ID) {
				continue;
			}
			Rect current = mTargetAreas.get(child.getId());
			if (current == null) {
				r = new Rect();
				mTargetAreas.append(child.getId(), r);
			} else {
				current.left = child.getLeft();
				current.bottom = child.getBottom();
				current.right = child.getRight();
				current.top = child.getTop();
			}
		}
	}

	public static class DragShadowBuilderExtension extends
			View.DragShadowBuilder {

		public static final int DEFAULT_EXTENSION = 80;
		private View mDragged;
		private Paint mPaint = new Paint();
		private Path mArrowPath = new Path();
		private static int draggedViewHeight;

		public DragShadowBuilderExtension(View draggedView) {
			mDragged = draggedView;
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Style.FILL_AND_STROKE);
			mArrowPath.moveTo((3 * mDragged.getWidth() / 4), 0);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) + 10, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) + 3, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) + 3,
					DEFAULT_EXTENSION);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) - 3,
					DEFAULT_EXTENSION);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) - 3, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4) - 10, 10);
			mArrowPath.lineTo((3 * mDragged.getWidth() / 4), 0);
			mArrowPath.close();
		}

		@Override
		public void onProvideShadowMetrics(Point shadowSize,
				Point shadowTouchPoint) {
			shadowSize.x = mDragged.getWidth();
			shadowSize.y = mDragged.getHeight() + DEFAULT_EXTENSION;
			shadowTouchPoint.x = (3 * mDragged.getWidth() / 4);
			shadowTouchPoint.y = DEFAULT_EXTENSION + mDragged.getHeight() / 2;
			draggedViewHeight = mDragged.getHeight();
		}

		@Override
		public void onDrawShadow(Canvas canvas) {
			canvas.save();
			canvas.translate(0, DEFAULT_EXTENSION);
			mDragged.draw(canvas);
			canvas.restore();
			canvas.drawPath(mArrowPath, mPaint);
		}
	}

}