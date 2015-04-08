package com.socyle.yogayoga.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.socyle.yogayoga.R;
import com.socyle.yogayoga.models.Step;

public class ExerciseStepDescAdapter extends ArrayAdapter<Step> {
	Context context;
	int layoutResourceId;
	List<Step> data = null;

	public ExerciseStepDescAdapter(Context context, int layoutResourceId,
			List<Step> data) {
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
			holder.textStepDesc = (TextView) row
					.findViewById(R.id.text_stepDesc);

			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}

		Step step = data.get(position);
		holder.textStepDesc.setText("STEP " + (position+1) + ": "
				+ step.getDesc());

		return row;
	}

	static class ViewHolder {
		TextView textStepDesc;
	}
}
