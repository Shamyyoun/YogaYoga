package com.socyle.yogayoga.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.socyle.yogayoga.R;

public class ProgramProgressView extends View {
	private Paint mPaint;
	private Paint textPaint;
	private RectF mOval;
	private int progress;
	private int mStart;
	private int mSweep;
	private Rect textBounds;
	private int padding = 3;

	public ProgramProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true);

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(getResources()
				.getColor(R.color.progressview_fgcolor));
		textBounds = new Rect();

		mOval = new RectF();

		mStart = 270;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int radius;
		if (canvas.getWidth() > canvas.getHeight()) {
			radius = canvas.getHeight();
		} else {
			radius = canvas.getWidth();
		}
		int stroke = (int) ((6 / 222.0) * radius);
		if (stroke == 0) {
			stroke = 1;
		}
		mPaint.setStrokeWidth(stroke);
		textPaint.setTextSize(radius / 4);

		int xProgress = (canvas.getWidth() / 2) - (radius / 2)
				+ (stroke/2) + padding;
		int yProgress = (canvas.getHeight() / 2) - (radius / 2)
				+ (stroke/2) + padding;
		mOval.set(xProgress, yProgress, xProgress + radius - stroke
				- padding, yProgress + radius - stroke - padding);

		// draw circle
		mPaint.setColor(getResources().getColor(R.color.progressview_bgcolor));
		canvas.drawOval(mOval, mPaint);

		// draw progress
		mPaint.setColor(getResources().getColor(R.color.progressview_fgcolor));
		canvas.drawArc(mOval, mStart, mSweep, false, mPaint);

		// draw text
		textPaint.getTextBounds(progress + "%", 0, (progress + "%").length(),
				textBounds);
		int xText = (radius / 2) - (textBounds.width() / 2) + xProgress;
		int yText = (int) ((radius / 2)
				- ((textPaint.descent() + textPaint.ascent()) / 2) + yProgress);
		canvas.drawText(progress + "%", xText, yText, textPaint);

		invalidate();
	}

	public void setProgress(int progress) {
		this.progress = progress;

		if (progress < 0) {
			progress = 0;
		}
		if (progress > 100) {
			progress = 100;
		}

		mSweep = (int) (360 * ((float) progress / 100));
		invalidate();
	}

	public int getProgress() {
		return progress;
	}
}
