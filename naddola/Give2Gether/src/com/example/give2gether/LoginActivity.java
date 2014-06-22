package com.example.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener {

	EditText loginEmail, loginPw;
	Button btnLogin, btnRegister;
	
	TextView findPw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		initViews();
	}

	public void initViews() {
		loginEmail = (EditText) findViewById(R.id.loginEmail);
		loginPw = (EditText) findViewById(R.id.loginPw);
		
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		findPw = (TextView) findViewById(R.id.findPw);

		btnLogin.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent;
		
		switch(v.getId()) {
		case R.id.btnLogin:
			intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
			break;
			
		case R.id.btnRegister:
			intent = new Intent(LoginActivity.this, SignUpSelectActivity.class);
			startActivity(intent);
			break;
			
		case R.id.findPw:
			break;
		}
		
	}
	
}
