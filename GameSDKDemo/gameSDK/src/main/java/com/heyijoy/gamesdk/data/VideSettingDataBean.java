package com.heyijoy.gamesdk.data;

public class VideSettingDataBean extends Bean{
	private boolean isAutoUpload = false;//是否自动上传
	private boolean isRecodeVoice;//是否录制声音
	private boolean isUseDataTraffic;//是否使用数据流量
	private String definitionQuality;//清晰度，"1":超清；"2":高清；"3":标清；
	public boolean isAutoUpload() {
		return isAutoUpload;
	}
	public void setAutoUpload(boolean isAutoUpload) {
		this.isAutoUpload = isAutoUpload;
	}
	public boolean isRecodeVoice() {
		return isRecodeVoice;
	}
	public void setRecodeVoice(boolean isRecodeVoice) {
		this.isRecodeVoice = isRecodeVoice;
	}
	public boolean isUseDataTraffic() {
		return isUseDataTraffic;
	}
	public void setUseDataTraffic(boolean isUseDataTraffic) {
		this.isUseDataTraffic = isUseDataTraffic;
	}
	public String getDefinitionQuality() {
		return definitionQuality;
	}
	public void setDefinitionQuality(String definitionQuality) {
		this.definitionQuality = definitionQuality;
	}
	 
	
	 
}
