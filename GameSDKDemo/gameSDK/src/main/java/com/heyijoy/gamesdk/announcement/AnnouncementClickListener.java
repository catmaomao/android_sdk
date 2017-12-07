/**
 * AnnouncementClickListener.java
 * com.heyijoy.gamesdk.announcement
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-9-28 		msh
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.announcement;

import java.util.List;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.WebViewActivity;
import com.heyijoy.gamesdk.widget.WebViewWelfareActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * ClassName:AnnouncementClickListener
 *
 * @author   msh
 * @version  
 * @since    Ver 1.1
 * @Date	 2014-9-28		上午11:35:27
 */
public class AnnouncementClickListener implements OnClickListener{
	 
	private Skipable skipable;
	private boolean flag=false;//是否进行判断 url 的标记，默认为false ，表示 不进行 判断
	public AnnouncementClickListener(Skipable skipable,boolean flag){
		this.skipable = skipable;
		this.flag=flag;
	}

	@Override
	public void onClick(View arg0) {
		
		String url=skipable.getUrl();
		if(flag)
		{
			if(url!=null && !"".equals(url))
		    {
				doJobByClick(arg0,url);	
		    }
		}else
		{
			doJobByClick(arg0,url);
		}
	}
	
	private void doJobByClick(View arg0,String url)
	{ 
       if("0".equals(skipable.getSkipType())){
    	   try {
				Intent i = new Intent(arg0.getContext(), WebViewActivity.class);
				if(url.contains("http:")){
					i.putExtra("url", url);
				}else{
					i.putExtra("url", "http://"+url);
				}
				arg0.getContext().startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
       } else if ("5".equals(skipable.getSkipType())){//视频播放
		       Intent intent = new Intent();
		       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
		               .FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		       String vid = skipable.getVideoId();
		       String video_hink =skipable.getVideoHink();
		       if(vid!=null&&!"".equals(vid)){
		    	   try {
		    	   intent.putExtra("video_id", vid);
		           intent.setComponent(new ComponentName("com.heyijoy.phone", "com.youku.ui.activity.DetailActivity"));
		           arg0.getContext().startActivity(intent);
		       }catch (Exception e) {
		    	   if(video_hink!=null&&!"".equals(video_hink) ){
			    	   Intent i = new Intent(arg0.getContext(), WebViewActivity.class);
						if(video_hink.contains("http:")){
							i.putExtra("url", video_hink);
						}else{
							i.putExtra("url", "http://"+video_hink);
						}
						arg0.getContext().startActivity(i);
		    	   }else{
		    		   Toast.makeText(arg0.getContext(), "请安装最新的合乐智趣客户端", 1000).show();;
		    	   }
		       }
		     }else if(video_hink!=null&&!"".equals(video_hink) ){
		    	   Intent i = new Intent(arg0.getContext(), WebViewActivity.class);
					if(video_hink.contains("http:")){
						i.putExtra("url", video_hink);
					}else{
						i.putExtra("url", "http://"+video_hink);
					}
					arg0.getContext().startActivity(i);
		      }
		}else {
			  //跳到游戏中心      
    		ComponentName componetName = new ComponentName(
                    //这个是另外一个应用程序的包名 
    				getPack(),
                   //这个参数是要启动的Activity 
                   url); 
           Intent intent= new Intent();
           //我们给他添加一个参数表示从apk1传过去的
           Bundle bundle = new Bundle();
           bundle.putString("appId", GameSDKApplication.getInstance().getAppid());
           bundle.putString("packagename", arg0.getContext().getPackageName());
           bundle.putString("presentId", skipable.getIntentId());//presentId
           bundle.putString("activeId", skipable.getIntentId());//活动ID
           bundle.putString("source", "36");//source
           intent.putExtras(bundle);
           intent.setComponent(componetName);
           PackageManager packageManager = arg0.getContext().getPackageManager();
           List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
           boolean isIntentSafe = activities.size() > 0;
           if(Util.isSupportPresent(arg0.getContext())&&isIntentSafe){
        	   try {
        		   arg0.getContext().startActivity(intent);
        	   } catch (Exception e) {
        	   }
           }else{
				Toast.makeText(arg0.getContext(),"请下载最新的合乐智趣视频客户端", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public String getPack()
    {
   	 String pack=GameSDKApplication.getInstance().getPackFormGameCenter();
   	 if("".equals(pack))
   	 {
   		 pack="com.heyijoy.phone";
   	 }
   	 return pack;
    }
	
	
}

