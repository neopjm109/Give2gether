package com.example.giv2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class EventPartyActivity extends Activity {

	TextView friendName, eventDday;
	Button btnGenerate;
	
	Intent intent;
	String name;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_party);
		
		intent = getIntent();
		name = intent.getStringExtra("name");
		initViews();
	}
	
	public void initViews() {
		friendName = (TextView) findViewById(R.id.eventFriendsName);
		
		friendName.setText(name);
	}

}
