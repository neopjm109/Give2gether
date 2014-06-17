package com.example.giv2gether;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

public class GoogleLoginActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = "ExampleActivity";
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_login);

		mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				"http://schemas.google.com/AddActivity",
				"http://schemas.google.com/BuyActivity").build();

		 // 연결 실패가 해결되지 않은 경우 표시되는 진행률 표시줄입니다.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");

		if (mConnectionResult == null) {
			mConnectionProgressDialog.show();
		} else {
			try {
				mConnectionResult.startResolutionForResult(
						GoogleLoginActivity.this, REQUEST_CODE_RESOLVE_ERR);

			} catch (SendIntentException e) {
				mConnectionResult = null;
				mPlusClient.connect();
			}
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			// 사용자가 이미 로그인 버튼을 클릭했습니다. 해결을 시작합니다.
            // 연결 오류가 발생했습니다. onConnected()가 연결 대화상자를 닫을 때까지
            // 기다립니다.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}

		 // 결과를 저장하고 사용자가 클릭할 때 연결 실패를 해결합니다.
		mConnectionResult = result;

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		 // 연결 오류를 해결했습니다.
		mConnectionProgressDialog.dismiss();

		TelephonyManager telManager = (TelephonyManager) this
				.getSystemService(this.TELEPHONY_SERVICE);

		String accountEmail = mPlusClient.getAccountName();
		String accountName = mPlusClient.getCurrentPerson().getName().getGivenName().toString();
		String accountPhone = telManager.getLine1Number();
		String accountBirth = mPlusClient.getCurrentPerson().getBirthday();

		Toast.makeText(
				this,
				accountEmail + " is connected. " + accountName + accountPhone
						+ accountBirth, Toast.LENGTH_LONG).show();

		Intent intent = new Intent(this, SignupProcActivity.class);
		intent.putExtra("email", accountEmail);
		intent.putExtra("name", accountName);
		intent.putExtra("phone", accountPhone);
		intent.putExtra("birth", accountBirth);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

}
