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
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

/**
 * @author shaohuma
 * @Date 201-05-08 实名认证
 */
public class CertificationDialog extends Dialog {
	private HYDialog dlg;
	private HYCallBack mcallback;// 传入的函数
	private Context context = null;
	private EditText identify_name;
	private EditText identify_certification;
	private Button btn_sure_modify;
	private TextView tv_error;
	private ImageView iv_delete;
	private LinearLayout ll_certification, ll_certification_result;
	private String from = "";

	public CertificationDialog(Context context, HYCallBack mcallback) {
		super(context);
		this.context = context;
		this.mcallback = mcallback;
	}

	public CertificationDialog(Context context, HYCallBack mcallback, String from) {
		super(context);
		this.context = context;
		this.mcallback = mcallback;
		this.from = from;
	}

	@Override
	public void show() {
		if (context == null) {
			return;
		}
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_hy_certification, null);
		dlg = new HYDialog(context, R.style.dialog);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		ll_certification_result = (LinearLayout) loginLayout1.findViewById(R.id.ll_certification_result);
		ll_certification = (LinearLayout) loginLayout1.findViewById(R.id.ll_certification);
		tv_title_text.setText(HYConstant.TITLE_CERTIFICATION);
		iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setVisibility(View.VISIBLE);
		tv_error = (TextView) loginLayout1.findViewById(R.id.tv_error);

		identify_name = (EditText) loginLayout1.findViewById(R.id.et_pwd);
		identify_certification = (EditText) loginLayout1.findViewById(R.id.et_new_pwd);
		btn_sure_modify = (Button) loginLayout1.findViewById(R.id.tv_sure_modify);

		identify_name.addTextChangedListener(new TextWatcher() {
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

		identify_certification.addTextChangedListener(new TextWatcher() {
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

		// 确认实名认证
		btn_sure_modify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String identifyName = identify_name.getText().toString();
				String identifyCertification = identify_certification.getText().toString();
				if (TextUtils.isEmpty(identifyName)) {
					showError("请输入真实姓名");
				} else if (TextUtils.isEmpty(identifyCertification)) {
					showError("请输入真实身份证号");
				} else {
					certification(identifyName, identifyCertification);
				}
			}
		});

		// dissmis弹框
		iv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("tip".equals(from)) {
					mcallback.onSuccess(null);
				}else{
					Intent intent = new Intent(context, HYInitActivity.class);
					intent.putExtra("from", "moreByFloatService");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
				if (dlg != null && dlg.isShowing()){
					dlg.dismiss();
				}
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

		boolean userCertifitationStatus = GameSDKApplication.getInstance().getUserCertifitationStatus();
		if (userCertifitationStatus) {
			ll_certification.setVisibility(View.GONE);
			tv_error.setVisibility(View.GONE);
			ll_certification_result.setVisibility(View.VISIBLE);
		}
	}

	public void showError(String msgError) {
		tv_error.setText(msgError);
		tv_error.setVisibility(View.VISIBLE);
	}

	/**
	 * 实名验证
	 */
	private void certification(String identifyName, String identifyCertification) {
		final HYProgressDlg proDlg = HYProgressDlg.show(context, "", "认证中…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				proDlg.dismiss();
				GameSDKApplication.getInstance().saveUserCertifitationStatus(true);
				ll_certification.setVisibility(View.GONE);
				tv_error.setVisibility(View.GONE);
				ll_certification_result.setVisibility(View.VISIBLE);
				if (!"tip".equals(from)) {
					mcallback.onSuccess(bean);
				}
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				// mcallback.onFailed(code, failReason);
				showError(failReason);
			}
		};
		HttpApi.getInstance().apiCertification(identifyName, identifyCertification, callback);
	}
}
