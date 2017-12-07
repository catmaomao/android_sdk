package com.heyijoy.gamesdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.memfloat.PerCenterDialog;
import com.heyijoy.gamesdk.util.HomeListener;
import com.heyijoy.gamesdk.util.HomeListener.OnHomePressedListener;

/**
 * 透明activity，为service启动普通dialog的媒介
 *
 * @author yt
 */
public class HYInitActivity extends Activity {
    private PerCenterDialog perCenterDialog;//个人中心对话框
    private HomeListener mHomeListener;
    private HYCallBack callback = new HYCallBack() {
        @Override
        public void onSuccess(Bean bean) {
//			Util.sendBroadcast(HYInitActivity.this);
            finish();
        }

        @Override
        public void onFailed(int code, String failReason) {
            finish();
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        String name = intent.getStringExtra("from");
        initShow(name);
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hy_activity_init);
        if (GameSDKApplication.getInstance() == null
                || GameSDKApplication.getInstance().getContext() == null) {
            GameSDKApplication.getInstance().init(this);
        }
        Intent intent = getIntent();
        String name = intent.getStringExtra("from");
        initShow(name);
    }


    public void initShow(String name) {
        if ("moreByFloatService".equals(name)) {
            startPer();
        }
        registerHomeListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void startPer() {
        HYCallBack callback = new HYCallBack() {
            @Override
            public void onSuccess(Bean bean) {
                finish();
            }

            @Override
            public void onFailed(int code, String failReason) {
                finish();
            }
        };
        perCenterDialog = new PerCenterDialog(this, callback);
        perCenterDialog.startShow();
    }


    @Override
    protected void onDestroy() {
        mHomeListener.stopListen();
        finishShowingDialog("no");
        super.onDestroy();

    }

    //关闭显示的对话框，str：为标记位。
    private void finishShowingDialog(String str) {

        if (perCenterDialog != null) {
            perCenterDialog.finishDialog(str);
        }
        super.onDestroy();
    }

    //注册home键的监听广播操作，监听到点击home键后关闭个人中心 对话框
    private void registerHomeListener() {
        mHomeListener = new HomeListener(this);
        mHomeListener.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                mHomeListener.stopListen();
                finishShowingDialog("yes");
            }
        });
        mHomeListener.startListen();
    }

}
