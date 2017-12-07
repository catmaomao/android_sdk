package com.heyijoy.gamesdk.util;

import com.heyijoy.gamesdk.constants.HYConstant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
/**
 * 监听home键广播
 * @author andong_yt
 */
public class HomeListener {
    private Context mContext; 
    private IntentFilter mFilter; 
    private OnHomePressedListener mListener; 
    private HomePressedRecevier mReceiver; 
 
    public interface OnHomePressedListener { 
        public void onHomePressed(); 
    } 
 
    public HomeListener(Context context) { 
        mContext = context; 
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); 
    } 
 
    public void setOnHomePressedListener(OnHomePressedListener listener) { 
        mListener = listener; 
        mReceiver = new HomePressedRecevier(); 
    } 
 
    public void startListen() { 
        if (mReceiver != null) { 
            mContext.registerReceiver(mReceiver, mFilter); 
        } 
    } 
 
    public void stopListen() { 
        if (mReceiver != null ) { 
        	try {
        		mContext.unregisterReceiver(mReceiver); 
        	} catch(IllegalArgumentException e){
        		e.printStackTrace();
        		System.out.println("已捕捉取消注册exception");
        	}
        } 
    }
    
    class HomePressedRecevier extends BroadcastReceiver{
        @Override 
        public void onReceive(Context context, Intent intent) { 
        	Log.e(HYConstant.TAG, "HOME PRESS");
            String action = intent.getAction(); 
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) { 
                String reason = intent.getStringExtra("reason"); 
                if (reason != null) {
                    if (mListener != null) { 
                        if (reason.equals("homekey")) { 
                            mListener.onHomePressed(); 
                        } 
                    } 
                } 
            } 
        } 
    } 
}
