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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.heyijoy.gamesdk.data.BlockParams;
import com.heyijoy.gamesdk.data.CookieContentResover;
import com.heyijoy.gamesdk.data.CookieContentResoverByCenter;
import com.heyijoy.gamesdk.data.GamePlayersCookieBean;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.data.OrderFloatBean;
import com.heyijoy.gamesdk.data.TaskBean;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.data.UserContentResover;
import com.heyijoy.gamesdk.data.VideSettingDataBean;
import com.heyijoy.gamesdk.data.VipBean;
import com.heyijoy.gamesdk.http.LoadImageAsyncTask;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.sql.MsgDBTool;
import com.heyijoy.gamesdk.util.Base64;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.sdk.ShareParams;

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
	private String appver;// 版本号 1.3通用版
	private String BRAND;// 无线终端品牌
	private String OS = "Android"; // 操作系统
	private String ProvidersCode = ""; // 运营商
	private String ProvidersName = ""; // 运营商
	private String IPAdd = ""; // IP地址
	private String newPackageID = null; // 礼包id
	private WindowManager.LayoutParams wmParams;
	private List<ResolveInfo> allPackageList;
	private Set<String> taskListId;
	private ArrayList<TaskBean> taskBeanList;
	private User currentUser;
	private int iconId = 0;
	private long timeDiff;// 跟后台的时间差
	private int showBindingDlgProbability;// 0~100
	private boolean isneedbind = false;// 是否是用户名注册登录的用户
	private boolean isPhoneRegOpen = false;// 输入手机号注册开关 默认关闭
	private boolean isNameRegOpen = false;// 输入用户名注册开关 默认关闭
	private boolean isHasTask = false, welfare = false, video = false;

	// floatSwitch 悬浮窗各个功能入口对应的开关信息，specialSwitch 红点是否显示的开关信息
	private boolean[] floatSwitch = null, specialSwitch = null;
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
	private String registerPlan = "1";// 注册方案，A方案-- "1" 有guid 生成用户名模式；B方案-- "2"
										// 无guid 生成用户名模式，默认 "1"

	private String welfareUrl = "";
	private String appType = "";
	private String mid = "";
	private HashMap<String, BitmapDrawable> preAvatar = null;

	private String client_id = "42c749c42d7a733f";
	private String client_secret = "8968f1f12e60255e0b30cc0f4b3743f8";
	private static int[] ordBack;// 悬浮窗图片资源id，按照默认顺序进行添加
	private static int[] specialBack;// 悬浮窗红点资源id，暂时有两个，前者礼包，后者消息
	private static int[] ordBackBig;// 对应的图片资源尺寸比ordBack的大些，用于个人中心上
	private static int[] specialBackBig;// 对应的图片资源尺寸比specialBack的大些，用于个人中心上，暂时有两个，前者礼包，后者消息
	// private IDDBTool iddbt;
	private MsgDBTool mdbt;
	private Boolean isHotWind = false;
	private boolean isAddRecordFunction;

	private boolean isInCocos2dxActivity;
	private boolean isInU3dActivity;

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
			setSystem();
			setVMParams();
			setSPForException();
			startPush();
			setPreDialog();
			setFloatDraw();
			setOrdBackData();
			setSpecialBackData();
			setOrdBackBigData();
			setSpecialBackBigData();
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

	public void setVideoSettingData(VideSettingDataBean videSettingDataBean) {
		Editor edit = getEditorForException();
		edit.putBoolean("isAutoUpload", videSettingDataBean.isAutoUpload());
		edit.putBoolean("isRecodeVoice", videSettingDataBean.isRecodeVoice());
		edit.putBoolean("isUseDataTraffic", videSettingDataBean.isUseDataTraffic());
		edit.putString("definitionQuality", videSettingDataBean.getDefinitionQuality());
		edit.commit();
	}

	public void setDataTrafficSwitch(boolean isSwitch) {
		Editor edit = getEditorForException();
		edit.putBoolean("isUseDataTraffic", isSwitch);
		edit.commit();
	}

	public VideSettingDataBean getVideoSettingData() {
		VideSettingDataBean videSettingDataBean = new VideSettingDataBean();
		SharedPreferences preferences = this.context.getSharedPreferences(HYConstant.YK_EXCEPTION,
				Context.MODE_PRIVATE);
		videSettingDataBean.setAutoUpload(preferences.getBoolean("isAutoUpload", true));
		videSettingDataBean.setRecodeVoice(preferences.getBoolean("isRecodeVoice", true));
		videSettingDataBean.setUseDataTraffic(preferences.getBoolean("isUseDataTraffic", false));
		videSettingDataBean.setDefinitionQuality(preferences.getString("definitionQuality", "2"));
		return videSettingDataBean;
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

	private void setFloatDraw() {
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
	}

	/**
	 * 取本地版本名
	 */
	public String getVesionName() {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo("net.vpntunnel", 0).versionName;
		} catch (NameNotFoundException e) {
			Logger.e(e.getMessage());
		}
		return versionName;
	}

	public boolean isHasTask() {
		return isHasTask;
	}

	public ArrayList<TaskBean> getTaskBeanList() {
		return taskBeanList;
	}

	public void setTaskBeanList(ArrayList<TaskBean> taskBeanList) {
		this.taskBeanList = taskBeanList;
	}

	public void clearTableListID() {
		if (taskListId == null) {
			return;
		} else {
			taskListId.clear();
			taskListId = null;
		}
	}

	public boolean getIsDiffTask() {
		if (taskListId == null) {
			return false;
		} else {
			return taskListId.size() == 2 ? true : false;
		}
	}

	public void setTableListID(String str) {
		if (taskListId == null) {
			taskListId = new HashSet<String>();
		}
		if (!taskListId.contains(str)) {
			taskListId.add(str);
		}
	}

	public void setHasTask(boolean isHasTask) {
		this.isHasTask = isHasTask;
	}

	public void setOrdBackData() {// 将ordBack 信息保存到 本地 SharedPreferences
//		Editor edit = getEditorForException();
//		for (int i = 0; i <= ordBack.length - 1; i++) {
//			edit.putInt("ord" + i, ordBack[i]);
//		}
//		edit.putInt("ordLen", ordBack.length);
//		edit.commit();
	}

	public int[] getOrdBackData() {// 获取本地保存的悬浮窗各个功能对应的图片资源，数组形式返回
		int ordLen = 0;
		SharedPreferences preferences = this.context.getSharedPreferences(HYConstant.YK_EXCEPTION,
				Context.MODE_PRIVATE);
		ordLen = preferences.getInt("ordLen", 0);
		if (ordLen == 0) {
			return null;
		}
		int[] ordBac = new int[ordLen];
		for (int i = 0; i <= ordLen - 1; i++) {
			ordBac[i] = preferences.getInt("ord" + i, 0);
		}
		return ordBac;
	}

	public void setSpecialBackData() {// 将specialBack 信息保存到 本地 SharedPreferences
//		Editor edit = getEditorForException();
//		for (int i = 0; i <= specialBack.length - 1; i++) {
//			edit.putInt("spe" + i, specialBack[i]);
//		}
//		edit.putInt("speLen", specialBack.length);
//		edit.commit();
	}

	public int[] getSpecialBackData() {// 获取本地保存的悬浮窗红点对应的图片资源，数组形式返回
		int specialLen = 0;
		SharedPreferences preferences = this.context.getSharedPreferences(HYConstant.YK_EXCEPTION,
				Context.MODE_PRIVATE);
		specialLen = preferences.getInt("speLen", 0);
		if (specialLen == 0) {
			return null;
		}
		int[] speBac = new int[specialLen];
		for (int i = 0; i <= specialLen - 1; i++) {
			speBac[i] = preferences.getInt("spe" + i, 0);
		}
		return speBac;
	}

	public void setOrdBackBigData() {// 将ordBackBig 信息保存到 本地 SharedPreferences
//		Editor edit = getEditorForException();
//		for (int i = 0; i <= ordBackBig.length - 1; i++) {
//			edit.putInt("ordbig" + i, ordBackBig[i]);
//		}
//		edit.putInt("ordBigLen", ordBackBig.length);
//		edit.commit();
	}

	public int[] getOrdBackBigData() {// 从SharedPreferences 中 获取到
										// 相应的个人中心的图片对应的资源id数组
		int ordBigLen = 0;
		SharedPreferences preferences = this.context.getSharedPreferences(HYConstant.YK_EXCEPTION,
				Context.MODE_PRIVATE);
		ordBigLen = preferences.getInt("ordBigLen", 0);
		if (ordBigLen == 0) {
			return null;
		}
		int[] ordBigBac = new int[ordBigLen];
		for (int i = 0; i <= ordBigLen - 1; i++) {
			ordBigBac[i] = preferences.getInt("ordbig" + i, 0);
		}
		return ordBigBac;
	}

	public void setSpecialBackBigData() {// 将specialBackBig 信息保存到 本地
											// SharedPreferences
//		Editor edit = getEditorForException();
//		for (int i = 0; i <= specialBackBig.length - 1; i++) {
//			edit.putInt("spebig" + i, specialBackBig[i]);
//		}
//		edit.putInt("speBigLen", specialBackBig.length);
//		edit.commit();
	}

	public int[] getSpecialBackBigData() {// 从SharedPreferences 中 获取到
											// 个人中心的红点图片对应的资源id信息
		int specialBigLen = 0;
		SharedPreferences preferences = this.context.getSharedPreferences(HYConstant.YK_EXCEPTION,
				Context.MODE_PRIVATE);
		specialBigLen = preferences.getInt("speBigLen", 0);
		if (specialBigLen == 0) {
			return null;
		}
		int[] speBigBac = new int[specialBigLen];
		for (int i = 0; i <= specialBigLen - 1; i++) {
			speBigBac[i] = preferences.getInt("spebig" + i, 0);
		}
		return speBigBac;
	}

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

	public int geticonid(Context context) {
		if (iconId == 0) {
			try {
				PackageManager mangager = context.getPackageManager();
				PackageInfo info = mangager.getPackageInfo(context.getPackageName(), 0);
				iconId = info.applicationInfo.icon;
			} catch (Exception e) {
				Logger.e(e.getMessage());
			}
		}
		return iconId;
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

	public OrderFloatBean getOrderFloatBean(Context context) {
		OrderFloatBean orderFloatBean = new OrderFloatBean();
		if (context == null) {
			orderFloatBean.setOrderFirst("1,2,3,4");
			orderFloatBean.setOrderSecond("5,6,7,8,9,10,11,13");
			return orderFloatBean;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.YK_EXCEPTION, Context.MODE_PRIVATE);
		orderFloatBean.setOrderFirst(preferences.getString("first", "1,2,3,4"));
		orderFloatBean.setOrderSecond(preferences.getString("second", "5,6,7,8,9,10,11,13"));
		return orderFloatBean;
	}

	public void setOrderFloatBean(OrderFloatBean orderFloatBean) {
		Editor edit = getEditorForException();
		edit.putString("first", orderFloatBean.getOrderFirst());
		edit.putString("second", orderFloatBean.getOrderSecond());
		edit.commit();
	}

	public int getShowBindingDlgProbability() {
		return showBindingDlgProbability;
	}

	public void setShowBindingDlgProbability(int showBindingDlgProbability) {
		this.showBindingDlgProbability = showBindingDlgProbability;
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

	public boolean isRemoteLogin() {
		return isRemoteLogin;
	}

	public void setRemoteLogin(boolean isRemoteLogin) {
		this.isRemoteLogin = isRemoteLogin;
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

	public boolean isWelfare() {
		return welfare;
	}

	public void setWelfare(boolean welfare) {
		this.welfare = welfare;
	}

	public String getWelfareUrl() {
		return welfareUrl;
	}

	public void setWelfareUrl(String welfareUrl) {
		this.welfareUrl = welfareUrl;
	}

	public boolean isVideo() {
		return video;
	}

	public void setVideo(boolean video) {
		this.video = video;
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

	public String getAppver() {
		return appver;
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

	/**
	 * 获取ip地址并保存
	 */
	private void setIPAdd() {
		IPAdd = Util.getLocalIpAddress();
	}

	public String getOS() {
		return OS;
	}

	public String getProvidersCode() {
		if (ProvidersCode == null || ProvidersCode.equals(""))
			setSystem();
		return ProvidersCode;
	}

	public String getProvidersName() {
		if (ProvidersName == null || ProvidersName.equals(""))
			setSystem();
		return ProvidersName;
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
		// 保存到游戏中心
		CookieContentResover.getInstance().insert(Base64.encode(cookie.getBytes()));
	}

	/**
	 * 获取 yktk
	 */
	public String getYktk() {

		String getCookie = getCookie();
		String yktk = "";
		if (getCookie == null || "".equals(getCookie)) {
			return "";
		}
		if (getCookie.contains("user_token")) {
			yktk = getCookie.substring(getCookie.indexOf(";") + 1);
		} else {
			yktk = getCookie;
		}
		return handleYktk(yktk);
	}

	/**
	 * 获取 token
	 */
	public String getYktoken() {

		String getCookie = getCookie();
		String token = "";
		if (getCookie.contains("user_token")) {
			try {
				JSONObject json = new JSONObject(getCookie);
				token = json.getString("user_token");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return token;
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
	 * 保存自动登录所需要的用户信息
	 */
	public String getCookie() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String cookie = preferences.getString(HYConstant.PREF_FILE_COOKIE, "");
		if ("".equals(cookie)) // 本地没有
		{
			cookie = CookieContentResover.getInstance().queryCookie();
			if (cookie == null || "".equals(cookie)) {
				GamePlayersCookieBean gocb = CookieContentResoverByCenter.getInstance().queryCookie();
				if (gocb == null) {
					return null;
				}
				cookie = gocb.getCookie();
				if (cookie == null || "".equals(cookie)) // 本地跟游戏中心都没有
				{
					return null;
				} else {
					cookie = new String(Base64.decode(cookie));
					String yktk = URLEncoder.encode(handleYktk(cookie));
					cookie = "yktk=" + yktk + ";";
					setRemoteLogin(true);// 本地没有，取游戏中心数据
					return cookie;
				}
			} else {
				setRemoteLogin(true);// 本地没有，取游戏中心数据
			}
		}
		return new String(Base64.decode(cookie));
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

	public boolean isMIUIorMEIZU() {// 关闭
		return false;
		/*
		 * if("Meizu".equals(android.os.Build.BRAND)){ return true; }else
		 * if(android
		 * .os.Build.BRAND!=null&&android.os.Build.BRAND.contains("xiaomi")){
		 * return true; } return false;
		 */
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
		if (UserContentResover.getInstance().queryByName(user.getUserName()) != null) {
			UserContentResover.getInstance().update(user);
		} else {
			UserContentResover.getInstance().insert(user);
		}

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
		if (UserContentResover.getInstance().queryByName(user.getUserName()) != null) {
			UserContentResover.getInstance().update(user);
		} else {
			UserContentResover.getInstance().insert(user);
		}
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
	 * @param device_id
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
		String pwd_remote = getpwdByName(userName);
		if (pwd_remote != null) {
			if ((!pwd.equals(pwd_remote)) && (!"".equals(pwd))) {
				user.setPassword(pwd_remote);
				setRemoteLogin(true);
			}
		} else if ("".equals(userName)) {
			user = UserContentResover.getInstance().queryCurrent();
			if ("".equals(user.getUserName())) {
				GamePlayersCookieBean gocb = CookieContentResoverByCenter.getInstance().queryCookie();
				if (gocb != null) {
					user.setUserName(gocb.getName());
				}
			}
			if (!"".equals(user.getUserName())) {
				setRemoteLogin(true);
			}
		}

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

	public boolean[] getFloatSwitch() {
		return floatSwitch;
	}

	public void setFloatSwitch(boolean[] floatSwitch) {
		this.floatSwitch = floatSwitch;
	}

	public boolean[] getSpecialSwitch() {
		return specialSwitch;
	}

	public void setSpecialSwitch(boolean[] specialSwitch) {
		this.specialSwitch = specialSwitch;
	}

	// 查询密码 根据用户名
	public String getpwdByName(String name) {
		String[] values = { name };

		Uri uri = Uri.parse(UserContentResover.getInstance().getCONTENT_URI());
		ContentResolver resolver = GameSDKApplication.getInstance().getContext().getContentResolver();
		Cursor cursor = resolver.query(uri, null, "userName = ? ", values, null);
		// 判断游标是否为空
		if ((cursor != null) && cursor.moveToFirst()) {
			String pwd = cursor.getString(1);
			cursor.close();
			return pwd;
		} else {
			return null;
		}
	}

	public void setVMParams() {
		wmParams = new WindowManager.LayoutParams();
	}

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}

	/**
	 * 保存自动登录所需要的用户信息
	 */
	public void saveVip(VipBean vipBean) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.VIP_IS_VIP, vipBean.getIsVip());// 0 非vip
																	// ，1轻会员
																	// ，2会员
		editor.putString(HYConstant.VIP_MSG, vipBean.getVipMsg());
		editor.putString(HYConstant.VIP_PKG_SWITCH, vipBean.getVipPkgSwitch());// 悬浮窗中vip列表展示开关
		editor.putString(HYConstant.VIP_SCHEME, vipBean.getVipScheme());
		editor.putString(HYConstant.VIP_SWITCH, vipBean.getVipSwitch());// 悬浮窗中vip购买展示开关
		editor.putString(HYConstant.USER_AVATAR, vipBean.getUserAvatar());
		editor.putString(HYConstant.USER_NAME, vipBean.getUserName());
		editor.putString(HYConstant.VIP_URL, vipBean.getVipUrl());// 购买vip活动展示地址
		editor.putString(HYConstant.VIP_SEND_PKG_MSG, vipBean.getVipSendPkgMsg());// vip购买支付成功后提示语
		editor.putString(HYConstant.FORUM_SWITCH, vipBean.getForumSwitch());// 论坛开关
		editor.putString(HYConstant.FORUM_URL, vipBean.getForumUrl());// 论坛地址
		editor.putBoolean(HYConstant.IS_VIP_GOOD, vipBean.getIsVipGood());
		editor.commit();
	}

	/**
	 * 获取 vipUrl
	 */
	public String getVipUrl() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String vipUrl = preferences.getString(HYConstant.VIP_URL, "");
		return vipUrl;
	}

	public VipBean getPreCenterUserMsg() {
		if (context == null) {
			return null;
		}
		VipBean vipBean = new VipBean();
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String userAvatar = preferences.getString(HYConstant.USER_AVATAR, "");
		String userName = preferences.getString(HYConstant.USER_NAME, "");
		Boolean isVipGood = preferences.getBoolean(HYConstant.IS_VIP_GOOD, true);
		vipBean.setUserAvatar(userAvatar);
		vipBean.setUserName(userName);
		vipBean.setIsVipGood(isVipGood);
		return vipBean;
	}

	public String getVipIsVip() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String isVip = preferences.getString(HYConstant.VIP_IS_VIP, "0");
		return isVip;
	}

	public String getVipSwitch() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String vipSwitch = preferences.getString(HYConstant.VIP_SWITCH, "0");
		return vipSwitch;
	}

	public String getVipScheme() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String vipSwitch = preferences.getString(HYConstant.VIP_SCHEME, "0");
		return vipSwitch;
	}

	public String getForumSwitch() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String forumSwitch = preferences.getString(HYConstant.FORUM_SWITCH, "0");
		return forumSwitch;
	}

	public BitmapDrawable getPreUserAvatar(String useravatar, ImageView preIv) {
		this.preIv = preIv;
		if (preAvatar.containsKey(useravatar)) {
			return preAvatar.get(useravatar);
		} else {
			getBitmapDrawable(useravatar);
			return null;
		}
	}

	private void getBitmapDrawable(String useravatar) {
		new AsyncTask<String, Integer, Bitmap>() {
			private String url = null;

			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					bd = new BitmapDrawable(result);
					preAvatar.put(url, bd);
					Message msg = Message.obtain();
					msg.obj = url;
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				url = params[0];
				Bitmap bitmap = LoadImageAsyncTask.GetBitmapByUrl(params[0]);
				return bitmap;
			}
		}.execute(useravatar);
	}

	public void clearHashMap() {
		preAvatar = null;
	}

	public String getNewPackageID() {
		return newPackageID;
	}

	public void setNewPackageID(String newPackageID) {
		this.newPackageID = newPackageID;
	}

	public String getForumUrl() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String forumUrl = preferences.getString(HYConstant.FORUM_URL, "");
		return forumUrl;
	}

	public String getVipMsg() {
		if (context == null) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		String isVip = preferences.getString(HYConstant.VIP_MSG, "");
		return isVip;
	}

	public String getToken() {
		if (context != null) {
			return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID)
					+ GameSDKApplication.getInstance().getAppType();
		}
		return null;
	}

	public String getPlushSign() {
		return Util.md5(
				HYConstant.PID + "&" + HYConstant.getSDKVersion() + "&" + GameSDKApplication.getInstance().getToken());
	}

	public String getPlushRecvSign() {
		return Util.md5(GameSDKApplication.getInstance().getMid() + "&" + HYConstant.PID + "&"
				+ HYConstant.getSDKVersion() + "&" + GameSDKApplication.getInstance().getToken());
	}

	public String getPckNameBySign() {
		if (context != null) {
			allPackageList = Util.getAllPagckage(context);
			for (int i = 0; i <= allPackageList.size() - 1; i++) {
				ResolveInfo localResolveInfo = allPackageList.get(i);
				String str1 = localResolveInfo.activityInfo.packageName;
				if (str1 != null && HYConstant.DEFAULT_PACKNAME.equals(str1)) {
					Logger.d("pack&ver", "first:" + str1);
					return HYConstant.DEFAULT_PACKNAME;
				}
				String str2 = Util.getSignMd5(context, str1);
				if (str2 != null && HYConstant.YKMAIN_SIGN_MD5.equals(str2)) {
					Logger.d("pack&ver", "second:" + str1);
					return str1;
				}
			}
		}
		Logger.d("pack&ver", "thrid！");
		return "";
	}

	// 取出source(从合乐智趣主客来源位置信息)
	public HashMap<String, String> getGameFromSource() {
		HashMap<String, String> gameCenterSourceMap = new HashMap<String, String>();
		if (context == null) {
			return gameCenterSourceMap;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		gameCenterSourceMap.put(HYConstant.COL_NAME_GAME_TRACE_SOURCE_1,
				preferences.getString(HYConstant.COL_NAME_GAME_TRACE_SOURCE_1, ""));
		gameCenterSourceMap.put(HYConstant.COL_NAME_GAME_TRACE_SOURCE_2,
				preferences.getString(HYConstant.COL_NAME_GAME_TRACE_SOURCE_2, ""));
		return gameCenterSourceMap;
	}

	// 查询并存储source(从合乐智趣主客来源位置信息)
	public void setGameFromSource(HashMap<String, String> gameCenterSourceMap) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_USER, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(HYConstant.COL_NAME_GAME_TRACE_SOURCE_1,
				gameCenterSourceMap.get(HYConstant.COL_NAME_GAME_TRACE_SOURCE_1));
		editor.putString(HYConstant.COL_NAME_GAME_TRACE_SOURCE_2,
				gameCenterSourceMap.get(HYConstant.COL_NAME_GAME_TRACE_SOURCE_2));
		editor.commit();
	}

	public void destroy() {
		if (app != null) {
			app = null;
		}

	}

	public boolean isInU3dActivity() {
		return isInU3dActivity;
	}

	public void setInU3dActivity(boolean isInU3dActivity) {
		this.isInU3dActivity = isInU3dActivity;
	}

	public boolean isInU2dOrCocos2dxActivity() {
		return this.isInCocos2dxActivity || this.isInU3dActivity;
	}

	public boolean isAddRecordFunction() {
		/*
		 * if(GameSDKApplication.getInstance().isInCocos2dxActivity()){
		 * if(YKRecordUploader.getCocosScrRecorder()!=null){ return true; }
		 * }else{ if(YKRecordUploader.getUnity3dScrRecorder()!=null){ return
		 * true; } } return false;
		 */
		return isAddRecordFunction;
	}

	public void initRecord() {
		// isAddRecordFunction = true;
		// YKAPIFactory.initSDK(context, GameSDKApplication
		// .getInstance().getClient_id(), GameSDKApplication.getInstance()
		// .getClient_secret());//多个SDK执行一次即可
		// com.ykcloud.sdk.platformtools.Log.setLevel(0);//设置录屏sdk log
	}

	/**
	 * 获取当前应用程序的包名
	 * 
	 * @param context
	 *            上下文对象
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
	 * @param status
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
	 * 
	 * @param context
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
	 * 
	 * @param context
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
	 * @param context
	 * @return
	 */
	public boolean getUserCertifitationStatus() {
		SharedPreferences preferences = context.getSharedPreferences("HeyiJoyCertifition", Context.MODE_PRIVATE);
		boolean status = preferences.getBoolean("userCertifition", false);
		return status;
	}
}
