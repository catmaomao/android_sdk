package com.heyijoy.gamesdk.activity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.util.Logger;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 * 用于全局捕获  
 * @author andong_yt
 *
 */
@SuppressLint("SimpleDateFormat")
public class HYActivityApplication  extends Application {
	private Context context=null;
	public boolean flag=true;
	private StringWriter sw=null;
	private PrintWriter pw=null;
	private String exceptionStr="";
	private DateFormat formatter = null;
	private String exceptiontime="";
	@Override
	public void onCreate() {
		super.onCreate();
		context=getApplicationContext();
		Thread.currentThread().setUncaughtExceptionHandler(new YKUncaughtExceptionHandler());
	}
	
	private class YKUncaughtExceptionHandler implements UncaughtExceptionHandler {
		
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {  
			
			if(ex instanceof RuntimeException)
			{
				sw = new StringWriter();  
	            pw = new PrintWriter(sw);  
	            ex.printStackTrace(pw);  
	            ex.printStackTrace();
	            exceptionStr=sw.toString();
	            if(exceptionStr!=null && exceptionStr.contains("com.heyijoy.gamesdk."))
	            {
	            	Logger.d("捕获未处理异常----获取来源：sdk---异常来源：sdk");
	            	formatter=new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	            	exceptiontime = formatter.format(new Date());  
	            }else
	            {
	            	Logger.d("捕获未处理异常----获取来源：sdk---异常来源：cp");
	            	formatter=new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	            	exceptiontime = formatter.format(new Date());  
	            }
			}
			
			Editor edit=GameSDKApplication.getInstance().getEditorForException();
			edit.putBoolean("flag", flag);
			edit.commit();
			
			int i=0;
			while(!GameSDKApplication.getInstance().isSend())
			{
				try {
					Thread.sleep(300);
					i++;
					if(i>=17)
					{
						Logger.d("捕获未处理异常----获取来源：sdk---异常来源：cp");
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
		    	}
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
		
		
	}
	
	
}

