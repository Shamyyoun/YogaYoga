package com.socyle.yogayoga;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.socyle.yogayoga.adapters.ExerciseStepDescAdapter;
import com.socyle.yogayoga.models.Exercise;

public class StepsDescFragment extends Fragment {
	private Exercise exercise;
	private ActionBarActivity activity;
	private ListView listStepsDesc;
	private ImageButton buttonBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stepsdesc,
				container, false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		exercise = (Exercise) getArguments().getSerializable(PosesFragment.KEY_EXERCISE);
		activity = (ActionBarActivity) getActivity();
		activity.setTitle(exercise.getName() + " / steps");
		listStepsDesc = (ListView) rootView.findViewById(R.id.list_stepsDesc);
		buttonBack = (ImageButton) rootView
				.findViewById(R.id.button_stepsDescBack);

		ExerciseStepDescAdapter adapter = new ExerciseStepDescAdapter(
				getActivity(), R.layout.list_stepsdesc_item,
				exercise.getSteps());
		listStepsDesc.setAdapter(adapter);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// send exercise to video fragment
				Bundle bundle = new Bundle();
				bundle.putSerializable(PosesFragment.KEY_EXERCISE, exercise);
				VideoFragment fragment = new VideoFragment();
				fragment.setArguments(bundle);
				
				// display video fragment
				FragmentTransaction ft = activity.getSupportFragmentManager()
						.beginTransaction();
				ft.setCustomAnimations(R.anim.fragmentvideo_slide_in, R.anim.fragmentdesc_slide_out);
				ft.replace(R.id.exercise_container, fragment);
				ft.commit();
			}
		});
	}
}