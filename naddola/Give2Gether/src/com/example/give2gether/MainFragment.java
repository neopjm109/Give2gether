package com.example.give2gether;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MainFragment extends Fragment {

	public static final String TAG = "naddola";
	
	/*
	 * 		Views
	 */
	View rootView;
	SettingPreference setting;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_main, container, false);
		
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
				if(isChecked){
					setting.setAutoLoginTrue();
				}
				else{
					setting.setAutoLoginFalse();
				}
			}
		});
		
	}
}
