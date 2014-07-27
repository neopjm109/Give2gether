package com.hmjcompany.give2gether;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.hmjcompany.give2gether.async.HttpPostAsyncTaskJsonArray;
import com.hmjcompany.give2gether.async.getSignedFriendHttpPostAsyncTask;

// Test Origon write
public class AddFriendsActivity extends Activity implements OnItemClickListener {

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
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friends);

		init();

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void init() {
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
	public void onItemClick(AdapterView<?> list, View v, int position,
			long resId) {
		ContactsAdapter tempAdapter = null;
		ArrayList<Contact> tempContacts = null;
		switch (baseAdapter.getSectionFromPostion(position)) {

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
		Contact tempContact = tempContacts.get(position);
		MyFriend tempFriend;
		if (tempAdapter.isChecked(position)) {
			tempFriend = getFriendSigned(tempContact);
			if (tempFriend != null) {
				Log.i(TAG,"insertFriendData : "+ tempFriend.getName() + tempFriend.getEmail()+
						tempFriend.getPhone() + tempFriend.getBirth()+"signed");
				dbManager.insertFriendsData(tempFriend.getName(), tempFriend.getEmail(),
						tempFriend.getPhone(), tempFriend.getBirth(), 1);
			} else {
				dbManager.insertFriendsData(tempContact.getName(), null,
						tempContact.getPhonenum(), null, 0);
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
				 * if (phonenumber.length() == 10) { phonenumber =
				 * phonenumber.substring(0, 3) + "-" + phonenumber.substring(3,
				 * 6) + "-" + phonenumber.substring(6); } else if
				 * (phonenumber.length() > 8) { phonenumber =
				 * phonenumber.substring(0, 3) + "-" + phonenumber.substring(3,
				 * 7) + "-" + phonenumber.substring(7); }
				 */

				Contact acontact = new Contact();
				acontact.setPhotoid(contactCursor.getLong(0));
				acontact.setPhonenum(phonenumber);
				acontact.setName(contactCursor.getString(2));

				// 이미 등록된 친구 패스
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

	// 이미 가입된 친구 확인
	public MyFriend getFriendSigned(Contact friend) {

		int id;
		String name = friend.getName();
		String email = null;
		String phone = "";
		String birth = null;
		MyFriend myFriend = null;

		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(friend.getPhonenum());
		while (m.find()) {
			phone += m.group(0);
		}

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		
		String signedTask[] = {name, email, phone, birth};

		try {
			JSONObject jsonResult = new getSignedFriendHttpPostAsyncTask().execute(signedTask).get();
			if (jsonResult != null) {
				try {
					JSONArray member;
					member = jsonResult.getJSONArray("member");
	
					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);
						id = c.getInt("id");
						email = c.getString("email");
						birth = c.getString("birth");
						myFriend = new MyFriend(id, name, email, phone, birth,
								true, null);
						Log.i(TAG, id+email);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				return myFriend;
			} else
				return null;
		} catch (Exception e){
			
		}
		return null;
	}
	
	public void postContactJsonArray() {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		
		try {
			String resp = new HttpPostAsyncTaskJsonArray(mContactList).execute().get();
			
			try {
				JSONArray jsonArr = new JSONArray(resp);
				for (int i = 0; i < jsonArr.length(); i++) {
					for (int j = 0; j < mContactList.size(); j++) {
						if (mContactList.get(j).getPhonenum()
								.equals(jsonArr.getString(i))) {
							mGivFriendList.add(mContactList.get(j));
							mContactList.remove(j);
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			
		}

	}


}
