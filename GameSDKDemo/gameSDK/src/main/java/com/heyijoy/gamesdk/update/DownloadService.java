package com.heyijoy.gamesdk.update;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYInit;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Logger;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class DownloadService extends Service {
	public static NotificationManager notificationMrg;
//	private String appName;
	public static String appName;
	public static final int MESSAGE_START = 3;
	public static final int MESSAGE_QUIT_NOTICE = 5;
	private  Messenger mClientMessager = null;  //客户端messenger
	public static final int START = 0;
	public static final int LOADING = 1;
	public static final int OVER = 2;
	public static final int FAIL = 3; 
	public static final int FAILCARD = 4; 
	private boolean isOver = false;//退出服务时，若没有下载完成，则删除通知栏
	
	private String loadUrl;
	private String installFileName;
	private String appname;
	
	private long loadSize;//大小
	private long loadingVelocity;//速度
	private int loadingProcess;//进度 
	MultiThreadDownload mtd;
	RemoteViews contentView;//通知的view
	Notification notification = null;

	public void onCreate() {
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		appName = intent.getStringExtra("appName");
//		com.heyijoy.gamesdk.lib.R.a(this);// 初始化自生成的R文件
		notificationMrg = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
//		this.startForeground(0, notification);
		if(HYInit.remoteVersionInfo == null){
			SharedPreferences preferences = getSharedPreferences(HYConstant.PREF_FILE_UPDATE_LOAD, Context.MODE_PRIVATE);
			 loadUrl = preferences.getString(HYConstant.PREF_FILE_UPDATE_LOAD_URL, "");
			 installFileName =  preferences.getString(HYConstant.PREF_FILE_UPDATE_INSTLL_FILE_NAME, "");
			 appname = preferences.getString(HYConstant.PREF_FILE_UPDATE_APP_NAME, "");
		}else{
			loadUrl = HYInit.remoteVersionInfo.getUpdateUrl();
			installFileName = HYInit.remoteVersionInfo.getInstallFileName();
			appname = GameSDKApplication.getInstance().getAppname();
			SharedPreferences preferences = getSharedPreferences(HYConstant.PREF_FILE_UPDATE_LOAD, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
//			editor.putString("PREF_FILE_USER_NAME", ""); // value to store
			editor.putString(HYConstant.PREF_FILE_UPDATE_LOAD_URL, loadUrl); // value to store
			editor.putString(HYConstant.PREF_FILE_UPDATE_INSTLL_FILE_NAME, installFileName); // value to store
			editor.putString(HYConstant.PREF_FILE_UPDATE_APP_NAME, appname); // value to store
			editor.commit();
			
		}
		mtd = new MultiThreadDownload(downloadHandler,
				loadUrl,
				FileUtil.setMkdir(DownloadService.this),installFileName);
		mtd.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private Handler downloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DecimalFormat df = new DecimalFormat("0.00");
			switch (msg.what) {
			case FileUtil.startDownloadMeg:
				//发送给客户端
				long currentsize = mtd.getLocalSize();
				long allsize = mtd.getFileSize();
				float floadingSize = ((float)currentsize)/(1024*1024.f); 
				float fallSize = ((float)allsize)/(1024*1024.f);
				String currentsizeStr = df.format(floadingSize);
				String allsizeStr = df.format(fallSize);
				displayNotificationMessage(loadingProcess,currentsizeStr,allsizeStr);
				
				Message messageStart = Message.obtain(null, START);
				Bundle bundleStart = new Bundle();
				bundleStart.putLong("SIZE", mtd.getFileSize());
				bundleStart.putLong("CURRENTSIZE", mtd.getLocalSize());
				messageStart.setData(bundleStart);
				try {
						if(mClientMessager != null){
							mClientMessager.send(messageStart);
						}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				break;
			case FileUtil.updateDownloadMeg:
					Logger.e("已下载："+mtd.getDownloadSize());
					
					loadSize = mtd.getDownloadSize();
					loadingVelocity = mtd.getDownloadSpeed();//速度
					loadingProcess = mtd.getDownloadPercent();//百分比
					
					
					
						int currentsize1 = mtd.getDownloadSize();
						long allsize1 = mtd.getFileSize();
						float floadingSize1 = ((float)currentsize1)/(1024*1024.f); 
						float fallSize1 = ((float)allsize1)/(1024*1024.f);
						String currentsizeStr1 = df.format(floadingSize1);
						String allsizeStr1 = df.format(fallSize1);
						displayNotificationMessage(loadingProcess,currentsizeStr1,allsizeStr1);
					
					//发送给客户端
					Message message = Message.obtain(null, LOADING);
					Bundle bundle = new Bundle();
					bundle.putLong("SPEED", loadingVelocity);
					bundle.putInt("PERCENT", loadingProcess);
					bundle.putLong("CURRENTSIZE", loadSize);
					bundle.putLong("SIZE", mtd.getFileSize());
					message.setData(bundle);
					try {
						if(mClientMessager!=null){
							mClientMessager.send(message);
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
				break;
			case FileUtil.updateFail:
				Toast.makeText(DownloadService.this, HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL, Toast.LENGTH_SHORT).show();
				//发送给客户端
				Message messagefail = Message.obtain(null, FAIL);
//				messagefail.arg1 = 0;
				try {
					if(mClientMessager!=null){
						mClientMessager.send(messagefail);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				stopSelf();//下载失败停止服务
				break;
			case FileUtil.sdkFail:
				Toast.makeText(DownloadService.this, "sd卡空间不足，请清理后重试！", Toast.LENGTH_SHORT).show();
				//发送给客户端
				Message messagefailsdk = Message.obtain(null, FAIL);
//				messagefailsdk.arg1 = 1;
				try {
					if(mClientMessager!=null){
						mClientMessager.send(messagefailsdk);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				stopSelf();//下载失败停止服务
				break;
			case FileUtil.endDownloadMeg:  
				isOver = true;//下载完成
				//发送给客户端
				Message messageOver = Message.obtain(null, OVER);
				Bundle bundleover = new Bundle();
				bundleover.putLong("SIZE", mtd.getFileSize());
				messageOver.setData(bundleover);
				try {
					if(mClientMessager!=null){
						mClientMessager.send(messageOver);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				Logger.e("DownLoadService", "DownLoadService下载完成");
		        	//通知栏下载完成
			        String savePath = FileUtil.setMkdir(DownloadService.this);
					String fileName = savePath + "/"+ installFileName;
	                Intent installIntent = new Intent(Intent.ACTION_VIEW);
	                installIntent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
	                PendingIntent notificationIntent1 = PendingIntent.getActivity(DownloadService.this, 0, installIntent, 0);
	                if(notification != null){
	                	 notification.setLatestEventInfo(DownloadService.this, appname, "下载完成,点击安装。", notificationIntent1);
	 	                notificationMrg.notify(R.string.hy_notify_no, notification);
	                }
		        
		        stopSelf();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		mHandler = null;
		downloadHandler = null;
	}
//    状态栏具体视图显示要求设置
	private void displayNotificationMessage(int count,String currentsize,String allsize) {
		if(notification==null){
			notification = new Notification(GameSDKApplication.getInstance().geticonid(this),"DownLoadManager", System.currentTimeMillis());
			Logger.d("debbbb","id-------------"+GameSDKApplication.getInstance().geticonid(this));
//			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知 
		}
		//创建一个自定义的Notification，可以使用RemoteViews 要定义自己的扩展消息，首先要初始化一个RemoteViews对象，然后将它传递给Notification的contentView字段，再把PendingIntent传递给contentIntent字段
		if(contentView == null){
			contentView = new RemoteViews(getPackageName(),
					R.layout.notification_version);
		}
		contentView.setTextViewText(R.id.n_title,
				appname);
		contentView.setTextViewText(R.id.n_text, count+"% ");
		contentView.setTextViewText(R.id.tv_loadingsize, currentsize+"MB");
		contentView.setTextViewText(R.id.tv_allsize, "/"+allsize+"MB");
		contentView.setProgressBar(R.id.n_progress, 100, count, false);
		contentView.setImageViewResource(R.id.icon, GameSDKApplication.getInstance().geticonid(this));
		notification.contentView = contentView;
		PendingIntent notificationIntent1 = PendingIntent.getActivity(DownloadService.this, 0, new Intent(), 0);
//		notification.setLatestEventInfo(DownloadService.this, appname, "下载完成,点击安装。", notificationIntent1);
		notification.contentIntent = notificationIntent1;
		notificationMrg.notify(R.string.hy_notify_no, notification);
	}

	public void startNotification(){
//		获得系统后台运行的NotificationManager服务
		notificationMrg = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	}
	
	
	private Handler serviceHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DownloadService.MESSAGE_START:
				mClientMessager = msg.replyTo;
				break;
			case DownloadService.MESSAGE_QUIT_NOTICE:
				if(notificationMrg!=null){
					notificationMrg.cancel(R.string.hy_notify_no);
				}
				break;
			}
		};
	};
	
	final Messenger mMessenger = new Messenger(serviceHandler);
	
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();  
	}
	
	public static boolean isWorked(Context context)  
	 {  
	  ActivityManager myManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
	  ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(120);  
	  for(int i = 0 ; i<runningService.size();i++)  
	  {  
	   if(runningService.get(i).service.getClassName().toString().equals("com.heyijoy.gamesdk.update.DownloadService"))  
	   {  
		   String packagename = context.getPackageName();
		    if(packagename.equals(runningService.get(i).service.getPackageName())){
		    	return true;
		    };
	   }  
	  }  
	  return false;  
	 }  
}
