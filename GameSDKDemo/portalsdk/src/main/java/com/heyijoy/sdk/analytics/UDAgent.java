package com.heyijoy.sdk.analytics;

import com.heyijoy.sdk.plugin.HeyiJoyUser;
import com.heyijoy.sdk.utils.ACache;
import com.heyijoy.sdk.utils.GUtils;
import com.heyijoy.sdk.verify.UToken;
import com.cs.csgamesdk.entity.Entrydata;
import com.cs.csgamesdk.sdk.CSGameSDK;
import com.heyijoy.sdk.CallBack;
import com.heyijoy.sdk.HeyiJoySDK;
import com.heyijoy.sdk.LoginLongService;
import com.heyijoy.sdk.UserExtraData;
import com.heyijoy.sdk.log.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class UDAgent {

	private static UDAgent instance;
	public final static String analytics_url = "https://matrix-api.youku-game.com/track/";
	private final static String analytics_init_url = "https://matrix-api.youku-game.com/server_info/";
	private UUserLog submitData;
	private String ext = "";

	private UDAgent() {

	}

	public static UDAgent getInstance() {

		if (instance == null) {
			instance = new UDAgent();
		}
		return instance;
	}

	/**
	 * 兼容方块
	 * 
	 * @param ext
	 */
	public void initBlock(String ext) {
		this.ext = ext;
	}

	public String getBlockParams() {
		return ext;
	}

	public void init(final Activity activity) {

		if (activity == null) {
			return;
		}

		UDManager.getInstance().initAnalytics(activity, analytics_init_url, new CallBack() {

			@Override
			public void callBack(String result) {
				initSubmit(activity);
			}
		});
	}

	/**
	 * 初始化提交设备信息
	 * 
	 * @param activity
	 */
	private void initSubmit(Activity activity) {
		UDManager.getInstance().submitInitData(activity, analytics_url, new CallBack() {

			@Override
			public void callBack(String result) {
				Log.d("HeyiJoySDK", "init-> submit success");
			}
		});
	}

	/**
	 * 上报用户信息，供数据统计使用
	 * 
	 * @param context
	 * @param user
	 */
	public void submitData(final Activity activity, final UserExtraData userExtraData, CallBack mCallBack) {

		Log.d("HeyiJoySDK", "submitData has been invoked");

		if (activity == null) {
			Log.e("HeyiJoySDK", "Activity is null");
			return;
		}

		if (mCallBack == null) {
			Log.e("HeyiJoySDK", "CallBack is null");
			return;
		}

		try {

			UToken token = HeyiJoySDK.getInstance().getUToken();
			if (token == null) {
				if (5 == userExtraData.getDataType()) {// 表示此时是退出游戏
					System.exit(0);
				}
				Log.e("HeyiJoySDK", "UToken is null, submit user info failed.");
				return;
			}

			if (userExtraData == null) {
				Log.e("HeyiJoySDK", "UserExtraData is null, submit user info failed.");
				return;
			} else {
				int dataType = userExtraData.getDataType();
				if (dataType == 3) {// 进入游戏,针对9377渠道
					String csIdentify = HeyiJoySDK.getInstance().getCsIdentify();
					if ("9377".equals(csIdentify)) {
						CSGameSDK.defaultSDK().SdkSetServerID(HeyiJoySDK.getInstance().getContext(),
								userExtraData.getServerID() + "");// 传游戏服务器序号

						SharedPreferences sp = HeyiJoySDK.getInstance().getContext().getSharedPreferences("csconfig",
								Context.MODE_PRIVATE);
						String userName = sp.getString("username", "");
						Log.d("HeyiJoySDK", "csIdentify userName is:" + userName);

						CSGameSDK.defaultSDK().enterdata(HeyiJoySDK.getInstance().getContext(),
								new Entrydata(userName));// 传递游戏名
					}
				}
			}

			long roleCreateTime = userExtraData.getRoleCreateTime();
			if (roleCreateTime <= 0) {
				roleCreateTime = System.currentTimeMillis() / 1000;
			}

			long levelUpTime = userExtraData.getRoleLevelUpTime();
			if (levelUpTime <= 0) {
				levelUpTime = System.currentTimeMillis() / 1000;
			}

			submitData = new UUserLog();
			submitData.setUser_id(token.getUserID() + "");
			submitData.setChannel_id(HeyiJoySDK.getInstance().getCurrChannel() + "");
//			submitData
//					.setFixed_time(GUtils.formatDate(System.currentTimeMillis() - GUtils.getTimesTamp(activity) + ""));
			submitData.setServer_id(userExtraData.getServerID() + "");
			submitData.setServer_name(userExtraData.getServerName());
			submitData.setRole_id(userExtraData.getRoleID());
			submitData.setRole_name(userExtraData.getRoleName());
			submitData.setUser_level(userExtraData.getRoleLevel());
			submitData.setImei(GUtils.getDeviceId(activity));
			submitData.setAndroid_id(GUtils.getAndroidId(activity));
			submitData.setUuid(GUtils.getUuid(activity));
			submitData.setStatus(submitData.getStatus());// 固定值
			submitData.setRole_create_time(roleCreateTime);
			submitData.setRole_level_up_time(levelUpTime);
			submitData.setMatrix_sdk_api_version(submitData.getMatrix_sdk_api_version());
			submitData.setMatrix_sdk_lang(submitData.getMatrix_sdk_lang());
			submitData.setMatrix_sdk_platform(submitData.getMatrix_sdk_platform());
			submitData.setMatrix_sdk_version(submitData.getMatrix_sdk_version());
			submitData.setMatrix_token(HeyiJoySDK.getInstance().getAppID() + "");
			submitData.setPackageName(activity.getPackageName());

			// 设备信息
			submitData.setMac(GUtils.getMacAddress(activity));
			submitData.setDevice_type(android.os.Build.MODEL);
			submitData.setDevice_dpi(GUtils.getScreenDpi(activity));

			ACache aCache = ACache.get(activity);
			aCache.put("login_long", submitData);

			int dataType = userExtraData.getDataType();
			if (1 == dataType) {
				submitData.setEvent("select server");
			} else if (2 == dataType) {
				submitData.setEvent("create role");
			} else if (3 == dataType) {
				submitData.setEvent("enter game");

				// 启动用户在线时长统计
				Intent service = new Intent();
				service.setClass(activity, LoginLongService.class);
				activity.startService(service);

			} else if (4 == dataType) {
				submitData.setEvent("level up");
			} else if (5 == dataType) {
				submitData.setEvent("exit game");
			} else if (6 == dataType) {
				submitData.setEvent("dopay");
				submitData.setOrderid(userExtraData.getOrderId());
			}

			UDManager.getInstance().submitUserInfo(activity, analytics_url, submitData, mCallBack);

			HeyiJoyUser.getInstance().submitExtraData(userExtraData);

		} catch (Exception e) {
			Log.e("HeyiJoySDK", "submit user info failed.\n" + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 自定义上报数据接口
	 */
	public void customData(Activity activity, String dataType, String customData, CallBack callBack) {
		UDManager.getInstance().customData(activity, analytics_url, dataType, customData, callBack);
	}
}
