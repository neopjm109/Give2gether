package com.hmjcompany.give2gether.async;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.example.giv2gether.Giv2DBManager;
import com.example.giv2gether.MyFriend;
import com.example.giv2gether.SettingPreference;

public class AsyncTaskPostFriendList extends AsyncTask<String, Integer, Long> {

		public static final String TAG = "naddola";
		JSONArray jFriendArr;
		String BirthEmail;
		String EventMessage;
		SettingPreference setting;
		Giv2DBManager dbManager;

		public AsyncTaskPostFriendList(SettingPreference setting, Giv2DBManager db, String Message){
			this.setting = setting;
			this.dbManager = db;
			this.EventMessage = Message;
		}
		
		@Override
		public Long doInBackground(String... params) {

			BirthEmail = setting.getID();
			ArrayList<MyFriend> fl = dbManager.getGivFriendsList();
			jFriendArr = new JSONArray();

			for (int i = 0; i < fl.size(); i++) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("email", fl.get(i).getEmail());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jFriendArr.put(obj);
			}

			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/pushEventToFriends.php";

				HttpPost post = new HttpPost(postUrl);
				Log.i(TAG,"postFriend : "+EventMessage);
				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("BirthEmail", BirthEmail));
				params2.add(new BasicNameValuePair("EventMessage", EventMessage));
				params2.add(new BasicNameValuePair("friends", jFriendArr
						.toString()));

				//Log.i(TAG, "sendArray - " + jFriendArr.toString());

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();

				if (resEntity != null) {
					String resp = EntityUtils.toString(resEntity);

					Log.i(TAG, "post result : " + resp);
				}

			} catch (MalformedURLException e) {
				//
			} catch (IOException e) {
				//
			}
			return null;
		}
	}