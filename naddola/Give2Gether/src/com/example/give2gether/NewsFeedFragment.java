package com.example.give2gether;

import java.util.ArrayList;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
		
		feedlist = (ArrayList<String>) mActivity.getIntent().getSerializableExtra("feedlist");
		
		noFeed = (TextView) rootView.findViewById(R.id.noFeed);
		feedListView = (ListView) rootView.findViewById(R.id.feedList);
		
		mAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), android.R.layout.simple_list_item_1, feedlist) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);
				
				((TextView) view).setTextColor(Color.BLACK);
				
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					}
				});
				
				return view;
			}
			
			
			
		};
		
		feedListView.setAdapter(mAdapter);
		
		if (feedlist.size() == 0) {
			noFeed.setVisibility(View.VISIBLE);
		} else {
			noFeed.setVisibility(View.INVISIBLE);
		}

		if (feedlist.size() > 0) {
			noFeed.setVisibility(View.INVISIBLE);
		}
		
//		selectFriendsAll();
		/*
		try {
			JSONObject jObj = new AsyncCheckNewsFeed().execute("http://naddola.cafe24.com/getNewsFeedDDays.php").get();
			JSONArray feed = jObj.getJSONArray("feed");
			
			
			for(int i=0; i<feed.length(); i++) {
				JSONObject c = feed.getJSONObject(i);
				
				String name = c.getString("name");
				String phone = c.getString("phone");
				String birth = c.getString("birth");
				int dday = c.getInt("dday");
				
				for(int j=0; j<arrMyFriendList.size(); j++) {
					if (phone.equals(arrMyFriendList.get(j).getPhone())) {
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
		*/
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
			
			MyFriend mf = new MyFriend(id, name, email, phone, birth, sign, "");
			arrMyFriendList.add(mf);			
			
			result.moveToNext();
		}

		result.close();
	}
	
}
