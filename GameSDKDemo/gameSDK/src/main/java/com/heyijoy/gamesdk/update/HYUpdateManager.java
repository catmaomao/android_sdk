package com.heyijoy.gamesdk.update;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.util.Logger;

import android.content.Context;

/**   
*    
* 项目名称：GameSDK   
* 类名称：HYUpdate   
* 类描述：   
* 创建人：msh  
* 创建时间：2014-2-14 下午2:44:03   
* @version        
*/
public class HYUpdateManager {
	private String appName ;//应用名
	private String dir ;//保存路径
	private Context context ;
	
	public HYUpdateManager(Context context) {
		this.context = context;
	}




/**
*TODO 未完成
*/
public boolean isNeedUpdate(){
	int localVersion = GameSDKApplication.getInstance().getVersionCode();
	Logger.e("3333333"+localVersion);
//	String remoteVersion = getRemoteVersion();
	/*if(localVersion>remoteVersion){//需要升级
		return false;
	}else{
		return true;
	}*/
	return false;
}
/**
*TODO 未完成
*/
public boolean isMustUpdate(){
	return false;
}
}
