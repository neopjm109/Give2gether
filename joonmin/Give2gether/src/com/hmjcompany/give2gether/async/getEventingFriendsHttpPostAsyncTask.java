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

import com.hmjcompany.give2gether.MyFriend;

public class getEventingFriendsHttpPostAsyncTask extends
		AsyncTask<String, Integer, JSONArray> {
	JSONArray jFriendArr;
	ArrayList<MyFriend> arrGivFriendList;
	
	public getEventingFriendsHttpPostAsyncTask(ArrayList<MyFriend> list) {
		// TODO Auto-generated constructor stub
		this.arrGivFriendList = list;
	}

	@Override
	protected JSONArray doInBackground(String... params) {

		jFriendArr = new JSONArray();

		for (int i = 0; i < arrGivFriendList.size(); i++) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("email", arrGivFriendList.get(i).getEmail());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jFriendArr.put(obj);
		}

		try {
			HttpClient client = new DefaultHttpClient();
			String postUrl;

			postUrl = "http://naddola.cafe24.com/selectEventingFriendList.php";

			HttpPost post = new HttpPost(postUrl);

			// 전달인자
			List params2 = new ArrayList();
			params2.add(new BasicNameValuePair("friends", jFriendArr.toString()));

			// Log.i(TAG, "sendArray - " +jContactArr.toString());

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
					HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePost = client.execute(post);
			HttpEntity resEntity = responsePost.getEntity();

			if (resEntity != null) {
				String resp = EntityUtils.toString(resEntity);

				try {
					JSONArray jsonArr = new JSONArray(resp);
					return jsonArr;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (MalformedURLException e) {
			//
		} catch (IOException e) {
			//
		}
		return null;
	}
}
