package com.hmjcompany.give2gether;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddWishActivity extends Activity {

	/*
	 * 		Views
	 */
	
	AutoCompleteTextView editTitle;
	EditText editPrice;
	Button btnAdd;
	
	/*
	 * 		Variables
	 */
	
	ArrayList<String> searchList;
	ArrayAdapter<String> sAdapter;
	JSONArray product = null;
	JSONObject jObj = null;
	
	String url = "http://neopjm109.cafe24.com/shopJson.php?query=";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wish);
		
		initViews();
	}
	
	public void initViews() {
		searchList = new ArrayList<String>();
				
		editTitle = (AutoCompleteTextView) findViewById (R.id.editTitle);
		
		editTitle.addTextChangedListener(new TextWatcher() {
			Timer timer = new Timer();
			final long delay = 500;
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT).show();

			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
				final String parseUrl = url + s.toString();
				
				timer.cancel();
				timer = new Timer();
				
				timer.schedule(new TimerTask() {

					public void run() {
						new JsonParse().execute(parseUrl);
					}
				}, delay);
			}
		});
		
		editPrice = (EditText) findViewById (R.id.editPrice);
		btnAdd = (Button) findViewById (R.id.btnAdd);
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if( editTitle.length() != 0 && editPrice.length() != 0) {
					Intent intent = new Intent();
					
					intent.putExtra("title", editTitle.getText().toString());
					intent.putExtra("price", editPrice.getText().toString());
					
					setResult(1001, intent);
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Put write", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private class JsonParse extends AsyncTask<String, String, JSONObject> {
		
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
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

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			try {
				product = result.getJSONArray("product");
				
				searchList.clear();
				sAdapter = null;
				
				for(int i=0; i<product.length(); i++) {
					JSONObject c = product.getJSONObject(i);
					String title = c.getString("title").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","");
					searchList.add(title);
				}

				sAdapter = new ArrayAdapter<String>(AddWishActivity.this, android.R.layout.simple_dropdown_item_1line, searchList);
				sAdapter.setNotifyOnChange(false);
				editTitle.setAdapter(sAdapter);
				editTitle.showDropDown();
				
				sAdapter.notifyDataSetChanged();
				Log.i("PJM", searchList.size() + " > " + searchList.get(0));

			} catch (Exception e) {
				
			}
		}
				
	}

}
