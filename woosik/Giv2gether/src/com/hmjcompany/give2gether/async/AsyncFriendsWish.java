package com.hmjcompany.give2gether.async;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncFriendsWish extends AsyncTask<String, String, JSONObject> {

	JSONObject jObj;
	
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
			
			if( !builder.toString().equals("[[]]") ) {
				jObj = new JSONObject(builder.toString());
			} else {
				jObj = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jObj;
		
	}

}
