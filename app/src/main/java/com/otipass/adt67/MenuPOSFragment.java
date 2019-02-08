package com.otipass.adt67;

import com.otipass.adt67.R;

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
public class MenuPOSFragment extends Fragment implements OnClickListener{

	private Button btn_sale, btn_return;
	private FragmentManager fragmentManager;
	private Fragment fragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_menu_pos, container, false);
		btn_sale = (Button) rootView.findViewById(R.id.btn_sale);
		btn_sale.setOnClickListener(this);
		btn_return = (Button) rootView.findViewById(R.id.btn_return);
		btn_return.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		fragmentManager = getFragmentManager();
		if (btn_sale.equals(v)) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor edt = pref.edit();
			edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_SALE);
			edt.commit();
			fragment = new PackageSelectFragment();
			if (fragment != null) {

				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.select_package));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}
		}

		if (btn_return.equals(v)) {
			fragment = new BaseFragment();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();

			Fragment header = new Header();
			Bundle args = new Bundle();
			args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.home_header));
			args.putBoolean(Header.QUIT_BUTTON, true);
			header.setArguments(args);

			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
		}
	}

}
