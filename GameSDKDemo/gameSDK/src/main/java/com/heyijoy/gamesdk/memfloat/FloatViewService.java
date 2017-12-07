package com.heyijoy.gamesdk.memfloat;

import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.util.HomeListener;
import com.heyijoy.gamesdk.util.HomeListener.OnHomePressedListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class FloatViewService extends Service {

	private FloatReceiver floatReceiver;
	private FloatView mFloatView;
	public static Boolean isShowFloat;// true 显示， false 隐藏悬浮窗（这里指消失）

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mFloatView = new FloatView(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		registerReceiver();
		registerHomeListener();
		showFloat();
		return super.onStartCommand(intent, flags, startId);
	}

	public void showFloat() {
		if (mFloatView != null) {
			mFloatView.show();
		}
	}

	public void hideFloat() {
		if (mFloatView != null) {
			mFloatView.hide();
		}
	}

	public void destroyFloat() {
		if (mFloatView != null) {
			mFloatView.destroy();
		}
		mFloatView = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyFloat();
		if (floatReceiver != null) {
			unregisterReceiver(floatReceiver);
		}
		if (mHomeListener != null) {
			mHomeListener.stopListen();
		}
		HYPlatform.closeHYFloat(FloatViewService.this);
	}

	private void registerReceiver() {// 注册接收广播
		IntentFilter intentFilter = new IntentFilter();
		floatReceiver = new FloatReceiver();
		intentFilter.addAction(HYConstant.YK_SDK_FLOAT);
		registerReceiver(floatReceiver, intentFilter);
	}

	/**
	 * 通过发广播控制悬浮窗的显示和隐藏
	 * 
	 * @author mashaohu
	 *
	 */
	private class FloatReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			synchronized (FloatViewService.this) {
				try {
					isShowFloat = true;
					String name = intent.getStringExtra("from");
					if ("preCenter".equals(name)) {
						// callMethod_02();
					}
					// unregisterReceiver(floatReceiver);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private HomeListener mHomeListener;

	/**
	 * 注册home键的监听广播操作，隐藏悬浮窗
	 */
	private void registerHomeListener() {
		mHomeListener = new HomeListener(this);
		mHomeListener.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				mHomeListener.stopListen();
				// hideFloat();
//				FloatView.isShowFloat = false;
			}
		});
		mHomeListener.startListen();
	}
}
