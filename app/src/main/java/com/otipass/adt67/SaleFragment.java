package com.otipass.adt67;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.otipass.adt67.R;
import com.otipass.tools.OtipassCard;
import com.otipass.tools.PersonalInfo;

@SuppressLint("NewApi")
public class SaleFragment extends Fragment implements OnClickListener {
	private View rootView;
	private Spinner spinner;
	private Button btn_continue, btn_cancel, btn_return;
	private FragmentManager fragmentManager;
	private Fragment fragment;
	private Bundle bundle;
	private OtipassCard card;
	private EditText firstName, name, email, postal_code, email_1;
	private PersonalInfo personalInfo;
	private ArrayAdapter<String> dataAdapter;
	private CheckBox cb;
	private HashMap<String, String> country_code_map;
	private String country;
	private int category;
	private AlertDialog alert;
	private boolean another_sale;
	private Footer footer;

	public SaleFragment() {

	}
	public SaleFragment(Footer footer) {
		this.footer = footer;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_sale_1, container, false);
		addListenerOnSpinnerItemSelection();

		btn_continue = (Button) rootView.findViewById(R.id.btn_Valid);
		btn_continue.setOnClickListener(this);
		btn_return = (Button) rootView.findViewById(R.id.Btn_return);
		btn_return.setOnClickListener(this);
		btn_cancel = (Button) rootView.findViewById(R.id.btn_Cancel);
		btn_cancel.setOnClickListener(this);
		firstName = (EditText) rootView.findViewById(R.id.firstname);
		name = (EditText) rootView.findViewById(R.id.name);
		email = (EditText) rootView.findViewById(R.id.email);
		email_1 = (EditText) rootView.findViewById(R.id.email_1);
		postal_code = (EditText) rootView.findViewById(R.id.postal_code);

		cb = (CheckBox) rootView.findViewById(R.id.Newsletter);

		fragmentManager = getFragmentManager();

		btn_cancel.setBackgroundResource(R.drawable.btn_cancel);

		bundle = getArguments();
		card = (OtipassCard) bundle.getSerializable(Constants.OTIPASS_CARD_KEY);
		another_sale = bundle.getBoolean(Constants.ANOTHER_SALE_KEY);
		if (another_sale) {
			personalInfo = (PersonalInfo) bundle.getSerializable(Constants.PERSON_KEY);
		}
		country = bundle.getString(Constants.COUNTRY_KEY);

		if (personalInfo != null) {
			if (!personalInfo.getCountry().equals(null)) {
				int spinnerPostion = dataAdapter.getPosition(country);
				spinner.setSelection(spinnerPostion);
				spinnerPostion = 0;
			}
			if (personalInfo.getFirstName().trim().length() > 0) {
				firstName.setText(personalInfo.getFirstName());
			}
			if (personalInfo.getName().trim().length() > 0) {
				name.setText(personalInfo.getName());
			}
			if (personalInfo.getEmail().length() > 1) {
				String [] email_array = personalInfo.getEmail().split("@");
				email.setText(email_array[0]);
				if (email_array.length > 1) {
					email_1.setText(email_array[1]);
				}
			}
			if (personalInfo.getPostalCode().trim().length() > 0) {
				postal_code.setText(personalInfo.getPostalCode());
			}
			if (personalInfo.getNewsletter()) {
				cb.setChecked(true);
			}
		}

		return rootView;
	}

	/**
	 * populate spinner
	 */
	private void addListenerOnSpinnerItemSelection() {

		country_code_map = parseStringArray(R.array.country_code_map);
		ArrayList<String> country_code_array = new ArrayList<String>();
		for (Map.Entry<String,String> entry : country_code_map.entrySet()) {
			String key = entry.getKey();
			if (key.equals(getString(R.string.FR)) == false && key.equals(getString(R.string.OTHER)) == false) {
				country_code_array.add(key);	
			}
		}

		Collections.sort(country_code_array);
		ArrayList<String> country_code_sorted = new ArrayList<String>();
		country_code_sorted.add(getString(R.string.FR));
		country_code_sorted.add(getString(R.string.OTHER));
		for (int i = 0; i < country_code_array.size(); i++) {
			country_code_sorted.add(country_code_array.get(i));
		}
		// TODO Auto-generated method stub
		spinner = (Spinner) rootView.findViewById(R.id.spinner);

		dataAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_item, country_code_sorted);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}

	public HashMap<String, String> parseStringArray(int stringArrayResourceId) {
		String[] stringArray = getResources().getStringArray(stringArrayResourceId);
		HashMap<String, String> outputArray = new HashMap<String, String>();
		for (String entry : stringArray) {
			String[] splitResult = entry.split("\\|", 2);
			outputArray.put(splitResult[1], splitResult[0]);
		}
		return outputArray;
	}

	private PersonalInfo getPersonDetails(){
		PersonalInfo person = new PersonalInfo(firstName.getText().toString(), name.getText().toString(), email.getText().toString().concat("@" + email_1.getText().toString()), postal_code.getText().toString(), country_code_map.get(spinner.getSelectedItem().toString()), cb.isChecked());

		return person;
	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		// TODO Auto-generated method stub
		if (btn_continue.equals(v)) {

			personalInfo = getPersonDetails();

			fragment = new SaleRecap(footer);
			bundle.putSerializable(Constants.OTIPASS_CARD_KEY, card);
			bundle.putString(Constants.COUNTRY_KEY, spinner.getSelectedItem().toString());
			if (personalInfo != null) {
				bundle.putSerializable(Constants.PERSON_KEY, personalInfo);
			}
			fragment.setArguments(bundle);
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			Fragment header = new Header();
			Bundle args = new Bundle();
			args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.recap));
			args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
			header.setArguments(args);

			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
		}

		if (btn_return.equals(v)) {

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor edt = pref.edit();
			edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_SALE);
			edt.commit();
			fragment = new  ScanFragment();
			bundle.putBoolean(Constants.SCAN_SALE_KEY, true);
			fragment.setArguments(bundle);
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

			Fragment header = new Header();
			Bundle args = new Bundle();
			args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.scan_pass));
			args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
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
							args = new Bundle();
							args.putString(Header.ARG_HEADER_TITLE, getString(R.string.select_package));
							args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
						}
					}else if (category == Constants.PROVIDER_CATEGORY) {
						fragment = new ScanFragment();
						if (fragment != null) {

							fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

							header = new Header();
							args = new Bundle();
							args.putString(Header.ARG_HEADER_TITLE, getString(R.string.scan_pass));
							args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);

						}
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
	}
}
