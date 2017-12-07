/**
 * LoginDialog.java
 * com.heyijoy.gamesdk.widget
 *
 *   		 2014-2-21 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.SMSReceiver;
import com.heyijoy.gamesdk.activity.HYInitActivity;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.HYLoginFailReason;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author msh
 * @since Ver 1.1
 * @Date 2014-2-21 上午10:06:37
 */
public class BindingPhoneDialog extends Dialog {
	private TimeCount time;// 计时器
	private HYProgressDlg proDlg;
	private HYDialog dlg;
	private SMSReceiver receiver;// 短信接收器
	private Button btn_binding;
	private Button btn_re_send;
	private ImageView iv_delete;
	private Context context = null;
	private EditText ed_verify;
	private EditText ed_phone;
	private TextView tv_error;
	private String phoneNo;
	private HYCallBack method;
	private String from;// 从何处跳转到绑定页面 快速注册和个人中心

	public BindingPhoneDialog(Context context, String from) {
		super(context);
		this.context = context;
		this.from = from;
	}

	public BindingPhoneDialog(Context context, HYCallBack method, String from) {
		super(context);
		this.context = context;
		this.method = method;
		this.from = from;
	}

	@Override
	public void show() {
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_hy_binding_phone, null);
		dlg = new HYDialog(context, R.style.dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);
		tv_error = (TextView) loginLayout1.findViewById(R.id.tv_error);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		tv_title_text.setText(HYConstant.TITLE_BINDING);
		iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setVisibility(View.VISIBLE);
		ed_verify = (EditText) loginLayout1.findViewById(R.id.et_verify_no);

		ed_phone = (EditText) loginLayout1.findViewById(R.id.et_phone);

		btn_re_send = (Button) loginLayout1.findViewById(R.id.btn_re_send);
		btn_re_send.setText("发送验证码");
		btn_re_send.setClickable(true);
		btn_re_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				phoneNo = ed_phone.getText().toString();
				if (TextUtils.isEmpty(phoneNo)) {
					showError("请填写手机号");
				} else {
					getVerifyNo(phoneNo);
				}
				// if (Util.isMobileNO(phoneNo)) {
				// 新增176，177电话号码段
				// getVerifyNo(phoneNo);
				// } else {
				// showError("电话号码错误");
				// }

			}
		});
		btn_binding = (Button) loginLayout1.findViewById(R.id.btn_binding);
		btn_binding.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String verifyNo = ed_verify.getText().toString();
				if (verifyNo.length() == 0) {
					showError("请先获取验证码");
				} else if (verifyNo.length() != 6) {
					showError("验证码错误");
				} else {
					binding(verifyNo);
				}
			}
		});

		iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ("5".equals(from)) {// 来自个人中心
					new BindDialog(context).show();
				} else if ("66".equals(from)) {
					Intent intent = new Intent(context, HYInitActivity.class);
					intent.putExtra("from", "moreByFloatService");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
				finishDialog();
			}
		});

		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					return true;
				}
				return false;
			}
		});

		dlg.show();
	}

	public void finishDialog() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		if (method != null) {
			method.onSuccess(null);
		}
	}

	/**
	 * 等待接受短信
	 */
	private void changeVerify(String phone) {
		if (receiver == null) {
			receiver = new SMSReceiver(smsReceiverHandler);
			IntentFilter filter = new IntentFilter();
			filter.setPriority(1000);
			filter.addAction("android.provider.Telephony.SMS_RECEIVED");
			context.registerReceiver(receiver, filter);
		}

		this.phoneNo = phone;
		time = new TimeCount(HYConstant.TIME_RE_SEND_VERIFY_WAIT, 1000);// 构造CountDownTimer对象
		time.start();
	}

	/**
	 * smsReceiverHandler:用于拦截验证码广播接收器的handler 将接收到的短信内容填充指定的text
	 *
	 * @since Ver 1.1
	 */
	private Handler smsReceiverHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SMSReceiver.MSG_WHAT_BINDING) {
				if (ed_verify != null) {
					ed_verify.setText(msg.getData().getString(SMSReceiver.MSG_DATA_BINDING));
					try {
						context.unregisterReceiver(receiver);
						receiver = null;
					} catch (Exception e) {
					}

				}
			}

		}
	};

	/**
	 * 获取验证码
	 */
	private void getVerifyNo(String phone) {
		proDlg = HYProgressDlg.show(context, "验证码发送中…");

		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				Logger.d("MSG-FIND-PWD_REQUEST-TIME", "" + System.currentTimeMillis());
				Toast.makeText(context, "验证码已发送，请稍后查看", Toast.LENGTH_SHORT).show();
				proDlg.dismiss();
				changeVerify(phoneNo);
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
				} else {
					showError(failReason);
				}
			}
		};
		HttpApi.getInstance().requestBindingVerifyNo(phone, callback);
	}

	/**
	 * 绑定手机号
	 */
	private void binding(String notifyNo) {
		proDlg = HYProgressDlg.show(context, "正在绑定手机号…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				Toast.makeText(context, "手机号绑定成功", Toast.LENGTH_SHORT).show();
				proDlg.dismiss();

				GameSDKApplication.getInstance()
						.setIsbind(GameSDKApplication.getInstance().getUserFromPref(context).getUserName());
				GameSDKApplication.getInstance().setIsneedbind(false);
				if ("5".equals(from)) {// 来自个人中心
					new BindDialog(context).show();
				} else if ("66".equals(from)) {// 来自PerCenterDialog，不带三方绑定
					Intent intent = new Intent(context, HYInitActivity.class);
					intent.putExtra("from", "moreByFloatService");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
				finishDialog();
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
				} else {
					showError(failReason);
				}
			}
		};
		HttpApi.getInstance().bindingPhone(phoneNo, notifyNo, callback);
	}

	public void showError(String msgError) {
		tv_error.setText(msgError);
		tv_error.setVisibility(View.VISIBLE);
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			btn_re_send.setText("重新获取");
			btn_re_send.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			btn_re_send.setClickable(false);
			btn_re_send.setText("剩余" + millisUntilFinished / 1000 + "s");
		}
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (receiver != null) {
			try {
				context.unregisterReceiver(receiver);
			} catch (Exception e) {
			}
			receiver = null;
		}
	}
}
