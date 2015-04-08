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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.socyle.yogayoga.adapters.ProgramExerciseAdapter;
import com.socyle.yogayoga.customviews.ProgramProgressView;
import com.socyle.yogayoga.database.ProgramDAO;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.models.Program;
import com.socyle.yogayoga.models.ProgramExercise;
import com.socyle.yogayoga.utils.FragmentUtil;

public class ProgramFragment extends Fragment {
	public static final String TAG = "program fragmnet";
	public static final String KEY_PROGRAM = "key_program";

	private MainActivity activity;
	private ProgramDAO programDAO;
	private Program program;
	private Bundle bundle;

	public ListView listProgramExercises;
	private Button buttonSettings;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_program, container,
				false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		activity = (MainActivity) getActivity();
		programDAO = new ProgramDAO(activity);
		listProgramExercises = (ListView) rootView
				.findViewById(R.id.list_programExercises);

		// check if there is no program to create one
		programDAO.open();
		if (programDAO.getCount() == 0) {
			List<ProgramExercise> list = new ArrayList<ProgramExercise>();
			Program program = new Program(list);
			programDAO.add(program);
		}
		program = programDAO.get(1);
		programDAO.close();

		// prepare bundle
		bundle = new Bundle();

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
		buttonSettings = (Button) actionBarRootView
				.findViewById(R.id.button_programSettings);

		// ----- set initial values -----
		ProgramExerciseAdapter adapter = new ProgramExerciseAdapter(activity,
				R.layout.list_programexercises_item, program.getExercises());
		listProgramExercises.setAdapter(adapter);

		// set onClick listeners
		buttonSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bundle.putSerializable(KEY_PROGRAM, program);
				FragmentUtil.gotoFragment(activity, bundle,
						new ProgramSettingsFragment(),
						RunningProgramFragment.TAG);
			}
		});

		// add programMainView fragment
		bundle.putSerializable(KEY_PROGRAM, program);
		ProgramMainViewFragment fragment = new ProgramMainViewFragment();
		fragment.setArguments(bundle);
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.replace(R.id.program_container, fragment);
		ft.commit();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (ProgramSettingsFragment.IS_OK) {
			ProgramSettingsFragment.IS_OK = false;
			program = (Program) ProgramSettingsFragment.OK_BUNDLE
					.get(KEY_PROGRAM);
		}
	}
}
