/**
 * LoginCallBack.java
 * com.heyijoy.gamesdk.act
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-9-25 		msh
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.act;

import android.content.Context;

import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.User;

/**
 * ClassName:LoginCallBack
 *
 * @author   msh
 * @version  
 * @since    Ver 1.1
 * @Date	 2014-9-25		下午1:26:04
 *
 */
public class LoginCallBack implements HYCallBack{

	HYCallBack hycallBack;
	Context context;
	public LoginCallBack(HYCallBack hycallBack,Context context) {
		this.hycallBack = hycallBack;
		this.context = context;
	}
	@Override
	public void onSuccess(Bean bean) {
		User user = (User) bean;
		GameSDKApplication.getInstance().setCurrentUser(user);
		hycallBack.onSuccess(user.toYkGameUser());
	}

	@Override
	public void onFailed(int code,String failReason) {
		hycallBack.onFailed(code,failReason);
	}

}

