package com.heyijoy.gamesdk.operatorpay;


import android.content.Context;
import android.widget.Toast;

// 包装了Toast
public class GlobleNotify {
	
	
	public static void ShowNotifyLong(String Notification, Context context) {
		Toast.makeText(context, Notification,
                Toast.LENGTH_LONG).show();
	}
	
	public static void ShowNotifyShort(String Notification, Context context) {
		Toast.makeText(context, Notification,
                Toast.LENGTH_SHORT).show();
	}

}
