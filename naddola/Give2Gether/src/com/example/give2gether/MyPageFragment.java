package com.example.give2gether;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MyPageFragment extends Fragment {
	
	View rootView;
	SettingPreference setting;
	Giv2DBManager dbManager;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_mypage, container, false);
		
		initViews();
		
		return rootView;
	}
	
	public void initViews() {

		setting = new SettingPreference(getActivity());
		
		TextView tv = (TextView) rootView.findViewById(R.id.mText);
		tv.setText("Welcome " + setting.getID());
		CheckBox ck = (CheckBox)rootView.findViewById(R.id.Setting_AutoLogin);
		ck.setChecked(setting.isLogin());
		ck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					setting.setAutoLoginTrue();
				else
					setting.setAutoLoginFalse();
			}
		});
		
		Button bt_resetDB = (Button)rootView.findViewById(R.id.Setting_bt_resetDB);
		bt_resetDB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dbManager = new Giv2DBManager(getActivity());
				dbManager.removeTable();
				dbManager.createTable();
			}
		});
		
	}
	
}
