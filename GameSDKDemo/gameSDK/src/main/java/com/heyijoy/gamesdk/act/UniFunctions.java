package com.heyijoy.gamesdk.act;

import android.app.Activity;
import android.widget.Toast;

public class UniFunctions {
//	private static LoginDialog dlg;
	public static void reLogin(final Activity activity){
		Toast.makeText(activity,"帐号身份已过期，请退出后重新登录！", 3000).show();
		//用户cookie失效时，不清除用户，而是把cookie清除掉
		GameSDKApplication.getInstance().saveCookie("");//清除cookie
//		HYCallBack callback = new HYCallBack() {
//			@Override
//			public void onSuccess(Bean bean) {
//				dlg.dismiss();
//			}
//			@Override
//			public void onFailed(String failReason) {
////				dlg.dismiss();
//				Toast.makeText(activity, failReason, 2000).show();
//			}
//		};   
//		dlg = new LoginDialog(activity, callback, false);
//		dlg.show();
	}
}
