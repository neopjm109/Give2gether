package com.example.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventGenerationActivity extends Activity {

	TextView friendName, eventDday;
	Button btnGenerate;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_generate);
		
		initViews();
	}
	
	public void initViews() {
		btnGenerate = (Button) findViewById(R.id.btnEventGenerate);
		
		btnGenerate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(EventGenerationActivity.this, EventMessageActivity.class);
				startActivity(intent);
			}
		});
	}

}
