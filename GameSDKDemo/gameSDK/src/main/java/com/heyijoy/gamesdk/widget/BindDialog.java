package com.heyijoy.gamesdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.activity.HYInitActivity;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.BindingPhoneDialog;
import com.heyijoy.gamesdk.widget.HYDialog;
import com.heyijoy.gamesdk.R;

/**
 * 绑定弹框
 * 
 * @author mashaohu
 *
 */
public class BindDialog extends Dialog {
	private Context context;
	private HYDialog dlg;
	private Button btn_back;
	private ImageView iv_nav_icon_right;
	private TextView tv_title_text, tv_bind_status;
	private RelativeLayout rl_bindthrid_account, rl_binding_phone;
	private HYCallBack method;

	public BindDialog(Context context, HYCallBack method) {
		super(context);
		this.context = context;
		this.method = method;
	}

	public BindDialog(Context context) {
		super(context);
		this.context = context;
	}

	public void show() {
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.hy_bind_dialog, null);
		dlg = new HYDialog(context, R.style.pre_dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);

		rl_binding_phone = (RelativeLayout) loginLayout1.findViewById(R.id.rl_binding_phone);
		iv_nav_icon_right = (ImageView) loginLayout1.findViewById(R.id.iv_nav_icon_right);
		tv_bind_status = (TextView) loginLayout1.findViewById(R.id.tv_bind_status);
		rl_bindthrid_account = (RelativeLayout) loginLayout1.findViewById(R.id.rl_bindthrid_account);
		btn_back = (Button) loginLayout1.findViewById(R.id.btn_back);
		tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		tv_title_text.setText(HYConstant.TITLE_CENTER);
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

		setOnclick();

		boolean isneedbind = GameSDKApplication.getInstance().isIsneedbind();
		Log.e("HeyiJoySDK", "isneedbind=" + isneedbind);
		if (isneedbind) {
			tv_bind_status.setText("未绑定");
			rl_binding_phone.setClickable(true);
			iv_nav_icon_right.setVisibility(View.VISIBLE);
		} else {
			rl_binding_phone.setClickable(false);
			iv_nav_icon_right.setVisibility(View.GONE);
			String phone = GameSDKApplication.getInstance().getPhonePre();
			tv_bind_status.setText(phone);
		}
	}

	/**
	 * 点击事件
	 */
	private void setOnclick() {

		// 绑定三方账号
		rl_bindthrid_account.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String userLoginType = GameSDKApplication.getInstance().getUserLoginType();
				Log.e("HeyiJoySDK", "userLoginType=" + userLoginType);
				if (!TextUtils.isEmpty(userLoginType)) {
					Toast.makeText(context, "您已有三方账号，无需绑定", Toast.LENGTH_SHORT).show();
				} else {
					dlg.dismiss();
					bindingThrid();
				}
			}
		});

		// 绑定手机号
		rl_binding_phone.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				bindingPhone();
			}
		});

		// 返回
		btn_back.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				Intent intent = new Intent(context, HYInitActivity.class);
				intent.putExtra("from", "moreByFloatService");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
	}

	/**
	 * 绑定三方账号
	 */
	protected void bindingThrid() {
		new BindingThridDialog(context).show();
	}

	/**
	 * 绑定手机
	 */
	private void bindingPhone() {
		BindingPhoneDialog bindingDlg = new BindingPhoneDialog(context, "5");
		bindingDlg.show();
	}

	// "no":不发送广播，"yes":发送广播，"preCenter":发送广播，并传递标记位
	public void finishDialog(String str) {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}

		if ("yes".equals(str)) {
			Util.sendBroadcast(context);
		}
		if (method != null) {
			method.onSuccess(null);
		}
	}
}
