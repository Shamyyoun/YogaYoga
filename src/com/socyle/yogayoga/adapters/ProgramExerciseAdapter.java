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

public class ProgramExerciseAdapter extends ArrayAdapter<ProgramExercise> {
	private Context context;
	private int layoutResourceId;
	private List<ProgramExercise> exercises;
	

	public ProgramExerciseAdapter(Context context, int layoutResourceId,
			List<ProgramExercise> exercises) {
		super(context, layoutResourceId, exercises);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.exercises = exercises;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.textExerciseNum = (TextView) row
					.findViewById(R.id.text_exerciseNum);
			holder.imageExerciseIcon = (ImageView) row
					.findViewById(R.id.image_exerciseIcon);
			holder.textExerciseName = (TextView) row
					.findViewById(R.id.text_exerciseName);

			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}

		ProgramExercise exercise = exercises.get(position);

		holder.textExerciseNum.setText((position + 1) + ".");
		holder.imageExerciseIcon.setImageResource(exercise.getIconId());
		holder.textExerciseName.setText(exercise.getName());

		return row;
	}

	static class ViewHolder {
		TextView textExerciseNum;
		ImageView imageExerciseIcon;
		TextView textExerciseName;
	}
}
