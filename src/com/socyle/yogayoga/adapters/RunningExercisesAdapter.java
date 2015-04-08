package com.socyle.yogayoga.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.socyle.yogayoga.R;
import com.socyle.yogayoga.models.ProgramExercise;

public class RunningExercisesAdapter extends ArrayAdapter<ProgramExercise> {
	Context context;
	int layoutResourceId;
	List<ProgramExercise> data = null;

	public RunningExercisesAdapter(Context context, int layoutResourceId,
			List<ProgramExercise> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.textExerciseName = (TextView) row.findViewById(R.id.text_runningexercise_name);
			holder.imageExerciseIcon = (ImageView) row.findViewById(R.id.image_runningexercise_icon);

			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}

		ProgramExercise exercise = data.get(position);
		
		holder.textExerciseName.setText((position+1) + ". " + exercise.getName());
		holder.imageExerciseIcon.setImageResource(exercise.getIconId());

		return row;
	}

	static class ViewHolder {
		TextView textExerciseName;
		ImageView imageExerciseIcon;
	}
}
