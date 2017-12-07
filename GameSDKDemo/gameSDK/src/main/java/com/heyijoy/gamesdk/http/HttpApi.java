/**
 * HttpApi.java
 * com.heyijoy.gamesdk.http
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-2-18 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.act.HYCallBackStr;
import com.heyijoy.gamesdk.announcement.Announcement;
import com.heyijoy.gamesdk.announcement.AnnouncementCache;
import com.heyijoy.gamesdk.announcement.PopUpBean;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.BonusBean;
import com.heyijoy.gamesdk.data.MsgBean;
import com.heyijoy.gamesdk.data.PayBean;
import com.heyijoy.gamesdk.data.SourceFromGameCenterContentResover;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.data.CommitData;
import com.heyijoy.gamesdk.data.VersionInfo;
import com.heyijoy.gamesdk.data.HYPayBean;
import com.heyijoy.gamesdk.data.InitParams;
import com.heyijoy.gamesdk.http.IHttpRequest.IHttpRequestCallBack;
import com.heyijoy.gamesdk.lib.HYBuildConfig;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.EncryptUtils;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.MyParse;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.sdk.HeyiJoySDK;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * ClassName:HttpApi
 * 
 * @author msh
 * @Date 2014-2-18 下午2:17:35
 */
@SuppressLint("SimpleDateFormat")
public class HttpApi {

	private static HttpApi httpApi = null;

	private HttpApi() {

	}

	public static synchronized HttpApi getInstance() {
		if (httpApi == null) {
			httpApi = new HttpApi();
		}
		return httpApi;
	}

	/**
	 * status为failed,请求返回失败
	 */
	public void statusFailed(JSONObject json, HYCallBack callbackRe) {
		String desc;
		try {
			desc = json.getString("desc");
			int code = json.getInt("code");
			callbackRe.onFailed(code, desc);
			Log.e(HYConstant.TAG, "status = failed" + ",code=" + code + ",desc=" + desc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异常失败
	 */
	public void exceptionFailed(int code, String desc, HYCallBack callbackRe) {
		callbackRe.onFailed(code, desc);
		Log.e("HY_SDK", "status = exception_failed" + ",code=" + code + ",desc=" + desc);
	}

	/**
	 * 初始化统计
	 */
	public void initAnalytics(final InitParams params, final HYCallBack callback) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.INIT_ANALYTICS, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("is_Offline", String.valueOf(params.getIs_Offline()));
		httpIntent.putParams("sdk_version_code", String.valueOf(params.getSdk_version_code()));
		httpIntent.putParams("channel_id", String.valueOf(params.getChannel_id()));
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());

		httpIntent.putParams("version_code", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callback);
					} else {

						callback.onSuccess(null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "初始化统计失败", callback);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callback);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * 上报数据
	 */
	public void submitDataAnalytics(final CommitData extraData, final HYCallBack callback) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.COMMIT_ANALYTICS, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("dataType", String.valueOf(extraData.getDataType()));
		httpIntent.putParams("serverID", extraData.getServerID());
		httpIntent.putParams("serverName", extraData.getServerName());
		httpIntent.putParams("roleID", extraData.getRoleID());
		httpIntent.putParams("roleName", extraData.getRoleName());
		httpIntent.putParams("roleLevel", extraData.getRoleLevel());
		httpIntent.putParams("moneyNum", extraData.getMoneyNum());
		httpIntent.putParams("roleCreateTime", extraData.getRoleCreateTime() + "");
		httpIntent.putParams("roleLevelUpTime", extraData.getRoleLevelUpTime() + "");

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callback);
					} else {

						callback.onSuccess(null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "上报数据失败", callback);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callback);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * login
	 */
	public void yklogin(final User user, final HYCallBack callbackRe) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_LOGIN, HttpRequestManager.METHOD_POST);
		String thirdparty = user.getThirdparty();
		boolean auto = user.isAuto();
		String lifeCode = GameSDKApplication.getInstance().getLifeCode();
		String thridparty = GameSDKApplication.getInstance().getUserLoginType();
		if (auto) {// 自动登录
			user.setAuto(false);
			// 1,三方自动登录,游客绑定三方后，使用三方方式登录
			if (!TextUtils.isEmpty(thridparty)) {
				httpIntent.putParams("thirdparty", thirdparty);
				httpIntent.putParams("lifecode", lifeCode);
				httpIntent.putParams("password", "BBB");
			} else {
				// 2，非三方
				httpIntent.putParams("password", user.getPassword());
			}

		} else {// 非自动登录
			// 1,三方登录
			if (!TextUtils.isEmpty(user.getThirdparty())) {
				httpIntent.putParams("password", user.getPassword());
				httpIntent.putParams("thirdparty", user.getThirdparty());
				httpIntent.putParams("thirdparty_credential", user.getThirdparty_credential());
			} else {
				// 2，非三方
				httpIntent.putParams("password", user.getPassword());
			}
		}

		httpIntent.putParams("username", user.getUserName());
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());

		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("version_code", GameSDKApplication.getInstance().getVersionCode() + "");
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				// Log.e("HeyiJoySDK", result);
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callbackRe);
					} else {
						boolean isBind = json.getBoolean("isbind");// 指手机号
						String userName = json.getString("username");
						if (isBind) {
							String phone = json.getString("phone");
							GameSDKApplication.getInstance().savePhonePre(phone);// 用户判断个人中心用户是否绑定过手机号
							user.setPhone(phone);
							GameSDKApplication.getInstance().setIsbind(user.getUserName());
							GameSDKApplication.getInstance().setIsneedbind(false);
						} else {
							user.setPhone("");
							GameSDKApplication.getInstance().setIsneedbind(true);
						}
						user.setUserName(userName);
						user.setSession(json.getString("sessionid"));
						user.setUid(json.getString("userid"));
						user.setCertification(json.getBoolean("authenticate"));
						user.setLifecode(json.getString("lifecode"));
						user.setBind3rd(json.getString("bind3rd"));
						String thirdpartyuserinfo = json.getString("thirdpartyuserinfo");
						if (!TextUtils.isEmpty(thirdpartyuserinfo)) {
							JSONObject jsonObject = new JSONObject(thirdpartyuserinfo);
							String nickname = jsonObject.getString("nickname");
							String headimgurl = jsonObject.getString("headimgurl");
							user.setThirdUsername(nickname);
							user.setThirdHeadImgUrl(headimgurl);
						}
						// ispay 兼容1.1.1之前版本（含）
						Iterator keys = json.keys();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							if ("ispay".equals(key)) {
								user.setIsPay(json.getBoolean("ispay"));
							}
						}
						GameSDKApplication.getInstance().setShareUser(user);
						GameSDKApplication.getInstance().saveUserCertifitationStatus(json.getBoolean("authenticate"));
						callbackRe.onSuccess(user);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "登录失败", callbackRe);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callbackRe);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * 绑定三方账号
	 */
	public void bindapi(final User user, final HYCallBack callbackRe) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_BINDTHIRDPARTY, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("thirdparty_credential", user.getThirdparty_credential());
		httpIntent.putParams("thirdparty", user.getThirdparty());
		httpIntent.putParams("password", GameSDKApplication.getInstance().getUserPwd());
		httpIntent.putParams("username", GameSDKApplication.getInstance().getUserName());
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("version_code", GameSDKApplication.getInstance().getVersionCode() + "");
		// Log.e("HeyiJoySDK", "username=" +
		// GameSDKApplication.getInstance().getUserName() + "&pwd="
		// + GameSDKApplication.getInstance().getUserPwd());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				// Log.e("HeyiJoySDK", "bindresult = " + result);
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callbackRe);
					} else {
						callbackRe.onSuccess(user);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "绑定失败", callbackRe);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callbackRe);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * 修改密码
	 */
	public void modifyPwd(final User user, final HYCallBack callbackRe) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_MODIFY_PWD, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		httpIntent.putParams("newpassword", user.getNew_password());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("password", user.getPassword());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("userid", GameSDKApplication.getInstance().getShareUser().getUid());
		httpIntent.putParams("username", user.getUserName());
		httpIntent.putParams("version_code", GameSDKApplication.getInstance().getVersionCode() + "");
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callbackRe);
					} else {
						boolean isBind = json.getBoolean("isbind");
						if (isBind) {
							GameSDKApplication.getInstance().setIsbind(user.getUserName());
						} else {
							GameSDKApplication.getInstance().setIsneedbind(true);
						}
						user.setSession(json.getString("sessionid"));
						user.setUid(json.getString("userid"));
						GameSDKApplication.getInstance().setShareUser(user);
						callbackRe.onSuccess(user);
					}
				} catch (JSONException e) {
					exceptionFailed(HYConstant.EXCEPTION_CODE, "密码修改失败", callbackRe);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callbackRe);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * 实名认证
	 */
	public void apiCertification(String identifyName, String identifyCertification, final HYCallBack callbackRe) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_CERTIFICATION, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("auth_name", identifyName);
		httpIntent.putParams("auth_idcard", identifyCertification);
		httpIntent.putParams("userid", GameSDKApplication.getInstance().getShareUser().getUid());
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("version_code", GameSDKApplication.getInstance().getVersionCode() + "");
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callbackRe);
					} else {
						callbackRe.onSuccess(new Bean());
					}
				} catch (JSONException e) {
					exceptionFailed(HYConstant.EXCEPTION_CODE, "认证失败", callbackRe);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callbackRe);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * 根据cookie进行登录
	 */
	public void ykloginByCookie(final User user, final HYCallBack callbackRe) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_LOGIN_COOKIE, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("account", URLEncoder.encode(user.getUserName()));
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		// httpIntent.putParams("phone", user.getUserName());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callbackRe);
					} else {
						user.setUid(json.getString("uid"));
						boolean isBind = json.getBoolean("isbind");
						if (isBind) {
							GameSDKApplication.getInstance().setIsbind(user.getUserName());
							GameSDKApplication.getInstance().setIsneedbind(false);
						} else {
							GameSDKApplication.getInstance().setIsneedbind(true);
						}
						user.setSession(json.getString("sessionid"));
						GameSDKApplication.getInstance().setShareUser(user);
						callbackRe.onSuccess(user);
					}
				} catch (JSONException e) {
					exceptionFailed(HYConstant.EXCEPTION_CODE, "登录失败", callbackRe);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callbackRe);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * 根据验证码登录
	 */
	public void loginByVerifyNo(final User user, String verifyNo, final HYCallBack recallback) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_LOGIN_BYVERIFYNO, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("phone", user.getUserName());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("verify_code", verifyNo);

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, recallback);
					} else {
						user.setSession(json.getString("sessionid"));
						user.setUid(json.getString("uid"));
						GameSDKApplication.getInstance().setShareUser(user);
						recallback.onSuccess(user);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, recallback);
			}
		};
		httpRequest.request(httpIntent, callBack);
	}

	/**
	 * 请求注册的验证码 输入手机号注册
	 */
	public void requestVerifyNo(String phoneNO, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_REG_GETVERINO, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("phone", phoneNO);
		httpIntent.putParams("sdkver", HYConstant.getSDKVersion());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());

		HttpRequestManager httpRequest = new HttpRequestManager();

		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				JSONObject json;
				try {
					json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						callBack.onSuccess(null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "请求失败", callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};

		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 获取绑定手机号的验证码
	 * 
	 * @param phone
	 * @param callBack
	 */
	public void requestBindingVerifyNo(String phone, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.BIND_PHONE_CODE, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("phone", phone);
		httpIntent.putParams("sdkver", HYConstant.getSDKVersion());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("userid", GameSDKApplication.getInstance().getUserId());
		httpIntent.putParams("username", GameSDKApplication.getInstance().getUserName());
		httpIntent.putParams("version_code", GameSDKApplication.getInstance().getVersionCode() + "");

		HttpRequestManager httpRequest = new HttpRequestManager();

		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				JSONObject json;
				try {
					json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						callBack.onSuccess(null);
					}
				} catch (JSONException e) {
					exceptionFailed(HYConstant.EXCEPTION_CODE, "请求失败", callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};

		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 绑定手机号接口
	 */
	public void bindingPhone(final String phone, String verifycode, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.BIND_PHONE, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		httpIntent.putParams("phone", phone);
		httpIntent.putParams("userid", GameSDKApplication.getInstance().getUserId());
		httpIntent.putParams("username", GameSDKApplication.getInstance().getUserName());
		httpIntent.putParams("verifycode", verifycode);

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						GameSDKApplication.getInstance().savePhonePre(phone);// 存储手机号
						callBack.onSuccess(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
					// callBack.onFailed(null);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * Regester 输入手机号注册 返回注册用户id
	 */
	public void regester(String verifyNo, final String mobile, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_REG, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("phone", mobile);
		httpIntent.putParams("phone_verify", verifyNo);
		httpIntent.putParams("sdkver", HYConstant.getSDKVersion());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());

		HttpRequestManager httpRequest = new HttpRequestManager();

		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						User user = new User();
						String uid = json.getString("uid");
						String session = json.getString("sessionid");
						String isNewUser = json.getString("isNew");
						if ("0".equals(isNewUser)) {// 老用户
							user.setIsNewUser("NO");
						} else if ("1".equals(isNewUser)) {// 新用户
							user.setIsNewUser("YES");
						}
						user.setSession(session);
						user.setUserName(mobile);
						user.setUid(uid);
						GameSDKApplication.getInstance().setShareUser(user);
						callBack.onSuccess(user);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					// callBack.onFailed(null);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};

		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 重置密码
	 * 
	 * @param phone
	 * @param notifyNO
	 * @param pwd
	 * @param callBack
	 */
	public void retrievePwd(final String phone, String verifycode, final String newpassword,
			final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_FORGET_PWD, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("newpassword", newpassword);
		httpIntent.putParams("phone", phone);
		httpIntent.putParams("verifycode", verifycode);
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						callBack.onSuccess(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
					callBack.onFailed(HYConstant.EXCEPTION_CODE, e.getMessage());
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 获取任务列表
	 */
	public void requestTask(final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_TASK_LIST, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("platform", "2");
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				MyParse.parseTask(result, callBack);
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};

		httpRequest.request(httpIntent, callBack1, "1");
	}

	/**
	 * 获取任务积分
	 */
	public void requestTaskBonus(final HYCallBack callBack, String taskId, String status) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_TASK_BONUS, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("platform", "2");
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("status", status);
		httpIntent.putParams("task_id", taskId);
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				Logger.d("pack&ver", "jsonData-Bonus=" + result);
				BonusBean bonusBean = new BonusBean();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						String desc = json.getString("desc");
						bonusBean.setStatus(status);
						bonusBean.setDesc(desc);
					} else {
						String bonus = json.getString("bonus");
						bonusBean.setStatus(status);
						bonusBean.setBonus(bonus);
					}
					callBack.onSuccess(bonusBean);
				} catch (Exception e) {
					e.printStackTrace();
					Logger.d("pack&ver", "jsonData-Bonus=getTaskBonus-error");
					exceptionFailed(HYConstant.EXCEPTION_CODE, "getTaskBonus-error", callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};

		httpRequest.request(httpIntent, callBack1, "1");
	}

	/**
	 * 从后台取版本
	 */
	public void getRemoteVersion(final HYCallBack callBack) {
		HttpRequestManager httpRequestManager = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			private String force_update;

			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String resultStr = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(resultStr);
					VersionInfo versionInfo = new VersionInfo();
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
						allFalse();
					} else {
						// 校准时间 初始化上下行短信地址
						long timeDiff = json.getLong("server_time");
						String upMSGMobile = json.getString("up_sms");
						String downMSGMobile = json.getString("down_sms");
						String payType = json.getString("pay_type");
						String probability = json.getString("probability");
						if (probability == null || "".equals(probability)) {
							probability = "0";
						}

						GameSDKApplication.getInstance().setPaySwitch(payType);// 支付方式

						boolean isPhoneRegOpen = json.getBoolean("phone_register");
						boolean isNameRegOpen = json.getBoolean("name_register");

						GameSDKApplication.getInstance().setPhoneRegOpen(isPhoneRegOpen);
						GameSDKApplication.getInstance().setNameRegOpen(isNameRegOpen);

						GameSDKApplication.getInstance().setShowBindingDlgProbability(Integer.parseInt(probability));
						if (upMSGMobile != null && (!"".equals(upMSGMobile)) && downMSGMobile != null
								&& (!"".equals(downMSGMobile))) {
							HYConstant.REG_MOBILE_UP_NO = upMSGMobile;
							String[] mobiles = downMSGMobile.split(",");
							for (int i = 0; i < mobiles.length; i++) {
								HYConstant.REG_MOBILE_RECEIVE_LIST.add(mobiles[i]);
							}
						}

						GameSDKApplication.getInstance().initTimeDiff(timeDiff);
						if (json.getBoolean("is_update")) {// app升级更新
							Long size = json.getLong("app_size");
							versionInfo.setRemoteSize(size);
							force_update = json.getString("force_update");
							if ("0".equals(json.getString("force_update"))) {
								versionInfo.setUpdateFlag("can");// 可以
							} else {
								versionInfo.setUpdateFlag("must");// 强制
							}
						} else {
							versionInfo.setUpdateFlag("no");
						}
						versionInfo.setVersionCode(json.getString("version_code"));
						versionInfo.setVersionName(json.getString("version_name"));
						versionInfo.setVersionDetail(json.getString("latest_version_desc"));
						versionInfo.setUpdateUrl(json.getString("download_link"));
						String auth = json.getString("authenticate");// 实名认证开关
						String auth_pay = json.getString("authenticate_pay");// 未实名认证的支付开关

						GameSDKApplication.getInstance().saveCertifitationStatus(auth, auth_pay);// 保存实名认证开关状态到本地

						callBack.onSuccess(versionInfo);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				allFalse();
				exceptionFailed(code, failReason, callBack);
			}
		};

		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_INIT);

		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("network", Util.netWorkType());
		httpIntent.putParams("version_code", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("bundle_id", GameSDKApplication.getInstance().getPageName() + "");
		httpRequestManager.request(httpIntent, callBack1);
	}

	/**
	 * 微信登录获取access_token
	 * 
	 * @param callBack
	 */
	public void getWXAccessToken(String url, final HYCallBackStr callBack) {
		HttpRequestManager httpRequestManager = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {

			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String resultStr = httpRequestManager.getDataString();
				callBack.callback(resultStr);
			}

			@Override
			public void onFailed(int code, String failReason) {
			}
		};
		httpRequestManager.requestWX(url, callBack1);
	}

	/**
	 * 获取微信个人信息
	 * 
	 * @param url
	 * @param callBack
	 */
	public void getWXInfo(String url, final HYCallBackStr callBack) {
		HttpRequestManager httpRequestManager = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {

			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String resultStr = httpRequestManager.getDataString();
				callBack.callback(resultStr);
			}

			@Override
			public void onFailed(int code, String failReason) {
			}
		};
		httpRequestManager.requestWX(url, callBack1);
	}

	public void requestFloatOrder(final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_FLOAT_ORDER, HttpRequestManager.METHOD_GET);
		httpIntent.putParams("ver_code", HYConstant.getSDKVersion());
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());

		HttpRequestManager httpRequest = new HttpRequestManager();

		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				MyParse.parseFloatOrder(result, callBack);
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};

		httpRequest.request(httpIntent, callBack1);
	}

	public void allFalse() {
		GameSDKApplication.getInstance().setPhoneRegOpen(false);
		GameSDKApplication.getInstance().setNameRegOpen(false);
	}

	/**
	 * 重置密码 请求验证码
	 */
	public void requestPwdVerifyNo(String phone, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.VERIFYCODE_RESETPASSWORD, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("phone", phone);
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						callBack.onSuccess(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * appOrderID唯一性查询
	 * 
	 * @param apporderID
	 * @param callBack
	 */
	public void checkAppOrderID(String apporderID, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_CHECK_APPORDERID, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("app_order_id", apporderID);

		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("success".equals(status)) {
						// if(json.has("inpourcard_info")){
						// String inpourcard_info =
						// json.getString("inpourcard_info");
						// GameSDKApplication.getInstance().saveInpourcardInfo(inpourcard_info);
						// }
						// if(json.has("inpourcard_desc")){
						// String inpourcard_desc =
						// json.getString("inpourcard_desc");
						// GameSDKApplication.getInstance().saveInpourcardDesc(inpourcard_desc);
						// }
						// if(json.has("pay_types")){
						// String pay_types = json.getString("pay_types");
						// GameSDKApplication.getInstance().setPaySwitch(pay_types);
						// }
						PayBean payBean = new PayBean();
						payBean.setParams(result);
						callBack.onSuccess(payBean);
					} else {
						statusFailed(json, callBack);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "重复订单核查解析错误", callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 手机号注册
	 */
	public void phoneRegisterApi(final String type, final String nickName, final String password,
			final String newPassword, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_REG_NICKNAME, HttpRequestManager.METHOD_POST);
		if ("quick".equals(type)) {
			httpIntent.putParams("register_way", String.valueOf(1));
		}
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("username", nickName);
		httpIntent.putParams("password", password);
		httpIntent.putParams("newpassword", newPassword);
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		httpIntent.putParams("sdkver", HYConstant.getSDKVersion());

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						User user = new User();
						String uid = json.getString("userid");
						String session = json.getString("sessionid");
						String nickName = json.getString("username");
						String isNewUser = json.getString("isNew");
						// 用户名注册的情况下 需要绑定
						GameSDKApplication.getInstance().setIsneedbind(true);
						if ("0".equals(isNewUser)) {// 老用户
							user.setIsNewUser("NO");
						} else if ("1".equals(isNewUser)) {// 新用户
							user.setIsNewUser("YES");
						}
						user.setSession(session);
						user.setUid(uid);
						user.setUserName(nickName);
						if ("quick".equals(type)) {
							user.setPassword(json.getString("password"));
							// Log.e("HeyiJoySDK",
							// "quick="+json.getString("password"));
						} else {
							// 正常注册没有返回密码
							user.setPassword(password);
						}
						GameSDKApplication.getInstance().setShareUser(user);
						callBack.onSuccess(user);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 用户名注册接口，包括快速登录（走的是注册接口）
	 */
	public void regByName(final String type, final String nickName, final String password, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_REG_NICKNAME, HttpRequestManager.METHOD_POST);
		if ("quick".equals(type)) {
			httpIntent.putParams("register_way", String.valueOf(1));
			httpIntent.putParams("udid", GameSDKApplication.getInstance().getUserDeviceId());
			// Log.e("HeyiJoySDK", "BLOCK_DEVICE_ID=" +
			// GameSDKApplication.getInstance().getUserDeviceId());
			// Log.e("HeyiJoySDK", "BLOCK_USERNAME=" +
			// GameSDKApplication.getInstance().getUserName());
			// Log.e("HeyiJoySDK", "BLOCK_PASSWORD=" +
			// GameSDKApplication.getInstance().getUserPwd());
		}
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("password", password);
		httpIntent.putParams("sdkver", HYConstant.getSDKVersion());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("username", nickName);

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				// Log.e("HeyiJoySDK", "quick=" + result);
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						User user = new User();
						String uid = json.getString("userid");
						String session = json.getString("sessionid");
						String nickName = json.getString("username");
						String isNewUser = json.getString("isNew");
						// 用户名注册的情况下 需要绑定
						GameSDKApplication.getInstance().setIsneedbind(true);
						if ("0".equals(isNewUser)) {// 老用户
							user.setIsNewUser("NO");
						} else if ("1".equals(isNewUser)) {// 新用户
							user.setIsNewUser("YES");
						}
						user.setSession(session);
						user.setUid(uid);
						user.setUserName(nickName);
						if ("quick".equals(type)) {
							user.setPassword(Util.md5(json.getString("password")));
							// Log.e("HeyiJoySDK", "注册MD5_PASSWORD=" +
							// Util.md5(json.getString("password")));
							user.setLifecode(json.getString("lifecode"));
							user.setPicPwd(json.getString("password"));// 存相册使用，明文
						} else {
							// 正常注册没有返回密码
							user.setPassword(password);
							user.setLifecode(json.getString("lifecode"));
						}
						GameSDKApplication.getInstance().setShareUser(user);
						callBack.onSuccess(user);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 获取活动
	 */
	public void requestAnnouncement() {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_ANNOUNCEMENT, HttpRequestManager.METHOD_GET);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("sdkver", HYConstant.getSDKVersion());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject jsonResult = new JSONObject(result);
					String status = jsonResult.getString("status");
					if ("failed".equals(status)) {
					} else {
						JSONArray jsonArr = jsonResult.getJSONArray("activities");
						List<Announcement> announcements = new ArrayList<Announcement>();
						AnnouncementCache announcementCache = AnnouncementCache.getInstance();
						if (jsonArr.length() > 0) {// 有活动
							JSONObject json0 = (JSONObject) jsonArr.get(0);
							announcementCache.setImgadd(json0.getString("image"));
							announcementCache.setImgurl(json0.getString("image_link"));
							announcementCache.setId(json0.getString("id"));
							announcementCache.setTitle(jsonResult.getString("title"));
							if (json0.has("image_pack_id")) {
								announcementCache.setIntentId(json0.getString("image_pack_id"));
							}
							try {
								announcementCache.setSkipType(json0.getString("image_skip_type"));
							} catch (Exception e) {
							}

							LoadImageAsyncTask imageLoadTask = new LoadImageAsyncTask(announcementCache);
							imageLoadTask.execute();
						}

						for (int j = 0; j < jsonArr.length(); j++) {
							Announcement announcement = new Announcement();
							JSONObject json = (JSONObject) jsonArr.get(j);
							String seq = json.getString("seq");
							announcement.setSkipType(json.getString("skip_type"));
							announcement.setContent(json.getString("content"));
							announcement.setUrlAdd(json.getString("link"));
							announcement.setUrlContent(json.getString("link_text"));
							announcement.setId(json.getString("id"));
							announcement.setTitle(json.getString("title"));
							if (json.has("pack_id")) {
								announcement.setIntentId(json.getString("pack_id"));
							}

							announcements.add(announcement);
						}
						announcementCache.setAnnouncements(announcements);

						JSONObject jsonPopUp = jsonResult.getJSONObject("popup");
						PopUpBean popBean = PopUpBean.getInstance();
						popBean.setId(jsonPopUp.getString("id"));
						popBean.setImageadd(jsonPopUp.getString("image"));
						popBean.setImageurl(jsonPopUp.getString("image_link"));
						popBean.setVideo_link(jsonPopUp.getString("video_link"));
						popBean.setVideo_vid(jsonPopUp.getString("video_vid"));
						popBean.setSkipType(jsonPopUp.getString("skip_type"));
						if (jsonPopUp.has("pack_id")) {
							popBean.setIntentId(jsonPopUp.getString("pack_id"));
						}
						LoadImageAsyncTask quitimageLoadTask = new LoadImageAsyncTask(popBean);
						quitimageLoadTask.execute();
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 询问是否有福利
	 */
	public static void requestBenefit(final HYCallBackStr callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_BENIFIT, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("sdkver", HYConstant.getSDKVersion());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				callBack.callback(result);
			}

			@Override
			public void onFailed(int code, String failReason) {
				callBack.callback(null);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 请求短信支付接口
	 * 
	 * @param amount
	 * @param orderID
	 * @param callBackUrl
	 * @param callBack
	 */
	public void requestSMSPay(HYPayBean ykPayBean, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_PAY_SMS, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("amount", ykPayBean.getAmount());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("apporderID", ykPayBean.getAppOrderId());
		httpIntent.putParams("gameCallbackUrl", ykPayBean.getNotifyUri());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("good_name", URLEncoder.encode(ykPayBean.getProductName()));
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																							// ID，1:移动
																							// 2:联通
																							// 3:电信
		if (ykPayBean.getAppExt1() != null && !ykPayBean.getAppExt1().equals(""))
			httpIntent.putParams("passthrough", ykPayBean.getAppExt1());// 透传参数
		httpIntent.putParams("pay_status", "1");// 1:能够计费 0:不能计费 2:能够部分计费
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						String orderid = json.getString("orderid");
						PayBean payBean = new PayBean();
						payBean.setParams(orderid);
						callBack.onSuccess(payBean);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 请求会员礼包接口
	 * 
	 * @param ykPayBean
	 * @param callBack
	 */
	public void requestIsVip(final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_BUY_VIP, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
			httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																								// ID，1:移动
																								// 2:联通
																								// 3:电信
		}
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				MyParse.parseVipMsg(result, callBack);
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 请求会员礼包码列表接口
	 * 
	 * @param ykPayBean
	 * @param callBack
	 */
	public void requestVipCodes(final HYCallBack callBack, String pageNum) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_VIPCODE_LIST, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
			httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																								// ID，1:移动
																								// 2:联通
																								// 3:电信
		}
		httpIntent.putParams("page_num", pageNum);
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				JSONObject json;
				try {
					json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						PayBean payBean = new PayBean();
						payBean.setParams(result);
						callBack.onSuccess(payBean);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 请求支付接口
	 * 
	 * @param amount
	 * @param orderID
	 * @param callBackUrl
	 * @param callBack
	 */
	public void requestPay(String payChannel, HYPayBean ykPayBean, final HYCallBack callBack) {
		HttpIntent httpIntent;
		if (GameSDKApplication.getInstance().isStandAlone()) {
			httpIntent = new HttpIntent(HYConstant.URL_PAY_STANDALONE, HttpRequestManager.METHOD_POST);// 单机模式的dopay接口url
		} else {
			httpIntent = new HttpIntent(HYConstant.URL_PAY, HttpRequestManager.METHOD_POST);// 网络模式的dopay接口url
		}

		if (HYConstant.CHANNEL_WXPAY.equals(payChannel)) {
			httpIntent.putParams("wechat_appid", HYConstant.WXAPPID);
		}
		httpIntent.putParams("good_name", URLEncoder.encode(ykPayBean.getProductName()));
		httpIntent.putParams("amount", ykPayBean.getAmount());
		httpIntent.putParams("appid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("app_order_id", ykPayBean.getAppOrderId());
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("game_callback_url", ykPayBean.getNotifyUri());

		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		if (ykPayBean.getAppExt1() != null && !ykPayBean.getAppExt1().equals("")) {
			httpIntent.putParams("passthrough", URLEncoder.encode(ykPayBean.getAppExt1()));// 透传参数
		}
		httpIntent.putParams("pay_channel", payChannel);
		httpIntent.putParams("sdk_version", HYConstant.getSDKVersion());
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		if (GameSDKApplication.getInstance().isStandAlone()) {
			httpIntent.putParams("userid", "999999999");
		} else {
			httpIntent.putParams("userid", GameSDKApplication.getInstance().getShareUser().getUid());
		}
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						PayBean payBean = new PayBean();
						payBean.setParams(result);
						callBack.onSuccess(payBean);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	public void askRechargeGold(String payChannel, String inpourAmount, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_PAY_INPOUR, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("first_pay_mark", "0");// 0为只充值不消费
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("inpour_amount", inpourAmount);
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("pay_channel", payChannel);
		httpIntent.putParams("sdk_version", HYConstant.VERSION_ALIPAY);// 现在使用的支付宝版本号
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {

			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("failed".equals(status)) {
						statusFailed(json, callBack);
					} else {
						PayBean payBean = new PayBean();
						payBean.setParams(result);
						callBack.onSuccess(payBean);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * @param payChannel
	 * @param inpourAmount
	 * @param cardCode
	 * @param cardNo
	 * @param cardOperator
	 * @param cardPwd
	 * @param amount
	 * @param orderID
	 * @param callBackUrl
	 * @param callBack
	 */
	public void requestOperatorPay(String payChannel, String inpourAmount, String isRecharge, String cardCode,
			String cardNo, String cardOperator, String cardPwd, HYPayBean ykPayBean, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_PAY_INPOUR, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("amount", ykPayBean.getAmount());
		httpIntent.putParams("apporderID", ykPayBean.getAppOrderId());
		httpIntent.putParams("card_code", cardCode);
		httpIntent.putParams("card_no", cardNo);
		httpIntent.putParams("card_operator", cardOperator);
		httpIntent.putParams("card_pwd", cardPwd);
		httpIntent.putParams("first_pay_mark", isRecharge);// 1为充值后再消费
		httpIntent.putParams("gameCallbackUrl", ykPayBean.getNotifyUri());
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("good_name", URLEncoder.encode(ykPayBean.getProductName()));
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("inpour_amount", inpourAmount);
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		if (ykPayBean.getAppExt1() != null && !ykPayBean.getAppExt1().equals(""))
			httpIntent.putParams("passthrough", ykPayBean.getAppExt1());// 透传参数
		httpIntent.putParams("pay_channel", payChannel);
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String data = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(data);
					String result = json.getString("result");
					JSONObject resultjson = new JSONObject(result);
					String channelError = resultjson.getString("channel_error");
					String channelInfo = resultjson.getString("channel_info");
					if ("2001".equals(channelError)) {
						PayBean payBean = new PayBean();
						payBean.setParams(data);
						callBack.onSuccess(payBean);
					} else {
						// callBack.onFailed(channelInfo);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * @param payChannel
	 * @param inpourAmount
	 * @param cardCode
	 * @param cardNo
	 * @param cardOperator
	 * @param cardPwd
	 * @param callBack
	 */
	public void requestOperatorRecharge(String payChannel, String inpourAmount, String isRecharge, String cardCode,
			String cardNo, String cardOperator, String cardPwd, final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_PAY_INPOUR, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("card_code", cardCode);
		httpIntent.putParams("card_no", cardNo);
		httpIntent.putParams("card_operator", cardOperator);
		httpIntent.putParams("card_pwd", cardPwd);
		httpIntent.putParams("first_pay_mark", isRecharge);// 1为充值后再消费
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("inpour_amount", inpourAmount);
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("pay_channel", payChannel);
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String data = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(data);
					String result = json.getString("result");
					JSONObject resultjson = new JSONObject(result);
					String channelError = resultjson.getString("channel_error");
					String channelInfo = resultjson.getString("channel_info");
					if ("2001".equals(channelError)) {
						PayBean payBean = new PayBean();
						payBean.setParams(data);
						callBack.onSuccess(payBean);
					} else {
						// callBack.onFailed(channelInfo);
					}
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, e.getMessage(), callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 查询优豆余额
	 * 
	 * @param callBack
	 */
	public void checkBalance(final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_CHECK_BALANCE, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("gameid", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																							// ID，1:移动
																							// 2:联通
																							// 3:电信
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					JSONObject json = new JSONObject(result);
					String balance = json.getString("balance");
					PayBean payBean = new PayBean();
					payBean.setParams(balance);
					callBack.onSuccess(payBean);
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "请重试", callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	// 查询积分任务
	public void checkTask(final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_CHECK_TASK, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("platform", "2");
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					PayBean payBean = new PayBean();
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if ("success".equals(status)) {
						payBean.setParams(json.getString("point"));
					} else {
						payBean.setParams(json.getString("0"));
					}
					callBack.onSuccess(payBean);
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "请重试", callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 查询历史订单
	 * 
	 * @param callBack
	 * @param status
	 * @param pgStart
	 * @param pgEnd
	 */
	public void getPayDetail(final HYCallBack callBack, String url, String pgStart, String pgEnd) {
		HttpIntent httpIntent = new HttpIntent(url, HttpRequestManager.METHOD_GET);
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																							// ID，1:移动
																							// 2:联通
																							// 3:电信
		httpIntent.putParams("pg_start", pgStart);// 从多少条开始取
		httpIntent.putParams("pg_end", pgEnd);// 取到多少条结束
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("status", "0");// 状态码：0:全部 2:成功 3:失败
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				try {
					PayBean payBean = new PayBean();
					payBean.setParams(result);
					callBack.onSuccess(payBean);
				} catch (Exception e) {
					e.printStackTrace();
					exceptionFailed(HYConstant.EXCEPTION_CODE, "请重试", callBack);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 获取礼包信息
	 * 
	 * @param callBack
	 */
	public void getPackages(final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_GET_PACKAGES, HttpRequestManager.METHOD_GET);
		httpIntent.putParams("app_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																							// ID，1:移动
																							// 2:联通
																							// 3:电信
		httpIntent.putParams("pg", "1");
		httpIntent.putParams("pz", "1");
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				PayBean payBean = new PayBean();
				payBean.setParams(result);
				callBack.onSuccess(payBean);
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 获取消费福利档位信息
	 * 
	 * @param callBack
	 */
	public void getConsumeBenefitsMsg(final HYCallBack callBack) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_GET_CONSUME_BENEFITS_MSG, HttpRequestManager.METHOD_GET);
		httpIntent.putParams("app_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																							// ID，1:移动
																							// 2:联通
																							// 3:电信
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				PayBean bean = new PayBean();
				bean.setParams(result);
				callBack.onSuccess(bean);
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 获取消费福利礼品信息
	 * 
	 * @param callBack
	 */
	public void getConsumeBenefitsPresent(final HYCallBack callBack, String amount) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_GET_CONSUME_BENEFITS_PRESENTS,
				HttpRequestManager.METHOD_GET);
		httpIntent.putParams("amount", amount);
		httpIntent.putParams("app_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("opid", GameSDKApplication.getInstance().getProvidersCode());// 运营商
																							// ID，1:移动
																							// 2:联通
																							// 3:电信
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				PayBean bean = new PayBean();
				bean.setParams(result);
				callBack.onSuccess(bean);
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 领取某个礼品提交个人信息
	 * 
	 * @param callBack
	 * @param gift_id
	 *            礼品id
	 * @param userName
	 *            用户输入的姓名
	 * @param phone
	 *            用户输入的电话号码
	 */

	public void submitPersonnelMsgForPresent(final HYCallBack callBack, String gift_id, String userName, String phone) {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_SUBMIT_CONSUME_BENEFITS_PERSONNEL_MSG,
				HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("game_id", GameSDKApplication.getInstance().getAppid());
		httpIntent.putParams("gamever", GameSDKApplication.getInstance().getVersionCode() + "");
		httpIntent.putParams("gift_id", gift_id);
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		if (!"".equals(GameSDKApplication.getInstance().getImei())) {
			httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());
		}
		httpIntent.putParams("mac", GameSDKApplication.getInstance().getMac());
		httpIntent.putParams("network", Util.netWorkType());
		if (!"".equals(GameSDKApplication.getInstance().getProvidersName())) {
			httpIntent.putParams("operator", GameSDKApplication.getInstance().getProvidersName());
		}
		httpIntent.putParams("phone", phone);// 用户输入的电话号码
		httpIntent.putParams("sguid", GameSDKApplication.getInstance().getSGuid());
		httpIntent.putParams("uid", GameSDKApplication.getInstance().getShareUser().getUid());
		httpIntent.putParams("user_name", URLEncoder.encode(userName));// 用户输入的姓名

		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				PayBean bean = new PayBean();
				bean.setParams(result);
				callBack.onSuccess(bean);
			}

			@Override
			public void onFailed(int code, String failReason) {
				exceptionFailed(code, failReason, callBack);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

	/**
	 * 获取access_token
	 * 
	 * @param callBack
	 */
	public void getAccessToken() {
		HttpIntent httpIntent = new HttpIntent(HYConstant.URL_GET_ACCESS_TOKEN, HttpRequestManager.METHOD_POST);
		httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());
		httpIntent.putParams("guid", GameSDKApplication.getInstance().getGuid());
		HttpRequestManager httpRequest = new HttpRequestManager();
		IHttpRequestCallBack callBack1 = new IHttpRequestCallBack() {
			@Override
			public void onSuccess(HttpRequestManager httpRequestManager) {
				String result = httpRequestManager.getDataString();
				String access_token = "";
				try {
					JSONObject json = new JSONObject(result);
					access_token = json.getString("access_token");
					GameSDKApplication.getInstance().setAccress_token(access_token);
				} catch (Exception e) {
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				Logger.v(failReason);
			}
		};
		httpRequest.request(httpIntent, callBack1);
	}

}
