package com.example.giv2gether;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

				new ImageThread(image).execute(searchList.get(pos).getImagePath());
				
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

						new InsertWish(myWish).execute();

						bAutoListClick = true;
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

				new SearchImageThread(mImage).execute(sData.getImagePath());
				
				mTitle.setText(sData.getTitle());
				mPrice.setText("쇼핑몰 최저가 : " + df.format(sData.getPrice()) + " 원\n"
						+ "Wish : " + df.format(sData.getWish()) + " \n");
				
			}
			
			return v;
		}
		
	}
	
	class SearchImageThread extends AsyncTask<String, Void, Bitmap> {

		Bitmap bmp;
		ImageView image;
		
		public SearchImageThread(ImageView image) {
			this.image = image;
			bmp = null;
		}
		
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				this.bmp = BitmapFactory.decodeStream(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return this.bmp;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if (result != null)
				image.setImageBitmap(result);
			else
				image.setImageResource(R.drawable.image_loading);
		}
	}
	
	/*
	 * 		ImageView. get a bitmap of image's url.
	 */
		
	class ImageThread extends AsyncTask<String, Void, Bitmap> {

		ImageView image;
		Bitmap bmp;
		
		public ImageThread(ImageView imageView) {
			this.image = imageView;
			bmp = null;
		}
		
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return bmp;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			image.setImageBitmap(result);
		}
	}
	
	/*
	 * 		wish is inserted into WEB Database
	 */
	
	private class InsertWish extends AsyncTask<String, String, Void> {
		SearchData myWishWeb;
		String resp = null;
		
		public InsertWish(SearchData myWishWeb) {
			this.myWishWeb = myWishWeb;
		}
		
		protected Void doInBackground(String... params) {
			
			/*
			 * 		Insert
			 */
			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/insertWish.php";
				
				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("email", setting.getID()));
				params2.add(new BasicNameValuePair("title", myWishWeb.getTitle()));
				params2.add(new BasicNameValuePair("price", myWishWeb.getPrice()+""));
				params2.add(new BasicNameValuePair("wish", myWishWeb.getWish()+""));
				params2.add(new BasicNameValuePair("image", myWishWeb.getImagePath()+""));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/*
			 * 		Select
			 */
			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/getLastMyWish.php";
				
				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("email", setting.getID()));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();
				
				if ( resEntity != null ) {
					resp = EntityUtils.toString(resEntity);	
				}

			} catch (Exception e) {
				e.printStackTrace();
			}			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			Intent intent = new Intent();
			
			intent.putExtra("title", myWish.getTitle());
			intent.putExtra("price", myWish.getPrice()+"");
			intent.putExtra("wish", myWish.getWish()+"");
			intent.putExtra("image", myWish.getImagePath());
			intent.putExtra("webId", resp);
			
			setResult(1001, intent);
			finish();
		}

	}
}
