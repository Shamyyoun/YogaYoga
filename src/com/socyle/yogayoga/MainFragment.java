package com.socyle.yogayoga;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.utils.FragmentUtil;

public class MainFragment extends Fragment implements OnClickListener {
	public static final String TAG = "main fragmnet";
	private MainActivity activity;
	private ImageView imageLoading1;
	private ImageView imageLoading2;
	private ImageView imageLoading3;
	private ImageView imageLoading4;
	private ImageButton buttonSettings;
	private ImageButton buttonPoses;
	private ImageButton buttonMusic;
	private ImageButton buttonTimer;
	private ImageButton buttonProgram;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		activity = (MainActivity) getActivity();
		imageLoading1 = (ImageView) rootView.findViewById(R.id.image_loading1);
		imageLoading2 = (ImageView) rootView.findViewById(R.id.image_loading2);
		imageLoading3 = (ImageView) rootView.findViewById(R.id.image_loading3);
		imageLoading4 = (ImageView) rootView.findViewById(R.id.image_loading4);
		buttonSettings = (ImageButton) rootView
				.findViewById(R.id.button_settings);
		buttonPoses = (ImageButton) rootView.findViewById(R.id.button_poses);
		buttonMusic = (ImageButton) rootView.findViewById(R.id.button_music);
		buttonTimer = (ImageButton) rootView.findViewById(R.id.button_timer);
		buttonProgram = (ImageButton) rootView
				.findViewById(R.id.button_program);

		// handle navigation
		NavigationInfo.fragment = null;
		NavigationInfo.tag = null;

		// customize menu drawer
		activity.mActiveViewId = activity.textHome.getId();
		activity.selectMenuDrawerItem(activity.textHome);

		// customize activity
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// customize actionbar
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setTitle(R.string.app_name);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);

		// add animations
		Animation anim = AnimationUtils.loadAnimation(activity,
				R.anim.loading_animation);
		imageLoading1.startAnimation(anim);
		imageLoading2.startAnimation(anim);
		imageLoading3.startAnimation(anim);
		imageLoading4.startAnimation(anim);

		// add action listeners
		buttonSettings.setOnClickListener(this);
		buttonPoses.setOnClickListener(this);
		buttonMusic.setOnClickListener(this);
		buttonTimer.setOnClickListener(this);
		buttonProgram.setOnClickListener(this);
		buttonSettings.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_poses:
			onButtonPoses();
			break;

		case R.id.button_music:
			onButtonMusic();
			break;

		case R.id.button_program:
			onButtonProgram();
			break;

		case R.id.button_timer:
			onButtonTimer();
			break;

		case R.id.button_settings:
			onButtonSettings();
			break;
		default:
			break;
		}
	}

	private void onButtonPoses() {
		FragmentUtil.gotoFragment(activity, new PosesFragment(), PosesFragment.TAG);
	}

	private void onButtonMusic() {
	}

	private void onButtonTimer() {
		FragmentUtil.gotoFragment(activity, new TimerSettingsFragment(), TimerSettingsFragment.TAG);
	}

	private void onButtonProgram() {
//		FragmentUtil.gotoFragment(activity, new ProgramFragment(), ProgramFragment.TAG);
		FragmentUtil.gotoFragment(activity, new ProgramFragment2(), ProgramFragment2.TAG);
	}

	private void onButtonSettings() {
	}
}
