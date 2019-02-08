package com.otipass.adt67;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("NewApi")
public class MenuProviderFragment extends Fragment implements OnClickListener{


	private Button btn_entry, btn_return;
	private Fragment fragment;
	private FragmentManager fragmentManager;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_menu_provider, container, false);

		btn_entry = (Button) rootView.findViewById(R.id.btn_entry);
		btn_entry.setOnClickListener(this);
		btn_return = (Button) rootView.findViewById(R.id.btn_return);
		btn_return.setOnClickListener(this);

		fragmentManager = getFragmentManager();

		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_entry.equals(v)) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor edt = pref.edit();
			edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_ENTRY);
			edt.commit();
			fragment = new ScanFragment();
			if (fragment != null) {

				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.scan_pass));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}
		}
		if (btn_return.equals(v)) {
			fragment = new BaseFragment();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();
		}
	}
}
