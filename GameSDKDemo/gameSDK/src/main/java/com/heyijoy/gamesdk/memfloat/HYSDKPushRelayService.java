package com.heyijoy.gamesdk.memfloat;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.data.MsgBean;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.sql.MsgDBTool;
import com.heyijoy.gamesdk.util.MyParse;
import com.heyijoy.gamesdk.util.Util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class HYSDKPushRelayService extends Service{
	private static final String TAG = "HYSDKPushRelayService";
	PushOrderRelayReceiver pushRelayReceiver;
	PushRelayReceiver pushRelayReceiver1;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart");
		/*sdk内部分发push广播接收注册*/
		IntentFilter intentFilter = new IntentFilter();
		pushRelayReceiver = new PushOrderRelayReceiver();
		intentFilter.addAction(HYConstant.YK_SDK_PUSH_ORDER_RELAY);
		if(GameSDKApplication.getInstance().getContext()==null) GameSDKApplication.getInstance().init(this);
		intentFilter.setPriority(Integer.valueOf(GameSDKApplication.getInstance().getAppid()));
		registerReceiver(pushRelayReceiver, intentFilter);
		
		
		IntentFilter intentFilter1 = new IntentFilter();
		pushRelayReceiver1 = new PushRelayReceiver();
		intentFilter1.addAction(HYConstant.YK_SDK_PUSH_RELAY);
		registerReceiver(pushRelayReceiver1, intentFilter1);
	}
	
	private class PushOrderRelayReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			synchronized(this){
				String msg = intent.getStringExtra("msg");
				if(GameSDKApplication.getInstance().getContext()==null) GameSDKApplication.getInstance().init(context);
				MsgBean msgBean=MyParse.parsePushMsg(msg);
				if(msgBean != null){
					abortBroadcast();
					//展示，再发
					new HYNotifyBar(context).showNotify(msgBean);
					Intent mintent = new Intent();
					mintent.setAction(HYConstant.YK_SDK_PUSH_RELAY);
					mintent.putExtra("msg", msg);
					if (android.os.Build.VERSION.SDK_INT >= 12) 
					{
						mintent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES); 
					}
					context.sendBroadcast(mintent);
				}
			}
		}
	}
	private class PushRelayReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			synchronized(this){
				if(Util.isFastRun()) return;
				String msg = intent.getStringExtra("msg");
				if(GameSDKApplication.getInstance().getContext()==null) GameSDKApplication.getInstance().init(context);
				MsgBean msgBean=MyParse.parsePushMsg(msg);
				MsgDBTool dbTool = new MsgDBTool(context);
				dbTool.addMsg(msgBean);
			}
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try{
			unregisterReceiver(pushRelayReceiver);
			unregisterReceiver(pushRelayReceiver1);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
	}
}
