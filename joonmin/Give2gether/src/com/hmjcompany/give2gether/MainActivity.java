package com.hmjcompany.give2gether;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


public class MainActivity extends FragmentActivity {

	/*
	 * 		TabLayout View & Variables
	 */
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	private String[] tabs = { "News Feed", "W Pocket", "My Friends", "My Page" };
	
	/*
	 * 		Database
	 */
	
	Giv2DBManager dbManager;
	SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initTabs();
		
		dbManager = new Giv2DBManager(this);
				
		db = dbManager.getDatabase();

	}

	public void initTabs() {
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(new ActionBar.TabListener() {
						
						public void onTabUnselected(Tab tab, FragmentTransaction ft) {
							
						}
						
						public void onTabSelected(Tab tab, FragmentTransaction ft) {
							viewPager.setCurrentItem(tab.getPosition());
						}
						
						public void onTabReselected(Tab tab, FragmentTransaction ft) {
							
						}
					}));
			
			viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				
				public void onPageSelected(int position) {
					actionBar.setSelectedNavigationItem(position);
					invalidateOptionsMenu();
				}
				
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				public void onPageScrollStateChanged(int arg0) {
					
				}
			});
			
		}
	}
	
	public Giv2DBManager getDBManager() {
		return dbManager;
	}

}
