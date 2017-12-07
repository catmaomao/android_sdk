package com.heyijoy.gamesdk.widget;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;
/**
 * 购买vip的vebview，支持支付宝、财付通、微信等支付;论坛页面
 * 
 * @author 徐琦
 */
public class WebViewVipActivity extends Activity{
    private WebView mWebView;
	private ProgressBar mprogreBar;// 上方的进度细条
    private String url;
    private boolean mflag=true;
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
        mWebView.addJavascriptInterface(new WebViewVipJSInterface(), "yksdkjs");
        if(getIntent().getBooleanExtra("fromForum", false)) mWebView.getSettings().setUserAgentString(TELEPHONY_SERVICE);
        String yktk = GameSDKApplication.getInstance().getYktk();
//        Map<String , String> cookie = new HashMap<String , String>() ;
//        cookie.put("yktk", yktk);
        
        
//        setCookie(this, "http://www.youku.com", cookie, false);
        url = getIntent().getStringExtra("url");
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				mprogreBar.setProgress(newProgress);
				if (newProgress == 100) {
					mprogreBar.setVisibility(View.GONE);
				}
			}
		});
		
        if(url==null){
        	url = "http://a404page.com/a404page-should-not-exists.html";
        }
        
    	mWebView.loadUrl(url);//开启论坛页面        
        
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
			      if( url.startsWith("http:") || url.startsWith("https:") ) {  
		                return false;  
			      }
			      try{
			    	  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  
			    	  startActivity(intent);
			      }catch(Exception e){
			    	  e.printStackTrace();
			      }
			     
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
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
	@Override
	public void finish() {
		overridePendingTransition(0, 0);
		super.finish();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
	    	if(mWebView.canGoBack()){
	    		finish();
	    		return true;
	    	}
	    	return super.onKeyDown(keyCode, event);
	      }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		if(url==null){
		}else{
			Util.sendBroadcast(this,"");//论坛页面关闭时发广播
		}
		super.onDestroy();
	}
	
	class WebViewVipJSInterface{
	    @JavascriptInterface
	    public void showTips(String tip) {
	        Toast.makeText(WebViewVipActivity.this, "网页打印:" + tip, Toast.LENGTH_SHORT).show();
	    }
	    @JavascriptInterface
	    public void finish(int i) {//1为支付成功
	    	if(i==1)
	    	{
	    		setResult(RESULT_OK);
	    		if("1".equals(GameSDKApplication.getInstance().getVipScheme())) 
	    		{
	    			startSucessDialog();
	    		}else{
	    			WebViewVipActivity.this.finish();
	    		}
	    		
	    	}else if(i==0)
	    	{    
	    		WebViewVipActivity.this.finish();
	    	}else
	    	{    
	    		WebViewVipActivity.this.finish();
	    	}
	    }
	}
	private void startSucessDialog() {
		
		final HYDialog dlg = new HYDialog(WebViewVipActivity.this, R.style.dialog);
		LinearLayout quitLayout = (LinearLayout) dlg.getLayoutInflater().inflate(
				R.layout.dialog_hy_vip_sucess, null);
		TextView tv_title_text = (TextView)quitLayout.findViewById(R.id.tv_title_text);
		tv_title_text.setText(HYConstant.TITLE_VIP);
		TextView image = (TextView)quitLayout.findViewById(R.id.hy_quit_baner);
		TextView image_text = (TextView)quitLayout.findViewById(R.id.hy_quit_baner_text);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(quitLayout);
		
		Button button_close = (Button)quitLayout.findViewById(R.id.btn_cancel);
		button_close.setText("关闭");
		button_close.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dlg.cancel();
				WebViewVipActivity.this.finish();
			}
		});
		
		Button button_invalid = (Button)quitLayout.findViewById(R.id.btn_quit);
		button_invalid.setVisibility(View.GONE);
		
		image_text.setVisibility(View.VISIBLE);
		image.setVisibility(View.VISIBLE);
		String str=GameSDKApplication.getInstance().getVipMsg();
		if(str==null||"".equals(str))
		{
			image_text.setText("第一步：点击合乐智趣悬浮窗\n\n第二步：选择领取码\n\n第三步：请持激活码在游戏内兑换礼包");
		}else
		{
			image_text.setText(str);
		}
		
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
					dlg.dismiss();
					WebViewVipActivity.this.finish();
				}
				return false;
			}
		});
		dlg.show();
		
	}
}
