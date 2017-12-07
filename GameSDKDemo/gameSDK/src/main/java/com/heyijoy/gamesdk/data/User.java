package com.heyijoy.gamesdk.data;

/**
 * ClassName:User
 *
 * @author msh
 * @version
 * @since Ver 1.1
 * @Date 2014-2-17 上午10:04:14
 */
public class User extends Bean {
	private static final long serialVersionUID = 1L;
	private String userName = "";// 用户名
	private String password;// 密码
	private String new_password;// 新密码
	private String userAvatar;// 用户头像url
	private String sex;// 性别
	private String address;// 地址
	private String nick;// 昵称
	private String uid = "";//
	private String isYoukuAccount;// 是否是合乐智趣帐号 0 合乐智趣已有帐号 1手机注册新帐号
	private String isNewUser;
	private String session;
	private String verifyNo;
	private String state;
	private boolean isRemote;
	private String phone;// 手机号
	private boolean isPay;// 是否支付过
	private boolean isCertification;// 是否实名认证过
	private String thirdparty = "";// 识别三方登录qq,wx,weibo
	private String thirdparty_credential;// 相当于access_token
	private String lifecode;
	private boolean isAuto = false;
	private String bind3rd;//游客账号是否绑定三方登录
	private String picPwd;
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

	public String getPicPwd() {
		return picPwd;
	}

	public void setPicPwd(String picPwd) {
		this.picPwd = picPwd;
	}

	public String getBind3rd() {
		return bind3rd;
	}

	public void setBind3rd(String bind3rd) {
		this.bind3rd = bind3rd;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public String getLifecode() {
		return lifecode;
	}

	public void setLifecode(String lifecode) {
		this.lifecode = lifecode;
	}

	public String getThirdparty() {
		return thirdparty;
	}

	public void setThirdparty(String thirdparty) {
		this.thirdparty = thirdparty;
	}

	public String getThirdparty_credential() {
		return thirdparty_credential;
	}

	public void setThirdparty_credential(String thirdparty_credential) {
		this.thirdparty_credential = thirdparty_credential;
	}

	public boolean isCertification() {
		return isCertification;
	}

	public void setCertification(boolean isCertification) {
		this.isCertification = isCertification;
	}

	public boolean getIsPay() {
		return isPay;
	}

	public void setIsPay(boolean isPay) {
		this.isPay = isPay;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}

	public boolean isRemote() {
		return isRemote;
	}

	public void setRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getVerifyNo() {
		return verifyNo;
	}

	public void setVerifyNo(String verifyNo) {
		this.verifyNo = verifyNo;
	}

	public String getIsYoukuAccount() {
		return isYoukuAccount;
	}

	public void setIsYoukuAccount(String isYoukuAccount) {
		this.isYoukuAccount = isYoukuAccount;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIsNewUser() {
		return isNewUser;
	}

	public void setIsNewUser(String isNewUser) {
		this.isNewUser = isNewUser;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public HYGameUser toYkGameUser() {
		HYGameUser hyGameUser = new HYGameUser();
		hyGameUser.setSession(session);
		hyGameUser.setUserName(userName);
		return hyGameUser;
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + ", new_password=" + new_password
				+ ", userAvatar=" + userAvatar + ", sex=" + sex + ", address=" + address + ", nick=" + nick + ", uid="
				+ uid + ", isYoukuAccount=" + isYoukuAccount + ", isNewUser=" + isNewUser + ", session=" + session
				+ ", verifyNo=" + verifyNo + ", state=" + state + ", isRemote=" + isRemote + ", phone=" + phone
				+ ", isPay=" + isPay + "]";
	}

}
