package com.example.giv2gether;

public class MyFriend {
	int id;
	String name;
	String email;
	String phone;
	String birth;
	String imagePath;
	
	public MyFriend(int id, String name, String email, String phone, String birth, String imagePath) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.birth = birth;
		this.imagePath = imagePath;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public String getBirth() {
		return birth;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
}
