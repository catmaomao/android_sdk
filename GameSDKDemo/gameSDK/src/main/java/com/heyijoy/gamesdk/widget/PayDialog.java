package com.heyijoy.gamesdk.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.HYPayBean;
import com.heyijoy.gamesdk.data.PayBean;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.memfloat.FloatViewService;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.PayResult;
import com.heyijoy.gamesdk.util.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 支付弹框
 * 
 * @author shaohuma
 *
 */
public class PayDialog extends Dialog {

	TenPayCallBackReceiver tenpayReceiver;
	WXPayCallBackReceiver wxpayReceiver;
	private Boolean canOpPay = false;// 应该代表短代支付
	private Boolean canCloseLoading = true;
	private Boolean isRecharge = false;
	private Boolean isSuccess = false;
	private TimeCount timeCount = new TimeCount(1500, 1500);
	private String callBackTips = "操作取消";
	private String payChannel = HYConstant.CHANNEL_ALIPAY;
	private String payList;
	private HYDialog dlg;
	private HYProgressDlg payLoading;
	private LinearLayout payLayout;
	private Activity activity;
	private HYPayBean ykPayBean;
	private HYCallBack payCallBack;
	private RelativeLayout btAlipay, btWXpay;
	private LinearLayout btUnionpay;
	private ImageView back, detail;
	private LinearLayout layoutUser;
	private TextView txtAmount, txtProduct;
	private float amount;
	private String product = "";
	private String orderId = "";
	private String unionpay_pay_url = "";// 银联支付地址
	private User user;
	private IWXAPI msgApi;// 微信支付接口类
	private HYDialog exitDialog;

	public PayDialog(Activity activity, HYPayBean ykPayBean, HYCallBack payCallBack, Boolean canOpPay,
			Boolean isRecharge) {
		super(activity);
		this.activity = activity;
		this.ykPayBean = ykPayBean;
		this.payCallBack = payCallBack;
		this.canOpPay = canOpPay;
		this.isRecharge = isRecharge;
		HYConstant.isRecharge = isRecharge;// 用于判断是支付还是充值,true-充值，false-支付
	}

	@Override
	public void show() {
		msgApi = WXAPIFactory.createWXAPI(activity, null);// 给微信支付初始化赋值
		showPayDialog();
	}

	private void showPayDialog() {

		payLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_hy_pay, null);
		dlg = new HYDialog(activity, R.style.pay_dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(payLayout);

		/* 返回键 */
		back = (ImageView) payLayout.findViewById(R.id.hy_pay_icon_back);
		detail = (ImageView) payLayout.findViewById(R.id.hy_pay_icon_detail);
		detail.setVisibility(View.GONE);
		/* 整个支付选项按钮布局 */
		btAlipay = (RelativeLayout) payLayout.findViewById(R.id.hy_pay_select_alipay_bt);
		btWXpay = (RelativeLayout) payLayout.findViewById(R.id.hy_pay_select_wxpay_bt);
		btUnionpay = (LinearLayout) payLayout.findViewById(R.id.hy_pay_select_unionpay_bt);

		/* 显示信息 */
		txtAmount = (TextView) payLayout.findViewById(R.id.hy_pay_amount_txt);
		layoutUser = (LinearLayout) payLayout.findViewById(R.id.hy_pay_user_layout);
		txtProduct = (TextView) payLayout.findViewById(R.id.hy_pay_product_txt);
		payList = GameSDKApplication.getInstance().getPaySwitch();// 将显示的支付方式的String类型转换为String数组

		// 单机
		if (GameSDKApplication.getInstance().isStandAlone()) {
			detail.setVisibility(View.INVISIBLE);
			layoutUser.setVisibility(View.GONE);
			user = new User();
			// initLogoStandAlone();
		} else {
			user = GameSDKApplication.getInstance().getUserFromPref(activity);
			initLogo();
		}

		try {
			amount = Float.parseFloat(ykPayBean.getAmount()) / 100;// 金额以分传递，显示的时候以元为单位，所以要除以100
		} catch (Exception e) {
			Toast.makeText(activity, "金额有误，请确认后重新购买！", Toast.LENGTH_SHORT).show();
			return;
		}
		product = ykPayBean.getProductName();

		txtAmount.setText(amount + "元");
		txtProduct.setText(product);

		back.setOnClickListener(new GoBackOnClickListener());
		btAlipay.setOnClickListener(new AlipayOnClickListener());
		btWXpay.setOnClickListener(new WXOnClickListener());
		btUnionpay.setOnClickListener(new UnionpayOnClickListener());

		dlg.show();

		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (Util.isFastDoubleClick()) {
						return true;
					}
					showExitPayAlert();
					return true;
				}
				return false;
			}
		});

		if (!isRecharge) {
			FloatViewService.isShowFloat = false;// 进入支付页面则关闭悬浮窗
		}
	}

	/**
	 * -------------------------选择支付方式的按钮------------------------------- 退出支付
	 * 
	 * @author shaohuma
	 *
	 */
	class GoBackOnClickListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			showExitPayAlert();
		}
	};

	/**
	 * 支付宝支付
	 * 
	 * @author shaohuma
	 *
	 */
	class AlipayOnClickListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			payChannel = HYConstant.CHANNEL_ALIPAY;
			startPrePay();
		}
	};

	/**
	 * 银联支付
	 * 
	 * @author shaohuma
	 *
	 */
	class UnionpayOnClickListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			payChannel = HYConstant.CHANNEL_UNIONPAY;
			startPrePay();
		}
	};

	/**
	 * 微信支付
	 * 
	 * @author shaohuma
	 *
	 */
	class WXOnClickListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			payChannel = HYConstant.CHANNEL_WXPAY;
			startPrePay();
		}
	};

	private boolean hasUnionpay = true;
	private boolean hasWxpay = true;

	/* 设置显示哪些支付方式 */
	private void initLogo() {
		// 支付宝
		if (!payList.contains("alipay")) {
			btAlipay.setVisibility(View.GONE);
		}

		// 银联支付
		if (!payList.contains("unionpay")) {
			btUnionpay.setVisibility(View.GONE);
			hasUnionpay = false;
		}

		// 微信
		if (!payList.contains("wechatpay") || !msgApi.isWXAppInstalled()) {// 微信游戏sdk本地支付
			btWXpay.setVisibility(View.GONE);
			hasWxpay = false;
		}

		// 默认支付宝支付，如果不支持其他支付
		if (!hasUnionpay && !hasWxpay) {
			btAlipay.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ---------------启动支付--------------------------------
	 */

	/**
	 * 请求支付订单号，若成功则请求第三方支付（运行dopay（））
	 */
	public void startPrePay() {
		if (Util.isFastDoubleClick())
			return;// 若点击过快，则只执行第一次
		startLoading();
		// 请求支付订单后的回调
		HYCallBack getOrderCallBack = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {// 正确即继续支付
				doPay(bean);
			}

			@Override
			public void onFailed(int code, String failReason) {// 错误则回调给cp错误代码
				if (failReason == null) {
					cancelPay("返回值错误");
					return;
				}

				if (failReason.equals(HttpRequestManager.COOKIE_OVERDUE)) {
//					UniFunctions.reLogin(activity);
				} else {
					cancelPay(failReason);
				}
				if (payLoading != null && payLoading.isShowing())
					payLoading.dismiss();
			}
		};
		if (!isRecharge) {// 支付
			HttpApi.getInstance().requestPay(payChannel, ykPayBean, getOrderCallBack);
		}
	}

	/** 拿到后台订单号，进行第三方支付分配 */
	private void doPay(Bean bean) {
		String params = "";
		String result = ((PayBean) bean).getParams();
		if (payChannel.equals(HYConstant.CHANNEL_ALIPAY)) {// 支付宝支付渠道
			try {
				JSONObject json = new JSONObject(result);
				JSONObject results = json.getJSONObject("results");
				params = results.getString("channel_params");
				orderId = results.getString("trade_id");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			doAlipay(params);// 支付宝支付
		} else if (payChannel.equals(HYConstant.CHANNEL_UNIONPAY)) {// 银联支付
			try {
				if (payLoading != null && payLoading.isShowing()) {
					payLoading.dismiss();
				}
				JSONObject json = new JSONObject(result);
				JSONObject results = json.getJSONObject("results");
				params = results.getString("channel_params");
				orderId = results.getString("trade_id");
				unionpay_pay_url = results.getString("unionpay_pay_url");

				doUnionPay(unionpay_pay_url, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (payChannel.equals(HYConstant.CHANNEL_WXPAY)) {// 微信支付渠道
			try {
				JSONObject json = new JSONObject(result);
				JSONObject results = json.getJSONObject("results");
				params = results.getString("channel_params");
				orderId = results.getString("trade_id");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			doWXPay(params);
		}
	}

	private class TenPayCallBackReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.v("微信广播");
			if (Util.isFastDoubleClick())
				return;
			synchronized (this) {
				if (payLoading != null && payLoading.isShowing())
					payLoading.dismiss();
				String payCode = intent.getStringExtra("payCode");
				try {
					if ("0".equals(payCode)) {
						Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
						binding();
					} else if ("1".equals(payCode)) {
						cancelPay("支付取消");
					} else if ("2".equals(payCode)) {
						cancelPay("支付失败");
					}
				} catch (Exception e) {
					cancelPay("支付失败");
				}
			}
			try {
				if (tenpayReceiver != null) {
					activity.unregisterReceiver(tenpayReceiver);
					tenpayReceiver = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	private void moveTaskToFront() { // api level 11
//		ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//
//		am.moveTaskToFront(activity.getTaskId(), 0);
//		// return isForeground(activity.getPackageName());
//	}

	private class WXPayCallBackReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.v("微信广播");
			try {
				if (wxpayReceiver != null) {
//					moveTaskToFront();
					activity.unregisterReceiver(wxpayReceiver);
					wxpayReceiver = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (Util.isFastDoubleClick())
				return;
			synchronized (this) {
				Boolean wasRecharge = intent.getBooleanExtra("isRecharge", false);
				int errCode = intent.getIntExtra("errCode", 99);
				String errStr = intent.getStringExtra("errStr");
				String appid = intent.getStringExtra("appid");
				if (appid == null || !appid.equals(GameSDKApplication.getInstance().getAppid()))
					return;
				String extra = intent.getStringExtra("extra");
				Logger.v("wasRecharge==" + wasRecharge);
				Logger.v("errCode==" + errCode);
				Logger.v("errStr==" + errStr);
				Logger.v("appid==" + appid);
				try {
					if (errCode == 0) {
						binding();
					} else {
						cancelPay("支付失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
					cancelPay("支付失败");
				}
			}

		}
	}

	/**
	 * ---------------支付宝支付--------------------------------
	 * 
	 * @param params
	 */
	private void doAlipay(final String params) {// 调用支付宝支付功能
		// PayTask payTask = new PayTask(activity, new OnPayListener() {
		// @Override
		// public void onPaySuccess(Context context, String resultStatus, String
		// memo, String result) {
		// binding();
		// if (payLoading != null && payLoading.isShowing())
		// payLoading.dismiss();
		// }
		//
		// @Override
		// public void onPayFailed(Context context, String resultStatus, String
		// failReason, String result) {
		// cancelPay(failReason);
		// }
		// });
		// payTask.pay(params);
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask(activity);
				String result = payTask.pay(params, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private static final int SDK_PAY_FLAG = 1;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				if (payLoading != null && payLoading.isShowing()) {
					payLoading.dismiss();
				}
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					paySuccess();
					// Toast.makeText(activity, "支付成功",
					// Toast.LENGTH_SHORT).show();
				} else {
					FloatViewService.isShowFloat = true;
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						// Toast.makeText(activity, "支付失败",
						// Toast.LENGTH_SHORT).show();
						cancelPay("支付失败");
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * ---------------银联支付--------------------------------
	 * 
	 * @param params
	 */
	private void doUnionPay(String unionpay_pay_url, String params) {
		Intent intent = new Intent(activity, WebViewActivity.class);
		intent.putExtra("url", unionpay_pay_url);
		intent.putExtra("params", params);
		intent.putExtra("pay_type", "unionpay");
		activity.startActivity(intent);
		if (dlg != null && dlg.isShowing())
			dlg.dismiss();
	}

	/**
	 * ---------------微信支付--------------------------------
	 * 
	 * @param params
	 */
	private void doWXPay(final String params) {
		/* 微信的广播接收注册 */
		IntentFilter tenpayFilter = new IntentFilter();
		tenpayReceiver = new TenPayCallBackReceiver();
		tenpayFilter.addAction(HYConstant.YK_TENPAY_CALLBACK + GameSDKApplication.getInstance().getAppid());
		activity.registerReceiver(tenpayReceiver, tenpayFilter);
		if (msgApi != null) {
			try {
				JSONObject json = new JSONObject(params);
				HYConstant.WX_APPID = json.getString("appid");
				msgApi.registerApp(json.getString("appid"));
				PayReq req = new PayReq();
				req.appId = json.getString("appid");
				req.partnerId = json.getString("partnerid");
				req.prepayId = json.getString("prepayid");
				req.nonceStr = json.getString("noncestr");
				req.timeStamp = json.getString("timestamp");
				req.packageValue = json.getString("package");
				req.sign = json.getString("sign");
				msgApi.sendReq(req);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * ---------------微信主客支付--------------------------------
	 * 
	 * @param params
	 */
	private void doYKWXPay(final String params) {
		/* 微信主客的广播接收注册 */
		IntentFilter wxpayFilter = new IntentFilter();
		wxpayReceiver = new WXPayCallBackReceiver();
		wxpayFilter.addAction(HYConstant.YK_YK_XPAY_CALLBACK);
		activity.registerReceiver(wxpayReceiver, wxpayFilter);

		if (params != null) {

			try {
				String ykPackName = Util.getPack();
				if (Util.confirmYK(activity, ykPackName)) {// 核对主客签名
					Intent intent = new Intent();
					ComponentName cn = new ComponentName(ykPackName, "com.youku.phone.gamecenter.GameWXPayActivity");
					intent.setComponent(cn);
					intent.putExtra("params", params);
					intent.putExtra("isRecharge", isRecharge);
					intent.putExtra("appid", GameSDKApplication.getInstance().getAppid());
					intent.putExtra("extra", GameSDKApplication.getInstance().getAppid());
					activity.startActivity(intent);
				} else {
					cancelPay("请下载最新的合乐智趣视频客户端!");
				}
			} catch (Exception e) {
				cancelPay("请下载最新的合乐智趣视频客户端");
				e.printStackTrace();
			}
		} else {
			cancelPay("请下载最新的合乐智趣视频客户端");
		}
		// if(payLoading!=null && payLoading.isShowing())payLoading.dismiss();
	}

	/* 建议用户绑定手机 */
	private void binding() {
		// if (GameSDKApplication.getInstance().isNameRegOpen()) {
		// SharedPreferences preferences =
		// activity.getSharedPreferences(HYConstant.PREF_FILE_USER,
		// Context.MODE_PRIVATE);
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String date = sdf.format(new java.util.Date());
		// String hasBindTime =
		// preferences.getString(HYConstant.PREF_FILE_HAS_BIND_TIME, "");
		// if (hasBindTime.equals(date)) {// 如果今天已经提示过绑定手机了，则不再提示，直接通知支付成功
		// paySuccess();
		// } else {
		// Util.showBindingDlg(activity, succesBack, "3");
		// Editor editor = preferences.edit();
		// editor.putString(HYConstant.PREF_FILE_HAS_BIND_TIME, date);
		// editor.commit();
		// }
		// } else {
		paySuccess();
		// }
	}

	HYCallBack succesBack = new HYCallBack() {
		@Override
		public void onSuccess(Bean bean) {
			paySuccess();
		}

		@Override
		public void onFailed(int code, String failReason) {
			paySuccess();
		}
	};

	private void paySuccess() {
		isSuccess = true;

		// if (hasRebate.equals("yes") || hasConvert.endsWith("yes") ||
		// hasBonus.equals("yes")) {
		// Intent intent = new Intent(activity, HYRebateActivity.class);
		// intent.putExtra("hasRebate", hasRebate);
		// intent.putExtra("rebateTitle", rebateTitle);
		// intent.putExtra("rebateMsg", rebateMsg);
		// intent.putExtra("hasBonus", hasBonus);
		// intent.putExtra("bonusTitle", bonusTitle);
		// intent.putExtra("bonusMsg", bonusMsg);
		// intent.putExtra("hasConvert", hasConvert);
		// intent.putExtra("convertTitle", convertTitle);
		// intent.putExtra("convertMsg", convertMsg);
		// activity.startActivity(intent);
		// activity.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
		// }

		dlgFinish();
	}

	private void cancelPay(String failReason) {
		timeCount.cancel();
		isSuccess = false;
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		if (payLoading != null && payLoading.isShowing()) {
			payLoading.dismiss();
		}
		if (exitDialog != null && exitDialog.isShowing()) {
			exitDialog.dismiss();
		}
		Toast.makeText(activity, failReason, Toast.LENGTH_SHORT).show();
	}

	private void dlgFinish() {

		Logger.v("isSuccess==" + isSuccess);
		if (isSuccess) {
			PayBean payBean = new PayBean();
			payBean.setParams(callBackTips);
			payCallBack.onSuccess(payBean);
		} else {
			payCallBack.onFailed(HYConstant.EXCEPTION_CODE, callBackTips);
		}
		try {
			if (tenpayReceiver != null) {
				activity.unregisterReceiver(tenpayReceiver);
				tenpayReceiver = null;
			}
			if (wxpayReceiver != null) {
				activity.unregisterReceiver(wxpayReceiver);
				wxpayReceiver = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isRecharge) {
			FloatViewService.isShowFloat = true;// 退出支付页面则打开悬浮窗
		} else {
			HYConstant.isRecharge = false;
		}
		if (dlg != null && dlg.isShowing())
			dlg.dismiss();
		if (payLoading != null && payLoading.isShowing())
			payLoading.dismiss();
		if (exitDialog != null && exitDialog.isShowing())
			exitDialog.dismiss();
	}

	private void startLoading() {
		if (payChannel.equals(HYConstant.CHANNEL_WXPAY)) {
			canCloseLoading = true;
		} else {
			canCloseLoading = false;
		}
		timeCount = new TimeCount(15000, 15000);
		timeCount.start();
		payLoading = HYProgressDlg.show(activity, "正在进入支付…");
		payLoading.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && canCloseLoading) {
					if (Util.isFastDoubleClick()) {
						return true;
					}
					showExitPayAlert();
					return true;
				}
				return false;
			}
		});
	}

	// 倒计时器
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			canCloseLoading = true;
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
		}
	}

	// 显示退出支付对话框
	private void showExitPayAlert() {
		if (isRecharge) {
			dlgFinish();
			return;
		}
		exitDialog = new HYDialog(activity, R.style.dialog);
		LinearLayout loginLayout1 = (LinearLayout) exitDialog.getLayoutInflater()
				.inflate(R.layout.dialog_hy_pay_show_exit, null);
		exitDialog.setCanceledOnTouchOutside(false);
		exitDialog.setContentView(loginLayout1);
		TextView exitContent = (TextView) loginLayout1.findViewById(R.id.hy_dialog_show_exit_content_txt);
		exitContent.setText("支付未完成，请确认是否退出？");
		Button btnConfirm = (Button) loginLayout1.findViewById(R.id.btn_confirm);
		btnConfirm.setText("确认退出");
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (exitDialog != null && exitDialog.isShowing())
					exitDialog.dismiss();
				dlgFinish();
			}
		});
		Button btnCancel = (Button) loginLayout1.findViewById(R.id.btn_cancel);
		btnCancel.setText("继续支付");
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (exitDialog != null && exitDialog.isShowing())
					exitDialog.dismiss();
			}
		});
		exitDialog.show();
	}
}
