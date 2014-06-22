package com.hmjcompany.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

public class SignupGiv2gether extends Activity implements OnClickListener,
		OnDateChangedListener {
	
	public static String TAG = "naddola";

	Button bt_signup;

	EditText et_email;
	EditText et_name;
	DatePicker dp_birth;
	String StringBirth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_giv2gether_signup);
		
		init();
	}

	public void init() {
		bt_signup = (Button) findViewById(R.id.signup_button);
		et_email = (EditText) findViewById(R.id.signup_email);
		et_name = (EditText) findViewById(R.id.signup_name);
		dp_birth = (DatePicker) findViewById(R.id.signup_birth);

		bt_signup.setOnClickListener(this);
		dp_birth.init(dp_birth.getYear(), dp_birth.getMonth(),
				dp_birth.getDayOfMonth(), this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.signup_button:
			
			TelephonyManager telManager = (TelephonyManager) this
			.getSystemService(this.TELEPHONY_SERVICE);

			
			String email = et_email.getText().toString();
			String name = et_name.getText().toString();
			String phone = telManager.getLine1Number();
			String birth = StringBirth; 
			
			checkEmail();
			
			Intent intent = new Intent(SignupGiv2gether.this, SignupProcActivity.class);
			intent.putExtra("email", email);
			intent.putExtra("name", name);
			intent.putExtra("phone", phone);
			intent.putExtra("birth", birth);
			startActivity(intent);
			
			Log.i(TAG, "SignupGiv2gether - email:"+email+"  name:" + name+"  phone:"+phone+"  birth:"+birth);
			break;
		}

	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		StringBirth = String.format("%d-%d-%d 00:00:00", year,monthOfYear+1,dayOfMonth);
	}
	
	public void checkEmail(){
		
	}

}
