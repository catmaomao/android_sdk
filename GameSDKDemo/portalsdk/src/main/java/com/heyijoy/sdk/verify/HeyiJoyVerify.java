package com.heyijoy.sdk.verify;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.widget.Toast;

import com.heyijoy.sdk.log.Log;
import com.heyijoy.sdk.utils.EncryptUtils;
import com.heyijoy.sdk.utils.GUtils;
import com.heyijoy.sdk.utils.HeyiJoyHttpUtils;
import com.heyijoy.sdk.HeyiJoySDK;

public class HeyiJoyVerify {

	public static final int TIMEOUT = 30 * 1000;

	/***
	 * 访问HeyiJoyServer验证sid的合法性，同时获取HeyiJoyServer返回的token，userID,sdkUserID信息
	 * 
	 * @param result
	 * @return
	 */
	public static UToken auth(String result) {

		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("appID", HeyiJoySDK.getInstance().getAppID() + "");
			params.put("channelID", "" + HeyiJoySDK.getInstance().getCurrChannel());
			params.put("imei", GUtils.getDeviceId(HeyiJoySDK.getInstance().getContext()));
			params.put("android_id", GUtils.getAndroidId(HeyiJoySDK.getInstance().getContext()));
			params.put("uuid", GUtils.getUuid(HeyiJoySDK.getInstance().getContext()));
			params.put("extension", result);
			params.put("sdkVersionCode", HeyiJoySDK.getInstance().getSDKVersionCode());

			StringBuilder sb = new StringBuilder();
			sb.append("appID=").append(HeyiJoySDK.getInstance().getAppID() + "").append("channelID=")
					.append(HeyiJoySDK.getInstance().getCurrChannel()).append("extension=").append(result)
					.append(HeyiJoySDK.getInstance().getAppKey());

			String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

			params.put("sign", sign);

			String authResult = HeyiJoyHttpUtils.httpGet(HeyiJoySDK.getInstance().getAuthURL(), params);

			Log.d("HeyiJoySDK", "The sign is " + sign + " The auth result is " + authResult);

			return parseAuthResult(authResult);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new UToken();

	}

	private static UToken parseAuthResult(String authResult) {

		if (authResult == null || TextUtils.isEmpty(authResult)) {

			return new UToken();
		}

		try {
			JSONObject jsonObj = new JSONObject(authResult);
			int state = jsonObj.getInt("state");

			if (state != 1) {
				Log.d("HeyiJoySDK", "auth failed. the state is " + state);
				if (15 == state) {
					HeyiJoySDK.getInstance().getContext().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(HeyiJoySDK.getInstance().getContext(), "新增用户已关闭", Toast.LENGTH_SHORT).show();
						}
					});
				}
				return new UToken();
			}

			JSONObject jsonData = jsonObj.getJSONObject("data");

			return new UToken(state, jsonData.getInt("userID"), jsonData.getString("sdkUserID"),
					jsonData.getString("username"), jsonData.getString("sdkUserName"), jsonData.getString("token"),
					jsonData.getString("extension"));

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return new UToken();
	}

	public static void saveUserId(String userid) {
		SharedPreferences preferences = HeyiJoySDK.getInstance().getContext().getSharedPreferences("UFILE",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("userid", userid);
		editor.commit();
	}

	public static String getUserId() {
		SharedPreferences preferences = HeyiJoySDK.getInstance().getContext().getSharedPreferences("UFILE",
				Context.MODE_PRIVATE);
		return preferences.getString("userid", "");
	}
}
