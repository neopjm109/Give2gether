package com.example.giv2gether;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class MyPageFragment extends Fragment {
	
	View rootView;
	ListView optionsView;
	ArrayAdapter<String> mAdapter;
	
	MainActivity mActivity;
	SettingPreference setting;
	
	String[] menu = {"참여 내역", "받은 내역", "결제 내역", "설정", "회원 탈퇴"};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab_mypage, container, false);

		Log.i("PJM", "4");
		
		initViews();
		
		return rootView;
	}
	
	public void initViews() {

		setting = new SettingPreference(getActivity());
		mActivity = (MainActivity) getActivity();
		
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
		
		optionsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 4:
					AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
					dialog.setMessage("정말 탈퇴하시겠습니까?");
					dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {

								@Override
								protected String doInBackground(String... params) {
									// TODO Auto-generated method stub
									try {
										HttpClient client = new DefaultHttpClient();
										String postUrl;

										postUrl = "http://naddola.cafe24.com/SignOutProc.php";
										
										HttpPost post = new HttpPost(postUrl);

										// 전달인자
										List params2 = new ArrayList();
										params2.add(new BasicNameValuePair("email", params[0]));

										UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
												HTTP.UTF_8);
										post.setEntity(ent);
										client.execute(post);

									} catch (Exception e) {
										e.printStackTrace();
									}
									return null;
								}

								@Override
								protected void onPostExecute(String result) {
									// TODO Auto-generated method stub
									
									super.onPostExecute(result);
									setting.setAutoLoginFalse();
									mActivity.getDBManager().removeTable();
									mActivity.finish();
								}
								
							};
							
							task.execute(setting.getID());
						}
						
					});
					
					dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
						
					});
					dialog.show();
					break;
				}
			}
			
		});
		
	}
	
}
