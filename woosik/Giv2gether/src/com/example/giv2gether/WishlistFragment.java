package com.example.giv2gether;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class WishlistFragment extends Fragment {

	/*
	 * 		Views
	 */
	
	View rootView;
	Button btnAddWish;
	ListView listMyWish;
	
	ArrayAdapter<String> mAdapter;
	
	/*
	 * 		Variables
	 */
	
	MainActivity mActivity;
	ArrayList<String> arrWishList;
	ArrayList<MyWish> arrMyWishList;

	
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
		
		mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, arrWishList);
		
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
				Toast.makeText(mActivity.getApplicationContext(), mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
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
						+ arrMyWishList.get(position).getEventOn() + " "
						+ arrMyWishList.get(position).getDate() );
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
			
			insertWishlistData(title, price);
			
			break;
		default:
			break;
		}
	}
	
	public void insertWishlistData (String title, int price) {
		mActivity.insertWishlistData(title, price);

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
		
		arrWishList.clear();
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			int id = result.getInt(0);
			String title = result.getString(1);
			int price = result.getInt(2);
			String eventOn = result.getString(3);
			String date = result.getString(4);

			MyWish myWish = new MyWish(id, title, price, eventOn, date);
			
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

}
