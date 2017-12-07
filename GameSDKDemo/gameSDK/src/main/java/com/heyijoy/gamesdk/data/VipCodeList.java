package com.heyijoy.gamesdk.data;

import java.util.List;


public class VipCodeList extends Bean{
	
	private static VipCodeList codeList;
	
	private  VipCodeList(){
	}
	
	public static synchronized VipCodeList getInstance(){
		if(codeList == null){
			codeList = new VipCodeList();
		}
		return codeList;
	}

	private List<VipCodeBean> codes;

	public List<VipCodeBean> getCodes() {
		return codes;
	}

	public void setCodes(List<VipCodeBean> codes) {
		this.codes = codes;
	}

}

