package com.heyijoy.gamesdk.data;

public class InitParams {

	private int is_Offline;//是否单机，ture单机，false网游
	private int sdk_version_code;//sdk版本号
	private int channel_id;//渠道id
	private int appid;
	private String appkey;
	public int getIs_Offline() {
		return is_Offline;
	}
	public void setIs_Offline(int is_Offline) {
		this.is_Offline = is_Offline;
	}
	public int getSdk_version_code() {
		return sdk_version_code;
	}
	public void setSdk_version_code(int sdk_version_code) {
		this.sdk_version_code = sdk_version_code;
	}
	public int getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}
	public int getAppid() {
		return appid;
	}
	public void setAppid(int appid) {
		this.appid = appid;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
}
