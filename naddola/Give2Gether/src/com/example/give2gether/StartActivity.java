package com.example.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity implements OnClickListener{
	
	public static final String TAG = "naddola";
	
	static SettingPreference setting;
	
	Button bt_Signup;
	Button bt_Login;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		Log.v(TAG, this.getClass().toString());
		
		init();
	}
	
	public void init(){
		
		setting = new SettingPreference(this);
		if(setting.isLogin()){
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		
		bt_Signup = (Button)findViewById(R.id.start_signup_bt);
		bt_Login = (Button)findViewById(R.id.start_login_bt);
		
		bt_Signup.setOnClickListener(this);
		bt_Login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		Intent intent;
		ActivityReference.ActList.add(this);
		switch(v.getId()){
		
		case R.id.start_signup_bt:
			intent = new Intent(StartActivity.this, SignUpSelectActivity.class);
			startActivity(intent);
			break;
			
		case R.id.start_login_bt:
			break;
		}
		
	}

}
