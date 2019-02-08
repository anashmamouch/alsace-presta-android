package com.otipass.adt67;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.otipass.sql.MuseumDbAdapter;
import com.otipass.synchronization.SynchronizationService;
import com.otipass.tools.EntrySale;
import com.otipass.tools.LastEntrySale;
import com.otipass.tools.OtipassCard;
import com.otipass.tools.tools;

@SuppressLint("NewApi")
public class MessageFragment extends Fragment implements OnClickListener{

	private Button btn_return, btn_valid, btn_cancel;
	private SharedPreferences pref;
	private int category;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private Bundle extras;
	private OtipassCard card;
	private boolean forced_entry;
	private String message, scan_type;
	private boolean pass_ko, pass_ok, scan_sale, cancel_last_action, cancel_action_ko, cancel_action_ok;
	private LinearLayout btns_layout, btn_return_layout;
	private TextView error_txtView;
	private ImageView img;
	private AlertDialog alert;
	private SynchronizationService synchroService;
	private MuseumDbAdapter dbAdapter;
	private Footer footer = null;

	public MessageFragment() {
	}
	public MessageFragment(Footer footer) {
		this.footer = footer;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_message, container, false);

		btn_return = (Button) rootView.findViewById(R.id.Btn_return);
		btn_return.setOnClickListener(this);
		btn_valid = (Button) rootView.findViewById(R.id.btn_Valid);
		btn_valid.setOnClickListener(this);
		btn_cancel = (Button) rootView.findViewById(R.id.btn_Cancel);
		btn_cancel.setOnClickListener(this);
		error_txtView = (TextView) rootView.findViewById(R.id.error_text);
		img = (ImageView) rootView.findViewById(R.id.img_msg);

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		fragmentManager = getFragmentManager();
		
		dbAdapter = new MuseumDbAdapter(getActivity());
		dbAdapter.open();

		extras             = getArguments();
		card               = (OtipassCard) extras.getSerializable(Constants.OTIPASS_CARD_KEY);
		forced_entry       = extras.getBoolean(Constants.FORCED_ENTRY_KEY);
		message            = extras.getString(Constants.MESSAGE_KEY);
		pass_ko            = extras.getBoolean(Constants.PASS_KO_KEY);
		pass_ok            = extras.getBoolean(Constants.PASS_OK_KEY);
		cancel_last_action = extras.getBoolean(Constants.CANCEL_LAST_ACTION);
		cancel_action_ko   = extras.getBoolean(Constants.CANCEL_ACTION_KO);
		cancel_action_ok   = extras.getBoolean(Constants.CANCEL_ACTION_OK);
		scan_type          = extras.getString(Constants.SCAN_TYPE_KEY);
		
		if (pass_ko) {
			btn_valid.setBackgroundResource(R.drawable.btn_valid_x);
			btn_valid.setEnabled(false);
			img.setBackgroundResource(R.drawable.st_ko);
			btn_cancel.setEnabled(false);
		}else {
			img.setBackgroundResource(R.drawable.st_warn);
			btn_cancel.setBackgroundResource(R.drawable.btn_cancel);
			error_txtView.setBackgroundResource(R.color.warning_msg);
		}

		if (pass_ok) {
			img.setBackgroundResource(R.drawable.st_ok);
			error_txtView.setBackgroundResource(R.color.vert_fonce);
			btn_cancel.setBackgroundResource(R.drawable.btn_cancel);
		}

		if (cancel_action_ko) {
			btn_valid.setBackgroundResource(R.drawable.btn_valid_x);
			btn_valid.setEnabled(false);
			btn_cancel.setBackgroundResource(R.drawable.btn_cancel_x);
			btn_cancel.setEnabled(false);
			img.setBackgroundResource(R.drawable.st_ko);
			error_txtView.setBackgroundResource(R.color.rouge);
		}

		if (cancel_action_ok) {
			img.setBackgroundResource(R.drawable.st_ok);
			error_txtView.setBackgroundResource(R.color.vert_fonce);
			btn_valid.setBackgroundResource(R.drawable.btn_valid_x);
			btn_valid.setEnabled(false);
			btn_cancel.setBackgroundResource(R.drawable.btn_cancel_x);
			btn_cancel.setEnabled(false);
		}

		if (message != null) {
			error_txtView.setText(message);
		}
		scan_sale    = extras.getBoolean(Constants.SCAN_SALE_KEY);
		if (scan_sale) {
			LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.MessageFragment_layout_1);
			ll.setBackgroundResource(R.color.btn_sale);
		}
		if (scan_type != null) {
			if (scan_type.equals(Constants.SCAN_SALE)) {
				LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.MessageFragment_layout_1);
				ll.setBackgroundResource(R.color.btn_sale);
			}else if (scan_type.equals(Constants.SCAN_ENTRY)) {
				LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.MessageFragment_layout_1);
				ll.setBackgroundResource(R.color.btn_entry);
			}
		}
		
		return rootView;
	}

	private class Synchro extends AsyncTask<Void, Integer, Integer>
	{

		public Synchro() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected void onProgressUpdate(Integer... values){

		}

		// send data to server
		@Override
		protected Integer doInBackground(Void... arg0) {
			try {
				int st;
				do {
					st = tools.getServiceState(getActivity());
				} while (st != tools.cIdle);
			} catch (Exception e) {}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer listUploadSize) {
			
			LastEntrySale lastEntrySale = LastEntrySale.getInstance(getActivity());
			EntrySale es = lastEntrySale.getEntrySale();
			if (es.getType() == Constants.SALE_TYPE) {
				String date = tools.formatSQLDate(es.getDate());
				int idUpdate = dbAdapter.getUpdateId(es.getNumOtipass(), date);

				if (idUpdate > 0) {
					dbAdapter.deleteUpdate((long)idUpdate);
				}
			}
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_return.equals(v)) {
			if (cancel_last_action) {
				fragment = new BaseFragment();
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.welcome_msg));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.home);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}else{
				fragment = new ScanFragment();
				Bundle bundle = new Bundle();
				if (scan_sale) {
					bundle.putBoolean(Constants.SCAN_SALE_KEY, true);
					fragment.setArguments(bundle);
				}

				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getActivity().getString(R.string.scan_pass));
				if (scan_sale) {
					args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				}else{
					args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
				}
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}
		}

		if (btn_valid.equals(v)) {
			if (cancel_last_action) {

				LastEntrySale lastEntrySale = LastEntrySale.getInstance(getActivity());

				int result = lastEntrySale.cNoEntryToCancel;
				if (lastEntrySale.getEntrySale2Cancel()) {
					result = lastEntrySale.cancelLastEntrySale();
					fragment = new MessageFragment(footer);
					Bundle bundle = new Bundle();
					if (result == lastEntrySale.cOK) {
						bundle.putString(Constants.MESSAGE_KEY, getString(R.string.operation_cancel_ok));
						bundle.putBoolean(Constants.CANCEL_ACTION_OK, true);
					}else {
						bundle.putString(Constants.MESSAGE_KEY, getString(R.string.operation_cancel_ko));
						bundle.putBoolean(Constants.CANCEL_ACTION_KO, true);
					}
					bundle.putBoolean(Constants.CANCEL_LAST_ACTION, true);
					fragment.setArguments(bundle);

					synchroService = new SynchronizationService(getActivity());
					if (footer != null) {
						synchroService.start(Constants.SEND_UPDATES, footer.communicationHandler, footer);
					} else {
						synchroService.start(Constants.SEND_UPDATES);
					}
					Synchro synchro = new Synchro();
					synchro.execute();
					
					fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

				}

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, "");
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}else{
				ServiceSelectFragment ssf = new ServiceSelectFragment(footer);
				extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
				if (forced_entry) {
					extras.putBoolean(Constants.FORCED_ENTRY_KEY, true);
				}
				ssf.setArguments(extras);
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, ssf).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.select_service));
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
					if (cancel_last_action) {
						fragment = new BaseFragment();
						fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();

						Fragment header = new Header();
						Bundle args = new Bundle();
						args.putString(Header.ARG_HEADER_TITLE, getString(R.string.welcome_msg));
						args.putInt(Constants.IMG_HEADER_KEY, R.drawable.home);
						header.setArguments(args);

						fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
					}else{
						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
						category = pref.getInt(Constants.CATEGORY_KEY, 0);

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
					}

				} }); 
			alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.Global_non), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				} }); 
			alert.show();
		}
	}
}
