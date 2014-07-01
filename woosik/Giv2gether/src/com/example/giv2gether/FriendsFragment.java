package com.example.giv2gether;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsFragment extends Fragment {

	public static final String TAG = "naddola";

	View rootView;
	MainActivity mActivity;
	Giv2DBManager dbManager;

	SeparatedListAdapter baseAdapter;
	MyFriendAdapter EventFriendAdapter;
	MyFriendAdapter GivFriendAdapter;
	MyFriendAdapter ContactFriendAdapter;

	ArrayList<MyFriend> arrEventFriendList;
	ArrayList<MyFriend> arrGivFriendList;
	ArrayList<MyFriend> arrContactFriendList;
	ArrayList<MyFriend> arrSearchFriendList;
	
	ArrayList<MyFriendsWish> arrMyFriendsWishList;

	boolean editOn = false;

	Button bt_AddFriends;
	ListView listFriend;
	EditText et_SearchFriend;
	TextWatcher textwatcher;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_friends, container, false);

		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		init();
	}

	public void init() {
		

		et_SearchFriend = (EditText) rootView
				.findViewById(R.id.friend_et_search);
		setTextWatcher();
		et_SearchFriend.addTextChangedListener(textwatcher);

		mActivity = (MainActivity) getActivity();
		dbManager = mActivity.getDBManager();
		listFriend = (ListView) rootView.findViewById(R.id.friend_listview);

		arrGivFriendList = new ArrayList<MyFriend>();
		arrContactFriendList = new ArrayList<MyFriend>();
		arrSearchFriendList = new ArrayList<MyFriend>();
		
		arrMyFriendsWishList = new ArrayList<MyFriendsWish>();
		selectFWishAll();

		ArrayList<MyFriend> arr = dbManager.getFriendsList();
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getSigned())
				arrGivFriendList.add(arr.get(i));
			else
				arrContactFriendList.add(arr.get(i));
		}
		arrEventFriendList = getEventingFriendList();
		baseAdapter = new SeparatedListAdapter(mActivity);

		EventFriendAdapter = new MyFriendAdapter(getActivity()
				.getApplicationContext(), R.layout.custom_friend_list,
				arrEventFriendList);
		if (arrEventFriendList.size() > 0)
			baseAdapter.addSection("이벤트 중인 친구들", EventFriendAdapter);
		GivFriendAdapter = new MyFriendAdapter(getActivity()
				.getApplicationContext(), R.layout.custom_friend_list,
				arrGivFriendList);
		if (arrGivFriendList.size() > 0)
			baseAdapter.addSection("Giv 친구들", GivFriendAdapter);
		ContactFriendAdapter = new MyFriendAdapter(getActivity()
				.getApplicationContext(), R.layout.custom_friend_list,
				arrContactFriendList);
		if (arrContactFriendList.size() > 0)
			baseAdapter.addSection("나만 친구들", ContactFriendAdapter);

		listFriend.setAdapter(baseAdapter);
		listFriend.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	public void setTextWatcher() {
		textwatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				arrSearchFriendList.clear();
				for (int i = 0; i < arrGivFriendList.size(); i++) {
					MyFriend tempFriend = arrGivFriendList.get(i);
					if (tempFriend.getName()
							.matches(".*" + s.toString() + ".*")) {
						arrSearchFriendList.add(tempFriend);
					}
				}

				GivFriendAdapter = new MyFriendAdapter(getActivity()
						.getApplicationContext(), R.layout.custom_friend_list,
						arrSearchFriendList);

				listFriend.setAdapter(GivFriendAdapter);
				listFriend.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			}
		};
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);

		MenuItem item1 = menu.add(0, 0, 0, "Edit Friends List");
		item1.setIcon(android.R.drawable.ic_menu_edit);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		MenuItem item2 = menu.add(0, 1, 1, "Add Friends");
		item2.setIcon(android.R.drawable.ic_menu_add);
		item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case 0:
			editOn = !editOn;
			GivFriendAdapter.notifyDataSetChanged();
			break;
		case 1:
			Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	class MyFriendViewHolder {
		ImageView mImage = null;
		TextView mName = null;
		TextView mBirth = null;
		String imagePath = null;
		Bitmap bmp = null;

		public MyFriendViewHolder(ImageView mImage, TextView mName,
				TextView mBirth, String imagePath) {
			this.mImage = mImage;
			this.mName = mName;
			this.mBirth = mBirth;
			this.imagePath = imagePath;
		}
	}

	class MyFriendAdapter extends ArrayAdapter<MyFriend> {

		ArrayList<MyFriend> list = new ArrayList<MyFriend>();

		public MyFriendAdapter(Context context, int resource,
				ArrayList<MyFriend> objects) {
			super(context, resource, objects);

			list = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			final MyFriendViewHolder viewHolder;
			ImageView mImage = null;
			TextView mName = null, mBirth = null;

			final int pos = position;

			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_friend_list, null);

				mImage = (ImageView) v.findViewById(R.id.Friend_list_PhotoWish);
				mName = (TextView) v.findViewById(R.id.Friend_list_Name);
				mBirth = (TextView) v.findViewById(R.id.Friend_list_Birth);

				viewHolder = new MyFriendViewHolder(mImage, mName, mBirth, list
						.get(position).getImagePath());
				v.setTag(viewHolder);
			} else {
				viewHolder = (MyFriendViewHolder) v.getTag();
				mImage = viewHolder.mImage;
				mName = viewHolder.mName;
				mBirth = viewHolder.mBirth;

				viewHolder.imagePath = list.get(position).getImagePath(); 
				
				viewHolder.mImage.setImageResource(R.drawable.image_loading);
			}

			final MyFriend mData = list.get(position);

			if (mData != null) {
				// new MyWishImageThread().execute(viewHolder);
				mName.setText(mData.getName());
				if (mData.getBirth() != null)
					mBirth.setText(mData.getBirth());
			}

			selectFWishlistData(mData.getPhone());
			
			new AsyncFriendsWish(FriendsFragment.this).execute("http://naddola.cafe24.com/getFriendWish.php?phone="+mData.getPhone());

			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					for (int i=0; i<arrMyFriendsWishList.size(); i++) {
						if (mData.getPhone().equals(arrMyFriendsWishList.get(i).phone)) {
							Log.i("PJM", arrMyFriendsWishList.get(i).getTitle());
							break;
						}
					}
					
					/*
					Intent intent = new Intent(mActivity,
							EventGenerationActivity.class);
					intent.putExtra("name", mData.getName());
					startActivity(intent);
					*/
				}
			});
			return v;
		}
	}

	
	public ArrayList<MyFriend> getEventingFriendList() {
		ArrayList<MyFriend> myFriends = new ArrayList<MyFriend>();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		getEventingFriendsHttpPostAsyncTask task = new getEventingFriendsHttpPostAsyncTask();

		JSONArray jsonArr = task.doInBackground();
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject c;
			try {
				c = jsonArr.getJSONObject(i);
				String email = c.getString("email");
				for(int j=0; j<arrGivFriendList.size(); j++){
					if(email.equals(arrGivFriendList.get(j).getEmail())){
						myFriends.add(arrGivFriendList.get(j));
						arrGivFriendList.remove(j);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return myFriends;

	}

	class getEventingFriendsHttpPostAsyncTask extends
			AsyncTask<String, Integer, JSONArray> {
		JSONArray jFriendArr;

		@Override
		protected JSONArray doInBackground(String... params) {

			jFriendArr = new JSONArray();

			for (int i = 0; i < arrGivFriendList.size(); i++) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("email", arrGivFriendList.get(i).getEmail());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jFriendArr.put(obj);
			}

			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/selectEventingFriendList.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("friends", jFriendArr
						.toString()));

				// Log.i(TAG, "sendArray - " +jContactArr.toString());

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();

				if (resEntity != null) {
					String resp = EntityUtils.toString(resEntity);
					
					try {
						JSONArray jsonArr = new JSONArray(resp);
						return jsonArr;
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Log.i(TAG, resp);
				}
				

			} catch (MalformedURLException e) {
				//
			} catch (IOException e) {
				//
			}
			return null;
		}
	}


	/*
	 * 		DB Function
	 */

	public void insertFWishlistData (String phone, String title, int price, int wish, String date, String imagePath, String bookmark, String event, int webId) {
		dbManager.insertFWishlistData(phone, title, price, wish, date, imagePath, bookmark, event, webId);

		selectFWishAll();

		EventFriendAdapter.notifyDataSetChanged();
		GivFriendAdapter.notifyDataSetChanged();
	}
	
	public void selectFWishlistData(int index) {
		Cursor result = dbManager.selectFWishlistData(index);
		
		if (result.moveToFirst()) {
			
			int id = result.getInt(0);
			String phone = result.getString(1);
			String title = result.getString(2);
			int price = result.getInt(3);
			int wish = result.getInt(4);
			String eventOn = result.getString(5);
			String date = result.getString(6);
			String imagePath = result.getString(7);
			String bookmarkOn = result.getString(8);
			int webId = result.getInt(9);
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);
			myWish.setWebId(webId);
		}
		
		result.close();
	}
	
	public void selectFWishlistData(String phone_index) {
		Cursor result = dbManager.selectFWishlistData(phone_index);
		
		if (result.moveToFirst()) {

			int id = result.getInt(0);
			String phone = result.getString(1);
			String title = result.getString(2);
			int price = result.getInt(3);
			int wish = result.getInt(4);
			String eventOn = result.getString(5);
			String date = result.getString(6);
			String imagePath = result.getString(7);
			String bookmarkOn = result.getString(8);
			int webId = result.getInt(9);
			
			MyFriendsWish myFWish = new MyFriendsWish(id, phone, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);
			myFWish.setWebId(webId);
		}
		
		result.close();
	}

	public boolean checkFWishlistData(int webId) {
		Cursor result = dbManager.checkFWishlistData(webId);
		
		if (result.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkFWishlistData(String phone) {
		Cursor result = dbManager.checkFWishlistData(phone);
		
		if (result.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void selectFWishAll() {
		Cursor result = dbManager.selectFWishAll();
		
		arrMyFriendsWishList.clear();
		
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			
			int id = result.getInt(0);
			String phone = result.getString(1);
			String title = result.getString(2);
			int price = result.getInt(3);
			int wish = result.getInt(4);
			String eventOn = result.getString(5);
			String date = result.getString(6);
			String imagePath = result.getString(7);
			String bookmarkOn = result.getString(8);
			int webId = result.getInt(9);
			
			
			MyFriendsWish myFWish = new MyFriendsWish(id, phone, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);
			myFWish.setWebId(webId);
			
			arrMyFriendsWishList.add(myFWish);

//			new ImageThread().execute(myWish);
	
			result.moveToNext();
		}
		
		Log.i("PJM", arrMyFriendsWishList.size()+"");

		result.close();
		
	}
	
	public void updateFWishlistData(int flag, int id, String query) {
		dbManager.updateFWishlistData(flag, id, query);
	}
	
	public void removeFWishlistData(int index, int webId) {
		dbManager.removeFWishlistData(index);
	}

	/*
	 * 		Async access Friend's wish
	 */
	
	class AsyncFriendsWish extends AsyncTask<String, String, JSONObject> {

		JSONObject jObj;
		JSONArray friendsWish;
		FriendsFragment fragment;
		
		public AsyncFriendsWish(FriendsFragment fm) {
			this.fragment = fm;
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub

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
				friendsWish = result.getJSONArray("wishlist");
				
				for(int i=0; i<friendsWish.length(); i++) {
					JSONObject c = friendsWish.getJSONObject(i);
					String phone = c.getString("phone");
					String title = c.getString("title");
					int price = c.getInt("price");
					int wish = c.getInt("wish");
					int bookmarkOn = c.getInt("bookmark");
					String bookmark;
					int eventOn = c.getInt("event");
					String event;
					String date = c.getString("date");
					String imagePath = c.getString("image");
					int webId = c.getInt("id");

					if (bookmarkOn == 0) {
						bookmark = "false";
					} else {
						bookmark = "true";
					}
					
					if (eventOn == 0) {
						event = "false";
					} else {
						event = "true";
					}
										
					if (!fragment.checkFWishlistData(phone)) {
						fragment.insertFWishlistData(phone, title, price, wish, date, imagePath, bookmark, event, webId);						
						Log.i("PJM", "insert");
					} else {
						Log.i("PJM", "Not insert");
					}
				
				}
				
			} catch (Exception e) {
				
			}
			
		}
	}
}
