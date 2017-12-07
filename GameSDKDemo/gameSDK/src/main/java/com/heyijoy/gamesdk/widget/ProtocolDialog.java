/**
 * LoginDialog.java
 * com.heyijoy.gamesdk.widget
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.util.Util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author shaohuma
 * @Date 用户协议
 */
public class ProtocolDialog extends Dialog {
	private HYDialog dlg;
	private HYCallBack mcallback;// 传入的函数
	private Context context = null;
	private WebView webView;
	private TextView tv_refuse, tv_receive;

	public ProtocolDialog(Context context, HYCallBack mcallback) {
		super(context, R.style.ProtocolDialog);
		this.context = context;
		this.mcallback = mcallback;
	}

	@Override
	public void show() {
		if (context == null) {
			return;
		}
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_hy_protocol, null);
		dlg = new HYDialog(context, R.style.ProtocolDialog);
		webView = (WebView) loginLayout1.findViewById(R.id.webview_protocol);
		tv_refuse = (TextView) loginLayout1.findViewById(R.id.tv_refuse);
		tv_receive = (TextView) loginLayout1.findViewById(R.id.tv_receive);

		webView.loadUrl(HYConstant.AGREEMENT);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});

		tv_refuse.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (dlg != null && dlg.isShowing()) {
					dlg.dismiss();
				}
			}
		});

		// 接受协议
		tv_receive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 此时将注册成功的回调返回，通知进入游戏
				if (dlg != null && dlg.isShowing()) {
					dlg.dismiss();
				}
				if (Util.hasInternet(context)) {
					mcallback.onSuccess(null);
				}
			}
		});

		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);
		dlg.show();

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		// window.setWindowAnimations(R.style.ProtocolDialog);
		window.getDecorView().setPadding(200, 0, 200, 0);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

	}

}
