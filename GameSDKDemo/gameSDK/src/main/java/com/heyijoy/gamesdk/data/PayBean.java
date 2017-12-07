package com.heyijoy.gamesdk.data;

public class PayBean extends Bean{
	private static final long serialVersionUID = 1L;
	private String params ;//订单返回结果
	private String rebateMsg ;//订单返利结果

	public String getParams() {
		return params;
	}
	public String getRebate() {
		return rebateMsg;
	}

	public void setParams(String params) {
		this.params = params;
	}
	public void setRebate(String rebateMsg) {
		this.rebateMsg = rebateMsg;
	}
	
}
