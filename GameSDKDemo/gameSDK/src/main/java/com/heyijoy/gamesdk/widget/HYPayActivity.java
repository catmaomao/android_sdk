package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.data.HYPayBean;
import com.heyijoy.gamesdk.operatorpay.OpPay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HYPayActivity extends Activity{
	Activity activity = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HYPayBean ykPayBean = (HYPayBean) getIntent().getSerializableExtra("ykPayBean");
		Intent intent = new Intent(activity , OpPay.class);
		String amount = String.valueOf(Integer.valueOf(ykPayBean.getAmount())/100);
		intent.putExtra("amount", amount);
		activity.startActivity(intent);
//		PayDialog payDialog = new PayDialog(activity , ykPayBean , payCallBack);
//		payDialog.show();
	}
}
