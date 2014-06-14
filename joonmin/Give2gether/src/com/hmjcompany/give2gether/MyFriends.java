package com.hmjcompany.give2gether;

public class MyFriends {
	int id;
	String name;
	String birth;
	
	MyFriends(int id, String name, String birth) {
		this.id = id;
		this.name = name;
		this.birth = birth;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getBirth() {
		return this.birth;
	}
}
