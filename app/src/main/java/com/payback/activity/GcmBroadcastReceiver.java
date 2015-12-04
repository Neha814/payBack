package com.payback.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	private static final String TAG = "GcmBroadcastReceiver";
	Context con;
	private String userFacebookid;
	private String strMessageID;
	private String strMessageType;
	private static boolean mDebugLog = true;
	private static String mDebugTag = "GcmBroadcastReceiver";
	private SharedPreferences preferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		con = context;
				Bundle extras = intent.getExtras();
				for (String key : extras.keySet())
					Log.e("okkkkkkkkk", key + "//   " + extras.getString(key));

		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmIntentService.class.getName());
		// Start the service, keeping the device awake while it is
		// launching.
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);

	} 	}


