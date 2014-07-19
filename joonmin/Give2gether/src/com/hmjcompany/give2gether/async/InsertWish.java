package com.hmjcompany.give2gether.async;

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

import android.os.AsyncTask;

import com.hmjcompany.give2gether.AddWishActivity.SearchData;
import com.hmjcompany.give2gether.SettingPreference;

public class InsertWish extends AsyncTask<String, String, String> {
	SearchData myWishWeb;
	SettingPreference setting;
	String resp = null;
	
	public InsertWish(SearchData myWishWeb, SettingPreference setting) {
		this.myWishWeb = myWishWeb;
		this.setting = setting;
	}
	
	protected String doInBackground(String... params) {
		
		/*
		 * 		Insert
		 */
		try {
			HttpClient client = new DefaultHttpClient();
			String postUrl;

			postUrl = "http://naddola.cafe24.com/insertWish.php";
			
			HttpPost post = new HttpPost(postUrl);

			// 전달인자
			List params2 = new ArrayList();
			params2.add(new BasicNameValuePair("email", setting.getID()));
			params2.add(new BasicNameValuePair("title", myWishWeb.getTitle()));
			params2.add(new BasicNameValuePair("price", myWishWeb.getPrice()+""));
			params2.add(new BasicNameValuePair("wish", myWishWeb.getWish()+""));
			params2.add(new BasicNameValuePair("image", myWishWeb.getImagePath()+""));

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
					HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePost = client.execute(post);
			HttpEntity resEntity = responsePost.getEntity();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * 		Select
		 */
		try {
			HttpClient client = new DefaultHttpClient();
			String postUrl;

			postUrl = "http://naddola.cafe24.com/getLastMyWish.php";
			
			HttpPost post = new HttpPost(postUrl);

			// 전달인자
			List params2 = new ArrayList();
			params2.add(new BasicNameValuePair("email", setting.getID()));

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
					HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePost = client.execute(post);
			HttpEntity resEntity = responsePost.getEntity();
			
			if ( resEntity != null ) {
				resp = EntityUtils.toString(resEntity);	
			}

		} catch (Exception e) {
			e.printStackTrace();
		}			
		
		return resp;
	}

}
