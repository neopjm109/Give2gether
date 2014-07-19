package com.hmjcompany.give2gether;

public class MyFriend {
	int id;
	String name;
	String email;
	String phone;
	String birth;
	boolean signed;
	String imagePath;
	
	public MyFriend(int id, String name, String email, String phone, String birth, boolean signed, String imagePath) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.birth = birth;
		this.signed = signed;
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
	
	public boolean getSigned() {
		return signed;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
}
