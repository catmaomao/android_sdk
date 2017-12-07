package com.heyijoy.gamesdk.http;

/**
 * @author   msh
 * @version  
 * @Date	 2014	2014-2-17		上午11:27:06
 */
public interface IHttpRequest {
	
	public void request(HttpIntent httpIntent, IHttpRequestCallBack callBack);
	
	/**
	 * 获得接口未解析时的数据String
	 * @return
	 */
	public String getDataString();
	
	public void cancel();
	
	public void setCookie(String cookie);
	
	public interface IHttpRequestCallBack{
		
		public void onSuccess(HttpRequestManager httpRequestManager);
		
		public void onFailed(int code,String failReason);
//		public void onCancel();
	}

}
