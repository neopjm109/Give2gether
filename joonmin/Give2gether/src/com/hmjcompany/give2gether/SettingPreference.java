package com.hmjcompany.give2gether;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Key
 * "islogin" 자동로그인여부 
 * "id" 로그인된 아이디
 * "name" 로그인된 이름  
 */
public class SettingPreference {

	static SharedPreferences setting;
	static boolean mIslogin;				//save autologin
	static String mId = null;				//save login id
	static String mName = null;
	
	public SettingPreference(Context context){
		setting = context.getSharedPreferences("setting", 0);
		
		mIslogin = setting.getBoolean("islogin", false);
		
		if(mIslogin){
			mId = setting.getString("id", null);
			mName = setting.getString("name", null);
		}
	}
	
	public String getID(){
		return mId;
	}
	
	public String getName(){
		return mName;
	}
	
	public boolean isLogin(){
		return mIslogin;
	}
	
	public void setAutoLoginFalse(){
		if(mIslogin){
			mIslogin = false;
			SharedPreferences.Editor editor;
			editor = setting.edit();
			editor.putBoolean("islogin", mIslogin);
			editor.commit();
		}
	}
	
	public void setAutoLoginTrue(){
		if(!mIslogin){
			mIslogin = true;
			SharedPreferences.Editor editor;
			editor = setting.edit();
			editor.putBoolean("islogin", mIslogin);
			editor.commit();
		}
	}
	
	public void setID(String id){
		mId = id;
		SharedPreferences.Editor editor;
		editor = setting.edit();
		editor.putString("id", id);
		editor.commit();
	}
	
	public void setName(String name){
		mName = name;
		SharedPreferences.Editor editor;
		editor = setting.edit();
		editor.putString("name", name);
		editor.commit();
	}
	
	public void settingClear(){
		SharedPreferences.Editor editor;
		editor = setting.edit();
		editor.clear();
		editor.commit();
	}
}
