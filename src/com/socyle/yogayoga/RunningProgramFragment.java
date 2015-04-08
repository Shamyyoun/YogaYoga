package com.socyle.yogayoga;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socyle.yogayoga.customviews.TimerView;
import com.socyle.yogayoga.customviews.VideoProgressBarView;
import com.socyle.yogayoga.database.ProgramDAO;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.models.Program;
import com.socyle.yogayoga.models.ProgramExercise;
import com.socyle.yogayoga.utils.AnimationUtil;
import com.socyle.yogayoga.utils.TimeUtil;

public class RunningProgramFragment extends Fragment implements OnClickListener {
	public static final String TAG = "running program fragmnet";

	private MainActivity activity;
	private Program program;
	private ProgramExercise currentExercise;
	private ProgramDAO programDAO;

	private ImageView imageVideo;
	private FrameLayout layoutVideoContainer;
	private LinearLayout layoutVideoControllers;
	private ImageButton buttonPlay;
	private ImageButton buttonNext;
	private ImageButton buttonBack;
	private VideoProgressBarView progressBarVideo;
	private TextView textStepNum;

	private TextView textExerciseTime;
	private TextView textExerciseNum;
	private TextView textTotalTime;
	private Button buttonPauseProgram;
	private Button buttonStopProgram;

	// video player variables
	private int currentFrame = -1;
	private Handler mHandler;
	private Runnable mRunnable;
	private boolean videoPlaying = false;
	private int videoProgress = 0;
	private Animation animFadein;
	private Animation animFadeout;
	private boolean controllersHidden = false;
	private int currentFrameTime = 0;
	private int currentTotalTime = 0;
	private boolean paused = false;
	private int tempCurrentFrame = -1;
	private int tempCurrentFrameTime = 0;
	private int tempCurrentTotalTime = 0;
	private int tempVideoProgress = 0;
	private boolean playNextExercise = false;

	// timer variables
	private int exercisesNum;
	private long exerciseTime;
	private long transitionTime;
	private Timer timerTimer;
	private long programTotalTime;
	private long programCurrentTotalTime;
	private long currentExerciseTime;
	private int currentExerciseNum;
	private long currentTransitionTime;
	private boolean pose;
	private boolean timerPlaying;
	private boolean timerPaused;
	private boolean temp = false;

	private Timer timerTotalTime;
	private boolean timerTotalTimePlaying = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_runningprogram,
				container, false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		Bundle bundle = getArguments();
		program = (Program) bundle.getSerializable(ProgramFragment.KEY_PROGRAM);
		activity = (MainActivity) getActivity();
		currentExercise = program.getNextExercise();
		programDAO = new ProgramDAO(activity);

		imageVideo = (ImageView) rootView
				.findViewById(R.id.image_program_videoFrame);
		layoutVideoContainer = (FrameLayout) rootView
				.findViewById(R.id.layout_program_videoContainer);
		layoutVideoControllers = (LinearLayout) rootView
				.findViewById(R.id.layout_program_videoControllers);
		buttonPlay = (ImageButton) rootView
				.findViewById(R.id.button_program_play);
		buttonNext = (ImageButton) rootView
				.findViewById(R.id.button_program_next);
		buttonBack = (ImageButton) rootView
				.findViewById(R.id.button_program_back);
		progressBarVideo = (VideoProgressBarView) rootView
				.findViewById(R.id.progressBar_program_video);
		textStepNum = (TextView) rootView
				.findViewById(R.id.text_program_stepNum);
		textExerciseTime = (TextView) rootView
				.findViewById(R.id.text_program_exerciseTime);
		textExerciseNum = (TextView) rootView
				.findViewById(R.id.text_program_exercisesNum);
		textTotalTime = (TextView) rootView
				.findViewById(R.id.text_program_totalTime);
		buttonPauseProgram = (Button) rootView
				.findViewById(R.id.button_program_pauseProgram);
		buttonStopProgram = (Button) rootView
				.findViewById(R.id.button_program_stopProgram);

		mHandler = new Handler();
		mRunnable = new MRunnable();
		animFadein = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_in);
		animFadeout = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_out);

		// handle navigation
		NavigationInfo.fragment = new ProgramFragment();
		NavigationInfo.tag = ProgramFragment.TAG;

		// customize menu drawer
		activity.mActiveViewId = activity.textProgram.getId();
		activity.selectMenuDrawerItem(activity.textProgram);

		// customize activity
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// customize actionbar
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);

		// ----- TO SOLVE A BUG ONLY -----
		if (currentExercise == null) {
			currentExercise = program.getExercises().get(0);
		}

		// ----- set initial values ------
		// video player values
		imageVideo.setImageResource(currentExercise.getSteps().get(0)
				.getImageId());
		textStepNum.setText(0 + "/" + (currentExercise.getSteps().size()));

		// timer values
		timerPlaying = false;
		timerPaused = true;
		pose = true;
		exercisesNum = program.getExercises().size();
		currentExerciseNum = program.getNextExercisePosition() + 1;
		exerciseTime = currentExercise.getTotalDuration() * 1000;
		currentExerciseTime = exerciseTime;
		transitionTime = program.getTransitionTime() * 1000;
		programTotalTime = program.getTotalTime() * 1000;
		programCurrentTotalTime = 0;
		textExerciseTime.setText(TimeUtil.convertTime("mm:ss", exerciseTime));
		textExerciseNum.setText("" + currentExerciseNum + "/" + exercisesNum);
		textTotalTime.setText(TimeUtil.convertTime("hh:mm:ss",
				programCurrentTotalTime));

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

		// add onClick listeners
		layoutVideoContainer.setOnClickListener(this);
		buttonPlay.setOnClickListener(this);
		buttonNext.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
		buttonPauseProgram.setOnClickListener(this);
		buttonStopProgram.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_program_videoContainer:
			showControllers(controllersHidden);
			break;

		case R.id.button_program_play:
			onButtonPlay();
			break;

		case R.id.button_program_next:
			onButtonNext();
			break;

		case R.id.button_program_back:
			onButtonBack();
			break;

		case R.id.button_program_pauseProgram:
			break;

		case R.id.button_program_stopProgram:
			break;

		default:
			break;
		}
	}

	private void playTimer() {
		if (timerPaused) {
			// run
			timerPlaying = true;
			timerPaused = false;
			timerTimer = new Timer();
			timerTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// check if pose or transition
					if (pose) {
						if (playNextExercise) {
							System.out.println("PLAAAAAAAAAAAAAAAAAAAAY");
							// end video first if playing
							videoPlaying = false;
							mHandler.removeCallbacksAndMessages(null);
							mHandler.removeCallbacks(mRunnable);
							mHandler.removeMessages(0);
							currentFrame = -1;
							currentFrameTime = 0;
							currentTotalTime = 0;

							// update UI
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									imageVideo.setImageResource(currentExercise
											.getSteps().get(0).getImageId());
									textStepNum.setText(0
											+ "/"
											+ (currentExercise.getSteps()
													.size()));
									buttonPlay
											.setImageResource(R.drawable.ex_but_play);

									videoProgress = 0;
									progressBarVideo.setProgress(videoProgress);
								}
							});
							// play next exercise
							playVideo();
							playNextExercise = false;
						}
						makeChangesToUi(TimeUtil.convertTime("mm:ss",
								currentExerciseTime), "" + currentExerciseNum,
								TimeUtil.convertTime("HH:mm:ss",
										programCurrentTotalTime));

						programCurrentTotalTime += 1000;
						currentExerciseTime -= 1000;

						// check if end exercise
						if (currentExerciseTime <= 0) {
							// set done and edit in the DB
							program.getExercises().get(currentExerciseNum - 1)
									.setDone(true);
							programDAO.open();
							programDAO.updateExerciseInProgram(
									program.getExercises().get(
											currentExerciseNum - 1),
									program.getId());
							programDAO.close();

							// check if there is transition or not
							if (transitionTime == 0) {
								// check if end timer
								if (currentExerciseNum > exercisesNum) {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

									// end timer
									timerPlaying = false;
									timerTimer.cancel();
									currentExerciseTime = exerciseTime;
									currentExerciseNum = exercisesNum;
									makeChangesToUi(TimeUtil.convertTime(
											"mm:ss", 0), "" + exercisesNum,
											TimeUtil.convertTime("HH:mm:ss",
													programCurrentTotalTime));

									// end totalTime timer
									timerTotalTimePlaying = false;
									timerTotalTime.cancel();

									// set some changes to layout
									activity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											layoutVideoContainer
													.setEnabled(false);
											buttonPlay.setEnabled(false);
											buttonNext.setEnabled(false);
											buttonBack.setEnabled(false);
										}
									});

									// update DB
									program.setTotalTime((int) (programCurrentTotalTime / 1000));
									programDAO.open();
									programDAO.update(program);
									programDAO.close();

									// reset values
									timerPlaying = false;
									timerPaused = true;
									pose = true;
									currentExerciseTime = exerciseTime;
									currentExerciseNum = 1;
									programCurrentTotalTime = 0;
								} else {
									// check if end timer or not
									if (currentExerciseNum >= exercisesNum) {
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}

										// end timer
										timerPlaying = false;
										timerTimer.cancel();

										program.setTotalTime((int) (programTotalTime / 1000));
										programDAO.open();
										programDAO.update(program);
										programDAO.close();

										currentExerciseTime = exerciseTime;
										currentExerciseNum = exercisesNum;
										makeChangesToUi(
												TimeUtil.convertTime("mm:ss", 0),
												"" + exercisesNum,
												TimeUtil.convertTime(
														"HH:mm:ss",
														programCurrentTotalTime));

										// set some changes to layout
										activity.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												layoutVideoContainer
														.setEnabled(false);
												buttonPlay.setEnabled(false);
												buttonNext.setEnabled(false);
												buttonBack.setEnabled(false);
											}
										});

										// update DB
										program.setTotalTime((int) (programCurrentTotalTime / 1000));
										programDAO.open();
										programDAO.update(program);
										programDAO.close();

										// reset values
										timerPlaying = false;
										timerPaused = true;
										pose = true;
										currentExerciseTime = exerciseTime;
										currentExerciseNum = 1;
										programCurrentTotalTime = 0;
									} else {
										// there is no transition time, so start
										// the next exercise

										program.setTotalTime((int) (programTotalTime / 1000));
										currentExerciseNum++;
										currentExerciseTime = program
												.getNextExercise()
												.getTotalDuration() * 1000;

										// edit program in DB
										programDAO.open();
										programDAO.update(program);
										programDAO.close();

										playNextExercise = true;
									}

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
						if (currentExerciseNum >= exercisesNum) {
							// end timer
							timerPlaying = false;
							timerTimer.cancel();

							program.setTotalTime((int) (programTotalTime / 1000));
							programDAO.open();
							programDAO.update(program);
							programDAO.close();

							currentExerciseTime = exerciseTime;
							currentExerciseNum = exercisesNum;
							makeChangesToUi(TimeUtil.convertTime("mm:ss", 0),
									"" + exercisesNum, TimeUtil
											.convertTime("HH:mm:ss",
													programCurrentTotalTime));

							// set some changes to layout
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									layoutVideoContainer.setEnabled(false);
									buttonPlay.setEnabled(false);
									buttonNext.setEnabled(false);
									buttonBack.setEnabled(false);
								}
							});

							// update DB
							program.setTotalTime((int) (programCurrentTotalTime / 1000));
							programDAO.open();
							programDAO.update(program);
							programDAO.close();

							// reset values
							timerPlaying = false;
							timerPaused = true;
							pose = true;
							currentExerciseTime = exerciseTime;
							currentExerciseNum = 1;
							programCurrentTotalTime = 0;
						} else {
							makeChangesToUi(TimeUtil.convertTime("ss",
									currentTransitionTime), ""
									+ currentExerciseNum, TimeUtil.convertTime(
									"HH:mm:ss", programCurrentTotalTime));

							programCurrentTotalTime += 1000;
							currentTransitionTime -= 1000;

							// check if end transition
							if (currentTransitionTime <= 0) {
								// end transition and start exercise
								program.setTotalTime((int) (programTotalTime / 1000));
								currentExerciseNum++;
								currentExerciseTime = program.getNextExercise()
										.getTotalDuration() * 1000;
								pose = true;

								// edit program in DB
								programDAO.open();
								programDAO.update(program);
								programDAO.close();

								playNextExercise = true;
							}
						}

					}
				}

				private void makeChangesToUi(final String exerciseTime,
						final String exerciseNum, final String programTotalTime) {
					final Semaphore semaphore = new Semaphore(0);
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							textExerciseTime.setText(exerciseTime);
							textExerciseNum.setText(exerciseNum + "/"
									+ exercisesNum);
							textTotalTime.setText(programTotalTime);

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
			timerPlaying = false;
			timerPaused = true;
			timerTimer.cancel();
		}

	}

	private void onButtonPlay() {
		if (!timerTotalTimePlaying) {
			playTotalTimeTimer();
		}
		if (videoPlaying) {
			pauseVideo();

			// pause timer
			timerTimer.cancel();
			timerPlaying = false;
			timerPaused = true;
		} else {
			playTimer();
			if (timerPaused) {
				int temp = currentFrame + 1;
				if (temp > currentExercise.getSteps().size() - 1) {
					endVideo();
				} else {
					gotoNextFrame();
					buttonPlay.setImageResource(R.drawable.ex_but_pause);
					playVideo();
				}
				timerPaused = false;
			} else {
				buttonPlay.setImageResource(R.drawable.ex_but_pause);
				playVideo();
			}
		}
	}

	private void onButtonNext() {
		if (controllersHidden) {
			showControllers(true);
		}
		if (videoPlaying) {
			pauseVideo();
			tempCurrentFrame = currentFrame;
			tempCurrentFrameTime = currentFrameTime;
			tempCurrentTotalTime = currentTotalTime;
		}

		if (tempCurrentFrame >= currentExercise.getSteps().size() - 1) {
			// goto start of the video
			tempCurrentFrame = -1;
			tempCurrentFrameTime = currentExercise.getSteps().get(0)
					.getDuration();
			imageVideo.setImageResource(currentExercise.getSteps().get(0)
					.getImageId());
			tempCurrentTotalTime = 0;
		} else {
			tempCurrentFrame++;
			tempCurrentFrameTime = currentExercise.getSteps()
					.get(tempCurrentFrame).getDuration();
			imageVideo.setImageResource(currentExercise.getSteps()
					.get(tempCurrentFrame).getImageId());
			tempCurrentTotalTime = currentExercise
					.getCurrentTime(tempCurrentFrame);
			timerPaused = true;
		}

		textStepNum.setText((tempCurrentFrame + 1) + "/"
				+ (currentExercise.getSteps().size()));
		tempVideoProgress = calcProgress(tempCurrentFrame);
		progressBarVideo.setProgress(tempVideoProgress);
	}

	private void onButtonBack() {
		if (controllersHidden) {
			showControllers(true);
		}
		if (videoPlaying) {
			pauseVideo();
			tempCurrentFrame = currentFrame;
			tempCurrentFrameTime = currentFrameTime;
			tempCurrentTotalTime = currentTotalTime;
		}

		System.out.println("TEMP CURRENT FRAME: " + tempCurrentFrame);
		System.out.println("CURRENT FRAME: " + currentFrame);

		if (tempCurrentFrame == 0) {
			// goto start of the video
			tempCurrentFrame = -1;
			tempCurrentFrameTime = currentExercise.getSteps().get(0)
					.getDuration();
			imageVideo.setImageResource(currentExercise.getSteps().get(0)
					.getImageId());
			tempCurrentTotalTime = 0;
		} else if (tempCurrentFrame <= -1) {
			// goto end of the video
			tempCurrentFrame = currentExercise.getSteps().size() - 1;
			tempCurrentFrameTime = currentExercise.getSteps()
					.get(tempCurrentFrame).getDuration();
			imageVideo.setImageResource(currentExercise.getSteps()
					.get(tempCurrentFrame).getImageId());
			tempCurrentTotalTime = currentExercise.getTotalDuration();
		} else {
			System.out.println("ELSEEEEE");
			tempCurrentFrame--;
			tempCurrentFrameTime = currentExercise.getSteps()
					.get(tempCurrentFrame).getDuration();
			imageVideo.setImageResource(currentExercise.getSteps()
					.get(tempCurrentFrame).getImageId());
			tempCurrentTotalTime = currentExercise
					.getCurrentTime(tempCurrentFrame);
			timerPaused = true;
		}

		textStepNum.setText((tempCurrentFrame + 1) + "/"
				+ (currentExercise.getSteps().size()));
		tempVideoProgress = calcProgress(tempCurrentFrame);
		progressBarVideo.setProgress(tempVideoProgress);
	}

	private void playVideo() {
		mHandler.post(mRunnable);
	}

	private void pauseVideo() {
		// pause timer
		if (timerPlaying) {
			timerTimer.cancel();
			timerPlaying = false;
		}

		// pause video
		mHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacks(mRunnable);
		mHandler.removeMessages(0);
		videoPlaying = false;

		if (controllersHidden) {
			showControllers(true);
		}

		buttonPlay.setImageResource(R.drawable.ex_but_play);
	}

	private void gotoNextFrame() {
		// move to next frame
		currentFrame++;
		currentFrameTime = 0;
	}

	private boolean shouldEndVideo() {
		if (currentFrame > currentExercise.getSteps().size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	private void endVideo() {
		// end video
		videoPlaying = false;
		mHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacks(mRunnable);
		mHandler.removeMessages(0);
		currentFrame = -1;
		currentFrameTime = 0;
		currentTotalTime = 0;

		// update UI
		imageVideo.setImageResource(currentExercise.getSteps().get(0)
				.getImageId());
		textStepNum.setText(0 + "/" + (currentExercise.getSteps().size()));
		showControllers(true);
		buttonPlay.setImageResource(R.drawable.ex_but_play);

		videoProgress = 0;
		progressBarVideo.setProgress(videoProgress);
	}

	private int calcProgress(int currentFrame) {
		return (int) (((float) (currentFrame + 1) / currentExercise.getSteps()
				.size()) * 100);
	}

	private void playTotalTimeTimer() {
		timerTotalTime = new Timer();
		timerTotalTime.schedule(new TimerTask() {
			@Override
			public void run() {
				timerTotalTimePlaying = true;
				programTotalTime += 1000;
			}
		}, 1000, 1000);
	}

	private void showControllers(final boolean show) {
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

	@Override
	public void onPause() {
		super.onPause();
		if (timerPlaying) {
			timerTimer.cancel();
		}

		if (timerTotalTimePlaying) {
			timerTotalTime.cancel();
		}

		// edit values in the DB
		program.setTotalTime((int) (programTotalTime / 1000));
		programDAO.open();
		programDAO.update(program);
		programDAO.close();
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
				videoPlaying = true;
				if (currentFrame == -1) {
					// start of the video
					currentFrame = 0;
				}
				if (currentFrameTime >= currentExercise.getSteps()
						.get(currentFrame).getDuration() - 1) {

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
							imageVideo.setImageResource(currentExercise
									.getSteps().get(currentFrame).getImageId());
							textStepNum.setText((currentFrame + 1) + "/"
									+ currentExercise.getSteps().size());

							videoProgress = calcProgress(currentFrame);
							progressBarVideo.setProgress(videoProgress);
						}
					});
				}
				mHandler.postDelayed(mRunnable, 1000);
			}
		}
	}
}
