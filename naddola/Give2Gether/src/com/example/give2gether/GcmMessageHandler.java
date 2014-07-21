package com.example.give2gether;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.give2gether.AddFriendsActivity.HttpPostAsyncTaskJsonArray;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {

	Giv2DBManager dbManager;

	public static final int NOTIFICATION_ID = 1;
	public static String TAG = "naddola";
	String mes;
	private Handler handler;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		init();
	}

	public void init() {
		dbManager = new Giv2DBManager(getApplicationContext());
		handler = new Handler();
		dbManager.getFriendsList();
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// This loop represents the service doing some work.
				for (int i = 0; i < 5; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				String tag = extras.getString("Tag");
				if (tag.equals("pushEventGeneration")) {
					Log.i(TAG, "Receive : pushEventGeneration");
					postFriendList();
				} else if (tag.equals("pushEventToFriends")) {
					sendNotification(extras.getString("Notice"));
					Log.i(TAG, "Receive : pushEventToFriends");
				}
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		long[] vib = { 0, 100, 200, 300 };
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				// .setSmallIcon(R.drawable.ic_stat_gcm)
				.setContentTitle("Giv2gether")
				.setSmallIcon(R.drawable.app_icon)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg).setAutoCancel(true).setVibrate(vib);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}

	public void postFriendList() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		AsyncTaskPostFriendList task = new AsyncTaskPostFriendList();

		task.doInBackground();
	}

	class AsyncTaskPostFriendList extends AsyncTask<String, Integer, Long> {

		JSONArray jFriendArr;
		String BirthEmail;

		@Override
		protected Long doInBackground(String... params) {

			SettingPreference setting = new SettingPreference(getApplicationContext());
			BirthEmail = setting.getID();
			ArrayList<MyFriend> fl = dbManager.getGivFriendsList();
			jFriendArr = new JSONArray();

			for (int i = 0; i < fl.size(); i++) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("email", fl.get(i).getEmail());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jFriendArr.put(obj);
			}

			try {
				HttpClient client = new DefaultHttpClient();
				String postUrl;

				postUrl = "http://naddola.cafe24.com/pushEventToFriends.php";

				HttpPost post = new HttpPost(postUrl);

				// 전달인자
				List params2 = new ArrayList();
				params2.add(new BasicNameValuePair("BirthEmail", BirthEmail));
				params2.add(new BasicNameValuePair("friends", jFriendArr
						.toString()));

				//Log.i(TAG, "sendArray - " + jFriendArr.toString());

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params2,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePost = client.execute(post);
				HttpEntity resEntity = responsePost.getEntity();

				if (resEntity != null) {
					String resp = EntityUtils.toString(resEntity);

					Log.i(TAG, "post result : " + resp);
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