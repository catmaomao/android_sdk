package com.heyijoy.gamesdk.act;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 用于接收消息推送，展示notification
 * @author shaohuma
 *
 */
public class MsgReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		synchronized(this){
//			byte[] content = intent.getByteArrayExtra(PushConstants.KEY_PUSH_MSG);
//			String getMsg=new String(content);
//			
//			if(getMsg==null||"".equals(getMsg))
//			{
//				Logger.d("pack&ver","接到的消息为空1");
//			}else
//			{   
//				Logger.d("pack&ver","接到的消息为："+getMsg);
//				if(GameSDKApplication.getInstance().getContext()==null) GameSDKApplication.getInstance().init(context);
//				Logger.d("pack&ver","接到的id："+GameSDKApplication.getInstance().getAppid());
//				JSONObject json;
//				try {
//					json = new JSONObject(getMsg);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				Intent mintent = new Intent();
//				mintent.setAction(HYConstant.YK_SDK_PUSH_ORDER_RELAY);
//				mintent.putExtra("msg", getMsg);
//				if (android.os.Build.VERSION.SDK_INT >= 12) 
//				{
//					mintent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES); 
//				}
//				context.sendOrderedBroadcast(mintent, null);
//				
//			}
		}
	}
}
