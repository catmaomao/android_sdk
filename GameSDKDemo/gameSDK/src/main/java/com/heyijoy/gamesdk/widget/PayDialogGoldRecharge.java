package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.HYPayBean;
import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PayDialogGoldRecharge {
	private Activity activity ;
	private HYProgressDlg payLoading ;
	private HYCallBack rechaCallBack;
	private LinearLayout payLayout;
	private TextView txtGoldrecha10 , txtGoldrecha20 , txtGoldrecha30 , txtGoldrecha50 , txtGoldrecha100 , txtGoldrecha300;
	private TextView btGoldDidRecharge , txtGoldRechaProtocol , btCommit;
	private EditText editOthernum;
	public PayDialogGoldRecharge(Activity activity , LinearLayout payLayout , HYCallBack rechaCallBack , HYProgressDlg payLoading){
		this.activity = activity;
		this.payLayout = payLayout;
		this.rechaCallBack = rechaCallBack;
		this.payLoading = payLoading;
		init();
	}
	private void init(){
		txtGoldrecha10 = (TextView)payLayout.findViewById(R.id.hy_pay_recharge_line1_linear);//选择10元充值
		txtGoldrecha20 = (TextView)payLayout.findViewById(R.id.hy_pay_goldrecha_20yuan_txt);//选择20元充值
		txtGoldrecha30 = (TextView)payLayout.findViewById(R.id.hy_pay_goldrecha_30yuan_txt);//选择30元充值
		txtGoldrecha50 = (TextView)payLayout.findViewById(R.id.hy_pay_goldrecha_50yuan_txt);//选择50元充值
		txtGoldrecha100 = (TextView)payLayout.findViewById(R.id.hy_pay_goldrecha_100yuan_txt);//选择100元充值
		txtGoldrecha300 = (TextView)payLayout.findViewById(R.id.hy_pay_goldrecha_300yuan_txt);//选择300元充值
		
		btGoldDidRecharge = (TextView)payLayout.findViewById(R.id.hy_pay_goldrecha_did_chongzhi_txt);/*确认充值优豆*/
		txtGoldRechaProtocol = (TextView)payLayout.findViewById(R.id.hy_pay_goldrecha_protocol_txt);/*YouKu虚拟社区币服务协议*/
		/*优豆充值输入框*/
		editOthernum = (EditText)payLayout.findViewById(R.id.hy_pay_goldrecha_othernum_edt);//其他金额
		
		/*选择优豆充值金额*/
		txtGoldrecha10.setOnClickListener(new Recharge10ClickListener());
		txtGoldrecha20.setOnClickListener(new Recharge20ClickListener());
		txtGoldrecha30.setOnClickListener(new Recharge30ClickListener());
		txtGoldrecha50.setOnClickListener(new Recharge50ClickListener());
		txtGoldrecha100.setOnClickListener(new Recharge100ClickListener());
		txtGoldrecha300.setOnClickListener(new Recharge300ClickListener());
		editOthernum.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
				unshineRechargeLogo();
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
			}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
			}
		 });
		txtGoldRechaProtocol.setOnClickListener(new GoldProtocolOnClickListener());
		btGoldDidRecharge.setOnClickListener(new DidChongzhiOnClickListener());
	}
	class Recharge10ClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			editOthernum.setText("10");
			unshineRechargeLogo();
			txtGoldrecha10.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_selected));
		}
	};
	class Recharge20ClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			editOthernum.setText("20");
			unshineRechargeLogo();
			txtGoldrecha20.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_selected));
		}
	};
	class Recharge30ClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			editOthernum.setText("30");
			unshineRechargeLogo();
			txtGoldrecha30.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_selected));
		}
	};
	class Recharge50ClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			editOthernum.setText("50");
			unshineRechargeLogo();
			txtGoldrecha50.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_selected));
		}
	};
	class Recharge100ClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			editOthernum.setText("100");
			unshineRechargeLogo();
			txtGoldrecha100.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_selected));
		}
	};
	class Recharge300ClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			editOthernum.setText("300");
			unshineRechargeLogo();
			txtGoldrecha300.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_selected));
		}
	};
	class DidChongzhiOnClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {//确认充值优豆
			rechargeBalance();
		}
	};
	class GoldProtocolOnClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {//Youku 虚拟社区币服务协议
			try {
				Intent intent = new Intent(activity, WebViewActivity.class);
				intent.putExtra("url", HYConstant.URL_GOLD_PROTO);
				activity.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	/*充值金额取消闪耀*/
	private void unshineRechargeLogo(){
		txtGoldrecha10.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_unselected));
		txtGoldrecha20.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_unselected));
		txtGoldrecha30.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_unselected));
		txtGoldrecha50.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_unselected));
		txtGoldrecha100.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_unselected));
		txtGoldrecha300.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.hy_pay_operator_unselected));
	}
	
	/**
	 * ---------------支付充值优豆--------------------------------
	 * 
	 */
	private void rechargeBalance(){
		
		//支付完成后的回调函数
		final HYPayBean rechargeBean = new HYPayBean();
		int moneyFen = 0;
		try{
			moneyFen = Integer.valueOf(editOthernum.getText().toString())*100;
		}catch(Exception e){
			if(payLoading!=null && payLoading.isShowing())payLoading.dismiss();
			Toast.makeText(activity, "金额有误，请确认后重新购买！", 3000).show();
			return;
		}
		if(moneyFen > 0){
			rechargeBean.setProductName("合乐智趣优豆");
			rechargeBean.setAmount(String.valueOf(moneyFen));
//			dlgFinish();
			PayDialog payDialog = new PayDialog(activity , rechargeBean , rechaCallBack , false , true);
			payDialog.show();
		}else{
			Toast.makeText(activity, "充值金额有误，请核对后重新输入！", 3000).show();
		}
		if(payLoading!=null && payLoading.isShowing())payLoading.dismiss();
	}
}
