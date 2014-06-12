package com.example.giv2gether;


public class MyWish {
	int id;
	String title;
	int price, wish;
	String eventOn;
	String date;
	String imagePath;
	
	public MyWish(int id, String title, int price, int wish, String eventOn, String date, String imagePath) {
		this.id = id;
		this.title = title;
		this.price = price;
		this.wish = wish;
		this.eventOn = eventOn;
		this.date = date;
		this.imagePath = imagePath;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getWish() {
		return wish;
	}
	
	public String getEventOn() {
		return eventOn;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
}
