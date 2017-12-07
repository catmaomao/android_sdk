package com.heyijoy.gamesdk.memfloat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.heyijoy.gamesdk.activity.HYRelayActivity;
import com.heyijoy.gamesdk.data.MsgBean;
import com.heyijoy.gamesdk.http.AsyncImageLoader;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

public class HYNotifyBar {
	
	RemoteViews mRemoteViews;
	
	
	/** Notification管理 */
	private NotificationManager mNotificationManager;
	/** Notification构造器 */
	private NotificationCompat.Builder mBuilder;
	private Context context;
	public HYNotifyBar(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
	}
	/** 初始化通知栏 */
	public void showNotify(final MsgBean msgBean){
		mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.hy_notifybar);
		if(msgBean.getIcon()!=null && msgBean.getIcon().length()>5){//若有指定图片
			try{
				AsyncImageLoader asyloader = new AsyncImageLoader();
				asyloader.loadDrawable(msgBean.getIcon(), new AsyncImageLoader.ImageCallback(){
					@Override
					public void imageLoaded(Drawable imageDrawable) {
						// TODO Auto-generated method stub
						Bitmap imageBitmap = Util.drawableToBitmap(imageDrawable);
						mRemoteViews.setImageViewBitmap(R.id.hy_notify_icon, imageBitmap);
						initNotify(msgBean);
					}
				});
			}catch(Exception e){
				e.printStackTrace();
				mRemoteViews.setImageViewResource(R.id.hy_notify_icon, context.getApplicationInfo().icon);
				initNotify(msgBean);
			}
		}else{
			mRemoteViews.setImageViewResource(R.id.hy_notify_icon, context.getApplicationInfo().icon);
			initNotify(msgBean);
		}

	}
	
	public void initNotify(MsgBean msgBean){
		mRemoteViews.setTextViewText(R.id.hy_notify_title, msgBean.getMainTitle());
		mRemoteViews.setTextViewText(R.id.hy_notify_content, msgBean.getContent());
//		mBuilder = new Builder(context);
		mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setContent(mRemoteViews)
//		.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
		.setAutoCancel(true)
		.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
		.setTicker("有新资讯")
		.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
		.setOngoing(false)//不是正在进行的   true为正在进行  效果和.flag一样
		.setDefaults(Notification.DEFAULT_ALL)
		.setSmallIcon(context.getApplicationInfo().icon);
		
		Intent resultIntent = new Intent(context, HYRelayActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resultIntent.putExtra("from", "HYNotifyBar");
		resultIntent.putExtra("msgBean", msgBean);
		resultIntent.putExtra("clickFrom", 3);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(100, mBuilder.build());
	}
	/** 
	 * 清除当前创建的通知栏 
	 */
	public void clearNotify(int notifyId){
		mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
//		mNotification.cancel(getResources().getString(R.string.app_name));
	}
	
}
