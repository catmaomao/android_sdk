package com.heyijoy.gamesdk.data;

public class MsgBean extends Bean{
	private String msgId = "";
	private String msgType = "";//1.会员  2.活动  3.礼包  4.专题   5.下载   6.其他
	private String mainTitle = "";
	private String subTitle="";
	private String linkUrl = "";
	private String content = "";
	private String buttTitle = "";
	private String appIds = "";
	private String mid = "";
	private Boolean isDisplayed=false;//("0") ? false : true    false:未展示 <---> 0
	private String sendTime = "";
	private String idUrl = "";		//礼包码链接
	private String icon = "";	
	private String overdueTime = "";
	private String packageName = "";//包名
	private String arriveTime = "";		//
	private Boolean isOpen = false;	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Boolean getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}
	public void setIsDisplayed(Boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIdUrl() {
		return idUrl;
	}
	public void setIdUrl(String idUrl) {
		this.idUrl = idUrl;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getButtTitle() {
		return buttTitle;
	}
	public void setButtTitle(String buttTitle) {
		this.buttTitle = buttTitle;
	}
	public String getAppIds() {
		return appIds;
	}
	public void setAppIds(String appIds) {
		this.appIds = appIds;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getMainTitle() {
		return mainTitle;
	}
	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean getIsDisplayed() {
		return isDisplayed;
	}
	public void setIsDisplayed(boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	public String getOverdueTime() {
		return overdueTime;
	}
	public void setOverdueTime(String overdueTime) {
		this.overdueTime = overdueTime;
	}
}
