package com.example.giv2gether;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsFragment extends Fragment implements OnClickListener {

	View rootView;
	MyFriendAdapter adapter;
	MainActivity mActivity;
	Giv2DBManager dbManager;
	ArrayList<MyFriend> arrMyFriendList;

	Button bt_AddFriends;
	ListView listFriend;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_friends, container, false);

		//init();

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		init();
	}

	public void init() {
		mActivity = (MainActivity) getActivity();
		dbManager = mActivity.getDBManager();
		bt_AddFriends = (Button) rootView.findViewById(R.id.fAddFriends);
		bt_AddFriends.setOnClickListener(this);
		listFriend = (ListView) rootView.findViewById(R.id.friend_listview);

		arrMyFriendList = new ArrayList<MyFriend>();
		
		arrMyFriendList = dbManager.getFriendsList();
		
		adapter = new MyFriendAdapter(getActivity().getApplicationContext(),
				R.layout.custom_friend_list, arrMyFriendList);
		
		listFriend.setAdapter(adapter);
		listFriend.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fAddFriends:
			Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
			startActivity(intent);
			break;
		}
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
				//LayoutInflater inflater = (LayoutInflater) mActivity
				//		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LayoutInflater inflater = (LayoutInflater) getActivity()
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
				//mBirth.setText(mData.getBirth());
			}
			/*
			 * v.setOnTouchListener(new View.OnTouchListener() {
			 * 
			 * @Override public boolean onTouch(View v, MotionEvent event) { //
			 * TODO Auto-generated method stub
			 * 
			 * switch(event.getAction()) { case MotionEvent.ACTION_DOWN:
			 * 
			 * x = (int) event.getX(); y = (int) event.getY();
			 * 
			 * v.getParent().requestDisallowInterceptTouchEvent(true); break;
			 * 
			 * case MotionEvent.ACTION_UP: Log.i("PJM", "Up"); v.setPadding(0,
			 * v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
			 * 
			 * if ( (x - event.getX()) > 150 ) {
			 * removeWishlistData(arrMyWishList.get(pos).getId());
			 * 
			 * arrMyWishList.remove(pos); }
			 * 
			 * break;
			 * 
			 * case MotionEvent.ACTION_MOVE: Log.i("PJM", "Move");
			 * 
			 * if (x > event.getX()) v.setPadding(v.getPaddingLeft() - 10,
			 * v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
			 * else v.setPadding(v.getPaddingLeft() + 10, v.getPaddingTop(),
			 * v.getPaddingRight(), v.getPaddingBottom());
			 * 
			 * break; }
			 * 
			 * return true; } });
			 */

			return v;
		}

	}
}
