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
import com.heyijoy.gamesdk.activity.HYQQLoginActivity;
import com.heyijoy.gamesdk.activity.HYWeiBoLoginActivity;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.util.Util;
import com.sina.weibo.sdk.WeiboAppManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author msh
 * @since 绑定三方账号
 */
public class BindingThridDialog extends Dialog {
	private HYDialog dlg;
	private Button btn_sure;
	private Context context = null;

	public BindingThridDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public void show() {
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.binding_thrid_dialog, null);
		dlg = new HYDialog(context, R.style.dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setCancelable(false);
		dlg.setContentView(loginLayout1);
		TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		LinearLayout ll_bing_qq = (LinearLayout) loginLayout1.findViewById(R.id.ll_bing_qq);
		LinearLayout ll_bing_wx = (LinearLayout) loginLayout1.findViewById(R.id.ll_bing_wx);
		LinearLayout ll_bing_weibo = (LinearLayout) loginLayout1.findViewById(R.id.ll_bing_weibo);
		ImageView iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setVisibility(View.VISIBLE);
		tv_title_text.setText("绑定账号");

		// 绑定qq
		ll_bing_qq.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				boolean qqClientAvailable = Util.isQQClientAvailable(context);
				if (qqClientAvailable) {
					bindQQ();
				} else {
					Toast.makeText(context, "请先安装QQ", Toast.LENGTH_SHORT).show();
				}

			}
		});

		// 绑定微信
		ll_bing_wx.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				bindWX();
			}
		});

		// 绑定微博
		ll_bing_weibo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				boolean hasWbInstall = WeiboAppManager.getInstance(context).hasWbInstall();
				if (hasWbInstall) {
					bindWB();
				} else {
					Toast.makeText(context, "请先安装微博客户端", Toast.LENGTH_SHORT).show();
				}

			}
		});

		// 取消弹框
		iv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new BindDialog(context).show();
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

	/**
	 * 绑定微博
	 */
	protected void bindWB() {
		if (Util.isFastDoubleClick())
			return;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HYConstant.WEIBO_LOGIN_BIND + GameSDKApplication.getInstance().getAppid());
		GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				synchronized (this) {
					// if (dlg != null && dlg.isShowing()) {
					// dlg.dismiss();
					// }
					GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
					String token = intent.getStringExtra(HYConstant.WEIBO_LOGIN);
					User user = new User();
					user.setThirdparty_credential(token);
					user.setThirdparty("weibo");
					bind(user);
				}
			}
		}, intentFilter);

		Intent intent = new Intent(context, HYWeiBoLoginActivity.class);
		intent.putExtra("bind", true);
		context.startActivity(intent);
	}

	/**
	 * 绑定微信
	 */
	protected void bindWX() {
		if (Util.isFastDoubleClick())
			return;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HYConstant.WX_LOGIN + GameSDKApplication.getInstance().getAppid());
		GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				synchronized (this) {
					// if (dlg != null && dlg.isShowing()) {
					// dlg.dismiss();
					// }
					GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
					String token = intent.getStringExtra(HYConstant.WX_LOGIN);
					User user = new User();
					user.setThirdparty_credential(token);
					user.setThirdparty("wechat");
					bind(user);
				}
			}
		}, intentFilter);

		Util.wxLogin(context);
	}

	/**
	 * 绑定qq
	 */
	protected void bindQQ() {
		if (Util.isFastDoubleClick())
			return;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HYConstant.QQ_LOGIN_BIND + GameSDKApplication.getInstance().getAppid());
		GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				synchronized (this) {
					// if (dlg != null && dlg.isShowing()) {
					// dlg.dismiss();
					// }
					GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
					String token = intent.getStringExtra(HYConstant.QQ_LOGIN);
					User user = new User();
					user.setThirdparty_credential(token);
					user.setThirdparty("qq");
					bind(user);
				}
			}
		}, intentFilter);

		Intent intent = new Intent(context, HYQQLoginActivity.class);
		intent.putExtra("bind", true);
		context.startActivity(intent);

	}

	/**
	 * 开始绑定三方账号流程
	 * 
	 * @param user
	 */
	protected void bind(final User user) {

		final HYProgressDlg proDlg = HYProgressDlg.show(context, "合乐智趣", "正在绑定…");
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				// Util.sendBroadcast(context);
				dlg.dismiss();
				proDlg.dismiss();
				GameSDKApplication.getInstance().setUserLoginType(user.getThirdparty());
				Toast.makeText(context, "绑定成功", Toast.LENGTH_SHORT).show();
				new BindDialog(context).show();
			}

			@Override
			public void onFailed(int code, String failReason) {
				proDlg.dismiss();
				if ((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))
						|| (HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))) {
					Toast.makeText(context, "绑定失败！" + failReason, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, failReason, Toast.LENGTH_LONG).show();
				}
			}
		};

		HttpApi.getInstance().bindapi(user, callback);
	}

	public void finishDialog() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
	}
}
