package com.example.give2gether.async;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class getSignedFriendHttpPostAsyncTask extends
		AsyncTask<String, Integer, JSONObject>

{
	@Override
	protected JSONObject doInBackground(String... params) {
		String name = params[0];
		String email = params[1];
		String phone = params[2];
		String birth = params[3];

		try {
			HttpClient client = new DefaultHttpClient();
			String postUrl;

			postUrl = "http://naddola.cafe24.com/checkSignedMember.php";

			HttpPost post = new HttpPost(postUrl);

			// 전달인자
			List params2 = new ArrayList();
			params2.add(new BasicNameValuePair("phone", phone));

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
					HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePost = client.execute(post);
			HttpEntity resEntity = responsePost.getEntity();

			if (resEntity != null) {
				String resp = EntityUtils.toString(resEntity);

				if (resp.equals("false")) {
					return null;
				} else {
					// Log.i(TAG, resp);
					JSONObject jobj;
					try {
						jobj = new JSONObject(resp);
						return jobj;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
