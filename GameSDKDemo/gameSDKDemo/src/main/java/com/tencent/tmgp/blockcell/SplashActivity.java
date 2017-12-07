package com.tencent.tmgp.blockcell;

import com.heyijoy.gamesdk.act.HYPlatform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		TextView tvRelease = (TextView) findViewById(R.id.tv_release);
		TextView tvDev = (TextView) findViewById(R.id.tv_dev);
		tvRelease.setOnClickListener(new OnClickListener() {//生成环境
			
			@Override
			public void onClick(View arg0) {
				enter();
			}
		});
		
		tvDev.setOnClickListener(new OnClickListener() {//测试环境
			
			@Override
			public void onClick(View arg0) {
//				BuildConfig.IS_TEST = true;
				enter();
			}
		});
	}
	
	public void enter(){
		HYPlatform.logout(this);
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
	}
}
