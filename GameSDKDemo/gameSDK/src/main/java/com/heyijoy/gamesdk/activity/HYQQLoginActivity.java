package com.heyijoy.gamesdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.util.QQUtil;
import com.heyijoy.gamesdk.util.Util;

public class HYQQLoginActivity extends Activity {

	private String TAG = "HeyiJoySDK";
	private boolean isToBind;// true来自个人中心绑定入口，false来自登录入口
	private HYThridParams thridParams;
	private String appid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hy_activity_init);
		isToBind = getIntent().getBooleanExtra("bind", false);
		thridParams = GameSDKApplication.getInstance().getThridParams();
		appid = Util.getQQAppid(thridParams);
//		Log.d(TAG, "qqappid=" + appid);
		if (TextUtils.isEmpty(appid)) {
			Toast.makeText(HYQQLoginActivity.this, "参数异常，请重新启动游戏", Toast.LENGTH_SHORT).show();
		} else {
			qqLogin();
		}
	}

	private static boolean isServerSideLogin = false;
	private Tencent mTencent;

	protected void qqLogin() {
		mTencent = Tencent.createInstance(appid, this.getApplicationContext());
		// if (!mTencent.isSessionValid()) {
		mTencent.login(this, "all", loginListener);
		isServerSideLogin = false;
		Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		// }

		// else {
		// if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
		// mTencent.logout(this);
		// mTencent.login(this, "all", loginListener);
		// isServerSideLogin = false;
		// Log.d(TAG, "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		// return;
		// }
		// mTencent.logout(this);
		// }
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			Log.d(TAG, "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
			initOpenidAndToken(values);
		}
	};

	public void initOpenidAndToken(JSONObject jsonObject) {
		try {
//			 Log.e(TAG, jsonObject.toString());
			// {"ret":0,"openid":"B2CD9B7B1FAB54C0A24FD036329ECA37","access_token":"D1EC03B4B1CDDD9806F6F7AE2CF116F8",
			// "pay_token":"1B5392DD0C76A5229F2B7EF3D36A7230","expires_in":7776000,
			// "pf":"desktop_m_qq-10000144-android-2002-","pfkey":"8006d51cb54d9132ff14879d67dcde90",
			// "msg":"","login_cost":301,"query_authority_cost":226,"authority_cost":0}
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
			int ret = jsonObject.getInt("ret");
			if (0 == ret) {
				responseSDK(token);
			}

		} catch (Exception e) {
			Log.e(TAG, "exception error");
			e.printStackTrace();
		}
	}

	/**
	 * 三方授权成功
	 * 
	 * @param type
	 * @param token
	 */
	private void responseSDK(String token) {
		Intent intent = new Intent();
		if (isToBind) {
			intent.setAction(HYConstant.QQ_LOGIN_BIND + GameSDKApplication.getInstance().getAppid());
		} else {
			intent.setAction(HYConstant.QQ_LOGIN + GameSDKApplication.getInstance().getAppid());
		}
		intent.putExtra(HYConstant.QQ_LOGIN, token);
		sendBroadcast(intent);
		finish();
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				QQUtil.showResultDialog(HYQQLoginActivity.this, "返回为空", "qq登录失败");
				finish();
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				QQUtil.showResultDialog(HYQQLoginActivity.this, "返回为空", "qq登录失败");
				finish();
				return;
			}
			// QQUtil.showResultDialog(HYDialogActivity.this,
			// response.toString(), "登录成功");
			// 有奖分享处理
			// handlePrizeShare();
			Log.d(TAG, "QQ授权成功");
//			Toast.makeText(HYQQLoginActivity.this, "QQ授权成功", Toast.LENGTH_SHORT).show();
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			QQUtil.toastMessage(HYQQLoginActivity.this, "授权错误" + e.errorDetail);
			QQUtil.dismissDialog();
			finish();
		}

		@Override
		public void onCancel() {
			QQUtil.toastMessage(HYQQLoginActivity.this, "授权取消");
			QQUtil.dismissDialog();
			if (isServerSideLogin) {
				isServerSideLogin = false;
			}
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {// qq登录
			Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public String getQQAppid() {
		if (this.thridParams == null || !this.thridParams.contains("QQ_APPID")) {
			return "";
		}
		return this.thridParams.getString("QQ_APPID");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTencent != null) {
			mTencent.releaseResource();
		}
	}

}
