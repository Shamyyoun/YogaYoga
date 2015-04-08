package com.socyle.yogayoga.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;

public class AnimationUtil {
	public static void setAlpha(View v, float alpha) {
		AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
		animation.setDuration(0);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}
}
