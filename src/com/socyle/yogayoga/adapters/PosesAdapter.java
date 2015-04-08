package com.socyle.yogayoga.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.socyle.yogayoga.R;
import com.socyle.yogayoga.models.Exercise;

public class PosesAdapter extends BaseAdapter {
	private Context context;
	private final List<Exercise> data;

	public PosesAdapter(Context context, List<Exercise> data) {
		this.context = context;
		this.data = data;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {
			
			// get view from layout
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.grid_poses_row, null);

			// set values
			Exercise exercise = data.get(position);
			ImageView buttonPose = (ImageView) gridView
					.findViewById(R.id.button_pose);
			buttonPose.setImageResource(exercise.getIconId());

		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
