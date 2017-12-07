/**
 * GameSDKApplication.java
 * com.heyijoy.gamesdk
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-2-18 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
 */

package com.heyijoy.gamesdk.act;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.ImageView;

import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.util.Base64;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.sdk.ShareParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author msh
 * @version
 * @Date 2014-2-18
 */
public class GameSDKApplication {
	private Context context;

	private String imsi;
	private String imei;
	private String mac;
//	private String device_id;
	private String guid;// 唯一标识
	private String sguid;// 唯一标识
	private String appname;
	private String appid;
	private String appkey;
	private String appPrivatekey;
	private String hotWindID;// 热云id
	private String OS = "Android"; // 操作系统
	private String ProvidersCode = ""; // 运营商
	private String ProvidersName = ""; // 运营商
	private String IPAdd = ""; // IP地址
	private String newPackageID = null; // 礼包id
	private WindowManager.LayoutParams wmParams;
	private User currentUser;
	private int iconId = 0;
	private long timeDiff;// 跟后台的时间差
	private boolean isneedbind = false;// 是否是用户名注册登录的用户
	private boolean isPhoneRegOpen = false;// 输入手机号注册开关 默认关闭
	private boolean isNameRegOpen = false;// 输入用户名注册开关 默认关闭

	// floatSwitch 悬浮窗各个功能入口对应的开关信息，specialSwitch 红点是否显示的开关信息
	private static GameSDKApplication app;
	private String paySwitch = "";
	private boolean isRemoteLogin = false;
	private Set<String> bindingList;
	private Handler mHandler = null;
	private boolean isStandAlone;// 单机环境
	private boolean isSend = false;// 是否发送
	private boolean flag = false;
	private BitmapDrawable bd = null;
	private ImageView preIv = null;

	private String welfareUrl = "";
	private String appType = "";
	private String mid = "";
	private HashMap<String, BitmapDrawable> preAvatar = null;

	private String client_id = "42c749c42d7a733f";
	private String client_secret = "8968f1f12e60255e0b30cc0f4b3743f8";
	private Boolean isHotWind = false;

	private boolean isInCocos2dxActivity;

	private String accress_token;

	public boolean isInCocos2dxActivity() {
		return isInCocos2dxActivity;
	}

	public void setInCocos2dxActivity(boolean isInCocos2dxActivity) {
		this.isInCocos2dxActivity = isInCocos2dxActivity;
	}

	public String getAccress_token() {
		return accress_token;
	}

	public void setAccress_token(String accress_token) {
		this.accress_token = accress_token;
	}

	public String getClient_id() {
		return client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	// public String getRegisterPlan() {
	// return registerPlan;
	// }
	//
	// public void setRegisterPlan(String registerPlan) {
	// this.registerPlan = registerPlan;
	// }

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	private GameSDKApplication() {
	}

	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
//			com.heyijoy.gamesdk.lib.R.a(context);// 初始化自生成的R文件
			setAppType();
			setAppid();
			setAppkey();
			setGuid();
//			setDeviceid();
			setSGuid();
			setAPPName();
			setAppPrivateKey();
			setHotWindID();
			setVMParams();
			setSPForException();
			startPush();
			setPreDialog();
		}
	}

	public void saveBlockCell(String ext) {

		String block_device_id = "";
		String block_username = "";
		String block_password = "";

		try {
			JSONObject jsonObject = new JSONObject(ext);
//			Log.d("HeyiJoySDK", "jsonObject=" + jsonObject.toString());
			block_device_id = jsonObject.getString("device_id");
			block_username = jsonObject.getString("block_username");
			block_password = jsonObject.getString("block_password");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			saveUserName(block_username);
			saveUserPwd(block_password);
			saveUserDeviceId(block_device_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 标记第一次使用了blockcell params
	 * 
	 * @param isFirst
	 */
	public void saveFistUseBlock(boolean isFirst) {
		SharedPreferences preferences = context.getSharedPreferences("fistblockcell", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("blockcell_first", isFirst);
		editor.commit();
	}

	public boolean getFirstUseBlock() {
		SharedPreferences preferences = context.getSharedPreferences("fistblockcell", Context.MODE_PRIVATE);
		return preferences.getBoolean("blockcell_first", false);
	}

	/**
	 * 设置device_id
	 */
//	private void setDeviceid() {
//		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		device_id = tm.getDeviceId();
//		if (TextUtils.isEmpty(device_id)) {
//			device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//			if (TextUtils.isEmpty(device_id)) {
//				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//				WifiInfo info = wifi.getConnectionInfo();
//				device_id = info.getMacAddress();
//			}
//		}
//		device_id = Util.md5(device_id);
//		Log.e("HeyiJoySDK", "DEVICE_ID=" + device_id);
//	}

	/**
	 * 获取三方登录分享等配置参数
	 * 
	 * @return
	 */
	public HYThridParams getThridParams() {
		Map<String, String> assetPropConfig = Util.getAssetPropConfig(context, "hy_thrid_config.properties");
		return new HYThridParams(assetPropConfig);
	}

	private void startPush() {
		Intent intent = new Intent();
		intent.setAction(HYConstant.START_YK_SDK_PUSH_RELAY);
		context.sendBroadcast(intent);
	}

	private void setSPForException() {
		Editor edit = getEditorForException();
		edit.putBoolean("flag", false);
		edit.commit();
	}

	private void setPreDialog() {
		preAvatar = new HashMap<String, BitmapDrawable>(0);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					String strUrl = (String) msg.obj;
					if (!"".equals(strUrl)) {
						BitmapDrawable bd = preAvatar.get(strUrl);
						preIv.setBackgroundDrawable(bd);
					}
					break;
				default:
					break;
				}
			}
		};
	}

	public SharedPreferences getSPForExceptionXML() {
		return context.getSharedPreferences(HYConstant.YK_EXCEPTION, Context.MODE_PRIVATE);
	}

	public boolean getFlagForException() {
		flag = getSPForExceptionXML().getBoolean("flag", false);
		return flag;
	}

	public Editor getEditorForException() {
		return getSPForExceptionXML().edit();
	}

	public SharedPreferences getSPForPackXML() {
		return context.getSharedPreferences(HYConstant.YK_PACK_VER, Context.MODE_PRIVATE);
	}

	/**
	 * 获取游戏中心的包名
	 * 
	 * @return
	 */
	public String getPackFormGameCenter() {

		return GameSDKApplication.getInstance().getSPForPackXML().getString(HYConstant.PACK_FROM_GAMECENTER, "");
	}

	public void setDataTrafficSwitch(boolean isSwitch) {
		Editor edit = getEditorForException();
		edit.putBoolean("isUseDataTraffic", isSwitch);
		edit.commit();
	}

	public static synchronized GameSDKApplication getInstance() {
		if (app == null) {
			app = new GameSDKApplication();
		}
		return app;
	}

	public boolean isIsneedbind() {
		return isneedbind;
	}

	public void setIsneedbind(boolean isneedbind) {
		this.isneedbind = isneedbind;
	}

	public Context getContext() {
		return context;
	}

//	private void setFloatDraw() {
//		ordBack = new int[12];
//		specialBack = new int[2];// 暂时有两个，前者礼包，后者消息
//		ordBackBig = new int[12];
//		specialBackBig = new int[2];// 暂时有两个，前者礼包，后者消息
//		ordBack[0] = R.drawable.hy_float_present_big_n;
//		ordBack[1] = R.drawable.hy_float_record_big;
//		ordBack[2] = R.drawable.hy_float_forum_big;
//		ordBack[3] = R.drawable.hy_float_msg_big_n;
//		ordBack[4] = R.drawable.hy_float_change_account_big;
//		ordBack[5] = R.drawable.hy_float_bind_big;
//		ordBack[6] = R.drawable.hy_float_vip_big;
//		ordBack[7] = R.drawable.hy_float_vip_code_big;
//		ordBack[8] = R.drawable.hy_float_account_big;
//		ordBack[9] = R.drawable.hy_float_task_big;
//		ordBack[10] = R.drawable.hy_float_welfare_big;
//		ordBack[11] = R.drawable.hy_float_consume_welfare_big;
//		specialBack[0] = R.drawable.hy_float_present_big_y;
//		specialBack[1] = R.drawable.hy_float_msg_big_y;
//		ordBackBig[0] = R.drawable.hy_float_present_big_n;
//		ordBackBig[1] = R.drawable.hy_float_record_big;
//		ordBackBig[2] = R.drawable.hy_float_forum_big;
//		ordBackBig[3] = R.drawable.hy_float_msg_big_n;
//		ordBackBig[4] = R.drawable.hy_float_change_account_big;
//		ordBackBig[5] = R.drawable.hy_float_bind_big;
//		ordBackBig[6] = R.drawable.hy_float_vip_big;
//		ordBackBig[7] = R.drawable.hy_float_vip_code_big;
//		ordBackBig[8] = R.drawable.hy_float_account_big;
//		ordBackBig[9] = R.drawable.hy_float_task_big;
//		ordBackBig[10] = R.drawable.hy_float_welfare_big;
//		ordBackBig[11] = R.drawable.hy_float_consume_welfare_big;
//		specialBackBig[0] = R.drawable.hy_float_present_big_y;
//		specialBackBig[1] = R.drawable.hy_float_msg_big_y;
//	}


	/**
	 * 取本地版本号
	 */
	public int getVersionCode() {
		int versionCode = 0;
		try {
			PackageManager mangager = context.getPackageManager();
			PackageInfo info = mangager.getPackageInfo(context.getPackageName(), 0);
			versionCode = info.versionCode;
		} catch (Exception e) {
			Logger.e(e.getMessage());
		}

		return versionCode;
	}


	private void setSGuid() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		mac = info.getMacAddress();
		imei = tm.getDeviceId();
		if (imei == null) {// 部分pad没有imei
			imei = "";
		}
		sguid = Util.md5(mac + "&" + imei + "&" + "&");
	}

	private void setGuid() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		mac = info.getMacAddress();
		imei = tm.getDeviceId();
		if (imei == null) {// 部分pad没有imei
			imei = "";
		}
		guid = Util.md5(mac + imei);
	}


	public boolean isPhoneRegOpen() {
		return isPhoneRegOpen;
	}

	public void setPhoneRegOpen(boolean isPhoneRegOpen) {
		this.isPhoneRegOpen = isPhoneRegOpen;
	}

	public boolean isNameRegOpen() {
		return isNameRegOpen;
	}

	public void setNameRegOpen(boolean isNameRegOpen) {
		this.isNameRegOpen = isNameRegOpen;
	}

	public boolean getIsbind(String userName) {
		if (bindingList == null) {
			return false;
		} else if (bindingList.contains(userName)) {
			return true;
		}
		return false;
	}

	public void setIsbind(String userName) {
		if (bindingList == null) {
			bindingList = new HashSet<String>();
		}
		bindingList.add(userName);
	}

	public boolean hasBinded() {
		if (!GameSDKApplication.getInstance().isIsneedbind()) {
			return true;
		} else if (GameSDKApplication.getInstance()
				.getIsbind(GameSDKApplication.getInstance().getUserFromPref(context).getUserName())) {// 如果已经绑定，返回
			return true;
		}
		return false;
	}


	private void setAPPName() {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			String msg = "";
			try {
				msg = appInfo.metaData.getString("HYGAME_APPNAME");
			} catch (Exception e) {
				msg = appInfo.metaData.getInt("HYGAME_APPNAME") + "";
			}
			appname = msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();

		}
	}

	public String getAppname() {
		return appname;
	}


	/**
	 * 获取ip地址并保存
	 */
	private void setIPAdd() {
		IPAdd = Util.getLocalIpAddress();
	}

	public String getOS() {
		return OS;
	}

	public String getIPAdd() {
		if (IPAdd == null || IPAdd.equals(""))
			setIPAdd();
		return IPAdd;
	}

	public String getAppTaskkey() {
		return HYConstant.YKMAIN_SIGN_MD5_TASK;
	}

	private void setAppType() {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			String msg = "";
			try {
				msg = appInfo.metaData.getInt("app_type") + "";
			} catch (Exception e) {
				msg = appInfo.metaData.getString("app_type");
			}
			appType = msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();

		}
	}

	private void setAppid() {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			String msg = "";
			try {
				msg = appInfo.metaData.getInt("HYGAME_APPID") + "";
			} catch (Exception e) {
				msg = appInfo.metaData.getString("HYGAME_APPID");
			}
			appid = msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();

		}
	}

	private void setAppkey() {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString("HYGAME_APPKEY");
			appkey = msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setAppPrivateKey() {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString("HYGAME_PRIVATEKEY");
			appPrivatekey = msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setHotWindID() {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString("HYGAME_HOTWIND");
			hotWindID = msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getAppPrivateKey() {
		return appPrivatekey;
	}

	public String getAppType() {
		return appType;
	}

	public String getAppid() {
		return appid;
	}

	public String getAppkey() {
		return appkey;
	}

	public String getHotWindID() {// 热云id
		return hotWindID;
	}

	public String getSGuid() {
		return sguid;
	}

	public String getGuid() {
		return guid;
	}

	public String getMac() {
		return mac;
	}

//	public String getDeviceid() {
//		return device_id;
//	}

	public String getImei() {
		return imei;
	}

	/**
	 * 取出自动登录所需要的用户信息
	 */
	public User getShareUser() {
		User user = new User();
		if (context == null) {
			return user;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		user.setUid(preferences.getString("PREF_FILE_USER_UID", ""));
		user.setPhone(preferences.getString("PREF_FILE_USER_PHONE", ""));
		user.setUserName(preferences.getString("PREF_FILE_USER_NAME", ""));
		user.setPassword(preferences.getString("PREF_FILE_USER_PWD", ""));
		user.setIsPay(preferences.getBoolean(HYConstant.PREF_FILE_USER_ISPAY, false));
		return user;
	}

	/**
	 * 登录，注册成功保存用户信息
	 * 
	 * @param user
	 */
	public void setShareUser(User user) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.PREF_FILE_USER_UID, user.getUid());
		editor.putString(HYConstant.PREF_FILE_USER_NAME, user.getUserName());
		editor.putString(HYConstant.PREF_FILE_USER_PWD, user.getPassword());
		editor.putString(HYConstant.MAIN_USER_THRID, user.getBind3rd());
		editor.putString(HYConstant.MAIN_USER_LIFECODE, user.getLifecode());// 保存lifecode
		editor.putBoolean(HYConstant.PREF_FILE_USER_ISPAY, user.getIsPay());
		if (!"".equals(user.getPhone()) || null != user.getPhone()) {
			editor.putString(HYConstant.PREF_FILE_USER_PHONE, user.getPhone());
		}
		editor.commit();
	}

	/**
	 * 取出lifecode
	 */
	public String getLifeCode() {
		if (context == null) {
			return "";
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		return preferences.getString(HYConstant.MAIN_USER_LIFECODE, "");
	}

	/**
	 * 取出用户登录或绑定三方状态，"" 是游客或注册用户名状态，wechat是微信登录或者游客绑定微信，qq是QQ登录，weibo是微博
	 */
	public String getUserLoginType() {
		if (context == null) {
			return "";
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		return preferences.getString(HYConstant.MAIN_USER_THRID, "");
	}

	public void setUserLoginType(String type) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.MAIN_USER_THRID, type);
		editor.commit();
	}
	
	/**
	 * 获取用户注销前的一次登录类型，游客，微信，qq，还是微博
	 * @return
	 */
	public String getBeforeLogoutUserLoginType() {
		if (context == null) {
			return "";
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		return preferences.getString(HYConstant.MAIN_USER_BEFORE_LOGOUT, "");
	}
	
	public void SetBeforeLogoutUserLoginType(String type) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.MAIN_USER_BEFORE_LOGOUT, type);
		editor.commit();
	}

	/**
	 * 保存充值卡信息
	 */
	// public void saveInpourcardInfo(String info) {
	// if (context == null) {
	// return;
	// }
	// SharedPreferences preferences = context.getSharedPreferences(
	// HYConstant.PREF_FILE_INPOURCARD_INFO, Context.MODE_PRIVATE);
	// Editor editor = preferences.edit();
	// if (info == null) {
	// info = "";
	// }
	// editor.putString(HYConstant.PREF_FILE_INPOURCARD_INFO, info);
	// editor.commit();
	// }

	/**
	 * 取充值卡信息
	 */
	// public String getInpourcardInfo() {
	// if (context == null) {
	// return null;
	// }
	// SharedPreferences preferences = context.getSharedPreferences(
	// HYConstant.PREF_FILE_INPOURCARD_INFO, Context.MODE_PRIVATE);
	// String info = preferences.getString(
	// HYConstant.PREF_FILE_INPOURCARD_INFO, "");
	// return info;
	// }

	/**
	 * 保存充值卡描述
	 */
	// public void saveInpourcardDesc(String desc) {
	// if (context == null) {
	// return;
	// }
	// SharedPreferences preferences = context.getSharedPreferences(
	// HYConstant.PREF_FILE_INPOURCARD_DESC, Context.MODE_PRIVATE);
	// Editor editor = preferences.edit();
	// if (desc == null) {
	// desc = "";
	// }
	// editor.putString(HYConstant.PREF_FILE_INPOURCARD_DESC, desc);
	// editor.commit();
	// }

	/**
	 * 取充值卡信息
	 */
	// public String getInpourcardDesc() {
	// if (context == null) {
	// return null;
	// }
	// SharedPreferences preferences = context.getSharedPreferences(
	// HYConstant.PREF_FILE_INPOURCARD_DESC, Context.MODE_PRIVATE);
	// String desc = preferences.getString(
	// HYConstant.PREF_FILE_INPOURCARD_DESC, "");
	// return desc;
	// }

	/**
	 * 保存自动登录所需要的用户信息
	 */
	public void saveCookie(String cookie) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		if (cookie == null) {
			cookie = "";
		}
		editor.putString(HYConstant.PREF_FILE_COOKIE, Base64.encode(cookie.getBytes()));
		editor.commit();

	}


	public String handleYktk(String yktk) {
		if (yktk == null || "".equals(yktk)) {
			return "";
		}

		if (yktk.contains("\"")) {
			return yktk.substring(yktk.indexOf("\"") + 1, yktk.indexOf(";") - 1);
		} else {
			return yktk.substring(yktk.indexOf("=") + 1, yktk.indexOf(";"));
		}
	}


	/**
	 * 保存注销状态
	 */
	public void saveLogoutSharePreferences(Boolean logout) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(HYConstant.PREF_FILE_LOGOUT_STATUS, logout);
		editor.commit();
	}

	/**
	 * 取注销状态
	 */
	public boolean getLogoutSharePreferences() {
		if (context == null) {
			return false;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		return preferences.getBoolean(HYConstant.PREF_FILE_LOGOUT_STATUS, false);
	}


	public String getProvidersCode() {
		if (ProvidersCode == null || ProvidersCode.equals(""))
			setSystem();
		return ProvidersCode;
	}

	public void setSystem() {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String IMSI = telephonyManager.getSubscriberId();
		// 返回唯一的用户ID;就是这张卡的编号神马的
		IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02 07是中国移动，01 06是中国联通，03 05是中国电信。
		if (IMSI != null && IMSI.length() >= 5) {
			setImsi(IMSI);// 初始化imsi
			IMSI = IMSI.substring(0, 5);
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
				ProvidersCode = "1";
				ProvidersName = "中国移动";
			} else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
				ProvidersCode = "2";
				ProvidersName = "中国联通";
			} else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) {
				ProvidersCode = "3";
				ProvidersName = "中国电信";
			} else if (IMSI.startsWith("46020")) {
				ProvidersName = "中国铁通";
			}
			ProvidersName = URLEncoder.encode(ProvidersName += "_" + IMSI);// 运营商名字是汉子，需要urlencode
		} else {
			ProvidersName = "";
		}

	}
	public long getServiceTime() {
		long local = System.currentTimeMillis() / 1000;
		long diff = this.timeDiff;
		Logger.v("localtime--" + local + "diff---" + diff + "servicetime---" + (local - diff));
		return local - diff;
	}

	public String getRegMSGContent() {
		String content = HYConstant.MSG_REG_CONTENT + "&" + GameSDKApplication.getInstance().getAppid() + "&"
				+ GameSDKApplication.getInstance().getGuid() + "&" + GameSDKApplication.getInstance().getServiceTime()
				+ "&" + HYConstant.getSDKVersion();
		return content;
	}

	public void initTimeDiff(long serviceTime) {
		long local = System.currentTimeMillis() / 1000;
		Logger.v("init---localtime--" + local + "diff---" + (local - serviceTime) + "servicetime---" + serviceTime);
		this.timeDiff = local - serviceTime;
	}

	public User getCurrentUser() {
		if (currentUser == null) {
			currentUser = new User();
		}
		return currentUser;
	}

	public String getImsi() {
		return imsi;
	}

	private void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * 保存自动登录所需要的用户信息
	 */
	public void saveShareUser(User user) {
		saveShareUserLocal(user);
	}

	/**
	 * 保存分享的参数
	 */
	public void saveShareParams(ShareParams params) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER_SHARE,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.SHARE_TITLE, params.getTitle());
		editor.putString(HYConstant.SHARE_DESC, params.getContent());
		editor.putString(HYConstant.SHARE_ICON_NAME, params.getNotifyIconText());
		editor.putString(HYConstant.SHARE_WEBURL, params.getSourceUrl());
		editor.putString(HYConstant.SHARE_IMGURL, params.getImgUrl());
		editor.commit();
	}

	/**
	 * 获取分享的参数
	 */
	public ShareParams getShareParams() {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER_SHARE,
				Context.MODE_PRIVATE);
		ShareParams shareParams = new ShareParams();
		shareParams.setTitle(preferences.getString(HYConstant.SHARE_TITLE, ""));
		shareParams.setContent(preferences.getString(HYConstant.SHARE_DESC, ""));
		shareParams.setNotifyIconText(preferences.getString(HYConstant.SHARE_ICON_NAME, ""));
		shareParams.setSourceUrl(preferences.getString(HYConstant.SHARE_WEBURL, ""));
		shareParams.setImgUrl(preferences.getString(HYConstant.SHARE_IMGURL, ""));
		return shareParams;
	}

	/**
	 * 保存自动登录所需要的用户信息(只保存到本地，不往游戏中心回写)
	 */
	public void saveShareUserLocal(User user) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.PREF_FILE_USER_NAME, user.getUserName());
		editor.putString(HYConstant.PREF_FILE_USER_PWD, user.getPassword());
		editor.putString(HYConstant.PREF_FILE_USER_UID, user.getUid());
		editor.putBoolean(HYConstant.PREF_FILE_USER_ISPAY, user.getIsPay());
		editor.commit();
	}

	/**
	 * 存储手机号到本地
	 * 
	 * @param phone
	 */
	public void savePhonePre(String phone) {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.PREF_FILE_USER_PHONE, phone);
		editor.commit();
	}

	/**
	 * 取出保存的手机号
	 * 
	 * @return
	 */
	public String getPhonePre() {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String phone = preferences.getString(HYConstant.PREF_FILE_USER_PHONE, "");
		return phone;
	}

	/**
	 * 更新用户账户本地密码
	 * 
	 * @param user
	 */
	public void updatePwd(User user) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.PREF_FILE_USER_PWD, user.getNew_password());
		editor.commit();
	}

	/**
	 * 存用户名到本地
	 * 
	 * @param username
	 */
	public void saveUserName(String username) {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.PREF_FILE_USER_NAME, username);
		editor.commit();
	}

	/**
	 * 获取本地用户名
	 * 
	 * @return username
	 */
	public String getUserName() {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String userName = preferences.getString(HYConstant.PREF_FILE_USER_NAME, "");
		return userName;
	}

	/**
	 * 存用户名密码到本地
	 * 
	 * @param pwd
	 */
	public void saveUserPwd(String pwd) {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.PREF_FILE_USER_PWD, pwd);
		editor.commit();
	}

	/**
	 * 获取本地用户密码
	 * 
	 * @return username
	 */
	public String getUserPwd() {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String userName = preferences.getString(HYConstant.PREF_FILE_USER_PWD, "");
		return userName;
	}

	/**
	 * 存用户设备id到本地，方块传的
	 * 
	 * @param block_device_id
	 */
	public void saveUserDeviceId(String block_device_id) {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.PREF_FILE_USER_DEVICEID, block_device_id);
		editor.commit();
	}

	/**
	 * 获取本地设备id,方块传的
	 * 
	 * @return
	 */
	public String getUserDeviceId() {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String device_id = preferences.getString(HYConstant.PREF_FILE_USER_DEVICEID, "");
		return device_id;
	}

	/**
	 * 获取用户id
	 * 
	 * @return userid
	 */
	public String getUserId() {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String userId = preferences.getString(HYConstant.PREF_FILE_USER_UID, "");
		return userId;
	}

	// 删除本地用户信息
	public void deleteUserInPref(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// editor.putString("PREF_FILE_USER_NAME", ""); // value to store
		editor.putString(HYConstant.PREF_FILE_USER_PWD, ""); // value to store
		editor.putString(HYConstant.PREF_FILE_USER_NAME, ""); // value to store
		editor.putString(HYConstant.PREF_FILE_USER_UID, ""); // value to store
		editor.putString(HYConstant.PREF_FILE_USER_PHONE, ""); // value to store
		editor.commit();
	}

	// 删除本地密码
	public void deletePwdINPref(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// editor.putString("PREF_FILE_USER_NAME", ""); // value to store
		editor.putString(HYConstant.PREF_FILE_USER_PWD, ""); // value to store
		editor.commit();
	}

	public User getUserFromPref(Context context) {
		User user = new User();
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String userName = preferences.getString(HYConstant.PREF_FILE_USER_NAME, "");
		String pwd = preferences.getString(HYConstant.PREF_FILE_USER_PWD, "");
		String uid = preferences.getString(HYConstant.PREF_FILE_USER_UID, "");
		boolean isPay = preferences.getBoolean(HYConstant.PREF_FILE_USER_ISPAY, false);
		user.setUserName(userName);
		user.setPassword(pwd);
		user.setUid(uid);
		user.setIsPay(isPay);
		return user;
	}

	public String getPaySwitch() {
		return paySwitch;
	}

	public void setPaySwitch(String paySwitch) {
		this.paySwitch = paySwitch;
	}

	public boolean isStandAlone() {
		return isStandAlone;
	}

	public void setStandAlone(boolean isStandAlone) {
		this.isStandAlone = isStandAlone;
	}


	public void setVMParams() {
		wmParams = new WindowManager.LayoutParams();
	}

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}



	/**
	 * 获取当前应用程序的包名
	 *
	 * @return 返回包名
	 */
	public String getPageName() {
		// 当前应用pid
		int pid = android.os.Process.myPid();
		// 任务管理类
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 遍历所有应用
		List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo info : infos) {
			if (info.pid == pid)// 得到当前应用
				return info.processName;// 返回包名
		}
		return "";
	}

	/**
	 * 存储实名认证开关状态
	 * 
	 * @param status_pay
	 */
	public void saveCertifitationStatus(String status_auth, String status_pay) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences("HeyiJoyCertifition", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("certifition", status_auth);
		editor.putString("certifition_pay", status_pay);
		editor.commit();
	}

	/**
	 * 获取实名认证状态开关
	 * @return
	 */
	public String getCertifitationStatus() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences("HeyiJoyCertifition", Context.MODE_PRIVATE);
		String status = preferences.getString("certifition", "");
		return status;
	}

	/**
	 * 获取实名认证支付状态开关on 开，off 关
	 * @return
	 */
	public String getCertifitationPayStatus() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences("HeyiJoyCertifition", Context.MODE_PRIVATE);
		String status = preferences.getString("certifition_pay", "");
		return status;
	}

	/**
	 * 存储用户是否实名认证true 已经实名，false未实名
	 * 
	 * @param status
	 */
	public void saveUserCertifitationStatus(boolean status) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences("HeyiJoyCertifition", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("userCertifition", status);
		editor.commit();
	}

	/**
	 * 获取用户实名认证状态
	 *
	 * @return
	 */
	public boolean getUserCertifitationStatus() {
		SharedPreferences preferences = context.getSharedPreferences("HeyiJoyCertifition", Context.MODE_PRIVATE);
		boolean status = preferences.getBoolean("userCertifition", false);
		return status;
	}
}
