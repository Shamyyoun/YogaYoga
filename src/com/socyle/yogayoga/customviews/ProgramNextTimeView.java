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

public class ProgramNextTimeView extends View {
	private Paint mPaint;
	private Paint textPaint;
	private RectF mOval;
	private Rect textBounds;
	private int padding = 3;
	private String time;

	public ProgramNextTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true);
		mPaint.setColor(getResources()
				.getColor(R.color.progressview_fgcolor));

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(getResources()
				.getColor(R.color.progressview_fgcolor));
		textBounds = new Rect();

		mOval = new RectF();
		
		time = "12:23 PM";
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
		textPaint.setTextSize(2*radius / 11);

		int x = (canvas.getWidth() / 2) - (radius / 2)
				+ (stroke/2) + padding;
		int y = (canvas.getHeight() / 2) - (radius / 2)
				+ (stroke/2) + padding;
		mOval.set(x, y, x + radius - stroke
				- padding, y + radius - stroke - padding);

		// draw circle
		mPaint.setColor(getResources().getColor(R.color.progressview_fgcolor));
		canvas.drawOval(mOval, mPaint);

		// draw text
		textPaint.getTextBounds(time, 0, time.length(),
				textBounds);
		int xText = (radius / 2) - (textBounds.width() / 2) + x - (stroke/2) - padding;
		int yText = (int) ((radius / 2)
				- ((textPaint.descent() + textPaint.ascent()) / 2) + y);
		canvas.drawText(time, xText, yText, textPaint);

		invalidate();
	}
	
	public void setTime(String time) {
		this.time = time;
		invalidate();
	}
}
