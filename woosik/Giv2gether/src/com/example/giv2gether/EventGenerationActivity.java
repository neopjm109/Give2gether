package com.example.giv2gether;

import java.net.URL;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventGenerationActivity extends Activity {

	TextView friendName, eventDday, wishTitle, wishPrice;
	ImageView wishImage;
	Button btnGenerate;
	
	ActionBar actionBar;
	Intent intent;
	String name, title, imagePath;
	int wish, webId;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_generate);
		
		initIntent();
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item1 = menu.add(0, 0, 0, "Generate Event");
		item1.setIcon(android.R.drawable.ic_menu_add);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case 0:
			Intent intent = new Intent(EventGenerationActivity.this, EventMessageActivity.class);
			intent.putExtra("name", name);
			intent.putExtra("webId", webId);
			startActivity(intent);
			finish();
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
