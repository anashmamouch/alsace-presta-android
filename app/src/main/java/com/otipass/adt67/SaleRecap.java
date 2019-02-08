package com.otipass.adt67;

import java.util.ArrayList;
import java.util.Calendar;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.otipass.adt67.R;
import com.otipass.sql.Fare;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Option;
import com.otipass.sql.Otipass;
import com.otipass.sql.ServicePass;
import com.otipass.synchronization.SynchronizationService;
import com.otipass.tools.EntrySale;
import com.otipass.tools.LastEntrySale;
import com.otipass.tools.OtipassCard;
import com.otipass.tools.PersonalInfo;
import com.otipass.tools.tools;

@SuppressLint("NewApi")
public class SaleRecap extends Fragment implements OnClickListener{

	private Button btn_return, btn_cancel, btn_valid;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private Bundle bundle;
	private OtipassCard card;
	private PersonalInfo persoInfo;
	private TextView NumOtipass, Fname_TxtView, Name_TxtView, Email_TxtView, Country_TxtView, PostalCode_TxtView, Newsletter_TxtView, PassType_TxtView, Message_txtView, Price_txtView, Entry_txtView;
	private MuseumDbAdapter dbAdapter;
	private ProgressDialog pDialog;
	private SynchronizationService synchroService;
	private int statuse = 1, category, idpackage, twin;
	private ArrayList<ServicePass> serviceList;
	private String serviceString = "";
	private LinearLayout FName_layout, Name_layout, Email_layout, Country_layout, PostalCode_layout, Newsletter_layout, Message_layout, Entry_layout;
	private String country;
	private String passType, entryTxt;
	private AlertDialog alert;
	private long startTime, clickTime; 
	private boolean another_sale, entryChecked;
	private double price;
    private Footer footer;
	private int idfare, idoption;

    public SaleRecap() {

    }
    public SaleRecap(Footer footer) {
        this.footer = footer;
    }
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_sale_recap, container, false);
		fragmentManager = getFragmentManager();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		idpackage = pref.getInt(Constants.PACKAGE_ID_KEY, 0);
		idfare = pref.getInt(Constants.FARE_ID_KEY, -1);
		idoption = pref.getInt(Constants.OPTION_ID_KEY, -1);
		twin = pref.getInt(Constants.TWIN_KEY, 0);
		category = pref.getInt(Constants.CATEGORY_KEY, 0);
		another_sale = pref.getBoolean(Constants.ANOTHER_SALE_KEY, false);
		entryChecked = pref.getBoolean(Constants.ENTRY_KEY, false);
		entryTxt = pref.getString(Constants.ENTRY_KEY_TXT, "");
				
		startTime = System.currentTimeMillis();

		dbAdapter = new MuseumDbAdapter(getActivity());
		dbAdapter.open();
		passType = dbAdapter.getPackageNameById(idpackage);
        if (tools.getIsNewTariffOn(getActivity())) {
            price = dbAdapter.getPackagePrice(idpackage, idfare, idoption);
        } else{
            price = dbAdapter.getPriceByIdPackage(idpackage);
        }


		btn_return = (Button) rootView.findViewById(R.id.Btn_return);
		btn_return.setOnClickListener(this);

		btn_valid = (Button) rootView.findViewById(R.id.btn_Valid);
		btn_valid.setOnClickListener(this);

		btn_cancel = (Button) rootView.findViewById(R.id.btn_Cancel);
		btn_cancel.setOnClickListener(this);
		btn_cancel.setBackgroundResource(R.drawable.btn_cancel);

		NumOtipass = (TextView) rootView.findViewById(R.id.NumOtipass);
		Fname_TxtView = (TextView) rootView.findViewById(R.id.FName);
		Name_TxtView = (TextView) rootView.findViewById(R.id.Name);
		Email_TxtView = (TextView) rootView.findViewById(R.id.Email);
		Country_TxtView = (TextView) rootView.findViewById(R.id.Country);
		PostalCode_TxtView = (TextView) rootView.findViewById(R.id.PostalCode);
		Newsletter_TxtView = (TextView) rootView.findViewById(R.id.Newsletter);
		PassType_TxtView = (TextView) rootView.findViewById(R.id.PassType);
		Price_txtView = (TextView) rootView.findViewById(R.id.Price);
		Entry_txtView= (TextView) rootView.findViewById(R.id.Entry_Text);
		Message_txtView = (TextView) rootView.findViewById(R.id.Message);

		FName_layout = (LinearLayout)rootView.findViewById(R.id.FName_layout);
		Name_layout = (LinearLayout)rootView.findViewById(R.id.Name_layout);
		Email_layout = (LinearLayout)rootView.findViewById(R.id.Email_layout);
		Country_layout = (LinearLayout)rootView.findViewById(R.id.Country_layout);
		PostalCode_layout = (LinearLayout)rootView.findViewById(R.id.PostalCode_layout);
		Newsletter_layout = (LinearLayout)rootView.findViewById(R.id.Newsletter_layout);
		Entry_layout = (LinearLayout)rootView.findViewById(R.id.Entry_layout);

		bundle = getArguments();
		card = (OtipassCard) bundle.getSerializable(Constants.OTIPASS_CARD_KEY);
		persoInfo = (PersonalInfo) bundle.getSerializable(Constants.PERSON_KEY);
		country = bundle.getString(Constants.COUNTRY_KEY);

		if (!card.equals(null)) {
			if (entryChecked) {
				NumOtipass.setText(getString(R.string.numotipass_status, card.getNumotipass(), getString(R.string.OTI_ACTIVE)));
			} else {
				NumOtipass.setText(getString(R.string.numotipass_status, card.getNumotipass(), getString(R.string.OTI_INACTIVE)));
			}
		}

		if (persoInfo.getFirstName().trim().length() == 0) {
			FName_layout.setVisibility(LinearLayout.GONE);
		}else {
			Fname_TxtView.setText(persoInfo.getFirstName().replaceAll("\\n", ""));
		}

		if (persoInfo.getName().trim().length() == 0) {
			Name_layout.setVisibility(LinearLayout.GONE);
		}else {
			Name_TxtView.setText(persoInfo.getName().replaceAll("\\n", ""));
		}

		if (persoInfo.getEmail().trim().length() <= 1) {
			Email_layout.setVisibility(LinearLayout.GONE);
		}else {
			Email_TxtView.setText(persoInfo.getEmail().replaceAll("\\n", ""));
		}

		if (persoInfo.getCountry().trim().length() > 0) {
			Country_TxtView.setText(country);
		}

		if (persoInfo.getPostalCode().trim().length() == 0) {
			PostalCode_layout.setVisibility(LinearLayout.GONE);
		}else {
			PostalCode_TxtView.setText(persoInfo.getPostalCode().replaceAll("\\n", ""));
		}

		if (persoInfo.getNewsletter() == false) {
			Newsletter_layout.setVisibility(LinearLayout.GONE);
		}else {
			Newsletter_TxtView.setText(getString(R.string.Global_oui));
		}
		String packageText = passType;
		if (idfare > -1) {
			Fare fare = dbAdapter.getFare(idfare);
			packageText += " " + fare.getName();
			if (idoption > 0) {
				Option option = dbAdapter.getOption(idoption);
				packageText += " " + option.getName();
			}

		}
		PassType_TxtView.setText(packageText);
		Price_txtView.setText(getString(R.string.price_euro, String.valueOf(price)));
		
		if (entryChecked) {
			Entry_txtView.setText(entryTxt);
			Message_txtView.setVisibility(TextView.GONE);
		} else {
			Entry_layout.setVisibility(LinearLayout.GONE);
		}

		return rootView;
	}

	private void processSale() {
		String now = tools.formatNow(Constants.SQL_FULL_DATE_FORMAT); 

		long id = dbAdapter.insertUpdate(now, 1, card.getNumotipass(), idpackage, persoInfo, twin, idfare, idoption);
		if (id > 0) {

			serviceList = new ArrayList<ServicePass>();
			serviceList = dbAdapter.getServicesByPackageId(idpackage);
			for (int i = 0; i < serviceList.size() ; i++) {
				ServicePass sp = new ServicePass();
				sp = serviceList.get(i);
				int number = dbAdapter.getServiceNumber(sp.getId(), idpackage);
				serviceString = serviceString.concat(String.valueOf(sp.getId()) + ":" + String.valueOf(number) + ";");
			}

			Calendar calendar = Calendar.getInstance();
			LastEntrySale lastEntrySale = LastEntrySale.getInstance(getActivity());
			lastEntrySale.recordLastEntrySale(new EntrySale(card.getNumotipass(), calendar, Constants.SALE_TYPE, 0));

			Synchro synchro = new Synchro();
			synchro.execute();
			int status = entryChecked ? Constants.PASS_ACTIVE : Constants.PASS_INACTIVE;	
			dbAdapter.updateOtipassAfterSale(serviceString, status, idpackage, card.getNumotipass(), idfare, idoption);
			Log.i(Constants.TAG, "processSale::insert sale "+card.getNumotipass());
			if (entryChecked) {
				// save entry
				String service_package = dbAdapter.getServicebyPackageId(idpackage);
				String[] provider_service = service_package.split(":");
				int service_id = Integer.valueOf(provider_service[0]);
				
				dbAdapter.insertEntry(now, card.getNumotipass(), (short)1, (short)Constants.NORMAL_ENTRY, false, service_id);
				Log.i(Constants.TAG, "processSale::save entry "+card.getNumotipass());
				Otipass otipass = dbAdapter.getOtipass(card.getNumotipass());
				String otipass_service = otipass.getService();
				String[] services = otipass_service.split(";"),serviceToUpdate;
				boolean exist = false;
				int j = 0, cpt_service = 0;
				while (!exist) {
					String [] service = services[j].split(":");
					if (service[0].equals(String.valueOf(service_id))) {
						exist = true;
						cpt_service = Integer.valueOf(service[1]);
					}
					j++;
				}

				if (exist && cpt_service >= 1) {
					otipass_service = "";
					for (int i = 0; i < services.length; i++) {
						serviceToUpdate = services[i].split(":");
						if (serviceToUpdate[0].equals(String.valueOf(service_id))) {
							serviceToUpdate[1] = String.valueOf(Integer.valueOf(serviceToUpdate[1]) - 1);
						}
						otipass_service = otipass_service.concat(serviceToUpdate[0].toString().concat(":" + serviceToUpdate[1].toString() + ";"));
					}
					otipass.setService(otipass_service);
					dbAdapter.updateOtipass(otipass);
				}

			}
			
			if (persoInfo.getName().length() != 0 && persoInfo.getFirstName().length() != 0 && persoInfo.getEmail().length() != 0) {
				fragment = new AskAnotherSale();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				bundle.putSerializable(Constants.PERSON_KEY, persoInfo);
				bundle.putSerializable(Constants.OTIPASS_CARD_KEY, card);
				fragment.setArguments(bundle);

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.ask_for_another_sale));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}else{

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
					fragment = new ScanFragment();
					if (fragment != null) {

						fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

						args.putString(Header.ARG_HEADER_TITLE, getString(R.string.scan_pass));
						args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);

					}
				}else {
					fragment = new MenuFragment();
					args.putString(Header.ARG_HEADER_TITLE, "");
					args.putInt(Constants.IMG_HEADER_KEY, 0);
				}
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
				if (fragment != null && fragment.getTag() == null) {

					fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
				}
			}
		}
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_return.equals(v)) {
			if (another_sale) {
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
			}else{
				fragment = new SaleFragment(footer);
				bundle.putSerializable(Constants.OTIPASS_CARD_KEY, card);
				if (persoInfo != null) {
					bundle.putSerializable(Constants.PERSON_KEY, persoInfo);
				}
				bundle.putString(Constants.COUNTRY_KEY, country);
				fragment.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.pass_number_inactif, card.getNumotipass()));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}
		}

		if (btn_valid.equals(v)) {

			clickTime = System.currentTimeMillis();    
			if ((clickTime - startTime) < 1000 ) {
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.setTitle(getString(R.string.confirmation_action));
				alertDialog.setIcon(getResources().getDrawable(R.drawable.ic_question));
				alertDialog.setMessage(getString(R.string.confirm_sale));
				alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						processSale();
					} }); 
				alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getActivity().getString(R.string.Global_non), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					} }); 
				alertDialog.show();
			} else {
				processSale();
			}
		}

		if (btn_cancel.equals(v)) {
			alert = new AlertDialog.Builder(getActivity()).create();
			alert.setTitle(getString(R.string.confirmation_action));
			alert.setIcon(getResources().getDrawable(R.drawable.ic_question));
			alert.setMessage(getString(R.string.confirm_quit));
			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

	private class Synchro extends AsyncTask<Void, Integer, Integer>
	{

		public Synchro() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage(getString(R.string.Upload_step_1));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			//pDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values){

		}

		// send data to server
		@Override
		protected Integer doInBackground(Void... arg0) {
			try {
				synchroService = new SynchronizationService(getActivity());
                if (footer != null) {
                    synchroService.start(Constants.SEND_UPDATES, footer.communicationHandler, footer);
                } else {
                    synchroService.start(Constants.SEND_UPDATES);
                }
			} catch (Exception e) {}
			return statuse;
		}

		@Override
		protected void onPostExecute(Integer listUploadSize) {
			pDialog.dismiss();

		}
	}
}
