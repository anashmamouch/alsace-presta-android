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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Otipass;
import com.otipass.sql.ProviderService;
import com.otipass.sql.ServicePass;
import com.otipass.sql.Usage;
import com.otipass.synchronization.SynchronizationService;
import com.otipass.tools.EntrySale;
import com.otipass.tools.LastEntrySale;
import com.otipass.tools.OtipassCard;
import com.otipass.tools.tools;

@SuppressLint("NewApi")
public class ServiceSelectFragment extends Fragment implements OnClickListener{

	private Button btn_Valid, btn_return, btn_cancel;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private View rootView;
	private Spinner spinner;
	private ArrayList<String> listServiceNames;
	private MuseumDbAdapter dbAdapter;
	private ArrayList<ServicePass> serviceList;
	private Bundle bundle;
	private OtipassCard card;
	private String service_name;
	private ArrayList<ProviderService> psList;
	private int idService;
	private Otipass otipass;
	private String otipass_service;
	private boolean exist = false;
	private int cpt_service;
	private boolean cpt_modified = false;
	private String [] serviceToUpdate;
	private String [] services;
	private int nbPersons = 1, cpt = 0; 
	private boolean entry_confirm = true, service_exist = false, serviceToConsume = false;
	private int nbChild;
	private AlertDialog aDialog;
	private ProgressDialog pDialog;
	private SynchronizationService synchroService;
	private int status;
	private String today;
	private boolean forced_entry;
	private int PassDuration, passPeriod;
	private String package_name;
	private TextView pkg_name, srv_name;
	private int mInterval = 1000; // 5 seconds by default, can be changed later
	private Handler mHandler;
	private Runnable mStatusChecker;
	private RelativeLayout type_pass;
	private int category;
	private long startTime, clickTime; 
	private Bundle extras;
	private long service_id;
	private boolean select_spinner_ko = true;
	private String serviceString = "";
	private Footer footer;

	public ServiceSelectFragment() {

	}
	public ServiceSelectFragment(Footer footer) {
		this.footer = footer;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_select_service, container, false);
		btn_Valid = (Button) rootView.findViewById(R.id.btn_Valid);
		btn_Valid.setOnClickListener(this);
		btn_return = (Button) rootView.findViewById(R.id.Btn_return);
		btn_return.setOnClickListener(this);
		btn_cancel = (Button) rootView.findViewById(R.id.btn_Cancel);
		btn_cancel.setOnClickListener(this);

		startTime = System.currentTimeMillis();

		// get Fragment manager
		fragmentManager = getFragmentManager();
		// open database
		dbAdapter = new MuseumDbAdapter(getActivity());
		dbAdapter.open();
		extras  = new Bundle();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		category = pref.getInt(Constants.CATEGORY_KEY, 0);

		// get otipass card from homeActivity
		bundle = getArguments();
		card = (OtipassCard) bundle.getSerializable(Constants.OTIPASS_CARD_KEY);
		forced_entry = bundle.getBoolean(Constants.FORCED_ENTRY_KEY);
		listServiceNames = new ArrayList<String>();
		psList = dbAdapter.getAllProviderService();

		otipass = new Otipass();
		otipass = dbAdapter.getOtipass(card.getNumotipass());
		String[] service_otipass = otipass.getService().split(";");

		// forced entry case
		if (forced_entry) {
			listServiceNames = new ArrayList<String>();
			for (int i = 0; i < psList.size(); i++) {
				ProviderService ps = new ProviderService();
				ps = psList.get(i);
				String[] service = ps.getService().split(":");
				service_name = dbAdapter.getServiceNameByServiceId(Integer.valueOf(service[0]));
				for (int k = 0; k < listServiceNames.size(); k++) {
					if (listServiceNames.get(k).equals(service_name)) {
						service_exist = true;
					}
				}
				if (service_exist == false) {
					listServiceNames.add(service_name);
				}
			}

			if (listServiceNames.size() > 1) {
				select_spinner_ko = false;
			}
		}else {
			// normal entry case
			listServiceNames = new ArrayList<String>();
			for (int i = 0; i < psList.size(); i++) {
				ProviderService ps = new ProviderService();
				ps = psList.get(i);
				String [] service_ = ps.getService().split(";");
				for (int j = 0; j < service_.length; j++) {

					String[] service = service_[j].split(":");

					service_name = dbAdapter.getServiceNameByServiceId(Integer.valueOf(service[0]));
					for (int k = 0; k < service_otipass.length; k++) {
						String [] service_pass = service_otipass[k].split(":");
						if (Integer.valueOf(service[0]) == Integer.valueOf(service_pass[0])) {
							if (Integer.valueOf(service_pass[1]) >= 1) {
								service_exist = false;
								for (int l = 0; l < listServiceNames.size(); l++) {
									if (listServiceNames.get(l).equals(service_name)) {
										service_exist = true;
									}
								}
								if (service_exist == false ) {
									listServiceNames.add(service_name);
								}
							}
						}
					}

					if (service_.length > 1) {
						select_spinner_ko = false;
					}
				}
			}

		}

		if (listServiceNames.size() > 0) {
			if (listServiceNames.size() == 1 && select_spinner_ko) {

				if (otipass != null) {
					package_name = dbAdapter.getPackageNameById(otipass.getPid());
				} else {
					package_name = "";
				}
				service_name = listServiceNames.get(0);
				idService = dbAdapter.getServiceIdByName(service_name);
				if (forced_entry) {
					boolean service_to_consume_ok = isThereServiceToConsume(service_name, otipass.getStatus());
					if (service_to_consume_ok) {
						otipass = dbAdapter.getOtipass(card.getNumotipass());
						consumeCoupon();
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
							bundle.putBoolean(Constants.ENTRY_CONFIRM_KEY, entry_confirm);
							fragment.setArguments(bundle);

							fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
						}
					}else {
						service_name = dbAdapter.getServiceNameById((int) idService);
						MessageFragment mf = new MessageFragment();
						extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
						extras.putString(Constants.MESSAGE_KEY, getString(R.string.service_already_consumed));
						extras.putBoolean(Constants.PASS_KO_KEY, true);
						mf.setArguments(extras);
						fragmentManager.beginTransaction()
						.replace(R.id.frame_container, mf).commit();

						Fragment header = new Header();
						Bundle args = new Bundle();
						args.putString(Header.ARG_HEADER_TITLE, getString(R.string.pass_type_service_name, package_name, service_name));
						args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
						header.setArguments(args);

						fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
					}
				}else{
					otipass = dbAdapter.getOtipass(card.getNumotipass());
					otipass_service = otipass.getService();
					services = otipass_service.split(";");

					int j = 0;
					while (!exist) {
						String [] service = services[j].split(":");
						if (service[0].equals(String.valueOf(idService))) {
							exist = true;
							cpt_service = Integer.valueOf(service[1]);
						}
						j++;
					}

					if (exist && cpt_service >= 1) {

						consumeCoupon();

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

					}else {

						aDialog = new AlertDialog.Builder(getActivity()).create();
						aDialog.setTitle(getString(R.string.Global_information));
						aDialog.setIcon(getResources().getDrawable(R.drawable.logoadt));
						if (cpt_service < 1) {
							aDialog.setMessage(getString(R.string.no_service_to_consume));
						}else{
							aDialog.setMessage(getString(R.string.pass_inaccepted));
						}
						aDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.Global_ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							} }); 
						aDialog.show();
					}
				}
				rootView = null;
			}else {
				addListenerOnSpinnerItemSelection(listServiceNames);
			}
		}else {
			aDialog = new AlertDialog.Builder(getActivity()).create();
			aDialog.setTitle(getString(R.string.Global_information));
			aDialog.setIcon(getResources().getDrawable(R.drawable.logoadt));
			if (cpt_service < 1) {
				aDialog.setMessage(getString(R.string.no_service_to_consume));
			}else{
				aDialog.setMessage(getString(R.string.pass_inaccepted));
			}
			aDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.Global_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				} }); 
			aDialog.show();
		}

		return rootView;
	}

	@SuppressLint("NewApi")
	private void addListenerOnSpinnerItemSelection(ArrayList<String> listServiceNames) {
		// TODO Auto-generated method stub

		spinner = (Spinner) rootView.findViewById(R.id.spinner);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_item, listServiceNames);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
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
			return status;
		}

		@Override
		protected void onPostExecute(Integer status) {
			pDialog.dismiss();

		}
	}

	private boolean isThereServiceToConsume(String service_name, int pass_status){
		boolean exist_ser = false;
		boolean service_to_consume_ok = false;
		otipass_service = otipass.getService();
		services = otipass_service.split(";");
		if (pass_status == Constants.PASS_CREATED) {
			service_to_consume_ok = true;
			serviceList = new ArrayList<ServicePass>();
			serviceList = dbAdapter.getServicesByPackageId(otipass.getPid());
			for (int i = 0; i < serviceList.size() ; i++) {
				ServicePass sp = new ServicePass();
				sp = serviceList.get(i);
				int number = dbAdapter.getServiceNumber(sp.getId(), otipass.getPid());
				serviceString = serviceString.concat(String.valueOf(sp.getId()) + ":" + String.valueOf(number) + ";");
			}

			dbAdapter.updateOtipassAfterEntry(serviceString, Constants.PASS_CREATED, otipass.getPid(), card.getNumotipass());

		}else{
			int idservice = dbAdapter.getServiceIdByName(service_name);
			int j = 0;
			while (!exist && j < services.length) {
				String [] service = services[j].split(":");
				if (service[0].equals(String.valueOf(idservice))) {
					exist_ser = true;
					cpt_service = Integer.valueOf(service[1]);
				}
				j++;
			}

			if (exist_ser && cpt_service >= 1) {
				service_to_consume_ok = true;
			}else{
				service_to_consume_ok = false;
			}
		}
		return service_to_consume_ok;
	}

	private void consumeCoupon(){

		otipass = dbAdapter.getOtipass(card.getNumotipass());
		otipass_service = otipass.getService();
		services = otipass_service.split(";");
		otipass_service = "";
		for (int i = 0; i < services.length; i++) {
			serviceToUpdate = services[i].split(":");
			if (serviceToUpdate[0].equals(String.valueOf(idService))) {
				cpt_modified = true;
				serviceToUpdate[1] = String.valueOf(Integer.valueOf(serviceToUpdate[1]) - 1);
			}
			otipass_service = otipass_service.concat(serviceToUpdate[0].toString().concat(":" + serviceToUpdate[1].toString() + ";"));
		}
		otipass.setService(otipass_service);


		today = tools.formatNow(Constants.EN_DATE_FORMAT);
		String [] date_ = today.split("-");
		today = date_[2];
		PassDuration = dbAdapter.getDurationByNumOtipass(card.getNumotipass());
		passPeriod = dbAdapter.getPeriodByNumOtipass(card.getNumotipass());

		if (otipass.getStatus() == Constants.PASS_INACTIVE || otipass.getStatus() == Constants.PASS_CREATED) {
			otipass.setStatus((short) Constants.PASS_ACTIVE);
			String date;
			if (PassDuration == 1) {
				// cas des pass 1 journée
				date = tools.endOfDay(Constants.FULL_DATE_FORMAT_FR);
			} else {
				date = tools.addDayToDate(Constants.FULL_DATE_FORMAT_FR, passPeriod);
			}
			Calendar cal = tools.setCalendarDate(date);
			if (cal != null) {
				otipass.setExpiry(tools.formatSQLDate(cal));
			}
		}
		dbAdapter.updateOtipass(otipass);
		String lastDayUsage = dbAdapter.getLastDayUsage(card.getNumotipass());

		if (lastDayUsage == null) {
			dbAdapter.insertUsage(new Usage(card.getNumotipass(), String.valueOf(Integer.valueOf(today))));
		}else {
			if (lastDayUsage.equals(String.valueOf(Integer.valueOf(today))) == false) {
				dbAdapter.insertUsage(new Usage(card.getNumotipass(), String.valueOf(Integer.valueOf(today))));
			}
		}

		String now = tools.formatNow(Constants.SQL_FULL_DATE_FORMAT);
		if (forced_entry) {
			dbAdapter.insertEntry(now, card.getNumotipass(), (short)nbPersons, (short)Constants.FORCED_ENTRY, false, idService);
		}else{
			dbAdapter.insertEntry(now, card.getNumotipass(), (short)nbPersons, (short)1, false, idService);
		}
		Log.i(Constants.TAG, "Consume:"+card.getNumotipass()+" "+idService);
		Calendar calendar = Calendar.getInstance();
		LastEntrySale lastEntrySale = LastEntrySale.getInstance(getActivity());
		lastEntrySale.recordLastEntrySale(new EntrySale(card.getNumotipass(), calendar, Constants.ENTRY_TYPE, idService));

		Synchro synchro = new Synchro();
		synchro.execute();
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_Valid.equals(v)) {

			//service_name = listServiceNames.get(0);
			service_name = (String) spinner.getSelectedItem();
			idService = dbAdapter.getServiceIdByName(service_name);
			package_name = dbAdapter.getPackageNameById(otipass.getPid());
			clickTime = System.currentTimeMillis();    
			if ((clickTime - startTime) < 1000 ) {
				// ask entry confirmation

				aDialog = new AlertDialog.Builder(getActivity()).create();
				aDialog.setTitle(getString(R.string.confirmation_action));
				aDialog.setIcon(getResources().getDrawable(R.drawable.ic_question));
				aDialog.setMessage(getString(R.string.confirm_entry));

				aDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (forced_entry) {
							boolean service_to_consume_ok = isThereServiceToConsume(service_name, otipass.getStatus());
							if (service_to_consume_ok) {
								otipass = dbAdapter.getOtipass(card.getNumotipass());
								consumeCoupon();

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
									bundle.putBoolean(Constants.ENTRY_CONFIRM_KEY, entry_confirm);
									fragment.setArguments(bundle);

									fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment).commit();
								}
							}else {
								service_name = dbAdapter.getServiceNameById((int) idService);
								MessageFragment mf = new MessageFragment();
								extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
								extras.putString(Constants.MESSAGE_KEY, getString(R.string.service_already_consumed));
								extras.putBoolean(Constants.PASS_KO_KEY, true);
								mf.setArguments(extras);
								fragmentManager.beginTransaction()
								.replace(R.id.frame_container, mf).commit();

								Fragment header = new Header();
								Bundle args = new Bundle();
								args.putString(Header.ARG_HEADER_TITLE, getString(R.string.pass_type_service_name, package_name, service_name));
								args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
								header.setArguments(args);

								fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
							}
						}else{
							otipass = dbAdapter.getOtipass(card.getNumotipass());
							otipass_service = otipass.getService();
							services = otipass_service.split(";");

							int j = 0;
							while (!exist) {
								String [] service = services[j].split(":");
								if (service[0].equals(String.valueOf(idService))) {
									exist = true;
									cpt_service = Integer.valueOf(service[1]);
								}
								j++;
							}

							if (exist && cpt_service >= 1) {

								consumeCoupon();

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

							}else {

								aDialog = new AlertDialog.Builder(getActivity()).create();
								aDialog.setTitle(getString(R.string.Global_information));
								aDialog.setIcon(getResources().getDrawable(R.drawable.logoadt));
								if (cpt_service < 1) {
									aDialog.setMessage(getString(R.string.no_service_to_consume));
								}else{
									aDialog.setMessage(getString(R.string.pass_inaccepted));
								}
								aDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.Global_ok), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									} }); 
								aDialog.show();
							}
						}
					} }); 
				aDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getActivity().getString(R.string.Global_non), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					} }); 
				aDialog.show();
			}else {
				if (forced_entry) {
					boolean service_to_consume_ok = isThereServiceToConsume(service_name, otipass.getStatus());
					if (service_to_consume_ok) {
						consumeCoupon();

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
							bundle.putBoolean(Constants.ENTRY_CONFIRM_KEY, entry_confirm);
							fragment.setArguments(bundle);

							fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
						}
					}else {
						service_name = dbAdapter.getServiceNameById((int) idService);
						MessageFragment mf = new MessageFragment();
						extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
						extras.putString(Constants.MESSAGE_KEY, getString(R.string.service_already_consumed));
						extras.putBoolean(Constants.PASS_KO_KEY, true);
						mf.setArguments(extras);
						fragmentManager.beginTransaction()
						.replace(R.id.frame_container, mf).commit();

						Fragment header = new Header();
						Bundle args = new Bundle();
						args.putString(Header.ARG_HEADER_TITLE, getString(R.string.pass_type_service_name, package_name, service_name));
						args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
						header.setArguments(args);

						fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
					}
				}else{
					otipass = dbAdapter.getOtipass(card.getNumotipass());
					otipass_service = otipass.getService();
					services = otipass_service.split(";");
					exist = false;
					int j = 0;
					while (!exist && j < services.length) {
						String [] service = services[j].split(":");
						if (service[0].equals(String.valueOf(idService))) {
							exist = true;
							cpt_service = Integer.valueOf(service[1]);
						}
						j++;
					}

					if (exist && cpt_service >= 1) {

						consumeCoupon();

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

					}else {

						service_name = dbAdapter.getServiceNameById((int) idService);
						MessageFragment mf = new MessageFragment();
						extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
						extras.putString(Constants.MESSAGE_KEY, getString(R.string.service_already_consumed));
						extras.putBoolean(Constants.PASS_KO_KEY, true);
						mf.setArguments(extras);
						fragmentManager.beginTransaction()
						.replace(R.id.frame_container, mf).commit();

						Fragment header = new Header();
						Bundle args = new Bundle();
						args.putString(Header.ARG_HEADER_TITLE, getString(R.string.pass_type_service_name, package_name, service_name));
						args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
						header.setArguments(args);

						fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();

					}
				}
			}

		}

		if (btn_return.equals(v)) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor edt = pref.edit();
			edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_ENTRY);
			edt.commit();
			fragment = new  ScanFragment();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

			Fragment header = new Header();
			Bundle args = new Bundle();
			args.putString(Header.ARG_HEADER_TITLE, getString(R.string.scan_pass));
			args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
			header.setArguments(args);
			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
		}

		if (btn_cancel.equals(v)) {

			aDialog = new AlertDialog.Builder(getActivity()).create();
			aDialog.setTitle(getString(R.string.confirmation_action));
			aDialog.setIcon(getResources().getDrawable(R.drawable.ic_question));
			aDialog.setMessage(getString(R.string.confirm_quit));
			aDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
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
			aDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.Global_non), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				} }); 
			aDialog.show();
		}
	}
}
