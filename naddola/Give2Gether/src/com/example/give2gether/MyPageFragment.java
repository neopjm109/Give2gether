package com.example.give2gether;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyPageFragment extends Fragment {

	Button bt_resetDB;
	
	View rootView;
	ListView optionsView;
	ArrayAdapter<String> mAdapter;
	
	MainActivity mActivity;
	SettingPreference setting;
	
	String[] menu = {"참여 내역", "받은 내역", "결제 내역", "설정"};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_mypage, container, false);
		
		initViews();
		
		return rootView;
	}
	
	public void initViews() {

		setting = new SettingPreference(getActivity());
		mActivity = (MainActivity) getActivity();

		bt_resetDB = (Button)rootView.findViewById(R.id.Setting_ResetDB);
		bt_resetDB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Giv2DBManager db = new Giv2DBManager(mActivity);
				db.removeTable();
				Toast.makeText(mActivity, "DB 초Gi화 했er", Toast.LENGTH_SHORT).show();
				mActivity.finish();
			}
		});
		
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

		optionsView = (ListView) rootView.findViewById(R.id.optionsView);
		mAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, menu);
		
		optionsView.setAdapter(mAdapter);
		
	}
	
}
