package com.hmjcompany.give2gether;

import java.util.ArrayList;

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
		arrWishList.add("ABC");
		arrWishList.add("XYZ");
		
		mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, arrWishList);
		
		listMyWish.setAdapter(mAdapter);
		listMyWish.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		btnAddWish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				insertWishlistData("Notebook", 10000);
			}
		});
		
		listMyWish.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Toast.makeText(mActivity.getApplicationContext(), mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void insertWishlistData(String title, int price) {
		mActivity.insertWishlistData(title, price);

		selectWishlistData(1);
		
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
	
	public void updateWishlistData() {
		
	}
	
	public void removeWishlistData() {
		
	}

}
