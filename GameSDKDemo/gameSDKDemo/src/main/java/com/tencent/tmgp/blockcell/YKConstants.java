package com.tencent.tmgp.blockcell;

import com.heyijoy.gamesdk.act.HYPlatform;

public class YKConstants {
	public static String VERIFY_URI = "http://123.126.98.120:8080/user/cp/login";//模拟cp服务器验证session地址（正式）
	public static String GET_ORDERID_URI = "http://sdk.api.gamex.mobile.youku.com/game/get_cp_apporderID";//模拟获取cp订单地址（正式地址）
	static{
		if(HYPlatform.isTest()){
			//设置环境为测试环境  默认为正式环境。
			VERIFY_URI = "http://10.105.28.41:9999/user/cp/login";//模拟cp服务器验证session地址（测试）
			GET_ORDERID_URI = "http://test.sdk.gamex.mobile.youku.com/game/get_cp_apporderID";//模拟获取cp订单地址（测试地址）
		}
	}
			
	

}
