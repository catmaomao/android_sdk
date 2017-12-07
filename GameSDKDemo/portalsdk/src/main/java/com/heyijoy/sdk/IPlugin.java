package com.heyijoy.sdk;

public interface IPlugin {

	/**
	 * 是否支持某个接口
	 * @param methodName
	 * @return
	 */
	public boolean isSupportMethod(String methodName);
	
}
