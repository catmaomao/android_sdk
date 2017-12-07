/**
 * LoginDialog.java
 * com.heyijoy.gamesdk.widget
 *
 *   		 2014-2-21 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author msh
 * @since 实名认证提示语
 */
public class CertificationPayTipDialog extends Dialog {
	private HYDialog dlg;
	private Button btn_sure;
	private Context context = null;
	private HYCallBack method;

	public CertificationPayTipDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CertificationPayTipDialog(Context context, HYCallBack method) {
		super(context);
		this.context = context;
		this.method = method;
	}

	@Override
	public void show() {
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.save_account, null);
		dlg = new HYDialog(context, R.style.dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setCancelable(false);
		dlg.setContentView(loginLayout1);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		Button btn_sure = (Button) loginLayout1.findViewById(R.id.btn_sure);
		TextView tv_tips = (TextView) loginLayout1.findViewById(R.id.tv_tips);
		ImageView iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setVisibility(View.VISIBLE);
		tv_title_text.setText("实名认证");
		btn_sure.setText("认证");
		tv_tips.setText(
				"根据国家规定，未认证的用户不能支付消费，请您点击“认证”或在悬浮标内-个人中心处填写您的有效身份证信息，完成认证。");

		btn_sure = (Button) loginLayout1.findViewById(R.id.btn_sure);
		btn_sure.setClickable(true);
		btn_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishDialog();
				new CertificationDialog(context,method,"tip").show();
			}
		});
		
//		取消弹框
		iv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishDialog();

				if (method != null) {
					method.onSuccess(null);
				}
			}
		});
		
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return false;
			}
		});

		dlg.show();
	}

	public void finishDialog() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
	}
}
