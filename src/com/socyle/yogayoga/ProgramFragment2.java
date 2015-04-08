package com.socyle.yogayoga;

import java.util.ArrayList;
import java.util.List;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.socyle.yogayoga.adapters.ProgramExerciseAdapter;
import com.socyle.yogayoga.adapters.RunningExercisesAdapter;
import com.socyle.yogayoga.database.ProgramDAO;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.models.Program;
import com.socyle.yogayoga.models.ProgramExercise;
import com.socyle.yogayoga.models.Step;
import com.socyle.yogayoga.utils.AnimationUtil;

public class ProgramFragment2 extends Fragment implements OnClickListener {
	public static final String TAG = "program fragmnet test";

	private MainActivity activity;
	private ProgramDAO programDAO;
	private Program program;

	private FrameLayout layoutList;
	private ListView listProgramExercises;

	private TextView textExerciseTitle;
	private LinearLayout layoutHeader;
	private ImageView imageNextExercise;
	private LinearLayout layoutVideoControllers;
	private ImageButton buttonStart;

	private ListView listRunningExercises;

	private Animation animListExercises;
	private Animation animListExercisesRunning;
	private Animation animHeader;
	private Animation animButtonStart;
	private Animation animFadein;
	private Animation animFadeout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_program2, container,
				false);

		initComponents(rootView);
		listRunningExercises.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				expandExercise(position);
			}
		});
		return rootView;
	}

	private void initComponents(View rootView) {
		activity = (MainActivity) getActivity();
		programDAO = new ProgramDAO(activity);

		layoutList = (FrameLayout) rootView.findViewById(R.id.layout_list);
		listProgramExercises = (ListView) rootView
				.findViewById(R.id.list_programExercises);

		textExerciseTitle = (TextView) rootView
				.findViewById(R.id.text_exerciseTitle);
		layoutHeader = (LinearLayout) rootView
				.findViewById(R.id.layout_programHeader);
		imageNextExercise = (ImageView) rootView
				.findViewById(R.id.image_nextExercise);
		layoutVideoControllers = (LinearLayout) rootView
				.findViewById(R.id.layout_videoControllers);
		buttonStart = (ImageButton) rootView
				.findViewById(R.id.button_startProgram);

		listRunningExercises = new ListView(activity);

		animListExercises = AnimationUtils.loadAnimation(activity,
				R.anim.listexercises_slide_out);
		animListExercisesRunning = AnimationUtils.loadAnimation(activity,
				R.anim.listexercisesrunning_slide_in);
		animHeader = AnimationUtils.loadAnimation(activity,
				R.anim.programheader_slide_out);
		animButtonStart = AnimationUtils.loadAnimation(activity,
				R.anim.buttonstart_rotation);
		animFadein = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_in);
		animFadeout = AnimationUtils.loadAnimation(activity,
				R.anim.videocontrollers_fade_out);

		// check if there is no program to create one
		programDAO.open();
		if (programDAO.getCount() == 0) {
			List<ProgramExercise> list = new ArrayList<ProgramExercise>();
			Program program = new Program(list);
			programDAO.add(program);
		}
		program = programDAO.get(1);
		programDAO.close();

		// handle navigation
		NavigationInfo.fragment = new MainFragment();
		NavigationInfo.tag = MainFragment.TAG;

		// customize menu drawer
		activity.mActiveViewId = activity.textProgram.getId();
		activity.selectMenuDrawerItem(activity.textProgram);

		// customize activity
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// customize actionbar
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setTitle("My Program");
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(R.layout.actionbar_program);
		View actionBarRootView = actionBar.getCustomView();

		// customize components
		FrameLayout.LayoutParams paramsListRunningExercises = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		paramsListRunningExercises.setMargins(0, 10, 0, 10);
		listRunningExercises.setLayoutParams(paramsListRunningExercises);
		listRunningExercises.setDividerHeight(0);
		listRunningExercises.setDivider(null);
		listRunningExercises.setSelector(R.drawable.null_pic);

		AnimationUtil.setAlpha(layoutVideoControllers, 0);
		animFadein.setStartOffset(700);

		// set initials
		ProgramExerciseAdapter programExerciseAdapter = new ProgramExerciseAdapter(
				activity, R.layout.list_programexercises_item,
				program.getExercises());
		listProgramExercises.setAdapter(programExerciseAdapter);

		RunningExercisesAdapter runningExercisesAdapter = new RunningExercisesAdapter(
				activity, R.layout.list_runningexercise_item,
				program.getExercises());
		listRunningExercises.setAdapter(runningExercisesAdapter);

		// add listeners
		buttonStart.setOnClickListener(this);

		animListExercises.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// remove listExercises and add list running exercises
				layoutList.removeAllViews();
				layoutList.addView(listRunningExercises);
				listRunningExercises.startAnimation(animListExercisesRunning);
			}
		});
		animListExercisesRunning.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				expandExercise(0);
			}
		});
		animHeader.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				((ViewGroup) layoutHeader.getParent()).removeView(layoutHeader);
				imageNextExercise.setBackgroundResource(R.drawable.null_pic);
				
				layoutVideoControllers.startAnimation(animFadein);
			}
		});
		animButtonStart.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
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
				animFadein.setStartOffset(0);
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
	}

	private void onButtonStart() {
		// animate views
		listProgramExercises.startAnimation(animListExercises);
		layoutHeader.startAnimation(animHeader);
		buttonStart.startAnimation(animButtonStart);
	}

	@SuppressLint("NewApi")
	private void expandExercise(int index) {
		// ---- collapse other items ----
		for (int i = 0; i < listRunningExercises.getAdapter().getCount(); i++) {
			// first remove steps if exists
			LinearLayout listItemRootView = (LinearLayout) listRunningExercises
					.getAdapter().getView(i,
							listRunningExercises.getChildAt(i),
							listRunningExercises);
			LinearLayout subItemContainer = (LinearLayout) listItemRootView
					.findViewById(R.id.layout_runningExercise_subitemContainer);
			subItemContainer.removeAllViews();

			// second set location of headers
			LinearLayout layoutHeader = (LinearLayout) listItemRootView
					.findViewById(R.id.layout_runningexercise_header);

			LayoutParams layoutParams = new LayoutParams(
					layoutHeader.getWidth(), layoutHeader.getHeight());
			layoutParams.setMargins(layoutHeader.getWidth() / -2, 0, 0, 0);
			layoutHeader.setLayoutParams(layoutParams);
		}

		// ---- expand this item ----
		// set location of this header
		LinearLayout listItemRootView = (LinearLayout) listRunningExercises
				.getAdapter().getView(index,
						listRunningExercises.getChildAt(index),
						listRunningExercises);
		LinearLayout layoutHeader = (LinearLayout) listItemRootView
				.findViewById(R.id.layout_runningexercise_header);
		LayoutParams layoutParams = new LayoutParams(layoutHeader.getWidth(),
				layoutHeader.getHeight());
		layoutParams.setMargins(0, 0, 0, 0);
		layoutHeader.setLayoutParams(layoutParams);

		// create view for sub items
		LinearLayout subItemContainer = (LinearLayout) listItemRootView
				.findViewById(R.id.layout_runningExercise_subitemContainer);
		LayoutTransition transition = new LayoutTransition();
		transition.setDuration(1000);
		subItemContainer.setLayoutTransition(transition);
		subItemContainer.removeAllViews();

		// add sub items to the container
		List<Step> steps = program.getExercises().get(index).getSteps();
		for (int i = 0; i < steps.size(); i++) {
			View subItemRootView = activity.getLayoutInflater().inflate(
					R.layout.list_runningexercise_subitem, null);
			TextView textStepId = (TextView) subItemRootView
					.findViewById(R.id.text_runningexercise_stepId);
			ImageView imageStep = (ImageView) subItemRootView
					.findViewById(R.id.image_runningexercise_step);

			textStepId.setText((i + 1) + ". ");
			imageStep.setImageResource(steps.get(i).getImageId());

			// add it to sub item container
			subItemContainer.addView(subItemRootView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_startProgram:
			onButtonStart();
			break;

		default:
			break;
		}
	}
}
