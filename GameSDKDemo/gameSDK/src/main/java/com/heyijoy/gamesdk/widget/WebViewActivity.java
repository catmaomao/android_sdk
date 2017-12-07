package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.memfloat.FloatViewService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 用于显示连接的页面 需要传递的参数有 StringExtra("url")*
 * 
 * @author msh
 * 
 */
public class WebViewActivity extends Activity {

	// private final static String TAG = "WebViewActivity";
	private WebView mWebView;
	private ProgressBar mprogreBar;// 上方的进度细条
	private Context mContext;
	private String url;
	private String pay_type;
	private Boolean isTenPay, isRecharge;
	private ImageView btn_back;
	private WebSettings settings;
	private String channel_params;

	/**
	 * intent -------string类型的 url, boolean类型的getCookie
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hy_activity_webview);
		mContext = this;
		btn_back = (ImageView) findViewById(R.id.back);
		mWebView = (WebView) findViewById(R.id.webView);
		mprogreBar = (ProgressBar) findViewById(R.id.progress);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				WebViewActivity.this.finish();
				FloatViewService.isShowFloat = true;// 退出支付页面则打开悬浮窗
				sendBroadcast();
			}
		});
		Intent intent = getIntent();
		isRecharge = intent.getBooleanExtra("isRecharge", false);
		isTenPay = intent.getBooleanExtra("isTenPay", false);
		url = intent.getStringExtra("url");
		channel_params = intent.getStringExtra("params");
		pay_type = intent.getStringExtra("pay_type");
		if (url != null && !"".equals(url) && URLUtil.isNetworkUrl(url)) {
			// 有网
			if (Util.hasInternet(mContext)) {
				// 可缩放
				settings = mWebView.getSettings();
//				settings.setBuiltInZoomControls(true);
//				settings.setSupportZoom(true);
//				settings.setUseWideViewPort(true);

				settings.setDomStorageEnabled(true);

				// 客串文件
				settings.setAllowFileAccess(true);
				// mWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
				if (Build.VERSION.SDK_INT >= 11) {
					settings.setEnableSmoothTransition(true);
				}
				// 可解析js
				settings.setJavaScriptEnabled(true);
				settings.setPluginState(PluginState.ON);
				
				if("unionpay".equals(pay_type)){
//					银联支付
					mWebView.postUrl(url, channel_params.getBytes());
				}else{
					mWebView.loadUrl(url);
				}
				
				// 展示进度以及连接不成功的提示语
				mWebView.setWebViewClient(new WebViewClient() {
					@Override
					public void onPageFinished(WebView view, String url) {
						mprogreBar.setVisibility(View.GONE);
						super.onPageFinished(view, url);
					}

					@Override
					public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
						mprogreBar.setVisibility(View.GONE);
						super.onReceivedError(view, errorCode, description, failingUrl);
					}

					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
//						if (url == null || "".equals(url)) {
//							if("unionpay".equals(pay_type)){
////								银联支付
//								view.postUrl(url, channel_params.getBytes());
//							}else{
								view.loadUrl(url);
//							}
							return true;
//						}
//						return true;
					}

					@Override
					public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
						handler.proceed();
					}

					@Override
					public void onPageStarted(WebView view, String url, Bitmap favicon) {
						if (url.contains("www.youku_tenpay.com")) {
							sendResultBraodcast(url);
							return;
						}
						super.onPageStarted(view, url, favicon);
					}

					@Override
					public void onLoadResource(WebView view, String url) {
						super.onLoadResource(view, url);
					}
				});
				mWebView.setWebChromeClient(new WebChromeClient() {

					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						mprogreBar.setProgress(newProgress);
						if (newProgress == 100) {
							mprogreBar.setVisibility(View.GONE);
						}
					}

				});
				final Handler handler = new Handler(new Callback() {

					@Override
					public boolean handleMessage(Message msg) {

						mWebView.loadUrl(url);
						return false;
					}
				});
			} else {// 无网
				Toast.makeText(WebViewActivity.this, HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK, Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		} else {// 地址不是http
			finish();
		}
	}

	// 返回的时候返回到上一层页面而不是退出
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mWebView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				mWebView.goBack();
				return true;
			}else{
				finish();
			}
			sendBroadcast();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWebView != null) {
			mWebView = null;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		if (mWebView != null) {
			mWebView.onResume();
		}
		// if (getRequestedOrientation() !=
		// ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		// && from.equals("announcement")) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// }
	}

	@SuppressLint("NewApi")
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mWebView != null) {
			mWebView.onPause();
		}
	}

	private void sendResultBraodcast(String url) {
		try {
			synchronized (this) {
				if (url.contains("www.youku_tenpay.com")) {
					Intent intent = new Intent();
					intent.setAction(HYConstant.YK_TENPAY_CALLBACK + HYPlatform.getAppId());
					if (url.substring(url.indexOf("success=")).equals("success=1")) {
						intent.putExtra("isSuccess", "支付成功");
					} else {
						intent.putExtra("isSuccess", "支付失败");
					}
					intent.putExtra("isRecharge", isRecharge);
					// 发送 一个无序广播
					sendBroadcast(intent);
					Logger.d("sendBroadcast");
					finish();
					return;
				}
			}
		} catch (Exception e) {
		}
	}

	private void sendBroadcast() {
		// 用户取消财付通支付
		if (isTenPay) {
			Intent intent = new Intent();
			intent.setAction(HYConstant.YK_TENPAY_CALLBACK + GameSDKApplication.getInstance().getAppid());
			intent.putExtra("isRecharge", isRecharge);
			intent.putExtra("isSuccess", "中途取消操作");
			sendBroadcast(intent);
		}
	}

	// msh add to modify android.view.windowleaked
	@Override
	public void finish() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		decorView.removeAllViews();
		super.finish();
	}

}
