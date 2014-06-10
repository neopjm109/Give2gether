package com.hmjcompany.give2gether;

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
import android.util.Log;
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
	EditText editPrice;
	Button btnAdd;
	
	/*
	 * 		Variables
	 */
	
	ArrayList<SearchData> searchList;
	SearchAdapter sAdapter;
	
	JSONArray product = null;
	JSONObject jObj = null;
	
	String url = "http://neopjm109.cafe24.com/shopJson.php?query=";

	DecimalFormat df = new DecimalFormat("#,##0");
	boolean bAutoListClick;
	
	Bitmap bm = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wish);
		
		initViews();
		
	}
	
	public void initViews() {
		searchList = new ArrayList<SearchData>();
		bAutoListClick = false;
				
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
				editTitle.setText(searchList.get(position).getTitle());
				editPrice.setText(searchList.get(position).getPrice()+"");
				
				bAutoListClick = true;
			}
			
		});
		
		editPrice = (EditText) findViewById (R.id.editPrice);
		btnAdd = (Button) findViewById (R.id.btnAdd);
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
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
	
	/*
	 * 		JsonParse
	 * 		- Using a Naver's open api, we get a data that is product information. 
	 */
	private class JsonParse extends AsyncTask<String, String, JSONObject> {
		
		protected JSONObject doInBackground(String... params) {
			
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
				
				searchList.clear();
				sAdapter = null;
				
				for(int i=0; i<product.length(); i++) {
					JSONObject c = product.getJSONObject(i);
					String title = c.getString("title").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","");
					title = title.replaceAll("\\([^)]+\\)", "");
					title = title.replaceAll("\\[[^)]+\\]", "");
					int price = c.getInt("lprice");
					String imagePath = c.getString("image");
					
					SearchData sData = new SearchData(title, (price / 1000), imagePath);
					
//					searchList.add(title + " " + df.format(price) + "ø¯");	
					searchList.add(sData);
				}

				sAdapter = new SearchAdapter(AddWishActivity.this, R.layout.custom_auto_list, searchList);
				sAdapter.setNotifyOnChange(false);

				editTitle.setAdapter(sAdapter);
				editTitle.showDropDown();
				
				sAdapter.notifyDataSetChanged();
				Log.i("PJM", searchList.size() + " > " + searchList.get(0).getTitle());

			} catch (Exception e) {
				
			}
			
		}
				
	}
	
	public class SearchData {
		String title, imagePath;
		int price;
		
		public SearchData(String title, int price, String imagePath) {
			this.title = title;
			this.price = price;
			this.imagePath = imagePath;
		}
			
		public String getTitle() {
			return title;
		}
		
		public int getPrice() {
			return price;
		}
		
		public String getImagePath() {
			return imagePath;
		}
		
	}

	class SearchAdapter extends ArrayAdapter<SearchData> {
		
		ArrayList<SearchData> list = new ArrayList<SearchData>();

		public SearchAdapter(Context context, int resource,
				ArrayList<SearchData> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			list = objects;
		}
		
		public View getView (int position, View converView, ViewGroup parent) {
			View v = converView;
			
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_auto_list, null);
			}
			
			SearchData sData = list.get(position);
			
			if (sData != null) {
				ImageView mImage = (ImageView) v.findViewById(R.id.mImage);
				TextView mTitle = (TextView) v.findViewById(R.id.mTitle);
				TextView mPrice = (TextView) v.findViewById(R.id.mPrice);
				
				Thread thread = new Thread(new GettingImageThread(sData.getImagePath()));
				thread.start();
				
				mImage.setImageBitmap(bm);
				mTitle.setText(sData.getTitle());
				mPrice.setText("ºÓ«Œ∏Ù √÷¿˙∞° : " + df.format(sData.getPrice()) + " Wish");
				
			}
			
			return v;
		}
		
	}
	
	class GettingImageThread implements Runnable {

		String src;
		public GettingImageThread(String src) {
			this.src = src;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(src);
				bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
