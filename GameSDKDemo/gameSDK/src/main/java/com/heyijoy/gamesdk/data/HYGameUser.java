/**
 * HYGameUser.java
 * com.heyijoy.gamesdk.data
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-3-19 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.data;
/**
 * ClassName:HYGameUser
 * 提供给游戏厂商的User
 * @author   msh
 * @version  
 * @since    Ver 1.1
 * @Date	 2014-3-19		下午1:56:03
 *
 */
public class HYGameUser extends Bean{
	private static final long serialVersionUID = 8765470977492701819L;
	private String userName;//用户名
	private String session;//用于后台验证获取uid，由合乐智趣游戏平台产生，有失效日期
	private String thirdUsername;//三方昵称
	private String thirdHeadImgUrl;//三方头像链接
	
	public String getThirdUsername() {
		return thirdUsername;
	}
	public void setThirdUsername(String thirdUsername) {
		this.thirdUsername = thirdUsername;
	}
	public String getThirdHeadImgUrl() {
		return thirdHeadImgUrl;
	}
	public void setThirdHeadImgUrl(String thirdHeadImgUrl) {
		this.thirdHeadImgUrl = thirdHeadImgUrl;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	
}

