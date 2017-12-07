package com.heyijoy.gamesdk.memfloat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HYSDKPushRelayReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mIntent = new Intent(context, HYSDKPushRelayService.class);
//		aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		aIntent.putExtra("key", "value");
//		aIntent.putExtra("key_from_activity", "value_from_activity");
		
		context.startService(mIntent);
	}

}
