package com.example.giv2gether;

import java.io.IOException;
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

				viewHolder.imagePath = list.get(position).getImagePath(); // When
																			// list
																			// items
																			// are
																			// deleted
																			// or
																			// Added,
																			// reinitialized
																			// new
																			// position
				viewHolder.mImage.setImageResource(R.drawable.image_loading);
			}

			final MyFriend mData = list.get(position);

			if (mData != null) {
				// new MyWishImageThread().execute(viewHolder);
				mName.setText(mData.getName());
				if (mData.getBirth() != null)
					mBirth.setText(mData.getBirth());
			}

			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mActivity,
							EventGenerationActivity.class);
					intent.putExtra("name", mData.getName());
					startActivity(intent);
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
}
