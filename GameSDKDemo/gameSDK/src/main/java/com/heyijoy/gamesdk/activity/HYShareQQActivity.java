package com.heyijoy.gamesdk.activity;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.HYProgressDlg;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
/**
 * qq分享
 * @author mashaohu
 *
 */
public class HYShareQQActivity extends Activity {

	private int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
	private Tencent mTencent;
	private HYThridParams thridParams;
	private String appid;
	private String TAG = "HeyiJoySDK";
	private String share_title;
	private String share_desc;
	private String share_weburl;
	private String share_imgurl;
	private HYProgressDlg proDlg;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (proDlg != null && proDlg.isShowing()) {
					proDlg.dismiss();
				}
				
				Bundle params = new Bundle();
				params.putString(QQShare.SHARE_TO_QQ_TITLE, share_title);
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, share_weburl);
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, share_desc);
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, share_imgurl);
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
				doShareToQQ(params);
				
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
		share_title = getIntent().getStringExtra("title");
		share_desc = getIntent().getStringExtra("desc");
		share_weburl = getIntent().getStringExtra("weburl");
		share_imgurl = getIntent().getStringExtra("share_imgurl");

		thridParams = GameSDKApplication.getInstance().getThridParams();
		appid = Util.getQQAppid(thridParams);
		// Log.d(TAG, "qqappid=" + appid);
		if (TextUtils.isEmpty(appid)) {
			Toast.makeText(HYShareQQActivity.this, "参数异常，请重新启动游戏", Toast.LENGTH_SHORT).show();
		} else {
			shareDialog();
		}
	}

	private void shareDialog() {
		try {
			mTencent = Tencent.createInstance(appid, this.getApplicationContext());
			
			Message msg = Message.obtain();
			msg.what = 1;
			handler.sendMessageDelayed(msg, 1000);
			
		} catch (Exception e) {
			Log.e(TAG, "ERROR:" + e.getMessage());
		}

	}

	/* qq分享 */
	private void doShareToQQ(final Bundle params) {
		// QQ分享要在主线程做
		ThreadManager.getMainHandler().post(new Runnable() {

			@Override
			public void run() {
				if (null != mTencent) {
					mTencent.shareToQQ(HYShareQQActivity.this, params, qqShareListener);
				}
			}
		});
	}

	/**
	 * 定义"0"分享成功，"1"分享取消,"2"分享失败
	 */
	IUiListener qqShareListener = new IUiListener() {
		@Override
		public void onCancel() {
			Toast.makeText(HYShareQQActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
			shareResult("1");
		}

		@Override
		public void onComplete(Object response) {
			Toast.makeText(HYShareQQActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
			shareResult("0");
		}

		@Override
		public void onError(UiError e) {
			Toast.makeText(HYShareQQActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
			shareResult("2");
		}
	};

	public void shareResult(String result) {
		Intent intent = new Intent();
		intent.setAction(HYConstant.SHARE_QQ + GameSDKApplication.getInstance().getAppid());
		intent.putExtra(HYConstant.SHARE_QQ, result);
		sendBroadcast(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTencent != null) {
			mTencent.releaseResource();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_QQ_SHARE) {// qq分享
			Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
