package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * 购买vip的vebview产品展示页面
 * 
 * @author 徐琦
 */
public class WebViewFullscreenActivity extends Activity {
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hy_activity_webview_fullscreen);
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.setBackgroundColor(0); // 设置背景色
		mWebView.addJavascriptInterface(new WebViewFullscreenSInterface(),
				"yksdkjs");
		String url = GameSDKApplication.getInstance().getVipUrl();
		mWebView.loadUrl(url);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url); // 在当前的webview中跳转到新的url
				return true;
			}
		});
	}

	@Override
	public void finish() {
		overridePendingTransition(0, 0);
		Util.sendBroadcast(this);
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			finish();
			break;
		default:
			break;
		}
	}

	class WebViewFullscreenSInterface {
		@JavascriptInterface
		public void finish() {
			WebViewFullscreenActivity.this.finish();
		}

		@JavascriptInterface
		public void payVip() {
			startBuyVip();
		}
	}

	private void startBuyVip() {
		Intent intent = new Intent(this, WebViewVipActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 0);
	}
}
