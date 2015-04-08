package com.socyle.yogayoga;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.socyle.yogayoga.database.ProgramDAO;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.models.Program;

public class ProgramSettingsFragment extends Fragment implements
		OnClickListener {
	public static final String TAG = "program settings fragmnet";
	public static boolean IS_OK = false;
	public static Bundle OK_BUNDLE;
	
	private Bundle bundle;
	private Program program;
	private MainActivity activity;

	private Button buttonTransitionTimePlus;
	private Button buttonTransitionTimeMinus;
	private TextView textTransitionTime;

	private Button buttonOk;
	private Button buttonCancel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_programsettings,
				container, false);
		initComponents(rootView);
		return rootView;
	}

	private void initComponents(View rootView) {
		bundle = getArguments();
		program = (Program) bundle.getSerializable(ProgramFragment.KEY_PROGRAM);
		activity = (MainActivity) getActivity();

		buttonTransitionTimePlus = (Button) rootView
				.findViewById(R.id.button_programSettings_transitionTimePlus);
		buttonTransitionTimeMinus = (Button) rootView
				.findViewById(R.id.button_programSettings_transitionTimeMinus);
		textTransitionTime = (TextView) rootView
				.findViewById(R.id.text_programSettings_transitionTime);
		buttonOk = (Button) rootView
				.findViewById(R.id.button_programSettings_ok);
		buttonCancel = (Button) rootView
				.findViewById(R.id.button_programSettings_cancel);

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
		actionBar.setTitle("Program Settings");
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);

		// set onClick listeners
		buttonTransitionTimePlus.setOnClickListener(this);
		buttonTransitionTimeMinus.setOnClickListener(this);
		buttonOk.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
		
		// initialize values
		int transitionTime = program.getTransitionTime();
		if (transitionTime != 0) {
			textTransitionTime.setText("" + transitionTime);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_programSettings_transitionTimePlus:
			onPickerButton(textTransitionTime, true);
			break;

		case R.id.button_programSettings_transitionTimeMinus:
			onPickerButton(textTransitionTime, false);
			break;

		case R.id.button_programSettings_ok:
			onOk();
			break;

		case R.id.button_programSettings_cancel:
			onCancel();
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

	private void onOk() {
		int transitionTime = getNumFromTextView(textTransitionTime);
		program.setTransitionTime(transitionTime);

		// edit program in the DB
		ProgramDAO dao = new ProgramDAO(activity);
		dao.open();
		dao.update(program);
		dao.close();

		// pass values and get back to ProgramFragment
		bundle.putSerializable(ProgramFragment.KEY_PROGRAM, program);
		
		IS_OK = true;
		OK_BUNDLE = bundle;
		
		activity.onBackPressed();
	}

	private void onCancel() {
		IS_OK = false;
		activity.onBackPressed();
	}
}
