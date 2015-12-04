package com.payback.activity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.payback.R;
import com.payback.functions.Constants;
import com.payback.functions.Methods;

public class GcmIntentService extends IntentService {
	private static final String TAG = "GcmIntentService";
	private Intent notificationIntent;
	private String spilt_="<-split->";
	private Methods sp;

	public GcmIntentService() {
		super(Constants.SENDER_ID);
		Log.e("GcmIntentService", "GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			Log.e(TAG, "push log onHandleIntent......" + intent.toString());
			Bundle extras = intent.getExtras();
			for (String key : extras.keySet())
				Log.e("ok", key + "//" + extras.get(key));

			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
			// The getMessageType() intent parameter must be the intent you
			// received
			// in your BroadcastReceiver.

			if (!extras.isEmpty()) { // has effect of unparcelling Bundle
				/*
				 * Filter messages based on message type. Since it is likely
				 * that GCM will be extended in the future with new message
				 * types, just ignore any message types you're not interested
				 * in, or that you don't recognize.
				 */

				sendNotification(extras);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		com.payback.activity.GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(Bundle extras) {
		sp = new Methods(getApplicationContext());
		//String msg = extras.getString("string");
		/*
		 * SessionManager session=new SessionManager(this); boolean
		 * bFlagForCurrent=session.isFirstScreen(); Log.i(TAG,
		 * "sendNotification bFlagForCurrent........."+bFlagForCurrent);
		 */
		int icon = R.mipmap.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;

	/*	String title = this.getString(R.string.app_name);
		String action = msg.split(spilt_)[0];
		int notifyid=sp.getNotifyId();
*/

		notification = new Notification(icon, "Notification!!", when);
		notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.putExtra("PUSH_MESSAGE_BUNDLE", extras);

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "i am notification","hi", intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled

		// int notify=notifyid+1;
		int notify=1;
		notificationManager.notify(notify, notification);
		sp.setNotifyId(notify);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	/*
	 * private void sendNotification(String msg) { mNotificationManager =
	 * (NotificationManager)
	 * this.getSystemService(Context.NOTIFICATION_SERVICE);
	 * 
	 * PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new
	 * Intent(this, DemoActivity.class), 0);
	 * 
	 * NotificationCompat.Builder mBuilder = new
	 * NotificationCompat.Builder(this) .setSmallIcon(R.drawable.ic_launcher)
	 * .setContentTitle("GCM Notification") .setStyle(new
	 * NotificationCompat.BigTextStyle() .bigText(msg)) .setContentText(msg);
	 * 
	 * mBuilder.setContentIntent(contentIntent);
	 * mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build()); }
	 */
}
