package com.hmjcompany.give2gether;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WishlistFragment extends Fragment {

	/*
	 * 		Views
	 */
	
	View rootView;
	Button btnAddWish;
	ListView listMyWish;
	
	MyWishAdapter mAdapter;
	
	/*
	 * 		Variables
	 */
	
	MainActivity mActivity;
	ArrayList<MyWish> arrMyWishList;

	Bitmap bm = null;

	DecimalFormat df = new DecimalFormat("#,##0");
	
	int x, y;
	int speed = 0;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_wish, container, false);
		
		initViews();
		
		return rootView;
	}
	
	public void initViews() {
		mActivity = (MainActivity) getActivity();
		btnAddWish = (Button) rootView.findViewById(R.id.btnAddWish);
		listMyWish = (ListView) rootView.findViewById(R.id.listMyWish);
		
		arrMyWishList = new ArrayList<MyWish>();

		selectWishAll();
		
		mAdapter = new MyWishAdapter(getActivity().getApplicationContext(), R.layout.custom_wish_list, arrMyWishList);
		
		listMyWish.setAdapter(mAdapter);
		listMyWish.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		btnAddWish.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if (arrMyWishList.size() < 3) {
					Intent i = new Intent(mActivity.getApplicationContext(), AddWishActivity.class);
					startActivityForResult(i, 1001);
				} else {
					Toast.makeText(mActivity.getApplicationContext(), "Can't put more 3", Toast.LENGTH_SHORT).show();
				}
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
			
			insertWishlistData(title, price, wish, imagePath);
			
			break;
		default:
			break;
		}
	}
	
	public void insertWishlistData (String title, int price, int wish, String imagePath) {
		mActivity.insertWishlistData(title, price, wish, imagePath);

		selectWishAll();

		mAdapter.notifyDataSetChanged();
	}
	
	public void selectWishlistData(int index) {
		Cursor result = mActivity.selectWishlistData(index);
		
		if (result.moveToFirst()) {
			
			int id = result.getInt(0);
			String title = result.getString(1);
			int price = result.getInt(2);
			int wish = result.getInt(3);
			String eventOn = result.getString(4);
			String date = result.getString(5);
			String imagePath = result.getString(6);
			String bookmarkOn = result.getString(7);
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, bm);

			Toast.makeText(mActivity.getApplicationContext(), myWish.getTitle(), Toast.LENGTH_SHORT).show();
		}
		
		result.close();
	}
	
	public void selectWishAll() {
		Cursor result = mActivity.selectWishAll();
		
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
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, bm);

			new ImageThread().execute(myWish);
	
			result.moveToNext();
		}

		result.close();
		
	}
	
	public void updateWishlistData(int flag, int id, String query) {
		mActivity.updateWishlistData(flag, id, query);
	}
	
	public void removeWishlistData(int index) {
		mActivity.removeWishlistData(index);
		mAdapter.notifyDataSetChanged();
	}
	
	class MyWishViewHolder {
		ImageView mImage = null;
		TextView mTitle, mPrice = null;
		String imagePath = null;
		Bitmap bmp = null;
		
		public MyWishViewHolder(ImageView mImage, TextView mTitle, TextView mPrice, String imagePath, Bitmap bmp) {
			this.mImage = mImage;
			this.mTitle = mTitle;
			this.mPrice = mPrice;
			this.imagePath = imagePath;
			this.bmp = bmp;
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
			ImageView mImage = null;
			TextView mTitle = null, mPrice = null;

			final int pos = position;

			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_wish_list, null);
				
				mImage = (ImageView) v.findViewById(R.id.wishImage);
				mTitle = (TextView) v.findViewById(R.id.wishTitle);
				mPrice = (TextView) v.findViewById(R.id.wishPrice);
				
				viewHolder = new MyWishViewHolder(mImage, mTitle, mPrice, list.get(position).getImagePath(), list.get(position).getBitmap());
				v.setTag(viewHolder);
			} else {
				viewHolder = (MyWishViewHolder) v.getTag();
				
				mImage = viewHolder.mImage;
				mTitle = viewHolder.mTitle;
				mPrice = viewHolder.mPrice;
				
				viewHolder.bmp = list.get(position).getBitmap();
				
			}

			final MyWish mData = list.get(position);
			
			if (mData != null) {
				mImage.setImageBitmap(viewHolder.bmp);
				mTitle.setText(mData.getTitle());
				mPrice.setText(df.format(mData.getPrice()) + " Wish");
				
				if (mData.getBookmarkOn().equals("true")) {
					v.setBackgroundColor(Color.WHITE);
				} else {
					v.setBackgroundColor(Color.TRANSPARENT);
				}
			}
			
			v.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub

					switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						x = (int) event.getX();
						y = (int) event.getY();
						
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;
						
					case MotionEvent.ACTION_UP:
						Log.i("PJM", "Up");
						v.setPadding(0, v.getPaddingTop(),
								v.getPaddingRight(), v.getPaddingBottom());
						
						if ( (x - event.getX()) > 250 ) {
							removeWishlistData(arrMyWishList.get(pos).getId());

							arrMyWishList.remove(pos);
						} else if ( (event.getX() - x) > 250) {
							
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
						}

						speed = 0;
						
						break;
						
					case MotionEvent.ACTION_MOVE:
						Log.i("PJM", "Move");
						
						if (x > event.getX()) {
							v.setPadding(speed, v.getPaddingTop(),
									v.getPaddingRight(), v.getPaddingBottom());
							if(speed > -100)
								speed-=10;
						} else {
							v.setPadding(speed, v.getPaddingTop(),
									v.getPaddingRight(), v.getPaddingBottom());
							if(speed < 100)
								speed+=10;
						}
						break;
					}
					
					return true;
				}
			});
			
			return v;
		}
		
	}

	class ImageThread extends AsyncTask<MyWish, Void, Bitmap> {

		MyWish myWish;
		Bitmap bmp;
		
		protected Bitmap doInBackground(MyWish... params) {
			try {
				myWish = params[0];
				URL url = new URL(params[0].imagePath);
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
}
