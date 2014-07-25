package com.example.give2gether;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EventMessageActivity extends Activity {
	
	TextView eventName, eventCongratulation;
	EditText et_eventMessage;
	Button sendMessage;
	
	Intent intent;
	String BrithEmail, name, StarterEmail, Message;
	int webId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_message);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		
		initIntent();
		initViews();
	}
	
	public void initIntent() {
		intent = getIntent();
		
		BrithEmail = intent.getStringExtra("email");
		StarterEmail = new SettingPreference(this).getID();
		name = intent.getStringExtra("name");
		webId = intent.getIntExtra("webId", 0);
	}
	
	public void initViews() {
		eventName = (TextView) findViewById(R.id.eventMessageFriendsName);
		eventCongratulation = (TextView) findViewById(R.id.eventCongratulation);
		et_eventMessage = (EditText) findViewById(R.id.eventMessage);
		et_eventMessage.setPadding(30, 30, 30, 30);
		
		sendMessage = (Button) findViewById(R.id.sendMessage);
		sendMessage.setHint(name+"님의 친구들에게 보낼 메세지를 입력하세요.");
		
		eventName.setText(name);
		
		sendMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message = et_eventMessage.getText().toString();
				new AsyncUpdateEventWish(webId).execute();
			}
		});
	}

	class AsyncUpdateEventWish extends AsyncTask<String, Void, Void> {
		int id;
		
		public AsyncUpdateEventWish(int id) {
			this.id = id;
		}
		
		protected Void doInBackground(String... params) {
			
			try {
				/* Update event column */
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/updateWishEvent.php";
				
				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("id", id+""));
				params2.add(new BasicNameValuePair("event", 1+""));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				client.execute(post);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			new AsyncInsertEventWish(webId).execute();
		}
		
	}

	class AsyncInsertEventWish extends AsyncTask<String, Void, Void> {
		int id;
		
		public AsyncInsertEventWish(int id) {
			this.id = id;
		}
		
		protected Void doInBackground(String... params) {
			
			try {
				/* Update event column */
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/insertEventWish.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("wish_id", id+""));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			new AsyncPushEventFriend(BrithEmail, StarterEmail, Message).execute();
		}
	}
	
	class AsyncPushEventFriend extends AsyncTask<String, Void, String> {
		String BirthEmail;
		String StarterEmail;
		String Message;
		
		public AsyncPushEventFriend(String BirthEmail, String StarterEmail, String Message) {
			this.BirthEmail = BirthEmail;
			this.StarterEmail = StarterEmail;
			this.Message = Message;
		}
		
		protected String doInBackground(String... params) {
			
			try {
				/* Update event column */
				HttpClient client = new DefaultHttpClient();
				String postUrl;

//				postUrl = "http://naddola.cafe24.com/gcmtest02.php";
				postUrl = "http://naddola.cafe24.com/pushEventGeneration.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("BirthEmail", BirthEmail));
				params2.add(new BasicNameValuePair("StarterEmail", StarterEmail));
				params2.add(new BasicNameValuePair("Message", Message));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("naddola", "BirthEmail : " + BirthEmail+result);
			finish();
		}
		
	}
}
