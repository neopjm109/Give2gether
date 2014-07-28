package com.example.giv2gether;

import java.io.Serializable;

public class Newsfeed implements Serializable{

	//뉴스피드 태그 
	public static final int PUT_FRIEND_BIRTH_DDAY = 1000;
	public static final int PUT_EVENT_GENERATION = 1001;
	public static final int PUT_MYEVENT_CHANGE = 1002;
	
	int mTag;
	String mMainPersonName;
	String mMainPersonEmail;
	int mDday;
	
	public Newsfeed(int Tag, String MainPersonName, String MainPersonEmail, int Dday){
		mTag = Tag;
		mMainPersonName = MainPersonName;
		mMainPersonEmail = MainPersonEmail;
		mDday = Dday;
	}
	
	public Newsfeed(int Tag, String MainPersonName, String MainPersonEmail){
		mTag = Tag;
		mMainPersonName = MainPersonName;
		mMainPersonEmail = MainPersonEmail;
	}
	
	int getTag(){
		return mTag;
	}
	
	String getPerson(){
		return mMainPersonName;
	}
	
	String getEmail(){
		return mMainPersonEmail;
	}
	
	int getDday(){
		return mDday;
	}
	
	String getNotice(){
		String Notice = null;
		if(this.mTag == PUT_FRIEND_BIRTH_DDAY)
			Notice = mMainPersonName+"님의 생일이 "+mDday+"일 남았습니다.";
		else if(this.mTag == PUT_EVENT_GENERATION && mDday > -3)
			Notice = mMainPersonName+"님의 이벤트가 시작되었습니다!!(New)"; 
		else if(this.mTag == PUT_EVENT_GENERATION && mDday < -3)
			Notice = mMainPersonName+"님의 이벤트가 시작되었습니다!!"; 
		else if(this.mTag == PUT_MYEVENT_CHANGE)
			Notice = mMainPersonName+"님이 당신의 이벤트에 참가했습니다.";
		return Notice;
	}

}
