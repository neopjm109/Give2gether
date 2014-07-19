package com.hmjcompany.give2gether;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

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

import com.hmjcompany.give2gether.async.AsyncTaskWPocketSynchronize;
import com.hmjcompany.give2gether.async.ImageThread;
import com.hmjcompany.give2gether.async.RemoveMyWish;
import com.hmjcompany.give2gether.async.UpdateBookmark;

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
	
	SettingPreference settings;
	
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
		
		mActivity.getIntent().getStringArrayListExtra("myWishList");
//		SyncWPocket();
		
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
		settings = new SettingPreference(mActivity);
		listMyWish = (ListView) rootView.findViewById(R.id.listMyWish);
		
		mHandler = new Handler();
		
		arrMyWishList = new ArrayList<MyWish>();

		mAdapter = new MyWishAdapter(getActivity().getApplicationContext(), R.layout.custom_wish_list, arrMyWishList);

		listMyWish.setAdapter(mAdapter);
		listMyWish.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listMyWish.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				final int pos = position;
				AlertDialog.Builder mDialog = new AlertDialog.Builder(mActivity);
				View dialogView = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_wish, null);
				
				mDialog.setView(dialogView);

				ImageView image = (ImageView) dialogView.findViewById(R.id.dialogImage);
				TextView price = (TextView) dialogView.findViewById(R.id.dialogPrice);

				try {
					Bitmap bmp = new ImageThread().execute(arrMyWishList.get(pos).getImagePath()).get();
					
					if(bmp != null) {
						image.setImageBitmap(bmp);
					} else {
						image.setImageResource(R.drawable.image_loading);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				price.setText("쇼핑몰 최저가 : " + df.format(arrMyWishList.get(pos).getPrice()) + " 원\n"
						+ "Wish : " + df.format(arrMyWishList.get(pos).getWish()) + " \n");
				
				mDialog.setTitle(arrMyWishList.get(pos).getTitle());

				mDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				
				mDialog.show();
			}
		});
		
		listMyWish.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				String query = null;

				if (arrMyWishList.get(position).bookmarkOn.equals("true")) {
					arrMyWishList.get(position).bookmarkOn = "false";
					query = "false";
					new UpdateBookmark(arrMyWishList.get(position).getId(), 0).execute();
				} else {
					arrMyWishList.get(position).bookmarkOn = "true";
					query = "true";
					new UpdateBookmark(arrMyWishList.get(position).getId(), 1).execute();
				}
				
				updateWishlistData(0, arrMyWishList.get(position).getId(), query);
				mAdapter.notifyDataSetChanged();
				
				return true;
			}
		});

		selectWishAll();
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(resultCode) {
		case 1001:
			int webId = Integer.parseInt(data.getStringExtra("webId"));
			String title = data.getStringExtra("title");
			int price = Integer.parseInt(data.getStringExtra("price"));
			int wish = Integer.parseInt(data.getStringExtra("wish"));
			String imagePath = data.getStringExtra("image");
			
			insertWishlistData(webId, title, price, wish, imagePath, "false");
			
			break;
		default:
			break;
		}
	}
	
	/*
	 * 		DB Function
	 */
	
	public void insertWishlistData (int id, String title, int price, int wish, String imagePath, String bookMark) {
		dbManager.insertWishlistData(id, title, price, wish, imagePath, bookMark);

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

			Toast.makeText(mActivity.getApplicationContext(), myWish.getTitle(), Toast.LENGTH_SHORT).show();
		}
		
		result.close();
	}
	
	public boolean checkWishlistData(int webId) {
		Cursor result = dbManager.selectWishlistData(webId);
		if (result.getCount() == 1) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public void selectWishAll() {
		Cursor result = dbManager.selectWishAll();
		
		arrMyWishList.clear();
		
		result.moveToFirst();
		
		int i=0;
		while (!result.isAfterLast()) {
			int id = result.getInt(0);
			String title = result.getString(1);
			int price = result.getInt(2);
			int wish = result.getInt(3);
			String eventOn = result.getString(4);
			String date = result.getString(5);
			String imagePath = result.getString(6);
			String bookmarkOn = result.getString(7);
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);

			try {
				new ImageMyWishThread(myWish).execute();
				/*		
				Bitmap bmp = new ImageThread().execute(myWish.getImagePath()).get();
				if(bmp != null) {
					myWish.setBmp(bmp);
					arrMyWishList.add(myWish);
					mAdapter.notifyDataSetChanged();
				}*/
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
							removeWishlistData(arrMyWishList.get(pos).getId(), arrMyWishList.get(pos).getId());

							arrMyWishList.remove(pos);
						}
					});
				}
				
			}
			
			return v;
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
						break;
						
					case 2:			// Remove Func
						removeWishlistData(arrMyWishList.get(pos).getId(), arrMyWishList.get(pos).getId());

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
	 * 		Synchronize my wishlist
	 */
	
	public void SyncWPocket() {
		try {
			JSONObject jObj = new AsyncTaskWPocketSynchronize().execute("http://naddola.cafe24.com/selectMyWishlist.php?email="+settings.getID()).get();
			JSONArray wishlist;
			
			wishlist = jObj.getJSONArray("wishlist");
			
			for(int i=0; i<wishlist.length(); i++) {
				JSONObject c = wishlist.getJSONObject(i);
				int webId = c.getInt("id");
				String title = c.getString("title");
				int price = c.getInt("price");
				int wish = c.getInt("wish");
				int bookmarkOn = c.getInt("bookmark");
				String bookmark;
				int eventOn = c.getInt("event");
				String date = c.getString("date");
				String imagePath = c.getString("image");
				
				if (bookmarkOn == 0) {
					bookmark = "false";
				} else {
					bookmark = "true";
				}
				
				if (!checkWishlistData(webId)) {
					insertWishlistData(webId, title, price, wish, imagePath, bookmark);
					
				} else {
				}
				
			}
		} catch (Exception e) {
			
		}
	}
	
	class ImageMyWishThread extends AsyncTask<String, Void, Bitmap> {

		Bitmap bmp;
		MyWish myWish;
		
		public ImageMyWishThread(MyWish myWish) {
			this.myWish = myWish;
		}
		
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(myWish.getImagePath());
				bmp = BitmapFactory.decodeStream(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return bmp;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);

			myWish.setBmp(result);
			arrMyWishList.add(myWish);
			mAdapter.notifyDataSetChanged();
			
		}

	}
}
