package com.example.giv2gether;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsFragment extends Fragment {

	/*
	 * 		Views
	 */
	View rootView;
	ListView friendsList;
	FriendsAdapter mAdapter;
	
	MainActivity mActivity;
	ArrayList<MyFriends> friends;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_friends, container, false);
		
		initViews();
		
		return rootView;
	}
	
	public void initViews() {
		mActivity = (MainActivity) getActivity();
		friends = new ArrayList<MyFriends>();
		friendsList = (ListView) rootView.findViewById(R.id.friendsList);

		friends.add(new MyFriends(1, "PJM", "(1989-10-09)"));
		friends.add(new MyFriends(1, "PCH", "(1994-07-20)"));
		
		mAdapter = new FriendsAdapter(mActivity.getApplicationContext(), R.layout.custom_friends_list, friends);
		
		friendsList.setAdapter(mAdapter);
	}
	
	class FriendsViewHolder {
		TextView fName, fBirth;
		ImageView fWish;
		
		public FriendsViewHolder(TextView name, TextView birth, ImageView wish){
			this.fName = name;
			this.fBirth = birth;
			this.fWish = wish;
		}
	}
	
	class FriendsAdapter extends ArrayAdapter<MyFriends> {

		ArrayList<MyFriends> list;
		
		public FriendsAdapter(Context context, int resource,
				ArrayList<MyFriends> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			list = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			TextView fName, fBirth;
			ImageView fWish;
			
			FriendsViewHolder viewHolder;
			
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_friends_list, null);
				
				fName = (TextView) v.findViewById(R.id.friendsName);
				fBirth = (TextView) v.findViewById(R.id.friendsBirth);
				fWish = (ImageView) v.findViewById(R.id.friendsWishImage);
				
				viewHolder = new FriendsViewHolder(fName, fBirth, fWish);
				v.setTag(viewHolder);
			} else {
				viewHolder = (FriendsViewHolder) v.getTag();
				
				fName = viewHolder.fName;
				fBirth = viewHolder.fBirth;
				fWish = viewHolder.fWish;
			}

			fName.setText(list.get(position).getName());
			fBirth.setText(list.get(position).getBirth());
			fWish.setImageResource(R.drawable.image_loading);
			
			return v;
		}
		
	}
}
