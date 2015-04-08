package com.socyle.yogayoga;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.socyle.yogayoga.adapters.StepAdapter;
import com.socyle.yogayoga.database.ProgramDAO;
import com.socyle.yogayoga.models.Exercise;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.models.Program;
import com.socyle.yogayoga.models.ProgramExercise;

public class ExerciseFragment extends Fragment {
	public static final String TAG = "exercise fragmnet";
	private Exercise exercise;
	private MainActivity activity;
	private ImageButton buttonAddToProgram;
	private ListView listSteps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_exercise, container,
				false);
		initComponents(rootView);

		return rootView;
	}

	private void initComponents(View rootView) {
		Bundle bundle = getArguments();
		exercise = (Exercise) bundle
				.getSerializable(PosesFragment.KEY_EXERCISE);
		activity = (MainActivity) getActivity();
		listSteps = (ListView) rootView.findViewById(R.id.list_steps);

		// handle navigation
		NavigationInfo.fragment = new PosesFragment();
		NavigationInfo.tag = PosesFragment.TAG;

		// customize menu drawer
		activity.mActiveViewId = activity.textPoses.getId();
		activity.selectMenuDrawerItem(activity.textPoses);

		// customize activity
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// customize actionbar
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(R.layout.actionbar_video);
		View actionBarRootView = actionBar.getCustomView();
		buttonAddToProgram = (ImageButton) actionBarRootView
				.findViewById(R.id.button_addToProgram);
		buttonAddToProgram.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onAddToProgram();
			}
		});

		// send exercise to video fragment
		VideoFragment fragment = new VideoFragment();
		fragment.setArguments(bundle);
		// add video fragment
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.replace(R.id.exercise_container, fragment);
		ft.commit();

		StepAdapter adapter = new StepAdapter(activity,
				R.layout.list_steps_item, exercise.getSteps());
		listSteps.setAdapter(adapter);
	}

	private void onAddToProgram() {
		ProgramDAO programDAO = new ProgramDAO(activity);

		// check if there is no program to create one
		programDAO.open();
		if (programDAO.getCount() == 0) {
			List<ProgramExercise> list = new ArrayList<ProgramExercise>();
			Program program = new Program(list);
			programDAO.add(program);
		}
		
		boolean result = programDAO.addExerciseToProgram(exercise, 1);
		programDAO.close();

		if (result) {
			Toast.makeText(activity, "Added to your program", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(activity, "Already exists in your program!",
					Toast.LENGTH_LONG).show();
		}
	}

}
