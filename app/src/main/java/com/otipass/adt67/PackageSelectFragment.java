package com.otipass.adt67;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.otipass.customchoicelist.CheckableLinearLayout;
import com.otipass.sql.Fare;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Option;
import com.otipass.sql.PackageObject;
import com.otipass.sql.Param;
import com.otipass.tools.Custom_gridView;
import com.otipass.tools.PersonalInfo;
import com.otipass.tools.tools;

@SuppressLint("NewApi")
public class PackageSelectFragment extends Fragment implements OnClickListener{

	private Button btn_continue, btn_return;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private View rootView;
	private Spinner spinner;
	private ArrayList<String> listPackageNames, listHolderNames, listOptionNames, listEntryNames;
	private MuseumDbAdapter dbAdapter;
	private ArrayList<PackageObject> listPackageService;
	private String package_name;
	private int idpackage;
	private SharedPreferences pref;
	private int category;
	private PackageObject po;
	private double price_adult, price_child;
	private Bundle args;
	private Bundle bundle;
	private PersonalInfo personalInfo;
	private boolean another_sale;
	private Custom_gridView adapterGridPass, adapterGridHolder, adapterGridOption, adapterGridEntry;
	private GridView gridPass, gridHolder, gridOption, gridEntry;
	private boolean optionChecked = false, entryChecked = false;
	private int passType = -1, profilType = -1;
	private TextView tvSelection, tvPrice, tvEntry;
	private String providerName = "";
	private int nbServices;
    private ArrayList<PackageObject> listPackages;
	private List<Fare> listFare = null;
	private List<Option> listOption = null;
	private int idfare = -1, idoption = -1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_select_package, container, false);
		btn_continue = (Button) rootView.findViewById(R.id.btn_Valid);
		btn_continue.setEnabled(false);
		btn_continue.setOnClickListener(this);
		btn_return = (Button) rootView.findViewById(R.id.Btn_return);
		btn_return.setOnClickListener(this);
		gridPass = (GridView) rootView.findViewById(R.id.gridPass);
		gridPass.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		gridHolder = (GridView) rootView.findViewById(R.id.gridHolder);
		gridHolder.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		gridOption = (GridView) rootView.findViewById(R.id.gridOption);
		gridOption.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		gridEntry = (GridView) rootView.findViewById(R.id.gridEntry);
		gridEntry.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		tvSelection = (TextView) rootView.findViewById(R.id.sale_selection);
		tvPrice = (TextView) rootView.findViewById(R.id.sale_price);
		tvPrice.setVisibility(TextView.GONE);
		tvEntry = (TextView) rootView.findViewById(R.id.entry);
		tvEntry.setVisibility(TextView.GONE);
		dbAdapter = new MuseumDbAdapter(getActivity());
		dbAdapter.open();
		Param param = dbAdapter.getParam(1L);
		providerName = param.getName();
		int index = providerName.lastIndexOf('-');
		if (index > 0) {
			providerName = providerName.substring(0, index);
		}
		int providerCategory = param.getCategory();
		
		listPackageNames = new ArrayList<String>();
		listHolderNames =  new ArrayList<String>();
		listOptionNames =  new ArrayList<String>();
		listEntryNames = new ArrayList<String>();
		listPackageService = dbAdapter.getPackage();
		
		bundle = getArguments();
		if (bundle != null) {
			personalInfo = (PersonalInfo) bundle.getSerializable(Constants.PERSON_KEY);
			another_sale = bundle.getBoolean(Constants.ANOTHER_SALE_KEY);
		}
		listPackages = dbAdapter.getPackage2Sale();
		for (int i = 0; i < listPackages.size(); i++) {
			listPackageNames.add(listPackages.get(i).getName().replace(getString(R.string.pass), ""));
		}

		listFare = dbAdapter.getFareList();
		listOption = dbAdapter.getOptionList();
		for (int i = 0; i < listFare.size(); i++) {
			listHolderNames.add(listFare.get(i).getName());
		}
		for (int i = 0; i < listOption.size(); i++) {
			listOptionNames.add(listOption.get(i).getName());
		}
		String[] entries = getResources().getStringArray(R.array.entry_types);
		for (int i = 0; i < entries.length; i++) {
			listEntryNames.add(entries[i]);
		}
		gridPass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				passType = position;
				setSelectionText();
			}
		});

		gridHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				profilType = position;
				setSelectionText();
			}
		});

		gridOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// here is a trick,  since the grid is checkable, once the cell is clicked, it is always checked
				// so one must keep the option checked state into a variable
				CheckableLinearLayout layout = (CheckableLinearLayout)view;
				optionChecked = !optionChecked;
				layout.setChecked(optionChecked);
				setSelectionText();
			}
		});
		gridEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckableLinearLayout layout = (CheckableLinearLayout)view;
				entryChecked = !entryChecked;
				TextView tv = (TextView)view.findViewById(R.id.grid_text);
				if (entryChecked) {
					tv.setText(getString(R.string.avec_entree));
				} else {
					tv.setText(getString(R.string.sans_entree));
				}
				layout.setChecked(entryChecked);
				setEntryText(entryChecked);
			}
		});
		adapterGridPass = new Custom_gridView(getActivity(), listPackageNames, R.layout.grid_pass);
		gridPass.setNumColumns(listPackages.size());
		gridPass.setAdapter(adapterGridPass);

		adapterGridHolder = new Custom_gridView(getActivity(), listHolderNames, R.layout.grid_single);
		gridHolder.setAdapter(adapterGridHolder);
		adapterGridOption = new Custom_gridView(getActivity(), listOptionNames, R.layout.grid_single);
		gridOption.setAdapter(adapterGridOption);
		adapterGridEntry = new Custom_gridView(getActivity(), listEntryNames, R.layout.grid_single);
		gridEntry.setAdapter(adapterGridEntry);
		TextView tvPass = (TextView)rootView.findViewById(R.id.sale_selection);
		if (providerCategory != Constants.POS_PROVIDER_ESTABLISHMENT) {
			gridEntry.setVisibility(GridView.GONE);
			tvPass.setText(getString(R.string.pass_selection));
		} else {
			tvPass.setText(getString(R.string.pass_selection_PV));
		}
		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		tvEntry.setText(getString(R.string.une_entree) + " "+ providerName);
		fragmentManager = getFragmentManager();
		

		return rootView;
	}

	private void setSelectionText() {
		String pref, href, oref;
		tvPrice.setVisibility(TextView.VISIBLE);
		double price = 0;

		String text = getString(R.string.pass);
		if (profilType > -1) {
			text += " " + listHolderNames.get(profilType);
		} 
		if (passType > -1) {
			text += " " + listPackageNames.get(passType);
		}
		if (optionChecked) {
			text += " " + getString(R.string.avec_option) + " " + listOptionNames.get(0);
		}
		if ((profilType > -1) && (passType > -1)) {
			// the selection is ok, enable validation button
			btn_continue.setEnabled(true); 
			btn_continue.setTextAppearance(getActivity(), R.style.button_valid_enabled);
			btn_continue.setBackgroundResource(R.drawable.btn_valid);
			idpackage = listPackages.get(passType).getId();
			idfare = listFare.get(profilType).getId();
			idoption = optionChecked ? listOption.get(0).getId() : 0;
			price = dbAdapter.getPackagePrice(idpackage, idfare, idoption);
			// here
			// display the package price
			NumberFormat formatter = NumberFormat.getCurrencyInstance();
			tvPrice.setText(formatter.format(price));
			// get the provider services for that packages
			String srvs = dbAdapter.getProviderPackageServices(idpackage);
			GridView grid = (GridView)rootView.findViewById(R.id.gridEntry);

			if (!srvs.isEmpty()) {
				String t[] = srvs.split(";");
				nbServices = t.length;
				if (nbServices > 1) {
					// it is not possible to know which service to consume if more than one service
					grid.setVisibility(GridView.GONE);
					entryChecked = false;
				} else {
					grid.setVisibility(GridView.VISIBLE);
				}
			}
		}
		tvSelection.setText(text);
	}
	private void setEntryText(boolean on) {
		setSelectionText();		
		if (on) {
			tvEntry.setVisibility(TextView.VISIBLE);
		} else {
			tvEntry.setVisibility(TextView.GONE);
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (btn_return.equals(v)) {

			Fragment header = new Header();
			Bundle args = new Bundle();
			args.putBoolean(Header.QUIT_BUTTON, true);
			category = pref.getInt(Constants.CATEGORY_KEY, 0);

			if (category == Constants.POS_CATEGORY) {
				fragment = new BaseFragment();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();
				args.putString(Header.ARG_HEADER_TITLE, "");
			}else {
				fragment = new MenuFragment();
				args.putString(Header.ARG_HEADER_TITLE, "");
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
			}

			header.setArguments(args);
			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();

		}
		if (btn_continue.equals(v)) {

			fragment = new ScanFragment();
			if (fragment != null) {
				SharedPreferences.Editor edt = pref.edit();
				edt.putInt(Constants.PACKAGE_ID_KEY, idpackage);
				edt.putInt(Constants.FARE_ID_KEY, idfare);
				edt.putInt(Constants.OPTION_ID_KEY, idoption);
				edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_SALE);
				edt.putBoolean(Constants.ENTRY_KEY, entryChecked);
				edt.putString(Constants.ENTRY_KEY_TXT, getString(R.string.une_entree) + " "+ providerName);
				edt.commit();

				args = new Bundle();
				args.putBoolean(Constants.SCAN_SALE_KEY, true);
				args.putSerializable(Constants.PERSON_KEY, personalInfo);
				args.putBoolean(Constants.ANOTHER_SALE_KEY, another_sale);
				fragment.setArguments(args);
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();
			}

			Fragment header = new Header();

			args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.scan_pass));
			args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
			header.setArguments(args);

			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();

		}
	}

}
