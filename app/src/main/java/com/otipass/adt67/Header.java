package com.otipass.adt67;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Header extends Fragment {
	public static final String ARG_HEADER_TITLE = "header_title";
	public static final String QUIT_BUTTON = "Quit button";
	private View rootView;

	public Header() {
		// Empty constructor required for fragment subclasses
	}

	// attach callbacks to the calling activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			//mCancelListener = (OnCancelButtonClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement  OnCancelButtonClickedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String s       = getArguments().getString(ARG_HEADER_TITLE);
		int imgId      = getArguments().getInt(Constants.IMG_HEADER_KEY);

		if (s.length() > 0) {
			rootView = inflater.inflate(R.layout.header, container, false);
			ActionBar bar = getActivity().getActionBar();
			bar.setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_bg));
			TextView tv = (TextView) rootView.findViewById(R.id.header_text);

			tv.setText(s);
		}else {
			rootView = inflater.inflate(R.layout.header2, container, false);
		}

		ImageView img = (ImageView) rootView.findViewById(R.id.header_img);
		if (imgId > 0) {
			img.setBackground(getResources().getDrawable(imgId));
		}else {
			img.setBackground(getResources().getDrawable(R.drawable.carte_picto));
		}
		return rootView;
	}
}
