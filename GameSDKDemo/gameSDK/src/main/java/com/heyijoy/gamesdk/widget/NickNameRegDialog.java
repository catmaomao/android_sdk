/**
 * LoginDialog.java
 * com.heyijoy.gamesdk.widget
 * Copyright (c) 2014, TNT All Rights Reserved.
 */

package com.heyijoy.gamesdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.HYLoginFailReason;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.util.Util;

/**
 * @author msh
 * @since Ver 1.1
 * @Date 2014-2-21 上午10:06:37
 */
public class NickNameRegDialog extends Dialog {
	private HYDialog dlg;
	private HYCallBack method;// 传入的函数
	private Context context = null;
	private EditText ed_userName;
	private EditText ed_pwd;
	private TextView tv_login;
	private Button btn_reg;
	private TextView tv_hink;
	private TextView tv_error;
	private boolean isfromClick;
	private boolean cbChecked = true;

	public NickNameRegDialog(Context context, HYCallBack method, boolean isfromClick) {
		super(context);
		this.context = context;
		this.method = method;
		this.isfromClick = isfromClick;
	}

	@Override
	public void show() {
		if (context == null) {
			return;
		}
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_hy_reg_name, null);
		dlg = new HYDialog(context, R.style.dialog);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		TextView tv_phone_register = (TextView) loginLayout1.findViewById(R.id.tv_phone_register);
		TextView tv_protocol = (TextView) loginLayout1.findViewById(R.id.tv_protocol);
		CheckBox cbProtocol = (CheckBox) loginLayout1.findViewById(R.id.cb_protocol);
		tv_title_text.setText(HYConstant.TITLE_REG);

		tv_protocol.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, WebViewActivity.class);
				intent.putExtra("url", HYConstant.AGREEMENT);
				context.startActivity(intent);
			}
		});

		cbProtocol.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cbChecked = true;
				} else {
					cbChecked = false;
				}
			}
		});

		tv_error = (TextView) loginLayout1.findViewById(R.id.tv_error);
		tv_error.setText("验证有点问题，请使用用户名注册");

		ed_userName = (EditText) loginLayout1.findViewById(R.id.et_username);
		ed_pwd = (EditText) loginLayout1.findViewById(R.id.et_pwd);

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
			}

		});

		if (!isfromClick) {
			tv_error.setVisibility(View.VISIBLE);
//			FadeAnimation tvErrorAnimation = new FadeAnimation(7000, 1, true);
//			tvErrorAnimation.start(tv_error);
		} else {
			tv_error.setVisibility(View.GONE);
		}

		// 跳转到合乐智趣账号登录dialog
		tv_login = (TextView) loginLayout1.findViewById(R.id.tv_login);
		tv_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				LoginDialog loginDlg = new LoginDialog(context, method, true, 0);
				loginDlg.show();
			}
		});
		
		/**
		 * 手机号注册
		 */
		tv_phone_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				PhoneRegisterDialog phoneRegister = new PhoneRegisterDialog(context, method);
				phoneRegister.show();
			}
		});

		// 用户名和密码注册
		btn_reg = (Button) loginLayout1.findViewById(R.id.btn_reg);
		btn_reg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = ed_userName.getText().toString();
				String pwd = ed_pwd.getText().toString();
				if (userName == null || userName.length() == 0) {
					showError("账号不能为空");
				} else if (!Util.isRightUserName(userName)) {
					showError("账号请使用3~20位非纯数字");
				} else if (Util.isAllNum(userName)) {
					showError("账号不能为纯数字");
				} else if (Util.isContainChinese(userName)) {
					showError("注册账号不能含有中文");
				}  else if (!Util.isRightPwd(pwd)) {
					showError("密码的长度为5~16位");
				}  else if (!Util.isAllCharacter(pwd)) {
					showError("密码不能含有特殊字符");
				} else if (userName.equals(pwd)) {
					showError("密码不能与登录名相同");
				} else if (!cbChecked) {
					showError("请同意用户协议");
				} else {
					regByNickName(userName, Util.md5(pwd));
				}
			}
		});
		tv_hink = (TextView) loginLayout1.findViewById(R.id.reg_tv_hink);
		tv_hink.setVisibility(View.GONE);
		tv_hink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		tv_hink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent i = new Intent(context, WebViewActivity.class);
					i.putExtra("url", HYConstant.AGREEMENT_URL);
					context.startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 物理返回键，由dialog处理
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					dlg.dismiss();
					method.onFailed(HYConstant.EXCEPTION_CODE, HYLoginFailReason.CANCELED);
					// if (GameSDKApplication.getInstance().isPhoneRegOpen()) {
					// new RegDialog(context, method).changeViewMobile(false);
					// } else {
					// if (TelephonyManager.SIM_STATE_READY == Util
					// .isHasSim(context)) {
					// new RegDialog(context, method).show();
					// }
					// }
				}
				return false;
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);
		dlg.show();
	}

	public void showError(String msgError) {
		tv_error.setText(msgError);
		tv_error.setVisibility(View.VISIBLE);
	}

	/**
	 * 用户名注册逻辑
	 */
	private void regByNickName(final String nickName, final String pwd) {
		final HYProgressDlg proDlg = HYProgressDlg.show(context, "合乐智趣游戏帐号  " + nickName, "正在注册…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				dlg.dismiss();
				proDlg.dismiss();
				final User user = (User) bean;
				GameSDKApplication.getInstance().saveShareUser(user);
				GameSDKApplication.getInstance().setIsneedbind(true);
				Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
				
				String certifitationStatus = GameSDKApplication.getInstance().getCertifitationStatus();// 后台打开实名认证开关
				if ("on".equals(certifitationStatus)) {
					// 弹出实名认证提示框
					new CertificationTipDialog(context, new HYCallBack() {

						@Override
						public void onSuccess(Bean bean) {
//							afterRegist(user);
							method.onSuccess(user);
						}

						@Override
						public void onFailed(int code, String failReason) {
							method.onSuccess(user);
						}
					}).show();
				} else {
//					afterRegist(user);
					method.onSuccess(user);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();

				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					Toast.makeText(context, "注册失败" + failReason, Toast.LENGTH_LONG).show();
				} else {
					tv_error.setText(failReason);
					tv_error.setVisibility(View.VISIBLE);
				}
			}
		};
		HttpApi.getInstance().regByName("", nickName, pwd, callback);
	}

	public void afterRegist(final User user) {
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				Toast.makeText(context, user.getUserName() + HYConstant.Toast_New_User, Toast.LENGTH_SHORT).show();
				method.onSuccess(user);
			}

			@Override
			public void onFailed(int code, String failReason) {
			}
		};
		Util.showBindingDlg(context, callback, "1");
	}
}
