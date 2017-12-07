/**
 * VersionInfo.java
 * com.heyijoy.gamesdk.data
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-2-20 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.data;

import com.heyijoy.gamesdk.act.GameSDKApplication;

/**
 * @author msh
 * @since Ver 1.1
 * @Date 2014-2-20 上午10:03:00
 */
public class VersionInfo extends Bean {
	/**
	 * @since Ver 1.1
	 */

	private static final long serialVersionUID = 1L;
	private String versionCode;// 版本号
	private String versionName;// 版本名称
	private String versionDetail;// 版本描述
	private String updateFlag;// 是否必须升级 must 必须升级 no 不是必须升级 can 可以升级
	private String updateUrl;// 升级地址
	private Long remoteSize;// 文件大小
	private String authenticate = "on";//实名认证开关

	
	public String getAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(String authenticate) {
		this.authenticate = authenticate;
	}

	public Long getRemoteSize() {
		return remoteSize;
	}

	public void setRemoteSize(Long remoteSize) {
		this.remoteSize = remoteSize;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionDetail() {
		return versionDetail;
	}

	public void setVersionDetail(String versionDetail) {
		this.versionDetail = versionDetail;
	}

	public String getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public String getInstallFileName() {
		return "heyijoy.apk";
		// return
		// GameSDKApplication.getInstance().getAppname()+"#"+getVersionCode()+".apk";
	}

	public String getAppName() {
		return GameSDKApplication.getInstance().getAppname();
	}
}
