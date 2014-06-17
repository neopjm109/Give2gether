package com.example.give2gether;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddWishActivity extends Activity {

	/*
	 * 		Views
	 */
	
	AutoCompleteTextView editTitle;
	ImageView editImage;
	EditText editPrice, editWish;
	Button btnAdd;
	
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
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wish);
		
		initViews();
		
	}
	
	public void initViews() {
		searchList = new ArrayList<SearchData>();
		bAutoListClick = false;

		editImage = (ImageView) findViewById (R.id.editImage);
		editTitle = (AutoCompleteTextView) findViewById (R.id.editTitle);
		
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
						
						// When list click, suggestion list isn't shown.
						if(!bAutoListClick)
							new JsonParse().execute(parseUrl);
						else
							bAutoListClick = false;
						
					}
					
				}, delay);
					
			}
		});
		
		editTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				myWish = new SearchData(searchList.get(position).getTitle(),
						searchList.get(position).getPrice(),
						searchList.get(position).getWish(),
						searchList.get(position).getImagePath());

				new ImageThread(editImage).execute(searchList.get(position).getImagePath());
				
				editTitle.setText(searchList.get(position).getTitle());
				editPrice.setText(df.format(searchList.get(position).getPrice()));
				editWish.setText(searchList.get(position).getWish()+"");
				
				bAutoListClick = true;
			}
			
		});

		editPrice = (EditText) findViewById (R.id.editPrice);
		editWish = (EditText) findViewById (R.id.editWish);
		btnAdd = (Button) findViewById (R.id.btnAdd);
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if( editTitle.length() != 0 && editPrice.length() != 0) {
					Intent intent = new Intent();
					
					intent.putExtra("title", myWish.getTitle());
					intent.putExtra("price", myWish.getPrice()+"");
					intent.putExtra("wish", myWish.getWish()+"");
					intent.putExtra("image", myWish.getImagePath());
					
					setResult(1001, intent);
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Put write", Toast.LENGTH_SHORT).show();
				}
				
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
					
					SearchData sData = new SearchData(title, price, (price / 1000), imagePath);
						
					searchList.add(sData);
				}

				sAdapter = new SearchAdapter(AddWishActivity.this, R.layout.custom_auto_list, searchList);
						
				sAdapter.notifyDataSetChanged();

				editTitle.setAdapter(sAdapter);
				editTitle.showDropDown();
				
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

				new SearchImageThread().execute(viewHolder);
				
				mTitle.setText(sData.getTitle());
				mPrice.setText("쇼핑몰 최저가 : " + df.format(sData.getPrice()) + " 원\n"
						+ "Wish : " + df.format(sData.getWish()) + " \n");
				
			}
			
			return v;
		}
		
	}
	
	class SearchImageThread extends AsyncTask<SearchViewHolder, Void, SearchViewHolder> {

		SearchViewHolder viewHolder;
		
		protected SearchViewHolder doInBackground(SearchViewHolder... params) {
			try {
				viewHolder = params[0];
				URL url = new URL(viewHolder.imagePath);
				viewHolder.bmp = BitmapFactory.decodeStream(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return viewHolder;
		}

		protected void onPostExecute(SearchViewHolder result) {
			super.onPostExecute(result);
			
			if (result.bmp != null)
				result.mImage.setImageBitmap(result.bmp);
			else
				result.mImage.setImageResource(R.drawable.image_loading);
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
}
