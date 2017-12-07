package com.heyijoy.sdk.analytics;

import java.io.Serializable;

/**
 * 用户日志
 */
public class UUserLog implements Serializable{
	private static final long serialVersionUID = 1L;
	private String user_id; 	//用户ID
    private String channel_id;  //渠道ID
    private String fixed_time;  //通过服务器校正后的时间
    private String server_id; 	//服务器ID
    private String server_name;  //服务器名称
    private String role_id;      //角色ID
    private String role_name;    //角色名称
    private String user_level;   //角色等级
    private String imei;    //设备号即device_id
    private String android_id;
    private String uuid;
    private String amount;    //充值金额
    private String currency;    //货币类型，cny人民币，usd美元
    private String orderid;    //订单号
    private String status = "success";    //请求结果
    private String matrix_sdk_api_version = "0.0.1"; 
    private String matrix_sdk_lang = "java"; 
    private String matrix_sdk_platform = "android";
    private String matrix_sdk_version = "1.0.0"; 
    private String matrix_token;  	//游戏ID
    private String appkey;  //游戏唯一标识
    private String event;     //操作类型
    private long role_create_time;     //角色创建时间,从1970年到现在的时间，单位秒
    private long role_level_up_time;     //角色升级时间,从1970年到现在的时间，单位秒
    private String packageName;     //获取包名
    
//    设备信息
    private String mac;             //mac地址
    private String device_type;      //机型
    private String device_dpi;       //分辨率
    
    
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAndroid_id() {
		return android_id;
	}
	public void setAndroid_id(String android_id) {
		this.android_id = android_id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public long getRole_create_time() {
		return role_create_time;
	}
	public void setRole_create_time(long role_create_time) {
		this.role_create_time = role_create_time;
	}
	public long getRole_level_up_time() {
		return role_level_up_time;
	}
	public void setRole_level_up_time(long role_level_up_time) {
		this.role_level_up_time = role_level_up_time;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public String getDevice_type() {
		return device_type;
	}
	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}
	public String getDevice_dpi() {
		return device_dpi;
	}
	public void setDevice_dpi(String device_dpi) {
		this.device_dpi = device_dpi;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public String getFixed_time() {
		return fixed_time;
	}
	public void setFixed_time(String fixed_time) {
		this.fixed_time = fixed_time;
	}
	public String getServer_id() {
		return server_id;
	}
	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}
	public String getServer_name() {
		return server_name;
	}
	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getUser_level() {
		return user_level;
	}
	public void setUser_level(String user_level) {
		this.user_level = user_level;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMatrix_sdk_api_version() {
		return matrix_sdk_api_version;
	}
	public void setMatrix_sdk_api_version(String matrix_sdk_api_version) {
		this.matrix_sdk_api_version = matrix_sdk_api_version;
	}
	public String getMatrix_sdk_lang() {
		return matrix_sdk_lang;
	}
	public void setMatrix_sdk_lang(String matrix_sdk_lang) {
		this.matrix_sdk_lang = matrix_sdk_lang;
	}
	public String getMatrix_sdk_platform() {
		return matrix_sdk_platform;
	}
	public void setMatrix_sdk_platform(String matrix_sdk_platform) {
		this.matrix_sdk_platform = matrix_sdk_platform;
	}
	public String getMatrix_sdk_version() {
		return matrix_sdk_version;
	}
	public void setMatrix_sdk_version(String matrix_sdk_version) {
		this.matrix_sdk_version = matrix_sdk_version;
	}
	public String getMatrix_token() {
		return matrix_token;
	}
	public void setMatrix_token(String matrix_token) {
		this.matrix_token = matrix_token;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
}
