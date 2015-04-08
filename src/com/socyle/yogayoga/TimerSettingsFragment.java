package com.socyle.yogayoga;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.utils.FragmentUtil;

public class TimerSettingsFragment extends Fragment implements OnClickListener {
	public static final String TAG = "timer settings fragmnet";
	private MainActivity activity;

	private Button buttonPosesNumPlus;
	private Button buttonPosesNumMinus;
	private TextView textPosesNum;

	private Button buttonPoseTimeMinutesPlus;
	private Button buttonPoseTimeMinutesMinus;
	private TextView textPoseTimeMinutes;
	private Button buttonPoseTimeSecondsPlus;
	private Button buttonPoseTimeSecondsMinus;
	private TextView textPoseTimeSeconds;

	private Button buttonTransitionTimePlus;
	private Button buttonTransitionTimeMinus;
	private TextView textTransitionTime;

	private Button buttonStart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_timersettings,
				container, false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		activity = (MainActivity) getActivity();
		buttonPosesNumPlus = (Button) rootView
				.findViewById(R.id.button_posesNumPlus);
		buttonPosesNumMinus = (Button) rootView
				.findViewById(R.id.button_posesNumMinus);
		textPosesNum = (TextView) rootView.findViewById(R.id.text_posesNum);
		buttonPoseTimeMinutesPlus = (Button) rootView
				.findViewById(R.id.button_poseTimeMinutesPlus);
		buttonPoseTimeMinutesMinus = (Button) rootView
				.findViewById(R.id.button_poseTimeMinutesMinus);
		textPoseTimeMinutes = (TextView) rootView
				.findViewById(R.id.text_poseTimeMinutes);
		buttonPoseTimeSecondsPlus = (Button) rootView
				.findViewById(R.id.button_poseTimeSecondsPlus);
		buttonPoseTimeSecondsMinus = (Button) rootView
				.findViewById(R.id.button_poseTimeSecondsMinus);
		textPoseTimeSeconds = (TextView) rootView
				.findViewById(R.id.text_poseTimeSeconds);
		buttonTransitionTimePlus = (Button) rootView
				.findViewById(R.id.button_transitionTimePlus);
		buttonTransitionTimeMinus = (Button) rootView
				.findViewById(R.id.button_transitionTimeMinus);
		textTransitionTime = (TextView) rootView
				.findViewById(R.id.text_transitionTime);
		buttonStart = (Button) rootView.findViewById(R.id.button_gotoTimer);

		// handle navigation
		NavigationInfo.fragment = new MainFragment();
		NavigationInfo.tag = MainFragment.TAG;

		// customize menu drawer
		activity.mActiveViewId = activity.textTimer.getId();
		activity.selectMenuDrawerItem(activity.textTimer);

		// customize activity
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// customize actionbar
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setTitle("Timer Settings");
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);

		// add action listeners
		buttonPosesNumPlus.setOnClickListener(this);
		buttonPosesNumMinus.setOnClickListener(this);
		buttonPoseTimeMinutesPlus.setOnClickListener(this);
		buttonPoseTimeMinutesMinus.setOnClickListener(this);
		buttonPoseTimeSecondsPlus.setOnClickListener(this);
		buttonPoseTimeSecondsMinus.setOnClickListener(this);
		buttonTransitionTimePlus.setOnClickListener(this);
		buttonTransitionTimeMinus.setOnClickListener(this);
		buttonStart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_posesNumPlus:
			onPickerButton(textPosesNum, true);
			break;

		case R.id.button_posesNumMinus:
			onPickerButton(textPosesNum, false);
			break;

		case R.id.button_poseTimeMinutesPlus:
			onPickerButton(textPoseTimeMinutes, true);
			break;

		case R.id.button_poseTimeMinutesMinus:
			onPickerButton(textPoseTimeMinutes, false);
			break;

		case R.id.button_poseTimeSecondsPlus:
			onButtonPoseTimeSeconds(true);
			break;

		case R.id.button_poseTimeSecondsMinus:
			onButtonPoseTimeSeconds(false);
			break;

		case R.id.button_transitionTimePlus:
			onPickerButton(textTransitionTime, true);
			break;

		case R.id.button_transitionTimeMinus:
			onPickerButton(textTransitionTime, false);
			break;

		case R.id.button_gotoTimer:
			onStartTimer();
			break;

		default:
			break;
		}
	}

	private void onPickerButton(TextView textView, boolean increase) {
		int num = getNumFromTextView(textView);

		if (increase) {
			num++;
		} else {
			if (num != 0) {
				num--;
			}
		}

		textView.setText("" + num);
	}

	private void onButtonPoseTimeSeconds(boolean increase) {
		int seconds = getNumFromTextView(textPoseTimeSeconds);

		if (increase) {
			seconds++;
			if (seconds >= 60) {
				seconds = 0;
				int minutes = getNumFromTextView(textPoseTimeMinutes) + 1;
				textPoseTimeMinutes.setText("" + minutes);
			}
			textPoseTimeSeconds.setText("" + seconds);
		} else {
			seconds--;
			int minutes = getNumFromTextView(textPoseTimeMinutes);
			if (seconds < 0 && minutes != 0) {
				seconds = 59;
				minutes--;
				textPoseTimeMinutes.setText("" + minutes);
			} else if (seconds < 0) {
				seconds = 0;
			}
			textPoseTimeSeconds.setText("" + seconds);
		}
	}
	
	private int getNumFromTextView(TextView textView) {
		// get num and convert it or initial it to 0
		String strNum = textView.getText().toString();
		int num;
		try {
			num = Integer.parseInt(strNum);
		} catch (NumberFormatException e) {
			num = 0;
		}
		return num;
	}

	private void onStartTimer() {
		String strPosesNum = textPosesNum.getText().toString();
		String strPoseTimeMinutes = textPoseTimeMinutes.getText().toString();
		String strPoseTimeSeconds = textPoseTimeSeconds.getText().toString();
		String strTransitionTime = textTransitionTime.getText().toString();

		// init values if not
		if (strPoseTimeMinutes.equals("mm")) {
			strPoseTimeMinutes = "0";
			textPoseTimeMinutes.setText(strPoseTimeMinutes);
		}
		if (strPoseTimeSeconds.equals("ss")) {
			strPoseTimeSeconds = "0";
			textPoseTimeSeconds.setText(strPoseTimeSeconds);
		}
		if (strTransitionTime.equals("ss")) {
			strTransitionTime = "0";
			textTransitionTime.setText(strTransitionTime);
		}

		// check values
		if (strPosesNum.equals("0")) {
			Toast.makeText(activity, "Set poses number", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (strPoseTimeMinutes.equals("0")) {
			if (strPoseTimeSeconds.equals("0")) {
				// invalid
				Toast.makeText(activity, "Set time for every pose",
						Toast.LENGTH_SHORT).show();
				return;
			}
		} else if (strPoseTimeSeconds.equals("0")) {
			if (strPoseTimeMinutes.equals("0")) {
				// invalid
				Toast.makeText(activity, "Set time for every pose",
						Toast.LENGTH_SHORT).show();
				return;
			}
		} else if (strPoseTimeMinutes.equals("0")
				&& strPoseTimeSeconds.equals("0")) {
			Toast.makeText(activity, "Set time for every pose",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// here all values are valid
		int posesNum = Integer.parseInt(strPosesNum);
		long poseTime = ((Long.parseLong(strPoseTimeMinutes) * 60) + Long
				.parseLong(strPoseTimeSeconds)) * 1000;
		long transitionTime = Long.parseLong(strTransitionTime) * 1000;

		// pass values
		Bundle bundle = new Bundle();
		bundle.putInt(TimerFragment.KEY_POSES_NUM, posesNum);
		bundle.putLong(TimerFragment.KEY_POSE_TIME, poseTime);
		bundle.putLong(TimerFragment.KEY_TRANSITION_TIME, transitionTime);

		// start TimerFragment
		TimerFragment fragment = new TimerFragment();
		FragmentUtil.gotoFragment(activity, bundle, fragment, TimerFragment.TAG);
	}

}