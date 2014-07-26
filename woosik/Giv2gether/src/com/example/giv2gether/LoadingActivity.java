package com.example.giv2gether;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hmjcompany.give2gether.async.AsyncCheckNewsFeed;
import com.hmjcompany.give2gether.async.AsyncFriendsWish;
import com.hmjcompany.give2gether.async.AsyncTaskWPocketSynchronize;

public class LoadingActivity extends Activity {

	ImageView logo;
	ProgressBar progressBar;
	TextView loadingName;
	int progress;
	GivLoader loader;
	Giv2DBManager dbManager;
	
	SettingPreference settings;

	ArrayList<MyWish> arrMyWishList;
	ArrayList<MyFriend> arrMyFriendList;
	ArrayList<String> arrNewsFeedList;
	
	Thread timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		loader = new GivLoader();
		settings = new SettingPreference(this);
		dbManager = new Giv2DBManager(this);
		
		logo = (ImageView) findViewById(R.id.loadingLogo);
		progressBar = (ProgressBar) findViewById(R.id.loadingProgress);
		loadingName = (TextView) findViewById(R.id.loadingName);
		
		progressBar.setMax(100);
		progress = 0;

		arrMyFriendList = new ArrayList<MyFriend>();
		arrNewsFeedList = new ArrayList<String>();
	
		timer = new Thread() {
			public void run() {
				try {
					SyncWPocket();
					
					Thread.sleep(500);
					
					GetNewsFeed();

					Thread.sleep(500);
					
					GetFriendsWish();

					Thread.sleep(500);
					
				} catch(Exception e) {
					
				} finally {
					Intent i = new Intent(LoadingActivity.this, MainActivity.class);
					i.putExtra("feedlist", arrNewsFeedList);
					startActivity(i);
					
					LoadingActivity.this.finish();
				}
			}
		};
		
		Handler handler = new Handler();
		
		handler.postDelayed(new Runnable() {
			public void run() {
				
				timer.start();
				
			}
		}, 1000);

	}
	
	/*
	 * 		DB Function
	 */
	
	public boolean checkWishlistData(int webId) {
		Cursor result = dbManager.selectWishlistData(webId);
		if (result.getCount() > 0) {
			return true;
		} else {
			return false;
		}
		
	}

	public void insertWishlistData (int webId, String title, int price, int wish, String imagePath, String bookmark) {
		dbManager.insertWishlistData(webId, title, price, wish, imagePath, bookmark);
	}
	
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

	public void insertFWishlistData (int id, String phone, String title, int price, int wish, String date, String imagePath, String bookmark, String event) {
		dbManager.insertFWishlistData(id, phone, title, price, wish, date, imagePath, bookmark, event);
	}
	
	public boolean checkFWishlistData(int id) {
		Cursor result = dbManager.checkFWishlistData(id);
		
		if (result.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkFWishlistData(String phone) {
		Cursor result = dbManager.checkFWishlistData(phone);
		
		if (result.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void selectFWishAll() {
		Cursor result = dbManager.selectFWishAll();
		
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			
			int id = result.getInt(0);
			String phone = result.getString(1);
			String title = result.getString(2);
			int price = result.getInt(3);
			int wish = result.getInt(4);
			String eventOn = result.getString(5);
			String date = result.getString(6);
			String imagePath = result.getString(7);
			String bookmarkOn = result.getString(8);
			int webId = result.getInt(9);
			
			MyFriendsWish myFWish = new MyFriendsWish(id, phone, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);
			
			result.moveToNext();
		}
		
		result.close();
		
	}
	
	/*
	 * 		1. WPocket Synchronize
	 * 		2. Get News Feed
	 * 		3. Get Friends Wish
	 */
	
	public void SyncWPocket() {

		progressBar.setProgress(progress+=10);
		runOnUiThread(new Runnable() {
			public void run() {
				loadingName.setText("WPocket 동기화 중");
			}
		});
		
		try {
			JSONObject jObj = new AsyncTaskWPocketSynchronize().execute("http://naddola.cafe24.com/selectMyWishlist.php?email="+settings.getID()).get();
			JSONArray wishlist;
			
			wishlist = jObj.getJSONArray("wishlist");

			progressBar.setProgress(progress+=10);
			
			for(int i=0; i<wishlist.length(); i++) {
				JSONObject c = wishlist.getJSONObject(i);
				String title = c.getString("title");
				int price = c.getInt("price");
				int wish = c.getInt("wish");
				int bookmarkOn = c.getInt("bookmark");
				String bookmark;
				int eventOn = c.getInt("event");
				String date = c.getString("date");
				String imagePath = c.getString("image");
				int webId = c.getInt("id");
				
				if (bookmarkOn == 0) {
					bookmark = "false";
				} else {
					bookmark = "true";
				}
				
				if (!checkWishlistData(webId)) {
					insertWishlistData(webId, title, price, wish, imagePath, bookmark);
				}
				
			}
		} catch (Exception e) {
			
		}

		progressBar.setProgress(progress+=10);
	}
	
	public void GetNewsFeed() {

		progressBar.setProgress(progress+=10);
		runOnUiThread(new Runnable() {
			public void run() {
				loadingName.setText("뉴스피드 받는 중");
			}
		});
		selectFriendsAll();

		// 생일
		try {
			JSONObject jObj = new AsyncCheckNewsFeed().execute("http://naddola.cafe24.com/getNewsFeedDDays.php").get();
			JSONArray feed = jObj.getJSONArray("feed");

			progressBar.setProgress(progress+=10);
			
			for(int i=0; i<feed.length(); i++) {
				JSONObject c = feed.getJSONObject(i);
				
				String name = c.getString("name");
				String phone = c.getString("phone");
				String birth = c.getString("birth");
				int dday = c.getInt("dday");
				
				for(int j=0; j<arrMyFriendList.size(); j++) {
					if (phone.equals(arrMyFriendList.get(j).getPhone())) {
						if (dday < 16)
							arrNewsFeedList.add(name +"님의 생일이 " + dday + "일 전입니다");
					}
				}
				
			}
			
		} catch (Exception e) {
			
		}
		progressBar.setProgress(progress+=10);

		// 시작된 이벤트
		try {
			JSONObject jObj = new AsyncCheckNewsFeed().execute("http://naddola.cafe24.com/getNewsFeedEvent.php").get();
			JSONArray feed = jObj.getJSONArray("feed");

			progressBar.setProgress(progress+=10);
			
			for(int i=0; i<feed.length(); i++) {
				JSONObject c = feed.getJSONObject(i);

				String name = c.getString("name");
				String title = c.getString("title");
				String phone = c.getString("phone");
				int dday = c.getInt("dday");

				for(int j=0; j<arrMyFriendList.size(); j++) {
					if (phone.equals(arrMyFriendList.get(j).getPhone())) {
						if (dday > -3) {
							arrNewsFeedList.add(name + "님의 " + title + 
									" 이벤트가 시작되었습니다. (NEW)");
						}
					}
				}
				
			}
			
		} catch (Exception e) {
			
		}
		progressBar.setProgress(progress+=10);
	}
	
	public void GetFriendsWish() {

		progressBar.setProgress(progress+=10);
		runOnUiThread(new Runnable() {
			public void run() {
				loadingName.setText("친구 이벤트 위시 받는중");
			}
		});

		progressBar.setProgress(progress+=10);
		for(int k=0; k<arrMyFriendList.size(); k++) {

			final MyFriend mData = arrMyFriendList.get(k);
			
			try {
				JSONObject jObj = new AsyncFriendsWish().
						execute("http://naddola.cafe24.com/getFriendWish.php?phone="+mData.getPhone()).get();
				JSONArray friendsWish;
	
				if(jObj != null) {
					
					friendsWish = jObj.getJSONArray("wishlist");
	
					for(int i=0; i<friendsWish.length(); i++) {
						
						JSONObject c = friendsWish.getJSONObject(i);
						int id = c.getInt("id");
						String phone = c.getString("phone");
						String title = c.getString("title");
						int price = c.getInt("price");
						int wish = c.getInt("wish");
						int bookmarkOn = c.getInt("bookmark");
						String bookmark;
						int eventOn = c.getInt("event");
						String event;
						String date = c.getString("date");
						String imagePath = c.getString("image");
	
						if (bookmarkOn == 0) {
							bookmark = "false";
						} else {
							bookmark = "true";
						}
						
						if (eventOn == 0) {
							event = "false";
						} else {
							event = "true";
						}
						
						if (!checkFWishlistData(phone)) {
							insertFWishlistData(id, phone, title, price, wish, date, imagePath, bookmark, event);					
						}
	
					}
				}
				
			} catch (Exception e) {
				
			}	
		}

		progressBar.setProgress(progress+=10);
	}

}
