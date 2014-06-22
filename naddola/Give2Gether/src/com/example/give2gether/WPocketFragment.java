package com.example.give2gether;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
				} else {
					arrMyWishList.get(position).bookmarkOn = "true";
					query = "true";
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
			
			insertWishlistData(title, price, wish, imagePath);
			
			break;
		default:
			break;
		}
	}
	
	/*
	 * 		DB Function
	 */
	
	public void insertWishlistData (String title, int price, int wish, String imagePath) {
		dbManager.insertWishlistData(title, price, wish, imagePath);

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
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);

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
			
			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath, bookmarkOn, null);

			new ImageThread().execute(myWish);
	
			result.moveToNext();
		}

		result.close();
		
	}
	
	public void updateWishlistData(int flag, int id, String query) {
		dbManager.updateWishlistData(flag, id, query);
	}
	
	public void removeWishlistData(int index) {
		dbManager.removeWishlistData(index);
		mAdapter.notifyDataSetChanged();
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
				mPrice.setText(df.format(mData.getPrice()) + " Wish");
				
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
							removeWishlistData(arrMyWishList.get(pos).getId());

							arrMyWishList.remove(pos);
						}
					});
				}
				
			}
			/*
			v.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub

					switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						x = (int) event.getX();
						y = (int) event.getY();
						minViewMovingX = (int) event.getX();
						maxViewMovingX = (int) event.getX();
						
						bLongPress = false;
						postCheckForLongClick(pos, 1000);
												
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;
						
					case MotionEvent.ACTION_UP:

						removeLongPressCallback();
						
						v.setPadding(0, v.getPaddingTop(),
								v.getPaddingRight(), v.getPaddingBottom());
						
						if ( (x - event.getX()) > 200 ) {
							removeWishlistData(arrMyWishList.get(pos).getId());

							arrMyWishList.remove(pos);
						} else if ( (event.getX() - x) > 200) {
							
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
						speedJitter = 10;
						minViewMovingX = 0;
						maxViewMovingX = 0;
						
						break;
						
					case MotionEvent.ACTION_MOVE:
						
						int mTouchSlop = ViewConfiguration.get(mActivity).getScaledTouchSlop();
						
						int deltaX = Math.abs((int)(x - event.getX()));
						
						if (deltaX >= mTouchSlop) {
							if (!bLongPress) {
								removeLongPressCallback();
							}
						}
						
						if (x > event.getX()) {

							maxViewMovingX = x;
							
							if (minViewMovingX > event.getX()) {
								minViewMovingX = (int) event.getX();
								
								if (speed > -100) {
									speed -= speedJitter;
									
									if(speedJitter > 0) {
										speedJitter -= 0.5;
									}
								}
								
							} else {
								if (speed < 0) {
									speed += speedJitter;
									
									if (speedJitter < 10) {
										speedJitter += 0.5;								
									}
								}
							}
							
							v.setPadding((int)speed, v.getPaddingTop(),
									v.getPaddingRight(), v.getPaddingBottom());
							
						} else {
							
							minViewMovingX = x;
							
							if (maxViewMovingX < event.getX()) {
								maxViewMovingX = (int) event.getX();
								
								if (speed < 100) {
									speed += speedJitter;

									if(speedJitter > 0) {
										speedJitter -= 0.5;
									}
								}
								
							} else {
								if (speed > 0) {
									speed -= speedJitter;

									if (speedJitter < 10) {
										speedJitter += 0.5;										
									}
								}
							}
							
							v.setPadding((int)speed, v.getPaddingTop(),
									v.getPaddingRight(), v.getPaddingBottom());
							
						}

						if (speed == 10) {
							x = (int) event.getX();
						}
						break;
					}
					
					return true;
				}
			});*/
			
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
						removeWishlistData(arrMyWishList.get(pos).getId());

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
}
