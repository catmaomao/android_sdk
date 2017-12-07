package com.heyijoy.gamesdk.data;

import java.util.ArrayList;
import java.util.List;

public class OperatorBean {
	private String cardDesc = "";
	private String cardOperator = "";
	private String cardType = "";
	private List<String> cardAmountList = new ArrayList<String>();
	public String getCardDesc() {
		return cardDesc;
	}
	public void setCardDesc(String cardDesc) {
		this.cardDesc = cardDesc;
	}
	public String getCardOperator() {
		return cardOperator;
	}
	public void setCardOperator(String cardOperator) {
		this.cardOperator = cardOperator;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public List<String> getCardAmountList() {
		return cardAmountList;
	}
	public void setCardAmountList(List<String> cardAmountList) {
		this.cardAmountList = cardAmountList;
	}
}
