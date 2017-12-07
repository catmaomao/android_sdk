package com.heyijoy.sdk;

import com.heyijoy.sdk.verify.UToken;


public interface IHeyiJoySDKListener {
	
	public void onResult(int code, String msg);
	
	public void onInitResult(InitResult result);
	
	public void onLoginResult(String data);
	
	public void onSwitchAccount();
	
	public void onSwitchAccount(String data);
	
	public void onLogout();
	
	public void onAuthResult(UToken authResult);
	
	public void onPayResult(PayResult result);
}
