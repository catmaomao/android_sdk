package com.heyijoy.gamesdk.data;

public class HYPayBean {
	

	private String amount;

	// 可选参数，应用订单号，应用内必须唯一，最大32字符。
	private String appOrderId;

	// 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
	private String notifyUri;

	// 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
	private String productName;

	// 必需参数，购买商品的商品id，应用指定，最大16字符。
	private String productId;

	// 可选参数，应用扩展信息1，原样返回，最大255字符。
	private String appExt1;


	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getNotifyUri() {
		return notifyUri;
	}

	public void setNotifyUri(String notifyUri) {
		this.notifyUri = notifyUri;
	}

	public String getAppExt1() {
		return appExt1;
	}

	public void setAppExt1(String appExt1) {
		this.appExt1 = appExt1;
	}


	public String getAppOrderId() {
		return appOrderId;
	}

	public void setAppOrderId(String appOrderId) {
		this.appOrderId = appOrderId;
	}

}
