package com.heyijoy.sdk.plugin;

import android.util.Log;

import com.heyijoy.sdk.IShare;
import com.heyijoy.sdk.PluginFactory;
import com.heyijoy.sdk.ShareParams;

/**
 * 用户分享
 * @author xiaohei
 *
 */
public class HeyiJoyShare {

	private static HeyiJoyShare instance;
	
	private IShare sharePlugin;
	
	private HeyiJoyShare(){
		
	}
	
	public static HeyiJoyShare getInstance(){
		if(instance == null){
			instance = new HeyiJoyShare();
			
		}
		return instance;
	}
	
	public void init(){
		this.sharePlugin = (IShare)PluginFactory.getInstance().initPlugin(IShare.PLUGIN_TYPE);
	}
	
	public boolean isSupport(String method){
		if(isPluginInited()){
			return this.sharePlugin.isSupportMethod(method);
		}
		return false;
	}
	
	/**
	 * 分享接口
	 * @param params
	 */
	public void share(ShareParams params){
		if(isPluginInited()){
			this.sharePlugin.share(params);
		}
	}
	
	private boolean isPluginInited(){
		if(this.sharePlugin == null){
			Log.e("HeyiJoySDK", "The share plugin is not inited or inited failed.");
			return false;
		}
		return true;
	}
	
}
