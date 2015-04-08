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

import com.socyle.yogayoga.components.TestVideoPlayer;
import com.socyle.yogayoga.customviews.VideoProgressBarView;
import com.socyle.yogayoga.customviews.VideoProgressView;
import com.socyle.yogayoga.models.Exercise;
import com.socyle.yogayoga.models.Step;

public class TestVideoFragment extends Fragment implements OnClickListener {
	public static final String KEY_FRAME_NO = "frame no.";
	public static final String KEY_CONTROLLERS_VISIBLE = "controllers visible";
	public static final String KEY_TEXT_FRAME_NO = "text frame no.";

	private Exercise exercise;
	private ActionBarActivity activity;

	private TestVideoPlayer videoPlayer;
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
		textStepNum = (TextView) rootView.findViewById(R.id.text_stepNum);
		progressBarVideo = (VideoProgressBarView) rootView
				.findViewById(R.id.progressBar_video);
		progressViewVideo = (VideoProgressView) rootView
				.findViewById(R.id.progressViewVideo);
		steps = exercise.getSteps();

		// set initial values
		imageVideo.setImageResource(steps.get(0).getImageId());
		textStepNum.setText(0 + "/" + (steps.size()));
		progressViewVideo.setDuration(exercise.getTotalDuration() * 1000);

		// set click listeners
		buttonStepsDesc.setOnClickListener(this);
		layoutVideoContainer.setOnClickListener(this);
		buttonPlay.setOnClickListener(this);
		buttonNext.setOnClickListener(this);
		buttonBack.setOnClickListener(this);

		// create the video player and pass parameters
		videoPlayer = new TestVideoPlayer(layoutVideoControllers, steps,
				buttonPlay, imageVideo, exercise, textStepNum,
				progressBarVideo, progressViewVideo, activity);
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.layout_videoContainer:
			videoPlayer.showControllers(videoPlayer.isControllersHidden());
			break;

		case R.id.button_stepsdesc:
			onButtonStepsDesc();
			break;

		case R.id.button_play:
			videoPlayer.play();
			break;

		case R.id.button_next:
			videoPlayer.next();
			break;

		case R.id.button_back:
			videoPlayer.back();
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
}
