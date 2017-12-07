package com.heyijoy.gamesdk.data;

public class VipBean extends Bean {
	Boolean isVipGood = true;
	String isVip = "0";
	String vipMsg;
	String vipUrl;
	String vipSwitch = "0";
	String vipPkgSwitch = "0";
	String consumeBenefitSwitch = "0";
	String vipScheme;
	String vipYtid;
	String vipSendPkgMsg;
	String forumSwitch;
	String forumUrl;//论坛url
	String userAvatar;//用户头像，值为 用户头像 链接
	String userName;//用户名
	public Boolean getIsVipGood() {
		return isVipGood;
	}
	public void setIsVipGood(Boolean isVipGood) {
		this.isVipGood = isVipGood;
	}
	public String getVipSendPkgMsg() {
		return vipSendPkgMsg;
	}
	public void setVipSendPkgMsg(String vipSendPkgMsg) {
		this.vipSendPkgMsg = vipSendPkgMsg;
	}
	public String getVipYtid() {
		return vipYtid;
	}
	public void setVipYtid(String vipYtid) {
		this.vipYtid = vipYtid;
	}
	public String getIsVip() {
		return isVip;
	}
	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}
	public String getVipMsg() {
		return vipMsg;
	}
	public void setVipMsg(String vipMsg) {
		this.vipMsg = vipMsg;
	}
	public String getVipUrl() {
		return vipUrl;
	}
	public void setVipUrl(String vipUrl) {
		this.vipUrl = vipUrl;
	}
	public String getVipSwitch() {
		return vipSwitch;
	}
	public void setVipSwitch(String vipSwitch) {
		this.vipSwitch = vipSwitch;
	}
	public String getVipPkgSwitch() {
		return vipPkgSwitch;
	}
	public void setVipPkgSwitch(String vipPkgSwitch) {
		this.vipPkgSwitch = vipPkgSwitch;
	}
	public String getConsumeBenefitSwitch() {
		return consumeBenefitSwitch;
	}
	public void setConsumeBenefitSwitch(String consumeBenefitSwitch) {
		this.consumeBenefitSwitch = consumeBenefitSwitch;
	}
	public String getVipScheme() {
		return vipScheme;
	}
	public void setVipScheme(String vipScheme) {
		this.vipScheme = vipScheme;
	}
	public String getForumSwitch() {
		return forumSwitch;
	}
	public void setForumSwitch(String forumSwitch) {
		this.forumSwitch = forumSwitch;
	}
	public String getForumUrl() {
		return forumUrl;
	}
	public void setForumUrl(String forumUrl) {
		this.forumUrl = forumUrl;
	}
	public String getUserAvatar() {
		return userAvatar;
	}
	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
