
package com.heyijoy.gamesdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.HYProgressDlg;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

/**
 * 微博分享
 * 
 * @author mashaohu
 *
 */
public class HYWBShareActivity extends Activity implements WbShareCallback {

	public static final String KEY_SHARE_TYPE = "key_share_type";
	public static final int SHARE_CLIENT = 1;
	public static final int SHARE_ALL_IN_ONE = 2;
	private String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog,"
			+ "invitation_write";
	private String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	private WbShareHandler shareHandler;
	private AuthInfo mAuthInfo;
	private HYThridParams thridParams;
	private String APP_KEY;
	private String TAG = "HeyiJoySDK";
	int flag = 0;
	private String share_title;
	private String share_desc;
	private String share_weburl;
	private String share_imgName;
	private HYProgressDlg proDlg;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (proDlg != null && proDlg.isShowing()) {
					proDlg.dismiss();
				}
				
				sendMultiMessage();
				
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hy_activity_init);
		proDlg = HYProgressDlg.show(this, "合乐智趣", "请稍后…");
		// share_title = "块大乱";
		// share_desc = "SDK立即付款设计的";
		// share_weburl = "http://t.cn/RK1dBcR";
		// share_imgName = "done";
		share_title = getIntent().getStringExtra("title");
		share_desc = getIntent().getStringExtra("desc");
		share_weburl = getIntent().getStringExtra("weburl");
		share_imgName = getIntent().getStringExtra("imgName");

		thridParams = GameSDKApplication.getInstance().getThridParams();
		APP_KEY = Util.getWeiBoAppkey(thridParams);
		// Log.d(TAG, "weibo_appkey=" + APP_KEY);
		if (TextUtils.isEmpty(APP_KEY)) {
			Toast.makeText(HYWBShareActivity.this, "参数异常，请重新启动游戏", Toast.LENGTH_SHORT).show();
		} else {
			mAuthInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
			WbSdk.install(this, mAuthInfo);
			shareHandler = new WbShareHandler(this);
			shareHandler.registerApp();
			
			Message msg = Message.obtain();
			msg.what = 1;
			handler.sendMessageDelayed(msg, 1000);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		shareHandler.doResultIntent(intent, this);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。
	 */
	private void sendMultiMessage() {

		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		weiboMessage.textObject = getTextObj();
		if (!TextUtils.isEmpty(share_imgName)) {
			weiboMessage.imageObject = getImageObj();
		}
		weiboMessage.mediaObject = getWebpageObj();
		shareHandler.shareMessage(weiboMessage, true);

	}

	@Override
	public void onWbShareSuccess() {
		Toast.makeText(HYWBShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
		shareResult("0");
	}

	@Override
	public void onWbShareCancel() {
		Toast.makeText(HYWBShareActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
		shareResult("1");
	}

	@Override
	public void onWbShareFail() {
		Toast.makeText(HYWBShareActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
		shareResult("2");
	}

	public void shareResult(String result) {
		Intent intent = new Intent();
		intent.setAction(HYConstant.SHARE_WEIBO + GameSDKApplication.getInstance().getAppid());
		intent.putExtra(HYConstant.SHARE_WEIBO, result);
		sendBroadcast(intent);
		finish();
	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = share_desc;
		textObject.title = share_title;
		return textObject;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj() {
		int imgid = HYWBShareActivity.this.getResources().getIdentifier(share_imgName, "drawable",
				HYWBShareActivity.this.getPackageName());
		ImageObject imageObject = new ImageObject();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgid);
		imageObject.setImageObject(bitmap);
		return imageObject;
	}

	/**
	 * 创建多媒体（网页）消息对象。
	 *
	 * @return 多媒体（网页）消息对象。
	 */
	private WebpageObject getWebpageObj() {
		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Utility.generateGUID();
		mediaObject.title = share_title;
		mediaObject.description = share_desc;
		int imgid = HYWBShareActivity.this.getResources().getIdentifier(share_imgName, "drawable",
				HYWBShareActivity.this.getPackageName());
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgid);
		// 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
		mediaObject.setThumbImage(bitmap);
		mediaObject.actionUrl = share_weburl;
		return mediaObject;
	}

	public boolean isCancle = false;

	@Override
	protected void onResume() {
		super.onResume();
	}
}
