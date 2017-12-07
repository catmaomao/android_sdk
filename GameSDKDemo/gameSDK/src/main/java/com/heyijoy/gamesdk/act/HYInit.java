/**
 * HYInit.java
 * com.heyijoy.gamesdk
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-2-20 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.act;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.heyijoy.gamesdk.constants.HYBuildConfig;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.VersionInfo;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.widget.HYProgressDlg;

/**
 * ClassName:HYInit
 * 
 * @author msh
 * @Date 2014-2-20 下午4:53:11
 */
public class HYInit {
	private Context context;
	private HYCallBack callbackRe;
	public static VersionInfo remoteVersionInfo = null;
	private HYProgressDlg dialog = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				HYCallBack callBack = (HYCallBack) msg.obj;
				getPackFormRemote(callBack);
				break;
			default:
				break;
			}
		}
	};

	public HYInit(Context context) {
		this.context = context;
	}

	@SuppressLint("InlinedApi")
	public void init(HYCallBack callback, boolean isTest) {
		this.callbackRe = callback;
		HYBuildConfig.IS_TEST = isTest;// 初始化时控制url测试还是正式环境
		GameSDKApplication.getInstance().init(context.getApplicationContext());

		dialog = HYProgressDlg.show(context, "", "初始化…");

		HYCallBack callBack = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				callbackRe.onSuccess(new Bean());
			}

			@Override
			public void onFailed(int code, String failReason) {
				callbackRe.onFailed(code, failReason);
				Log.e(HYConstant.TAG, "code=" + code + ",failReason=" + failReason);
				if (HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason)
						|| HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason)) {
					Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "初始化失败,请重新启动游戏", Toast.LENGTH_SHORT).show();
				}
			}
		};

		Message msg = Message.obtain();
		msg.obj = callBack;
		msg.what = 1;
		handler.sendMessageDelayed(msg, 1000);

	}

	private void getPackFormRemote(HYCallBack callBack) {

		HttpApi.getInstance().getRemoteVersion(callBack);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}, 1000);
	}
}
