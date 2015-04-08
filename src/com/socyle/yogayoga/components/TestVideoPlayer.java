package com.socyle.yogayoga.components;

import java.util.List;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.socyle.yogayoga.R;
import com.socyle.yogayoga.customviews.VideoProgressBarView;
import com.socyle.yogayoga.customviews.VideoProgressView;
import com.socyle.yogayoga.models.Exercise;
import com.socyle.yogayoga.models.Step;

public class TestVideoPlayer {
	private View layoutVideoControllers;
	private List<Step> steps;
	private ImageButton buttonPlay;
	private ImageView imageVideo;
	private Exercise exercise;
	private TextView textStepNum;
	private VideoProgressBarView progressBarVideo;
	private VideoProgressView progressViewVideo;
	private ActionBarActivity activity;

	private int currentFrame = -1;
	private Handler mHandler;
	private Runnable mRunnable;
	private boolean isPlaying = false;
	private int videoProgress = 0;
	private Animation animFadein;
	private Animation animFadeout;
	private boolean controllersHidden = false;
	private int currentFrameTime = 0;
	private int currentTotalTime = 0;
	private boolean paused = false;

	public TestVideoPlayer(View layoutVideoControllers, List<Step> steps,
			ImageButton buttonPlay, ImageView imageVideo, Exercise exercise,
			TextView textStepNum, VideoProgressBarView progressBarVideo,
			VideoProgressView progressViewVideo, ActionBarActivity activity) {

		this.layoutVideoControllers = layoutVideoControllers;
		this.steps = steps;
		this.buttonPlay = buttonPlay;
		this.imageVideo = imageVideo;
		this.exercise = exercise;
		this.textStepNum = textStepNum;
		this.progressBarVideo = progressBarVideo;
		this.progressViewVideo = progressViewVideo;
		this.activity = activity;

		mHandler = new Handler();
		mRunnable = new MRunnable();

		animFadein = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_in);
		animFadeout = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_out);

		// set animation listeners for anim
		animFadein.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setAlphaForView(TestVideoPlayer.this.layoutVideoControllers, 1);
			}
		});
		animFadeout.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setAlphaForView(TestVideoPlayer.this.layoutVideoControllers, 0);
			}
		});
	}

	public void play() {
		if (isPlaying) {
			pauseVideo();
		} else {
			if (paused) {
				int temp = currentFrame + 1;
				if (temp > steps.size() - 1) {
					endVideo();
				} else {
					gotoNextFrame();
					buttonPlay.setImageResource(R.drawable.ex_but_pause);
					playVideo();
				}
				paused = false;
			} else {
				buttonPlay.setImageResource(R.drawable.ex_but_pause);
				playVideo();
			}
		}
	}

	public void next() {
		if (controllersHidden) {
			showControllers(true);
		}
		if (isPlaying) {
			pauseVideo();
		}

		if (currentFrame >= steps.size() - 1) {
			// goto start of the video
			currentFrame = -1;
			currentFrameTime = steps.get(0).getDuration();
			imageVideo.setImageResource(steps.get(0).getImageId());
			currentTotalTime = 0;
		} else {
			currentFrame++;
			currentFrameTime = steps.get(currentFrame).getDuration();
			imageVideo.setImageResource(steps.get(currentFrame).getImageId());
			currentTotalTime = exercise.getCurrentTime(currentFrame);
			paused = true;
		}

		textStepNum.setText((currentFrame + 1) + "/" + (steps.size()));
		videoProgress = calcProgress();
		progressBarVideo.setProgress(videoProgress);
		if (progressViewVideo != null) {
			progressViewVideo.ANIM_DURATION = 500;
			progressViewVideo
					.setTime((exercise.getTotalDuration() - currentTotalTime) * 1000);
			progressViewVideo.ANIM_DURATION = 1000;
		}
	}

	public void back() {
		if (controllersHidden) {
			showControllers(true);
		}
		if (isPlaying) {
			pauseVideo();
		}

		if (currentFrame == 0) {
			// goto start of the video
			currentFrame = -1;
			currentFrameTime = steps.get(0).getDuration();
			imageVideo.setImageResource(steps.get(0).getImageId());
			currentTotalTime = 0;
		} else if (currentFrame <= -1) {
			// goto end of the video
			currentFrame = steps.size() - 1;
			currentFrameTime = steps.get(currentFrame).getDuration();
			imageVideo.setImageResource(steps.get(currentFrame).getImageId());
			currentTotalTime = exercise.getTotalDuration();
		} else {
			currentFrame--;
			currentFrameTime = steps.get(currentFrame).getDuration();
			imageVideo.setImageResource(steps.get(currentFrame).getImageId());
			currentTotalTime = exercise.getCurrentTime(currentFrame);
			paused = true;
		}

		textStepNum.setText((currentFrame + 1) + "/" + (steps.size()));
		videoProgress = calcProgress();
		progressBarVideo.setProgress(videoProgress);
		if (progressViewVideo != null) {
			progressViewVideo.ANIM_DURATION = 500;
			progressViewVideo
					.setTime((exercise.getTotalDuration() - currentTotalTime) * 1000);
			progressViewVideo.ANIM_DURATION = 1000;
		}
	}

	private void playVideo() {
		mHandler.post(mRunnable);
	}

	private void pauseVideo() {
		mHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacks(mRunnable);
		mHandler.removeMessages(0);

		// timerVideo.cancel();
		isPlaying = false;

		if (controllersHidden) {
			showControllers(true);
		}

		buttonPlay.setImageResource(R.drawable.ex_but_play);
	}

	private void gotoNextFrame() {
		// move to next frame
		currentFrame++;
		currentFrameTime = 0;

		// update UI
		if (progressViewVideo != null) {
			progressViewVideo.setTime(calcCurrentTime());
		}
	}

	private boolean shouldEndVideo() {
		if (currentFrame > steps.size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	private void endVideo() {
		// end video
		isPlaying = false;
		mHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacks(mRunnable);
		mHandler.removeMessages(0);
		currentFrame = -1;
		currentFrameTime = 0;
		currentTotalTime = 0;

		// update UI
		imageVideo.setImageResource(steps.get(0).getImageId());
		textStepNum.setText(0 + "/" + (steps.size()));
		showControllers(true);
		buttonPlay.setImageResource(R.drawable.ex_but_play);

		videoProgress = 0;
		progressBarVideo.setProgress(videoProgress);
		if (progressViewVideo != null) {
			progressViewVideo.ANIM_DURATION = 500;
			progressViewVideo.setTime(calcCurrentTime());
			progressViewVideo.ANIM_DURATION = 1000;
		}
	}

	private int calcProgress() {
		return (int) (((float) (currentFrame + 1) / steps.size()) * 100);
	}

	private long calcCurrentTime() {
		return (exercise.getTotalDuration() - currentTotalTime) * 1000;
	}

	public void showControllers(boolean show) {
		if (show) {
			// show controllers
			layoutVideoControllers.startAnimation(animFadein);
			controllersHidden = false;
		} else {
			// hide controllers
			layoutVideoControllers.startAnimation(animFadeout);
			controllersHidden = true;
		}
	}

	private void setAlphaForView(View v, float alpha) {
		AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
		animation.setDuration(0);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}

	public boolean isControllersHidden() {
		return controllersHidden;
	}

	private class MRunnable implements Runnable {

		@Override
		public void run() {
			if (shouldEndVideo()) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						endVideo();
					}
				});
			} else {
				currentTotalTime++;
				isPlaying = true;
				if (currentFrame == -1) {
					// start of the video
					currentFrame = 0;
				}
				if (currentFrameTime >= steps.get(currentFrame).getDuration() - 1) {

					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							gotoNextFrame();
						}
					});
				} else {
					// update UI
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							currentFrameTime++;

							if (!controllersHidden) {
								showControllers(false);
							}
							buttonPlay
									.setImageResource(R.drawable.ex_but_pause);

							// display image and edit info
							imageVideo.setImageResource(steps.get(currentFrame)
									.getImageId());
							textStepNum.setText((currentFrame + 1) + "/"
									+ steps.size());

							videoProgress = calcProgress();
							progressBarVideo.setProgress(videoProgress);
							if (progressViewVideo != null) {
								progressViewVideo.setTime(calcCurrentTime());
							}

							// currentFrameTime++;
						}
					});
				}
				// currentTotalTime++;
				mHandler.postDelayed(mRunnable, 1000);
			}
		}
	}
}
