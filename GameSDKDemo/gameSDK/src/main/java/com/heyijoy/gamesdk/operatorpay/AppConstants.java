package com.heyijoy.gamesdk.operatorpay;


public interface AppConstants {
	 
	//移动mod计费产品ID  PRD20130831595
	public static final String CHARGE_PRODUCT_ID=PropertyConfig.getInstance().getString("CHARGE_PRODUCT_ID");  //"PRD20130831595";   //PRD20130720606
	//联通沃商店产品ID
	public static final String CUCC_CHARGE_PRODUCT_ID=PropertyConfig.getInstance().getString("CUCC_CHARGE_PRODUCT_ID"); 
	//电信天翼空间产品ID
	public static final String CTS_CHARGE_PRODUCT_ID=PropertyConfig.getInstance().getString("CTS_CHARGE_PRODUCT_ID"); 

	
	//定义相关广播，注释以后添加
	//计费签到成功
	public static final String FEE_SIGNIN_OK = "fee_signin_ok";
	//计费签到失败
	public static final String FEE_SIGNIN_ERR = "fee_signin_err";
	//移动mdo计费成功
	public static final String FEE_CHARGE_OK = "fee_charge_ok";
	//移动mdo计费失败
	public static final String FEE_CHARGE_ERROR = "fee_charge_error";
	//已经计过费
	public static final String FEE_IS_CHARGED = "fee_is_charged";
	//联通计费返回成功
	public static final String CUCC_FEE_CHAGER_OK="cucc_fee_charge_ok";
	//联通计费返回失败
	public static final String CUCC_FEE_CHAGER_ERROR="cucc_fee_charge_error";
	//联通计费返回取消
	public static final String CUCC_FEE_CHAGER_CANCEL="cucc_fee_charge_cancel";
	//电信计费返回成功		
	public static final String TC_FEE_CHAGER_OK="tc_fee_charge_ok";
	//电信计费返回失败
	public static final String TC_FEE_CHAGER_ERROR="tc_fee_charge_error";
	//问询是否能计费-能计费
	public static final String CAN_FEECHARGE="can_feeCharge";
	//问询是否能计费-不能计费
	public static final String CAN_NOT_FEECHARGE="can_not_feeCharge";
}
