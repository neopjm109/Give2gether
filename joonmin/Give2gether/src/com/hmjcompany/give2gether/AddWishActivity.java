package com.hmjcompany.give2gether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddWishActivity extends Activity {

	/*
	 * 		Views
	 */
	
	EditText editTitle, editPrice;
	Button btnAdd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wish);
		
		initViews();
	}
	
	public void initViews() {
		editTitle = (EditText) findViewById (R.id.editTitle);
		editPrice = (EditText) findViewById (R.id.editPrice);
		btnAdd = (Button) findViewById (R.id.btnAdd);
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if( editTitle.length() != 0 && editPrice.length() != 0) {
					Intent intent = new Intent();
					
					intent.putExtra("title", editTitle.getText().toString());
					intent.putExtra("price", editPrice.getText().toString());
					
					setResult(1001, intent);
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Put write", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
