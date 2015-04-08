package com.socyle.yogayoga.customviews;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.socyle.yogayoga.R;
import com.socyle.yogayoga.VideoFragment;

public class VideoProgressBarView extends View {
	private Paint mPaint;
	private int oldProgress = 0;
	private int newProgress = 0;
	private int padding;
	private Bitmap icon;
	private final int ANIM_DURATION = 500;

	public VideoProgressBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.exercise_progressicon);
		padding = icon.getWidth();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw background
		int width = canvas.getWidth() - (padding*2);
		int height = (int) ((14 / 38.0) * icon.getHeight());
		int x = padding;
		int y = (canvas.getHeight() - height) / 2;
		mPaint.setColor(getResources().getColor(
				R.color.videoprogressbar_bgcolor));
		canvas.drawRect(x, y, x + width, y + height,
				mPaint);

		// draw progress
		mPaint.setColor(getResources().getColor(
				R.color.videoprogressbar_fgcolor));
		int progressWidth = (int) ((newProgress / 100.0) * width);
		canvas.drawRect(x, y, x + progressWidth, y + height, mPaint);

		// draw icon
		int xIcon = x + progressWidth - (icon.getWidth()/2);
		int yIcon = (canvas.getHeight() - icon.getHeight()) / 2;
		canvas.drawBitmap(icon, xIcon, yIcon, mPaint);

		invalidate();
	}

	@SuppressLint("NewApi")
	public void setProgress(int progress) {
		if (progress > 100) {
			progress = 100;
		} else if (progress < 0) {
			progress = 0;
		}

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;

		// animate to desired progress value
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			final ValueAnimator anim = ValueAnimator.ofInt(oldProgress,
					progress);
			anim.setDuration(ANIM_DURATION);
			anim.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					newProgress = Integer.parseInt(anim.getAnimatedValue()
							.toString());
					invalidate();
				}
			});
			anim.start();
		} else {
			final com.nineoldandroids.animation.ValueAnimator anim = com.nineoldandroids.animation.ValueAnimator
					.ofInt(oldProgress, progress);
			anim.setDuration(ANIM_DURATION);
			anim.addUpdateListener(new com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(
						com.nineoldandroids.animation.ValueAnimator animation) {
					newProgress = Integer.parseInt(anim.getAnimatedValue()
							.toString());
					invalidate();
				}
			});
			anim.start();
		}

		oldProgress = progress;
	}

	public int getProgress() {
		return newProgress;
	}
}
