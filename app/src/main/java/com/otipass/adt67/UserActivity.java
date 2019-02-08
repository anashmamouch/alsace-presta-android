/**
================================================================================

    PASS MUSEUM project

    Package com.otipass.passmuseum

    @copyright Otipass 2013. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 3780 $
    $Id: UserActivity.java 3780 2014-07-01 07:26:21Z ede $

================================================================================
 */

package com.otipass.adt67;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.otipass.adt67.R;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Param;
import com.otipass.sql.User;
import com.otipass.tools.tools;

@SuppressLint("NewApi")
public class UserActivity extends Activity {
	private MuseumDbAdapter dbAdapter;
	private long idUser;
	private TextView tvError;
	private User user;
	private boolean mpm_installed = false, mulhouse_installed = false;

	private boolean checkUser(String login, String clearPwd) {
		boolean checked = false;
		String cipheredPwd, salt;
		user = dbAdapter.getUserByLogin(login);
		if (user != null) {
			cipheredPwd = user.getPassword();
			salt = user.getSalt();
			checked = tools.checkMD5(clearPwd, salt, cipheredPwd);
			idUser = user.getId();
		}
		// dbAdapter.close();
		return checked;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		dbAdapter = new MuseumDbAdapter(this);
		dbAdapter.open();
		setTitle(R.string.app_name);
		Button connexion = (Button) findViewById(R.id.btn_Valid);
		tvError = (TextView) findViewById(R.id.error_text);
		tvError.setVisibility(TextView.INVISIBLE);

		connexion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String login, pwd;
				EditText loginField = (EditText) findViewById(R.id.login_admin);
				EditText pwdField = (EditText) findViewById(R.id.pwd_admin);
				login = loginField.getText().toString().trim();
				pwd = pwdField.getText().toString().trim();
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

				boolean ok = checkUser(login, pwd);

				// check user credentials
				if (ok) {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UserActivity.this);
					SharedPreferences.Editor editor = prefs.edit();
					// this is mismatched but difficult to correct, it refers to user profile and not provider category
					editor.putInt(Constants.CATEGORY_KEY, user.getProfile());
					editor.putInt(Constants.USER_KEY, (int)idUser);
					editor.commit();
					tvError.setVisibility(TextView.INVISIBLE);
					setResult((int)idUser);
					finish();
				} else {
					tvError.setVisibility(TextView.VISIBLE);
					tvError.setText(getString(R.string.Identifiants_incorrects));
					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				}
			}
		});
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

	}


	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		ActionBar bar = getActionBar();
		bar.setTitle(title);
		bar.setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_bg));

	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		try {
			menu.findItem(R.id.mpm).setVisible(mpm_installed);
			menu.findItem(R.id.mulhouse).setVisible(mulhouse_installed);
		}catch (Exception ex) {};
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.about:

			Param param = dbAdapter.getParam(1L);

			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

			try {
				dlgAlert.setMessage("v. "+getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName + "\n " + getString(R.string.otipass) + "-" + Constants.plateform + "\n" + getString(R.string.device, param.getName()));
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


	// prevent user to escape user connection
	@Override
	public void onBackPressed() {
	}

}
