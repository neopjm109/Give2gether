package com.hmjcompany.give2gether;

import java.util.ArrayList;

public class GivLoader {

	private ArrayList<MyWish> arrMyWishList;
	
	private ArrayList<MyFriend> arrEventFriendList;
	private ArrayList<MyFriend> arrGivFriendList;
	private ArrayList<MyFriend> arrContactFriendList;

	private ArrayList<MyFriendsWish> arrMyFriendsWishList;

	public GivLoader() {
		arrMyWishList = new ArrayList<MyWish>();
		
		arrEventFriendList = new ArrayList<MyFriend>();
		arrGivFriendList = new ArrayList<MyFriend>();
		arrContactFriendList = new ArrayList<MyFriend>();
	}
	
	/*
	 * 		Getter
	 */

	public ArrayList<MyWish> getArrMyWishList() {
		return arrMyWishList;
	}

	public ArrayList<MyFriend> getEventFriendList() {
		return arrEventFriendList;
	}

	public ArrayList<MyFriend> getGivFriendList() {
		return arrGivFriendList;
	}

	public ArrayList<MyFriend> getContactFriendList() {
		return arrContactFriendList;
	}

	public ArrayList<MyFriendsWish> getMyFriendsWishList() {
		return arrMyFriendsWishList;
	}
	
	/*
	 * 		Setter
	 */

	public void setArrMyWishList(ArrayList<MyWish> list) {
		this.arrMyWishList = list;
	}

	public void setEventFriendList(ArrayList<MyFriend> list) {
		this.arrEventFriendList = list;
	}

	public void setGivFriendList(ArrayList<MyFriend> list) {
		this.arrGivFriendList = list;
	}

	public void setContactFriendList(ArrayList<MyFriend> list) {
		this.arrContactFriendList = list;
	}
	
	public void getMyFriendsWishList(ArrayList<MyFriendsWish> list) {
		this.arrMyFriendsWishList = list;
	}
	
}
