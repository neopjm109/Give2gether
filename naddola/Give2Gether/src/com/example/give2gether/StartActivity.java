package com.example.give2gether;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener{
	
	static SettingPreference setting;
	ConnectivityManager cManager;
	NetworkInfo mobile;
	NetworkInfo wifi;
	
	Button btnSignup;
	Button btnLogin;
	
	public static ArrayList<Activity> mActivityManager = new ArrayList<Activity>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		/*
		 * 		AutoLogin
		 */
		setting = new SettingPreference(this);
		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if (wifi.isConnected() || mobile.isConnected()) {
			if(setting.isLogin()){
				Intent intent = new Intent(StartActivity.this, LoadingActivity.class);
				startActivity(intent);
				finish();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Check your network state", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		initViews();
	}
	
	public void initViews(){

		btnSignup = (Button)findViewById(R.id.btnSignUpStart);
		btnLogin = (Button)findViewById(R.id.btnLoginStart);

		btnSignup.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		mActivityManager.add(this);
		Intent intent;
		switch(v.getId()){

		case R.id.btnSignUpStart:
			intent = new Intent(StartActivity.this, SignUpSelectActivity.class);
			startActivity(intent);
			break;
			
		case R.id.btnLoginStart:
			intent = new Intent(StartActivity.this, LoginActivity.class);
			startActivity(intent);
			break;
			
		}
		
	}

}
