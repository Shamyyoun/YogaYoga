package com.socyle.yogayoga.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.socyle.yogayoga.R;

public class FragmentUtil {
	public static void gotoFragment(ActionBarActivity activity, Bundle bundle, Fragment fragment, String tag) {
		gotoTheFragment(activity, bundle, fragment, tag);
	}
	
	public static void gotoFragment(ActionBarActivity activity, Fragment fragment, String tag) {
		gotoTheFragment(activity, null, fragment, tag);
	}
	
	private static void gotoTheFragment(ActionBarActivity activity, Bundle bundle, Fragment fragment, String tag) {
		if (bundle != null) {
			fragment.setArguments(bundle);
		}

		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.replace(R.id.container, fragment, tag);
		ft.commit();
	}
}
