package com.example.give2gether;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.give2gether.async.AsyncTaskPostFriendList;
import com.google.android.gms.gcm.GoogleCloudMessaging;
public class GcmMessageHandler extends IntentService {


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
		handler = new Handler();
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
				sendNotification("Giv2gether", "Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Giv2gether", "Deleted messages on server: "
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
					sendNotification(extras.getString("Notice"), extras.getString("Message"));
					Log.i(TAG, "Receive : pushEventGeneration");
					postFriendList(extras.getString("Message"));
				} else if (tag.equals("pushEventToFriends")) {
					sendNotification(extras.getString("Notice"), extras.getString("Message"));
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
	private void sendNotification(String title, String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);         
		long[] pattern = {0, 100, 1000, 200};          // 진동, 무진동, 진동 무진동 숫으로 시간을 설정한다.
		vibe.vibrate(pattern, -1);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				// .setSmallIcon(R.drawable.ic_stat_gcm)
				.setContentTitle(title)
				.setSmallIcon(R.drawable.app_icon)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg).setAutoCancel(true);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
	
	public void postFriendList(String eventMessage) {
		
		SettingPreference setting = new SettingPreference(getApplicationContext());
		Giv2DBManager dbManager = new Giv2DBManager(getApplicationContext());
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		AsyncTaskPostFriendList task = new AsyncTaskPostFriendList(setting, dbManager, eventMessage);

		task.doInBackground();
	}

}