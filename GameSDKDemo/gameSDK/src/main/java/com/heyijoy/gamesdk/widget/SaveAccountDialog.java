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
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author msh
 * @since Ver 1.1 快速登录绑定账号到相册提示语
 */
public class SaveAccountDialog extends Dialog {
	private HYDialog dlg;
	private Button btn_sure;
	private Context context = null;
	private HYCallBack method;
	private String from;// 从何处跳转到绑定页面 "HINT" "LOGIN" "NICKNAME_REG"
	private User user;

	public SaveAccountDialog(Context context, String from) {
		super(context);
		this.context = context;
		this.from = from;
	}

	public SaveAccountDialog(Context context, User user, HYCallBack method, String from) {
		super(context);
		this.context = context;
		this.method = method;
		this.from = from;
		this.user = user;
	}

	@Override
	public void show() {
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.save_account, null);
		dlg = new HYDialog(context, R.style.dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setCancelable(false);
		dlg.setContentView(loginLayout1);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		tv_title_text.setText(HYConstant.HINT_INFO);

		btn_sure = (Button) loginLayout1.findViewById(R.id.btn_sure);
		btn_sure.setClickable(true);
		btn_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 保存账号密码到相册
				try {
					finishDialog();
					// User shareUser =
					// GameSDKApplication.getInstance().getShareUser();
					LinearLayout inflate = (LinearLayout) getLayoutInflater().inflate(R.layout.hy_saveaccount, null);
					TextView save_username = (TextView) inflate.findViewById(R.id.tv_save_username);
					TextView save_pwd = (TextView) inflate.findViewById(R.id.tv_save_pwd);
					if (user != null) {
						save_username.setText(user.getUserName());
						save_pwd.setText(user.getPicPwd());
					}else{
						save_username.setText("账号保存异常");
					}

					inflate.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
							MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
					inflate.layout(0, 0, inflate.getMeasuredWidth(), inflate.getMeasuredHeight());
					inflate.setDrawingCacheEnabled(true);
					inflate.buildDrawingCache();
					Bitmap bitmap = Bitmap.createBitmap(inflate.getDrawingCache());
					inflate.setDrawingCacheEnabled(false);
					Util.saveImageToGallery(context, bitmap);// 保存操作
				} catch (Exception e) {
					e.printStackTrace();
					if (method != null) {
						method.onFailed(HYConstant.EXCEPTION_CODE, e.getMessage());
					}
				}
			}
		});

		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() ==
				// KeyEvent.ACTION_DOWN) {
				// finishDialog();
				// }
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

}
