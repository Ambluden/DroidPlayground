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
package com.luksprog.playground.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ArrowView extends View {

	private View mTargetView;
	private Path mArrow = new Path();
	private Paint mPaint = new Paint();

	public ArrowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint.setColor(Color.parseColor("#99cc00"));
		mPaint.setStyle(Style.FILL);
	}

	public ArrowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setColor(Color.parseColor("#99cc00"));
		mPaint.setStyle(Style.FILL);
	}

	public ArrowView(Context context) {
		super(context);
		mPaint.setColor(Color.parseColor("#99cc00"));
		mPaint.setStyle(Style.FILL);
	}

	public void setTargetItem(View target) {
		mTargetView = target;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mArrow.reset();
		if (mTargetView != null) {
			mArrow.moveTo(0, getMeasuredHeight() / 2);
			final int half = mTargetView.getLeft() + mTargetView.getWidth() / 2;
			mArrow.lineTo(half - 5, getMeasuredHeight() / 2);
			mArrow.lineTo(half, 0);
			mArrow.lineTo(half + 5, getMeasuredHeight() / 2);
			mArrow.lineTo(getMeasuredWidth(), getMeasuredHeight() / 2);
			mArrow.lineTo(getMeasuredWidth(), getMeasuredHeight());
			mArrow.lineTo(0, getMeasuredHeight());
			mArrow.close();
			canvas.drawPath(mArrow, mPaint);
			Log.e("TAG", "Left : " + mTargetView.getLeft() + " Right: "
					+ mTargetView.getRight());
		}

	}

}
