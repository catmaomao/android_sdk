package com.heyijoy.gamesdk.operatorpay;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.PayBean;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.HYProgressDlg;
import com.zb.feecharge.FeeChargeManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Button;

public class OpPay{
	//计费签到成功
	private static final String FEE_SIGNIN_OK = AppConstants.FEE_SIGNIN_OK;// "fee_signin_ok";
	//计费签到失败
	private static final String FEE_SIGNIN_ERR = AppConstants.FEE_SIGNIN_ERR;// "fee_signin_err";
	//已经记过费了
	private static final String FEE_IS_CHARGED = AppConstants.FEE_IS_CHARGED;// "fee_is_charged";
	//移动mdo计费成功
	private static final String FEE_CHARGE_OK = AppConstants.FEE_CHARGE_OK;// "fee_charge_ok";
	//移动mdo计费失败
	private static final String FEE_CHARGE_ERROR =AppConstants.FEE_CHARGE_ERROR;
	
	 
	//联通计费返回成功
	public static final String CUCC_FEE_CHAGER_OK=AppConstants.CUCC_FEE_CHAGER_OK;
	//联通计费返回失败
	public static final String CUCC_FEE_CHAGER_ERROR=AppConstants.CUCC_FEE_CHAGER_ERROR;
	//联通计费返回取消
	public static final String CUCC_FEE_CHAGER_CANCEL=AppConstants.CUCC_FEE_CHAGER_CANCEL;
	
	//电信计费返回成功		
	public static final String TC_FEE_CHAGER_OK=AppConstants.TC_FEE_CHAGER_OK;
	//电信计费返回失败
	public static final String TC_FEE_CHAGER_ERROR=AppConstants.TC_FEE_CHAGER_ERROR;
	//问询是否能计费-能计费
	public static final String CAN_FEECHARGE=AppConstants.CAN_FEECHARGE;
	//问询是否能计费-不能计费
	public static final String CAN_NOT_FEECHARGE=AppConstants.CAN_NOT_FEECHARGE;	
		
	private Button mBtn_tc = null;
	private Button mfeeQuery=null;
	
//	private EditText t_price=null;
	public static Boolean canOpPay = false;
	private Boolean isPay = false;
	private String orderId = "1";
	private String amount = "1";
	private String productName = "0";
	private PersistentHelper persistentHelper = null;
	public static FeeObserver feeObserver = null;
//	private BroadcastReceiver infoReceiver = null;
	private BroadcastReceiver queryReceiver = null;
	private BroadcastReceiver payReceiver = null;
	private SIMCardInfo siminfo ;
	private String opid=null; //运营商 1 为移动  2 为联通 3 电信
	private String bussId;//业务ID
	private HYProgressDlg payLoading ;
	private Activity activity;
	HYCallBack queryCallBack;
	HYCallBack payCallBack;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//
//		super.onCreate(savedInstanceState);
//		isPay = getIntent().getBooleanExtra("isPay", false);
//		orderId = getIntent().getStringExtra("orderId");
//		amount = getIntent().getStringExtra("amount");
//		initialize();
//		if(isPay){
//			feeChargeSignin();//计费方法
//		}else{
//			feeObserver = new FeeObserver(activity);
//			FeeChargeManager.getInstance().InitializeQuery(opid, AppConstants.CHANNEL_ID, amount);
//			long processid = FeeChargeManager.getInstance().feeChargeQuery();
//			FeeChargeManager.getInstance().registProcessObserver(feeObserver);
//			feeObserver.setProcessID("feeChargeQuery", processid);
//		}
//	}
	public void SMSPay(Activity activity , String orderId , String amount , String productName , HYCallBack payCallBack){
		this.activity = activity;
		this.orderId = orderId;
		this.amount = amount;
		this.productName = productName;
		this.payCallBack = payCallBack;
		initializePay();
		feeChargeSignin();//计费方法
	}
	public void query(Activity activity,String amount , HYCallBack queryCallBack){
//		this.orderId = orderId;
		this.activity = activity;
		this.queryCallBack = queryCallBack;
		initializeQuery();
		feeObserver = new FeeObserver(activity);
		FeeChargeManager.getInstance().InitializeQuery(opid, "a"+GameSDKApplication.getInstance().getAppid(), amount);
		long processid = FeeChargeManager.getInstance().feeChargeQuery(activity);
		FeeChargeManager.getInstance().registProcessObserver(feeObserver);
		feeObserver.setProcessID("feeChargeQuery", processid);
	}


//	protected void onDestroy() {
//		super.onDestroy();
//
//		if (persistentHelper != null) {
//			persistentHelper.cleanup();
//		}
//		this.unregisterReceiver(infoReceiver);
//	}
//
//
//	private void registEvent() {
//	 
//		// 手机卡计费支付请求签到
//		mBtn_tc.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				feeChargeSignin();
//			}
//		});
//		//测试问询接口，
//		mfeeQuery.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				feeObserver = new FeeObserver(activity);
//				FeeChargeManager.getInstance().InitializeQuery(opid, AppConstants.CHANNEL_ID, amount);
//				
//				long processid = FeeChargeManager.getInstance().feeChargeQuery();
//				FeeChargeManager.getInstance().registProcessObserver(feeObserver);
//				feeObserver.setProcessID("feeChargeQuery", processid);
//			}
//		});
//	 
//	}
//	
//	private void initialize() {
//		IntentFilter intentFilter = new IntentFilter();
//		infoReceiver = new InfoReceiver();
//		intentFilter.addAction(FEE_SIGNIN_ERR);
//		intentFilter.addAction(FEE_SIGNIN_OK);
//		intentFilter.addAction(FEE_CHARGE_OK);
//		intentFilter.addAction(FEE_IS_CHARGED);
//		intentFilter.addAction(FEE_CHARGE_ERROR);
//		
//		
//		intentFilter.addAction(CUCC_FEE_CHAGER_OK);
//		intentFilter.addAction(CUCC_FEE_CHAGER_ERROR);
//		intentFilter.addAction(CUCC_FEE_CHAGER_CANCEL);
//		
//		intentFilter.addAction(TC_FEE_CHAGER_OK);
//		intentFilter.addAction(TC_FEE_CHAGER_ERROR);
//		intentFilter.addAction(CAN_FEECHARGE);
//		intentFilter.addAction(CAN_NOT_FEECHARGE);
//		
//		activity.registerReceiver(infoReceiver, intentFilter);
//		//取手机卡号所对应的运营商,项目集成时候直接把这个类拷贝自己项目中，进行调用即可
//		siminfo = new SIMCardInfo(activity);
//		if (siminfo.getProvidersName().equalsIgnoreCase("中国移动")){
//			opid="1";//运营商ID
//			bussId="11";//移动计费的业务Id
//		}else if (siminfo.getProvidersName().equalsIgnoreCase("中国联通")) {
// 			opid="2";
// 			bussId="22";
//		} else if (siminfo.getProvidersName().equalsIgnoreCase("中国电信")){
//			opid="3";
//			bussId="23";
//		}
//	}
	private void initializeQuery() {
		IntentFilter intentFilter = new IntentFilter();
		queryReceiver = new InfoReceiver();
		intentFilter.addAction(CAN_FEECHARGE);
		intentFilter.addAction(CAN_NOT_FEECHARGE);
		activity.registerReceiver(queryReceiver, intentFilter);
		//取手机卡号所对应的运营商,项目集成时候直接把这个类拷贝自己项目中，进行调用即可
		siminfo = new SIMCardInfo(activity);
		if (siminfo.getProvidersName().equalsIgnoreCase("中国移动")){
			opid="1";//运营商ID
			bussId="11";//移动计费的业务Id
		}else if (siminfo.getProvidersName().equalsIgnoreCase("中国联通")) {
			opid="2";
			bussId="22";
		} else if (siminfo.getProvidersName().equalsIgnoreCase("中国电信")){
			opid="3";
			bussId="23";
		}
	}
	private void initializePay() {
		IntentFilter intentFilter = new IntentFilter();
		payReceiver = new InfoReceiver();
		intentFilter.addAction(FEE_SIGNIN_ERR);
		intentFilter.addAction(FEE_SIGNIN_OK);
		intentFilter.addAction(FEE_CHARGE_OK);
		intentFilter.addAction(FEE_IS_CHARGED);
		intentFilter.addAction(FEE_CHARGE_ERROR);
		intentFilter.addAction(CUCC_FEE_CHAGER_OK);
		intentFilter.addAction(CUCC_FEE_CHAGER_ERROR);
		intentFilter.addAction(CUCC_FEE_CHAGER_CANCEL);
		intentFilter.addAction(TC_FEE_CHAGER_OK);
		intentFilter.addAction(TC_FEE_CHAGER_ERROR);
		
		activity.registerReceiver(payReceiver, intentFilter);
		//取手机卡号所对应的运营商,项目集成时候直接把这个类拷贝自己项目中，进行调用即可
		siminfo = new SIMCardInfo(activity);
		if (siminfo.getProvidersName().equalsIgnoreCase("中国移动")){
			opid="1";//运营商ID
			bussId="11";//移动计费的业务Id
		}else if (siminfo.getProvidersName().equalsIgnoreCase("中国联通")) {
			opid="2";
			bussId="22";
		} else if (siminfo.getProvidersName().equalsIgnoreCase("中国电信")){
			opid="3";
			bussId="23";
		}
	}
	/**
	 * 接收相应的广播
	 * @author bgzhou
	 *
	 */
	private class InfoReceiver extends BroadcastReceiver {

		public InfoReceiver() {

		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String actionCode = intent.getAction();
			if (actionCode.equals(FEE_IS_CHARGED)) {// 已包月的消息
//				GlobleNotify.ShowNotifyShort("已经计过费了！,达到上限了！", activity);
				activity.removeStickyBroadcast(intent);
				if(payCallBack != null) payCallBack.onFailed(HYConstant.EXCEPTION_CODE,"已经计过费了！,达到上限了！");
				closePayReceiver();
				//后面处理客户端自己的逻辑
			} else if (actionCode.equals(FEE_SIGNIN_OK)) {
				//计费签到成功 以后就直接调用真正运营商支付的方法
//				GlobleNotify.ShowNotifyShort("请求签到成功！", activity);
				if(siminfo.getProvidersName().equals("中国电信")||(siminfo.getProvidersName().equals("中国联通")&&!Util.hasInternet(context))){//若为电信支付，需要再启动一个activity，用来接收onActivityResult传回的结果
					Intent ctIntent = new Intent(activity , CTResultActivity.class);
					ctIntent.putExtra("providerName", siminfo.getProvidersName());
					activity.startActivity(ctIntent);
				}else{
					//统一运营商支付方法调用
					java.util.Map<String, Object> map=FeeChargeManager.getInstance().feeChargeOP(activity, 
							MdoSmsService.class, siminfo.getProvidersName());
					if(map.get("resultType")!=null){
						//处理移动Mdo计费返回processId; 在feeObserver类中会处理移动mdo短信计费的结果,并且发送mdo计费成功的广播
						Long processid = Long.parseLong((String) map.get("result"));
						feeObserver.setProcessID("MDO", processid);
					}else{
						if(payCallBack != null && siminfo.getProvidersName().equals("中国移动")) payCallBack.onFailed(HYConstant.EXCEPTION_CODE,"计费失败！可能是计费计费渠道号未配置！");
					}
				}
				
				activity.getApplicationContext().removeStickyBroadcast(intent);
			}else if (actionCode.equals(FEE_SIGNIN_ERR)) {// 
				// 计费签到失败 FeeCharge SIGNIN 失败 需 提示用户
//				GlobleNotify.ShowNotifyShort("计费签到失败！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				if(payCallBack != null) payCallBack.onFailed(HYConstant.EXCEPTION_CODE,"计费签到失败！");
				closePayReceiver();
				//后面处理客户端自己的逻辑
			}else if (actionCode.equals(FEE_CHARGE_OK)) {// 计费插件计费成功
				// 移动mdo短信计费成功消息
//				GlobleNotify.ShowNotifyShort("移动Mdo请求计费成功！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				//后面处理客户端自己的逻辑
				PayBean bean = new PayBean();
				bean.setParams("请求计费成功！");
				if(payCallBack != null) payCallBack.onSuccess(bean);
				closePayReceiver();

			}else if (actionCode.equals(FEE_CHARGE_ERROR)){
				// 移动mdo短信计费失败消息
//				GlobleNotify.ShowNotifyShort("移动Mdo请求计费失败！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				if(payCallBack != null) payCallBack.onFailed(HYConstant.EXCEPTION_CODE,"计费失败！");
				closePayReceiver();
				//后面处理客户端自己的逻辑
			}else if (actionCode.equals(CUCC_FEE_CHAGER_ERROR)) { 
				// 联通沃计费失败消息
//				GlobleNotify.ShowNotifyShort("联通沃请求计费失败！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				if(payCallBack != null) payCallBack.onFailed(HYConstant.EXCEPTION_CODE,"计费失败！");
				closePayReceiver();
				//后面处理客户端自己的逻辑
			}else if (actionCode.equals(CUCC_FEE_CHAGER_CANCEL)) { 
				// 联通沃计费取消消息
//				GlobleNotify.ShowNotifyShort("联通沃请求计费取消！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				if(payCallBack != null) payCallBack.onFailed(HYConstant.EXCEPTION_CODE,"计费失败！");
				closePayReceiver();
				//后面处理客户端自己的逻辑
			}else if (actionCode.equals(CUCC_FEE_CHAGER_OK)) { 
				// 联通沃计费成功消息
//				GlobleNotify.ShowNotifyShort("联通沃请求计费成功！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				//后面处理客户端自己的逻辑
				if(payCallBack != null) payCallBack.onSuccess(null);
				closePayReceiver();

			}else if (actionCode.equals(TC_FEE_CHAGER_OK)) { 
				// 电信天翼空间计费成功消息
//				GlobleNotify.ShowNotifyShort("电信天翼空间请求计费成功！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				//后面处理客户端自己的逻辑
				if(payCallBack != null) payCallBack.onSuccess(null);
				closePayReceiver();

			}else if (actionCode.equals(TC_FEE_CHAGER_ERROR)) { 
				// 电信天翼空间计费失败消息
//				GlobleNotify.ShowNotifyShort("电信天翼空间请求计费失败！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				if(payCallBack != null) payCallBack.onFailed(HYConstant.EXCEPTION_CODE,"计费失败！");
				closePayReceiver();
				//后面处理客户端自己的逻辑
			}else if (actionCode.equals(CAN_FEECHARGE)) { 
				// 电信天翼空间计费失败消息
//				GlobleNotify.ShowNotifyShort("问询返回，可以计费！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				canOpPay = true;
				if(queryCallBack != null)queryCallBack.onSuccess(null);
				activity.removeStickyBroadcast(intent);
				closeQueryReceiver();
				//后面处理客户端自己的逻辑
			}else if (actionCode.equals(CAN_NOT_FEECHARGE)) { 
				// 电信天翼空间计费失败消息
//				GlobleNotify.ShowNotifyShort("问询返回，不可以计费！", activity);
				activity.getApplicationContext().removeStickyBroadcast(intent);
				canOpPay = false;
				if(queryCallBack != null) queryCallBack.onFailed(HYConstant.EXCEPTION_CODE,null);
				activity.removeStickyBroadcast(intent);
				closeQueryReceiver();
				//后面处理客户端自己的逻辑   支付按钮变灰
//				mBtn_tc.setBackgroundResource(R.drawable.ic_launcher);
//				mBtn_tc.setEnabled(false);
			}  
			
		}
	}
	/**
	 * 计费签到请求方法封装
	 */
	private void feeChargeSignin() {
		TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		Build bd = new Build();
		String clientID = "";
		String userID = "";
		String imei = telephonyManager.getDeviceId();
		String imsi = telephonyManager.getSubscriberId();
		String msmCenter = "";
		String user_agent = bd.MODEL;
		String phone_number = "";
		String operator = FeeChargeManager.OPERATOR_CM;
		String currentApn = APNManager.checkApn(activity.getApplicationContext());
		String netMode = null;

		if (APNManager.isWiFi(activity.getApplicationContext())) {
			netMode = FeeChargeManager.NET_ACCESS_MODE_WIFI;
		} else if (currentApn != null) {
			if (currentApn.equals("cmnet")) {
				netMode = FeeChargeManager.NET_ACCESS_MODE_NET;
			} else if (currentApn.equals("cmwap")) {
				netMode = FeeChargeManager.NET_ACCESS_MODE_WAP;// FeeChargeManager.NET_ACCESS_MODE_WAP;
			} else if (currentApn.endsWith("3gnet")) {
				netMode = FeeChargeManager.OPERATOR_CNC;
			} else {
				netMode = "other";
			}
		}else {
			netMode = "other";
		}

		feeObserver = new FeeObserver(activity);
		//获取mac地址
		String mac= IPUtil.getMacAddress(activity);
		FeeChargeManager.getInstance().Initialize(clientID, userID, imei, imsi,
				msmCenter, user_agent, phone_number, "PRD20140528167", operator,
				"a"+GameSDKApplication.getInstance().getAppid(), netMode, orderId,opid,mac
				,amount,bussId,GameSDKApplication.getInstance().getAppname(),productName);
		FeeChargeManager.getInstance().registProcessObserver(feeObserver);
		long processid = FeeChargeManager.getInstance().Signin(activity);
		feeObserver.setProcessID("Signin", processid);

	}
	
	private void closeQueryReceiver(){
		try{
			if (persistentHelper != null) {
				persistentHelper.cleanup();
			}
			if(queryReceiver!=null)activity.unregisterReceiver(queryReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void closePayReceiver(){
		try{
			if (persistentHelper != null) {
				persistentHelper.cleanup();
			}
			if(payReceiver!=null)activity.unregisterReceiver(payReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
//	/**
//	 * 计费支付返回接收方法
//	 */
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		//回调逻辑(注意电信计费：用户 必须在电信计费成功页面上点“返回”按钮 才行 )
//		int payresult=FeeChargeManager.getInstance().feeCharge_TC_return(requestCode,
//				resultCode, data);
//
//	}
	
}
