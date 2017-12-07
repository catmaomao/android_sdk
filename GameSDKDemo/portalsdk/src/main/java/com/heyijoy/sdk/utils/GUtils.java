package com.heyijoy.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

import com.heyijoy.sdk.log.Log;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

public class GUtils {

	protected static String uuid;

	public static String getDeviceID(Context context) {
		if (uuid == null) {
			generateDeviceID(context);
		}
		return uuid;
	}

	public static void generateDeviceID(Context context) {

		if (uuid == null) {
			synchronized (GUtils.class) {
				if (uuid == null) {
					final SharedPreferences prefs = context.getSharedPreferences("g_device_id.xml", 0);
					final String id = prefs.getString("device_id", null);

					if (id != null) {
						// Use the ids previously computed and stored in the
						// prefs file
						uuid = id;
					} else {
						final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

						// Use the Android ID unless it's broken, in which case
						// fallback on deviceId,
						// unless it's not available, then fallback on a random
						// number which we store
						// to a prefs file
						try {
							if (!"9774d56d682e549c".equals(androidId)) {
								uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
							} else {
								final String deviceId = ((TelephonyManager) context
										.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
								uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString()
										: UUID.randomUUID().toString();
							}
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						}

						// Write the value out to the prefs file
						prefs.edit().putString("device_id", uuid.toString()).commit();

					}
				}
			}
		}

	}

	/**
	 * 获取android_id
	 * 
	 * @param context
	 * @return
	 */
	public static String getAndroidId(Context context) {
		String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		if (androidId == null) {
			return "";
		}
		return androidId;
	}

	/**
	 * 获取device_id
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (deviceId == null) {
			return "";
		}
		return deviceId;
	}

	/**
	 * 获取uuid
	 * 
	 * @param context
	 * @return
	 */
	public static String getUuid(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("m_uuid.xml", Context.MODE_PRIVATE);
		String uuid = prefs.getString("uuid", "");
		if ("".equals(uuid)) {
			uuid = UUID.randomUUID().toString();
			prefs.edit().putString("uuid", uuid.toString()).commit();
		}
		return uuid;
	}

	/**
	 * 获取mac地址
	 * 
	 * @param activity
	 * @return
	 */
	public static String getMacAddress(Context context) {
		WifiManager localWifiManager = (WifiManager) context.getSystemService("wifi");
		WifiInfo localWifiInfo = localWifiManager == null ? null : localWifiManager.getConnectionInfo();
		if (localWifiInfo != null) {
			String str = localWifiInfo.getMacAddress();
			if ((str == null) || (str.equals(""))) {
				str = "null";
			}
			return str;
		}
		return "null";
	}

	/**
	 * 获取屏幕分辨率
	 * 
	 * @param activity
	 * @return
	 */
	public static String getScreenDpi(Activity activity) {

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		return dm.widthPixels + "×" + dm.heightPixels;

	}

	/*
	 * 存储时间戳
	 */
	public static void saveTimesTamp(Activity context, long server_timestamp) {
		SharedPreferences preferences = context.getSharedPreferences("timestamp_file", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putLong("timestamp", server_timestamp);
		editor.commit();
	}

	/**
	 * 获取时间戳
	 * 
	 * @param context
	 * @return
	 */
	public static long getTimesTamp(Activity context) {
		SharedPreferences preferences = context.getSharedPreferences("timestamp_file", Context.MODE_PRIVATE);
		return preferences.getLong("timestamp", 0);
	}

	/**
	 * 
	 * @return true: 网络可用 ; false: 网络不可用
	 */
	public static boolean hasInternet(Context context) {
		ConnectivityManager m;
		if (context != null) {
			m = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		} else {
			m = null;
		}

		if (m == null) {
			Log.d("NetWorkState", "Unavailabel");
			return false;
		} else {
			NetworkInfo[] info = m.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						Log.d("NetWorkState", "Availabel");
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String formatDate(String s) {
		String pattern = "yyyy-MM-dd'T'HH:mm:ssZZ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		long lt = new Long(s);
		Date date = new Date(lt);
		String format = simpleDateFormat.format(date);
		return format;
	}
}
