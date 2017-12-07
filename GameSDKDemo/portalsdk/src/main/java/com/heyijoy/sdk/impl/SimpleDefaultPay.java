package com.heyijoy.sdk.impl;

import android.widget.Toast;

import com.heyijoy.sdk.IPay;
import com.heyijoy.sdk.PayParams;
import com.heyijoy.sdk.HeyiJoySDK;

public class SimpleDefaultPay implements IPay{

	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}

	@Override
	public void pay(PayParams data) {
		Toast.makeText(HeyiJoySDK.getInstance().getContext(), "调用[支付接口]接口成功，PayParams中的参数，除了extension，其他的请都赋值，最后还需要经过打包工具来打出最终的渠道包", Toast.LENGTH_LONG).show();
	}

}
