package com.otipass.adt67;
import java.util.ArrayList;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Param;
import com.otipass.sql.ServicePass;
import com.otipass.sql.Tablet;
import com.otipass.tools.tools;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private static final int USER_ACTIVITY = 1;
	private static final int ADMIN_ACTIVITY = 2;
	private static final int HOME_ACTIVITY = 3;

	private static final int cIdle = 0; 
	private static final int cInitialized = 1;

	private MuseumDbAdapter dbAdapter;
	private SharedPreferences pref;
	private static int idUser;
	private static boolean pendingIdentification;

	private void checkApplication() {
		int status = cIdle;
		dbAdapter = new MuseumDbAdapter(this);
		dbAdapter.open();
		dbAdapter.databaseBackupSDCard();
		Param param = dbAdapter.getParam(1L);
		if (param != null) {
			if (param.getSoftwareVersion() != "") {
				status = cInitialized;
			}
			Tablet tablet = dbAdapter.getTablet(1L);
			if (tablet.getDbModel() > 1) {
				tools.setIsNewTariffOn(this, true);
			}
		}
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = pref.edit();

		// launch the default activity
		Intent intent = null;
		if (status == cIdle) {
			idUser = 0;
			editor.putInt(Constants.USER_KEY, (int)idUser);
			editor.commit();
			// call admin initialization activity
			intent = new Intent(MainActivity.this, AdminActivity.class);
			startActivityForResult(intent, ADMIN_ACTIVITY);
		} else {
			// the application is initialized
			idUser = pref.getInt(Constants.USER_KEY, 0);
			if (idUser > 0) {
				intent = new Intent(MainActivity.this, HomeActivity.class);
				//startActivity(intent);
				startActivityForResult(intent, HOME_ACTIVITY);
				
			} else {
				// not connected, launch user connection activity
				pendingIdentification = true;
				intent = new Intent(MainActivity.this, UserActivity.class);
				startActivityForResult(intent, USER_ACTIVITY);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		// retour en arrière sur déconnexion lors du démarrage, il vaut mieux rester comme ça
		if (intent != null ) {
			boolean calledFromExternPackage = intent.getBooleanExtra(Constants.EXTERN_CALL_KEY, false);
			pref = PreferenceManager.getDefaultSharedPreferences(this);
			idUser = pref.getInt(Constants.USER_KEY, 0);
			if (calledFromExternPackage) {
			} else {
				// user should not be connected when launching from scratch
				pref = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(Constants.USER_KEY, 0);
				editor.commit();
			}
		}
		// init the communication service state
		tools.setServiceState(this, tools.cIdle);
		tools.setPeriodicCall(this, false);
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
		if (!pendingIdentification) {
			// application main checks
			checkApplication();
		}
	}

	/*
	 * Call back functions for User and Admin activities
	 * 
	 * @param int requestCode
	 * @param int resultCode
	 * @param Intent data
	 * 
	 * @return void
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case (USER_ACTIVITY):{
			// returns the id of the connected user
			idUser = resultCode;
			pendingIdentification = false;
			checkApplication();
//			Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//			startActivityForResult(intent, HOME_ACTIVITY);

			break;
		}
		case (ADMIN_ACTIVITY):{
			if(resultCode == Activity.RESULT_OK){
				checkApplication();
			}
			break;
		}

		default:
			// return from home activity
			finish();
			break;
		}
	}
	@Override
	public void onNewIntent(Intent intent)
	{
        boolean calledFromExternPackage = intent.getBooleanExtra(Constants.EXTERN_CALL_KEY, false);
        if (calledFromExternPackage) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
            idUser = pref.getInt(Constants.USER_KEY, 0);
            checkApplication();
        }
	}

}
