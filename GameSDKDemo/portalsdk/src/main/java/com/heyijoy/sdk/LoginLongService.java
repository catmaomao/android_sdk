package com.heyijoy.sdk;

import java.util.Timer;
import java.util.TimerTask;

import com.heyijoy.sdk.analytics.UDAgent;
import com.heyijoy.sdk.analytics.UDManager;
import com.heyijoy.sdk.analytics.UUserLog;
import com.heyijoy.sdk.utils.ACache;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 统计app活跃服务
 * 
 * @author mashaohu
 *
 */
public class LoginLongService extends Service {

	private UUserLog submitData;
	private Timer timer;
	private TimerTask timerTask;

	@Override
	public void onStart(Intent intent, int startId) {
		try {
			doTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ACache mCache;

	private void getCache() {
		if (mCache != null) {
			mCache = null;
		}
		mCache = ACache.get(this);
		submitData = (UUserLog) mCache.getAsObject("login_long");
	}

	private void doTimer() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}

		if (timer == null) {
			timer = new Timer();
		}

		if (timerTask == null) {
			timerTask = new TimerTask() {

				@Override
				public void run() {
					getCache();
					if (submitData != null) {
						submitData.setEvent("hb");
						UDManager.getInstance().submitUserInfo(HeyiJoySDK.getInstance().getContext(),
								UDAgent.analytics_url, submitData, new CallBack() {

									@Override
									public void callBack(String result) {
										// TODO...
									}
								});
					} else {
						Log.e("HeyiJoySDK", "loginlong -- submitData is null");
					}
				}
			};
		}

		timer.schedule(timerTask, 0, 5 * 60 * 1000);// 5分钟发送一次
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
