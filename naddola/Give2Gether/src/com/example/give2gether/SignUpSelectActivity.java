package com.example.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SignUpSelectActivity extends Activity implements OnClickListener {

	// LoginButton bt_FacebookLogin;
	Button btnFacebookLogin, btnGoogleLogin, btngiv2gether;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signupselect);

		initViews();
	}

	public void initViews() {
		StartActivity.mActivityManager.add(this);
		btnFacebookLogin = (Button) findViewById(R.id.signup_facebookButton);
		btnGoogleLogin = (Button) findViewById(R.id.signup_googleButton);
		btngiv2gether = (Button) findViewById(R.id.signup_Give2Gether);

		btnFacebookLogin.setOnClickListener(this);
		btnGoogleLogin.setOnClickListener(this);
		btngiv2gether.setOnClickListener(this);
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
