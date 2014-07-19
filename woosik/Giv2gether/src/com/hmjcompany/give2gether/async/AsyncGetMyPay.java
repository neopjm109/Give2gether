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

public class AsyncGetMyPay extends AsyncTask<String, Void, Integer> {

	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			HttpClient client = new DefaultHttpClient();
			String postUrl;

			postUrl = "http://naddola.cafe24.com/getMyPay.php";
			
			HttpPost post = new HttpPost(postUrl);

			// 전달인자
			List params2 = new ArrayList();
			params2.add(new BasicNameValuePair("wish_id", params[0]));
			params2.add(new BasicNameValuePair("email", params[1]));

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
					HTTP.UTF_8);
			post.setEntity(ent);

			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();

			if (httpEntity != null) {
				String resp = EntityUtils.toString(httpEntity);
				return Integer.parseInt(resp);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
