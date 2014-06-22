package com.hmjcompany.give2gether;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsFeedFragment extends Fragment {

	/*
	 * 		Views
	 */
	
	View rootView;
	TextView noFeed;
	ListView feedListView;
	ArrayAdapter<String> mAdapter;
	ArrayList<String> feedlist;
	
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
		
		noFeed = (TextView) rootView.findViewById(R.id.noFeed);
		feedListView = (ListView) rootView.findViewById(R.id.feedList);
		
		mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, feedlist);
		
		feedListView.setAdapter(mAdapter);
		
		if (feedlist.size() == 0) {
			noFeed.setVisibility(View.VISIBLE);
		} else {
			noFeed.setVisibility(View.INVISIBLE);
		}
	}
}
