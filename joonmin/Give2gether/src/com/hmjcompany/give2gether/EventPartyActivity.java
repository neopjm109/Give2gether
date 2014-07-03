package com.hmjcompany.give2gether;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EventPartyActivity extends Activity {

	TextView friendName, eventDday, wishTitle, wishPrice, eventProgressText;
	EditText payPresent;
	ImageView wishImage;
	Button btnPresent;
	ProgressBar eventProgress;
	
	Intent intent;
	String name, title, imagePath;
	int wish, webId;
	
	SettingPreference setting;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_party);

		initIntent();		
		initViews();
	}
	
	public void initIntent() {
		intent = getIntent();
		name = intent.getStringExtra("name");
		title = intent.getStringExtra("title");
		wish = intent.getIntExtra("wish", 0);
		imagePath = intent.getStringExtra("imagePath");
		webId = intent.getIntExtra("webId", 0);
	}
	
	public void initViews() {
		setting = new SettingPreference(this);
				
		friendName = (TextView) findViewById(R.id.eventFriendsName);		
		wishTitle = (TextView) findViewById(R.id.eventTitle);
		wishPrice = (TextView) findViewById(R.id.eventPrice);
		wishImage = (ImageView) findViewById(R.id.eventImage);

		eventProgress = (ProgressBar) findViewById(R.id.eventProgress);
		eventProgressText = (TextView) findViewById(R.id.eventProgressText);
		payPresent = (EditText) findViewById(R.id.editEventPay);
		
		btnPresent = (Button) findViewById(R.id.btnEventPresent);
		btnPresent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AsyncPresentEvent().execute();
			}
		});

		friendName.setText(name);
		wishTitle.setText(title);
		wishPrice.setText(wish+" Wish");
		eventProgress.setMax(wish);
		
		new AsyncGetEventProgress().execute();
		
		new ImageThread().execute(imagePath);
	}
	
	class AsyncPresentEvent extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;
	
				postUrl = "http://naddola.cafe24.com/insertEventPay.php";
				
				HttpPost post = new HttpPost(postUrl);
	
				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("wish_id", webId+""));
				params2.add(new BasicNameValuePair("email", setting.getID()));
				params2.add(new BasicNameValuePair("pay", payPresent.getText().toString()));
				
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				client.execute(post);
				
			} catch (Exception e) {
				
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			finish();
		}	
		
	}
	
	class AsyncGetEventProgress extends AsyncTask<String, Void, JSONObject> {
		JSONObject jObj;
		JSONArray pays;
		
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/getEventProgressPay.php";
				
				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("wish_id", webId+""));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);

				HttpResponse response = client.execute(post);
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

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			int id = 0;
			int pay = 0;
			try {
				pays = result.getJSONArray("pay");
				
				for(int i=0; i<pays.length(); i++) {
					JSONObject c = pays.getJSONObject(i);
					id = c.getInt("id");
					pay = c.getInt("pay");
				}
			} catch (Exception e) {
				
			}
			eventProgress.setProgress(pay);
			
			eventProgressText.setText( (100 * pay)/wish + "% (" + pay + " Wish / " + wish + " Wish)");
		}
		
		
	}

	// 		Get a Image by url
	class ImageThread extends AsyncTask<String, Void, Bitmap> {
		
		Bitmap bmp;
		
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				bmp = BitmapFactory.decodeStream(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return bmp;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);

			if (result != null)
				wishImage.setImageBitmap(result);
			else
				wishImage.setImageResource(R.drawable.ic_launcher);

		}
	}

}
