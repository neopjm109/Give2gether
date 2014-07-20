package com.hmjcompany.give2gether;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.hmjcompany.give2gether.async.AsyncFriendsWish;
import com.hmjcompany.give2gether.async.ImageThread;
import com.hmjcompany.give2gether.async.getEventingFriendsHttpPostAsyncTask;

public class FriendsFragment extends Fragment {

	public static final String TAG = "naddola";

	View rootView;
	MainActivity mActivity;
	Giv2DBManager dbManager;

	SeparatedListAdapter baseAdapter;
	MyEventFriendAdapter EventFriendAdapter;
	MyGivFriendAdapter GivFriendAdapter;
	MyContactFriendAdapter ContactFriendAdapter;

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

		EventFriendAdapter = new MyEventFriendAdapter(getActivity()
				.getApplicationContext(), R.layout.custom_friend_list,
				arrEventFriendList);
		if (arrEventFriendList.size() > 0)
			baseAdapter.addSection("이벤트 중인 친구들", EventFriendAdapter);
		GivFriendAdapter = new MyGivFriendAdapter(getActivity()
				.getApplicationContext(), R.layout.custom_friend_list,
				arrGivFriendList);
		if (arrGivFriendList.size() > 0)
			baseAdapter.addSection("Giv 친구들", GivFriendAdapter);
		ContactFriendAdapter = new MyContactFriendAdapter(getActivity()
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

				GivFriendAdapter = new MyGivFriendAdapter(getActivity()
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
			
			if (editOn) {
				item.setIcon(android.R.drawable.ic_menu_save);
			} else {
				item.setIcon(android.R.drawable.ic_menu_edit);
			}

			baseAdapter.notifyDataSetChanged();
			
			break;
		case 1:
			editOn = false;
			
			baseAdapter.notifyDataSetChanged();
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
	
	class MyEventFriendAdapter extends ArrayAdapter<MyFriend> {

		ArrayList<MyFriend> list = new ArrayList<MyFriend>();

		public MyEventFriendAdapter(Context context, int resource,
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
			}

			final MyFriend mData = list.get(position);

			if (mData != null) {
				mName.setText(mData.getName());
				if (mData.getBirth() != null)
					mBirth.setText(mData.getBirth());
			}
			
			mImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(editOn) {
						removeFriendsData(mData.getPhone());
					}
				}
			});

			selectFWishlistData(mData.getPhone());

			try {
				JSONObject jObj = new AsyncFriendsWish().
						execute("http://naddola.cafe24.com/getFriendWish.php?phone="+mData.getPhone()).get();
				JSONArray friendsWish;

				if(jObj != null) {
					
					friendsWish = jObj.getJSONArray("wishlist");

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
						
						if (!checkFWishlistData(phone)) {
							insertFWishlistData(phone, title, price, wish, date, imagePath, bookmark, event, webId);					
						}

						Bitmap bmp = new ImageThread().execute(imagePath).get();
						if(!editOn) {
							mImage.setImageBitmap(bmp);
						} else {
							mImage.setImageResource(android.R.drawable.ic_menu_delete);
						}
						EventFriendAdapter.notifyDataSetChanged();

					}
				}
				
			} catch (Exception e) {
				
			}

			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					String title = null;
					int wish = 0;
					String imagePath = null;
					int webId = 0;
					
					for (int i=0; i<arrMyFriendsWishList.size(); i++) {
						if (mData.getPhone().equals(arrMyFriendsWishList.get(i).phone)) {
							title = arrMyFriendsWishList.get(i).getTitle();
							wish = arrMyFriendsWishList.get(i).getWish();
							imagePath = arrMyFriendsWishList.get(i).getImagePath();
							webId = arrMyFriendsWishList.get(i).getWebId();
							break;
						}
					}
					
					Intent intent = new Intent(mActivity, EventPartyActivity.class);
					intent.putExtra("name", mData.getName());
					intent.putExtra("title",title);
					intent.putExtra("wish", wish);
					intent.putExtra("imagePath", imagePath);
					intent.putExtra("webId", webId);
					startActivity(intent);
					
				}
			});
			return v;
		}
	}

	class MyGivFriendAdapter extends ArrayAdapter<MyFriend> {

		ArrayList<MyFriend> list = new ArrayList<MyFriend>();

		public MyGivFriendAdapter(Context context, int resource,
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
			}

			final MyFriend mData = list.get(position);

			if (mData != null) {
				mName.setText(mData.getName());
				if (mData.getBirth() != null)
					mBirth.setText(mData.getBirth());
			}
			
			mImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(editOn) {
						dbManager.removeFriendsData(mData.getPhone());
					}
				}
			});
			
			selectFWishlistData(mData.getPhone());

			try {
				JSONObject jObj = new AsyncFriendsWish().execute("http://naddola.cafe24.com/getFriendWish.php?phone="+mData.getPhone()).get();
				JSONArray friendsWish = jObj.getJSONArray("wishlist");
				
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
					
					if (!checkFWishlistData(phone)) {
						insertFWishlistData(phone, title, price, wish, date, imagePath, bookmark, event, webId);					
					}

					Bitmap bmp = new ImageThread().execute(imagePath).get();
					if(!editOn) {
						mImage.setImageBitmap(bmp);
					} else {
						mImage.setImageResource(android.R.drawable.ic_menu_delete);
					}
					GivFriendAdapter.notifyDataSetChanged();

				}
			} catch (Exception e) {
				
			}			
			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					String title = null;
					int wish = 0;
					String imagePath = null;
					int webId = 0;
					boolean bNull = false;
					
					for (int i=0; i<arrMyFriendsWishList.size(); i++) {
						if (mData.getPhone().equals(arrMyFriendsWishList.get(i).phone)) {
							title = arrMyFriendsWishList.get(i).getTitle();
							wish = arrMyFriendsWishList.get(i).getWish();
							imagePath = arrMyFriendsWishList.get(i).getImagePath();
							webId = arrMyFriendsWishList.get(i).getWebId();
							break;
						}
						
						if ( i == (arrMyFriendsWishList.size()-1) &&
								!mData.getPhone().equals(arrMyFriendsWishList.get(i).phone)) {
							bNull = true;
						}
					}
					
					if (!bNull) {
					
						Intent intent = new Intent(mActivity, EventGenerationActivity.class);
						intent.putExtra("email", mData.getEmail());
						intent.putExtra("name", mData.getName());
						intent.putExtra("title",title);
						intent.putExtra("wish", wish);
						intent.putExtra("imagePath", imagePath);
						intent.putExtra("webId", webId);
						startActivity(intent);
						
					} else {
						
						Toast.makeText(mActivity.getApplicationContext(), "친구의 위시가 없네요", Toast.LENGTH_SHORT).show();
						
					}
					
				}
			});
			return v;
		}
	}

	class MyContactFriendAdapter extends ArrayAdapter<MyFriend> {

		ArrayList<MyFriend> list = new ArrayList<MyFriend>();

		public MyContactFriendAdapter(Context context, int resource,
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

			if(editOn) {
				viewHolder.mImage.setImageResource(android.R.drawable.ic_menu_delete);
			} else {
				viewHolder.mImage.setImageResource(R.drawable.image_loading);
			}
			
			mImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(editOn) {
						removeFriendsData(mData.getPhone());
					}
				}
			});

			selectFWishlistData(mData.getPhone());
			
			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
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

		try {
			JSONArray jsonArr = new getEventingFriendsHttpPostAsyncTask(arrGivFriendList).execute().get();
			
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			
		}
		return myFriends;

	}

	/*
	 * 		DB Function
	 */

	public void removeFriendsData(String phone) {
		dbManager.removeFriendsData(phone);
		
		for(int i=0; i<arrEventFriendList.size(); i++) {
			if(arrEventFriendList.get(i).getPhone().equals(phone)) {
				arrEventFriendList.remove(i);
				break;
			}
		}
		
		for(int i=0; i<arrGivFriendList.size(); i++) {
			if(arrGivFriendList.get(i).getPhone().equals(phone)) {
				arrGivFriendList.remove(i);
				break;
			}
		}
		
		for(int i=0; i<arrContactFriendList.size(); i++) {
			if(arrContactFriendList.get(i).getPhone().equals(phone)) {
				arrContactFriendList.remove(i);
				break;
			}
		}
		
		baseAdapter.notifyDataSetChanged();
	}

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

			MyFriendsWish myFWish = new MyFriendsWish(id, phone, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);
			myFWish.setWebId(webId);
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

			result.moveToNext();
		}
		
		result.close();
		
	}
	
	public void updateFWishlistData(int flag, int id, String query) {
		dbManager.updateFWishlistData(flag, id, query);
	}
	
	public void removeFWishlistData(int index, int webId) {
		dbManager.removeFWishlistData(index);
	}

}
