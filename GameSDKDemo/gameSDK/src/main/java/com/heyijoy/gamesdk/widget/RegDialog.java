/**
 * LoginDialog.java
 * com.heyijoy.gamesdk.widget
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-2-21 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.widget;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.SMSReceiver;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.data.HYLoginFailReason;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author   msh
 * @since    Ver 1.1
 * @Date	 2014-2-21		上午10:06:37
 */
public class RegDialog extends Dialog{
	private HYProgressDlg proDlg;
	private HYDialog dlg;
	private HYCallBack method;
	private TimeCount time;//计时器
	private SMSReceiver receiver;//短信接收器
	
	private SMSReceiver receiverVerifyNo;//验证码短信接收器
	private MSGSendReceiver mReceiver01;// 短信发送状态 广播接收器
	private Button btn_reg;
	private TextView tv_reg_name;
	private Button btn_other_login;
	private Button btn_back;
	private Button btn_resend;
	private EditTextWithErr ed_mobile;
	private Context context = null; 
	private EditTextWithErr et_verify_no;
	private String mobile;
	private TextView tv_error;
	private TimeWait timerReciver;//计时器 用于快速注册接收时间
	
	private TimeMSGSend timerSend;//计时器 用于快速注册发送时间
	
	private TextView yk_no_nameregister;
	private RelativeLayout yk_have_nameregister;
	
	
	private User user = new User();
	public RegDialog(Context context) {
		super(context);
		this.context = context;
	}
	public RegDialog(Context context,HYCallBack method) {
		super(context);
		this.context = context;
		this.method = method;
	}
	@Override
	public void show() {
	 LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(
		        R.layout.dialog_hyreg, null);
	 
	 dlg = new HYDialog(context, R.style.dialog);
	 dlg.setContentView(loginLayout1);
	 
	 TextView tv_title_text = (TextView)loginLayout1.findViewById(R.id.tv_title_text);
	 tv_title_text.setText(HYConstant.TITLE_REG);
	 
	 TextView tv_hink = (TextView)loginLayout1.findViewById(R.id.reg_tv_hink);
	 tv_hink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
	 tv_hink.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			try {
				Intent i = new Intent(context, WebViewActivity.class);
				i.putExtra("url", HYConstant.AGREEMENT_URL);
				context.startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	 
	 dlg.setCanceledOnTouchOutside(false);
	 btn_reg = (Button)loginLayout1.findViewById(R.id.btn_reg);
	 btn_reg.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			msgReg();
		}
	});
	 
	 btn_other_login = (Button)loginLayout1.findViewById(R.id.btn_other_login);
	 btn_other_login.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			dlg.dismiss();
			dlg.cancel();
			LoginDialog loginDlg = new LoginDialog(context,method,true,1);
			loginDlg.show();
		}
	});
	 
	 dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
					dlg.dismiss();
					method.onFailed(HYConstant.EXCEPTION_CODE,HYLoginFailReason.CANCELED);
				}
				return false;
			}
		});
	 
	 dlg.show();
	 
	/* Window dialogWindow = dlg.getWindow();
     WindowManager.LayoutParams lp = dialogWindow.getAttributes();
     Activity activity = (Activity)context;
     WindowManager m = activity.getWindowManager();
     Display d = m.getDefaultDisplay(); // 获取	屏幕宽、高用
     if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { 
    	 lp.height = (int) (d.getHeight() * 0.6); //
    	 lp.width = (int) (d.getWidth() * 0.5); // 
    	 } else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { 
    		 lp.height = (int) (d.getWidth() * 0.6); // 
        	 lp.width = (int) (d.getHeight() * 0.5); // 
    	 }*/
//     dlg.show(300,260);
	}
	
	/**
	*短信注册
	*/
	private void msgReg(){
		proDlg = HYProgressDlg.show(context, "正在验证手机号…",true);
//		HttpApi.getInstance().regOneKeyStatistic(null,HYConstant.REG_START,null,"0");//一键注册埋点
	     String content =GameSDKApplication.getInstance().getRegMSGContent();
	     try{
//	    	 TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//	    	 int absent = manager.getSimState();  
//	    	 if (TelephonyManager.SIM_STATE_READY  != absent){  //没有手机卡
//	    		 sendFailDo();
//	    		 HttpApi.getInstance().regOneKeyStatistic(null,HYConstant.REG_NO_SIM,null,"0");//没有sim卡
//	    	 }else{ 
	    	 
    	    	// 建立自定义Action常数的Intent(给PendingIntent参数之用) 
    	        Intent itSend = new Intent("HY_SMS_SEND_ACTIOIN");
    	        //sentIntent参数为传送后接受的广播信息PendingIntent 
    	        PendingIntent mSendPI = PendingIntent.getBroadcast(context, 0, itSend, 0);
    			
    	        // 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver 
    	        IntentFilter mFilter01 = new IntentFilter("HY_SMS_SEND_ACTIOIN");
    	        mReceiver01 = new MSGSendReceiver();
    	        context.registerReceiver(mReceiver01, mFilter01);
    	        
    	        timerSend = new TimeMSGSend(HYConstant.TIME_ONE_BUTTON_MSG_SEND_WAIT, 1000);
    	        timerSend.cancel();
    	        timerSend.start();
    	        Logger.d("MSG-SEND-TIME", ""+System.currentTimeMillis());
    	        SmsManager smsManager = SmsManager.getDefault();
    	        smsManager.sendTextMessage(HYConstant.REG_MOBILE_UP_NO, null, content, mSendPI, null);	
//	    	 } 
	     }catch(Exception e){
	    	 sendFailDo();
//	    	 HttpApi.getInstance().regOneKeyStatistic(null,HYConstant.REG_SEND_API_EXCEPTION,null,"0");//发送异常
	     }
			
			
	}
	
	
	 /*自定义MSGSendReceiver重写BroadcastReceiver监听短信状态信息 */
	  public class MSGSendReceiver extends BroadcastReceiver
	  {
	    @Override
	    public void onReceive(Context context, Intent intent)
	    {
	      if (intent.getAction().equals("HY_SMS_SEND_ACTIOIN")){
	        try{
	        	timerSend.cancel();
	        	if(getResultCode()==Activity.RESULT_OK){//发送成功
	        		
//	        		 HttpApi.getInstance().regOneKeyStatistic(null,HYConstant.REG_SEND_SUCESS,null,"0");//发送成功
	        		
	        		Logger.d("MSG-SEND-SUCCESS-TIME", ""+System.currentTimeMillis());
	        		receiver = new SMSReceiver(smsReceiverHandler);
		       		IntentFilter filter = new IntentFilter();  
		       	    filter.setPriority(1000);  
		       	    filter.addAction("android.provider.Telephony.SMS_RECEIVED");  
		       	    context.registerReceiver(receiver,filter);
		       	    
//		       	    HYSocketManger.getInstance().doRequestSocket(smsReceiverHandler);
		       	    
			       	timerReciver = new TimeWait(HYConstant.TIME_ONE_BUTTON_REG_WAIT, 1000);
			       	timerReciver.start();
	        	}else{
	        		String receiptStr ="RESULT_ERROR_OTHER";
	        		if(getResultCode() == SmsManager.RESULT_ERROR_NO_SERVICE){
	        			receiptStr = "RESULT_ERROR_NO_SERVICE";
	        		}else if(getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE){
	        			receiptStr = "RESULT_ERROR_GENERIC_FAILURE";
	        		}else if(getResultCode() == SmsManager.RESULT_ERROR_NULL_PDU){
	        			receiptStr = "RESULT_ERROR_NULL_PDU";
	        		}else if(getResultCode() == SmsManager.RESULT_ERROR_RADIO_OFF){
	        			receiptStr = "RESULT_ERROR_RADIO_OFF";
	        		}
//	        		HttpApi.getInstance().regOneKeyStatistic(null,HYConstant.REG_SEND_FAIL,receiptStr,"0");//发送失败的回执
	        		sendFailDo();
	        	}
	        	try {
					context.unregisterReceiver(mReceiver01);
				} catch (Exception e) {
				}
	        }catch(Exception e){
	          sendFailDo();
	          e.getStackTrace();
	        }
	      }/*else if(intent.getAction().equals("SMS_DELIVERED_ACTION")){
	          try{
	        	  if(getResultCode()==Activity.RESULT_OK){//发送到达
	        		  Logger.d("MSG-SEND-TO-SUCCESS-TIME", ""+System.currentTimeMillis());
	        		  }
	        	  }
	          catch(Exception e){
	            e.getStackTrace();
	          }
	        } */     
	    }
	  }
	
	  private void sendFailDo(){//发送失败的处理 跳转到输入手机号注册页  超时或者没有卡 或者信号不好 或者发送被拦截 或者异常
		  Logger.v("sendFail--------");
		  if(timerSend!=null){
			  timerSend.cancel();
		  }
	     proDlg.dismiss();
	     dlg.dismiss();
	     try{
	    	 context.unregisterReceiver(mReceiver01);
	    	 receiver = null;
	     }catch(Exception e){
	     }
	     changeViewMobile(false);
	  }
	
	/**
	 * smsReceiverHandler:用于拦截验证码广播接收器的handler
	 *
	 * @since Ver 1.1
	 */
	private Handler smsReceiverHandler = new Handler(){
		private String phone;
		/**
		 *  将接收到的短信内容填充指定的text
		 */
		@Override 
		public void handleMessage(Message msg) {
			if(msg.what ==2 ){//收超时
				try {
					dlg.dismiss();
					proDlg.dismiss();
					timerReciver.cancel();
				} catch (Exception e) {
				}
		    	//时间到  跳转
		    	changeViewMobile(false);
			}else if(msg.what ==3){//发超时
//				HttpApi.getInstance().regOneKeyStatistic(null,HYConstant.REG_SEND_TIME_OUT,null,"0");//发送成功
				sendFailDo();
			}else if(msg.what == SMSReceiver.MSG_WHAT_REG_NEW_USER){
					timerReciver.cancel();
					String source = msg.getData().getString(SMSReceiver.MSG_DATA_SOURCE);
					phone = msg.getData().getString(SMSReceiver.MSG_DATA_USERNAME);
					String pwd = msg.getData().getString(SMSReceiver.MSG_DATA_PWD);
					
					User user = new User();
					user.setUserName(phone);
					GameSDKApplication.getInstance().setIsneedbind(false);
					
					if(SMSReceiver.MSG_DATA_SOURCE_MMS.equals(source)){//短信注册成功
						user.setPassword(pwd);
						login(user,true);
					}else{//socket 注册成功
						user.setVerifyNo(pwd);
						login(user,false);
					}
					
			}else if(msg.what == SMSReceiver.MSG_WHAT_REG_OLD_USER){
					timerReciver.cancel();
					proDlg.dismiss();
					proDlg = HYProgressDlg.show(context, "注册成功，正在登录…");
					String source = msg.getData().getString(SMSReceiver.MSG_DATA_SOURCE);
					String verifyNo = msg.getData().getString(SMSReceiver.MSG_DATA_VERIFYNO);
					phone = msg.getData().getString(SMSReceiver.MSG_DATA_USERNAME);
					user.setUserName(phone);
					GameSDKApplication.getInstance().setIsneedbind(false);
					
					if(SMSReceiver.MSG_DATA_SOURCE_MMS.equals(source)){
//						HttpApi.getInstance().regOneKeyStatistic(phone,HYConstant.REG_SUCESS_OLD,null,"1");//老用户
					}else{
//						Toast.makeText(context, verifyNo, Toast.LENGTH_LONG).show();
//						HttpApi.getInstance().regOneKeyStatistic(phone,HYConstant.REG_SUCESS_OLD,null,"2");//老用户
					}
					
					HYCallBack callBack = new HYCallBack() {
						@Override
						public void onSuccess(Bean bean) {
							GameSDKApplication.getInstance().saveShareUser((User)bean);
							method.onSuccess(((User)bean));
							dlg.dismiss();
							proDlg.dismiss();
							Toast.makeText(context, user.getUserName()+HYConstant.Toast_Old_User, Toast.LENGTH_SHORT).show();
							
//							HttpApi.getInstance().loginStatistic(user.getUserName(), "2");//老用户注册成功，验证码登录成功埋点
						}
						@Override
						public void onFailed(int code,String failReason) {
							proDlg.dismiss();
							dlg.dismiss();
							Toast.makeText(context, failReason, Toast.LENGTH_LONG).show();
							new LoginDialog(context, method, true,true,user,1).show();
						}
					};
					HttpApi.getInstance().loginByVerifyNo(user, verifyNo, callBack);
			}
			
//			HYSocketManger.getInstance().close();
			 try{
		    	 context.unregisterReceiver(receiver);
		    	 receiver = null;
		     }catch(Exception e){
		    	 
		     }
		}
	};
	
	private Handler smsVerifyNOReceiverHandler = new Handler(){
		/**
		 *  将接收到的短信内容填充指定的text
		 */
		@Override 
		public void handleMessage(Message msg) {
		 if(msg.what == SMSReceiver.MSG_WHAT_REG_GET_VERIFYNO){//六位的验证码
				String verifyNo = msg.getData().getString("VERIFY_NO");;
				if(et_verify_no!=null){
				et_verify_no.setText(verifyNo);
				}
			}
			 try{
		    	 context.unregisterReceiver(receiverVerifyNo);
		    	 receiverVerifyNo = null;
		     }catch(Exception e){
		     }
		}
	};
	
	/**
	 * 切换到输入手机号页面，通过输入手机号请求验证码
	 * @param isFastRegister   是否来自于快速注册 的，true：是；  false：否。
	 */
	public void changeViewMobile(boolean isFastRegister){
		
		if(!GameSDKApplication.getInstance().isPhoneRegOpen()){
			
			if(!GameSDKApplication.getInstance().isNameRegOpen())
			{
				GameSDKApplication.getInstance().setPhoneRegOpen(true);
				GameSDKApplication.getInstance().setNameRegOpen(false);
			}else
			{
				if (TelephonyManager.SIM_STATE_READY  != Util.isHasSim(context)){
					if(isFastRegister) 
					{
						new NickNameRegDialog(context, method,false).show();
					}else
					{ 
						new NickNameRegDialog(context, method,true).show();
					}
				}else
				{ 
					new NickNameRegDialog(context, method,false).show();
				} 
				return;
			} 
		}
		 
		showPhoneRegist(isFastRegister);
	
//	 dlg.show(300,260);
	}
	
	/**
	 * 手机号码注册
	 * @param isFastRegister
	 */
	public void showPhoneRegist(boolean isFastRegister){
		 LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(
			        R.layout.dialog_hy_reg_mobile, null);
		 dlg = new HYDialog(context, R.style.dialog);
		 dlg.setContentView(loginLayout1);
		 TextView tv_title_text = (TextView)loginLayout1.findViewById(R.id.tv_title_text);
		 tv_title_text.setText(HYConstant.TITLE_REG);
		 dlg.setCanceledOnTouchOutside(false);
		 tv_error = (TextView)loginLayout1.findViewById(R.id.tv_error);
		
		 if (TelephonyManager.SIM_STATE_READY  == Util.isHasSim(context))
		 {
			 tv_error.setText("验证有点问题，请使用手机号注册");
			 tv_error.setVisibility(View.VISIBLE);
		 }else
		 { 
			 if(isFastRegister)
			 {
				 tv_error.setText("验证有点问题，请使用手机号注册");
				 tv_error.setVisibility(View.VISIBLE);
			 }else
			 {  
				 tv_error.setText("");
				 tv_error.setVisibility(View.INVISIBLE);
			 }
		 }
		 
		 ed_mobile = (EditTextWithErr)loginLayout1.findViewById(R.id.et_username);
		 ed_mobile.setErrTextView(tv_error);
		 btn_reg = (Button)loginLayout1.findViewById(R.id.btn_reg);
		 btn_reg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String mobile = ed_mobile.getText().toString();
				if(Util.isMobileNO(mobile)){
					requestVerifyNo(mobile);
				}else{
					tv_error.setVisibility(View.VISIBLE);
					ed_mobile.setError("手机号格式错误");
				}
			}
		});
		 
		 yk_no_nameregister = (TextView)loginLayout1.findViewById(R.id.no_nameregister);
		 yk_have_nameregister=(RelativeLayout)loginLayout1.findViewById(R.id.have_nameregister);
		 if(GameSDKApplication.getInstance().isNameRegOpen())
		 {   
			 //有用户注册，需要显示
			 yk_no_nameregister.setVisibility(View.GONE);
			 yk_have_nameregister.setVisibility(View.VISIBLE);
			 
			 tv_reg_name = (TextView)loginLayout1.findViewById(R.id.tv_reg_name);
			 tv_reg_name.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
			 tv_reg_name.getPaint().setFakeBoldText(true); 
			 tv_reg_name.setOnClickListener(new View.OnClickListener() 
			 {
				@Override
				public void onClick(View arg0) {
					dlg.dismiss();
					NickNameRegDialog nameReg = new NickNameRegDialog(context,method,true);
					nameReg.show();
				}
			});	 
		 }else
		 {
			 yk_no_nameregister.setVisibility(View.VISIBLE);
			 yk_have_nameregister.setVisibility(View.GONE);
		 }

		 btn_other_login = (Button)loginLayout1.findViewById(R.id.btn_other_login);
		 btn_other_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				LoginDialog loginDlg = new LoginDialog(context,method,true,2);
				loginDlg.show();
			}
		});
		 
		 dlg.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
						dlg.dismiss();
						method.onFailed(HYConstant.EXCEPTION_CODE,HYLoginFailReason.CANCELED);
					}
					return false;
				}
			});
		 dlg.show();
	}
	
	
	/**
	*切换到验证码输入界面
	*输入参数方便重新发送时调用  
	*/
	private void changeViewVerify(String mobileno){
		//注册接收器
		if(receiverVerifyNo == null){
			receiverVerifyNo = new SMSReceiver(smsVerifyNOReceiverHandler);
			 IntentFilter filter = new IntentFilter();  
		     filter.setPriority(1000);  
		     filter.addAction("android.provider.Telephony.SMS_RECEIVED");  
		     context.registerReceiver(receiverVerifyNo,filter);  
		}
		
		this.mobile = mobileno;
		time = new TimeCount(HYConstant.TIME_RE_SEND_VERIFY_WAIT, 1000);//构造CountDownTimer对象
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(
		        R.layout.dialog_hy_reg_verify, null);
	 dlg = new HYDialog(context, R.style.dialog);
	 TextView tv_title_text = (TextView)loginLayout1.findViewById(R.id.tv_title_text);
	 tv_title_text.setText(HYConstant.TITLE_REG);
	 dlg.setCanceledOnTouchOutside(false);
	 dlg.setContentView(loginLayout1);
	 tv_error = (TextView)loginLayout1.findViewById(R.id.tv_error);
	 et_verify_no = (EditTextWithErr)loginLayout1.findViewById(R.id.et_verify_no);
	 et_verify_no.setErrTextView(tv_error);
	 
	 btn_reg = (Button)loginLayout1.findViewById(R.id.btn_reg);
	 btn_reg.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(et_verify_no.getText().length()<6){
				et_verify_no.setError("验证码错误");
			}else{
				sendVerifyNo(et_verify_no.getText().toString(),mobile);
			}
		}
	});
	 
	 btn_back = (Button)loginLayout1.findViewById(R.id.back);
	 btn_back.setVisibility(View.VISIBLE);
	 btn_back.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			dlg.dismiss();
			changeViewMobile(false);
			time.cancel();
		}
	});
	 btn_resend = (Button)loginLayout1.findViewById(R.id.btn_re_send);
	 btn_resend.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			requestVerifyNo(mobile);
		}
	});
	 dlg.setOnDismissListener(new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			/*try {
				if(receiver!=null){
					context.unregisterReceiver(receiver);
				}
			} catch (Exception e) {
			}*/
		}
	});
	 
	 dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
					dlg.dismiss();
					changeViewMobile(false);
					time.cancel();
				}
				return false;
			}
		});
	 
	 time.start();
	 dlg.show();
//	 dlg.show(300,260);
	}
	
	/**
	*通过手机号请求验证码
	*/
	private void requestVerifyNo(final String mobile){
		proDlg = HYProgressDlg.show(context, "正在获取验证码……");
		 HYCallBack callBack = new HYCallBack() {
				@Override
				public void onSuccess(Bean bean) {
					dlg.dismiss();
					changeViewVerify(mobile);
					proDlg.dismiss();
				}
				@Override
				public void onFailed(int code,String failReason) {
					proDlg.dismiss();
					if((HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))||(!HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))){
						Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
					}else{
						if(tv_error!=null){
							tv_error.setText(failReason);
							tv_error.setVisibility(View.VISIBLE);
						}
					}
				}
			};
//		HttpApi.getInstance().regProcessStatistic(null,mobile, "1", "1");//新用户注册申请
		HttpApi.getInstance().requestVerifyNo(mobile, callBack);
	}
	
	/**
	*登录逻辑
	*/
	private void login(final User user,final boolean isPWD){
		proDlg = proDlg.setMessage("注册成功，正在登录…");
		HYCallBack callBack = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				GameSDKApplication.getInstance().saveShareUser((User)bean);
//				HttpApi.getInstance().collectDevices("2");
				dlg.dismiss();
				proDlg.dismiss();
				Toast.makeText(context, user.getUserName()+HYConstant.Toast_New_User, Toast.LENGTH_SHORT).show();
				method.onSuccess(((User)bean));
				if(isPWD){
//					HttpApi.getInstance().loginStatistic(user.getUserName(), "1");//新用户注册成功 通过密码进行登录 成功
				}else{
//					HttpApi.getInstance().loginStatistic(user.getUserName(), "2");//验证码登录成功埋点
				}
				
			}
			@Override
			public void onFailed(int code,String failReason) {
				proDlg.dismiss();
				dlg.dismiss();
				Toast.makeText(context, failReason, Toast.LENGTH_LONG).show();
				new LoginDialog(context, method, true,true,user,1).show();
			}
		};
		if(isPWD){
			HttpApi.getInstance().yklogin(user, callBack);
		}else{
			HttpApi.getInstance().loginByVerifyNo(user, user.getVerifyNo(), callBack);
		}
		
	}
	
	/**
	*发送验证码，注册
	*/
	private void sendVerifyNo(String verifyNO,final String phoneNo){
		user.setUserName(phoneNo);
		proDlg = HYProgressDlg.show(context, "正在注册…");
		 HYCallBack callBack = new HYCallBack() {
				
				@Override
				public void onSuccess(Bean bean) {//注册成功，自动登录
					User currentU = (User)bean;
					GameSDKApplication.getInstance().setIsneedbind(false);
					if(currentU.getIsNewUser().equals("YES")){//新用户 埋点
//						HttpApi.getInstance().regVerifyStatistic(currentU.getUid(),currentU.getUserName(),"1");
//						HttpApi.getInstance().regProcessStatistic(currentU.getUid(),currentU.getUserName(), "1", "3");//新用户注册成功
						Toast.makeText(context, user.getUserName()+HYConstant.Toast_New_User, Toast.LENGTH_SHORT).show();
					}else if(currentU.getIsNewUser().equals("NO")){//老用户 埋点
//						HttpApi.getInstance().regVerifyStatistic(currentU.getUid(),currentU.getUserName(), "2");
//						HttpApi.getInstance().regProcessStatistic(currentU.getUid(),currentU.getUserName(),"1", "4");//老用户重新登录成功
						Toast.makeText(context, user.getUserName()+HYConstant.Toast_Old_User, Toast.LENGTH_SHORT).show();
					}
					
					proDlg.dismiss();
					dlg.dismiss();
					GameSDKApplication.getInstance().saveShareUser(currentU);
					method.onSuccess(((User)bean));
				}
				
				@Override
				public void onFailed(int code,String failReason) {
					proDlg.dismiss();
//					HttpApi.getInstance().regProcessStatistic(null,phoneNo,"1", "5");//注册失败
					if((!HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason))&&(
							!HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason))){
						tv_error.setText(failReason);
						tv_error.setVisibility(View.VISIBLE);
					}else{
						Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
					}
				}
			};
//		HttpApi.getInstance().regProcessStatistic(null,phoneNo,"1","2");//开始注册验证
		HttpApi.getInstance().regester(verifyNO,phoneNo, callBack);
	}
	
	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			btn_resend.setText("重新验证");
			btn_resend.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			btn_resend.setClickable(false);
			btn_resend.setText(millisUntilFinished /1000+"秒");
		}
	}
	
	
	/* 定义一个倒计时的内部类 */
	class TimeWait extends CountDownTimer {
		public TimeWait(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			 Message message = new Message();  
	            message.what = 2;  
	            smsReceiverHandler.sendMessage(message); 
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
		}
	}
	
	/* 定义一个倒计时的内部类 倒计时短信是否发送成功 */
	class TimeMSGSend extends CountDownTimer {
		public TimeMSGSend(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			 Message message = new Message();  
	            message.what = 3;  
	            smsReceiverHandler.sendMessage(message); 
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
		}
	}
	
/*	TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            Message message = new Message();  
            message.what = 1;  
            smsReceiverHandler.sendMessage(message);  
        }  
    }; */
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		/*if(receiver!= null){
			try {
				context.unregisterReceiver(receiver);
			} catch (Exception e) {
			}
			receiver = null;
		}*/
	}
}

