package com.heyijoy.gamesdk.act;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.heyijoy.gamesdk.announcement.AnnouncementCache;
import com.heyijoy.gamesdk.announcement.AnnouncementClickListener;
import com.heyijoy.gamesdk.announcement.PopUpBean;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.CookieContentResover;
import com.heyijoy.gamesdk.data.CookieContentResoverByCenter;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.data.UserContentResover;
import com.heyijoy.gamesdk.data.VipBean;
import com.heyijoy.gamesdk.data.HYPayBean;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYBuildConfig;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.memfloat.FloatService;
import com.heyijoy.gamesdk.memfloat.FloatViewService;
import com.heyijoy.gamesdk.operatorpay.OpPay;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.LoginDialog;
import com.heyijoy.gamesdk.widget.NickNameRegDialog;
import com.heyijoy.gamesdk.widget.PayDialog;
import com.heyijoy.gamesdk.widget.SaveAccountDialog;
import com.heyijoy.gamesdk.widget.ShareDialog;
import com.heyijoy.gamesdk.widget.WebViewFullscreenActivity;
import com.heyijoy.sdk.CallBack;
import com.heyijoy.sdk.ShareParams;
import com.heyijoy.sdk.analytics.UDAgent;
import com.heyijoy.gamesdk.widget.CertificationPayTipDialog;
import com.heyijoy.gamesdk.widget.CertificationTipDialog;
import com.heyijoy.gamesdk.widget.HYDialog;
import com.heyijoy.gamesdk.widget.HYProgressDlg;
import com.heyijoy.gamesdk.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author msh
 * @version
 * @Date 2014-2-14
 */
public class HYPlatform {
	public static String FAIL_REASION_NO_POP_UP = "FAIL_REASION_NO_POP_UP";

	public static void init(Context context) {
		GameSDKApplication.getInstance().init(context.getApplicationContext());
	}

	/**
	 * 退出，下次自动登录
	 */
	@SuppressLint("NewApi")
	public static void quit(final Context context, final HYCallBack callback) {
		final HYDialog dlg = new HYDialog(context, R.style.dialog);
		LinearLayout quitLayout = (LinearLayout) dlg.getLayoutInflater().inflate(R.layout.dialog_hy_quit, null);
		TextView tv_title_text = (TextView) quitLayout.findViewById(R.id.tv_title_text);
		tv_title_text.setText(HYConstant.TITLE_QUIT);
		TextView image_text = (TextView) quitLayout.findViewById(R.id.hy_quit_baner_text);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(quitLayout);
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					callback.onFailed(keyCode, "back");
				}
				return false;
			}
		});
		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {

			}
		});

		Button button_cancel = (Button) quitLayout.findViewById(R.id.btn_cancel);
		button_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// callback.onFailed(HYConstant.EXCEPTION_CODE,"cancel");
				dlg.cancel();
			}
		});

		Button button_quit = (Button) quitLayout.findViewById(R.id.btn_quit);
		button_quit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				callback.onSuccess(null);
				AnnouncementCache.getInstance().destroy();
				PopUpBean.getInstance().destroy();
				UserContentResover.getInstance().destroy();
				CookieContentResover.getInstance().destroy();
				CookieContentResoverByCenter.getInstance().destroy();
				GameSDKApplication.getInstance().clearTableListID();
				HYPlatform.closeHYFloat(context);
				dlg.cancel();
			}
		});
		// if (PopUpBean.getInstance().getBannar() != null) {
		// BitmapDrawable bd = new
		// BitmapDrawable(PopUpBean.getInstance().getBannar());
		// image.setBackgroundDrawable(bd);
		// AnnouncementClickListener listener = new
		// AnnouncementClickListener(PopUpBean.getInstance(), true);
		// image.setOnClickListener(listener);
		// } else {
		image_text.setText("您确定要退出游戏吗？");
		// }
		dlg.show();
	}

	public static void setInCocos2dxActivity(Boolean b) {
		GameSDKApplication.getInstance().setInCocos2dxActivity(b);
	}

	/**
	 * 退出，下次自动登录 不带退出弹窗
	 */
	@SuppressLint("NewApi")
	public static void quitNoPopUp(Context context) {
		HYPlatform.closeHYFloat(context);
	}

	/**
	 * 注销，切换用户，下次不自动登录
	 */
	public static void logout(Context context) {
		// GameSDKApplication.getInstance().deletePwdINPref(context);
		// GameSDKApplication.getInstance().saveCookie("");//清除cookie
		GameSDKApplication.getInstance().saveLogoutSharePreferences(true);// 保存为注销状态
		GameSDKApplication.getInstance().setRemoteLogin(false);// 保存为注销状态
		GameSDKApplication.getInstance().setHasTask(false);
		GameSDKApplication.getInstance().clearTableListID();
		GameSDKApplication.getInstance().setTableListID("change");
		// 获取用户注销前的登录状态
		GameSDKApplication.getInstance()
				.SetBeforeLogoutUserLoginType(GameSDKApplication.getInstance().getUserLoginType());
		GameSDKApplication.getInstance().setUserLoginType("");
		GameSDKApplication.getInstance().savePhonePre("");
		GameSDKApplication.getInstance().saveUserCertifitationStatus(false);// 保存用户实名认证为注销状态
		HYPlatform.closeHYFloat(context);
	}

	/**
	 * 清除本地用户信息 回到初始安装状态下次不自动登录
	 */
	public static void deleteUser(Context context) {
		GameSDKApplication.getInstance().deleteUserInPref(context);
		// GameSDKApplication.getInstance().saveCookie("");//清除cookie
		GameSDKApplication.getInstance().saveLogoutSharePreferences(true);// 保存为注销状态
	}

	/**
	 * 自动登录
	 */
	public static void autoLogin(final HYCallBack callbackRe, final Context context) {

		HYThridParams thridParams = GameSDKApplication.getInstance().getThridParams();
		String type = Util.hasThridlogin(thridParams);
		if ("no".equals(type)) {// 不带三方登录，使用方块官方包名
			// 此时，兼容方块老用户处理
			dealBlockUserOld(callbackRe, context);
		} else {
			hyLogin(callbackRe, context);// 不使用方块官方包名
		}
	}

	private static void dealBlockUserOld(HYCallBack callbackRe, final Context context) {
		if (!TextUtils.isEmpty(UDAgent.getInstance().getBlockParams())) {// 针对方块游戏处理,其他游戏如果没有特殊需求，传空字符串即可（即不作处理）
			boolean firstUseBlock = GameSDKApplication.getInstance().getFirstUseBlock();
			if (!firstUseBlock) {
				GameSDKApplication.getInstance().saveBlockCell(UDAgent.getInstance().getBlockParams());
			}
		} else {// 传递空值，也就不存在老用户第一次点击问题了
			GameSDKApplication.getInstance().saveFistUseBlock(true);
		}

		boolean firstUseBlock = GameSDKApplication.getInstance().getFirstUseBlock();
		if (!firstUseBlock) {
			final LoginCallBack loginCallBack = new LoginCallBack(callbackRe, context);
			String userName = GameSDKApplication.getInstance().getUserName();
			if (userName.startsWith("Guest_")) {// 方块游客第一次
				final HYProgressDlg proDlg = HYProgressDlg.show(context, "合乐智趣游戏 ", "正在快速登录…");
				HYCallBack callback = new HYCallBack() {
					@Override
					public void onSuccess(Bean bean) {
						proDlg.dismiss();
						final User user = (User) bean;
						GameSDKApplication.getInstance().saveLogoutSharePreferences(false);// 撤销注销
						GameSDKApplication.getInstance().saveFistUseBlock(true);
						String certifitationStatus = GameSDKApplication.getInstance().getCertifitationStatus();// 后台打开实名认证开关
						if ("on".equals(certifitationStatus)) {
							// 登录成后，执行实名认证提示逻辑
							if (user.isCertification()) {
								afterQuickLogin(user, context, loginCallBack);
							} else {
								// 弹出实名认证提示框
								new CertificationTipDialog(context, new HYCallBack() {

									@Override
									public void onSuccess(Bean bean) {
										afterQuickLogin(user, context, loginCallBack);
									}

									@Override
									public void onFailed(int code, String failReason) {

									}
								}).show();
							}
						} else {
							afterQuickLogin(user, context, loginCallBack);
						}
					}

					@Override
					public void onFailed(int code, String failReason) {
						proDlg.dismiss();
						Toast.makeText(context, "快速登录失败", Toast.LENGTH_LONG).show();
						loginCallBack.onFailed(code, failReason);
					}
				};
				HttpApi.getInstance().regByName("quick", "AAA", "BBB", callback);
			} else {// 有用户名和密码第一次或不是方块之前的官方包名或如百度渠道新用户
				hyLogin(callbackRe, context);
			}
		} else {
			hyLogin(callbackRe, context);
		}
	}

	public static void afterQuickLogin(final User user, final Context context, final LoginCallBack loginCallBack) {
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				// 账号保存成功
				loginCallBack.onSuccess(user);
//				HYCallBack callbackner = new HYCallBack() {
//					@Override
//					public void onSuccess(Bean bean) {
//						Toast.makeText(context, user.getUserName() + HYConstant.Toast_New_User, Toast.LENGTH_SHORT)
//								.show();
//						loginCallBack.onSuccess(user);
//					}
//
//					@Override
//					public void onFailed(int code, String failReason) {
//						Toast.makeText(context, user.getUserName() + HYConstant.Toast_New_User, Toast.LENGTH_SHORT)
//								.show();
//						loginCallBack.onSuccess(user);
//					}
//				};
//				Util.showBindingDlg(context, callbackner, "1");// 绑定手机号提示
			}

			@Override
			public void onFailed(int code, String failReason) {
				loginCallBack.onSuccess(user);
				Toast.makeText(context, "账号保存异常", Toast.LENGTH_SHORT).show();
			}
		};

		// 保存账号图片到相册温馨提示框
		new SaveAccountDialog(context, user, callback, "hint").show();
	}

	private static void hyLogin(final HYCallBack callbackRe, final Context context) {
		final LoginCallBack loginCallBack = new LoginCallBack(callbackRe, context);
		final User user = GameSDKApplication.getInstance().getUserFromPref(context);
		if ("".equals(user.getUserName())) {// 本地跟游戏中心没有用户名
			// 则登录（一般是第一次登录时调用）
			HYCallBack callBack = new HYCallBack() {
				@Override
				public void onSuccess(Bean bean) {
					GameSDKApplication.getInstance().saveLogoutSharePreferences(false);// 撤销注销
					loginCallBack.onSuccess(bean);
				}

				@Override
				public void onFailed(int code, String failReason) {
					loginCallBack.onFailed(code, failReason);
				}
			};
			// dialog.dismiss();
			new LoginDialog(context, callBack, false, 0).show();

		} else if (GameSDKApplication.getInstance().getLogoutSharePreferences()) {
			// 切换账号，注销时调用
			HYCallBack callBack = new HYCallBack() {
				@Override
				public void onSuccess(Bean bean) {
					GameSDKApplication.getInstance().saveFistUseBlock(true);
					GameSDKApplication.getInstance().saveLogoutSharePreferences(false);// 撤销注销
					loginCallBack.onSuccess(bean);
				}

				@Override
				public void onFailed(int code, String failReason) {
					loginCallBack.onFailed(code, failReason);
				}
			};
			// dialog.dismiss();
			new LoginDialog(context, callBack, false, 0).show();
		} else {
			final HYProgressDlg dialog = HYProgressDlg.show(context, "合乐智趣游戏帐号  " + user.getUserName(), "正在登录…");
			user.setAuto(true);
			// 自动登录了
			HYCallBack callback = new HYCallBack() {
				@Override
				public void onSuccess(Bean bean) {
					dialog.dismiss();
					GameSDKApplication.getInstance().saveShareUser(user);// 保存到本地
					GameSDKApplication.getInstance().saveLogoutSharePreferences(false);// 撤销注销
					final User cu_user = (User) bean;

					String certifitationStatus = GameSDKApplication.getInstance().getCertifitationStatus();// 后台打开实名认证开关
					if ("on".equals(certifitationStatus)) {
						// 登录成后，执行实名认证提示逻辑
						if (cu_user.isCertification()) {
//							loginCallBack.onSuccess(cu_user);
							showBindingAccount(context, loginCallBack, cu_user);
						} else {
							// 弹出实名认证提示框
							new CertificationTipDialog(context, new HYCallBack() {

								@Override
								public void onSuccess(Bean bean) {
//									loginCallBack.onSuccess(cu_user);
									showBindingAccount(context, loginCallBack, cu_user);
								}

								@Override
								public void onFailed(int code, String failReason) {

								}
							}).show();
						}
					} else {
//						loginCallBack.onSuccess(cu_user);
						showBindingAccount(context, loginCallBack, cu_user);
					}
				}

				@Override
				public void onFailed(int code, String failReason) {
					dialog.dismiss();
					Toast.makeText(context, failReason, Toast.LENGTH_LONG).show();
					new LoginDialog(context, loginCallBack, false, 0).show();
				}
			};
			HttpApi.getInstance().yklogin(user, callback);
		}

	}

	public static void showBindingAccount(Context context, final LoginCallBack loginCallBack, final User cu_user) {
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				loginCallBack.onSuccess(cu_user);
			}

			@Override
			public void onFailed(int code, String failReason) {
				loginCallBack.onSuccess(cu_user);
			}
		};
		Util.showBindingDlgByChance(context, callback, "2");
	}

	/**
	 * 合乐智趣支付接口
	 * 
	 * @param activity
	 *            activity
	 * @param ykPayBean
	 *            支付的相关信息实体
	 * @param payCallBack
	 *            支付完成后的回调函数
	 */
	public static void doPay(final Activity activity, final HYPayBean ykPayBean, final HYCallBack payCallBack) {
		if (Util.isFastDoubleClick())
			return;// 若点击过快，则只执行第一次

		String certifitationPayStatus = GameSDKApplication.getInstance().getCertifitationPayStatus();
		if ("on".equals(certifitationPayStatus)) {// "on"代开不实名，不给认证开关
			boolean userCertifitationStatus = GameSDKApplication.getInstance().getUserCertifitationStatus();
			if (!userCertifitationStatus) {// 针对没有认证的用户
				new CertificationPayTipDialog(activity, new HYCallBack() {

					@Override
					public void onSuccess(Bean bean) {

					}

					@Override
					public void onFailed(int code, String failReason) {

					}
				}).show();
			} else {
				payM(activity, ykPayBean, payCallBack);
			}
		} else {
			payM(activity, ykPayBean, payCallBack);
		}
	}

	public static void payM(final Activity activity, final HYPayBean ykPayBean, final HYCallBack payCallBack) {
		int amount = 1;
		try {
			amount = Integer.valueOf(ykPayBean.getAmount());
		} catch (Exception e) {
			Toast.makeText(activity, "金额有误，请确认后重新购买！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ykPayBean.getProductName().length() < 1) {
			ykPayBean.setProductName("游戏道具");
		}
		final HYProgressDlg payLoading = HYProgressDlg.show(activity, "正在进入支付…");
		// 单机游戏的处理方法
		if (GameSDKApplication.getInstance().isStandAlone()) {
			if (Util.isFastDoubleClick())
				return;// 若点击过快，则只执行第一次
			if (payLoading != null && payLoading.isShowing())
				payLoading.dismiss();
			// 电信联通无网络情况下提示联网
			if (!Util.hasInternet(activity)) {
				if (GameSDKApplication.getInstance().getProvidersCode().equals("3")) {
					Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
					activity.startActivity(wifiSettingsIntent);
					Toast.makeText(activity, "需要联网后进行支付！", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			doPayNext(activity, ykPayBean, payCallBack, amount);
			return;
		}
		// 联网游戏的处理方法
		HYCallBack canPayCallBack = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				Logger.d("HYSDK", "验证订单成功");
				Util.isFastDoubleClick();// 若点击过快，则只执行第一次
				doPayNext(activity, ykPayBean, payCallBack, Integer.valueOf(ykPayBean.getAmount()));
				if (payLoading != null && payLoading.isShowing())
					payLoading.dismiss();
			}

			@Override
			public void onFailed(int code, String failReason) {
				Logger.d("HYSDK", "验证订单失败");
				Util.isFastDoubleClick();// 若点击过快，则只执行第一次
				payCallBack.onFailed(code, failReason);
				if (payLoading != null && payLoading.isShowing())
					payLoading.dismiss();
			}
		};
		HttpApi.getInstance().checkAppOrderID(ykPayBean.getAppOrderId(), canPayCallBack);
	}

	private static void doPayNext(final Activity activity, final HYPayBean ykPayBean, final HYCallBack payCallBack,
			int moneyFen) {
		if ((!GameSDKApplication.getInstance().getPaySwitch().contains("smsproxy")
				&& !GameSDKApplication.getInstance().isStandAlone()) || (moneyFen % 100) != 0
				|| Util.isAirplaneMode()) {
			if (!Util.hasInternet(activity)) {
				Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
				activity.startActivity(wifiSettingsIntent);
				Toast.makeText(activity, "需要联网后进行支付！", Toast.LENGTH_SHORT).show();
				return;
			}
			PayDialog payDialog = new PayDialog(activity, ykPayBean, payCallBack, false, false);
			payDialog.show();
		} else {
			final HYProgressDlg payLoading = HYProgressDlg.show(activity, "正在进入支付…");

			try {
				HYCallBack queryCallBack = new HYCallBack() {
					@Override
					public void onSuccess(Bean bean) {
						if (payLoading != null && payLoading.isShowing())
							payLoading.dismiss();
						PayDialog payDialog = new PayDialog(activity, ykPayBean, payCallBack, true, false);
						payDialog.show();
					}

					@Override
					public void onFailed(int code, String failReason) {
						if (payLoading != null && payLoading.isShowing())
							payLoading.dismiss();
						if (!Util.hasInternet(activity)) {
							Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
							activity.startActivity(wifiSettingsIntent);
							Toast.makeText(activity, "需要联网后进行支付！", Toast.LENGTH_SHORT).show();
							return;
						}
						PayDialog payDialog = new PayDialog(activity, ykPayBean, payCallBack, false, false);
						payDialog.show();
					}
				};

				OpPay opLoading = new OpPay();
				String amount = String.valueOf(moneyFen / 100);
				opLoading.query(activity, amount, queryCallBack);
			} catch (Exception e) {
				if (payLoading != null && payLoading.isShowing())
					payLoading.dismiss();
				if (GameSDKApplication.getInstance().isStandAlone()) {// 若为单机模式
					if (!Util.hasInternet(activity)) {
						Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
						activity.startActivity(wifiSettingsIntent);
						Toast.makeText(activity, "需要联网后进行支付！", Toast.LENGTH_SHORT).show();
						return;
					}
					PayDialog payDialog = new PayDialog(activity, ykPayBean, payCallBack, false, false);
					payDialog.show();
				} else {
					PayDialog payDialog = new PayDialog(activity, ykPayBean, payCallBack, false, false);
					payDialog.show();
				}
			}
			new Handler().postDelayed(new Runnable() {
				public void run() {
					if (payLoading != null && payLoading.isShowing())
						payLoading.dismiss();
				}

			}, 60000);
		}
	}

	/**
	 * 返回sdk版本 通过代码写sdk版本
	 */
	public static String getSDKVersion() {
		return HYConstant.getSDKVersion();
	}

	public static Boolean isTest() {
		return HYBuildConfig.IS_TEST;
	}

	/**
	 * 设置为单机游戏环境
	 */
	public static void setStandAlone(Boolean b) {
		GameSDKApplication.getInstance().setStandAlone(b);
		User user = new User();
		GameSDKApplication.getInstance().saveShareUserLocal(user);
	}


//	public static void setYKFloat(Context context, final HYCallBack callback) {
//		/* 悬浮窗的广播接收注册 */
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(HYConstant.YK_CHANGE_ACCOUNT + GameSDKApplication.getInstance().getAppid());
//		GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				synchronized (this) {
//					callback.onSuccess(null);
//				}
//			}
//		}, intentFilter);
//
//		Intent service = new Intent();
//		service.setClass(context, FloatService.class);
//		context.startService(service);
//	}

	/**
	 * 开启悬浮窗接口
	 * 
	 * @param context
	 * @param items
	 * @param callback
	 */
	public static void startHYFloat(final Context context, final HYCallBackWithContext callback) {
		openFloat(context, callback, new VipBean());
	}

	private static void openFloat(Context context, final HYCallBackWithContext callback, VipBean vipBean) {
		Intent intent = new Intent();
		intent.setClass(context, FloatViewService.class);
		if (callback != null) {
			/* 悬浮窗的广播接收注册 */
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(HYConstant.YK_CHANGE_ACCOUNT + GameSDKApplication.getInstance().getAppid());
			GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					synchronized (this) {
						if (Util.isFastDoubleClick())
							return;
						GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
						// GameSDKApplication.getInstance().clearHashMap();
						callback.callback(context);
					}
				}
			}, intentFilter);
		}
		context.startService(intent);
	}

	/**
	 * 开启悬浮窗（无切换账号回调）
	 * 
	 * @param context
	 * @param items
	 */
	public static void startHYFloat(Context context) {
		startHYFloat(context, null);
	}

	public static void closeHYFloat(Context context) {
		Intent serviceStop = new Intent();
		serviceStop.setClass(context, FloatViewService.class);
		context.stopService(serviceStop);
	}

	/**
	 * 未处理异常统计
	 * 
	 * @param ex
	 */
	@SuppressLint("SimpleDateFormat")
	public static void exceptionYKStatis(Exception ex) {
		if (ex instanceof RuntimeException) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			String exceptiontime = "";
			DateFormat formatter = null;
			ex.printStackTrace(pw);
			ex.printStackTrace();
			String exceptionStr = sw.toString();
			if (exceptionStr != null && exceptionStr.contains("com.heyijoy.gamesdk.")) {
				Logger.d("捕获未处理异常----获取来源：cp---异常来源：sdk");
				formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
				exceptiontime = formatter.format(new Date());
			} else {
				Logger.d("捕获未处理异常----获取来源：cp---异常来源：cp");
				formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
				exceptiontime = formatter.format(new Date());
			}
			Editor edit = GameSDKApplication.getInstance().getEditorForException();
			edit.putBoolean("flag", true);
			edit.commit();

			int i = 0;
			while (!GameSDKApplication.getInstance().isSend()) {
				try {
					Thread.sleep(300);
					i++;
					if (i >= 17) {
						Logger.d("获取来源：未发送post统计，但强制关闭！");
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("NewApi")
	public static void exceptionYKFinish(Activity activity) {
		boolean flag = false;
		SharedPreferences sp = activity.getSharedPreferences(HYConstant.YK_EXCEPTION, Context.MODE_PRIVATE);
		flag = sp.getBoolean("flag", false);
		if (flag) {
			HYPlatform.closeHYFloat(activity);
			flag = false;
			Editor edit = sp.edit();
			edit.putBoolean("flag", flag);
			edit.commit();
			activity.finish();
			activity.finishAffinity();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	/**
	 * 与exceptionYKFinish方法 不同在于，不进行 修改 对应的 SharedPreferences 文件(备用可能需要的方法)
	 * 
	 * @param activity
	 */
	@SuppressLint("NewApi")
	public static void exceptionYKFinishForNOSave(Activity activity) {
		boolean flag = false;
		SharedPreferences sp = activity.getSharedPreferences(HYConstant.YK_EXCEPTION, Context.MODE_PRIVATE);
		flag = sp.getBoolean("flag", false);
		if (flag) {
			HYPlatform.closeHYFloat(activity);
			activity.finish();
			activity.finishAffinity();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	public static void buyVip(Activity activity) {
		buyVip(activity, "2");// 1sdk悬浮窗， 2游戏
	}

	public static void buyVip(Context context, String entrytypes) {
		Intent intent = new Intent(context, WebViewFullscreenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("entrytypes", entrytypes);// 1sdk悬浮窗， 2游戏 , 3消息系统
		context.startActivity(intent);
	}

	public static Boolean getIsVip() {
		return GameSDKApplication.getInstance().getVipIsVip().equals("0") ? false : true;
	}

	public static String getWXappid() {
		return HYConstant.WX_APPID;
	}

	public static Boolean getIsRecharge() {
		return HYConstant.isRecharge;
	}

	/**
	 * 获取当前应用的AppID
	 */
	public static String getAppId() {
		return GameSDKApplication.getInstance().getAppid();
	}

	public static void share(Context context, ShareParams shareParams, CallBack callback) {
		if (Util.isFastDoubleClick())
			return;
		// GameSDKApplication.getInstance().saveShareParams(shareParams);
		new ShareDialog(context, shareParams, callback).show();
	}
}
