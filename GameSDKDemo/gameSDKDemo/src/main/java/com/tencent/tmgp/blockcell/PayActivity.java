package com.tencent.tmgp.blockcell;

import java.util.ArrayList;
import java.util.List;

import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.HYPayBean;
import com.tencent.tmgp.blockcell.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 支付
 * @author shaohuma
 *
 */
public class PayActivity extends Activity {
	private ProgressDialog dlg;
	private String gameOrderId;
	private EditText amountEdit;
	private EditText orderIdEdit;
	private EditText notifyUrlEdit;
	private EditText pdtIdEdit;
	private EditText pdtNameEdit;
	private Button getOidBt ;
	private Button commitBt ;
	private HYPayBean ykPayBean = new HYPayBean();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		init();
	}
	private void init(){
		amountEdit = (EditText)findViewById(R.id.demo_pay_amount_edit);
		orderIdEdit = (EditText)findViewById(R.id.demo_pay_orderid_edit);
		notifyUrlEdit = (EditText)findViewById(R.id.demo_pay_notify_url_edit);
		pdtIdEdit = (EditText)findViewById(R.id.demo_pay_productid_edit);
		pdtNameEdit = (EditText)findViewById(R.id.demo_pay_productname_edit);
		getOidBt = (Button)findViewById(R.id.demo_pay_get_orderid_bt);
		commitBt = (Button)findViewById(R.id.demo_pay_commit_bt);
		getOidBt.setOnClickListener(new GetOidOnClickListener());
		commitBt.setOnClickListener(new CommitOnClickListener());
		
		//给edittext赋值
		amountEdit.setText("1");
		orderIdEdit.setText("defaultapporderid");
		notifyUrlEdit.setText("http://10.105.28.41:9999/notice");
		pdtIdEdit.setText("123456789");
		pdtNameEdit.setText("番茄锤");
	}
	
	/**
	 * 以下一直到下一个“分割线”为模拟cp获得游戏订单号的方法（*仅供参考，cp可以依自己的风格自行编写*）
	 * @author Administrator
	 *
	 */
	//获得cp订单号
	class GetOidOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			getGameOrderId();
		}
	}
	private void getGameOrderId(){
		dlg = ProgressDialog.show(this, null, "Please wait...", true, true);
		new Thread() {
		public void run() {
			ApplicationInfo appInfo = null;
			String appID="";
			try {
				appInfo = getPackageManager().getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
					appID=appInfo.metaData.getInt("HYGAME_APPID")+"";
				} catch (Exception e) {
					appID=appInfo.metaData.getString("HYGAME_APPID");
				}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("uid", MainActivity.cp_uid)); 
			params.add(new BasicNameValuePair("cp_gameId", appID)); 
			params.add(new BasicNameValuePair("cp_goodsId", pdtIdEdit.getText().toString())); 
			params.add(new BasicNameValuePair("cp_goodsName", pdtNameEdit.getText().toString())); 
			params.add(new BasicNameValuePair("cp_amount", amountEdit.getText().toString()));
			params.add(new BasicNameValuePair("callback_url", notifyUrlEdit.getText().toString()));
			String uri = YKConstants.GET_ORDERID_URI;//模拟获取cp订单地址
			String result = "";
			result = HttpTool.post(uri, params);
	        
			Message msg = new Message();
			msg.what = 1;
			msg.obj = result;
			mHandler.sendMessage(msg);
			}
		}.start();
	}
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case 1:{
					String result = (String) msg.obj;
					try {
						if(result != null && !result.equals("")){
							JSONObject json =  new JSONObject(result);
							gameOrderId = json.getString("apporderID");
							orderIdEdit.setText(gameOrderId);
						}else{
							Toast.makeText(PayActivity.this, "获取游戏订单号失败", 2000).show();
						}
						
						dlg.dismiss();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
					break;
				default:
					break;
			}
		};
	};
	
	/**
	 * 以上为模拟cp获得游戏订单号的方法（*仅供参考，cp可以依自己的风格自行编写*）
	 * --------------------------------------------------------------华丽的分割线
	 * 以下为cp接入支付方法
	 * @author Administrator
	 *
	 */
	//点击执行支付
	class CommitOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			setParas();//添加支付请求参数
			HYPlatform.doPay(PayActivity.this , ykPayBean , ykCallBack);//****支付接口，合乐智趣支付方法，需要UI线程调用*****
		}
	}
	
	
	
	//将EditText中的值付给ykPayBean
	//若支付宝打不开，一直loading，检查一下\yk_lib\libs\下的三个“armeabi”、“armeabi-v7a”、“x86”文件夹下面的.so文件是不是丢失了，若丢失手动copy进来即可。
	//若游戏工程也含有so文件，需要将\yk_lib\libs\下的so文件整合到游戏工程下面
	private void setParas(){
		ykPayBean.setAmount(amountEdit.getText().toString());//金额（以分为单位，只能传整数值，不能有小数）
		ykPayBean.setAppOrderId(orderIdEdit.getText().toString());////cp自己生成的订单号，不能为空，不能重复（若是单机游戏没有订单号，则传"defaultapporderid"）
		ykPayBean.setNotifyUri(notifyUrlEdit.getText().toString());//cp的支付回调通知地址，不能为空，（目前合乐智趣后台不提供设置通知地址的功能）
		ykPayBean.setProductId(pdtIdEdit.getText().toString());//cp的物品ID（没有可以传"0"）
		ykPayBean.setProductName(pdtNameEdit.getText().toString());//物品名称（没有就传"游戏道具"）
//		ykPayBean.setAppExt1("");//cp透传参数（没有透传参数就注销本行,支持最多64位,不支持中文）
		
	}
	
	//支付完成后的回调函数
	HYCallBack ykCallBack = new HYCallBack(){
		@Override
		public void onSuccess(Bean bean) {
			Toast.makeText(PayActivity.this, "操作成功", 2000).show();
		}
		@Override
		public void onFailed(int code ,String failReason) {
			//进行支付失败操作，failReason为失败原因
			Toast.makeText(PayActivity.this, failReason, 2000).show();
		}
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		HYPlatform.exceptionYKFinish(PayActivity.this);
		
	}
}
