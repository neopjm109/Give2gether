package com.example.giv2gether;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hmjcompany.give2gether.async.AsyncCheckMyPay;
import com.hmjcompany.give2gether.async.AsyncGetEventProgress;
import com.hmjcompany.give2gether.async.AsyncGetMyPay;
import com.hmjcompany.give2gether.async.AsyncPresentEvent;
import com.hmjcompany.give2gether.async.ImageThread;

public class EventPartyActivity extends Activity {

	TextView friendName, eventDday, wishTitle, wishPrice, eventProgressText;
	EditText payPresent;
	ImageView wishImage;
	Button btnPresent;
	ProgressBar eventProgress;
	
	Intent intent;
	String name, title, imagePath;
	int wish, webId, chkPay;
	boolean bPayCheck = false;
	
	ActionBar actionBar;
	SettingPreference setting;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_party);

		initIntent();		
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item1 = menu.add(0, 0, 0, "Present Pay");
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
			AlertDialog.Builder abDialog = new AlertDialog.Builder(EventPartyActivity.this);
			
			abDialog.setMessage("선물하실 건가요?");
			abDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int thisPay = Integer.parseInt(payPresent.getText().toString());
					if (thisPay < chkPay) {
						dialog.dismiss();
						Toast.makeText(EventPartyActivity.this, "더 높은 숫자를 입력하세요", Toast.LENGTH_SHORT).show();
					} else {
						String presentPay[] = {webId+"", setting.getID(), payPresent.getText().toString()};

						try {
							boolean bPresent = new AsyncPresentEvent().execute(presentPay).get();
							if(bPresent) {
								finish();
							}
						} catch (Exception e) {
							
						}
					}
				}
			});
			
			abDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			
			abDialog.show();
			
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
		setting = new SettingPreference(this);

		friendName = (TextView) findViewById(R.id.eventFriendsName);		
		wishTitle = (TextView) findViewById(R.id.eventTitle);
		wishPrice = (TextView) findViewById(R.id.eventPrice);
		wishImage = (ImageView) findViewById(R.id.eventImage);

		eventProgress = (ProgressBar) findViewById(R.id.eventProgress);
		eventProgressText = (TextView) findViewById(R.id.eventProgressText);
		payPresent = (EditText) findViewById(R.id.editEventPay);
		
		payPresent.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(count > 0) {
					btnPresent.setEnabled(true);
				} else {
					btnPresent.setEnabled(false);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnPresent = (Button) findViewById(R.id.btnEventPresent);
		btnPresent.setEnabled(false);
		btnPresent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder abDialog = new AlertDialog.Builder(EventPartyActivity.this);
				
				abDialog.setMessage("선물하실 건가요?");
				abDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						int thisPay = Integer.parseInt(payPresent.getText().toString());
						if (thisPay < chkPay) {
							dialog.dismiss();
							Toast.makeText(EventPartyActivity.this, "더 높은 숫자를 입력하세요", Toast.LENGTH_SHORT).show();
						} else {
							String presentPay[] = {webId+"", setting.getID(), payPresent.getText().toString()};
							
							try {
								boolean bPresent = new AsyncPresentEvent().execute(presentPay).get();
								if(bPresent) {
									finish();
								}
							} catch (Exception e) {
								
							}
							
						}
					}
				});
				
				abDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				
				abDialog.show();
			}
		});

		friendName.setText(name);
		wishTitle.setText(title);
		wishPrice.setText(wish+" Wish");
		eventProgress.setMax(wish);
		
		// Check My Pay
		String checkMyPay[] = {webId+"", setting.getID()};

		try {
			bPayCheck = new AsyncCheckMyPay().execute(checkMyPay).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(bPayCheck) {
			payPresent.setHint("이미 지불하신 상태입니다.");
		}
		
		try {
			chkPay = new AsyncGetMyPay().execute(checkMyPay).get();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject jObj = new AsyncGetEventProgress().execute(checkMyPay).get();
			JSONArray pays;
			
			int id = 0;
			int pay = 0;
			try {
				pays = jObj.getJSONArray("pay");
				
				for(int i=0; i<pays.length(); i++) {
					JSONObject c = pays.getJSONObject(i);
					id = c.getInt("id");
					pay = c.getInt("pay");
				}
			} catch (Exception e) {
				
			}
			eventProgress.setProgress(pay);
			
			eventProgressText.setText( (100 * pay)/wish + "% (" + pay + " Wish / " + wish + " Wish)");
		} catch (Exception e) {
			
		}
		
		try {
			Bitmap bmp = new ImageThread().execute(imagePath).get();
			if (bmp != null) {
				wishImage.setImageBitmap(bmp);
			}
		} catch (Exception e) {
			
		}
	}
	

}
