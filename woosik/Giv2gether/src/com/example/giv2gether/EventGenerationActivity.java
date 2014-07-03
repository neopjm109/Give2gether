package com.example.giv2gether;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EventGenerationActivity extends Activity {

	TextView friendName, eventDday, wishTitle, wishPrice;
	ImageView wishImage;
	Button btnGenerate;
	
	Intent intent;
	String name, title, imagePath;
	int wish, webId;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_generate);
		
		initIntent();		
		initViews();
	}
	
	public void initIntent() {
		intent = getIntent();
		name = intent.getStringExtra("name");
		title = intent.getStringExtra("title");
		wish = intent.getIntExtra("wish", 0);
		imagePath = intent.getStringExtra("imagePath");
		webId = intent.getIntExtra("webId", 0);
	}
	
	public void initViews() {
		btnGenerate = (Button) findViewById(R.id.btnEventGenerate);
		
		btnGenerate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(EventGenerationActivity.this, EventMessageActivity.class);
				intent.putExtra("name", name);
				intent.putExtra("webId", webId);
				startActivity(intent);
				finish();
			}
		});
		
		friendName = (TextView) findViewById(R.id.eventGenerateFriendsName);		
		wishTitle = (TextView) findViewById(R.id.eventGenerateTitle);
		wishPrice = (TextView) findViewById(R.id.eventGeneratePrice);
		wishImage = (ImageView) findViewById(R.id.eventGenerateImage);

		friendName.setText(name);
		wishTitle.setText(title);
		wishPrice.setText(wish+" Wish");
		
		new ImageThread().execute(imagePath);
	}

	// 		Get a Image by url
	class ImageThread extends AsyncTask<String, Void, Bitmap> {

		Bitmap bmp;
		
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				bmp = BitmapFactory.decodeStream(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return bmp;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if (result != null)
				wishImage.setImageBitmap(result);
			else
				wishImage.setImageResource(R.drawable.ic_launcher);
		}
	}

}
