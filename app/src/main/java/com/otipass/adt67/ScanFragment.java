package com.otipass.adt67;

import com.otipass.adt67.R;
import com.otipass.tools.Callback;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.zxing.integration.android.*;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class ScanFragment extends Fragment implements OnClickListener{

	private ImageView scanAnim;
	private Button btn_return, btn_cancel, btn_scan_manually, btn_scan_qrcode;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private String entry;
	private SharedPreferences pref;
	private int category;
	private Bundle args;
	private boolean scan_sale;
	private AlertDialog alert;
    private Callback.OnQrcodeListener mReturnListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

		// get fragment manager
		fragmentManager = getFragmentManager();

		btn_return = (Button) rootView.findViewById(R.id.Btn_return);
		btn_return.setOnClickListener(this);
		scanAnim = (ImageView) rootView.findViewById(R.id.scan);
		scanAnim.setImageBitmap(null);
		scanAnim.setBackgroundResource( R.animator.anim_scan );  
		btn_cancel = (Button) rootView.findViewById(R.id.btn_Cancel);
		btn_cancel.setOnClickListener(this);
		btn_cancel.setBackgroundResource(R.drawable.btn_cancel);
		btn_scan_manually = (Button) rootView.findViewById(R.id.scan_manually);
		btn_scan_manually.setOnClickListener(this);
        btn_scan_qrcode = (Button) rootView.findViewById(R.id.scan_qrcode);
        btn_scan_qrcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				try {
					/*
					Intent intent = new Intent("com.google.zxing.client.android.SCAN");
					intent.setPackage("com.google.zxing.client.android");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
					startActivityForResult(intent, 0);
*/
                    IntentIntegrator integrator = new IntentIntegrator(getActivity());
                    integrator.initiateScan();
				}catch (Exception ex) {	}
            }
        });


		final AnimationDrawable scanAnimation = (AnimationDrawable) scanAnim.getBackground();
		scanAnim.post(new Runnable() {
			public void run() {
				if ( scanAnimation != null ) scanAnimation.start();
			}
		});


		Animation imgAnimation = AnimationUtils.loadAnimation(getActivity(), R.animator.anim_card);
		ImageView image = (ImageView) rootView.findViewById(R.id.card);
		image.startAnimation(imgAnimation);

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		args = new Bundle();
		args = getArguments();
		if (args != null) {
			scan_sale = args.getBoolean(Constants.SCAN_SALE_KEY);
			if (scan_sale) {
				LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ScanFragment_layout);
				ll.setBackgroundResource(R.color.btn_sale);
			}
		}
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_return.equals(v)) {

			Fragment header = new Header();
			args = new Bundle();

			entry = pref.getString(Constants.SCAN_TYPE_KEY, null);
			if (entry.equals(Constants.SCAN_ENTRY)) {
				category = pref.getInt(Constants.CATEGORY_KEY, 0);
				if (category == Constants.PROVIDER_CATEGORY) {
					fragment = new BaseFragment();
					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();
					args.putString(Header.ARG_HEADER_TITLE, "");
				}else {
					fragment = new MenuFragment();
					args.putString(Header.ARG_HEADER_TITLE, "");
				}
			}else{
				fragment = new PackageSelectFragment();
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.select_package));			
			}

			if (fragment != null && fragment.getTag() == null) {
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			}

			header.setArguments(args);
			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
		}

		if (btn_cancel.equals(v)) {

			alert = new AlertDialog.Builder(getActivity()).create();
			alert.setTitle(getString(R.string.confirmation_action));
			alert.setIcon(getResources().getDrawable(R.drawable.ic_question));
			alert.setMessage(getString(R.string.confirm_quit));
			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
					category = pref.getInt(Constants.CATEGORY_KEY, 0);
					
					SharedPreferences.Editor editor = pref.edit();
					editor.putBoolean(Constants.ANOTHER_SALE_KEY, false);
					editor.remove(Constants.TWIN_KEY);
					editor.remove(Constants.PI_FIRST_NAME_KEY);
					editor.remove(Constants.PI_NAME_KEY);
					editor.remove(Constants.PI_EMAIL_KEY);
					editor.remove(Constants.PI_COUNTRY_KEY);
					editor.remove(Constants.PI_NEWSLETTER_KEY);
					editor.remove(Constants.PI_PC_KEY);
					editor.commit();
					

					Fragment header = new Header();
					Bundle args = new Bundle();

					if (category == Constants.POS_CATEGORY) {
						fragment = new PackageSelectFragment();
						if (fragment != null) {

							Fragment h = new Header();
							args = new Bundle();
							args.putString(Header.ARG_HEADER_TITLE, getString(R.string.select_package));
							args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
							h.setArguments(args);

							fragmentManager.beginTransaction().replace(R.id.header_frame, h).commit();
						}
					}else if (category == Constants.PROVIDER_CATEGORY) {
						fragment = new BaseFragment();
						args.putString(Header.ARG_HEADER_TITLE, getString(R.string.welcome_msg));
						args.putInt(Constants.IMG_HEADER_KEY, R.drawable.home);
						fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();
					}else {
						fragment = new MenuFragment();
						args.putString(Header.ARG_HEADER_TITLE, "");
						args.putInt(Constants.IMG_HEADER_KEY, 0);
					}
					if (fragment != null && fragment.getTag() == null) {
						fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
					}
					
					header.setArguments(args);

					fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
				} }); 
			alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.Global_non), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				} }); 
			alert.show();
		}

		if (btn_scan_manually.equals(v)) {
			fragment = new ScanManuallyFragment();

			args = new Bundle();
			args.putBoolean(Constants.SCAN_SALE_KEY, scan_sale);
			fragment.setArguments(args);
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

			Fragment header = new Header();
			args = new Bundle();
			args.putString(Header.ARG_HEADER_TITLE, getString(R.string.numotipass_entry));
			args.putInt(Constants.IMG_HEADER_KEY, R.drawable.hand_keyboard);
			header.setArguments(args);
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
		}
	}
}
