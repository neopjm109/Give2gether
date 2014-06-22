package com.example.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity implements OnClickListener{
	
	static SettingPreference setting;
	
	Button btnSignup;
	Button btnLogin;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		/*
		 * 		AutoLogin
		 */
		setting = new SettingPreference(this);
		if(setting.isLogin()){
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			startActivity(intent);
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
