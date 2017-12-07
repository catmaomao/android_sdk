package com.heyijoy.gamesdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.util.Util;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

public class HYWeiBoLoginActivity extends Activity {

	private AuthInfo mAuthInfo;
	private SsoHandler mSsoHandler;

	private String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog,"
			+ "invitation_write";
	private String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	private boolean isToBind = false;
	private HYThridParams thridParams;
	private String APP_KEY;
	private String TAG = "HeyiJoySDK";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hy_activity_init);
		isToBind = getIntent().getBooleanExtra("bind", false);

		thridParams = GameSDKApplication.getInstance().getThridParams();
		APP_KEY = Util.getWeiBoAppkey(thridParams);
//		Log.d(TAG, "weibo_appkey=" + APP_KEY);
		if (TextUtils.isEmpty(APP_KEY)) {
			Toast.makeText(HYWeiBoLoginActivity.this, "参数异常，请重新启动游戏", Toast.LENGTH_SHORT).show();
		} else {
			weiboLogin();
		}

	}

	public String getAppkey() {
		if (this.thridParams == null || !this.thridParams.contains("WEIBO_APPKEY")) {
			return "";
		}
		return this.thridParams.getString("WEIBO_APPKEY");
	}

	public void weiboLogin() {
		mAuthInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
		WbSdk.install(this, mAuthInfo);
		
		mSsoHandler = new SsoHandler(this);
		mSsoHandler.authorize(new SelfWbAuthListener());
	}

	private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
		@Override
		public void onSuccess(final Oauth2AccessToken token) {
			HYWeiBoLoginActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (token.isSessionValid()) {
						String token2 = token.getToken();
						// 保存 Token 到 SharedPreferences
						AccessTokenKeeper.writeAccessToken(HYWeiBoLoginActivity.this, token);
//						Toast.makeText(HYWeiBoLoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "微博授权成功");
						Intent intent = new Intent();
						if (isToBind) {
							intent.setAction(HYConstant.WEIBO_LOGIN_BIND + GameSDKApplication.getInstance().getAppid());
						} else {
							intent.setAction(HYConstant.WEIBO_LOGIN + GameSDKApplication.getInstance().getAppid());
						}
						intent.putExtra(HYConstant.WEIBO_LOGIN, token2);
						sendBroadcast(intent);
						finish();
					}
				}
			});
		}

		@Override
		public void cancel() {
			Toast.makeText(HYWeiBoLoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
			finish();
		}

		@Override
		public void onFailure(WbConnectErrorMessage errorMessage) {
			Toast.makeText(HYWeiBoLoginActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {// 微博登录
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
