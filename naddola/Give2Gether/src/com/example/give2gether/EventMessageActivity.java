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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventMessageActivity extends Activity {
	
	TextView eventName, eventCongratulation, eventMessage;
	Button sendMessage;
	
	Intent intent;
	String email, name;
	int webId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_message);
		
		initIntent();
		initViews();
	}
	
	public void initIntent() {
		intent = getIntent();
		
		email = intent.getStringExtra("email");
		name = intent.getStringExtra("name");
		webId = intent.getIntExtra("webId", 0);
	}
	
	public void initViews() {
		eventName = (TextView) findViewById(R.id.eventMessageFriendsName);
		eventCongratulation = (TextView) findViewById(R.id.eventCongratulation);
		eventMessage = (TextView) findViewById(R.id.eventMessage);
		
		sendMessage = (Button) findViewById(R.id.sendMessage);
		
		eventName.setText(name);
		
		sendMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
			new AsyncPushEventFriend(email).execute();
		}
		
	}
	
	class AsyncPushEventFriend extends AsyncTask<String, Void, String> {
		String email;
		
		public AsyncPushEventFriend(String email) {
			this.email = email;
		}
		
		protected String doInBackground(String... params) {
			
			try {
				/* Update event column */
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				//postUrl = "http://naddola.cafe24.com/gcmtest02.php";
				postUrl = "http://naddola.cafe24.com/pushEventGeneration.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("email", email));

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
			Log.i("naddola", email+result);
			finish();
		}
	}
}
