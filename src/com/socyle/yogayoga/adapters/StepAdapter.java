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
import com.socyle.yogayoga.models.Step;

public class StepAdapter extends ArrayAdapter<Step> {
	Context context;
	int layoutResourceId;
	List<Step> data = null;

	public StepAdapter(Context context, int layoutResourceId,
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
			holder.textStepId = (TextView) row.findViewById(R.id.text_stepId);
			holder.imageStep = (ImageView) row.findViewById(R.id.image_step);

			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}

		Step step = data.get(position);
		holder.textStepId.setText((position+1) + ".");
		holder.imageStep.setImageResource(step.getImageId());

		return row;
	}

	static class ViewHolder {
		TextView textStepId;
		ImageView imageStep;
	}
}
