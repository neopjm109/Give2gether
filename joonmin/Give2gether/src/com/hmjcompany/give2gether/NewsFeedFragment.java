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

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsFeedFragment extends Fragment {

	/*
	 * 		Views
	 */
	
	MainActivity mActivity;
	Giv2DBManager dbManager;
	
	View rootView;
	TextView noFeed;
	ListView feedListView;
	ArrayAdapter<String> mAdapter;
	ArrayList<String> feedlist;
	
	ArrayList<MyFriend> arrMyFriendList;
	
	JSONObject jObj = null;
	JSONArray feed = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_newsfeed, container, false);
		
		initViews();
		setHasOptionsMenu(true);
		
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem item1 = menu.add(0, 0, 0, "All Delete");
		item1.setIcon(android.R.drawable.ic_menu_edit);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	
	public void initViews() {
		arrMyFriendList = new ArrayList<MyFriend>();
		feedlist = new ArrayList<String>();
		
		mActivity = (MainActivity) getActivity();
		dbManager = mActivity.getDBManager();
		
		noFeed = (TextView) rootView.findViewById(R.id.noFeed);
		feedListView = (ListView) rootView.findViewById(R.id.feedList);
		
		mAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), android.R.layout.simple_list_item_1, feedlist) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);
				
				((TextView) view).setTextColor(Color.BLACK);
				
				return view;
			}
			
		};
		
		feedListView.setAdapter(mAdapter);
		
		if (feedlist.size() == 0) {
			noFeed.setVisibility(View.VISIBLE);
		} else {
			noFeed.setVisibility(View.INVISIBLE);
		}
		
		selectFriendsAll();
		
		new AsyncCheckNewsFeed().execute("http://naddola.cafe24.com/getNewsFeedDDays.php");
	}
	
	/*
	 * 		DB Function
	 */
	
	public void selectFriendsAll() {
		Cursor result = dbManager.selectFriendsAll();

		arrMyFriendList.clear();
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			int id = result.getInt(0);
			String name = result.getString(1);
			String email = result.getString(2);
			String phone = result.getString(3);
			String birth = result.getString(4);
			int signed = result.getInt(5);
			boolean sign = false;
			
			if (signed == 1) {
				sign = true;
			}
			
			Log.i("PJM", id+" "+name+" "+email+" "+phone+" "+birth+" "+signed);
			
			MyFriend mf = new MyFriend(id, name, email, phone, birth, sign, "");
			arrMyFriendList.add(mf);			
			
			result.moveToNext();
		}

		result.close();
	}
	
	class AsyncCheckNewsFeed extends AsyncTask<String, Void, JSONObject> {

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
				feed = result.getJSONArray("feed");
				
				
				for(int i=0; i<feed.length(); i++) {
					JSONObject c = feed.getJSONObject(i);
					
					String name = c.getString("name");
					String phone = c.getString("phone");
					String birth = c.getString("birth");
					int dday = c.getInt("dday");
					
					for(int j=0; j<arrMyFriendList.size(); j++) {
						if (phone.equals(arrMyFriendList.get(j).getPhone())) {
							Log.i("PJM", arrMyFriendList.get(j).getPhone()+" "+phone+" "+dday+"");
							
							if (dday < 16)
								feedlist.add(arrMyFriendList.get(j).getName()+"님의 생일이 " + dday + "일 전입니다");
						}
					}
				}

				if (feedlist.size() > 0) {
					noFeed.setVisibility(View.INVISIBLE);
					mAdapter.notifyDataSetChanged();
				}
				
			} catch (Exception e) {
				
			}
		}
		
	}
}
