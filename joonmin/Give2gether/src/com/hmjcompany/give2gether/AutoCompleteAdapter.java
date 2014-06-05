package com.hmjcompany.give2gether;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
	
	private ArrayList<String> listResult;
	JSONArray product = null;
	JSONObject jObj = null;

	public AutoCompleteAdapter(Context context) {
		super(context, android.R.layout.simple_dropdown_item_1line);
		// TODO Auto-generated constructor stub
		
		listResult = new ArrayList<String>();
	}
	
	public Filter getFilter() {
		Filter myFilter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// TODO Auto-generated method stub
				FilterResults filterResults = new FilterResults();
				
				if (constraint != null) {
					new JsonParse().execute("http://webheavens.com/suggestion.php?name=" + constraint);
					filterResults.values = listResult;
					filterResults.count = listResult.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// TODO Auto-generated method stub
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
			
		};
		
		return myFilter;
	}
	private class JsonParse extends AsyncTask<String, String, JSONObject> {

		JSONObject jObj = null;
		
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
				product = result.getJSONArray("results");
				
				listResult.clear();
				
				for(int i=0; i<product.length(); i++) {
					JSONObject c = product.getJSONObject(i);
					listResult.add(c.getString("name"));
				}
				Log.i("PJM", listResult.get(0));

			} catch (Exception e) {
				
			}
		}
				
	}
}
