package com.heyijoy.sdk.plugin;

import com.heyijoy.sdk.impl.SimpleDefaultPay;
import com.heyijoy.sdk.IPay;
import com.heyijoy.sdk.PayParams;
import com.heyijoy.sdk.PluginFactory;

/***
 * 支付插件
 * @author xiaohei
 *
 */
public class HeyiJoyPay{
	
	private static HeyiJoyPay instance;
	
	private IPay payPlugin;
	
	private HeyiJoyPay(){
		
	}
	
	public static HeyiJoyPay getInstance(){
		if(instance == null){
			instance = new HeyiJoyPay();
		}
		return instance;
	}
	
	public void init(){
		this.payPlugin = (IPay)PluginFactory.getInstance().initPlugin(IPay.PLUGIN_TYPE);
		if(this.payPlugin == null){
			this.payPlugin = new SimpleDefaultPay();
		}
	}
	
	public boolean isSupport(String method){
		if(this.payPlugin == null){
			return false;
		}
		
		return this.payPlugin.isSupportMethod(method);
	}
	
	/***
	 * 支付接口（弹出支付界面）
	 * @param data
	 */
	public void pay(PayParams data){
		if(this.payPlugin == null){
			return;
		}
		this.payPlugin.pay(data);
	}
}
