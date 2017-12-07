/**
 * LoginDialog.java
 * com.heyijoy.gamesdk.widget
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.activity.HYInitActivity;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author shaohuma
 * @Date 2016-08-27 修改密码
 */
public class ModifyPwdDialog extends Dialog {
	private HYDialog dlg;
	private HYCallBack mcallback;// 传入的函数
	private int flag = 0;// 跳转 登陆对话框 的 标记位，0：默认值；1：快速注册；2：手机号注册；3：用户名注册
	private Context context = null;
	private EditText ed_pwd;
	private EditText ed_new_pwd, et_new_pwd_again;
	private Button btn_sure_modify;
	private TextView tv_error, tv_find_pwd;
	private ImageView iv_delete;
	private boolean canBackButton;
	private boolean isShowLoginFailed;
	private User user;

	public ModifyPwdDialog(Context context, HYCallBack mcallback, boolean backButton, int flag) {
		super(context);
		this.context = context;
		this.mcallback = mcallback;
		this.canBackButton = backButton;
		this.flag = flag;
	}

	@Override
	public void show() {
		if (context == null) {
			return;
		}
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_hy_modify_pwd, null);
		dlg = new HYDialog(context, R.style.dialog);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		tv_title_text.setText(HYConstant.TITLE_MODIFY_PWE);
		tv_find_pwd = (TextView) loginLayout1.findViewById(R.id.tv_find_pwd);
		iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setVisibility(View.VISIBLE);
		tv_error = (TextView) loginLayout1.findViewById(R.id.tv_error);

		ed_pwd = (EditText) loginLayout1.findViewById(R.id.et_pwd);
		ed_new_pwd = (EditText) loginLayout1.findViewById(R.id.et_new_pwd);
		et_new_pwd_again = (EditText) loginLayout1.findViewById(R.id.et_new_pwd_again);
		btn_sure_modify = (Button) loginLayout1.findViewById(R.id.tv_sure_modify);

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

		ed_new_pwd.addTextChangedListener(new TextWatcher() {
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
		et_new_pwd_again.addTextChangedListener(new TextWatcher() {
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

		// 确认修改密码
		btn_sure_modify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = GameSDKApplication.getInstance().getUserName();
				String pwd = ed_pwd.getText().toString();
				String new_pwd = ed_new_pwd.getText().toString();
				String new_pwd_again = et_new_pwd_again.getText().toString();
				if (!Util.isRightUserName(userName)) {
					showError("用户名长度错误");
				} else if (!Util.isRightPwd(pwd)) {
					showError("旧密码位数错误");
				} else if (!Util.isRightPwd(new_pwd)) {
					showError("密码的长度为5~16位");
				} else if (!new_pwd.equals(new_pwd_again)) {
					showError("新密码输入不一致");
				} else {
					User user = new User();
					user.setUserName(userName);
					user.setPassword(Util.md5(pwd));
					user.setNew_password(Util.md5(new_pwd));
					modifyPwd(user, true, false);
				}
			}
		});

		//忘记密码
		tv_find_pwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HYCallBack callback = new HYCallBack() {

					@Override
					public void onSuccess(Bean bean) {
						if (dlg != null && dlg.isShowing())
							dlg.dismiss();
					}

					@Override
					public void onFailed(int code, String failReason) {

					}
				};
				FindPasswordDialog finddlg = new FindPasswordDialog(context, callback);
				finddlg.show();
			}
		});

		// dissmis修改密码弹框
		iv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dlg != null && dlg.isShowing()){
					dlg.dismiss();
				}
				Intent intent = new Intent(context, HYInitActivity.class);
				intent.putExtra("from", "moreByFloatService");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
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
		
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);
		dlg.show();
	}

	public void showError(String msgError) {
		tv_error.setText(msgError);
		tv_error.setVisibility(View.VISIBLE);
	}

	/**
	 * 修改密码逻辑
	 */
	private void modifyPwd(final User user, final boolean isPWD, final boolean isNewUser) {
		final HYProgressDlg proDlg = HYProgressDlg.show(context, "合乐智趣游戏帐号  " + user.getUserName(), "正在修改密码…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				dlg.dismiss();
				proDlg.dismiss();
				final User userRe = (User) bean;
				userRe.setPassword(user.getNew_password());
				GameSDKApplication.getInstance().saveShareUser(userRe);

				mcallback.onSuccess(bean);
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				mcallback.onFailed(code, failReason);
				showError(failReason);
			}
		};
		HttpApi.getInstance().modifyPwd(user, callback);
	}
}
