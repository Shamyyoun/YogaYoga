package com.socyle.yogayoga;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.socyle.yogayoga.customviews.ProgramProgressView;
import com.socyle.yogayoga.models.Program;
import com.socyle.yogayoga.models.ProgramExercise;
import com.socyle.yogayoga.utils.AnimationUtil;

public class ProgramMainViewFragment extends Fragment {
	public static final String TAG = "program main view fragmnet";

	private MainActivity activity;
	private Bundle bundle;
	private Program program;
	private ProgramExercise currentExercise;

	private ImageView imageNextExercise;
	private ProgramProgressView programProgressView;
	private ImageButton buttonStart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_programmainview,
				container, false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		activity = (MainActivity) getActivity();
		bundle = getArguments();
		program = (Program) bundle.getSerializable(ProgramFragment.KEY_PROGRAM);
		currentExercise = program.getNextExercise();

		imageNextExercise = (ImageView) rootView.findViewById(R.id.image_nextExercise);
		programProgressView = (ProgramProgressView) rootView
				.findViewById(R.id.progressView_program);
		buttonStart = (ImageButton) rootView
				.findViewById(R.id.button_startProgram);

		// ----- set initial values -----
		if (currentExercise == null) {
			if (program.getExercises().size() == 0) {
				AnimationUtil.setAlpha(imageNextExercise, 1);
			} else {
				ProgramExercise exercise = program.getExercises().get(program.getExercises().size()-1);
				imageNextExercise.setImageResource(exercise.getSteps().get(0).getImageId());
			}
		} else {
			imageNextExercise.setImageResource(currentExercise.getSteps().get(0).getImageId());
		}
		programProgressView.setProgress(program.getProgress());

		// set onClick listeners
		buttonStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (program.getExercises().size() <= 0) {
					Toast.makeText(activity, "No poses in the program!",
							Toast.LENGTH_SHORT).show();
				} else if(currentExercise == null){
					Toast.makeText(activity, "You finished this program",
							Toast.LENGTH_SHORT).show();
				} else {
					// add runningProgram fragment
					bundle.putSerializable(ProgramFragment.KEY_PROGRAM, program);
					RunningProgramFragment fragment = new RunningProgramFragment();
					fragment.setArguments(bundle);
					FragmentTransaction ft = activity
							.getSupportFragmentManager().beginTransaction();
					ft.replace(R.id.program_container, fragment);
					ft.commit();
				}
			}
		});
	}
}
