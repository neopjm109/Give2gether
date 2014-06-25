package com.hmjcompany.give2gether;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	EditText loginEmail, loginPw;
	Button btnLogin, btnRegister;
	
	TextView findPw;
	
	JSONArray member;
	JSONObject jObj;
	
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
			new JsonParse().execute("http://naddola.cafe24.com/member.php?email="+loginEmail.getText().toString()+"&password="+loginPw.getText().toString());
			break;
			
		case R.id.btnRegister:
			intent = new Intent(LoginActivity.this, SignUpSelectActivity.class);
			startActivity(intent);
			break;
			
		case R.id.findPw:
			break;
		}
		
	}

	/*
	 * 		JsonParse
	 * 		- Using a Naver's open api, we get a data that is member information. 
	 */
	
	private class JsonParse extends AsyncTask<String, String, JSONObject> {
		
		protected JSONObject doInBackground(String... params) {
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(params[0]);
			
			try {
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity httpEntity = response.getEntity();
				InputStream is = httpEntity.getContent();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder builder = new StringBuilder();
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}
				
				is.close();
				
				jObj = new JSONObject(builder.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return jObj;
			
		}

		protected void onPostExecute(JSONObject result) {
			
			String chkPassword = "";
			
			try {
				member = result.getJSONArray("member");
				
				if (member.length() > 0) {
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), loginPw.getText().toString() + "!=" + chkPassword, Toast.LENGTH_SHORT).show();
				}
				
			} catch (Exception e) {
				
			}
			
		}

	}
	
}
