package com.socyle.yogayoga;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.socyle.yogayoga.adapters.PosesAdapter;
import com.socyle.yogayoga.database.ExerciseDAO;
import com.socyle.yogayoga.models.Exercise;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.utils.FragmentUtil;

public class PosesFragment extends Fragment {
	public static final String TAG = "poses fragmnet";
	public static final String KEY_EXERCISE = "exercise";
	private MainActivity activity;
	private ExerciseDAO exerciseDAO;
	private GridView gridPoses;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_poses, container,
				false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		activity = (MainActivity) getActivity();
		exerciseDAO = new ExerciseDAO(activity);
		gridPoses = (GridView) rootView.findViewById(R.id.grid_poses);

		// handle navigation
		NavigationInfo.fragment = new MainFragment();
		NavigationInfo.tag = MainFragment.TAG;

		// customize menu drawer
		activity.mActiveViewId = activity.textPoses.getId();
		activity.selectMenuDrawerItem(activity.textPoses);

		// customize activity
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// customize actionbar
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setTitle("All Poses");
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);

		// load all exercises from database to the listview
		exerciseDAO.open();
		final PosesAdapter posesAdapter = new PosesAdapter(activity,
				exerciseDAO.getAll());
		gridPoses.setAdapter(posesAdapter);

		gridPoses.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// get selected exercise
				Exercise exercise = (Exercise) posesAdapter.getItem(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable(KEY_EXERCISE, exercise);

				// open exercises fragment
				ExerciseFragment fragment = new ExerciseFragment();
				FragmentUtil.gotoFragment(activity, bundle, fragment, ExerciseFragment.TAG);
			}
		});

	}

	@Override
	public void onResume() {
		exerciseDAO.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		exerciseDAO.close();
		super.onPause();
	}
}
