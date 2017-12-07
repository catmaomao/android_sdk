/**
 * SMSReceiver.java
 * com.heyijoy.gamesdk
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-2-18 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.act;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.heyijoy.gamesdk.constants.HYConstant;
import com.heyijoy.gamesdk.util.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;

/**
 * ClassName:SMSReceiver
 * @author   msh
 * @Date	 2014-2-18		下午3:17:22
 */
public class SMSReceiver extends BroadcastReceiver {
	public static final int MSG_WHAT_REG_GET_VERIFYNO = 6;
	public static final int MSG_WHAT_REG_OLD_USER = 7;
	public static final int MSG_WHAT_REG_NEW_USER = 8;
	public static final int MSG_WHAT_FIND_PWD = 9;
	public static final int MSG_WHAT_BINDING = 10;
	
	public static final String MSG_DATA_USERNAME = "USERNAME";
	public static final String MSG_DATA_PWD = "PWD";
	public static final String MSG_DATA_VERIFYNO = "VERIFY_NO";
	public static final String MSG_DATA_BINDING = "BINDING";
	public static final String MSG_DATA_TIME = "TIME";
	public static final String MSG_DATA_SOURCE = "SOURCE";
	public static final String MSG_DATA_SOURCE_MMS = "MSG_DATA_SOURCE_MMS";
	public static final String MSG_DATA_SOURCE_SOCKET = "MSG_DATA_SOURCE_SOCKET";
	
	private static final String MMS_PRE_VERIFY="验证码:";
	private static final String MMS_PRE_PWD="密码:";
	private static final String MMS_PRE_USERNAME="账号:";
	private static final String MMS_PRE_TIME="#";
	
	private Handler handler;
	public SMSReceiver(Handler handler) {
		this.handler = handler;
	}
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(bundle==null)
        {
        	return;
        }
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);

            String sender ;
            String content ;
            try {
            	 sender = smsMessage[n].getOriginatingAddress();//获取短信的发送者
                 content = smsMessage[n].getMessageBody();//获取短信的内容
			} catch (Exception e) {
				return;
			}
            if(isNeedIntercept(sender)){
            	HYConstant.REG_MOBILE_RECEIVE_NO = sender; 
            	Logger.v("mms receive"+content);
            	Message msg = new Message();
            	Bundle data = new Bundle();
            	if(content.contains(HYConstant.MMS_TITLE_ONEBUTTON_OLDER_USER)){
            		msg.what = MSG_WHAT_REG_OLD_USER;
            		String serverTime = getStr(content,MMS_PRE_TIME);
            		String userName = getStr(content,MMS_PRE_USERNAME);
            		data.putString(MSG_DATA_VERIFYNO, getStr(content,MMS_PRE_VERIFY));
            		data.putString(MSG_DATA_USERNAME, userName);
            		data.putString(MSG_DATA_TIME, serverTime);
            		data.putString(MSG_DATA_SOURCE, MSG_DATA_SOURCE_MMS);
            		this.abortBroadcast();
            	}else if(content.contains(HYConstant.MMS_TITLE_ONEBUTTON_NEW_USER)){
            		msg.what = MSG_WHAT_REG_NEW_USER;
            		String serverTime = getStr(content,MMS_PRE_TIME);
            		String userName = getStr(content,MMS_PRE_USERNAME);
            		data.putString(MSG_DATA_USERNAME, userName);
            		data.putString(MSG_DATA_PWD, getStr(content,MMS_PRE_PWD));
            		data.putString(MSG_DATA_TIME, serverTime);
            		data.putString(MSG_DATA_SOURCE, MSG_DATA_SOURCE_MMS);
            	}else if(content.contains(HYConstant.MMS_TITLE_FIND_PWD)){
            		msg.what = MSG_WHAT_FIND_PWD;
            		data.putString(MSG_DATA_VERIFYNO, getStr(content,MMS_PRE_VERIFY));
            	}else if(content.contains(HYConstant.MMS_TITLE_REG_GET_VERIFYNO)){
            		msg.what = MSG_WHAT_REG_GET_VERIFYNO;
            		data.putString(MSG_DATA_VERIFYNO, getStr(content,MMS_PRE_VERIFY));
            	}else if(content.contains(HYConstant.MMS_TITLE_BINDING)){
            		msg.what = MSG_WHAT_BINDING;
            		data.putString(MSG_DATA_BINDING, getStr(content,MMS_PRE_VERIFY));
            	}else if(content.contains(HYConstant.MMS_TITLE_BINDING_PHONE)){
            		msg.what = MSG_WHAT_BINDING;
            		data.putString(MSG_DATA_BINDING, getStr(content,MMS_PRE_VERIFY));
            	}
            	msg.setData(data);
            	handler.sendMessage(msg);
            }
        }
    }
    
    private boolean isNeedIntercept(String mobile){
    	if(mobile!=null){
    		Iterator<String> i = HYConstant.REG_MOBILE_RECEIVE_LIST.iterator();
    		for (Iterator<String> iterator = HYConstant.REG_MOBILE_RECEIVE_LIST.iterator(); iterator.hasNext();) {
				String mobile_tmp = (String) iterator.next();
				if(mobile.contains(mobile_tmp)){
					return true;
				}
			}
    	}
    	return false;
    }
    public String getStr(String sourceStr,int index){
		String outStr="";
		Pattern p = Pattern.compile("\\((.*?)\\)");
		Matcher m = p.matcher(sourceStr);
		int i = 1;
		 while(m.find()) {
			 if(index==i){
				 outStr = m.group(1);
			 }
			 i++;
		 }
		return outStr;
	}
    public String getStr(String sourceStr,String preStr){
  		String outStr="";
  		Pattern p = Pattern.compile("\\（(.*?)\\）");
  		Matcher m = p.matcher(sourceStr);
  		 while(m.find()) {
  				 outStr = m.group(1);
  				 if(outStr.startsWith(preStr)){
  					outStr =  outStr.substring(preStr.length());
  					return outStr;
  				 }
  		 }
  		return "";
  	}
}

