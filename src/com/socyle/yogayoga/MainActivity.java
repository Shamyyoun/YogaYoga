package com.socyle.yogayoga;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.socyle.yogayoga.models.Exercises;
import com.socyle.yogayoga.models.NavigationInfo;
import com.socyle.yogayoga.utils.FragmentUtil;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	public static final String KEY_VIDEO_FRAGMENT = "video fragment";
	private static final String STATE_MENUDRAWER = "net.simonvt.menudrawer.samples.WindowSample.menuDrawer";
	private static final String STATE_ACTIVE_VIEW_ID = "net.simonvt.menudrawer.samples.WindowSample.activeViewId";

	private MenuDrawer mMenuDrawer;
	public int mActiveViewId;

	private ImageView imageLoading;
	private ImageButton buttonHome;
	public TextView textHome;
	public TextView textPoses;
	public TextView textProgram;
	public TextView textMusic;
	public TextView textTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Exercises.addAll(this);

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND,
				Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(R.layout.menu_drawer);
		getSupportActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			FragmentUtil.gotoFragment(this, new MainFragment(), MainFragment.TAG);
		} else {
			mActiveViewId = savedInstanceState.getInt(STATE_ACTIVE_VIEW_ID);
		}

		initComponents();
	}

	private void initComponents() {
		imageLoading = (ImageView) findViewById(R.id.image_loading_menu);
		buttonHome = (ImageButton) findViewById(R.id.button_menuHome);
		textHome = (TextView) findViewById(R.id.text_home);
		textPoses = (TextView) findViewById(R.id.text_poses);
		textProgram = (TextView) findViewById(R.id.text_program);
		textMusic = (TextView) findViewById(R.id.text_music);
		textTimer = (TextView) findViewById(R.id.text_timer);

		// set active item
		mActiveViewId = textHome.getId();
		selectMenuDrawerItem(textHome);

		// add animation
		Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.loading_animation);
		imageLoading.startAnimation(anim);

		// set onClick listeners
		buttonHome.setOnClickListener(this);
		textHome.setOnClickListener(this);
		textPoses.setOnClickListener(this);
		textProgram.setOnClickListener(this);
		textMusic.setOnClickListener(this);
		textTimer.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_menuHome:
		case R.id.text_home:
		case R.id.text_poses:
		case R.id.text_program:
		case R.id.text_music:
		case R.id.text_timer:
			onMenuDrawerItem(v);
			break;

		default:
			break;
		}
	}

	private void onMenuDrawerItem(View view) {
		mMenuDrawer.setActiveView(view);
		mMenuDrawer.closeMenu();
		mActiveViewId = view.getId();

		Fragment fragment = new MainFragment();
		String tag = MainFragment.TAG;
		switch (view.getId()) {
		case R.id.button_menuHome:
		case R.id.text_home:
			fragment = new MainFragment();
			tag = MainFragment.TAG;
			selectMenuDrawerItem(textHome);
			break;

		case R.id.text_poses:
			fragment = new PosesFragment();
			tag = PosesFragment.TAG;
			selectMenuDrawerItem(textPoses);
			break;

		case R.id.text_program:
			fragment = new ProgramFragment();
			tag = ProgramFragment.TAG;
			selectMenuDrawerItem(textProgram);
			break;

		case R.id.text_music:
			// /////////
			break;

		case R.id.text_timer:
			fragment = new TimerSettingsFragment();
			tag = TimerSettingsFragment.TAG;
			selectMenuDrawerItem(textTimer);
			break;

		default:
			break;

		}
		Fragment myFragment = getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (myFragment == null || !myFragment.isVisible()) {
			FragmentUtil.gotoFragment(this, fragment, tag);
		}
	}

	public void selectMenuDrawerItem(View view) {
		// make all none selected first
		textHome.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		textPoses.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		textProgram.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		textMusic.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		textTimer.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));

		view.setBackgroundColor(getResources().getColor(
				R.color.menudrawer_selecteditem));
	}

	@Override
	protected void onRestoreInstanceState(Bundle inState) {
		super.onRestoreInstanceState(inState);
		mMenuDrawer.restoreState(inState.getParcelable(STATE_MENUDRAWER));
		int acitveViewId = inState.getInt(STATE_ACTIVE_VIEW_ID);
		View view = findViewById(acitveViewId);
		selectMenuDrawerItem(view);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(STATE_MENUDRAWER, mMenuDrawer.saveState());
		outState.putInt(STATE_ACTIVE_VIEW_ID, mActiveViewId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mMenuDrawer.toggleMenu();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// check if menu drawer is opened
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}

		// goto specific fragment
		if (NavigationInfo.fragment != null) {
			FragmentUtil.gotoFragment(this, NavigationInfo.fragment, NavigationInfo.tag);
		} else {
			super.onBackPressed();
		}
	}
}
