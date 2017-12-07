package com.heyijoy.gamesdk.operatorpay;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.zb.feecharge.common.IMessageDefine;
import com.zb.feecharge.observer.ProcessObserver;
import com.zb.feecharge.util.P;

import android.content.Context;
import android.content.Intent;

public class FeeObserver extends ProcessObserver{

	private Hashtable<String, String> table = new Hashtable();
	private static Context c = null;	
	public static  Intent feeSignIntent = null; // 标识计费signin状态
	//定义广播常量
	private static final String FEE_SIGNIN_OK = AppConstants.FEE_SIGNIN_OK;
	private static final String FEE_SIGNIN_ERR = AppConstants.FEE_SIGNIN_ERR;
	private static final String FEE_CHARGE_OK = AppConstants.FEE_CHARGE_OK;
	private static final String FEE_IS_CHARGED = AppConstants.FEE_IS_CHARGED;
	private static final String FEE_CHARGE_ERROR = AppConstants.FEE_CHARGE_ERROR;
	//问询是否能计费-能计费
	public static final String CAN_FEECHARGE=AppConstants.CAN_FEECHARGE;
	//问询是否能计费-不能计费
	public static final String CAN_NOT_FEECHARGE=AppConstants.CAN_NOT_FEECHARGE;
	
	public FeeObserver(Context c){
		HttpParams params = new BasicHttpParams(); 
		//mRequestHelper = new HttpRequestHelper(params);
		table =  new Hashtable<String, String>();
		P.log(this, "ApplicationContext-->" + (c == null));
		this.c = c;
	}
	
	public void setProcessID(String tag, long processID){
		table.put(processID + "", tag);
		Iterator iterator = table.keySet().iterator();
		while(iterator.hasNext()){
			P.log(this, "iterator" + iterator.next().toString());
		}
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public void handleProcessMsg(int respCode, Hashtable respData, long processID, String respMsg) {
		// TODO Auto-generated method stub
		switch(respCode){
		case IMessageDefine.MSG_RESP_CODE_DONE_SUCESS:{
			this.handleProcess(processID, respData,respMsg);
			break;
		}
		
		case IMessageDefine.MSG_RESP_CODE_DONE_FAILED:{
			// 初始化失败
//			P.d("IMessageDefine.MSG_RESP_CODE_DONE_FAILED:", respCode + "");
			//sendBroadcast(FEE_SIGNIN_ERR);
			break;
		}
		
		case IMessageDefine.MSG_RESP_COCE_DONE_PARTIAL:{
			//部分计费成功，暂时不考虑
			break;
		}
		
		case IMessageDefine.MSG_RESP_CODE_EXCEPTION:{
			//异常逻辑
			sendBroadcast(FEE_SIGNIN_ERR);
			break;//MSG_RESP_CODE_MDO_FAILED
		}
		case IMessageDefine.MSG_RESP_CODE_MDO_FAILED:{
			//移动mdo计费失败
			sendBroadcast(FEE_CHARGE_ERROR);
			break;
		}
		default:
			return;
		
		
		}
	}
	
	private void handleProcess(long processID, Hashtable respData,String respMessage){
		String tag = table.get(processID + "");
		P.log(this, "----handleProcess------tag=" + tag);
		if(tag != null){
//			if(tag.equals("FeeCharge")){//  wap 解析计费 客户端认为已经计费成功了
//				P.log(this, " ============FeeCharge done="+tag);
//				Toast.makeText(c, "去调用计费逻辑了", 2000).show();
//				sendBroadcast(FEE_CHARGE_OK);
//			}
//			if(tag.equals("COMIC")) {//动漫的计费逻辑  
//				
//				String orderStatus = respData.get(IMessageDefine.MSG_FEECODE_CHARGE_CHECK).toString();
//				
//				if(orderStatus.equals(IMessageDefine.MSG_RESP_CODE_DONE_SUCESS)) {
//		 
//					sendBroadcast(FEE_CHARGE_OK);
//				}
//			}
//			if(tag.equals("CMCC")) {//视频基地apk的计费逻辑   
//				if(respMessage.equals("Response Indicator: 3")){
//					P.log(this, " =========tag is null 视频基地插件计费="+respMessage.equals("Response Indicator: 3")+"  tag:"+tag);
//					sendBroadcast(FEE_CHARGE_OK);
//				}
//			}
			if(tag.equals("MDO")){//  mdo移动短信计费成功
				P.log(this, " MDO done success="+tag);
				P.log(this, "  已经去短信计费后台调用计费了");
				sendBroadcast(FEE_CHARGE_OK);
			}
			//计费返回发送广播信息
			if(tag.equals("Signin")){ //将来重构到中
				P.log(this, "signinOK");
				if(respData!=null){
					String orderStatus = respData.get(IMessageDefine.MSG_FEECODE_CHARGE_CHECK).toString();
//					P.d("----------orderStatus-->", orderStatus);
					if(orderStatus != null){
						if(orderStatus.equals(IMessageDefine.MSG_FEECODE_CHARGE_DOEN)){//已经计费了
							// do nothing
							P.log(this, "=======IMessageDefine.MSG_FEECODE_CHARGE_DOEN      FEECHARGE IS DONE="+orderStatus);
							sendBroadcast(FEE_IS_CHARGED);
						 
						}else if(orderStatus.equals(IMessageDefine.MSG_FEECODE_CHARGE_MISS)){//没有计费，需要计费
							P.log(this, "=======IMessageDefine.MSG_FEECODE_CHARGE_MISS="+orderStatus);
							sendBroadcast(FEE_SIGNIN_OK);
						}
						else {
							//部分业务收费也让发广播
							P.log(this, "=======other="+orderStatus);
							sendBroadcast(FEE_SIGNIN_OK);
						}
					}
				}else {
					//Signin 请求异常
					P.log(this, "=======Signin异常处理=");
					sendBroadcast(FEE_SIGNIN_ERR);
				}
			}
			//问询
			if(tag.equals("feeChargeQuery")){
				if(respData!=null){
					String canFee = respData.get(IMessageDefine.MSG_FEECODE_FEECHARGE_CHECK).toString();
//					P.d("----------canFee-->", canFee);
					if(canFee != null){
						if(canFee.equals(IMessageDefine.MSG_DATA_CAN_CHARGE)){//可以计费
							sendBroadcast(CAN_FEECHARGE);
						}else {
							sendBroadcast(CAN_NOT_FEECHARGE);
						}
					}
				}
			}
		} 
	}

	private void sendBroadcast(String action){
		
		Intent intent = new Intent();
		
		intent.setAction(action);		
		
		c.sendStickyBroadcast(intent);
		
		P.log(this, "broadcast send success!");
		
	}
}