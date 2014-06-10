package com.hmjcompany.give2gether;


public class MyWish {
	int id;
	String title;
	int price;
	String eventOn;
	String date;
	
	public MyWish(int id, String title, int price, String eventOn, String date) {
		this.id = id;
		this.title = title;
		this.price = price;
		this.eventOn = eventOn;
		this.date = date;
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
	
	public String getEventOn() {
		return eventOn;
	}
	
	public String getDate() {
		return date;
	}
	
}
