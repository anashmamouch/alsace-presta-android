package com.otipass.adt67;

import com.otipass.adt67.R;

import models.Constants;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmationFragment extends Fragment implements OnClickListener{
	private Button btn_home;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private Bundle bundle;
	private boolean entry_confirmation;
	private TextView confirm_msg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_confirmation, container, false);

		btn_home = (Button) rootView.findViewById(R.id.home);
		btn_home.setOnClickListener(this);
		confirm_msg = (TextView) rootView.findViewById(R.id.confirm_msg);

		bundle = getArguments();
		if (bundle != null) {
			entry_confirmation = bundle.getBoolean(Constants.ENTRY_CONFIRM_KEY);

			if (entry_confirmation) {
				confirm_msg.setText(getString(R.string.save_entry_ok));
			}
		}

		fragmentManager = getFragmentManager();

		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_home.equals(v)) {
			fragment = new BaseFragment();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			
			Fragment header = new Header();
			Bundle args = new Bundle();
			args.putBoolean(Header.QUIT_BUTTON, true);
			args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.home_header));
			header.setArguments(args);
			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			
		}

	}
}
