package com.heyijoy.sdk.plugin;

import com.heyijoy.sdk.IUser;
import com.heyijoy.sdk.PluginFactory;
import com.heyijoy.sdk.impl.SimpleDefaultUser;
import com.heyijoy.sdk.UserExtraData;

/**
 * 用户插件
 * @author xiaohei
 *
 */
public class HeyiJoyUser{
	private static HeyiJoyUser instance;
	
	private IUser userPlugin;
	
	private HeyiJoyUser(){
		
	}
	
	public void init(){
		this.userPlugin = (IUser)PluginFactory.getInstance().initPlugin(IUser.PLUGIN_TYPE);
		if(this.userPlugin == null){
			this.userPlugin = new SimpleDefaultUser();
		}
	}
	
	public static HeyiJoyUser getInstance(){
		if(instance == null){
			instance = new HeyiJoyUser();
		}
		
		return instance;
	}
	
	/**
	 * 是否支持某个方法
	 * @param method
	 */
	public boolean isSupport(String method){
		if(userPlugin == null){
			return false;
		}
		return userPlugin.isSupportMethod(method);
	}
	
	/**
	 * 登录接口
	 */
	public void login(){
		if(userPlugin==null){
			return;
		}
		
		userPlugin.login();
	}
	
	public void login(String customData){
		if(userPlugin == null){
			return;
		}
		userPlugin.loginCustom(customData);
	}
	
	public void switchLogin(){
		if(userPlugin == null){
			return;
		}
		
		userPlugin.switchLogin();
	}
	
	public void showAccountCenter(){
		if(userPlugin == null){
			return;
		}
		
		userPlugin.showAccountCenter();
	}
	
	/**
	 * 退出当前帐号
	 */
	public void logout() {
		if (userPlugin == null) {
			return;
		}
		
		userPlugin.logout();
	}
	
	/***
	 * 提交扩展数据，角色登录成功之后，需要调用
	 * @param extraData
	 */
	public void submitExtraData(UserExtraData extraData){
		if(this.userPlugin == null){
			return;
		}
		
		userPlugin.submitExtraData(extraData);
	}
	
	/**
	 * SDK退出接口，有的SDK需要在退出的时候，弹出SDK的退出确认界面。
	 * 如果SDK不需要退出确认界面，则弹出游戏自己的退出确认界面
	 */
	public void exit(){
		if(this.userPlugin == null){
			return;
		}
		userPlugin.exit();
	}
	
	/**
	 * 上传礼包兑换码
	 * @param code
	 */
	public void postGiftCode(String code){
		if(this.userPlugin == null){
			return;
		}
		userPlugin.postGiftCode(code);
	}
}
