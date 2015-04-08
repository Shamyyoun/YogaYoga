package com.socyle.yogayoga;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

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

import com.socyle.yogayoga.customviews.TimerView;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.utils.FragmentUtil;
import com.socyle.yogayoga.utils.TimeUtil;

public class TimerFragment extends Fragment implements OnClickListener {
	public static final String TAG = "timer fragmnet";
	public static final String KEY_POSES_NUM = "poses num";
	public static final String KEY_POSE_TIME = "pose time";
	public static final String KEY_TRANSITION_TIME = "transition time";

	private MainActivity activity;

	private int posesNum;
	private long poseTime;
	private long transitionTime;

	private TextView textTime;
	private TextView textPoseNum;
	private TextView textTotalTime;

	private Button buttonReset;
	private Button buttonStart;
	private Button buttonSettings;

	private Timer timer;

	private long totalTime;
	private long currentTotalTime;
	private long currentPoseTime;
	private int currentPoseNum;
	private long currentTransitionTime;
	private boolean pose;
	private boolean timerRunning;
	private boolean paused;
	private boolean temp = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_timer, container,
				false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		Bundle bundle = getArguments();
		posesNum = bundle.getInt(KEY_POSES_NUM);
		poseTime = bundle.getLong(KEY_POSE_TIME);
		transitionTime = bundle.getLong(KEY_TRANSITION_TIME);

		activity = (MainActivity) getActivity();
		textTime = (TextView) rootView.findViewById(R.id.text_time);
		textPoseNum = (TextView) rootView.findViewById(R.id.text_poseNum_timer);
		textTotalTime = (TextView) rootView.findViewById(R.id.text_totalTime);
		buttonReset = (Button) rootView.findViewById(R.id.button_reset);
		buttonStart = (Button) rootView.findViewById(R.id.button_startTimer);
		buttonSettings = (Button) rootView
				.findViewById(R.id.button_settings_timer);

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
		actionBar.setTitle("Timer");
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);

		// set initial values
		textTime.setText(TimeUtil.convertTime("mm:ss", poseTime));
		textPoseNum.setText("1/" + posesNum);
		totalTime = (poseTime * posesNum) + ((posesNum - 1) * transitionTime);
		resetTimerValues();

		// add action listeners
		buttonReset.setOnClickListener(this);
		buttonStart.setOnClickListener(this);
		buttonSettings.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_reset:
			onResetTimer();
			break;

		case R.id.button_startTimer:
			onStartTimer();
			break;

		case R.id.button_settings_timer:
			onSettings();
			break;

		default:
			break;
		}
	}

	private void onResetTimer() {
		if (timerRunning) {
			timer.cancel();
		}
		resetTimerValues();
		onStartTimer();
	}

	private void onStartTimer() {
		if (paused) {
			// run
			timerRunning = true;
			paused = false;
			buttonStart.setText("Pause");
			timer = new Timer();

			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// check if pose or transition
					if (pose) {
						makeChangesToUi(TimeUtil.convertTime("mm:ss",
								currentPoseTime), "" + currentPoseNum, TimeUtil
								.convertTime("HH:mm:ss", currentTotalTime));

						currentTotalTime += 1000;
						currentPoseTime -= 1000;

						// check if end pose
						if (currentPoseTime <= 0) {
							// check if there is transition or not
							if (transitionTime == 0) {
								// check if end timer
								if (currentPoseNum == posesNum) {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

									// end timer
									timerRunning = false;
									timer.cancel();
									currentPoseTime = poseTime;
									currentPoseNum = posesNum;
									makeChangesToUi(TimeUtil.convertTime(
											"mm:ss", 0), "" + posesNum,
											TimeUtil.convertTime("HH:mm:ss",
													totalTime));
									resetTimerValues();
									activity.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											buttonStart.setText("Start");
										}
									});
								} else {
									// there is no transition time, so start the
									// next pose
									currentPoseTime = poseTime;
									currentPoseNum++;
								}
							} else {
								// end pose and start transition
								currentTransitionTime = transitionTime;
								pose = false;
							}
						}
					}

					else {
						// check if end timer or start transition
						if ((currentPoseNum + 1) > posesNum) {
							// end timer
							timerRunning = false;
							timer.cancel();
							currentPoseTime = poseTime;
							currentPoseNum = posesNum;
							makeChangesToUi(TimeUtil.convertTime("mm:ss", 0),
									"" + posesNum,
									TimeUtil.convertTime("HH:mm:ss", totalTime));
							resetTimerValues();
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									buttonStart.setText("Start");
								}
							});
						} else {
							makeChangesToUi(TimeUtil.convertTime("ss",
									currentTransitionTime),
									"" + currentPoseNum, TimeUtil.convertTime(
											"HH:mm:ss", currentTotalTime));

							currentTotalTime += 1000;
							currentTransitionTime -= 1000;

							// check if end transition
							if (currentTransitionTime <= 0) {
								// end transition and start pose
								currentPoseTime = poseTime;
								currentPoseNum++;
								pose = true;
							}
						}

					}
				}

				private void makeChangesToUi(final String time,
						final String poseNum, final String totalTime) {
					final Semaphore semaphore = new Semaphore(0);
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							textTime.setText(time);
							textPoseNum.setText(poseNum + "/" + posesNum);
							textTotalTime.setText(totalTime);

							semaphore.release();

						}
					};

					activity.runOnUiThread(runnable);
					try {
						semaphore.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}, 0, 1000);

		} else {
			// pause
			timerRunning = false;
			paused = true;
			buttonStart.setText("Start");
			timer.cancel();
		}

	}

	private void onSettings() {
		// cancel timer if running
		if (timerRunning) {
			timer.cancel();
		}

		// start TimerSettingsFragment
		FragmentUtil.gotoFragment(activity, new TimerSettingsFragment(), null);
	}

	private void resetTimerValues() {
		timerRunning = false;
		paused = true;
		pose = true;
		currentPoseTime = poseTime;
		currentPoseNum = 1;
		currentTotalTime = 0;
	}

	@Override
	public void onPause() {
		if (timerRunning) {
			timer.cancel();
		}
		super.onPause();
	}

}
