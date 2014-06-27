package com.example.give2gether;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Contactables;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddFriendsActivity extends Activity implements OnItemClickListener{

	public static final String TAG = "naddola";

	ListView list;
	SeparatedListAdapter baseAdapter;
	ContactsAdapter adapter;
	ContactsAdapter adapter2;
	Giv2DBManager dbManager;
	ArrayList<Contact> mContactList;
	ArrayList<Contact> mGivFriendList;
	ArrayList<MyFriend> mFriendList;

	EditText et_search;
	ArrayList<Contact> mSearchContactList;
	TextWatcher textwatcher;

	Button bt_confirm;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friends);

		init();

	}

	public void init() {
		dbManager = new Giv2DBManager(getApplicationContext());

		list = (ListView) findViewById(R.id.AddFriends_list);
		et_search = (EditText) findViewById(R.id.AddFriend_et_search);
		bt_confirm = (Button) findViewById(R.id.AddFriends_button);
		bt_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mGivFriendList = new ArrayList<Contact>();
		mSearchContactList = new ArrayList<Contact>();
		setTextWatcher();
		et_search.addTextChangedListener(textwatcher);

		mFriendList = dbManager.getFriendsList();
		mContactList = getContactList();
		
		postContactJsonArray();

		baseAdapter = new SeparatedListAdapter(this);
		
		adapter = new ContactsAdapter(AddFriendsActivity.this,
				R.layout.custom_addfriends_list, mGivFriendList);
		baseAdapter.addSection("Giv2Gether 친구", adapter);
		adapter2 = new ContactsAdapter(AddFriendsActivity.this,
				R.layout.custom_addfriends_list, mContactList);
		baseAdapter.addSection("전화번호 친구", adapter2);
		
		list.setAdapter(baseAdapter);
		list.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> list, View v, int position, long resId) {
		ContactsAdapter tempAdapter = null;
		ArrayList<Contact> tempContacts = null;
		Log.i(TAG, adapter.checkbox.length+"");
		Log.i(TAG, adapter2.checkbox.length+"");
		switch(baseAdapter.getSectionFromPostion(position)){
		
		case 0:
			tempAdapter = adapter;
			tempContacts = mGivFriendList;
			break;
			
		case 1:
			tempAdapter = adapter2;
			tempContacts = mContactList;
			break;
		}
		position = baseAdapter.getPosition(position);
		tempAdapter.setChecked(position);
		Contact tempFriend = tempContacts.get(position);
		Log.i(TAG, tempFriend.getName());
		if (tempAdapter.isChecked(position)) {
			if (checkFriendSigned(tempFriend)) {
				dbManager.insertFriendsData(tempFriend.getName(), null,
						tempFriend.getPhonenum(), null, 1);
			} else {
				dbManager.insertFriendsData(tempFriend.getName(), null,
						tempFriend.getPhonenum(), null, 0);
			}
		}
		baseAdapter.notifyDataSetChanged();
	}


	public void setTextWatcher() {
		textwatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mSearchContactList.clear();
				for (int i = 0; i < mContactList.size(); i++) {
					Contact tempContact = mContactList.get(i);
					if (tempContact.getName().matches(
							".*" + s.toString() + ".*")) {
						mSearchContactList.add(tempContact);
					}
				}

				adapter = new ContactsAdapter(AddFriendsActivity.this,
						R.layout.custom_addfriends_list, mSearchContactList);

				list.setAdapter(adapter);
				list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			}
		};
	}

	private ArrayList<Contact> getContactList() {

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

		String[] selectionArgs = null;

		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		Cursor contactCursor = managedQuery(uri, projection, null,
				selectionArgs, sortOrder);

		ArrayList<Contact> contactlist = new ArrayList<Contact>();

		if (contactCursor.moveToFirst()) {
			do {
				String phonenumber = contactCursor.getString(1).replaceAll("-",
						"");
				/*
				if (phonenumber.length() == 10) {
					phonenumber = phonenumber.substring(0, 3) + "-"
							+ phonenumber.substring(3, 6) + "-"
							+ phonenumber.substring(6);
				} else if (phonenumber.length() > 8) {
					phonenumber = phonenumber.substring(0, 3) + "-"
							+ phonenumber.substring(3, 7) + "-"
							+ phonenumber.substring(7);
				}
				*/

				Contact acontact = new Contact();
				acontact.setPhotoid(contactCursor.getLong(0));
				acontact.setPhonenum(phonenumber);
				acontact.setName(contactCursor.getString(2));
				
				//이미 등록된 친구 패스 
				if (checkFriend(phonenumber))
					continue;

				contactlist.add(acontact);
			} while (contactCursor.moveToNext());
		}
		return contactlist;
	}

	private class ContactsAdapter extends ArrayAdapter<Contact> {

		private int resId;
		private ArrayList<Contact> contactlist;
		private LayoutInflater Inflater;
		private Context context;
		private boolean[] checkbox;

		public ContactsAdapter(Context context, int textViewResourceId,
				List<Contact> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			resId = textViewResourceId;
			contactlist = (ArrayList<Contact>) objects;
			Inflater = (LayoutInflater) ((Activity) context)
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			checkbox = new boolean[contactlist.size()];
		}

		public boolean isChecked(int position) {
			return checkbox[position];
		}

		public void setChecked(int position) {
			checkbox[position] = !checkbox[position];
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			ViewHolder holder;
			if (v == null) {
				v = Inflater.inflate(resId, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) v
						.findViewById(R.id.AddFriends_list_Name);
				holder.tv_phonenumber = (TextView) v
						.findViewById(R.id.AddFriends_list_Phone);
				holder.iv_photoid = (ImageView) v
						.findViewById(R.id.AddFriends_list_Photo);
				holder.checkbox = (CheckBox) v
						.findViewById(R.id.AddFriends_list_checkbox);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			Contact acontact = contactlist.get(position);

			if (acontact != null) {
				holder.tv_name.setText(acontact.getName());
				holder.tv_phonenumber.setText(acontact.getPhonenum());
				holder.checkbox.setChecked(checkbox[position]);

				Bitmap bm = openPhoto(acontact.getPhotoid());
				// ��������� �⺻ ���� �����ֱ�
				if (bm != null) {
					holder.iv_photoid.setImageBitmap(bm);
				} else {
					holder.iv_photoid.setImageDrawable(getResources()
							.getDrawable(R.drawable.ic_launcher));
				}

				holder.checkbox.setClickable(false);
				holder.checkbox.setFocusable(false);

			}

			return v;
		}

		/**
		 * ����ó ���� id�� ������ ���� �� bitmap�� ��.
		 * 
		 * @param contactId
		 *            ����ó ���� ID
		 * @return bitmap ����ó ����
		 */
		private Bitmap openPhoto(long contactId) {
			Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
					contactId);
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(context.getContentResolver(),
							contactUri);

			if (input != null) {
				return BitmapFactory.decodeStream(input);
			}

			return null;
		}

		private class ViewHolder {
			ImageView iv_photoid;
			TextView tv_name;
			TextView tv_phonenumber;
			CheckBox checkbox;
		}
	}

	// 이미 친구등록된 친구 확
	public boolean checkFriend(String phone) {
		for (int i = 0; i < mFriendList.size(); i++) {
			if (phone.equals(mFriendList.get(i).getPhone())) {
				return true;
			}
		}
		return false;
	}

	// 이미 가입된 친구 확
	public boolean checkFriendSigned(Contact friend) {

		String name = friend.getName();
		String email = null;
		String phone = "";
		String birth = null;

		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(friend.getPhonenum());
		while (m.find()) {
			phone += m.group(0);
		}

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		HttpPostAsyncTask task = new HttpPostAsyncTask();

		if (task.doInBackground(name, email, phone, birth) == 1)
			return true;
		else
			return false;
	}

	public void postContactJsonArray(){
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		HttpPostAsyncTaskJsonArray task = new HttpPostAsyncTaskJsonArray();

		task.doInBackground();
		
	}

	class HttpPostAsyncTask extends AsyncTask<String, Integer, Long>

	{
		@Override
		protected Long doInBackground(String... params) {
			long result;
			String name = params[0];
			String email = params[1];
			String phone = params[2];
			String birth = params[3];

			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/checkSignedMember.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("phone", phone));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();

				if (resEntity != null) {
					String resp = EntityUtils.toString(resEntity);

					if (resp.equals("false")) {
						return result = 0;
					} else {
						Log.i(TAG, resp);
						return result = 1;
					}
				}

			} catch (MalformedURLException e) {
				//
			} catch (IOException e) {
				//
			}
			return null;
		}
	}

	class HttpPostAsyncTaskJsonArray extends AsyncTask<String, Integer, Long>
	{
		
		JSONArray jContactArr;
		@Override
		protected Long doInBackground(String... params) {
			
			jContactArr = new JSONArray();
			for(int i=0; i<10; i++){
				JSONObject obj = new JSONObject();
				try {
					obj.put("phone", mContactList.get(i).getPhonenum());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jContactArr.put(obj);
			}
			
			long result;
			

			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/test2.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("phone", jContactArr.toString()));
				
				//Log.i(TAG, "sendArray - " +jContactArr.toString());
				
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();

				if (resEntity != null) {
					String resp = EntityUtils.toString(resEntity);
					try {
						JSONArray jsonArr = new JSONArray(resp);
						for(int i=0; i<jsonArr.length(); i++){
							for(int j=0; j<mContactList.size(); j++){
								if(mContactList.get(j).getPhonenum().equals(jsonArr.getString(i))){
									mGivFriendList.add(mContactList.get(j));
									mContactList.remove(j);
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Log.i(TAG, resp);
				}

			} catch (MalformedURLException e) {
				//
			} catch (IOException e) {
				//
			}
			return null;
		}
	}
}
