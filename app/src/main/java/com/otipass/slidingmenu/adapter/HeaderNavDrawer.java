package com.otipass.slidingmenu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.otipass.adt67.R;
import com.otipass.slidingmenu.adapter.NavDrawerListCustomAdapter.RowType;

public class HeaderNavDrawer implements Item {
	private final String         name;
	private int background;

	public HeaderNavDrawer(String name, int background) {
		this.name = name;
		this.background = background;
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.header_nav_drawer, null);
			// Do some initialization
		} else {
			view = convertView;
		}

		TextView text = (TextView) view.findViewById(R.id.separator);
		text.setText(name);
		text.setBackgroundResource(background);

		return view;
	}

}