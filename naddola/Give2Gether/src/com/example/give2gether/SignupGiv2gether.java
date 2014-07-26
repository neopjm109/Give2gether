package com.example.give2gether;

import java.util.ArrayList;

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
import android.widget.Toast;

public class SignupGiv2gether extends Activity implements OnClickListener,
		OnDateChangedListener {

	public static String TAG = "naddola";

	Button bt_signup;

	EditText et_email;
	EditText et_name;
	EditText et_password1;
	EditText et_password2;
	DatePicker dp_birth;
	String StringBirth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_giv2gether_signup);

		init();
	}

	public void init() {
		ArrayList<Activity> mActivityManager = StartActivity.mActivityManager;
		mActivityManager.add(this);
		bt_signup = (Button) findViewById(R.id.signup_button);
		et_email = (EditText) findViewById(R.id.signup_email);
		et_password1 = (EditText) findViewById(R.id.signup_password1);
		et_password2 = (EditText) findViewById(R.id.signup_password2);
		et_name = (EditText) findViewById(R.id.signup_name);
		dp_birth = (DatePicker) findViewById(R.id.signup_birth);
		
		et_email.setPrivateImeOptions("defaultInputmode=english;");
		et_password1.setPrivateImeOptions("defaultInputmode=english;");
		et_password2.setPrivateImeOptions("defaultInputmode=english;");

		bt_signup.setOnClickListener(this);
		dp_birth.init(dp_birth.getYear(), dp_birth.getMonth(),
				dp_birth.getDayOfMonth(), this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.signup_button:

			checkEmail();
			if (checkPassword().equals("true")) {

				TelephonyManager telManager = (TelephonyManager) this
						.getSystemService(this.TELEPHONY_SERVICE);

				String email = et_email.getText().toString();
				String password = et_password1.getText().toString();
				String name = et_name.getText().toString();
				String phone = telManager.getLine1Number();
				String birth = StringBirth;

				checkEmail();

				Intent intent = new Intent(SignupGiv2gether.this,
						SignupProcActivity.class);
				intent.putExtra("email", email);
				intent.putExtra("password", password);
				intent.putExtra("name", name);
				intent.putExtra("phone", phone);
				intent.putExtra("birth", birth);
				startActivity(intent);

				Log.i(TAG, "SignupGiv2gether - email:" + email + "  name:"
						+ name + "  phone:" + phone + "  birth:" + birth);
				break;
			}
			else{
				Toast.makeText(getApplicationContext(), checkPassword(), Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		StringBirth = String.format("%d-%d-%d 00:00:00", year, monthOfYear + 1,
				dayOfMonth);
	}

	public void checkEmail() {

	}

	public String checkPassword() {
		String pw1 = et_password1.getText().toString();
		String pw2 = et_password2.getText().toString();

		if (!pw1.equals(pw2)) {
			return "두개의 패스워드가 다릅니다. 확인해주세요.";
		} else if(pw1.length() < 6 || pw2.length() < 6 || pw1.length() > 18 || pw2.length() > 18){
			return "비밀번호가 길이가 맞지 않습니다. 6~18자 사이로 해주세요.";
		}
		else
			return "true";
	}
}
