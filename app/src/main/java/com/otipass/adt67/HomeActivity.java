package com.otipass.adt67;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import models.Constants;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.otipass.slidingmenu.adapter.HeaderNavDrawer;
import com.otipass.slidingmenu.adapter.Item;
import com.otipass.slidingmenu.adapter.ListItem;
import com.otipass.slidingmenu.adapter.NavDrawerListAdapter;
import com.otipass.slidingmenu.adapter.NavDrawerListCustomAdapter;
import com.otipass.slidingmenu.model.NavDrawerItem;
import com.otipass.sql.Fare;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Option;
import com.otipass.sql.Otipass;
import com.otipass.sql.Param;
import com.otipass.sql.Usage;
import com.otipass.synchronization.SynchroAlarm;
import com.otipass.synchronization.SynchronizationService;
import com.otipass.tools.Callback;
import com.otipass.tools.Callback.OnOtherButtonClickedListener;
import com.otipass.tools.Callback.OnReturnButtonClickedListener;
import com.otipass.tools.LastEntrySale;
import com.otipass.tools.Nfc;
import com.otipass.tools.OtipassCard;
import com.otipass.tools.PersonalInfo;
import com.otipass.tools.tools;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class HomeActivity extends Activity implements  OnReturnButtonClickedListener, OnOtherButtonClickedListener{
	private static final String TAG = "Alsace";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] mHomeList, mPassList, mStatsList, mStockList, mMaintenanceList;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private Footer footer;
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private Fragment fragment = null;
	private FragmentManager fragmentManager;
	private MuseumDbAdapter dbAdapter;
	private String message;
	private int category;
	private Otipass otipass;
	private int nbUsePass, status, menuItemSelected, lastMenuItemSelected, idService, cpt_service, displayType, PassDuration, PassPeriod, nbPersons = 1, synchronizationType, progressStep, WLProgress, communicationSequenceStatus;
	private Date expiryDate;
	private Date nowDate;
	private SynchronizationService synchroService;
	private AlertDialog alert;
	private Bundle extras;
	private OtipassCard card;
	private String[] menuSections;
	private SharedPreferences pref;
	private String otipass_service;
	private boolean exist = false, connected, cpt_modified = false, rapid_mode = false, NFCEnabled, forced_entry, entry_confirm = true, synchroInProgress = false, syncInProgress, another_sale, newsletter, max_use_ok = false;
	private String [] services;
	private String [] serviceToUpdate;
	private Bundle bundle;
	private ProgressDialog pDialog;
	private Fragment header;
	private Bundle args;
	private String package_name, today, service_name, scan_type, now, communicationStatusText, name, first_name, country, email, postal_code;
	private long service_id;
	private int nb_authorized;
	private ProgressDialog progressDialog;
	private int progressValue;
	private int nbWLCards = 0;
	private int nbCards = 0;
	private int nbWLSteps = 0;
	private int lastProgress;
	private Dialog aDialog;
	private Param param;
	private int idUser = 0;
	private static boolean mustClose;
	// dialog types uses by handler
	public static final int cTypeDialog = 1;
	public static final int cTypeLabel = 2;

	// sections
	private static final int cHomeSection = 0;
	private static final int cPassSection = 1;
	private static final int cStatsSection = 2;
	private static final int cMaintenanceSection = 3;

	private NavDrawerListCustomAdapter adapterCustom;

	private LinearLayout layout_header;
	private boolean mpm_installed = false;
	private boolean mulhouse_installed = false;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		layout_header = (LinearLayout) findViewById(R.id.header_frame);
		pref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);

		// open database
		dbAdapter = new MuseumDbAdapter(this);
		dbAdapter.open();
		param = dbAdapter.getParam(1L);
		setSynchroAlarm();

		// call fragment manager
		fragmentManager = getFragmentManager();

		// init footer
		initFooter();

		// init header
		initHeader("", 0);

		// init menu
		configureMenu();

		// check MPM package
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo("com.otipass.passmuseum", PackageManager.GET_ACTIVITIES);
			mpm_installed = true;
		}
		catch (PackageManager.NameNotFoundException e) {
		}

		// check Mulhouse package
		pm = getPackageManager();
		try {
			pm.getPackageInfo("com.otipass.mulhouse", PackageManager.GET_ACTIVITIES);
			mulhouse_installed = true;
		}
		catch (PackageManager.NameNotFoundException e) {
			Log.i("tag", e.getMessage());
		}

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(1);
		}
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(
				this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// check whether the NFC is enabled
		NFCEnabled = checkApplication();
		if (!NFCEnabled) {
			new AlertDialog.Builder(this).setIcon(getResources().getDrawable(R.drawable.ic_attention))
			.setTitle(getString(R.string.Global_information))
			.setMessage(getString(R.string.NFC_not_active))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.Global_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
		}
	}

	private void setSynchroAlarm() {
		param = dbAdapter.getParam(1L);
		String callingTime = param.getCall();
		SynchroAlarm.setAlarm(this, callingTime);
		//SynchroAlarm.setPeriodicAlarm(this);
	}

	/**
	 * check whether nfc is enabled or not
	 * @return
	 */
	private boolean checkApplication() {
		boolean result = true;
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC) ) {
			if (!nfcAdapter.isEnabled()) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * init footer
	 */

	private void initFooter(){
		//footer = Footer.getInstance();
		footer = new Footer();
		fragmentManager.beginTransaction().replace(R.id.footer_frame, footer).commit();
	}

	/**
	 * init header
	 * @param text
	 * @param viewButton
	 */

	private void initHeader(String text, int drawableId) {
		Fragment header = new Header();
		Bundle args = new Bundle();
		args.putString(Header.ARG_HEADER_TITLE, text);
		args.putInt(Constants.IMG_HEADER_KEY, drawableId);
		header.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String message;
		int profile = 0;

		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.about:

			Param param = dbAdapter.getParam(1L);

			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
			try {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
				profile = preferences.getInt(Constants.CATEGORY_KEY, 0);
			} catch (Exception ex) {

			}

			try {
				String[] user_profiles = getResources().getStringArray(R.array.user_profiles);
				message = getString(R.string.otipass) + " - "+ Constants.plateform + " - " + "v. "+ getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
				message += "\n" + getString(R.string.connected_as, user_profiles[profile]);
				message += "\n" + getString(R.string.device, param.getName());

				dlgAlert.setMessage(message);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dlgAlert.setTitle(R.string.about);
			dlgAlert.setIcon(getResources().getDrawable(R.drawable.logoadt));
			dlgAlert.setPositiveButton(R.string.Global_ok,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//dismiss the dialog  
				}
			});
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			return true;

		case R.id.mpm:
			if (mpm_installed) {
				Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.otipass.passmuseum");
				intent1.putExtra(Constants.EXTERN_CALL_KEY, true);
				startActivity(intent1);
			}
			return true;

		case R.id.mulhouse:
				if (mulhouse_installed) {
					Intent intent2 = getPackageManager().getLaunchIntentForPackage("com.otipass.mulhouse");
					intent2.putExtra(Constants.EXTERN_CALL_KEY, true);
					startActivity(intent2);
				}
				return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.about).setVisible(!drawerOpen);
		if (mpm_installed == false) {
			menu.findItem(R.id.mpm).setVisible(false);
		}
		if (mulhouse_installed == false) {
			menu.findItem(R.id.mulhouse).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private void homeFunc() {
		layout_header.setVisibility(LinearLayout.VISIBLE);
		Fragment header = new Header();
		Bundle args = new Bundle();
		args.putString(Header.ARG_HEADER_TITLE, getString(R.string.welcome_msg));
		args.putInt(Constants.IMG_HEADER_KEY, R.drawable.home);
		header.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();

		fragment = new BaseFragment(footer);
		fragmentManager.beginTransaction()
		.replace(R.id.frame_container, fragment, Constants.BASE_FRAGMENT_TAG).commit();
	}
	
	private void entrysaleFunc() {
		footer.silentSynchronize();
		layout_header.setVisibility(LinearLayout.VISIBLE);
		Fragment h = new Header();
		args = new Bundle();

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

		h.setArguments(args);
		fragmentManager.beginTransaction().replace(R.id.header_frame, h).commit();
		
	}
	
	private void statsEntryFunc() {
		layout_header.setVisibility(LinearLayout.GONE);
		fragment = new AccessWebFragment();
		bundle = new Bundle();
		bundle.putString(Constants.ACTION_KEY, Constants.WEB_ENTRY_ACTION);
		fragment.setArguments(bundle);
	}
	
	private void statsSaleFunc() {
		layout_header.setVisibility(LinearLayout.GONE);
		fragment = new AccessWebFragment();
		bundle = new Bundle();
		bundle.putString(Constants.ACTION_KEY, Constants.WEB_SALE_ACTION);
		fragment.setArguments(bundle);
	}
	
	private void orderStockFunc() {
		layout_header.setVisibility(LinearLayout.GONE);
		fragment = new AccessWebFragment();
		bundle = new Bundle();
		bundle.putString(Constants.ACTION_KEY, Constants.WEB_CMD_ACTION);
		fragment.setArguments(bundle);
	}
	
	private void initializationFunc() {
		fragment = null;
		layout_header.setVisibility(LinearLayout.VISIBLE);
		alert = new AlertDialog.Builder(HomeActivity.this).create();
		alert.setTitle(getString(R.string.confirmation_action));
		alert.setIcon(getResources().getDrawable(R.drawable.ic_question));
		alert.setMessage(getString(R.string.confirm_initialization));
		alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			footer.synchronise(SynchronizationService.cGetTotalWL);
			} }); 
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.Global_non), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			} }); 
		alert.show();
	}
	
	private void synchronizationFunc() {
		layout_header.setVisibility(LinearLayout.VISIBLE);
		footer.synchronise(SynchronizationService.cGetPartialWL);
		WaitSynchro ws = new WaitSynchro();
		ws.execute();
	}
	
	private void entryFunc() {
		layout_header.setVisibility(LinearLayout.VISIBLE);

		SharedPreferences.Editor edt = pref.edit();
		edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_ENTRY);
		edt.commit();
		fragment = new ScanFragment();
		if (fragment != null) {

			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment, Constants.SCAN_FRAGMENT_TAG).commit();

			header = new Header();
			args = new Bundle();
			args.putString(Header.ARG_HEADER_TITLE, getString(R.string.scan_pass));
			args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
			header.setArguments(args);

			fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
		}
	}
	
	private void saleFunc() {
		layout_header.setVisibility(LinearLayout.VISIBLE);

		SharedPreferences.Editor edt = pref.edit();
		edt.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_SALE);
		edt.commit();
		fragment = new PackageSelectFragment();
		if (fragment != null) {

			Fragment h = new Header();
			args = new Bundle();
			args.putString(Header.ARG_HEADER_TITLE, getString(R.string.select_package));
			args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
			h.setArguments(args);

			fragmentManager.beginTransaction().replace(R.id.header_frame, h).commit();
		}
	}
	
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		if (category == Constants.POS_PROVIDER_CATEGORY) {

			switch (position) {
			case Constants.HOME:
				homeFunc();
				break;
			case Constants.ENTRY_SALE:
				entrysaleFunc();
				break;
			case Constants.STAT_ENTRIES:
				statsEntryFunc();
				break;
			case Constants.STAT_SALES:
				statsSaleFunc();
				break;
			case Constants.ORDER_STOCK:
				orderStockFunc();
				break;
			case Constants.INITIALIZATION:
				initializationFunc();				
				break;
			case Constants.SYNCHRONIZATION:
				synchronizationFunc();				
				break;
			case Constants.CANCEL_OPERATION:
				cancelLastAction();

				break;
			default:
				break;
			}
		}
		if (category == Constants.POS_CATEGORY) {

			switch (position) {
			case Constants.HOME_POS:
				homeFunc();
				break;
			case Constants.SALE_POS:
				saleFunc();
				break;
			case Constants.STAT_SALES_POS:
				statsSaleFunc();
				break;
			case Constants.ORDER_STOCK_POS:
				orderStockFunc();
				break;
			case Constants.INITIALIZATION_POS:
				initializationFunc();	
				break;
			case Constants.SYNCHRONIZATION_POS:
				synchronizationFunc();				
				break;
			case Constants.CANCEL_OPERATION_POS:
				cancelLastAction();
				break;

			default:
				break;
			}
		}
		if (category == Constants.PROVIDER_CATEGORY) {
			switch (position) {
			case Constants.HOME_PROVIDER:
				homeFunc();
				break;
			case Constants.ENTRY_PROVIDER:
				entryFunc();
				break;

			case Constants.STAT_ENTRIES_PROVIDER:
				statsEntryFunc();
				break;
			case Constants.INITIALIZATION_PROVIDER:
				initializationFunc();	
				break;
			case Constants.SYNCHRONIZATION_PROVIDER:
				synchronizationFunc();				
				break;
			case Constants.CANCEL_OPERATION_PROVIDER:
				cancelLastAction();
				break;

			default:
				break;
			}
		}
		if (fragment != null && fragment.getTag() == null) {
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment).commit();
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(getString(R.string.pass_alsace));

			// update selected item and title, then close the drawer

		} else {
			// error in creating fragment
			Log.e("HomeActivity", "Error in creating fragment");
		}
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void cancelLastAction(){
		LastEntrySale lastEntrySale = LastEntrySale.getInstance(HomeActivity.this);
		Calendar now_cal = Calendar.getInstance();
		String entrySaleMsg = lastEntrySale.getLastEntrySale(now_cal);

		header = new Header();
		args = new Bundle();

		fragment = new MessageFragment();
		bundle = new Bundle();
		bundle.putBoolean(Constants.CANCEL_LAST_ACTION, true);
		if (entrySaleMsg.length() > 0) {
			String [] numPassCancel = entrySaleMsg.split("\\s");
			String msg_cancel = numPassCancel[0].concat(" ".concat(numPassCancel[1]));
			if (msg_cancel.equals(getString(R.string.entry_to_cancel))) {
				bundle.putString(Constants.MESSAGE_KEY, getString(R.string.confirm_cancel_entry, numPassCancel[2]));
			}else if (msg_cancel.equals(getString(R.string.sale_to_cancel))) {
				bundle.putString(Constants.MESSAGE_KEY, getString(R.string.confirm_cancel_sale, numPassCancel[2]));
			}
			args.putString(Header.ARG_HEADER_TITLE, entrySaleMsg);
		}else {
			bundle.putString(Constants.MESSAGE_KEY, getString(R.string.no_opertation_to_cancel));
			bundle.putBoolean(Constants.CANCEL_ACTION_KO, true);
			args.putString(Header.ARG_HEADER_TITLE, "");
		}
		fragment.setArguments(bundle);

		args.putString(Header.ARG_HEADER_TITLE, entrySaleMsg);
		header.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}


	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onReturnButtonClicked() {
		// This is called when returning form webAccessFragment
		displayView(Constants.HOME_POS);
	}
	private void configureMenu() {

		// load slide menu items

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
		category = preferences.getInt(Constants.CATEGORY_KEY, 0);

		//		Param param = dbAdapter.getParam(1L);
		//		category = param.getCategory();

		if (category == Constants.POS_CATEGORY) {
			navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_pos);
			mPassList = new String[] {getString(R.string.sale_header)};
		}else if (category == Constants.PROVIDER_CATEGORY) {
			navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_provider);
			mPassList = new String[] {getString(R.string.entry_header)};
		}else {
			navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
			mPassList = new String[] {getString(R.string.entry_sale)};
		}


		menuItemSelected = lastMenuItemSelected = 0;
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		NavDrawerListAdapter adapter = new NavDrawerListAdapter(this);

		menuSections = new String[] {getString(R.string.home_section), getString(R.string.pass_section), getString(R.string.stats_section), getString(R.string.stock_section), getString(R.string.maintenance_section)};
		mHomeList = new String[] {getString(R.string.message_fragment)};
		mStatsList = new String[] {getString(R.string.entry_stats_activity)};
		if (category == Constants.POS_PROVIDER_CATEGORY) {
			mStatsList = new String[] {getString(R.string.entry_stats_activity), getString(R.string.sale_stats_activity)};
		}else if (category == Constants.POS_CATEGORY) {
			mStatsList = new String[] {getString(R.string.sale_stats_activity)};
		}else if (category == Constants.PROVIDER_CATEGORY) {
			mStatsList = new String[] {getString(R.string.entry_stats_activity)};
		}
		mStockList = new String[] {getString(R.string.order_stock)};
		mMaintenanceList = new String[] {getString(R.string.initialization), getString(R.string.synchronization), getString(R.string.cancel_last_operation)};

		if (!menuSections[cHomeSection].isEmpty()) {
			adapter.addSection(menuSections[cHomeSection], new ArrayAdapter<String>(this, R.layout.drawer_list_item_color, mHomeList));
		}
		if (!menuSections[cPassSection].isEmpty()) {
			adapter.addSection(menuSections[cPassSection], new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPassList));
		}
		if (!menuSections[cStatsSection].isEmpty()) {
			adapter.addSection(menuSections[cStatsSection], new ArrayAdapter<String>(this, R.layout.drawer_list_item, mStatsList));
		}
		if (!menuSections[cMaintenanceSection].isEmpty()) {
			adapter.addSection(menuSections[cMaintenanceSection], new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMaintenanceList));
		}

		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		// set up the drawer's list view with items and click listener
		//mDrawerList.setAdapter(adapter);


		// populate drawer with sections and colors

		List<Item> items = new ArrayList<Item>();
		items.add(new HeaderNavDrawer(getString(R.string.home_section), R.color.violet));
		for (int i = 0; i < mHomeList.length; i++) {
			items.add(new ListItem("     ", mHomeList[i], R.color.violet));
		}

		items.add(new HeaderNavDrawer(getString(R.string.pass_section), R.color.btn_entry));
		for (int i = 0; i < mPassList.length; i++) {
			items.add(new ListItem("     ", mPassList[i], R.color.btn_entry));
		}

		items.add(new HeaderNavDrawer(getString(R.string.stats_section), R.color.btn_sale));
		for (int i = 0; i < mStatsList.length; i++) {
			items.add(new ListItem("     ", mStatsList[i], R.color.btn_sale));
		}

		if (category == Constants.POS_CATEGORY || category == Constants.POS_PROVIDER_CATEGORY) {
			items.add(new HeaderNavDrawer(getString(R.string.stock_section), R.color.rouge1));
			for (int i = 0; i < mStockList.length; i++) {
				items.add(new ListItem("     ", mStockList[i], R.color.rouge1));
			}
		}

		items.add(new HeaderNavDrawer(getString(R.string.maintenance_section), R.color.noir));
		for (int i = 0; i < mMaintenanceList.length; i++) {
			items.add(new ListItem("     ", mMaintenanceList[i], R.color.noir));
		}


		NavDrawerListCustomAdapter adapterNav = new NavDrawerListCustomAdapter(this, items);
		mDrawerList.setAdapter(adapterNav);

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				if ((menuItemSelected > 0)) {
					NavDrawerListAdapter adapter = (NavDrawerListAdapter)mDrawerList.getAdapter();
					adapter.checkItem(menuItemSelected, lastMenuItemSelected);
				}
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	/*
	 * management of the "Back" Android button
	 * 
	 * If pressed, displays a confirmation message to quit the application
	 * 
	 */
	public void onBackPressed() {
		exitApplication();
	}

	private void  exitApplication() {
		new AlertDialog.Builder(this)
		.setMessage(
				getString(
						R.string.Confirmation_quitter)
						.toString())
						.setCancelable(false)
						.setIcon(R.drawable.logoadt)
						.setTitle(
								getString(R.string.Global_information)
								.toString())
								.setPositiveButton(getString(R.string.Global_oui),
										new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										SharedPreferences.Editor editor = pref.edit();
										editor.putBoolean(Constants.ANOTHER_SALE_KEY, false);
										editor.remove(Constants.TWIN_KEY);
										editor.remove(Constants.PI_FIRST_NAME_KEY);
										editor.remove(Constants.PI_NAME_KEY);
										editor.remove(Constants.PI_EMAIL_KEY);
										editor.remove(Constants.PI_COUNTRY_KEY);
										editor.remove(Constants.PI_NEWSLETTER_KEY);
										editor.remove(Constants.PI_PC_KEY);
										idUser = 0;
										editor.putInt(Constants.USER_KEY, (int)idUser);
										editor.commit();
										mustClose = true;
                                        setResult(Activity.RESULT_OK);
										finish();

									}
								})
								.setNegativeButton(getString(R.string.Global_non),
										new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

									}
								}).show();

	}


	/*
	 * Android OS primitive, called when the activity looses focus
	 * 
	 * 
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
	}

	/*
	 * Android OS primitive, called when the activity gains focus
	 * 
	 * 
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		if (mustClose) {
			mustClose = false;
			finish();
		}
		try {
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
			IntentFilter[] intentFiltersArray = new IntentFilter[] {ndef};

			String[][] techListsArray = new String[][] {new String[] {Ndef.class.getName()}};
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
		} catch (Exception ex) {
			Log.e(Constants.TAG, "HomeActivity onResume" + ex.getMessage());
		}
	}


	@Override
	public void onNewIntent(Intent intent) 
	{	
		setIntent(intent);
		resolveIntent(intent);
	}

	private void resolveIntent(Intent intent) {
		// TODO Auto-generated method stub

		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			extras = intent.getExtras();
			card = Nfc.readCard(intent, extras);
			if (card != null) {
				ScanFragment scan_fragment = (ScanFragment)getFragmentManager().findFragmentByTag(Constants.SCAN_FRAGMENT_TAG);
				if (scan_fragment != null) {

					if (scan_fragment.isVisible()) {
						controlPass(card);
					}
				}
			}
		}
	}

    private int countServicesOfProvider() {
        int nb = 0;
        otipass = new Otipass();
        otipass = dbAdapter.getOtipass(card.getNumotipass());

        String service_package = dbAdapter.getServicebyPackageId(otipass.getPid());
        if (service_package != null) {
            String[] services = service_package.split(";");
            nb = services.length;
        }
        return nb;
    }

	private int isThereServiceToConsume(){
		exist = false;
		int service_to_consume_ok = -2;
		otipass = new Otipass();
		otipass = dbAdapter.getOtipass(card.getNumotipass());

		String service_package = dbAdapter.getServicebyPackageId(otipass.getPid());
		if (service_package != null) {
			String [] service_package_ = service_package.split(";");
			int i = 0;
			otipass_service = otipass.getService();
			services = otipass_service.split(";");
			while((service_to_consume_ok < 1)  && i < service_package_.length) {
				String[] provider_service = service_package_[i].split(":");
				service_id = Long.valueOf(provider_service[0]);

				exist = false;
				int j = 0;
				while (!exist && j < services.length) {
					String [] service = services[j].split(":");
					if (service[0].equals(String.valueOf(service_id))) {
						exist = true;
						service_to_consume_ok = Integer.valueOf(service[1]);
						break;
					}
					j++;
				}

				i++;
			}
		}else {
			service_to_consume_ok = -1;
		}
		return service_to_consume_ok;
	}

	private void Consume(){
		exist = false;
		otipass = new Otipass();
		otipass = dbAdapter.getOtipass(card.getNumotipass());

		String service_package = dbAdapter.getServicebyPackageId(otipass.getPid());
		String[] provider_service = service_package.split(":");
		long service_id = Long.valueOf(provider_service[0]);

		otipass_service = otipass.getService();
		services = otipass_service.split(";");

		int j = 0;
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
					cpt_modified = true;
					serviceToUpdate[1] = String.valueOf(Integer.valueOf(serviceToUpdate[1]) - 1);
				}
				otipass_service = otipass_service.concat(serviceToUpdate[0].toString().concat(":" + serviceToUpdate[1].toString() + ";"));
			}
			otipass.setService(otipass_service);
			dbAdapter.updateOtipass(otipass);

			today = tools.formatNow(Constants.EN_DATE_FORMAT);
			String now = tools.formatNow(Constants.SQL_FULL_DATE_FORMAT);
			String date;
			PassDuration = dbAdapter.getDurationByNumOtipass(card.getNumotipass());
			PassPeriod = dbAdapter.getPeriodByNumOtipass(card.getNumotipass());
			if (PassDuration == 1) {
				// cas des pass 1 journée
				date = tools.endOfDay(Constants.FULL_DATE_FORMAT_FR);
			} else {
				date = tools.addDayToDate(Constants.FULL_DATE_FORMAT_FR, PassPeriod);
			}
			Calendar cal = tools.setCalendarDate(date);
			if (cal != null) {
				date = tools.formatSQLDate(cal);
			}
			
			dbAdapter.updateOtipassExpiryDate(date, card.getNumotipass());

			String lastDayUsage = dbAdapter.getLastDayUsage(card.getNumotipass());

			if (lastDayUsage == null) {
				dbAdapter.insertUsage(new Usage(card.getNumotipass(), today));
			}else {
				if (lastDayUsage.equals(today) == false) {
					dbAdapter.insertUsage(new Usage(card.getNumotipass(), today));
				}
			}
			dbAdapter.insertEntry(now, card.getNumotipass(), (short)nbPersons, (short)1, false, (int) service_id);
            Log.i(Constants.TAG, "Consume:"+card.getNumotipass()+" "+service_id);
			fragment = new ConfirmationFragment();
			if (fragment != null) {
				footer.silentSynchronize();
				bundle = new Bundle();
				bundle.putBoolean(Constants.ENTRY_CONFIRM_KEY, entry_confirm);
				fragment.setArguments(bundle);

				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
			}

		}else {

			alert = new AlertDialog.Builder(HomeActivity.this).create();
			alert.setTitle(getString(R.string.Global_information));
			alert.setIcon(getResources().getDrawable(R.drawable.logoadt));
			if (cpt_service < 1) {
				alert.setMessage(getString(R.string.no_service_to_consume));
			}else{
				alert.setMessage(getString(R.string.pass_inaccepted));
			}
			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Global_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				} }); 
			alert.show();
		}
	}
	
	private void rejectCardOnSale(OtipassCard card, Otipass otipass) {
		if (otipass.getStatus() == Constants.PASS_INACTIVE) {
			message = getString(R.string.OTI_INACTIVE);
		}
		if (otipass.getStatus() == Constants.PASS_ACTIVE) {
			message = getString(R.string.OTI_ACTIVE);
		}
		if (otipass.getStatus() == Constants.PASS_EXPIRED) {
			message = getString(R.string.OTI_EXPIRED);
		}
		if (otipass.getStatus() == Constants.PASS_INVALID) {
			message = getString(R.string.OTI_INVALID);
		}
		Log.i(Constants.TAG, "rejectCardOnSale:"+card.getNumotipass()+" "+message);
		MessageFragment mf = new MessageFragment(footer);
		extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
		extras.putString(Constants.MESSAGE_KEY, getString(R.string.sale_pass_fail));
		extras.putBoolean(Constants.PASS_KO_KEY, true);
		extras.putBoolean(Constants.PASS_OK_KEY, false);
		extras.putBoolean(Constants.SCAN_SALE_KEY, true);
		extras.putString(Constants.SCAN_TYPE_KEY, scan_type);
		mf.setArguments(extras);
		fragmentManager.beginTransaction()
		.replace(R.id.frame_container, mf).commit();

		Fragment header = new Header();
		Bundle args = new Bundle();
		args.putString(Header.ARG_HEADER_TITLE, getString(R.string.pass_status, otipass.getNumOtipass(), message));
		args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
		header.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
	}

	private void displayRejectMessage(String message) {
		new AlertDialog.Builder(this)
		.setMessage(message)
		.setCancelable(false)
		.setIcon(R.drawable.logoadt)
		.setTitle(getString(R.string.Global_information))
		.setPositiveButton(getString(R.string.Global_ok),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
				}
			}).show();
	}
	
	private void controlPassSale(Otipass otipass) {
		pref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
		another_sale = pref.getBoolean(Constants.ANOTHER_SALE_KEY, false);
		first_name   = pref.getString(Constants.PI_FIRST_NAME_KEY, null);
		name         = pref.getString(Constants.PI_NAME_KEY, null);
		email        = pref.getString(Constants.PI_EMAIL_KEY, null);
		country      = pref.getString(Constants.PI_COUNTRY_KEY, null);
		postal_code  = pref.getString(Constants.PI_PC_KEY, null);
		newsletter   = pref.getBoolean(Constants.PI_NEWSLETTER_KEY, false);
		if (another_sale) {
			PersonalInfo persoInfo = new PersonalInfo(first_name, name, email, postal_code, country, newsletter);
			if (otipass.getStatus() == Constants.PASS_CREATED) {
				// sale summary
				fragment = new SaleRecap(footer);
				bundle = new Bundle();
				bundle.putSerializable(Constants.OTIPASS_CARD_KEY, card);
				if (persoInfo != null) {
					bundle.putSerializable(Constants.PERSON_KEY, persoInfo);
				}
				fragment.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.recap));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				header.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
			}else {
				// card not in right state
				rejectCardOnSale(card, otipass);
			}
		}else{
			if (otipass.getStatus() == Constants.PASS_CREATED) {
				extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
				extras.putBoolean(Constants.SCAN_SALE_KEY, true);
				extras.putBoolean(Constants.ANOTHER_SALE_KEY, another_sale);
				SaleFragment sf = new SaleFragment(footer);
				sf.setArguments(extras);
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, sf).commit();

				Fragment header = new Header();
				Bundle args = new Bundle();
				args.putString(Header.ARG_HEADER_TITLE, getString(R.string.pass_number_inactif, card.getNumotipass()));
				args.putInt(Constants.IMG_HEADER_KEY, R.drawable.cart);
				header.setArguments(args);

				fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();

			}else {
				rejectCardOnSale(card, otipass);
			}
		}
	}
	
	private void rejectCardOnEntry(OtipassCard card, String message, String title) {
        Log.i(Constants.TAG, "rejectCardOnEntry:"+card.getNumotipass()+" "+message+" "+title);
		MessageFragment mf = new MessageFragment(footer);
		extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
		extras.putBoolean(Constants.PASS_KO_KEY, true);
		extras.putBoolean(Constants.PASS_OK_KEY, false);
		extras.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_ENTRY);
		extras.putString(Constants.MESSAGE_KEY, message);
		mf.setArguments(extras);
		fragmentManager.beginTransaction()
		.replace(R.id.frame_container, mf).commit();

		Fragment header = new Header();
		Bundle args = new Bundle();
		args.putString(Header.ARG_HEADER_TITLE, title);
		args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
		header.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
	}

	private void acceptCardOnEntry(OtipassCard card, String message, boolean forcedEntry, String title) {
        Log.i(Constants.TAG, "acceptCardOnEntry:"+card.getNumotipass()+" "+message+" "+title);
		MessageFragment mf = new MessageFragment(footer);
		extras.putSerializable(Constants.OTIPASS_CARD_KEY, card);
		if (forcedEntry) {
			extras.putBoolean(Constants.FORCED_ENTRY_KEY, true);
			extras.putBoolean(Constants.PASS_OK_KEY, false);
		} else {
			extras.putBoolean(Constants.FORCED_ENTRY_KEY, false);
			extras.putBoolean(Constants.PASS_OK_KEY, true);
		}
		extras.putBoolean(Constants.PASS_KO_KEY, false);
		extras.putString(Constants.SCAN_TYPE_KEY, Constants.SCAN_ENTRY);
		extras.putString(Constants.MESSAGE_KEY, message);
		mf.setArguments(extras);
		fragmentManager.beginTransaction()
		.replace(R.id.frame_container, mf).commit();

		Fragment header = new Header();
		Bundle args = new Bundle();
		args.putString(Header.ARG_HEADER_TITLE, title);
		args.putInt(Constants.IMG_HEADER_KEY, R.drawable.entry);
		header.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.header_frame, header).commit();
	}
	
	private void controlPassEntry(Otipass otipass) {
		String message, sDate="", title="";
        Log.i(Constants.TAG, "controlPassEntry:"+otipass.getNumOtipass());
		int nb_service_provider = dbAdapter.isProviderHasServices();
		if (nb_service_provider > 0) {
			// this provider has at least 1 service
			if (otipass.getStatus() == Constants.PASS_INVALID) {
				title = getString(R.string.pass_status, otipass.getNumOtipass(), getString(R.string.OTI_INVALID));
				rejectCardOnEntry(card, getString(R.string.pass_invalid_blocked), title);
			} else if (otipass.getStatus() == Constants.PASS_CREATED) {
				if (footer.isOnline()) {
					// check card at server
					synchroService = new SynchronizationService(getApplicationContext());
					synchroService.start(Constants.CHECK_OTIPASS, card.getNumotipass(), footer);
					CheckSynchro synchro = new CheckSynchro();
					synchro.execute();
				} else {
					// force entry when no Internet connection
					title = getString(R.string.pass_status, otipass.getNumOtipass(), getString(R.string.OTI_UNDEFINED));
					acceptCardOnEntry(card, getString(R.string.type_pass_unkown), true, title);
				}
			}else {
				// card expired, active or inactive
				expiryDate = null;
				nbUsePass = dbAdapter.getNbUsePass(card.getNumotipass());
				String lastDayUsage = dbAdapter.getLastDayUsage(card.getNumotipass());
				today = tools.formatNow(Constants.EN_DATE_FORMAT);
				String [] date_ = today.split("-");
				today = date_[2];
				if (otipass.getStatus() != Constants.PASS_INACTIVE) {
					SimpleDateFormat  format = new SimpleDateFormat(Constants.SQL_FULL_DATE_FORMAT);  
					try {
						expiryDate = format.parse(otipass.getExpiry());
					} catch (ParseException e) {}
					try {
						now = tools.formatNow(Constants.SQL_FULL_DATE_FORMAT);
						nowDate = format.parse(now);
					} catch (ParseException e) {}
					if (expiryDate != null) {
						nb_authorized = dbAdapter.getDurationByNumOtipass(card.getNumotipass());
						if ((nbUsePass >= nb_authorized && lastDayUsage.equals(String.valueOf(Integer.valueOf(today))) == false) || nowDate.after(expiryDate)) {
							if ((nbUsePass >= nb_authorized && lastDayUsage.equals(String.valueOf(Integer.valueOf(today))) == false)) {
								max_use_ok = true;
							}
							if (nowDate.after(expiryDate)) {
								max_use_ok = false;
							}
							otipass.setStatus((short) Constants.PASS_EXPIRED);
							dbAdapter.updateOtipass(otipass);
						}
					}
				}
				if (otipass.getStatus() == Constants.PASS_EXPIRED) {
					if (max_use_ok) {
						message =  getString(R.string.max_use_ok);
					}else{
						sDate = tools.formatTextDate(otipass.getExpiry());
						message =  getString(R.string.pass_expired, sDate);
					}
					title = getString(R.string.pass_status, otipass.getNumOtipass(), getString(R.string.OTI_EXPIRED));
					rejectCardOnEntry(card, message, title);
					
				} else {
					// pass active or inactive
					package_name = dbAdapter.getPackageNameById(otipass.getPid());
					if (otipass.getFareId() > -1) {
						Fare fare = dbAdapter.getFare(otipass.getFareId());
						if (fare != null) {
							package_name += " " + fare.getName();
							if (otipass.getOptionId() > 0) {
								Option option = dbAdapter.getOption(otipass.getOptionId());
								package_name += " " + option.getName();
							}
						}

					}
                    if (otipass.getStatus() == Constants.PASS_ACTIVE) {
                        title = getString(R.string.pass_status, otipass.getNumOtipass(), getString(R.string.OTI_ACTIVE))+'\n'+package_name;
                    } else {
                        title = getString(R.string.pass_status, otipass.getNumOtipass(), getString(R.string.OTI_INACTIVE))+'\n'+package_name;
                    }
                    int nbServicesOfProvider = countServicesOfProvider();
					int nb = isThereServiceToConsume();
					if (nb > 0) {
						// there is still a service to consume for this pass
						if (otipass.getStatus() == Constants.PASS_ACTIVE) {
							pref = getPreferences(0);
							rapid_mode = pref.getBoolean(Constants.RAPID_MODE_KEY, false);
							if (rapid_mode) {
								alert = new AlertDialog.Builder(HomeActivity.this).create();
								alert.setTitle(getString(R.string.confirmation_action));
								alert.setIcon(getResources().getDrawable(R.drawable.ic_question));
								alert.setMessage(getString(R.string.confirm_entry));
								alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.Global_oui), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Consume();
									} }); 
								alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.Global_non), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									} }); 
								alert.show();
							}else{
                                if (nbServicesOfProvider > 1) {
                                    // more than one service is proposed by the provider, service selection needed
                                    service_name = getString(R.string.prestation_multiple);
                                } else {
                                    service_name = dbAdapter.getServiceNameById((int) service_id);
                                }
								if (otipass.getExpiry() != null) {
									sDate = tools.formatTextDate(otipass.getExpiry());
								}
								acceptCardOnEntry(card, getString(R.string.pass_validity_entry, service_name, sDate), false, title);
							}
						} else {
							// Pass inactive
							PassDuration = dbAdapter.getDurationByNumOtipass(card.getNumotipass());
							PassPeriod = dbAdapter.getPeriodByNumOtipass(card.getNumotipass());
							if (PassDuration == 1) {
								// cas des pass 1 journée
								sDate = tools.endOfDay(Constants.DATE_FORMAT_FR_NOSEC) + " h";
							} else {
								sDate = tools.addDayToDate(Constants.DATE_FORMAT_FR_NOSEC, PassPeriod) + " h";
							}
							acceptCardOnEntry(card, getString(R.string.pass_validity, sDate), false, title);
						}
					} else if (nb == 0){
						// no more service to consume
						service_name = dbAdapter.getServiceNameById((int) service_id);
						rejectCardOnEntry(card, getString(R.string.pass_type_service_name, getString(R.string.service_already_consumed), service_name), title);
					} else {
						// not in the package
						int index;
						String s="", name = dbAdapter.getParam(1L).getName();
						if ((index = name.lastIndexOf('-')) > -1) {
							s =  name.substring(0, index);
							if ((index = s.lastIndexOf('-')) > -1) {
								s =  s.substring(0, index);
							}
						}
						rejectCardOnEntry(card, getString(R.string.service_not_included, s),  title);
					}
				}
			}
		}else {
			// no service available for the provider
            title = getString(R.string.pass_status, otipass.getNumOtipass(), "");
			rejectCardOnEntry(card, getString(R.string.no_provider_service), title);
		}
	}
	
	private void controlPass(OtipassCard card){
		pref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
		String scan_type = pref.getString(Constants.SCAN_TYPE_KEY, null);
		if (scan_type != null) {
			if (card.getClient().contains(Constants.TAG)) {
				// This is a Pass Alsace
				otipass = new Otipass();
				otipass = dbAdapter.getOtipass(card.getNumotipass());
				if (otipass != null) {
					if (extras == null) {
						extras = new Bundle();
					}
					if (scan_type.equals(Constants.SCAN_SALE)) {
						controlPassSale(otipass);
					} else {
						controlPassEntry(otipass);
					}
				}else {
					// Otipass = null, not in the tablet database
					if (footer.isOnline()) {
						// connected, check the card by the server
						synchroService = new SynchronizationService(getApplicationContext());
						synchroService.start(Constants.CHECK_OTIPASS, card.getNumotipass(), footer);
						CheckSynchro synchro = new CheckSynchro();
						synchro.execute();
					} else {
						// not connected, check impossible, card is rejected
						displayRejectMessage(getString(R.string.pass_adt_ko_offline));
					}
				}
			} else {
				// no TAG Pass Alsace detected
				displayRejectMessage(getString(R.string.pass_adt_ko));
			}
		}
	}

	@Override
	public void onOtherButtonClicked(int function) {
		// back from otipass number entered manually 
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
		int numotipass = pref.getInt(Constants.NUMOTIPASS_KEY, 0);
		if (numotipass > 0) {
			// manual entry accepts card as a Pass Alsace card
			card = new OtipassCard(numotipass, Constants.TAG);
			controlPass(card);
		}
	}
	
	// this class is intended to wait for end of communication process
	private class WaitSynchro extends AsyncTask<Void, Integer, Void>
	{
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Integer... values){
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			int st;
			do {
				st = tools.getServiceState(getApplicationContext());
			} while (st != tools.cIdle);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// to check messages
			homeFunc();
		}
	}
	
	// this class is intended to wait for the check of the card at the server
	private class CheckSynchro extends AsyncTask<Void, Integer, Void>
	{
		@Override
		protected void onPreExecute() {
			Drawable icon = getResources().getDrawable(R.drawable.ic_attention);
			String title = getString(R.string.Global_information);
			alert = new AlertDialog.Builder(HomeActivity.this).create();
			alert.setTitle(title);
			alert.setIcon(icon);
			alert.setMessage(getString(R.string.check_card_state_running) + getString(R.string.Global_veuillez_patienter));
			alert.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values){
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			boolean end = false;
			long startTime = System.currentTimeMillis();
			do {
				// display at least 2 seconds
				if ((System.currentTimeMillis() - startTime) > 2000) {
					end = (tools.getServiceState(getApplicationContext()) != tools.cCommmunicationPending);
					if (!end) {
						end = (System.currentTimeMillis() - startTime) > 5000;
					}
				}
			} while (!end);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
            String title="";
			alert.dismiss();
			// info sent back by the server
			Otipass checkCard = synchroService.getCheckedCard();
			if (checkCard != null) {
				status = checkCard.getStatus();
				OtipassCard card = new OtipassCard((int)checkCard.getNumOtipass(), checkCard.getSerial(), Constants.TAG, checkCard.getStatus());
				try {
					if (status == Constants.PASS_UNDEFINED) {
						displayRejectMessage(getString(R.string.pass_undefined));
					} else {
						if (dbAdapter.getOtipass(checkCard.getNumOtipass()) != null) { 
							dbAdapter.updateOtipass(checkCard);
							Log.d("service_check", checkCard.getService());
						} else {
							dbAdapter.insertOtipassObject(checkCard);
						}
						String day_use = checkCard.getUseDay();
						List<Usage> useList = new ArrayList<Usage>();
						if (day_use.length() > 0) {
							String [] use_day = day_use.split(";");
							for (int j = 0; j < use_day.length; j++) {
								useList.add(new Usage((int)checkCard.getNumOtipass(), String.valueOf(Integer.valueOf(use_day[j].toString()))));
							}
						}
						for (int j = 0; j < useList.size(); j++) {
							Usage use = new Usage();
							use = useList.get(j);
							int nb_use = dbAdapter.isUsePassExists(use.getNumOtipass(), use.getDate());
							if (nb_use == 0) {
								dbAdapter.insertUsage(use);
							}
						}
						pref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
						String scan_type = pref.getString(Constants.SCAN_TYPE_KEY, null);
						if (scan_type.equals(Constants.SCAN_SALE)) {
							if (status == Constants.PASS_CREATED) {
								controlPass(card);
							} else {
								rejectCardOnSale(card, checkCard);
							}
						} else {
							if (status == Constants.PASS_EXPIRED) {
                                title = getString(R.string.pass_status, checkCard.getNumOtipass(), getString(R.string.OTI_EXPIRED));
								rejectCardOnEntry(card, getString(R.string.pass_expired, otipass.getExpiry()), "");
							}else if (status == Constants.PASS_INVALID) {
                                title = getString(R.string.pass_status, checkCard.getNumOtipass(), getString(R.string.OTI_INVALID));
								rejectCardOnEntry(card, getString(R.string.pass_invalid_blocked), ""); 
							}else if (status == Constants.PASS_CREATED) {
                                title = getString(R.string.pass_status, checkCard.getNumOtipass(), getString(R.string.OTI_UNDEFINED));
								acceptCardOnEntry(card, getString(R.string.type_pass_unkown), true, title);
							}else {
								controlPass(card);
							}
						} 
					}
				} catch (Exception e) {
					Log.e(Constants.TAG, HomeActivity.class.getName() + " - onPostExecute - " + e.getMessage());
				}
			} else {
				displayRejectMessage(getString(R.string.pass_undefined));
			}
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			try {
				String contents = scanResult.getContents();
				card = new OtipassCard(Integer.valueOf(contents), "", Constants.TAG, OtipassCard.cOtipassCard);
				controlPass(card);
			} catch (Exception ex) {
				tools.showAlert(this, getString(R.string.pass_adt_ko), tools.cWarning);
			}
		}

	}

}