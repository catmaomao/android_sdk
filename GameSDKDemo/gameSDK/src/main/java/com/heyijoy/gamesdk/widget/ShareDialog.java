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
import com.heyijoy.gamesdk.activity.HYShareQQActivity;
import com.heyijoy.gamesdk.activity.HYWBShareActivity;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.memfloat.FloatService;
import com.heyijoy.gamesdk.memfloat.FloatView;
import com.heyijoy.gamesdk.memfloat.FloatViewService;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.util.WXUtil;
import com.heyijoy.sdk.CallBack;
import com.heyijoy.sdk.ShareParams;
import com.sina.weibo.sdk.WeiboAppManager;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author msh
 * @since 三方分享
 */
public class ShareDialog extends Dialog {

	private String TAG = "HeyiJoySDK";
	private HYDialog dlg;
	private Context context = null;
	private CallBack method;
	private String share_title;
	private String share_desc;
	private String share_weburl;
	private String share_imgName;
	private String share_imgurl;
	private String share_wxWebUrl;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (proDlg != null && proDlg.isShowing()) {
					proDlg.dismiss();
				}
				String type = (String) msg.obj;
				WXWebpageObject webpage = new WXWebpageObject();
				if(type.isEmpty()){
					webpage.webpageUrl = share_weburl;//微信分享
				}else{
					webpage.webpageUrl = share_wxWebUrl;//微信朋友圈分享,针对微信朋友圈短链接问题，需cp传长链接
				}
				WXMediaMessage mediaMessagge = new WXMediaMessage(webpage);
				mediaMessagge.title = share_title;
				mediaMessagge.description = share_desc;
				if (!TextUtils.isEmpty(share_imgName)) {// 说明cp有传此参数
					int imgid = context.getResources().getIdentifier(share_imgName, "drawable",
							context.getPackageName());
					Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), imgid);
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
					bmp.recycle();
					mediaMessagge.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
				}
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("webpage");
				req.message = mediaMessagge;
				req.scene = type.isEmpty() ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
				if (api != null) {
					api.sendReq(req);
				}
				break;
			default:
				break;
			}
		}
	};

	public ShareDialog(Context context, ShareParams params, CallBack callback) {
		super(context);
		this.context = context;
		this.method = callback;
		if (callback == null) {
			Toast.makeText(context, "CallBack is null", Toast.LENGTH_SHORT).show();
			return;
		}
		if (params != null) {
			share_title = params.getTitle();
			share_desc = params.getContent();
			share_weburl = params.getSourceUrl();
			share_imgName = params.getNotifyIconText();
			share_imgurl = params.getImgUrl();
		    share_wxWebUrl = params.getWxWebUrl();
		} else {
			Toast.makeText(context, "ShareParams is null", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void show() {
		try {
			LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.share_dialog, null);
			dlg = new HYDialog(context, R.style.dialog);
			dlg.setCanceledOnTouchOutside(false);
			dlg.setCancelable(false);
			dlg.setContentView(loginLayout1);
			// ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams)
			// loginLayout1.getLayoutParams();
			// layoutParams.width =
			// context.getResources().getDisplayMetrics().widthPixels
			// - DensityUtil.dp2px(context, 16f);
			// layoutParams.bottomMargin = DensityUtil.dp2px(context, 8f);
			// loginLayout1.setLayoutParams(layoutParams);
			dlg.getWindow().setGravity(Gravity.BOTTOM);
			dlg.getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
			ImageView iv_share_qq = (ImageView) loginLayout1.findViewById(R.id.iv_share_qq);
			ImageView iv_share_wx = (ImageView) loginLayout1.findViewById(R.id.iv_share_wx);
			ImageView iv_share_wx_circle = (ImageView) loginLayout1.findViewById(R.id.iv_share_wx_circle);
			ImageView iv_share_weibo = (ImageView) loginLayout1.findViewById(R.id.iv_share_weibo);
			TextView tv_share_cancel = (TextView) loginLayout1.findViewById(R.id.tv_share_cancel);

			// qq
			iv_share_qq.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						boolean qqClientAvailable = Util.isQQClientAvailable(context);
						if (qqClientAvailable) {
							shareQQ();
						} else {
							Toast.makeText(context, "请先安装QQ客户端", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Log.e(TAG, "ERROR:" + e.getMessage());
					}
				}
			});

			// 微信
			iv_share_wx.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						shareWX("");
					} catch (Exception e) {
						Log.e(TAG, "ERROR:" + e.getMessage());
					}
				}
			});
			// 微信朋友圈
			iv_share_wx_circle.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						shareWX("circle");
					} catch (Exception e) {
						Log.e(TAG, "ERROR:" + e.getMessage());
					}
				}
			});

			// 微博
			iv_share_weibo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						boolean hasWbInstall = WeiboAppManager.getInstance(context).hasWbInstall();
						if (hasWbInstall) {
							FloatViewService.isShowFloat = false;// 进入微博分享则关闭悬浮窗
							shareWB();
						} else {
							Toast.makeText(context, "请先安装微博客户端", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Log.e(TAG, "ERROR:" + e.getMessage());
					}
				}
			});

			// 取消弹框
			tv_share_cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					finishDialog();
				}
			});

			dlg.show();

		} catch (Exception e) {
			Log.e(TAG, "ERROR:" + e.getMessage());
		}

	}

	/**
	 * 微博
	 */
	protected void shareWB() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HYConstant.SHARE_WEIBO + GameSDKApplication.getInstance().getAppid());
		GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				synchronized (this) {
					Util.sendBroadcast(context);

					GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
					String result = intent.getStringExtra(HYConstant.SHARE_WEIBO);
					method.callBack(result);
				}
			}
		}, intentFilter);

		Intent intent = new Intent(context, HYWBShareActivity.class);
		intent.putExtra("title", share_title);
		intent.putExtra("desc", share_desc);
		intent.putExtra("weburl", share_weburl);
		intent.putExtra("imgName", share_imgName);
		context.startActivity(intent);
	}

	/**
	 * 微信
	 */
	private HYThridParams thridParams;
	private String appid;
	private static final int THUMB_SIZE = 96;
	private HYProgressDlg proDlg;
	private IWXAPI api;

	protected void shareWX(String type) {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HYConstant.SHARE_WX + GameSDKApplication.getInstance().getAppid());
		GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				synchronized (this) {

					GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
					String result = intent.getStringExtra(HYConstant.SHARE_WX);
					method.callBack(result);
				}
			}
		}, intentFilter);

		thridParams = GameSDKApplication.getInstance().getThridParams();
		appid = Util.getWXAppid(thridParams);
		// Log.d(TAG, "微信分享appid=" + appid);
		if (TextUtils.isEmpty(appid)) {
			Toast.makeText(context, "参数异常，请重新启动游戏", Toast.LENGTH_SHORT).show();
		} else {
			api = WXAPIFactory.createWXAPI(context, appid);
			if (Util.isWeixinAvilible(context)) {
				
				proDlg = HYProgressDlg.show(context, "合乐智趣", "请稍后…");
				
				api.registerApp(appid);

				Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = type;
				handler.sendMessageDelayed(msg, 1000);

			} else {
				Toast.makeText(context, "请先安装微信客户端", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 分享qq 定义"0"分享成功，"1"分享取消,"2"分享失败
	 */
	protected void shareQQ() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HYConstant.SHARE_QQ + GameSDKApplication.getInstance().getAppid());
		GameSDKApplication.getInstance().getContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				synchronized (this) {

					GameSDKApplication.getInstance().getContext().unregisterReceiver(this);
					String result = intent.getStringExtra(HYConstant.SHARE_QQ);
					method.callBack(result);
				}
			}
		}, intentFilter);

		Intent intent = new Intent(context, HYShareQQActivity.class);
		intent.putExtra("title", share_title);
		intent.putExtra("desc", share_desc);
		intent.putExtra("weburl", share_weburl);
		intent.putExtra("share_imgurl", share_imgurl);
		context.startActivity(intent);
	}

	private String buildTransaction(final String type) {
		return ("".equals(type)) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	public void finishDialog() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
	}
}
