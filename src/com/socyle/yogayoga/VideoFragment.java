package com.socyle.yogayoga;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socyle.yogayoga.customviews.VideoProgressBarView;
import com.socyle.yogayoga.customviews.VideoProgressView;
import com.socyle.yogayoga.models.Exercise;
import com.socyle.yogayoga.models.Step;
import com.socyle.yogayoga.utils.AnimationUtil;

public class VideoFragment extends Fragment implements OnClickListener {
	public static final String KEY_FRAME_NO = "frame no.";
	public static final String KEY_CONTROLLERS_VISIBLE = "controllers visible";
	public static final String KEY_TEXT_FRAME_NO = "text frame no.";

	private Exercise exercise;
	private ActionBarActivity activity;

	private FrameLayout layoutVideoContainer;
	private ImageButton buttonStepsDesc;
	private ImageView imageVideo;
	private LinearLayout layoutVideoControllers;
	private ImageButton buttonPlay;
	private ImageButton buttonNext;
	private ImageButton buttonBack;
	private TextView textStepNum;
	private VideoProgressBarView progressBarVideo;
	private VideoProgressView progressViewVideo;
	private List<Step> steps;
	private int currentFrame = -1;
	private Handler mHandler;
	private Runnable mRunnable;
	private boolean isPlaying = false;
	private int videoProgress = 0;
	private Animation animFadein;
	private Animation animFadeout;
	private boolean isControllersHidden = false;
	private int currentFrameTime = 0;
	private int currentTotalTime = 0;
	private boolean paused = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_video,
				container, false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		// define components
		exercise = (Exercise) getArguments().get(PosesFragment.KEY_EXERCISE);
		activity = (ActionBarActivity) getActivity();
		activity.setTitle(exercise.getName());
		buttonStepsDesc = (ImageButton) rootView
				.findViewById(R.id.button_stepsdesc);
		imageVideo = (ImageView) rootView.findViewById(R.id.image_videoFrame);
		layoutVideoContainer = (FrameLayout) rootView
				.findViewById(R.id.layout_videoContainer);
		layoutVideoControllers = (LinearLayout) rootView
				.findViewById(R.id.layout_videoControllers);
		buttonPlay = (ImageButton) rootView.findViewById(R.id.button_play);
		buttonNext = (ImageButton) rootView.findViewById(R.id.button_next);
		buttonBack = (ImageButton) rootView.findViewById(R.id.button_back);
		mHandler = new Handler();
		mRunnable = new MRunnable();
		textStepNum = (TextView) rootView.findViewById(R.id.text_stepNum);
		progressBarVideo = (VideoProgressBarView) rootView
				.findViewById(R.id.progressBar_video);
		progressViewVideo = (VideoProgressView) rootView
				.findViewById(R.id.progressViewVideo);
		steps = exercise.getSteps();
		animFadein = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_in);
		animFadeout = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_out);

		// set initial values
		imageVideo.setImageResource(steps.get(0).getImageId());
		textStepNum.setText(0 + "/" + (steps.size()));
		progressViewVideo.setDuration(exercise.getTotalDuration() * 1000);

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
				AnimationUtil.setAlpha(layoutVideoControllers, 1);
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
				AnimationUtil.setAlpha(layoutVideoControllers, 0);
			}
		});

		// set click listeners
		buttonStepsDesc.setOnClickListener(this);
		layoutVideoContainer.setOnClickListener(this);
		buttonPlay.setOnClickListener(this);
		buttonNext.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.layout_videoContainer:
			showControllers(isControllersHidden);
			break;

		case R.id.button_stepsdesc:
			onButtonStepsDesc();
			break;

		case R.id.button_play:
			onButtonPlay();
			break;

		case R.id.button_next:
			onButtonNext();
			break;

		case R.id.button_back:
			onButtonBack();
			break;

		default:
			break;
		}
	}

	private void onButtonStepsDesc() {
		// send exercise to stepsdesc fragment
		Bundle bundle = new Bundle();
		bundle.putSerializable(PosesFragment.KEY_EXERCISE, exercise);
		StepsDescFragment fragment = new StepsDescFragment();
		fragment.setArguments(bundle);

		// display stepsdesc fragment
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.setCustomAnimations(R.anim.fragmentdesc_slide_in,
				R.anim.fragmentvideo_slide_out);
		ft.replace(R.id.exercise_container, fragment);
		ft.commit();
	}

	private void onButtonPlay() {
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

	private void onButtonNext() {
		if (isControllersHidden) {
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
		progressViewVideo.ANIM_DURATION = 500;
		progressViewVideo
				.setTime((exercise.getTotalDuration() - currentTotalTime) * 1000);
		progressViewVideo.ANIM_DURATION = 1000;
	}

	private void onButtonBack() {
		if (isControllersHidden) {
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
		progressViewVideo.ANIM_DURATION = 500;
		progressViewVideo
				.setTime((exercise.getTotalDuration() - currentTotalTime) * 1000);
		progressViewVideo.ANIM_DURATION = 1000;
	}

	private void playVideo() {
		mHandler.post(mRunnable);
	}

	private void pauseVideo() {
		mHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacks(mRunnable);
		mHandler.removeMessages(0);

		isPlaying = false;

		if (isControllersHidden) {
			showControllers(true);
		}

		buttonPlay.setImageResource(R.drawable.ex_but_play);
	}

	private void gotoNextFrame() {
		// move to next frame
		currentFrame++;
		currentFrameTime = 0;

		// update UI
		progressViewVideo.setTime(calcCurrentTime());
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
		progressViewVideo.ANIM_DURATION = 500;
		progressViewVideo.setTime(calcCurrentTime());
		progressViewVideo.ANIM_DURATION = 1000;
	}

	private int calcProgress() {
		return (int) (((float) (currentFrame + 1) / steps.size()) * 100);
	}

	private long calcCurrentTime() {
		return (exercise.getTotalDuration() - currentTotalTime) * 1000;
	}

	private void showControllers(boolean show) {
		if (show) {
			// show controllers
			layoutVideoControllers.startAnimation(animFadein);
			isControllersHidden = false;
		} else {
			// hide controllers
			layoutVideoControllers.startAnimation(animFadeout);
			isControllersHidden = true;
		}
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

							if (!isControllersHidden) {
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
							progressViewVideo.setTime(calcCurrentTime());

						}
					});
				}
				mHandler.postDelayed(mRunnable, 1000);
			}
		}
	}
}
