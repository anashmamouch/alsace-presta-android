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
public class MenuFragment extends Fragment implements OnClickListener {

	private Button entry_btn, sale_btn , btn_return;
	private Fragment fragment;
	private static MenuFragment mInstance = null;
	private FragmentManager fragmentManager;
	// use this function to call an instance 
	public static MenuFragment getInstance() {          
		if (mInstance == null) {      
			mInstance = new MenuFragment();    
		}
		return mInstance;  
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
		entry_btn = (Button) rootView.findViewById(R.id.btn_entry);
		entry_btn.setOnClickListener(this);
		sale_btn = (Button) rootView.findViewById(R.id.btn_sale);
		sale_btn.setOnClickListener(this);
		btn_return = (Button) rootView.findViewById(R.id.btn_Retour);
		btn_return.setOnClickListener(this);
		btn_return.setBackgroundResource(R.drawable.btn_back);
		fragmentManager = getFragmentManager();
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor edt = pref.edit();

		if (entry_btn.equals(v)) {
			fragment = new ScanFragment();
			if (fragment != null) {
				edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_ENTRY);
				edt.commit();

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
		if (sale_btn.equals(v)) {
			fragment = new PackageSelectFragment();
			if (fragment != null) {

				edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_SALE);
				edt.commit();

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
			if (fragment != null) {

				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, "");
				args.putInt(Constants.IMG_HEADER_KEY, 0);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}
		}

	}

}
