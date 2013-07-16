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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

import com.luksprog.playground.R;

public class ArcView extends View {

	private static final int BAR_WIDTH = 30;
	private Paint mInitialArcPaint = new Paint();
	private Paint mCoverPaint = new Paint();
	private Paint mFillPaint = new Paint();
	private Paint mTextPaint = new Paint();
	private Paint mBackPaint = new Paint();
	private RectF mRectF = new RectF();
	private Rect mBackRect = new Rect();
	private int mFillValue = 0;
	private float mAngleValue = 80;
	private int x, y;

	public ArcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ArcView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ArcView(Context context) {
		super(context);
		mInitialArcPaint.setAntiAlias(true);
		mInitialArcPaint.setColor(Color.parseColor("#f8cccb"));
		mCoverPaint.setAntiAlias(true);
		mFillPaint.setAntiAlias(true);
		mFillPaint.setColor(Color.parseColor("#e73229"));
		mTextPaint.setTextSize(20);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cream_pixels);
		mBackPaint.setShader(new BitmapShader(bitmap, TileMode.REPEAT, TileMode.REPEAT));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mBackRect.left = (int)(mRectF.left = 0);
		mBackRect.top = (int)(mRectF.top = 0);
		mBackRect.right = (int)(mRectF.right = getWidth());
		mBackRect.bottom = (int)(mRectF.bottom = getHeight());
		canvas.drawRect(mBackRect, mBackPaint);
		final int centerX = getWidth() / 2;
		final int centerY = getHeight() / 2;
		
		// use 10 degrees for the bottom middle space on both side
		canvas.drawArc(mRectF, 80, -340, true, mInitialArcPaint);
		canvas.drawArc(mRectF, 80, -mAngleValue, true, mFillPaint);
		mCoverPaint.setColor(Color.parseColor("#fee1eb"));
		mCoverPaint.setStyle(Style.STROKE);
		mCoverPaint.setStrokeWidth(2.0f);
		canvas.drawCircle(centerX, centerY, centerY - BAR_WIDTH, mCoverPaint);
		mCoverPaint.setColor(Color.WHITE);
		mCoverPaint.setStyle(Style.FILL);
		canvas.drawCircle(centerX, centerY, centerY - BAR_WIDTH, mCoverPaint);
		canvas.drawText(String.valueOf(mFillValue), x, y, mTextPaint);
	}

	public int getFillValue() {
		return mFillValue;
	}

	/**
	 * Values between 0 and 500.
	 * 
	 * @param fillValue
	 */
	public void setFillValue(int fillValue) {
		this.mFillValue = fillValue;
		x = getWidth() / 2
				- (int) (mTextPaint.measureText(String.valueOf(mFillValue)) / 2);
		y = getHeight() / 2 - (int) (mTextPaint.getTextSize() / 2);
		mAngleValue = (mFillValue / 500f) * 340f;
		invalidate();
	}	
	
}
