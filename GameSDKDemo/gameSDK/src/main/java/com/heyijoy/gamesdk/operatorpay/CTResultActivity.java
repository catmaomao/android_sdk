package com.heyijoy.gamesdk.operatorpay;

import com.zb.feecharge.FeeChargeManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class CTResultActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		FeeObserver feeObserver = OpPay.feeObserver;
		String providerName = getIntent().getStringExtra("providerName");
		//统一运营商支付方法调用
		java.util.Map<String, Object> map=FeeChargeManager.getInstance().feeChargeOP(this, 
					MdoSmsService.class, providerName);
		if(map.get("resultType")!=null){
			//处理移动Mdo计费返回processId; 在feeObserver类中会处理移动mdo短信计费的结果,并且发送mdo计费成功的广播
			Long processid = Long.parseLong((String) map.get("result"));
			feeObserver.setProcessID("MDO", processid);
		}
	}
	/**
	 * 计费支付返回接收方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//回调逻辑(注意电信计费：用户 必须在电信计费成功页面上点“返回”按钮 才行 )
		int payresult=FeeChargeManager.getInstance().feeCharge_TC_return(requestCode,
				resultCode, data);
		finish();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		return super.onKeyDown(keyCode, event);
	}
}
