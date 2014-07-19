package com.hmjcompany.give2gether;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hmjcompany.give2gether.async.ImageThread;
import com.hmjcompany.give2gether.async.InsertWish;

public class AddWishActivity extends Activity {

	/*
	 * 		Views
	 */
	
	EditText editTitle;
	ListView itemList;
	
	/*
	 * 		Variables
	 */
	
	SearchData myWish;
	
	ArrayList<SearchData> searchList;
	SearchAdapter sAdapter;
	
	JSONArray product = null;
	JSONObject jObj = null;
	
	String url = "http://neopjm109.cafe24.com/shopJson.php?query=";

	DecimalFormat df = new DecimalFormat("#,##0");
	boolean bAutoListClick;
	
	Handler mHandler;
	SettingPreference setting;
	ActionBar actionBar;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wish);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		
		initViews();
		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	public void initViews() {
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setting = new SettingPreference(this);
		
		searchList = new ArrayList<SearchData>();
		bAutoListClick = false;
		
		mHandler = new Handler();

		editTitle = (EditText) findViewById (R.id.editTitle);
		editTitle.requestFocus();
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		editTitle.addTextChangedListener(new TextWatcher() {
			Timer timer = new Timer();
			final long delay = 500;
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			public void afterTextChanged(Editable s) {

				String query = s.toString();
				query = query.replaceAll(" ", "");
				final String parseUrl = url + query;
				
				timer.cancel();
				timer = new Timer();
  				
				timer.schedule(new TimerTask() {
					
					public void run() {
						runOnUiThread(new Runnable(){
							public void run(){
								// When list click, suggestion list isn't shown.
								if(!bAutoListClick)
									new JsonParse().execute(parseUrl);
								else
									bAutoListClick = false;
								
							}
						});						
					}					
				}, delay);
					
			}
		});
		
		itemList = (ListView) findViewById(R.id.itemList);
				
		itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				final int pos = position;
				AlertDialog.Builder mDialog = new AlertDialog.Builder(AddWishActivity.this);
				View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_wish, null);
				
				mDialog.setView(dialogView);

				ImageView image = (ImageView) dialogView.findViewById(R.id.dialogImage);
				TextView price = (TextView) dialogView.findViewById(R.id.dialogPrice);

				try {
					Bitmap bmp = new ImageThread().execute(searchList.get(pos).getImagePath()).get();
					
					if(bmp != null) {
						image.setImageBitmap(bmp);
					} else {
						image.setImageResource(R.drawable.image_loading);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				price.setText("쇼핑몰 최저가 : " + df.format(searchList.get(pos).getPrice()) + " 원\n"
						+ "Wish : " + df.format(searchList.get(pos).getWish()) + " \n");
				
				mDialog.setTitle(searchList.get(pos).getTitle());
				mDialog.setPositiveButton("이거 사줘", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						myWish = new SearchData(searchList.get(pos).getTitle(),
								searchList.get(pos).getPrice(),
								searchList.get(pos).getWish(),
								searchList.get(pos).getImagePath());

						bAutoListClick = true;
						
						try {
							String resp = new InsertWish(myWish, setting).execute().get();
							
							if(resp != null) {

								Intent intent = new Intent();
								
								intent.putExtra("title", myWish.getTitle());
								intent.putExtra("price", myWish.getPrice()+"");
								intent.putExtra("wish", myWish.getWish()+"");
								intent.putExtra("image", myWish.getImagePath());
								intent.putExtra("webId", resp);
								
								setResult(1001, intent);
								finish();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					
				});
				
				mDialog.setNegativeButton("아니", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				
				mDialog.show();
			}
			
		});
		
	}
	
	/*
	 * 		JsonParse
	 * 		- Using a Naver's open api, we get a data that is product information. 
	 */
	
	private class JsonParse extends AsyncTask<String, String, JSONObject> {
		
		protected JSONObject doInBackground(String... params) {

			searchList.clear();
			sAdapter = null;
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(params[0]);
			
			try {
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity httpEntity = response.getEntity();
				InputStream is = httpEntity.getContent();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder builder = new StringBuilder();
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}
				
				is.close();
				
				jObj = new JSONObject(builder.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return jObj;
			
		}

		protected void onPostExecute(JSONObject result) {
			
			try {
				product = result.getJSONArray("product");
				
				for(int i=0; i<product.length(); i++) {
					JSONObject c = product.getJSONObject(i);
					String title = c.getString("title").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","");
					title = title.replaceAll("\\([^)]+\\)", "");
					title = title.replaceAll("\\[[^)]+\\]", "");
					int price = c.getInt("lprice");
					String imagePath = c.getString("image");
					
					SearchData sData = new SearchData(title, price, (int)Math.ceil(price / 1000.0), imagePath);

					searchList.add(sData);
				}

				sAdapter = new SearchAdapter(AddWishActivity.this, R.layout.custom_auto_list, searchList);
						
				sAdapter.notifyDataSetChanged();
				
				itemList.setAdapter(sAdapter);

			} catch (Exception e) {
				
			}
			
		}

	}
	
	/*
	 * 		AutoCompleteTextView List Adapter
	 */
	
	public class SearchData {
		String title, imagePath;
		int price, wish;
		
		public SearchData(String title, int price, int wish, String imagePath) {
			this.title = title;
			this.price = price;
			this.wish = wish;
			this.imagePath = imagePath;
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
		
		public String getImagePath() {
			return imagePath;
		}
		
	}
	
	class SearchViewHolder {
		ImageView mImage = null;
		TextView mTitle, mPrice = null;
		String imagePath = null;
		Bitmap bmp = null;
		
		public SearchViewHolder(ImageView mImage, TextView mTitle, TextView mPrice, String imagePath) {
			this.mImage = mImage;
			this.mTitle = mTitle;
			this.mPrice = mPrice;
			this.imagePath = imagePath;
		}
	}

	class SearchAdapter extends ArrayAdapter<SearchData> {
		
		ArrayList<SearchData> list = new ArrayList<SearchData>();

		public SearchAdapter(Context context, int resource, ArrayList<SearchData> objects) {
			super(context, resource, objects);
			list = objects;
		}
		
		public View getView (int position, View converView, ViewGroup parent) {
			View v = converView;
			ImageView mImage = null;
			TextView mTitle, mPrice = null;
			SearchViewHolder viewHolder;
			
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_auto_list, null);

				mImage = (ImageView) v.findViewById(R.id.mImage);
				mTitle = (TextView) v.findViewById(R.id.mTitle);
				mPrice = (TextView) v.findViewById(R.id.mPrice);
				
				viewHolder = new SearchViewHolder(mImage, mTitle, mPrice, list.get(position).getImagePath());
				v.setTag(viewHolder);
			} else {
				viewHolder = (SearchViewHolder) v.getTag();
				mImage = viewHolder.mImage;
				mTitle = viewHolder.mTitle;
				mPrice = viewHolder.mPrice;
			}
			
			SearchData sData = list.get(position);
			
			if (sData != null) {

				try {
					Bitmap bmp = new ImageThread().execute(sData.getImagePath()).get();
					viewHolder.bmp = bmp;
					mImage.setImageBitmap(viewHolder.bmp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mTitle.setText(sData.getTitle());
				mPrice.setText("쇼핑몰 최저가 : " + df.format(sData.getPrice()) + " 원\n"
						+ "Wish : " + df.format(sData.getWish()) + " \n");
				
			}
			
			return v;
		}
		
	}
			
	/*
	 * 		wish is inserted into WEB Database
	 */
	
}
