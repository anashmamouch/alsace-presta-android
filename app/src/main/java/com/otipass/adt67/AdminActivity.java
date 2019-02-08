package com.otipass.adt67;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.otipass.adt67.R;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Param;
import com.otipass.synchronization.SynchronizationService;
import com.otipass.tools.admin;
import com.otipass.tools.tools;

@SuppressLint("NewApi")
public class AdminActivity extends Activity{
	private static final String TAG = "Pass Alsace";
	private static final int cWifi = 1;
	private static final int c3G = 2;
	private static final int cEthernet = 3;
	private static final int cTablet = 1;
	private static final int cSmartphone = 2;

	final Context context = this;
	int providerId = 0;
	int comStatus;
	Activity adminActivity = this;
	private Footer footer = null;
	private TextView tvError;
	private MuseumDbAdapter dbAdapter;

	private boolean checkUser(String clearPwd) {
		boolean checked = false;
		String cipheredPwd = admin.getPwd();
		String salt = admin.getSalt();
		checked = tools.checkMD5(clearPwd, salt, cipheredPwd);
		return checked;
	}

	private void init() {
		boolean connected;
		int connectionType = 0;
		int deviceType = 0;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			// detection WiFi network
			if (mWifi != null && mWifi.isAvailable()) {
				connectionType = cWifi;
			} else {
				// detection 3G network
				NetworkInfo mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if (mMobile != null && mMobile.isAvailable()) {
					connectionType = c3G;
				} else {
					//detection Ethernet ?? needs to be checked
					connectionType = cEthernet; 
				}
			}
			// detection tablet or smartphone
			boolean isTablet = tools.isTablet();
			if (isTablet) {
				deviceType = cTablet;
			} else {
				deviceType = cSmartphone;
			}

			// connection detection
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			connected = (networkInfo != null) && networkInfo.isAvailable() &&  networkInfo.isConnected();

			if (connected) {

				footer.startInitSequence(providerId, deviceType, connectionType);
				new Thread() {
					@Override
					public void run() {
						int status;
						try {
							do {
								sleep(1000);
							} while ((status = footer.getInitSequenceStatus()) == SynchronizationService.cComPending);

							if (status == SynchronizationService.cComOK) {
								setResult(Activity.RESULT_OK);
								finish();
							}
						} catch (Exception e) {}
					}
				}.start();


			} else {
				tools.showAlert(AdminActivity.this, getString(R.string.aucune_connexion_disponible), tools.cError);
			}
		} catch (Exception e) {
			Log.e(TAG, SynchronizationService.class.getName() + " - isOnline -" + e.getMessage());
		}
	}	

	private void initFooter(){
		footer = Footer.getInstance();
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.footer_frame, footer).commit();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		providerId = 0;
		comStatus = SynchronizationService.cComClientMethodFailure;
		setContentView(R.layout.activity_admin);
		setTitle(getString(R.string.app_name));
		initFooter();
		tvError = (TextView) findViewById(R.id.error_text);
		tvError.setVisibility(TextView.INVISIBLE);
		Button connexion = (Button) findViewById(R.id.btn_Valid);
		connexion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd, sId;
				EditText pwdField = (EditText) findViewById(R.id.pwd_admin);
				pwd = pwdField.getText().toString();
				EditText providerField = (EditText) findViewById(R.id.provider_id);
				sId = providerField.getText().toString();
				// check admin credentials
				if (checkUser(pwd)) {
					try {
						providerId = Integer.valueOf(sId);
					} catch (Exception e) {

					}
					if (providerId > 0) {
						tvError.setVisibility(TextView.INVISIBLE);
						init();
					} else {
						tvError.setVisibility(TextView.VISIBLE);
						tvError.setText(getString(R.string.Provider_id_incorrect));
					}
				} else {
					tvError.setVisibility(TextView.VISIBLE);
					tvError.setText(getString(R.string.Identifiants_incorrects));
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		try {
			menu.findItem(R.id.mpm).setVisible(false);
			menu.findItem(R.id.mulhouse).setVisible(false);
		}catch (Exception ex) {};
        return super.onPrepareOptionsMenu(menu);
    }


	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.about:
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

			try {
				dlgAlert.setMessage("v. " + getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName + "\n Otipass© 2015 -" + Constants.plateform + "\n");
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dlgAlert.setTitle(R.string.about);
			dlgAlert.setPositiveButton(R.string.Global_ok,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//dismiss the dialog  
				}
			});
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		ActionBar bar = getActionBar();
		bar.setTitle(title);
		bar.setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_bg));

	}

	// prevent user to escape admin session
	@Override
	public void onBackPressed() {
	}
}
