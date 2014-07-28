package com.example.giv2gether;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewsFeedFragment extends Fragment {

	static final String TAG = "naddola";

	/*
	 * Views
	 */

	MainActivity mActivity;
	Giv2DBManager dbManager;

	View rootView;
	TextView noFeed;
	ListView feedListView;
	NewsfeedAdapter mAdapter;
	ArrayList<String> feedlist;
	ArrayList<Newsfeed> newsfeedlist;
	ArrayList<MyFriend> arrMyFriendList;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_newsfeed, container, false);

		initViews();
		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem item1 = menu.add(0, 0, 0, "All Delete");
		item1.setIcon(android.R.drawable.ic_menu_edit);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	public void initViews() {

		feedlist = new ArrayList<String>();
		newsfeedlist = new ArrayList<Newsfeed>();

		mActivity = (MainActivity) getActivity();
		dbManager = mActivity.getDBManager();

		arrMyFriendList = dbManager.getGivFriendsList();
		newsfeedlist = (ArrayList<Newsfeed>) mActivity.getIntent()
				.getSerializableExtra("newsfeedlist");

		noFeed = (TextView) rootView.findViewById(R.id.noFeed);
		feedListView = (ListView) rootView.findViewById(R.id.feedList);

		mAdapter = new NewsfeedAdapter(getActivity().getApplicationContext(),
				R.layout.custom_newsfeed_list, newsfeedlist);

		feedListView.setAdapter(mAdapter);
		feedListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		feedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent;

				for (int i = 0; i < arrMyFriendList.size(); i++) {
					if (arrMyFriendList.get(i).getEmail()
							.equals(newsfeedlist.get(position).getEmail())) {

						int FeedTag = newsfeedlist.get(position).getTag();
						MyFriendsWish myFwish = selectFWishlistData(arrMyFriendList
								.get(i).getPhone());
						if (myFwish == null) {
							Toast.makeText(mActivity.getApplicationContext(),
									"친구의 위시가 없네요", Toast.LENGTH_SHORT).show();
							return;
						}

						String email = arrMyFriendList.get(i).getEmail();
						String name = arrMyFriendList.get(i).getName();
						String title = myFwish.getTitle();
						int wish = myFwish.getWish();
						String imagePath = myFwish.getImagePath();
						int webId = myFwish.getId();

						switch (FeedTag) {

						case Newsfeed.PUT_FRIEND_BIRTH_DDAY:
							intent = new Intent(mActivity,
									EventGenerationActivity.class);
							intent.putExtra("email", email);
							intent.putExtra("name", name);
							intent.putExtra("title", title);
							intent.putExtra("wish", wish);
							intent.putExtra("imagePath", imagePath);
							intent.putExtra("webId", webId);
							startActivity(intent);
							break;

						case Newsfeed.PUT_EVENT_GENERATION:
							intent = new Intent(mActivity,
									EventPartyActivity.class);
							intent.putExtra("name", name);
							intent.putExtra("title", title);
							intent.putExtra("wish", wish);
							intent.putExtra("imagePath", imagePath);
							intent.putExtra("webId", webId);
							startActivity(intent);
							break;
						}
					}
				}
			}
		});

		if (newsfeedlist.size() == 0) {
			noFeed.setVisibility(View.VISIBLE);
		} else {
			noFeed.setVisibility(View.GONE);
		}

		if (newsfeedlist.size() > 0) {
			noFeed.setVisibility(View.GONE);
		}

		// selectFriendsAll();
		/*
		 * try { JSONObject jObj = new AsyncCheckNewsFeed().execute(
		 * "http://naddola.cafe24.com/getNewsFeedDDays.php").get(); JSONArray
		 * feed = jObj.getJSONArray("feed");
		 * 
		 * 
		 * for(int i=0; i<feed.length(); i++) { JSONObject c =
		 * feed.getJSONObject(i);
		 * 
		 * String name = c.getString("name"); String phone =
		 * c.getString("phone"); String birth = c.getString("birth"); int dday =
		 * c.getInt("dday");
		 * 
		 * for(int j=0; j<arrMyFriendList.size(); j++) { if
		 * (phone.equals(arrMyFriendList.get(j).getPhone())) { if (dday < 16)
		 * feedlist.add(arrMyFriendList.get(j).getName()+"님의 생일이 " + dday +
		 * "일 전입니다"); } }
		 * 
		 * }
		 * 
		 * if (feedlist.size() > 0) { noFeed.setVisibility(View.INVISIBLE);
		 * mAdapter.notifyDataSetChanged(); }
		 * 
		 * } catch (Exception e) {
		 * 
		 * }
		 */
	}

	class NewsfeedViewHolder {
		int Tag = 0;
		TextView mNotice;

		public NewsfeedViewHolder(int Tag, TextView mNotice) {
			this.Tag = Tag;
			this.mNotice = mNotice;
		}
	}

	class NewsfeedAdapter extends ArrayAdapter<Newsfeed> {

		ArrayList<Newsfeed> list = new ArrayList<Newsfeed>();

		public NewsfeedAdapter(Context context, int resource,
				ArrayList<Newsfeed> objects) {
			super(context, resource, objects);
			list = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			NewsfeedViewHolder viewHolder = null;
			TextView mNotice = null;

			final int pos = position;

			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_newsfeed_list, null);

				mNotice = (TextView) v.findViewById(R.id.newsfeedNotice);
				if (list.get(position).getTag() == Newsfeed.PUT_FRIEND_BIRTH_DDAY)
					viewHolder = new NewsfeedViewHolder(
							Newsfeed.PUT_FRIEND_BIRTH_DDAY, mNotice);
				else if (list.get(position).getTag() == Newsfeed.PUT_EVENT_GENERATION)
					viewHolder = new NewsfeedViewHolder(
							Newsfeed.PUT_EVENT_GENERATION, mNotice);
				else if (list.get(position).getTag() == Newsfeed.PUT_MYEVENT_CHANGE)
					;

				v.setTag(viewHolder);
			} else {
				viewHolder = (NewsfeedViewHolder) v.getTag();

				mNotice = viewHolder.mNotice;
			}

			final Newsfeed mData = list.get(position);

			if (mData != null) {
				mNotice.setText(mData.getNotice());
			}
			return v;
		}
	}

	public MyFriendsWish selectFWishlistData(String phone_index) {
		Cursor result = dbManager.selectFWishlistData(phone_index);
		MyFriendsWish myFWish = null;

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

			myFWish = new MyFriendsWish(id, phone, title, price, wish, eventOn,
					date, imagePath, bookmarkOn, null);
		}

		result.close();

		return myFWish;
	}

}
