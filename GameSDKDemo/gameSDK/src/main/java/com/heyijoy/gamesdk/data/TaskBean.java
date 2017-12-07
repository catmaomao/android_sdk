package com.heyijoy.gamesdk.data;

public class TaskBean extends Bean{
	private String id="" ; 
	private String taskType="" ; //任务类型  1-下载，2登录，3-支付
	private String status="" ; //用户完成任务状态，0-未完成，1-完成
	private String bonus="" ;//积分
	private String desc="" ; 
	private int amount=0 ; 
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBonus() {
		return bonus;
	}
	public void setBonus(String bonus) {
		this.bonus = bonus;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	
}
