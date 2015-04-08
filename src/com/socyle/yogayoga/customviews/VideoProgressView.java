package com.socyle.yogayoga.customviews;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.socyle.yogayoga.R;
import com.socyle.yogayoga.utils.TimeUtil;

public class VideoProgressView extends View {
	public int ANIM_DURATION = 1000;
	private Paint mPaint;
	private Paint textPaint;
	private RectF mOval;
	private int padding = 3;
	private Rect textBounds;
	private int oldPercent;
	private int mStart = 90;
	private int mSweep = 0;
	private long duration = 0;
	private long time = 0;

	public VideoProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true);

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(getResources().getColor(
				R.color.videoprogress_fgcolor));
		textBounds = new Rect();

		mOval = new RectF();
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
		int stroke = (int) ((31 / 171.0) * (3 * radius / 2));
		int textSize = radius / 4;

		mPaint.setStrokeWidth(stroke);
		textPaint.setTextSize(textSize);

		int xOval = (stroke / 2) + padding;
		int yOval = (canvas.getHeight() / 2)
				- (radius + (stroke / 2) + padding);
		mOval.set(xOval, yOval, (xOval + radius - padding - (stroke / 2)) * 2,
				(yOval + radius * 2 - padding - stroke));

		// draw circle
		mPaint.setColor(getResources().getColor(R.color.videoprogress_bgcolor));
		canvas.drawOval(mOval, mPaint);

		// draw progress
		mPaint.setColor(getResources().getColor(R.color.videoprogress_fgcolor));
		canvas.drawArc(mOval, mStart, mSweep, false, mPaint);

		// draw text
		textPaint.getTextBounds("00:00", 0, ("00:00").length(), textBounds);
		int xText = xOval + ((2 * stroke) / 3);
		int yText = (int) ((radius)
				- ((textPaint.descent() + textPaint.ascent()) / 2) + yOval - (stroke / 2));
		canvas.drawText(TimeUtil.convertTime("mm:ss", time), xText, yText,
				textPaint);

		invalidate();
	}

	public void setTime(long time) {
		this.time = time;

		if (time < 0) {
			time = 0;
		} else if (time > duration) {
			time = duration;
		}
		animateTo(time);
	}

	@SuppressLint("NewApi")
	private void animateTo(long time) {
		int percent = (int) (((duration - time) / (float) duration) * 100);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;

		// animate to desired progress value
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			final ValueAnimator anim = ValueAnimator.ofInt(oldPercent, percent);
			anim.setDuration(ANIM_DURATION);
			anim.setInterpolator(new LinearInterpolator());
			anim.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					int mPercent = Integer.parseInt(anim.getAnimatedValue()
							.toString());
					mSweep = (int) (180 * ((float) mPercent / 100));
					invalidate();
				}
			});
			anim.start();
		} else {
			final com.nineoldandroids.animation.ValueAnimator anim = com.nineoldandroids.animation.ValueAnimator
					.ofInt(oldPercent, percent);
			anim.setDuration(ANIM_DURATION);
			anim.setInterpolator(new LinearInterpolator());
			anim.addUpdateListener(new com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(
						com.nineoldandroids.animation.ValueAnimator animation) {
					int mPercent = Integer.parseInt(anim.getAnimatedValue()
							.toString());
					mSweep = (int) (180 * ((float) mPercent / 100));
					invalidate();
				}
			});
			anim.start();
		}

		oldPercent = percent;
	}

	public long getTime() {
		return time;
	}

	public void setDuration(long duration) {
		this.duration = duration;
		time = duration;
		invalidate();
	}

	public long getDuration() {
		return duration;
	}
}
