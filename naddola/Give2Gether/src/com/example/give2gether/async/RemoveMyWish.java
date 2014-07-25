package com.example.give2gether.async;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

public class RemoveMyWish extends AsyncTask<String, String, Void> {
	int id;
	
	public RemoveMyWish(int id) {
		this.id = id;
	}
	
	protected Void doInBackground(String... params) {
		
		try {
			HttpClient client = new DefaultHttpClient();
			String postUrl;

			postUrl = "http://naddola.cafe24.com/removeMyWish.php";
			
			HttpPost post = new HttpPost(postUrl);

			// 전달인자
			List params2 = new ArrayList();
			params2.add(new BasicNameValuePair("id", id+""));

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
					HTTP.UTF_8);
			post.setEntity(ent);
			client.execute(post);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
