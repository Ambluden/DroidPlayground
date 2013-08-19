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
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.luksprog.playground.R;
import com.luksprog.playground.view.GameViewSample.GameView.Direction;

public class GameViewSample extends Activity implements View.OnClickListener {

	private Button mGoUp, mGoLeft, mGoRight, mGoDown;
	private GameView mGameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_gameviewsample);
		mGameView = (GameView) findViewById(R.id.gameView);
		mGoUp = (Button) findViewById(R.id.buttonUp);
		mGoUp.setOnClickListener(this);
		mGoLeft = (Button) findViewById(R.id.buttonLeft);
		mGoLeft.setOnClickListener(this);
		mGoRight = (Button) findViewById(R.id.buttonRight);
		mGoRight.setOnClickListener(this);
		mGoDown = (Button) findViewById(R.id.buttonDown);
		mGoDown.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonUp:
			mGameView.update(Direction.UP, 5);
			break;
		case R.id.buttonRight:
			mGameView.update(Direction.RIGHT, 5);
			break;
		case R.id.buttonDown:
			mGameView.update(Direction.DOWN, 5);
			break;
		case R.id.buttonLeft:
			mGameView.update(Direction.LEFT, 5);
			break;
		}
	}

	public static class GameView extends View {

		private static final int UNBOUND_SIZE = 800;
		private Bitmap mBitmap;
		private int mBitmapHeight = 0;
		private int mBitmapWidth = 0;
		private Paint mPaint = new Paint();
		private Point mCurrentLocation = new Point();

		public GameView(Context context, AttributeSet attrs) {
			super(context, attrs);
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			mBitmapHeight = mBitmap.getHeight();
			mBitmapWidth = mBitmap.getWidth();
			mCurrentLocation.x = 50;
			mCurrentLocation.y = 50;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			boolean changePaint = false;
			
			//mPaint.setStyle(changePaint ? Style.STROKE : null);
			canvas.drawBitmap(mBitmap, mCurrentLocation.x, mCurrentLocation.y,
					mPaint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			int heightSize = MeasureSpec.getSize(heightMeasureSpec);
			final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			// find out which is the smallest between the offered width and
			// height, as you use this in portrait, it would most likely be the
			// width
			if (widthMode == MeasureSpec.UNSPECIFIED) {
				widthSize = UNBOUND_SIZE;
			}
			if (heightMode == MeasureSpec.UNSPECIFIED) {
				heightSize = UNBOUND_SIZE;
			}
			widthSize = Math.min(widthSize, heightSize);
			// the view is now square
			setMeasuredDimension(widthSize, widthSize);
		}

		public void update(Direction whereTo, int amount) {
			switch (whereTo) {
			case UP:
				mCurrentLocation.y -= amount;
				break;
			case RIGHT:
				mCurrentLocation.x += amount;
				break;
			case DOWN:
				mCurrentLocation.y += amount;
				break;
			case LEFT:
				mCurrentLocation.x -= amount;
				break;
			}
			invalidate();
		}		

		public static enum Direction {
			UP, RIGHT, LEFT, DOWN
		}

	}

}
