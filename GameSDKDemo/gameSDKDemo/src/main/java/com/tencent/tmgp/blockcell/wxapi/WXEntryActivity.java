package com.tencent.tmgp.blockcell.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.util.Util;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private static IWXAPI api;
	private HYThridParams thridParams;
	private static String APPID;
	private static String APPSECRET;
	private String TAG = "HeyiJoySDK";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResources().getIdentifier("wx_activity", "layout", getPackageName()));
		thridParams = GameSDKApplication.getInstance().getThridParams();
		APPID = Util.getWXAppid(thridParams);
		APPSECRET = Util.getWXAppSecret(thridParams);
		if (TextUtils.isEmpty(APPID) || TextUtils.isEmpty(APPSECRET)) {
			Toast.makeText(WXEntryActivity.this, "参数异常，请重新启动游戏", Toast.LENGTH_SHORT).show();
		} else {
			api = WXAPIFactory.createWXAPI(this, APPID);
			api.handleIntent(getIntent(), this);
		}
	}

	/**
	 * 初始化微信
	 * 
	 * @param context
	 * @return
	 */
	public static IWXAPI initWeiXin(Context context) {
		if (TextUtils.isEmpty(APPID)) {
			Toast.makeText(context.getApplicationContext(), "微信appid不能为空", Toast.LENGTH_SHORT).show();
		}
		api = WXAPIFactory.createWXAPI(context, APPID, true);
		api.registerApp(APPID);
		return api;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	private String code;

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {// 微信登录
			switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				Log.d(TAG, "微信登录成功");
				// Toast.makeText(this, "微信授权成功", Toast.LENGTH_SHORT).show();
				code = ((SendAuth.Resp) resp).code;
				Intent intent = new Intent();
				intent.setAction("wx_login" + GameSDKApplication.getInstance().getAppid());
				intent.putExtra("wx_login", code);//直接写成，而不写成HYConstant.WX_LOGIN是为了可以顺利混淆HYConstant
				sendBroadcast(intent);
//				getAccessToken(code);
				System.out.println("code=" + code);
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(this, "授权取消", Toast.LENGTH_SHORT).show();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		} else {
			switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
				shareResult("0");
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
				shareResult("1");
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
				shareResult("2");
				break;
			default:
				break;
			}
		}

		finish();
	}

	public void shareResult(String result) {
		Intent intent = new Intent();
		intent.setAction("SHARE_WX" + GameSDKApplication.getInstance().getAppid());
		intent.putExtra("SHARE_WX", result);
		sendBroadcast(intent);
		finish();
	}

	public String getCodeUrl(String code) {
		return "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APPID + "&secret=" + APPSECRET + "&code="
				+ code + "&grant_type=authorization_code";
	}

	/**
	 * 获取授权口令 code 微信的临时code
	 */
	private void getAccessToken(String code) {
//		String url = getCodeUrl(code);
//
//		HYCallBackStr hyCallBack = new HYCallBackStr() {
//
//			@Override
//			public void callback(String str) {
//				processGetAccessTokenResult(str);
//			}
//
//		};
		// 网络请求获取access_token
//		HttpApi.getInstance().getWXAccessToken(url, hyCallBack);
	}

	/**
	 * 处理获取的授权信息结果
	 * 
	 * @param response
	 *            授权信息结果
	 */
	private void processGetAccessTokenResult(String response) {
		// 验证获取授权口令返回的信息是否成功
//		if (validateSuccess(response)) {
//			try {
//				Log.d("HeyiJoySDK", "response = " + response);
//				JSONObject json = new JSONObject(response);
//				String access_token = json.getString("access_token");
//				int expires_in = json.getInt("expires_in");
//				String refresh_token = json.getString("refresh_token");
//				String openid = json.getString("openid");
//				String scope = json.getString("scope");
//				String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid="
//						+ openid;
//
//				HYCallBackStr hyCallBack = new HYCallBackStr() {
//
//					@Override
//					public void callback(String str) {
//						 if (dialog != null && dialog.isShowing()) {
//						 dialog.dismiss();
//						 }
//						dealWithWxPersonInfo(str);
//					}
//
//				};
//
//				HttpApi.getInstance().getWXInfo(url, hyCallBack);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//		} else {
//			// 授权口令获取失败，解析返回错误信息
//			// 提示错误信息
//			Toast.makeText(this, "授权口令获取失败", Toast.LENGTH_SHORT).show();
//		}
	}

	/**
	 * 处理微信用户信息
	 * 
	 * @param str
	 */
	private void dealWithWxPersonInfo(String str) {
//		Log.d("HeyiJoySDK", "userinfo = " + str);
//		try {
//			JSONObject json = new JSONObject(str);
//			String openid = json.getString("openid");
//			String nickname = json.getString("nickname");
//			int sex = json.getInt("sex"); // 1男，2女
//			String language = json.getString("language");
//			String city = json.getString("city");
//			String province = json.getString("province");
//			String country = json.getString("country");
//			String headimgurl = json.getString("headimgurl");
//			String privilege = json.getString("privilege");// 这是一个数组
//			String unionid = json.getString("unionid");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 验证是否成功
	 * 
	 * @param response
	 *            返回消息
	 * @return 是否成功
	 */
	private boolean validateSuccess(String response) {
		return (!response.contains("errmsg") && (!response.contains("errcode")));
	}
}