package com.tencent.tmgp.blockcell;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.act.HYCallBackWithContext;
import com.heyijoy.gamesdk.act.HYInit;
import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.HYGameUser;
import com.heyijoy.gamesdk.util.EncryptUtils;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.sdk.analytics.UDAgent;
import com.tencent.tmgp.blockcell.R;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	public static String cp_uid = "";
	private ProgressDialog dlg;
	private HYGameUser hyGameUser = null;
	private TextView tv_user;
	private WaitingBar waitBar;
	private Button btn_enter;
	private String session = null;
	boolean flag = false;
	private String TAG = "HeyiJoySDK";
	private String SH = "ÔÐÐÜöþÐÛØØÒÚØÈÜØ²ò¯ÏñØÒêì¨Úõ©²­ÛÊíÝÚë÷ÁÒ×úãèí¬²ãí¶ûÛÞ¨ÃìÛúîÞÔ®«¶ôÔ¡ÕÖ×Øñëý÷¶­òÛôé«ßóÍÚèñ¨ýïÀñÒø¯ò®úÞßëïÐøõÚÐàýó÷ÞÜÉõøØöóóÑðÐªÕÖà";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// YKPlatform.setStandAlone(true);
		tv_user = (TextView) findViewById(R.id.tv_user);
		waitBar = (WaitingBar) findViewById(R.id.waitingBar1);
		btn_enter = (Button) findViewById(R.id.btn_enter);

		String encryption = EncryptUtils.getEncryption(SH);
		System.out.println("encryption="+encryption);
		
		String hh = EncryptUtils.getEncryption(encryption);
		System.out.println("oldencryption="+hh);

//		String ext = "";
		String ext = "{'device_id':'600e57441571070c99c2945adca9e1e2','block_username':'Guest_8944','block_password':''}";
		UDAgent.getInstance().initBlock(ext);
		
		boolean isNotNeedInit = getIntent().getBooleanExtra("isNotNeedInit", false);
		if (!isNotNeedInit) {
			waitBar.setVisibility(View.VISIBLE);
			btn_enter.setClickable(false);
			new HYInit(MainActivity.this).init(initCallback,true);
		}
		
	}

	// 点击进入游戏
	public void enterOnClick(View view) {
		// 非单机游戏需要走登录流程
		if (session != null) {// 若已登录则直接进入游戏
			cpLoginVerify(session);// cp验证用户登录的合法性
		} else {// 若未登录调用合乐智趣登录接口
			HYPlatform.autoLogin(loginCallback, MainActivity.this);// 调用合乐智趣登录接口
		}
	}

	// 合乐智趣初始化的回调方法
	HYCallBack initCallback = new HYCallBack() {
		@Override
		public void onSuccess(Bean bean) {
			btn_enter.setClickable(true);
			waitBar.setVisibility(View.GONE);
			Toast.makeText(MainActivity.this, "我初始化成功啦", Toast.LENGTH_SHORT).show();
//			HYPlatform.autoLogin(loginCallback, MainActivity.this);
			// 调用合乐智趣登录接口
																	// 单机游戏不需要调用
		}

		@Override
		public void onFailed(int code, String failReason) {
			btn_enter.setClickable(true);
			waitBar.setVisibility(View.GONE);
			// Toast.makeText(MainActivity.this, failReason,
			// Toast.LENGTH_LONG).show();
		}
	};

	// 合乐智趣登录的回调方法
	HYCallBack loginCallback = new HYCallBack() {
		@Override
		public void onSuccess(Bean bean) {
			hyGameUser = (HYGameUser) bean;
			session = hyGameUser.getSession();// 合乐智趣游戏平台唯一标示，消费时需要用到
			String userName = hyGameUser.getUserName();// 用户名
			tv_user.setText("  用户名:" + userName + "\n" + " session:" + session);
			startHYFloat();// 登陆成功后启动悬浮窗
		}

		@Override
		public void onFailed(int code, String failReason) {
			Toast.makeText(MainActivity.this, "登录失败" + failReason, Toast.LENGTH_LONG).show();
		}
	};

	// 悬浮窗的回调实现方法
	HYCallBackWithContext callbackChangeAccount = new HYCallBackWithContext() {
		@Override
		public void callback(Context context) {
			HYPlatform.logout(context);// 注销账号方法
			// 注销账号后回到登陆页（代码需cp自行编写）
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.putExtra("isNotNeedInit", true);
			MainActivity.this.finish();
			startActivity(intent);
		}
	};

	private void startHYFloat() {
		// 登陆成功后调用悬浮窗接口
		HYPlatform.startHYFloat(MainActivity.this, callbackChangeAccount);
		// YKPlatform.startHYFloat(MainActivity.this);//启动无切换账号回调功能的悬浮窗
	}

	/**
	 * 模拟cp使用从合乐智趣拿到的“session”验证用户登录合法性，并取得用户信息的代码，仅供参考，cp可以依自己的风格自行编写
	 */
	private void cpLoginVerify(final String session) {
		dlg = ProgressDialog.show(this, null, "Please wait...", true, false);
		new Thread() {
			public void run() {
				ApplicationInfo appInfo = null;
				String appKey = "";
				try {
					appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
					appKey = appInfo.metaData.getString("HYGAME_APPKEY");
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				// List<NameValuePair> params = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("appkey", appKey));
				// params.add(new BasicNameValuePair("sessionid", session));
				// String uri = HYConstants.VERIFY_URI;//模拟cp服务器验证session地址
				// String result ;
				// result = HttpTool.post(uri, params);
				String result = "{\"status\": \"success\", \"result\": {\"uid\": \"1983\"}}";
				Message msg = new Message();
				msg.what = 1;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1: {
				dlg.dismiss();
				String result = (String) msg.obj;
				try {
					if (result.equals("{}")) {
						Toast.makeText(MainActivity.this, "账号验证过期,请重试", 2000).show();
					} else if (result != null && !result.equals("")) {
						JSONObject json = new JSONObject(result);
						String status = json.getString("status");
						if (status.equals("success")) {
							JSONObject ujson = json.getJSONObject("result");
							cp_uid = ujson.getString("uid");
							Intent intent = new Intent(MainActivity.this, GameActivity.class);
							intent.putExtra("user", hyGameUser);
							MainActivity.this.startActivity(intent);
							MainActivity.this.finish();
						} else {
							Toast.makeText(MainActivity.this, "账号验证失败", 2000).show();
						}
					} else {
						Toast.makeText(MainActivity.this, "账号验证失败", 2000).show();
					}
				} catch (JSONException e) {
					Toast.makeText(MainActivity.this, "账号验证失败", 2000).show();
					e.printStackTrace();
				}
			}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 单机模式不调用合乐智趣退出接口
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			HYCallBack callBack = new HYCallBack() {
				@Override
				public void onSuccess(Bean bean) {
					MainActivity.this.finish();
				}

				@Override
				public void onFailed(int code, String failReason) {
					// 针对对话框dismiss的监听回调的方法，此处可以增添取消对话框后的其他处理
				}
			};
			HYPlatform.quit(MainActivity.this, callBack);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		HYPlatform.exceptionYKFinish(MainActivity.this);
	}
}
