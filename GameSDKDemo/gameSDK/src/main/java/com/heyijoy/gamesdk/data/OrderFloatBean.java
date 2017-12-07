package com.heyijoy.gamesdk.data;

/**
 * 保存悬浮窗接口的一级目录、二级目录的信息
 * @author andong_yt
 *
 */
//礼包1，录屏2，论坛3， 消息4 ，切换账号5，绑定手机6，合乐智趣会员7，领取码8，交易明细9 ，积分任务10，福利11，奖品兑换入口12
public class OrderFloatBean extends Bean{
	private String orderFirst="" ; 
	private String orderSecond="" ;
	public String getOrderFirst() {
		return orderFirst;
	}
	public void setOrderFirst(String orderFirst) {
		this.orderFirst = orderFirst;
	}
	public String getOrderSecond() {
		return orderSecond;
	}
	public void setOrderSecond(String orderSecond) {
		this.orderSecond = orderSecond;
	}  
	
	 
}
