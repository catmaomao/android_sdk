package com.heyijoy.sdk.verify;

public class UToken {

	private boolean suc;
	private int userID;
	private String sdkUserID;
	private String username;
	private String sdkUsername;
	private String token;
	private String extension;
	private int state;
	
	public UToken(){
		this.suc = false;
	}
	
	public UToken(int state,int userID, String sdkUserID, String username, String sdkUsername, String token, String extension){
		this.state = state;
		this.userID = userID;
		this.sdkUserID = sdkUserID;
		this.username = username;
		this.sdkUsername = sdkUsername;
		this.token = token;
		this.extension = extension;
		this.suc = true;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getSdkUserID() {
		return sdkUserID;
	}
	public void setSdkUserID(String sdkUserID) {
		this.sdkUserID = sdkUserID;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public boolean isSuc() {
		return suc;
	}

	public void setSuc(boolean suc) {
		this.suc = suc;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSdkUsername() {
		return sdkUsername;
	}

	public void setSdkUsername(String sdkUsername) {
		this.sdkUsername = sdkUsername;
	}

	@Override
	public String toString() {
		return "UToken [suc=" + suc + ", userID=" + userID + ", sdkUserID=" + sdkUserID + ", username=" + username
				+ ", sdkUsername=" + sdkUsername + ", token=" + token + ", extension=" + extension + "]";
	}
}
