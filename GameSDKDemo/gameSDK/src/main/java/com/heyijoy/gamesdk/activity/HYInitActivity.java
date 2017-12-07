package com.heyijoy.gamesdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.memfloat.PerCenterDialog;
import com.heyijoy.gamesdk.orderlist.MsgListDialog;
import com.heyijoy.gamesdk.orderlist.OrderListDialog;
import com.heyijoy.gamesdk.orderlist.TaskListDialog;
import com.heyijoy.gamesdk.util.HomeListener;
import com.heyijoy.gamesdk.util.HomeListener.OnHomePressedListener;
import com.heyijoy.gamesdk.widget.BindingPhoneDialog;
import com.heyijoy.gamesdk.R;

/**
 * 透明activity，为service启动普通dialog的媒介
 * 
 * @author yt
 */
public class HYInitActivity extends Activity {
	private MsgListDialog msgListDialog;
	private TaskListDialog taskListDialog;//积分任务对话框
	private BindingPhoneDialog bindingDlg;//绑定对话框
	private PerCenterDialog perCenterDialog;//个人中心对话框
//	private VipCodeListDialog vipCodeListDialog;//领取码对话框
//	private VideoSettingDialog videoSettingDialog;//录屏设置对话框
	private HomeListener mHomeListener;
	private boolean[] floatSwitch=null;
	private HYCallBack callback = new HYCallBack() {
		@Override
		public void onSuccess(Bean bean) {
//			Util.sendBroadcast(HYInitActivity.this);
			finish();
		}

		@Override
		public void onFailed(int code,String failReason) {
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
	
	
	public void initShow(String name){
		if ("FloatService".equals(name)) {
			startMsg();
		} else if ("OrderByPreCenter".equals(name)) {
//			startOrder();
		} else if ("BindByPreCenter".equals(name)) {
			startBind();
		} else if ("moreByFloatService".equals(name)) {
//			floatSwitch=GameSDKApplication.getInstance().getFloatSwitch();
			
//			if(floatSwitch!=null)
//			{
				startPer();
//			}
		} else if ("CodeByPreCenter".equals(name)) {
			startCode();
		} else if ("TaskByPreCenter".equals(name)) {
			startTask();
		}else if ("VideoSetting".equals(name)) {
//			startVideoSetting();
		}
		registerHomeListener();
	}

	private void startVideoSetting() {
//		videoSettingDialog = new VideoSettingDialog(this, callback);
//		videoSettingDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void startCode() {
//		vipCodeListDialog = new VipCodeListDialog(this, callback);
//		vipCodeListDialog.showDialog();
	}

	private void startPer() {
		HYCallBack callback = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				finish();
			}

			@Override
			public void onFailed(int code,String failReason) {
				finish();
			}
		};
		perCenterDialog = new PerCenterDialog(this, callback);
		perCenterDialog.startShow();
	}

	private void startBind() {
//		bindingDlg = new BindingPhoneDialog(this, callback, "5");
//		bindingDlg.show();
	}


	private void startMsg() {
		msgListDialog = new MsgListDialog(this, callback);
		msgListDialog.showDialog();
	}

	private void startTask() {
		taskListDialog = new TaskListDialog(this, callback);
		taskListDialog.showDialog();
	}
	
	@Override
	protected void onDestroy() {
		mHomeListener.stopListen();
		finishShowingDialog("no");
	}
	
	//关闭显示的对话框，str：为标记位。 
	private void finishShowingDialog(String str) {

		if (msgListDialog != null) {
			msgListDialog.closeDialog();
		}
		if (bindingDlg != null) {
			bindingDlg.finishDialog();
		}
		if (taskListDialog != null) {
			taskListDialog.finishDialog();
		}
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
