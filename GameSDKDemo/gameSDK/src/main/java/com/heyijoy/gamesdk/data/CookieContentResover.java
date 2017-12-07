/**
 * UserContentResover.java
 * com.heyijoy.gamesdk.data
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-8-19 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.data;

import com.heyijoy.gamesdk.act.GameSDKApplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * ClassName:UserContentResover
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 *
 * @author   msh
 * @version  
 * @Date	 2014-8-19		上午9:55:06
 *
 */
public class CookieContentResover {
	 
	public static String CONTENT_URI="content://com.youku.gamecenter.outer.GamePlayersProvider/properties";
	private static CookieContentResover app;
	private static String CONTENT_URI1="";
	private static String CONTENT_URI2="";
	private static boolean flag=false;
	private CookieContentResover()
	{
		
	}
	public static String getCONTENT_URI() {
		return CONTENT_URI;
	}
	public static synchronized CookieContentResover getInstance() {
		if (app == null) {
			app = new CookieContentResover();
			 String pack=GameSDKApplication.getInstance().getPackFormGameCenter();
			 if(!"".equals(pack))
			 {
				 CONTENT_URI="content://"+pack+".gamecenter.outer.GamePlayersProvider/properties";
			 } 
			 if("content://com.youku.gamecenter.outer.GamePlayersProvider/properties".equals(CONTENT_URI))
			 {
				 flag=true;
				 CONTENT_URI1="content://com.youku.gamecenter.outer.GamePlayersProvider/properties";
				 CONTENT_URI2="content://com.youku.phone.gamecenter.outer.GamePlayersProvider/properties";
			 }
		}
		return app;
	}
		
		
	private static ContentResolver resolver;
	private static String KEY =  "key";//键
	private static String VALUE =  "value";//cookie值
	private static String COOKIE =  "cookie";//cookie的键值
	 
	
   
	//插入数据
     public static void insert(String cookie)
     {
    	 Uri uri=null;
         //获得ContentResolver对象
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         
         if(queryCookie()==null){//插入
        	 ContentValues values=new ContentValues();
             values.put(CookieContentResover.KEY, COOKIE);
             values.put(CookieContentResover.VALUE, cookie);
             try {
            	 if(flag)
            	 {
            		 uri=Uri.parse(CONTENT_URI1); 
                	 resolver.insert(uri, values);
            	 }else
            	 {
            		 uri=Uri.parse(CONTENT_URI); 
            		 resolver.insert(uri, values);
            	 }
			} catch (Exception e) {
				try {
					if(flag)
					{
						uri=Uri.parse(CONTENT_URI2); 
	                	resolver.insert(uri, values);
					}
				} catch (Exception e2) {
				}
			}
            
         }else{
        	 update(cookie);
         }
         
        
     }
     //删除数据
     //删除第id行
     public static void deleteCookie()
     {
         //指定uri
    	 String []values = {COOKIE};
    	 Uri uri=null;
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         try {
        	 if(flag)
        	 {
        		 uri=Uri.parse(CONTENT_URI1);
            	 resolver.delete(uri, "key = ? ", values);
        	 }else
        	 {
        		 uri=Uri.parse(CONTENT_URI);
            	 resolver.delete(uri, "key = ? ", values);
        	 }
		} catch (Exception e) {
			try {
				if(flag)
				{
					 uri=Uri.parse(CONTENT_URI2);
	            	 resolver.delete(uri, "key = ? ", values);
				}
			} catch (Exception e2) {
			}
		}
         
     }
     //更新
     public static void update(String cookie)
     {
    	 Uri uri=null;
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         ContentValues values=new ContentValues();
         values.put(CookieContentResover.KEY, COOKIE);
         values.put(CookieContentResover.VALUE, cookie);
         try {
        	 if(flag)
        	 {
        		 uri=Uri.parse(CONTENT_URI1);
            	 resolver.update(uri, values, null, null);
        	 }else
        	 {
        		 uri=Uri.parse(CONTENT_URI);
            	 resolver.update(uri, values, null, null);
        	 }
		} catch (Exception e) {
			try {
				if(flag)
				{
					uri=Uri.parse(CONTENT_URI2);
	            	 resolver.update(uri, values, null, null);
				}
			} catch (Exception e2) {
			}
		}
        
     }
     //查询
     public static String  queryCookie()
     { 
    	 Uri uri=null;
    	 Cursor cursor=null;
         String []values = {COOKIE};
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         if(flag)
         {
        	 uri=Uri.parse(CONTENT_URI1);
             cursor=resolver.query(uri, null, "key = ? ", values, null);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                     String cookie = cursor.getString(1); 
                     return cookie;
             }
             uri=Uri.parse(CONTENT_URI2);
             cursor=resolver.query(uri, null, "key = ? ", values, null);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                     String cookie = cursor.getString(1);
                     return cookie;
             }
         }else
         {
        	 uri=Uri.parse(CONTENT_URI);
             cursor=resolver.query(uri, null, "key = ? ", values, null);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                     String cookie = cursor.getString(1);
                     return cookie;
             }
         }
         return null;
     }
     
     public void destroy(){
    	 app=null;
 	}
     
}

