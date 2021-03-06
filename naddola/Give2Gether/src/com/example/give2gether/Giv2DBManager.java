package com.example.give2gether;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class Giv2DBManager {
	
	/*
	 * 		Static Variables
	 */

	static final String DB_NAME = "give2gether.db";
	static final String DB_TABLE_FRIENDS = "friends";
	static final String DB_TABLE_WISHLIST = "wishlist";
	static final String DB_TABLE_FRIENDS_WISHLIST = "friendswishlist";
	
	static final int DB_MODE = Context.MODE_PRIVATE;
	
	SQLiteDatabase db;

	public Giv2DBManager(Context context) {
		db = context.openOrCreateDatabase(DB_NAME, DB_MODE, null);
//		removeFWTable();
//		removeTable();
		createTable();
	}

	public Giv2DBManager(Context context, String dbName, int dbMode) {
		db = context.openOrCreateDatabase(dbName, dbMode, null);
//		removeFWTable();
//		removeTable();
		createTable();
	}
	
	public SQLiteDatabase getDatabase() {
		return db;
	}
	
	public void createTable() {
		String sql = "create table if not exists " + DB_TABLE_FRIENDS
				+ " (id integer primary key autoincrement,"
				+ " name text not null,"
				+ " email text not null,"
				+ " phone text not null,"
				+ " birth text,"
				+ " signed integer not null)";
		db.execSQL(sql);

		sql = "create table if not exists " + DB_TABLE_WISHLIST
				+ " (id integer primary key,"
				+ " title text not null,"
				+ " price integer not null,"
				+ " wish integer not null,"
				+ " event text not null,"
				+ " date text,"
				+ " image text not null,"
				+ " bookmark text not null)";
		
		db.execSQL(sql);

		sql = "create table if not exists " + DB_TABLE_FRIENDS_WISHLIST
				+ " (id integer primary key autoincrement,"
				+ " phone text not null,"
				+ " title text not null,"
				+ " price integer not null,"
				+ " wish integer not null,"
				+ " event text not null,"
				+ " date text,"
				+ " image text not null,"
				+ " bookmark text not null,"
				+ " webId integer not null)";
		
		db.execSQL(sql);
	}
	
	public void removeTable() {
		String sql = "drop table " + DB_TABLE_FRIENDS;
		db.execSQL(sql);

		sql = "drop table " + DB_TABLE_WISHLIST;
		db.execSQL(sql);

		sql = "drop table " + DB_TABLE_FRIENDS_WISHLIST;
		db.execSQL(sql);
	}
	
	public void removeFWTable() {
		String sql = "drop table " + DB_TABLE_FRIENDS_WISHLIST;
		db.execSQL(sql);
	}
	
	/*
	 * 		DB Function
	 */
		
	// Friends
	
	public void insertFriendsData(String name, String email, String phone, String birth, int signed) {
		String fName = name;
		String fEmail = email;
		String fPhone = phone;
		String fBirth = "";
		int fSigned = signed; 
		
		//DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy", Locale.KOREAN);
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd", Locale.KOREAN);
		DateFormat df2 = new SimpleDateFormat("mm/dd", Locale.KOREAN);
		Date Birth = null;
		
		try {
			Birth = df.parse(birth);
			fBirth = df2.format(Birth);
		} catch (Exception e) {
			
		}
		String sql = "insert into " + DB_TABLE_FRIENDS
				+ " values(NULL, '"
				+ fName + "', '"
				+ fEmail + "', '"
				+ fPhone + "', '"
				+ fBirth + "', '"
				+ fSigned + "');";
		
		db.execSQL(sql);
	}
	
	public Cursor selectFriendsAll() {
		String sql = "select * from " + DB_TABLE_FRIENDS + ";";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public Cursor selectGivFriendsAll() {
		String sql = "select * from " + DB_TABLE_FRIENDS + " where signed = 1;";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public ArrayList<MyFriend> getFriendsList(){
		Cursor result = selectFriendsAll();
		ArrayList<MyFriend> fl = new ArrayList<MyFriend>();
		
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			int id = result.getInt(0);
			String name = result.getString(1);
			String email = result.getString(2);
			String phone = result.getString(3);
			String birth = result.getString(4);
			int tempSigned = result.getInt(5);
			boolean signed;
			if(tempSigned == 1)
				signed = true;
			else
				signed = false;
			//String imagePath = result.getString(5);
			
			MyFriend tempFriend= new MyFriend(id, name, email, phone, birth, signed, null);
			
			fl.add(tempFriend);

			//new ImageThread().execute(tempFriend);
	
			result.moveToNext();
		}
		return fl;
	}
	
	public ArrayList<MyFriend> getGivFriendsList(){
		Cursor result = selectGivFriendsAll();
		ArrayList<MyFriend> fl = new ArrayList<MyFriend>();
		
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			int id = result.getInt(0);
			String name = result.getString(1);
			String email = result.getString(2);
			String phone = result.getString(3);
			String birth = result.getString(4);
			int tempSigned = result.getInt(5);
			boolean signed;
			if(tempSigned == 1)
				signed = true;
			else
				signed = false;
			//String imagePath = result.getString(5);
			
			MyFriend tempFriend= new MyFriend(id, name, email, phone, birth, signed, null);
			
			fl.add(tempFriend);

			//new ImageThread().execute(tempFriend);
	
			result.moveToNext();
		}
		return fl;
	}
	
	public Cursor selectFriendsData(int index) {
		String sql = "select * from " + DB_TABLE_FRIENDS + " where id=" + index + ";";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public void updateFriendsData() {
		
	}
	
	public void removeFriendsData(int index) {
		String sql = "delete from " + DB_TABLE_FRIENDS + " where id = " + index + ";";
		db.execSQL(sql);
	}
	
	public void removeFriendsData(String phone) {
		String sql = "delete from " + DB_TABLE_FRIENDS + " where phone = '" + phone + "';";
		db.execSQL(sql);
	}
	
	
	// Wishlist

	public void insertWishlistData(int id, String title, int price, int wish, String imagePath, String bookMark) {
		int wId = id;
		String wTitle = title;
		int wPrice = price;
		int wWish = wish;
		String wImagePath = imagePath;
		String wEvent = "false";
		String wBookmark = bookMark;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String wDate = df.format(date);
				
		String sql = "insert into " + DB_TABLE_WISHLIST
				+ " values('"
				+ wId + "', '"
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
		String sql = "select * from " + DB_TABLE_WISHLIST + " where id=" + index + ";";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public Cursor selectWishAll() {
		String sql = "select * from " + DB_TABLE_WISHLIST + ";";
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
		
		String sql = "update " + DB_TABLE_WISHLIST + " set " + set +" = '" + query + "' where id = " + id + ";";
		db.execSQL(sql);
	}
	
	public void removeWishlistData(int index) {
		String sql = "delete from " + DB_TABLE_WISHLIST + " where id = " + index + ";";
		db.execSQL(sql);
	}
	

	// Friends Wishlist
	
	public void insertFWishlistData(String phone, String title, int price, int wish, String date, String imagePath, String bookmark, String event, int webId) {
		String wPhone = phone;
		String wTitle = title;
		int wPrice = price;
		int wWish = wish;
		String wDate = date;
		String wImagePath = imagePath;
		String wEvent = event;
		String wBookmark = bookmark;
		int wWebId = webId;

		if (wEvent.equals("0")) {
			wEvent = "false";
		} else {
			wEvent = "true";			
		}
		
		if (wBookmark.equals("0")) {
			wEvent = "false";			
		} else {
			wEvent = "true";			
		}
				
		String sql = "insert into " + DB_TABLE_FRIENDS_WISHLIST
				+ " values(NULL, '"
				+ wPhone +"', '"
				+ wTitle + "', '"
				+ wPrice + "', '"
				+ wWish + "','"
				+ wEvent + "', '"
				+ wDate + "','"
				+ wImagePath + "','"
				+ wBookmark + "','"
				+ wWebId + "');";
		
		db.execSQL(sql);
	}
	
	public Cursor selectFWishlistData(int index) {
		String sql = "select * from " + DB_TABLE_FRIENDS_WISHLIST + " where id='" + index + "';";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}

	public Cursor selectFWishlistData(String phone) {
		String sql = "select * from " + DB_TABLE_FRIENDS_WISHLIST + " where phone='" + phone + "';";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}

	public Cursor checkFWishlistData(int webId) {
		String sql = "select * from " + DB_TABLE_FRIENDS_WISHLIST + " where webId='" + webId + "';";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}

	public Cursor checkFWishlistData(String phone) {
		String sql = "select * from " + DB_TABLE_FRIENDS_WISHLIST + " where phone='" + phone + "';";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public Cursor selectFWishAll() {
		String sql = "select * from " + DB_TABLE_FRIENDS_WISHLIST + ";";
		Cursor result = db.rawQuery(sql, null);
		
		return result;
	}
	
	public void updateFWishlistData(int flag, int id, String query) {
		
		String set = null;
		switch(flag) {
		case 0:
			set = "bookmark";
			break;
		}
		
		String sql = "update " + DB_TABLE_FRIENDS_WISHLIST + " set " + set +" = '" + query + "' where id = " + id + ";";
		db.execSQL(sql);
	}
	
	public void removeFWishlistData(int index) {
		String sql = "delete from " + DB_TABLE_FRIENDS_WISHLIST + " where id = " + index + ";";
		db.execSQL(sql);
	}

}