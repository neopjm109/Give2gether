package com.example.giv2gether;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SignupProcActivity extends Activity {

	public static String TAG = "naddola";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		String Email = intent.getStringExtra("email");
		String Password = intent.getStringExtra("password");
		String Name = intent.getStringExtra("name");
		String Phone = intent.getStringExtra("phone");
		String Birth = intent.getStringExtra("birth");

		Log.v(TAG, "SignupProcActivity - email:" + Email + "  Name:" + Name
				+ "  Phone:" + Phone + "  Birth:" + Birth);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpPostAsyncTask task = new HttpPostAsyncTask();

		task.doInBackground(Email, Password, Name, Phone, Birth);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class HttpPostAsyncTask extends AsyncTask<String, Integer, Long>

	{
		@Override
		protected Long doInBackground(String... params) {
			String Email = params[0];
			String Password = params[1];
			String Name = params[2];
			String Phone = params[3];
			String Birth = params[4];

			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				//자체 가입
				if(Password != null)
					postUrl = "http://naddola.cafe24.com/insertMemberGiv2gether.php";
				
				//google, facebook 가입, 로그인  
				else
					postUrl = "http://naddola.cafe24.com/insertMemberSNS.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("email", Email));
				params2.add(new BasicNameValuePair("password", Password));
				params2.add(new BasicNameValuePair("name", Name));
				params2.add(new BasicNameValuePair("phone", Phone));
				params2.add(new BasicNameValuePair("birth", Birth));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();

				if (resEntity != null) {
					String resp = EntityUtils.toString(resEntity);
					
					if (resp.equals("SNS Login Success") ||
							resp.equals("Giv2gether Signup Success")) {
						SettingPreference setting = new SettingPreference(
								SignupProcActivity.this);
						setting.setAutoLoginTrue();
						setting.setID(Email);
						setting.setName(Name);
						Intent intent = new Intent(SignupProcActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					}else if(resp.equals("Signed email")){
						Toast.makeText(getApplicationContext(), "이미 가입된 email입니다.",
								Toast.LENGTH_LONG).show();
						finish();
					}
					else {
						Toast.makeText(getApplicationContext(), "회원가입 실패",
								Toast.LENGTH_LONG).show();
						finish();
					}
				}

			} catch (MalformedURLException e) {
				//
			} catch (IOException e) {
				//
			}
			return null;
		}
	}

}
