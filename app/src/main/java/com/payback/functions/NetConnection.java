package com.payback.functions;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;

public class NetConnection {

	public static boolean checkInternetConnectionn(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// If permission not given in manifest,it will give error..so first add
		// internet permission to manifest file
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {

			return true;
		} else {
			return false;
		}

	}

	public static boolean checkInternetConFragment(Activity a){
		ConnectivityManager conMgr = (ConnectivityManager) a
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// If permission not given in manifest,it will give error..so first add
		// internet permission to manifest file
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {

			return true;
		} else {
			return false;
		}
	}

	public static void checkForStrictMode() {

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);
		}
	}

	
}
