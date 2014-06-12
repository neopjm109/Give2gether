package com.example.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;

public class SignUpSelectActivity extends Activity implements OnClickListener {

	// LoginButton bt_FacebookLogin;
	Button bt_FacebookLogin;
	SignInButton bt_GoogleLogin;
	Button bt_giv2gether;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signupselect);

		init();
	}

	public void init() {
		bt_FacebookLogin = (Button) findViewById(R.id.signup_facebookButton);
		bt_GoogleLogin = (SignInButton) findViewById(R.id.signup_googleButton);
		bt_giv2gether = (Button) findViewById(R.id.signup_Give2Gether);

		bt_FacebookLogin.setOnClickListener(this);
		bt_GoogleLogin.setOnClickListener(this);
		bt_giv2gether.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		
		Intent intent;
		
		switch (view.getId()) {
		case R.id.signup_facebookButton:
			intent = new Intent(SignUpSelectActivity.this,
					FacebookLoginActivity.class);
			startActivity(intent);
			break;
			
		case R.id.signup_googleButton:
			intent = new Intent(SignUpSelectActivity.this, GoogleLoginActivity.class);
			startActivity(intent);
			break;
			
		case R.id.signup_Give2Gether:
			intent = new Intent(SignUpSelectActivity.this, SignupGiv2gether.class);
			startActivity(intent);
			break;

		}
	}
}
