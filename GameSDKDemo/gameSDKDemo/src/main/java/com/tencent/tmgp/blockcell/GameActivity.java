package com.tencent.tmgp.blockcell;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import com.tencent.tmgp.blockcell.R;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.act.HYCallBackWithContext;
import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.HYGameUser;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.ShareDialog;
import com.heyijoy.sdk.CallBack;
import com.heyijoy.sdk.ShareParams;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@SuppressLint("NewApi")
public class GameActivity extends Activity {

	public static HYGameUser user;
	private String TAG = "GameActivity";
	private Tencent mTencent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		mTencent = Tencent.createInstance("222222", this.getApplicationContext());
		// 单机版不登陆，所以不需要以下内容
		getTopTitle();

	}

	private void getTopTitle() {
		TextView tv = (TextView) findViewById(R.id.username);
		Intent intent = getIntent();
		user = (HYGameUser) intent.getSerializableExtra("user");
		String name = user.getUserName();
		String session = user.getSession();
		String sdkVerson = HYPlatform.getSDKVersion();
		tv.setText(name + "\n" + "session: " + session + "\n" + "sdkVersion: " + sdkVerson);
	}

	@Override
	protected void onResume() {
		super.onResume();
		HYPlatform.exceptionYKFinish(GameActivity.this);
	}

	// 切换用户操作
	public void changeOnClick(View view) {
		HYPlatform.logout(GameActivity.this);
		Intent intent = new Intent(GameActivity.this, MainActivity.class);
		intent.putExtra("isNotNeedInit", true);
		GameActivity.this.finish();
		startActivity(intent);
	}

	// 清除用户信息，返回主页面（方便测试，不对外开放）
	public void cleanOnClick(View view) {
		HYPlatform.deleteUser(GameActivity.this);
		Intent intent = new Intent(GameActivity.this, MainActivity.class);
		GameActivity.this.finish();
		startActivity(intent);
	}

	// 跳转到物品购买界面
	public void payOnClick(View view) {
		Intent intent = new Intent(this, PayActivity.class);
		startActivity(intent);
	}

	// 开启悬浮窗按钮
	public void openFloatOnClick(View view) {
		startHYFloat();
	}

	// 关闭悬浮窗按钮
	public void closeFloatOnClick(View view) {
		HYPlatform.closeHYFloat(this);
	}

	HYCallBackWithContext callbackChangeAccount = new HYCallBackWithContext() {
		@Override
		public void callback(Context context) {
			HYPlatform.logout(context);// 注销账号方法
			// 注销账号后回到登陆页（代码需cp自行编写）
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.putExtra("isNotNeedInit", true);
			GameActivity.this.finish();
			startActivity(intent);
		}
	};

	private void startHYFloat() {
		// 登陆成功后启动悬浮窗
		HYPlatform.startHYFloat(GameActivity.this, callbackChangeAccount);
		// HYPlatform.startHYFloat(GameActivity.this);//启动无切换账号回调功能的悬浮窗
	}

	/**
	 * 单机模式不调用合乐智趣退出接口
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			HYCallBack callBack = new HYCallBack() {
				@Override
				public void onSuccess(Bean bean) {
					GameActivity.this.finish();
				}

				@Override
				public void onFailed(int code, String failReason) {
				}
			};
			HYPlatform.quit(GameActivity.this, callBack);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 微信登录
	 * 
	 * @param view
	 */

	public void wxLogin(View view) {
		// IWXAPI api = WXEntryActivity.initWeiXin(this);
		//
		// SendAuth.Req req = new SendAuth.Req();
		// req.scope = "snsapi_userinfo";
		// req.state = "heyijoy_wxlogin";
		// api.sendReq(req);

		Util.wxLogin(this);
	}

	/**
	 * qq登录
	 * 
	 * @param view
	 */
	private static boolean isServerSideLogin = false;

	public void qqLogin(View view) {
		// if (mTencent == null) {
		// mTencent = Tencent.createInstance("222222",
		// this.getApplicationContext());
		// }

		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
			isServerSideLogin = false;
			Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		} else {
			if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
				mTencent.logout(this);
				mTencent.login(this, "all", loginListener);
				isServerSideLogin = false;
				Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
				return;
			}
			mTencent.logout(this);
		}
	}

	/**
	 * 微博登录
	 * 
	 * @param view
	 */
	private AuthInfo mAuthInfo;
	private SsoHandler mSsoHandler;

	private String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog,"
			+ "invitation_write";
	private String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	private String APP_KEY = "3833575060";

	public void weiboLogin(View view) {
		mAuthInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
		WbSdk.install(this, mAuthInfo);

		mSsoHandler = new SsoHandler(this);
		mSsoHandler.authorize(new SelfWbAuthListener());
	}

	/**
	 * 分享
	 * 
	 * @param view
	 */
	public void share(View view) {
		// Toast.makeText(GameActivity.this, "尚未开放", Toast.LENGTH_SHORT).show();
		Resources localResources = GameActivity.this.getResources();
		String str = GameActivity.this.getPackageName();
		int imgid = localResources.getIdentifier("hy_qq", "drawable", str);
		// Uri uri = Uri.parse(
		// ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
		// localResources.getResourcePackageName(imgid) + "/"
		// + localResources.getResourceTypeName(imgid) + "/" +
		// localResources.getResourceEntryName(imgid));
		// String path = "android:resources://" + str + uri.getPath();
		// Log.e("HeyiJoySDK", "path=" + uri.toString());
		ShareParams shareParams = new ShareParams();
		shareParams.setTitle("方块大乱斗");
		shareParams.setContent("这是一款超好玩，超萌酷，超有挑战性，可与全球玩家实时对战的休闲游戏，快来挑战极限吧！");
		shareParams.setSourceUrl("http://t.cn/RK1dBcR");
		shareParams.setNotifyIconText("blockshare_icon");
		shareParams.setImgUrl("http://blockcell.net/image/share_icon.png");
		shareParams.setWxWebUrl("http://blockcell.net");
		new ShareDialog(this, shareParams, new CallBack() {

			@Override
			public void callBack(String result) {
				Log.e("HeyiJoySDK", "分享结果:" + result);
			}
		}).show();
	}

	/* qq登录 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
		if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {// qq登录
			Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
		} else if (mSsoHandler != null) {// 微博登录
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
			initOpenidAndToken(values);
		}
	};

	public void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				QQUtil.showResultDialog(GameActivity.this, "返回为空", "登录失败");
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				QQUtil.showResultDialog(GameActivity.this, "返回为空", "登录失败");
				return;
			}
			QQUtil.showResultDialog(GameActivity.this, response.toString(), "登录成功");
			// 有奖分享处理
			// handlePrizeShare();
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			QQUtil.toastMessage(GameActivity.this, "onError: " + e.errorDetail);
			QQUtil.dismissDialog();
		}

		@Override
		public void onCancel() {
			QQUtil.toastMessage(GameActivity.this, "onCancel: ");
			QQUtil.dismissDialog();
			if (isServerSideLogin) {
				isServerSideLogin = false;
			}
		}
	}

	/* qq登录 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTencent != null) {
			mTencent.releaseResource();
		}
	}

	/* 微博登录 */
	/**
	 * 登入按钮的监听器，接收授权结果。
	 */
	private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
		@Override
		public void onSuccess(final Oauth2AccessToken token) {
			GameActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (token.isSessionValid()) {
						// 保存 Token 到 SharedPreferences
						AccessTokenKeeper.writeAccessToken(GameActivity.this, token);
						Toast.makeText(GameActivity.this, "成功", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

		@Override
		public void cancel() {
			Toast.makeText(GameActivity.this, "取消", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFailure(WbConnectErrorMessage errorMessage) {
			Toast.makeText(GameActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
		}
	}
	/* 微博登录 */
}
