package com.otipass.adt67;

import models.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.otipass.tools.tools;

public class AccessWebActivity extends Activity implements OnClickListener{

	private WebView wv;
	private Button btn_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_access);

		btn_return = (Button) findViewById(R.id.btn_return);
		btn_return.setOnClickListener(this);

		wv = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = wv.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true); 
		webSettings.setBuiltInZoomControls(true);
		wv.setWebViewClient(new WebViewClient());

		String domain = tools.getDomain(AccessWebActivity.this);
		String url = Constants.ADTPlateform + "." + domain + "/ot/console/alsace/pass/sale";
		if (tools.isNetworkAvailable(AccessWebActivity.this))
			wv.loadUrl(url);
		else {
			Toast.makeText(getApplicationContext(), R.string.no_available_connexion, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btn_return.equals(v)) {
			finish();
		}
	}
}
