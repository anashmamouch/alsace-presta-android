package com.otipass.tools;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.otipass.adt67.R;
import com.otipass.customchoicelist.CheckableLinearLayout;

public class Custom_gridView extends ArrayAdapter<String> {

	private final Context context;
	private final ArrayList<String> itemsArrayList;
	private View rowView;
	private int resource;


	public Custom_gridView(Context context, ArrayList<String> itemsArrayList, int resource) {

		super(context, R.layout.grid_single_image, itemsArrayList);

		this.context = context;
		this.itemsArrayList = itemsArrayList;
		this.resource = resource;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(resource, parent, false);
		TextView nameView = (TextView) rowView.findViewById(R.id.grid_text);
		nameView.setText(itemsArrayList.get(position));

		return rowView;
	}

	public String getPackageName(int position, ArrayList<String> itemsArrayList){
		return itemsArrayList.get(position);
	}


}
