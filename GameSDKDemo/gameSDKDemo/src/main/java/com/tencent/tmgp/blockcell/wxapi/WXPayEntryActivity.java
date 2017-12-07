package com.tencent.tmgp.blockcell.wxapi;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.util.Util;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static IWXAPI api;
	private HYThridParams thridParams;
	private static String APPID;
	private String TAG = "HeyiJoySDK";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResources().getIdentifier("pay_result", "layout", getPackageName()));

		thridParams = GameSDKApplication.getInstance().getThridParams();
		APPID = Util.getWXAppid(thridParams);
		if (TextUtils.isEmpty(APPID)) {
			Toast.makeText(WXPayEntryActivity.this, "参数异常，请重新启动游戏", Toast.LENGTH_SHORT).show();
		} else {
			api = WXAPIFactory.createWXAPI(this, APPID);
			api.handleIntent(getIntent(), this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.app_tip);
			// builder.setMessage(getString(R.string.pay_result_callback_msg,
			// String.valueOf(resp.errCode)));
			// builder.show();
			wxPay(resp);
		}
	}

	/**
	 * 微信支付
	 */
	private void wxPay(BaseResp resp) {
		try {
			synchronized (this) {
				Intent intent = new Intent();
				intent.setAction("yk_tenpay_callback" + HYPlatform.getAppId());

				int errCode = resp.errCode;
				if (0 == errCode) {
					intent.putExtra("payCode", "0");// success
				} else if (-2 == errCode) {
					intent.putExtra("payCode", "1");// cancel
				} else {
					intent.putExtra("payCode", "2");// failed
				}

				sendBroadcast(intent);
				finish();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}