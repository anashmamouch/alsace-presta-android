package com.otipass.adt67;

import com.otipass.tools.OtipassCard;
import com.otipass.tools.PersonalInfo;

import models.Constants;
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

public class AskAnotherSale extends Fragment implements OnClickListener{

	private Button btn_yes, btn_no, btn_valid;
	private boolean another_sale_confirm = false;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private int category;
	private SharedPreferences pref;
	private Bundle bundle;
	private PersonalInfo persoInfo;
	private OtipassCard card;
	private int twin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootView = inflater.inflate(R.layout.fragment_another_sale_ask, container, false);
		btn_yes = (Button) rootView.findViewById(R.id.btn_yes);
		btn_yes.setOnClickListener(this);
		btn_no = (Button) rootView.findViewById(R.id.btn_no);
		btn_no.setOnClickListener(this);
		btn_valid = (Button) rootView.findViewById(R.id.btn_Valid);
		btn_valid.setOnClickListener(this);

		fragmentManager = getFragmentManager();

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		bundle = getArguments();
		persoInfo = (PersonalInfo) bundle.getSerializable(Constants.PERSON_KEY);
		card = (OtipassCard) bundle.getSerializable(Constants.OTIPASS_CARD_KEY);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_yes.equals(v)) {
			btn_yes.setBackgroundResource(R.color.btn_sale);
			btn_no.setBackgroundResource(R.color.gris2);
			btn_valid.setBackgroundResource(R.drawable.btn_valid);
			another_sale_confirm = true;
		}

		if (btn_no.equals(v)) {
			btn_no.setBackgroundResource(R.color.btn_sale);
			btn_yes.setBackgroundResource(R.color.gris2);
			btn_valid.setBackgroundResource(R.drawable.btn_valid);
			another_sale_confirm = false;
		}

		if (btn_valid.equals(v)) {
			if (another_sale_confirm) {

				fragment = new PackageSelectFragment();
				bundle.putSerializable(Constants.PERSON_KEY, persoInfo);
				bundle.putBoolean(Constants.ANOTHER_SALE_KEY, true);
				fragment.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				
				twin = pref.getInt(Constants.TWIN_KEY, 0);

				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(Constants.ANOTHER_SALE_KEY, true);
				if (twin == 0) {
					editor.putInt(Constants.TWIN_KEY, card.getNumotipass());
				}
				
				if (persoInfo.getName() != null) {
					editor.putString(Constants.PI_NAME_KEY, persoInfo.getName());
				}
				if (persoInfo.getFirstName() != null) {
					editor.putString(Constants.PI_FIRST_NAME_KEY, persoInfo.getFirstName());
				}
				if (persoInfo.getCountry() != null) {
					editor.putString(Constants.PI_COUNTRY_KEY, persoInfo.getCountry());
				}
				if (persoInfo.getEmail() != null) {
					editor.putString(Constants.PI_EMAIL_KEY, persoInfo.getEmail());
				}

				editor.putBoolean(Constants.PI_NEWSLETTER_KEY, persoInfo.getNewsletter());
				if (persoInfo.getPostalCode() != null) {
					editor.putString(Constants.PI_PC_KEY, persoInfo.getPostalCode());
				}

				editor.commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.select_package));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				header.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}else{

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
				category = pref.getInt(Constants.CATEGORY_KEY, 0);

				Fragment header = new Header();
				Bundle args = new Bundle();
				if (category == Constants.POS_CATEGORY) {
					fragment = new MenuPOSFragment();
					args.putString(Header.ARG_HEADER_TITLE, "");
				}else if (category == Constants.PROVIDER_CATEGORY) {
					fragment = new MenuProviderFragment();
					args.putString(Header.ARG_HEADER_TITLE, "");
				}else {
					fragment = new MenuFragment();
					args.putString(Header.ARG_HEADER_TITLE, "");
				}
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}
		}
	}
}
