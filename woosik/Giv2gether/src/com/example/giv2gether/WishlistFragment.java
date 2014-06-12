package com.example.giv2gether;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
	ArrayList<String> arrWishList;
	ArrayList<MyWish> arrMyWishList;

	Bitmap bm = null;

	DecimalFormat df = new DecimalFormat("#,##0");
	
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
		
		arrWishList = new ArrayList<String>();
		arrMyWishList = new ArrayList<MyWish>();

		selectWishAll();
		
		mAdapter = new MyWishAdapter(getActivity().getApplicationContext(), R.layout.custom_wish_list, arrMyWishList);
		
		listMyWish.setAdapter(mAdapter);
		listMyWish.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		btnAddWish.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if (arrWishList.size() < 3) {
					Intent i = new Intent(mActivity.getApplicationContext(), AddWishActivity.class);
					startActivityForResult(i, 1001);
				} else {
					Toast.makeText(mActivity.getApplicationContext(), "Can't put more 3", Toast.LENGTH_SHORT).show();
				}
				
//				insertWishlistData("Notebook", 10000);
			}
		});
		
		listMyWish.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Toast.makeText(mActivity.getApplicationContext(), mAdapter.getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
			}
		});
		
		listMyWish.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> partent, View view,
					int position, long id) {
				AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
				final int pos = position;
				
				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						removeWishlistData(arrMyWishList.get(pos).getId());

						arrWishList.remove(pos);
						arrMyWishList.remove(pos);
						mAdapter.notifyDataSetChanged();
					}
				});
				
				alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
				});
				
				alert.setTitle("Are you delete?");
				alert.setMessage(arrMyWishList.get(position).getId() + " "
						+ arrMyWishList.get(position).getTitle() + " "
						+ arrMyWishList.get(position).getPrice() + " "
						+ arrMyWishList.get(position).getWish() + " "
						+ arrMyWishList.get(position).getEventOn() + " "
						+ arrMyWishList.get(position).getDate() + " "
						+ arrMyWishList.get(position).getImagePath() );
				alert.show();
				
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
	
	public void insertWishlistData (String title, int price, int wish, String imagePath) {
		mActivity.insertWishlistData(title, price, wish, imagePath);

		selectWishAll();
		
		mAdapter.notifyDataSetChanged();
	}
	
	public void selectWishlistData(int index) {
		Cursor result = mActivity.selectWishlistData(index);
		
		if (result.moveToFirst()) {
			String title = result.getString(1);
			arrWishList.add(title);
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

			MyWish myWish = new MyWish(id, title, price, wish, eventOn, date, imagePath);
			
			arrWishList.add(title);
			arrMyWishList.add(myWish);
			
			result.moveToNext();
		}
		
		result.close();
		
	}
	
	public void updateWishlistData() {
		
	}
	
	public void removeWishlistData(int index) {
		mActivity.removeWishlistData(index);
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
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.custom_wish_list, null);
			}
			
			MyWish mData = list.get(position);
			
			if (mData != null) {
				ImageView mImage = (ImageView) v.findViewById(R.id.wishImage);
				TextView mTitle = (TextView) v.findViewById(R.id.wishTitle);
				TextView mPrice = (TextView) v.findViewById(R.id.wishPrice);
				
				new ImageThread(mImage).execute(mData.getImagePath());
				
				mImage.setImageBitmap(bm);
				mTitle.setText(mData.getTitle());
				mPrice.setText(df.format(mData.getPrice()) + " Wish");
				
			}
			
			return v;
		}
		
	}
	
	class ImageThread extends AsyncTask<String, Void, Bitmap> {

		ImageView image;
		
		public ImageThread(ImageView imageView) {
			this.image = imageView;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				bm = null;
				URL url = new URL(params[0]);
				bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			image.setImageBitmap(result);
		}
	}
	
}
