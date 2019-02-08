package com.otipass.adt67;

import java.util.ArrayList;
import java.util.List;

import models.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Otipass;
import com.otipass.tools.Callback.OnOtherButtonClickedListener;
import com.otipass.tools.Callback.OnReturnButtonClickedListener;
import com.otipass.tools.OtipassCard;

public class ScanManuallyFragment extends Fragment implements OnClickListener{

	private AutoCompleteTextView numotipass;
	private MuseumDbAdapter dbAdapter;
	private List<Otipass> otipassList;
	private ArrayList<String> otipass_list;
	private ArrayList<String> otipass_list_empty;
	private Button btn_valid, btn_return, btn_cancel;
	private OtipassCard card;
	private Context context;
	private Bundle args;
	private boolean scan_sale;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private AlertDialog alert;
	private int category;
	private ArrayAdapter<String> adapter;
	private ArrayAdapter<String> adapter_empty;

	// listener for callback events
	private OnReturnButtonClickedListener mReturnListener;
	private OnOtherButtonClickedListener mOtherListener;

	// attach callbacks to the calling activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mReturnListener = (OnReturnButtonClickedListener) activity;
			mOtherListener = (OnOtherButtonClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnContinueButtonClickedListener");
		}
	}

	public ScanManuallyFragment(){

	}

	public ScanManuallyFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_manually_scan, container, false);
		numotipass = (AutoCompleteTextView) rootView.findViewById(R.id.numotipass);
		numotipass.setGravity(Gravity.CENTER);
		btn_valid = (Button) rootView.findViewById(R.id.btn_Valid);
		btn_valid.setOnClickListener(this);
		btn_return = (Button) rootView.findViewById(R.id.btn_return);
		btn_return.setOnClickListener(this);
		btn_cancel = (Button) rootView.findViewById(R.id.btn_Cancel);
		btn_cancel.setOnClickListener(this);

		((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		setEditTextFocus(numotipass, true);

		fragmentManager = getFragmentManager();

		dbAdapter = new MuseumDbAdapter(getActivity());
		dbAdapter.open();
		otipassList = new ArrayList<Otipass>();
		otipassList = dbAdapter.getOtipassList();

		otipass_list = new ArrayList<String>();
		otipass_list_empty = new ArrayList<String>();
		for (int i = 0; i < otipassList.size(); i++) {
			Otipass otipass = new Otipass();
			otipass = otipassList.get(i);
			otipass_list.add(String.valueOf(otipass.getNumOtipass()));
		}

		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.autocomplete_item, otipass_list);
		adapter_empty = new ArrayAdapter<String>(getActivity(),
				R.layout.autocomplete_item, otipass_list_empty);
		numotipass.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{   
				s = numotipass.getText();
				if (s.length() > 2) {
					numotipass.setAdapter(adapter);
				}else {
					numotipass.setAdapter(adapter_empty);
				}
			}

		});

		args = new Bundle();
		args = getArguments();
		if (args != null) {
			scan_sale = args.getBoolean(Constants.SCAN_SALE_KEY);
			if (scan_sale) {
				LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.Scan_manually_layout_1);
				ll.setBackgroundResource(R.color.btn_sale);
			}
		}

		return rootView;
	}

	public OtipassCard getCard() {
		return card;
	}

	// manage editText's focus manually
	public void setEditTextFocus(EditText searchEditText, boolean isFocused)
	{
		searchEditText.setCursorVisible(isFocused);
		searchEditText.setFocusable(isFocused);
		searchEditText.setFocusableInTouchMode(isFocused);
		if (isFocused) {
			searchEditText.requestFocus();
		} else {
			InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_valid.equals(v)) {

			if (numotipass.getText().toString().length() > 0) {
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(numotipass.getWindowToken(), 0);
				card = new OtipassCard(Integer.valueOf(numotipass.getText().toString()));
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor edt = pref.edit();
				edt.putInt(Constants.NUMOTIPASS_KEY, card.getNumotipass());
				edt.commit();
				mOtherListener.onOtherButtonClicked(Constants.FUNC_MANUALLY_SCANNED);
			}
		}

		if (btn_return.equals(v)) {
			fragment = new ScanFragment();
			if (fragment != null) {
				((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(numotipass.getWindowToken(), 0);
				args = new Bundle();
				args.putBoolean(Constants.SCAN_SALE_KEY, scan_sale);
				fragment.setArguments(args);
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

				Fragment header = new Header();
				args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.scan_pass));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}
		}

		if (btn_cancel.equals(v)) {

			alert = new AlertDialog.Builder(getActivity()).create();
			alert.setTitle(getString(R.string.confirmation_action));
			alert.setIcon(getResources().getDrawable(R.drawable.ic_question));
			alert.setMessage(getString(R.string.confirm_quit));
			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(numotipass.getWindowToken(), 0);
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
