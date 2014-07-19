package com.hmjcompany.give2gether.async;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.os.AsyncTask;
import android.util.Log;

public class AsyncGetEventProgress extends AsyncTask<String, Void, JSONObject> {
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
			params2.add(new BasicNameValuePair("wish_id", params[0]));

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

}
