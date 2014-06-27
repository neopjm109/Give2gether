package com.hmjcompany.give2gether;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WPocketFragment extends Fragment {

	/*
	 * 		Views
	 */
	
	View rootView;
	ListView listMyWish;
	
	MyWishAdapter mAdapter;
	
	/*
	 * 		Variables
	 */
	
	MainActivity mActivity;
	Giv2DBManager dbManager;
	ArrayList<MyWish> arrMyWishList;

	DecimalFormat df = new DecimalFormat("#,##0");
	
	boolean editOn = false;
	
	//		Swipe
	int x, y;
	float speed = 0;
	float speedJitter = 10;

	int minViewMovingX = 0;
	int maxViewMovingX = 0;
	
	boolean bLongPress;
	CheckForLongPress longPress = null;
	Handler mHandler;
		
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_wpocket, container, false);
		
		initViews();
		setHasOptionsMenu(true);
		
		return rootView;
	}	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem item1 = menu.add(0, 0, 0, "Edit My Wish List");
		item1.setIcon(android.R.drawable.ic_menu_edit);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		MenuItem item2 = menu.add(0, 1, 1, "Add My Wish");
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
			
			mAdapter.notifyDataSetChanged();
			break;
		case 1:
			editOn = false;
			mAdapter.notifyDataSetChanged();
			if (arrMyWishList.size() < 3) {
				Intent i = new Intent(mActivity.getApplicationContext(), AddWishActivity.class);
				startActivityForResult(i, 1001);
			} else {
				Toast.makeText(mActivity.getApplicationContext(), "Can't put more 3", Toast.LENGTH_SHORT).show();
			}
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void initViews() {
		mActivity = (MainActivity) getActivity();
		dbManager = mActivity.getDBManager();
		listMyWish = (ListView) rootView.findViewById(R.id.listMyWish);
		
		mHandler = new Handler();
		
		arrMyWishList = new ArrayList<MyWish>();

		selectWishAll();
		
		mAdapter = new MyWishAdapter(getActivity().getApplicationContext(), R.layout.custom_wish_list, arrMyWishList);
		
		listMyWish.setAdapter(mAdapter);
		listMyWish.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listMyWish.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				String query = null;

				if (arrMyWishList.get(position).bookmarkOn.equals("true")) {
					arrMyWishList.get(position).bookmarkOn = "false";
					query = "false";
					new UpdateBookmark(arrMyWishList.get(position).getWebId(), 0).execute();
				} else {
					arrMyWishList.get(position).bookmarkOn = "true";
					query = "true";
					new UpdateBookmark(arrMyWishList.get(position).getWebId(), 1).execute();
				}
				
				updateWishlistData(0, arrMyWishList.get(position).getId(), query);
				mAdapter.notifyDataSetChanged();
				
				return false;
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(resultCode) {
		case 1001:
			String title = data.getStringExtra("title");
			int price = Integer.parseInt(data.getStringExtra("price"));
			int wish = Integer.parseInt(data.getStringExtra("wish"));
			String imagePath = data.getStringExtra("image");
			int webId = Integer.parseInt(data.getStringExtra("webId"));
			
			Log.i("PJM", webId+"");
			
			insertWishlistData(title, price, wish, imagePath, webId);
			
			break;
		default:
			break;
		}
	}
	
	/*
	 * 		DB Function
	 */
	
	public void insertWishlistData (String title, int price, int wish, String imagePath, int webId) {
		dbManager.insertWishlistData(title, price, wish, imagePath, webId);

		selectWishAll();

		mAdapter.notifyDataSetChanged();
	}
	
	public void selectWishlistData(int index) {
		Cursor result = dbManager.selectWishlistData(index);
		
		if (result.moveToFirst()) {
			
			int id = result.getInt(0);
			String title = result.getString(1);
			int price = result.getInt(2);
			int wish = result.getInt(3);
			String eventOn = result.getString(4);
			String date = result.getString(5);
			String imagePath = result.getString(6);
			String bookmarkOn = result.getString(7);
			int webId = result.getInt(8);
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);
			myWish.setWebId(webId);

			Toast.makeText(mActivity.getApplicationContext(), myWish.getTitle(), Toast.LENGTH_SHORT).show();
		}
		
		result.close();
	}
	
	public void selectWishAll() {
		Cursor result = dbManager.selectWishAll();
		
		arrMyWishList.clear();
		
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			int id = result.getInt(0);
			String title = result.getString(1);
			int price = result.getInt(2);
			int wish = result.getInt(3);
			String eventOn = result.getString(4);
			String date = result.getString(5);
			String imagePath = result.getString(6);
			String bookmarkOn = result.getString(7);
			int webId = result.getInt(8);
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);
			myWish.setWebId(webId);

			new ImageThread().execute(myWish);
	
			result.moveToNext();
		}

		result.close();
		
	}
	
	public void updateWishlistData(int flag, int id, String query) {
		dbManager.updateWishlistData(flag, id, query);
	}
	
	public void removeWishlistData(int index, int webId) {
		dbManager.removeWishlistData(index);
		mAdapter.notifyDataSetChanged();
		new RemoveMyWish(webId).execute();
	}
	
	/*
	 * 		for MyWish ArrayList & ArrayAdapter
	 */
	
	class MyWishViewHolder {
		ImageView mImage = null, mFunc = null;
		TextView mTitle, mPrice = null;
		String imagePath = null;
		Bitmap bmp = null;
		
		public MyWishViewHolder(ImageView mImage, ImageView func, TextView mTitle, TextView mPrice, String imagePath, Bitmap bmp) {
			this.mImage = mImage;
			this.mTitle = mTitle;
			this.mPrice = mPrice;
			this.imagePath = imagePath;
			this.bmp = bmp;
			this.mFunc = func;
		}
	}
	
	class MyWishAdapter extends ArrayAdapter<MyWish> {

		ArrayList<MyWish> list = new ArrayList<MyWish>();

		public MyWishAdapter(Context context, int resource, ArrayList<MyWish> objects) {
			super(context, resource, objects);
			
			list = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			final MyWishViewHolder viewHolder;
			ImageView mImage = null, mFunc = null;
			TextView mTitle = null, mPrice = null;

			final int pos = position;

			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_wish_list, null);
				
				mImage = (ImageView) v.findViewById(R.id.wishImage);
				mTitle = (TextView) v.findViewById(R.id.wishTitle);
				mPrice = (TextView) v.findViewById(R.id.wishPrice);
				mFunc = (ImageView) v.findViewById(R.id.wishFuncBtn);
				
				viewHolder = new MyWishViewHolder(mImage, mFunc, mTitle, mPrice, list.get(position).getImagePath(), list.get(position).getBitmap());
				v.setTag(viewHolder);
			} else {
				viewHolder = (MyWishViewHolder) v.getTag();
				
				mImage = viewHolder.mImage;
				mTitle = viewHolder.mTitle;
				mPrice = viewHolder.mPrice;
				mFunc = viewHolder.mFunc;
				
				viewHolder.bmp = list.get(position).getBitmap();
				
			}

			final MyWish mData = list.get(position);
			
			if (mData != null) {
				mImage.setImageBitmap(viewHolder.bmp);
				mTitle.setText(mData.getTitle());
				mPrice.setText(df.format(mData.getWish()) + " Wish");
				
				if (!editOn) {
					if (mData.getBookmarkOn().equals("true")) {
						mFunc.setImageResource(android.R.drawable.star_big_on);
						mFunc.setVisibility(View.VISIBLE);
						
					} else {
						mFunc.setVisibility(View.INVISIBLE);
					}
					mFunc.setOnClickListener(null);
				} else {
					mFunc.setVisibility(View.VISIBLE);
					mFunc.setImageResource(android.R.drawable.ic_menu_delete);
					
					mFunc.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							removeWishlistData(arrMyWishList.get(pos).getId(), arrMyWishList.get(pos).getWebId());

							arrMyWishList.remove(pos);
						}
					});
				}
				
			}
			
			return v;
		}
		
	}

	// 		Get a Image by url
	class ImageThread extends AsyncTask<MyWish, Void, Bitmap> {

		MyWish myWish;
		Bitmap bmp;
		
		protected Bitmap doInBackground(MyWish... params) {
			try {
				myWish = params[0];
				URL url = new URL(params[0].getImagePath());
				bmp = BitmapFactory.decodeStream(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return bmp;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			myWish.bmp = result;
			
			arrMyWishList.add(myWish);
			
			mAdapter.notifyDataSetChanged();
		}
	}	
	
	//		Long Click Runnable
	class CheckForLongPress implements Runnable {
		int pos;
		
		public CheckForLongPress(int pos) {
			this.pos = pos;
		}
		
		public void run() {
			bLongPress = true;
			AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
			
			ArrayList<String> menulist = new ArrayList<String>();
			if ( arrMyWishList.get(pos).getBookmarkOn().equals("true"))
				menulist.add("즐겨찾기 해제");
			else
				menulist.add("즐겨찾기 지정");
			menulist.add("수정하기");
			menulist.add("삭제");
			
			ArrayAdapter<String> dAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.dialog_menu, menulist);
			
			dialog.setTitle("메뉴");
			
			dialog.setAdapter(dAdapter, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {

					switch(which) {
					case 0:			// Bookmark Func
						
						String query = null;
						
						if (arrMyWishList.get(pos).bookmarkOn.equals("true")) {
							arrMyWishList.get(pos).bookmarkOn = "false";
							query = "false";
						} else {
							arrMyWishList.get(pos).bookmarkOn = "true";
							query = "true";
						}
						
						updateWishlistData(0, arrMyWishList.get(pos).getId(), query);
						mAdapter.notifyDataSetChanged();
						break;
						
					case 1:			// Modify Func
						Toast.makeText(mActivity, pos+"", Toast.LENGTH_SHORT).show();
//						Toast.makeText(mActivity.getApplicationContext(), "곧 나옵니다!", Toast.LENGTH_SHORT).show();
						break;
						
					case 2:			// Remove Func
						removeWishlistData(arrMyWishList.get(pos).getId(), arrMyWishList.get(pos).getWebId());

						arrMyWishList.remove(pos);
						break;
					}
					
					longPress = null;
				}
			});
			
			dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
					
					longPress = null;
				}
			});
			
			dialog.show();
		}
	}
	
	private void postCheckForLongClick(int position, int delayOffset) {
		bLongPress = false;
		
		if (longPress == null) {
			longPress = new CheckForLongPress(position);
		}
		
		mHandler.postDelayed(longPress, delayOffset);
	}
	
	private void removeLongPressCallback() {
		if (longPress != null) {
			mHandler.removeCallbacks(longPress);
		}
	}

	/*
	 * 		bookmark is updated in WEB Database
	 */
	
	private class UpdateBookmark extends AsyncTask<String, String, Void> {
		int id;
		int bookmark;
		
		public UpdateBookmark(int id, int bookmark) {
			this.id = id;
			this.bookmark = bookmark;
		}
		
		protected Void doInBackground(String... params) {
			
			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/updateWishBookmark.php";
				
				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("id", id+""));
				params2.add(new BasicNameValuePair("bookmark", bookmark+""));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}


	/*
	 * 		Remove wish function
	 */
	
	private class RemoveMyWish extends AsyncTask<String, String, Void> {
		int id;
		
		public RemoveMyWish(int id) {
			this.id = id;
		}
		
		protected Void doInBackground(String... params) {
			
			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/removeMyWish.php";
				
				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("id", id+""));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}

}
