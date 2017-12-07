package com.heyijoy.sdk.analytics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.heyijoy.sdk.http.HYHttpApi;
import com.heyijoy.sdk.log.Log;
import com.heyijoy.sdk.utils.GUtils;
import com.heyijoy.sdk.CallBack;
import com.heyijoy.sdk.HeyiJoySDK;

public class UDManager {

	private static JSONObject params;
	private static JSONObject params_data;
	private static JSONObject params_context;
	private static JSONObject params_matrix;
	private static UDManager instance;

	private UDManager() {

	}

	public static UDManager getInstance() {
		if (instance == null) {
			instance = new UDManager();
		}
		return instance;
	}

	public void initAnalytics(final Activity activity, String url, final CallBack mCallBack) {

		CallBack callBack = new CallBack() {

			@Override
			public void callBack(String data) {
				if (data != null) {
					if (data.contains("server_timestamp")) {
						try {
							JSONObject jsonObject = new JSONObject(data);
							long server_timestamp = jsonObject.getLong("server_timestamp") * 1000;
							long current_timestamp = System.currentTimeMillis();
							GUtils.saveTimesTamp(activity, current_timestamp - server_timestamp);

							mCallBack.callBack("");
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Log.e("HeyiJoySDK", "server_timestamp is missing");
					}
				} else {
					Log.e("HeyiJoySDK", "init data is null");
				}
			}
		};

		HYHttpApi.getInstance().httpsGet(url, callBack);

	}

	public void submitUserInfo(Activity context, String url, UUserLog submitData, CallBack mCallBack) {

		try {
			if (submitData != null) {

				params_data = new JSONObject();
				params_context = new JSONObject();
				params_matrix = new JSONObject();

				params_data.put("event", submitData.getEvent());
				Log.d("HeyiJoySDK", "event=" + submitData.getEvent());

				params_context.put("appkey", HeyiJoySDK.getInstance().getAppKey() + "");
				params_context.put("user_id", submitData.getUser_id());
				params_context.put("channel_id", HeyiJoySDK.getInstance().getCurrChannel() + "");
				params_context.put("fixed_time",
						GUtils.formatDate(System.currentTimeMillis() - GUtils.getTimesTamp(context) + ""));
				params_context.put("server_id", submitData.getServer_id());
				params_context.put("server_name", submitData.getServer_name());
				params_context.put("role_id", submitData.getRole_id());
				params_context.put("role_name", submitData.getRole_name());
				params_context.put("user_level", submitData.getUser_level());
				params_context.put("device_id", submitData.getImei());
				params_context.put("android_id", submitData.getAndroid_id());
				params_context.put("uuid", submitData.getUuid());
				params_context.put("status", submitData.getStatus());
				params_context.put("role_create_time", submitData.getRole_create_time());
				params_context.put("role_level_up_time", submitData.getRole_level_up_time());
				params_context.put("bundle_id", submitData.getPackageName());
				params_context.put("cp_version", getPackageInfo(context).versionCode);// 游戏版本号
				params_context.put("sdk_version", HeyiJoySDK.getInstance().getSDKVersionCode());// sdk版本号

				String orderid = submitData.getOrderid();// 订单号
				if (!"".equals(orderid)) {
					params_context.put("orderid", orderid);
				}

				// 设备信息
				params_context.put("mac", submitData.getMac());
				params_context.put("device_type", submitData.getDevice_type());
				params_context.put("device_dpi", submitData.getDevice_dpi());

				params_data.put("context", params_context);

				// matrix
				params_matrix.put("matrix_sdk_api_version", "0.0.1");
				params_matrix.put("matrix_sdk_lang", "java");
				params_matrix.put("matrix_sdk_platform", "android");
				params_matrix.put("matrix_sdk_version", "1.0.0");
				params_matrix.put("matrix_token", HeyiJoySDK.getInstance().getAppID() + "");

				params_data.put("matrix_sdk_context", params_matrix);

				params = new JSONObject(params_data.toString());

				HYHttpApi.getInstance().httpsPost(url, params.toString(), mCallBack);

			} else {
				Log.d("HeyiJoySDK", "The UUserLog is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (params_data != null) {
				params_data = null;
			}
			if (params_context != null) {
				params_context = null;
			}
			if (params_matrix != null) {
				params_matrix = null;
			}
			if (params != null) {
				params = null;
			}
		}
	}

	/*
	 * 初始化提交设备信息
	 */
	public void submitInitData(Activity activity, String url, CallBack mCallBack) {
		try {
			params_data = new JSONObject();
			params_context = new JSONObject();
			params_matrix = new JSONObject();

			params_data.put("event", "startup");
			Log.d("HeyiJoySDK", "event = startup");

			submitCommon(activity);// 上报数据，通用部分

			params_data.put("context", params_context);
			params_data.put("matrix_sdk_context", params_matrix);

			params = new JSONObject(params_data.toString());

			HYHttpApi.getInstance().httpsPost(url, params.toString(), mCallBack);

		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (params_data != null) {
				params_data = null;
			}
			if (params_context != null) {
				params_context = null;
			}
			if (params_matrix != null) {
				params_matrix = null;
			}
			if (params != null) {
				params = null;
			}
		}
	}

	List<String> mList;

	/**
	 * 自定义上报数据
	 */
	public void customData(Activity activity, String url, String dataType, String customData, CallBack mCallBack) {
		try {
			if (mList != null) {
				mList = null;
			}
			mList = new ArrayList<String>();
			mList.add("appkey");
			mList.add("channel_id");
			mList.add("fixed_time");
			mList.add("device_id");
			mList.add("android_id");
			mList.add("uuid");
			mList.add("status");
			mList.add("mac");
			mList.add("device_type");
			mList.add("device_dpi");

			params_context = new JSONObject(customData);
			params_data = new JSONObject();
			params_matrix = new JSONObject();

			params_data.put("event", dataType);

			Iterator<String> iterator = params_context.keys();// 遍历cp自定义的上报数据
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (mList.contains(key)) {
					continue;
				}
				String value = params_context.getString(key);
				params_context.put(key, value);
			}

			submitCommon(activity);// 上报数据，通用部分

			params_data.put("context", params_context);
			params_data.put("matrix_sdk_context", params_matrix);
			params = new JSONObject(params_data.toString());

			HYHttpApi.getInstance().httpsPost(url, params.toString(), mCallBack);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (params_data != null) {
				params_data = null;
			}
			if (params_context != null) {
				params_context = null;
			}
			if (params_matrix != null) {
				params_matrix = null;
			}
			if (params != null) {
				params = null;
			}
			if (mList != null) {
				mList = null;
			}
		}
	}

	/**
	 * 上报数据，通用部分
	 */
	public void submitCommon(Activity activity) {

		try {
			if (params_context == null) {
				Log.e("HeyiJoySDK", "params_context is null");
				return;
			} else {
				params_context.put("appkey", HeyiJoySDK.getInstance().getAppKey() + "");// appkey
				params_context.put("channel_id", HeyiJoySDK.getInstance().getCurrChannel() + "");// channel_id
				params_context.put("fixed_time",
						GUtils.formatDate(System.currentTimeMillis() - GUtils.getTimesTamp(activity) + ""));
				params_context.put("device_id", GUtils.getDeviceId(activity));
				params_context.put("android_id", GUtils.getAndroidId(activity));
				params_context.put("uuid", GUtils.getUuid(activity));
				params_context.put("status", "success");

				// 设备信息
				params_context.put("mac", GUtils.getMacAddress(activity));
				params_context.put("device_type", android.os.Build.MODEL);
				params_context.put("device_dpi", GUtils.getScreenDpi(activity));
				params_context.put("bundle_id", activity.getPackageName());// 包名
				params_context.put("cp_version", getPackageInfo(activity).versionCode);// 游戏版本号
				params_context.put("sdk_version", HeyiJoySDK.getInstance().getSDKVersionCode());// sdk版本号
			}

			if (params_matrix == null) {
				Log.e("HeyiJoySDK", "params_matrix is null");
				return;
			} else {
				// matrix
				params_matrix.put("matrix_sdk_api_version", "0.0.1");
				params_matrix.put("matrix_sdk_lang", "java");
				params_matrix.put("matrix_sdk_platform", "android");
				params_matrix.put("matrix_sdk_version", "1.0.0");
				params_matrix.put("matrix_token", HeyiJoySDK.getInstance().getAppID() + "");// appid
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private PackageInfo getPackageInfo(Context context) {
		PackageInfo pi = null;

		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pi;
	}
}