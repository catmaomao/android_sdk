/*
 * @(#)Constant.java	 2013-3-28
 *
 * Copyright 2005-2013 YOUKU.com
 * All rights reserved.
 * 
 * YOUKU.com PROPRIETARY/CONFIDENTIAL.
 */

package com.heyijoy.gamesdk.util;

/**
 * @author   msh
 * @version  
 * @Date	 2014-2-17
 */
public class Profile {
	/**
	 * @param tag
	 *            TAG
	 * @param useragent
	 * @param context
	 */
	public static void initProfile(String tag, String useragent) {
		TAG = tag;
		User_Agent = useragent;
	}
	
	public static void initProfile(int from, String tag, String useragent) {
		initProfile(tag, useragent);
		FROM = from;
	}

	/**
	 * 设置登陆状态
	 * 
	 * @param bLogined
	 * @param cookie
	 */
	public static void setLoginState(Boolean bLogined, String cookie) {
		isLogined = bLogined;
		COOKIE = cookie;
	}

	public static final int TIMEOUT = 30000;// 网络访问超时
	public static String TAG;// log默认标签
	public static String User_Agent;// UA
	public static String COOKIE = null;// 服务器返回的 Cookie 串
	public static boolean isLogined;// 登录状态
	public static int FROM;//0 youku; 1 tudou

}
