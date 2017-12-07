package com.heyijoy.gamesdk.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;
/**
 * 福利webview
 * 
 * @author 徐琦
 */
public class WebViewWelfareActivity extends Activity {
    private WebView mWebView;
	private ProgressBar mprogreBar;// 上方的进度细条
    private boolean mflag=true;
    String from ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hy_activity_webview_fullscreen);
        mWebView = (WebView) findViewById(R.id.webView);
        mprogreBar = (ProgressBar) findViewById(R.id.progress);
        mWebView.setBackgroundColor(getResources().getColor(android.R.color.white));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setBackgroundColor(0); // 设置背景色  
        String yktk = GameSDKApplication.getInstance().getYktk();
        CookieManager cookieManager = CookieManager.getInstance();
        //获取登陆时的cookie
//        String yktkEncode = URLEncoder.encode(yktk);
        cookieManager.setCookie(".youku.com", "yktk="+yktk);
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
        
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				mprogreBar.setProgress(newProgress);
				if (newProgress == 100) {
					mprogreBar.setVisibility(View.GONE);
				}
			}
		});
        mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				closeProgress();
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				closeProgress();
				super.onReceivedError(view, errorCode, description,
						failingUrl);
			}


			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					String url) {
				view.loadUrl(url);   //在当前的webview中跳转到新的url
			     
	            return true;
			}
			@Override
			public void onPageStarted(WebView view, String url,
					Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			private void closeProgress() {
				if (mflag) {
					try {
						Thread.sleep(700);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mflag = false;
				}
			}
		});
    }
    
	@Override
	public void finish() {
		overridePendingTransition(0, 0);
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		Util.sendBroadcast(this,"");
		super.onDestroy();
	}
}
