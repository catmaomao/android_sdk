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
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 手机号注册
 * 
 * @author mashaohu
 *
 */
public class PhoneRegisterDialog extends Dialog {
	private TimeCount time;// 计时器
	private HYProgressDlg proDlg;
	private HYDialog dlg;
	private HYCallBack method;
	private Button btn_find_pwd;// 重置密码
	private Button btn_re_send;
	private Context context = null;
	private TextView tv_error;
	private String phoneNo;

	private ImageView iv_delete;
	private EditText ed_verify;
	private EditText ed_phone;
	private EditText ed_new_pwd, et_new_pwd_again;

	public PhoneRegisterDialog(Context context, HYCallBack method) {
		super(context);
		this.context = context;
		this.method = method;
	}

	@Override
	public void show() {
		changeFindPwdView();
	}

	private void changeFindPwdView() {
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_phone_register, null);
		dlg = new HYDialog(context, R.style.dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);
		tv_error = (TextView) loginLayout1.findViewById(R.id.tv_error);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		tv_title_text.setText("手机注册");
		iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setVisibility(View.VISIBLE);
		ed_verify = (EditText) loginLayout1.findViewById(R.id.et_verify_no);
		ed_phone = (EditText) loginLayout1.findViewById(R.id.et_phone);
		ed_new_pwd = (EditText) loginLayout1.findViewById(R.id.et_new_pwd);
		et_new_pwd_again = (EditText) loginLayout1.findViewById(R.id.et_new_pwd_again);
		btn_re_send = (Button) loginLayout1.findViewById(R.id.btn_re_send);
		btn_re_send.setText("发送验证码");
		btn_re_send.setClickable(true);
		btn_re_send.setOnClickListener(new View.OnClickListener() {// 发送验证码
			@Override
			public void onClick(View arg0) {
				phoneNo = ed_phone.getText().toString();
				if (TextUtils.isEmpty(phoneNo)) {
					showError("请填写手机号码");
				} else if (!Util.isMobileNO(phoneNo)) {
					showError("手机号码错误");
				} else {
					getVerifyCode(phoneNo);
				}
			}
		});
		btn_find_pwd = (Button) loginLayout1.findViewById(R.id.btn_find_pwd);
		btn_find_pwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phoneNo = ed_phone.getText().toString();
				String verify_code = ed_verify.getText().toString();
				String newPassword = ed_new_pwd.getText().toString();
				String newPasswordAgain = et_new_pwd_again.getText().toString();
				if (TextUtils.isEmpty(phoneNo)) {
					showError("请填写手机号码");
				} else if (verify_code.length() == 0) {
					showError("请先获取验证码");
				} else if (verify_code.length() != 6) {
					showError("验证码错误");
				} else if (!Util.isRightPwd(newPassword)) {
					tv_error.setText("密码请输入6~16位字母+数字组合");
					tv_error.setVisibility(View.VISIBLE);
				} else if (Util.isAllNum(newPassword)) {
					tv_error.setText("为弱密码，密码不能为纯数字");
					tv_error.setVisibility(View.VISIBLE);
				} else if (Util.isAllCharacter(newPassword)) {
					tv_error.setText("为弱密码，密码不能为纯特殊字符");
					tv_error.setVisibility(View.VISIBLE);
				} else if (!newPassword.equals(newPasswordAgain)) {
					showError("两次输入的密码不一致");
				} else {
					phoneRegister(phoneNo, verify_code, newPassword, newPasswordAgain);
				}
			}
		});

		iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishDialog();
				new NickNameRegDialog(context, method, true).show();
			}
		});

		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					finishDialog();
				}
				return false;
			}
		});

		dlg.show();
	}

	public void showError(String msgError) {
		tv_error.setText(msgError);
		tv_error.setVisibility(View.VISIBLE);
	}

	public void finishDialog() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
	}

	/**
	 * 获取重置密码的验证码
	 */
	private void getVerifyCode(String phone) {
		proDlg = HYProgressDlg.show(context, "验证码发送中…");

		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				Toast.makeText(context, "验证码已发送，请查看", Toast.LENGTH_SHORT).show();
				proDlg.dismiss();
				time = new TimeCount(HYConstant.TIME_RE_SEND_VERIFY_WAIT, 1000);// 构造CountDownTimer对象
				time.start();
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
				} else {
					tv_error.setText(failReason);
					tv_error.setVisibility(View.VISIBLE);
				}
			}
		};
		HttpApi.getInstance().requestPwdVerifyNo(phone, callback);
	}

	/**
	 * 手机号注册并自动登录
	 */
	private void phoneRegister(final String phone, String verify_code, final String newPassword,
			final String newPasswordagain) {
		proDlg = HYProgressDlg.show(context, "正在注册…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {// 修改密码成功，自动登录
				proDlg.dismiss();
				dlg.dismiss();
				final User user = (User) bean;
				// GameSDKApplication.getInstance().saveShareUser(user);
				Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();

				String certifitationStatus = GameSDKApplication.getInstance().getCertifitationStatus();// 后台打开实名认证开关
				if ("on".equals(certifitationStatus)) {
					// 弹出实名认证提示框
					new CertificationTipDialog(context, new HYCallBack() {

						@Override
						public void onSuccess(Bean bean) {
							method.onSuccess(user);
						}

						@Override
						public void onFailed(int code, String failReason) {

						}
					}).show();
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
				} else {
					tv_error.setText(failReason);
					tv_error.setVisibility(View.VISIBLE);
				}
			}
		};

		HttpApi.getInstance().phoneRegisterApi(phoneNo, verify_code, newPassword, newPasswordagain, callback);
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			btn_re_send.setText("重新验证");
			btn_re_send.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			btn_re_send.setClickable(false);
			btn_re_send.setText(millisUntilFinished / 1000 + "秒");
		}
	}
}
