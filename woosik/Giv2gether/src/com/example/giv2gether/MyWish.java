package com.example.giv2gether;

import android.graphics.Bitmap;

public class MyWish {
	int id;
	String title;
	int price, wish;
	String eventOn;
	String date;
	String imagePath;
	String bookmarkOn;
	Bitmap bmp;
	
	public MyWish(int id, String title, int price, int wish, String eventOn, String date, String imagePath, String bookmarkOn, Bitmap bmp) {
		this.id = id;
		this.title = title;
		this.price = price;
		this.wish = wish;
		this.eventOn = eventOn;
		this.date = date;
		this.imagePath = imagePath;
		this.bookmarkOn = bookmarkOn;
		this.bmp = bmp;
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

	public String getBookmarkOn() {
		return bookmarkOn;
	}
	
	public Bitmap getBitmap() {
		return bmp;
	}
}
