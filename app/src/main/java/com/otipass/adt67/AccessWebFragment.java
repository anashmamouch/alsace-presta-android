package com.otipass.adt67;


import org.apache.http.util.EncodingUtils;

import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.User;
import com.otipass.tools.Callback.OnReturnButtonClickedListener;
import com.otipass.tools.tools;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class AccessWebFragment extends Fragment implements OnClickListener{

	private Button btn_return;
	private Fragment fragment;
	private FragmentManager fragmentManager;
	private Bundle bundle;
	private String url, webAction, action, controller;
	private MuseumDbAdapter dbAdapter;
	
    // listener for callback events
    private OnReturnButtonClickedListener mReturnListener;

    // attach callbacks to the calling activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	mReturnListener = (OnReturnButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnContinueButtonClickedListener");
        }
    }
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_web_access, container, false);
		final RelativeLayout l1 = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
		// get fragment manager
		fragmentManager = getFragmentManager();
		
		btn_return = (Button) rootView.findViewById(R.id.Btn_return);
		btn_return.setOnClickListener(this);

		// webView
		WebView wv = (WebView) rootView.findViewById(R.id.webView);
		WebSettings webSettings = wv.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true); 
		webSettings.setBuiltInZoomControls(true);
		wv.setWebViewClient(new WebViewClient());
		
		wv.setWebViewClient(new WebViewClient() {

			   public void onPageFinished(WebView view, String url) {
			        // do your stuff here
				   l1.setVisibility(RelativeLayout.GONE);
			    }
			});		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int idUser = prefs.getInt(Constants.USER_KEY, 0);
		String domain = tools.getDomain(getActivity());
		String url = Constants.ADTPlateform + "." + domain + "/ot/console/alsace/auth/viamobile/lang";

		bundle = getArguments();
		if (bundle != null) {
			webAction = bundle.getString(Constants.ACTION_KEY);
		}
		
		if (url.length() > 0) {
			switch (webAction)  {
			case Constants.WEB_ENTRY_ACTION:
				controller = "entry";
				action = "list";
				break;
			case Constants.WEB_SALE_ACTION:
				controller = "order";
				action = "list";
				break;
			case Constants.WEB_CMD_ACTION:
				controller = "stockcommand";
				action = "add";
				break;
			}
			dbAdapter = new MuseumDbAdapter(getActivity());
			dbAdapter.open();
			
			User user = dbAdapter.getUser((long)idUser);
			if (user != null) {
				String pwd = user.getPassword();
				String login = user.getUserid();
				
				String postData = "userid=" + login + "&password_=" + pwd + "&controller_=" + controller + "&action_=" + action;
				wv.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
			}
			
		}
		

		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_return.equals(v)) {
			mReturnListener.onReturnButtonClicked();

		}
	}
}
