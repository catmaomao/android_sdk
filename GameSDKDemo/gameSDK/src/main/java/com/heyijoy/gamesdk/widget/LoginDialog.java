/**
 * LoginDialog.java
 * com.heyijoy.gamesdk.widget
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.widget;

import org.json.JSONException;
import org.json.JSONObject;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.SMSReceiver;
import com.heyijoy.gamesdk.activity.HYQQLoginActivity;
import com.heyijoy.gamesdk.activity.HYWeiBoLoginActivity;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.act.HYCallBackStr;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.data.HYLoginFailReason;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.sina.weibo.sdk.WeiboAppManager;
import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author msh
 * @since Ver 1.1
 * @Date 2014-2-21 上午10:06:37 登录dialog
 */
public class LoginDialog extends Dialog {
	private HYDialog dlg;
	private HYProgressDlg regProDlg;
	private SMSReceiver receiver;// 短信接收器
	private MSGSendReceiver mReceiver01;// 短信发送状态广播接收器
	private HYCallBack method;// 传入的函数
	private int flag = 0;// 跳转 登陆对话框 的 标记位，0：默认值；1：快速注册；2：手机号注册；3：用户名注册
	private Context context = null;
	private EditText ed_userName;
	private EditText ed_pwd;
	private TextView tv_reg, tv_quick_login;
	private Button btn_login;
	private CheckBox cb_protocol;
	private TextView tv_findPwd;
	private TextView tv_error, tv_protocol;
	private ImageView wx_login, qq_login, wb_login;
	private boolean canBackButton;
	private boolean isShowLoginFailed;
	private User user;
	private TimeWait timerReciver;// 计时器 用于快速注册接收时间

	private TimeMSGSend timerSend;// 计时器 用于快速注册发送时间

	private boolean status = false;
	private boolean cbChecked = true;

	private String third_username = "";
	private String third_headimg = "";

	public LoginDialog(Context context, HYCallBack method, boolean backButton, int flag) {
		super(context);
		this.context = context;
		this.method = method;
		this.canBackButton = backButton;
		this.flag = flag;
	}

	public LoginDialog(Context context, HYCallBack method, boolean backButton, boolean isLoginFailed, User user,
			int flag) {
		super(context);
		this.context = context;
		this.method = method;
		this.canBackButton = backButton;
		this.isShowLoginFailed = isLoginFailed;
		this.user = user;
		this.flag = flag;
	}

	@Override
	public void show() {
		if (context == null) {
			return;
		}
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_hylogin, null);
		dlg = new HYDialog(context, R.style.dialog);
		cb_protocol = (CheckBox) loginLayout1.findViewById(R.id.cb_protocol);
		tv_protocol = (TextView) loginLayout1.findViewById(R.id.tv_protocol);
		tv_error = (TextView) loginLayout1.findViewById(R.id.tv_error);
		ed_userName = (EditText) loginLayout1.findViewById(R.id.et_username);
		ed_pwd = (EditText) loginLayout1.findViewById(R.id.et_pwd);
		wx_login = (ImageView) loginLayout1.findViewById(R.id.wx_login);
		qq_login = (ImageView) loginLayout1.findViewById(R.id.qq_login);
		wb_login = (ImageView) loginLayout1.findViewById(R.id.wb_login);
		LinearLayout llhasthridlogin = (LinearLayout) loginLayout1.findViewById(R.id.ll_hasthridlogin);
		HYThridParams thridParams = GameSDKApplication.getInstance().getThridParams();
		String type = Util.hasThridlogin(thridParams);
		if ("no".equals(type)) {
			llhasthridlogin.setVisibility(View.GONE);
		} else {
			llhasthridlogin.setVisibility(View.VISIBLE);
		}

		/**
		 * 兼容方块老用户
		 */
		// 赋值用户名
		String userName = GameSDKApplication.getInstance().getUserName();
		if (!"".equals(userName)) {
			ed_userName.setText(userName);
			Editable et = ed_userName.getText();
			ed_userName.setSelection(et.length());
		}

		// 赋值用户密码,三方登录的状态下，不在登录ui显示密码
		boolean logoutSharePreferences = GameSDKApplication.getInstance().getLogoutSharePreferences();
		if (logoutSharePreferences) {
			String beforeLogoutUserLoginType = GameSDKApplication.getInstance().getBeforeLogoutUserLoginType();
			if ("".equals(beforeLogoutUserLoginType)) {
				String userPwd = GameSDKApplication.getInstance().getUserPwd();
				ed_pwd.setText("abcdefghij");// 假的显示密码，只做显示效果，6位，登录请求时真正的密码从缓存中取
			}
		}

		// Log.e("HeyiJoySDK", "LOGIN_PASSWORD=" + userPwd);

		// if (isShowLoginFailed) {
		// tv_error.setText("登录失败");
		// tv_error.setVisibility(View.VISIBLE);
		// ed_userName.setText(user.getUserName());
		// ed_pwd.setText(user.getPassword());
		// }
		tv_reg = (TextView) loginLayout1.findViewById(R.id.tv_reg);
		tv_quick_login = (TextView) loginLayout1.findViewById(R.id.tv_quick_login);

		ed_userName.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				tv_error.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

		});

		ed_pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				tv_error.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				status = true;
			}

		});

		/**
		 * 用户协议选择框状态
		 */
		cb_protocol.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cbChecked = true;
				} else {
					cbChecked = false;
				}
			}
		});

		/**
		 * 用户协议
		 */
		tv_protocol.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new ProtocolDialog(context, new HYCallBack() {

					@Override
					public void onSuccess(Bean bean) {
					}

					@Override
					public void onFailed(int code, String failReason) {

					}
				}).show();
			}
		});

		// qq登录
		qq_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!cbChecked) {
					tv_error.setText("请同意用户协议");
					tv_error.setVisibility(View.VISIBLE);
				} else {
					boolean qqClientAvailable = Util.isQQClientAvailable(context);
					if (qqClientAvailable) {
						IntentFilter intentFilter = new IntentFilter();
						intentFilter.addAction(HYConstant.QQ_LOGIN + GameSDKApplication.getInstance().getAppid());
						GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								synchronized (this) {
									if (dlg != null && dlg.isShowing()) {
										dlg.dismiss();
									}
									GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
									String token = intent.getStringExtra(HYConstant.QQ_LOGIN);
									User user = new User();
									user.setUserName(HYConstant.AAA);
									user.setPassword(HYConstant.BBB);
									user.setThirdparty_credential(token);
									user.setThirdparty("qq");
									login(user);
								}
							}
						}, intentFilter);

						Intent intent = new Intent(context, HYQQLoginActivity.class);
						context.startActivity(intent);
					} else {
						Toast.makeText(context, "请先安装QQ客户端", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		// 微信登录
		wx_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!cbChecked) {
					tv_error.setText("请同意用户协议");
					tv_error.setVisibility(View.VISIBLE);
				} else {
					IntentFilter intentFilter = new IntentFilter();
					intentFilter.addAction(HYConstant.WX_LOGIN + GameSDKApplication.getInstance().getAppid());
					GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							synchronized (this) {
								if (dlg != null && dlg.isShowing()) {
									dlg.dismiss();
								}
								GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
								String token = intent.getStringExtra(HYConstant.WX_LOGIN);
								User user = new User();
								user.setUserName(HYConstant.AAA);
								user.setPassword(HYConstant.BBB);
								user.setThirdparty_credential(token);
								user.setThirdparty("wechat");
								login(user);
							}
						}
					}, intentFilter);

					Util.wxLogin(context);
				}
			}
		});
		// 微博登录
		wb_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!cbChecked) {
					tv_error.setText("请同意用户协议");
					tv_error.setVisibility(View.VISIBLE);
				} else {
					boolean hasWbInstall = WeiboAppManager.getInstance(context).hasWbInstall();
					if (hasWbInstall) {
						IntentFilter intentFilter = new IntentFilter();
						intentFilter.addAction(HYConstant.WEIBO_LOGIN + GameSDKApplication.getInstance().getAppid());
						GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								synchronized (this) {
									if (dlg != null && dlg.isShowing()) {
										dlg.dismiss();
									}
									GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
									String token = intent.getStringExtra(HYConstant.WEIBO_LOGIN);
									User user = new User();
									user.setUserName(HYConstant.AAA);
									user.setPassword(HYConstant.BBB);
									user.setThirdparty_credential(token);
									user.setThirdparty("weibo");
									login(user);
								}
							}
						}, intentFilter);

						Intent intent = new Intent(context, HYWeiBoLoginActivity.class);
						context.startActivity(intent);
					} else {
						Toast.makeText(context, "请先安装微博客户端", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		btn_login = (Button) loginLayout1.findViewById(R.id.btn_login);
		// 登录点击事件
		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String userName = ed_userName.getText().toString();
					String pwd = ed_pwd.getText().toString();
					// Log.e("HeyiJoySDK", "userName=" + userName);
					if (TextUtils.isEmpty(userName)) {
						tv_error.setText("请输入用户名");
						tv_error.setVisibility(View.VISIBLE);
					} else if (TextUtils.isEmpty(pwd)) {
						tv_error.setText("请输入密码");
						tv_error.setVisibility(View.VISIBLE);
					} else if (!cbChecked) {
						tv_error.setText("请同意用户协议");
						tv_error.setVisibility(View.VISIBLE);
					} else {
						User user = new User();
						user.setUserName(userName);
						if (status) {
							user.setPassword(Util.md5(pwd));
						} else {
							user.setPassword(GameSDKApplication.getInstance().getUserPwd());
						}
						login(user);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 快速登录事件
		tv_quick_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isFastDoubleClick()) {
					return;
				}
				if (!cbChecked) {
					tv_error.setText("请同意用户协议");
					tv_error.setVisibility(View.VISIBLE);
				} else {
					dlg.dismiss();
					quickLogin("quick", "AAA", "BBB");
				}

				// new ProtocolDialog(context, new HYCallBack() {
				//
				// @Override
				// public void onSuccess(Bean bean) {
				// dlg.dismiss();
				// quickLogin("quick", "AAA", "BBB");
				// }
				//
				// @Override
				// public void onFailed(int code, String failReason) {
				//
				// }
				// }).show();
			}
		});

		// 注册点击事件
		tv_reg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isFastDoubleClick()) {
					return;
				}
				if (!cbChecked) {
					tv_error.setText("请同意用户协议");
					tv_error.setVisibility(View.VISIBLE);
				} else {
					dlg.dismiss();
					new NickNameRegDialog(context, method, true).show();
				}

				// new ProtocolDialog(context, new HYCallBack() {
				//
				// @Override
				// public void onSuccess(Bean bean) {
				// dlg.dismiss();
				// new NickNameRegDialog(context, method, true).show();
				// }
				//
				// @Override
				// public void onFailed(int code, String failReason) {
				//
				// }
				// }).show();
			}
		});

		tv_findPwd = (TextView) loginLayout1.findViewById(R.id.tv_find_pwd);
		// 忘记密码
		tv_findPwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isFastDoubleClick()) {
					return;
				}
				HYCallBack callback = new HYCallBack() {

					@Override
					public void onSuccess(Bean beyan) {
					}

					@Override
					public void onFailed(int code, String failReason) {

					}
				};
				FindPasswordDialog finddlg = new FindPasswordDialog(context, callback);
				finddlg.show();
			}
		});

		// 由dialog处理物理返回键
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					dlg.dismiss();
					switch (flag) {
					// case 3:
					// new NickNameRegDialog(context, method, true).show();
					// break;
					// case 2:
					// new RegDialog(context, method).changeViewMobile(false);
					// break;
					// case 1:
					// new RegDialog(context, method).show();
					// break;
					default:
						method.onFailed(HYConstant.EXCEPTION_CODE, HYLoginFailReason.CANCELED);
						break;
					}
				}
				return false;
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);
		dlg.show();
		// dlg.show(300,300);
	}

	/**
	 * 登录逻辑
	 */
	public void login(final User user) {
		final HYProgressDlg proDlg = HYProgressDlg.show(context, "合乐智趣", "正在登录…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				GameSDKApplication.getInstance().saveFistUseBlock(true);
				dlg.dismiss();
				proDlg.dismiss();
				final User userRe = (User) bean;
				GameSDKApplication.getInstance().saveShareUser(userRe);

				Toast.makeText(context, user.getUserName() + HYConstant.Toast_New_User, Toast.LENGTH_SHORT).show();

				String certifitationStatus = GameSDKApplication.getInstance().getCertifitationStatus();// 后台打开实名认证开关
				if ("on".equals(certifitationStatus)) {
					// 登录成后，执行实名认证提示逻辑
					if (userRe.isCertification()) {
						showBindingAccount(userRe);
					} else {
						// 弹出实名认证提示框
						new CertificationTipDialog(context, new HYCallBack() {

							@Override
							public void onSuccess(Bean bean) {
								showBindingAccount(userRe);
							}

							@Override
							public void onFailed(int code, String failReason) {

							}
						}).show();
					}
				} else {
					showBindingAccount(userRe);
				}

			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					tv_error.setText("网络异常，请重新登录");
					tv_error.setVisibility(View.VISIBLE);
					ed_userName.setText(user.getUserName());
					ed_pwd.setText(user.getPassword());
				} else {
					tv_error.setText(failReason);
					tv_error.setVisibility(View.VISIBLE);
				}
			}

		};
		HttpApi.getInstance().yklogin(user, callback);
	}

	/**
	 * 显示绑定账号提示框
	 */
	public void showBindingAccount(final User userRe) {
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				method.onSuccess(userRe);
			}

			@Override
			public void onFailed(int code, String failReason) {
				method.onSuccess(userRe);
			}
		};

		Util.showBindingDlgByChance(context, callback, "2");
	}

	/**
	 * 短信注册
	 */
	private void msgReg() {
		regProDlg = HYProgressDlg.show(context, "正在验证手机号…", true);

		String content = GameSDKApplication.getInstance().getRegMSGContent();
		try {
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			int absent = manager.getSimState();
			if (TelephonyManager.SIM_STATE_READY != absent) { // 没有手机卡
				sendFailDo();
			} else {
				/* 建立自定义Action常数的Intent(给PendingIntent参数之用) */
				Intent itSend = new Intent("HY_SMS_SEND_ACTIOIN");

				/* sentIntent参数为传送后接受的广播信息PendingIntent */
				PendingIntent mSendPI = PendingIntent.getBroadcast(context, 0, itSend, 0);

				/* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
				IntentFilter mFilter01 = new IntentFilter("HY_SMS_SEND_ACTIOIN");
				mReceiver01 = new MSGSendReceiver();
				context.registerReceiver(mReceiver01, mFilter01);

				timerSend = new TimeMSGSend(HYConstant.TIME_ONE_BUTTON_MSG_SEND_WAIT, 1000);
				timerSend.cancel();
				timerSend.start();

				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(HYConstant.REG_MOBILE_UP_NO, null, content, mSendPI, null);
			}
		} catch (Exception e) {
			sendFailDo();
		}
	}

	/**
	 * 快速登录
	 * 
	 * @param string
	 */
	private void quickLogin(String type, final String nickName, final String pwd) {
		final HYProgressDlg proDlg = HYProgressDlg.show(context, "合乐智趣游戏 ", "正在快速登录…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				GameSDKApplication.getInstance().saveFistUseBlock(true);
				dlg.dismiss();
				proDlg.dismiss();
				final User user = (User) bean;
				GameSDKApplication.getInstance().saveShareUser(user);

				String certifitationStatus = GameSDKApplication.getInstance().getCertifitationStatus();// 后台打开实名认证开关
				if ("on".equals(certifitationStatus)) {
					// 登录成后，执行实名认证提示逻辑
					if (user.isCertification()) {
						afterQuickLogin(user);
					} else {
						// 弹出实名认证提示框
						new CertificationTipDialog(context, new HYCallBack() {

							@Override
							public void onSuccess(Bean bean) {
								afterQuickLogin(user);
							}

							@Override
							public void onFailed(int code, String failReason) {

							}
						}).show();
					}
				} else {
					afterQuickLogin(user);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();

				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					Toast.makeText(context, "快速登录失败" + failReason, Toast.LENGTH_LONG).show();
				} else {
					tv_error.setText(failReason);
					tv_error.setVisibility(View.VISIBLE);
				}
			}
		};
		HttpApi.getInstance().regByName(type, nickName, pwd, callback);
	}

	public void afterQuickLogin(final User user) {
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				// 账号保存成功
				method.onSuccess(user);
				// HYCallBack callbackner = new HYCallBack() {
				// @Override
				// public void onSuccess(Bean bean) {
				//
				// Toast.makeText(context, user.getUserName() +
				// HYConstant.Toast_New_User, Toast.LENGTH_SHORT)
				// .show();
				// method.onSuccess(user);
				// }
				//
				// @Override
				// public void onFailed(int code, String failReason) {
				// Toast.makeText(context, user.getUserName() +
				// HYConstant.Toast_New_User, Toast.LENGTH_SHORT)
				// .show();
				// method.onSuccess(user);
				// }
				// };
				// Util.showBindingDlg(context, callbackner, "1");// 绑定手机号提示
			}

			@Override
			public void onFailed(int code, String failReason) {
				Toast.makeText(context, "账号保存异常", Toast.LENGTH_SHORT).show();
				method.onSuccess(user);
			}
		};

		// 保存账号图片到相册温馨提示框
		new SaveAccountDialog(context, user, callback, "hint").show();
	}

	/* 自定义MSGSendReceiver重写BroadcastReceiver监听短信状态信息 */
	public class MSGSendReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("HY_SMS_SEND_ACTIOIN")) {
				try {
					timerSend.cancel();
					if (getResultCode() == Activity.RESULT_OK) {// 发送成功
						// HttpApi.getInstance().regquickStatistic(null,HYConstant.REG_SEND_SUCESS,null,"0");//登录界面注册埋点

						receiver = new SMSReceiver(smsReceiverHandler);
						IntentFilter filter = new IntentFilter();
						filter.setPriority(1000);
						filter.addAction("android.provider.Telephony.SMS_RECEIVED");
						context.registerReceiver(receiver, filter);

						// 开始计时
						if (timerReciver != null) {
							timerReciver.cancel();
						}
						timerReciver = new TimeWait(HYConstant.TIME_ONE_BUTTON_REG_WAIT, 1000);
						timerReciver.start();

					} else {
						String receiptStr = "RESULT_ERROR_OTHER";
						if (getResultCode() == SmsManager.RESULT_ERROR_NO_SERVICE) {
							receiptStr = "RESULT_ERROR_NO_SERVICE";
						} else if (getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE) {
							receiptStr = "RESULT_ERROR_GENERIC_FAILURE";
						} else if (getResultCode() == SmsManager.RESULT_ERROR_NULL_PDU) {
							receiptStr = "RESULT_ERROR_NULL_PDU";
						} else if (getResultCode() == SmsManager.RESULT_ERROR_RADIO_OFF) {
							receiptStr = "RESULT_ERROR_RADIO_OFF";
						}
						sendFailDo();
					}
					// 退出监听器
					try {
						context.unregisterReceiver(mReceiver01);
					} catch (Exception e) {
					}
				} catch (Exception e) {
					sendFailDo();
					e.getStackTrace();
				}
			}
		}
	}

	private void sendFailDo() {// 发送失败的处理 跳转到输入手机号注册页 超时或者没有卡 或者信号不好 或者发送被拦截
								// 或者异常
		regProDlg.dismiss();
		dlg.dismiss();
		if (timerSend != null) {
			timerSend.cancel();
		}
		try {
			context.unregisterReceiver(mReceiver01);
			receiver = null;
		} catch (Exception e) {
		}
		RegDialog regdlg = new RegDialog(context, this.method);
		regdlg.changeViewMobile(true);
	}

	/**
	 * smsReceiverHandler:用于拦截验证码广播接收器的handler
	 *
	 */
	private Handler smsReceiverHandler = new Handler() {
		private String phone;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {// 收超时
				try {
					dlg.dismiss();
					regProDlg.dismiss();
					timerReciver.cancel();
				} catch (Exception e) {
				}

				// 时间到 跳转
				RegDialog regdlg = new RegDialog(context, method);
				regdlg.changeViewMobile(true);
			} else if (msg.what == 2) {// 发超时
				// HttpApi.getInstance().regquickStatistic(null,HYConstant.REG_SEND_TIME_OUT,null,"0");//发送成功
				sendFailDo();
			} else if (msg.what == SMSReceiver.MSG_WHAT_REG_NEW_USER) {
				if (regProDlg != null) {
					regProDlg.dismiss();
				}
				timerReciver.cancel();
				String source = msg.getData().getString(SMSReceiver.MSG_DATA_SOURCE);
				phone = msg.getData().getString(SMSReceiver.MSG_DATA_USERNAME);
				String pwd = msg.getData().getString(SMSReceiver.MSG_DATA_PWD);
				User user = new User();
				user.setUserName(phone);
				GameSDKApplication.getInstance().setIsneedbind(false);

				if (SMSReceiver.MSG_DATA_SOURCE_MMS.equals(source)) {
					// 注册成功
					user.setPassword(pwd);
					login(user);
				} else {
					// 注册成功
					user.setVerifyNo(pwd);
					login(user);
				}

			} else if (msg.what == SMSReceiver.MSG_WHAT_REG_OLD_USER) {
				timerReciver.cancel();
				String source = msg.getData().getString(SMSReceiver.MSG_DATA_SOURCE);
				String verifyNo = msg.getData().getString(SMSReceiver.MSG_DATA_VERIFYNO);
				phone = msg.getData().getString(SMSReceiver.MSG_DATA_USERNAME);
				final User user = new User();
				user.setUserName(phone);
				GameSDKApplication.getInstance().setIsneedbind(false);

				if (SMSReceiver.MSG_DATA_SOURCE_MMS.equals(source)) {
					// HttpApi.getInstance().regquickStatistic(phone,HYConstant.REG_SUCESS_OLD,null,"1");//登录界面注册埋点
					// 注册成功 老用户
				} else {
					// HttpApi.getInstance().regquickStatistic(phone,HYConstant.REG_SUCESS_OLD,null,"2");//登录界面注册埋点
					// 注册成功 老用户
				}

				HYCallBack callBack = new HYCallBack() {
					@Override
					public void onSuccess(Bean bean) {
						dlg.dismiss();
						GameSDKApplication.getInstance().saveShareUser((User) bean);
						method.onSuccess(((User) bean));
						regProDlg.dismiss();
						Toast.makeText(context, user.getUserName() + HYConstant.Toast_Old_User, Toast.LENGTH_SHORT)
								.show();

						// HttpApi.getInstance().loginStatistic(user.getUserName(),
						// "2");//验证码登录成功埋点
					}

					@Override
					public void onFailed(int code, String failReason) {
						regProDlg.dismiss();
						ed_userName.setText(user.getUserName());
						Toast.makeText(context, failReason, Toast.LENGTH_LONG).show();
					}
				};
				HttpApi.getInstance().loginByVerifyNo(user, verifyNo, callBack);
			}

			// HYSocketManger.getInstance().close();
			// 退出监听器
			try {
				context.unregisterReceiver(receiver);
			} catch (Exception e) {
			}
		}
	};
	/*
	 * TimerTask task = new TimerTask() {
	 * 
	 * @Override public void run() { Message message = new Message();
	 * message.what = 1; smsReceiverHandler.sendMessage(message); } };
	 */

	/* 定义一个倒计时的内部类 */
	class TimeWait extends CountDownTimer {
		public TimeWait(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			Message message = new Message();
			message.what = 1;
			smsReceiverHandler.sendMessage(message);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
		}
	}

	/* 定义一个倒计时的内部类 倒计时短信是否发送成功 */
	class TimeMSGSend extends CountDownTimer {
		public TimeMSGSend(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			Message message = new Message();
			message.what = 2;
			smsReceiverHandler.sendMessage(message);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
		}
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (receiver != null) {
			context.unregisterReceiver(receiver);
		}
	}
}
