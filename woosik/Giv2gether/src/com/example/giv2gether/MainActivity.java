package com.example.giv2gether;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity {

	/*
	 * 		TabLayout View & Variables
	 */
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	private String[] tabs = { "Main", "My Wishlist", "My Friends" };
	
	/*
	 * 		Database
	 */
	
	private static final String dbName = "give2gether.db";
	private static final String dbTableFriends = "friends";
	private static final String dbTableWishlist = "wishlist";
	
	private static final int dbMode = Context.MODE_PRIVATE;
	
	SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initTabs();

		createDatabase(dbName, dbMode);

//		removeTable();
		createTable();

	}
	
	public void initTabs() {
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(new ActionBar.TabListener() {
						
						public void onTabUnselected(Tab tab, FragmentTransaction ft) {
							
						}
						
						public void onTabSelected(Tab tab, FragmentTransaction ft) {
							viewPager.setCurrentItem(tab.getPosition());
						}
						
						public void onTabReselected(Tab tab, FragmentTransaction ft) {
							
						}
					}));
			
			viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				
				public void onPageSelected(int position) {
					actionBar.setSelectedNavigationItem(position);
				}
				
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				public void onPageScrollStateChanged(int arg0) {
					
				}
			});
			
		}
	}
	
	public void createDatabase(String dbName, int dbMode) {
		db = openOrCreateDatabase(dbName, dbMode, null);
	}
	
	public void createTable() {
		String sql = "create table if not exists " + dbTableFriends
				+ " (id integer primary key autoincrement,"
				+ " name text not null,"
				+ " email text not null,"
				+ " phone text not null,"
				+ " birth text)";
		db.execSQL(sql);

		sql = "create table if not exists " + dbTableWishlist
				+ " (id integer primary key autoincrement,"
				+ " title text not null,"
				+ " price integer not null,"
				+ " wish integer not null,"
				+ " event text not null,"
				+ " date text,"
				+ " image text not null,"
				+ " bookmark text not null)";
		
		db.execSQL(sql);
	}
	
	public void removeTable() {
		String sql = "drop table " + dbTableFriends;
		db.execSQL(sql);

		sql = "drop table " + dbTableWishlist;
		db.execSQL(sql);
	}
	
	/*
	 * 		DB Function
	 */
	
	// Friends
	public void insertFriendsData(String name, String email, String phone, String birth) {
		String fName = name;
		String fEmail = email;
		String fPhone = phone;
		
		DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy", Locale.KOREAN);
		Date fBirth = null;
		
		try {
			fBirth = df.parse(birth);
		} catch (Exception e) {
			
		}
		
		String sql = "insert into " + dbTableFriends
				+ " values(NULL, '"
				+ fName + "', '"
				+ fEmail + "', '"
				+ fPhone + "', '"
				+ fBirth + "');";
		
		db.execSQL(sql);
		
	}
	
	public void selectFriendsData(String tableName, int id) {
		String sql = "select " + tableName + "where id='" + id + "';";
		db.execSQL(sql);
	}
	
	public void updateFriendsData() {
		
	}
	
	public void removeFriendsData() {
		
	}
	
	// Wishlist
	public void insertWishlistData(String title, int price, int wish, String imagePath) {
		String wTitle = title;
		int wPrice = price;
		int wWish = wish;
		String wImagePath = imagePath;
		String wEvent = "false";
		String wBookmark = "false";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String wDate = df.format(date);
				
		String sql = "insert into " + dbTableWishlist
				+ " values(NULL, '"
				+ wTitle + "', '"
				+ wPrice + "', '"
				+ wWish + "','"
				+ wEvent + "', '"
				+ wDate + "','"
				+ wImagePath + "','"
				+ wBookmark + "');";
		
		db.execSQL(sql);
	}
	
	public Cursor selectWishlistData(int index) {
		String sql = "select * from " + dbTableWishlist + " where id=" + index + ";";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public Cursor selectWishAll() {
		String sql = "select * from " + dbTableWishlist + ";";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public void updateWishlistData(int flag, int id, String query) {
		
		String set = null;
		switch(flag) {
		case 0:
			set = "bookmark";
			break;
		}
		
		String sql = "update " + dbTableWishlist + " set " + set +" = '" + query + "' where id = " + id + ";";
		db.execSQL(sql);
	}
	
	public void removeWishlistData(int index) {
		String sql = "delete from " + dbTableWishlist + " where id = " + index + ";";
		db.execSQL(sql);
	}


}
