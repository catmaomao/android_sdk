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

import java.util.ArrayList;
import java.util.List;

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
public class UserContentResover {
	public static String CONTENT_URI="content://com.youku.gamecenter.outer.GamePlayersProvider/players";
	private static UserContentResover app;
	private static String CONTENT_URI1="";
	private static String CONTENT_URI2="";
	private static boolean flag=false;
	private UserContentResover()
	{
		
	}
	public String getCONTENT_URI() {
		return CONTENT_URI;
	}
	public static synchronized UserContentResover getInstance() {
		if (app == null) {
			app = new UserContentResover();
			 String pack=GameSDKApplication.getInstance().getPackFormGameCenter();
			 if(!"".equals(pack))
			 {
				 CONTENT_URI="content://"+pack+".gamecenter.outer.GamePlayersProvider/players";
			 } 
			 if("content://com.youku.gamecenter.outer.GamePlayersProvider/players".equals(CONTENT_URI))
			 {
				 flag=true;
				 CONTENT_URI1="content://com.youku.gamecenter.outer.GamePlayersProvider/players";
				 CONTENT_URI2="content://com.youku.phone.gamecenter.outer.GamePlayersProvider/players";
			 }
			 
		}
		return app;
	}
	
	private static ContentResolver resolver;
	private static String NAME =  "userName";//用户名
	private static String PWD = "password";//密码
	private static String SEX = "sex";//性别
	private static String ADDRESS = "address";//地址
	private static String NICK = "nick";//昵称
	private static String UID = "uid";//
	private static String ISYOUKUACCOUNT = "isYoukuAccount";//是否是合乐智趣帐号 0 合乐智趣已有帐号 1手机注册新帐号
	private static String ISNEWUSER = "isNewUser";//是否是新用户
	private static String SESSION = "session";
	private static String VERIFYVO = "verifyNo";//验证码
	private static String STATE = "state"; 
     public static final String CONTENT_TYPE="vnd.android.cursor.dir/com.youku.gamecenter.outer.GamePlayersProvider";
     public static final String CONTENT_ITEM_TYPE="vnd.android.cursor.dir/com.youku.gamecenter.outer.GamePlayersProvider";
     //插入数据
     public void insert(User user)
     {
    	 Uri uri=null;
         //获得ContentResolver对象
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         ContentValues values=new ContentValues();
         values.put(UserContentResover.NAME, user.getUserName());
         values.put(UserContentResover.PWD, user.getPassword());
         values.put(UserContentResover.SEX, System.currentTimeMillis());
         values.put(UserContentResover.ADDRESS, user.getAddress());
         values.put(UserContentResover.NICK, user.getNick());
         values.put(UserContentResover.UID, user.getUid());
         values.put(UserContentResover.ISYOUKUACCOUNT, user.getIsYoukuAccount());
         values.put(UserContentResover.ISNEWUSER, user.getIsNewUser());
         values.put(UserContentResover.SESSION, user.getSession());
         values.put(UserContentResover.VERIFYVO, user.getVerifyNo());
         values.put(UserContentResover.STATE, user.getState());
         
         //将信息插入
         try {
        	 if(flag)
        	 {   
            	 uri=Uri.parse(CONTENT_URI1);
            	 Uri ti1=resolver.insert(uri, values);
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
					Uri ti2=resolver.insert(uri, values);
				}
			} catch (Exception e2) {
			}
		}
         
     }
     //删除数据
     //删除第id行
     public static void delete(String name)
     {
         //指定uri
    	 Uri uri=null;
    	 String []values = {name};
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         try {
        	 if(flag)
        	 {
        		 uri=Uri.parse(CONTENT_URI1);
            	 resolver.delete(uri, "userName = ? ", values);
        	 }else
        	 {
        		 uri=Uri.parse(CONTENT_URI);
            	 resolver.delete(uri, "userName = ? ", values);
        	 }
		} catch (Exception e) {
			try {
				if(flag)
				{
					 uri=Uri.parse(CONTENT_URI2);
	            	 resolver.delete(uri, "userName = ? ", values);
				}
			} catch (Exception e2) {
			}
		}
         
     }
     //更新
     public void update(User user)
     {
    	 Uri uri=null;
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         ContentValues values=new ContentValues();
         String [] name = {user.getUserName()};
//         values.put(UserContentResover.NAME, user.getUserName());
         values.put(UserContentResover.PWD, user.getPassword());
         values.put(UserContentResover.SEX, System.currentTimeMillis());
         values.put(UserContentResover.ADDRESS, user.getAddress());
         values.put(UserContentResover.NICK, user.getNick());
         values.put(UserContentResover.UID, user.getUid());
         values.put(UserContentResover.ISYOUKUACCOUNT, user.getIsYoukuAccount());
         values.put(UserContentResover.ISNEWUSER, user.getIsNewUser());
         values.put(UserContentResover.SESSION, user.getSession());
         values.put(UserContentResover.VERIFYVO, user.getVerifyNo());
         values.put(UserContentResover.STATE, user.getState());
         try {
        	 if(flag)
        	 {
        		 uri=Uri.parse(CONTENT_URI1);
            	 resolver.update(uri, values, "userName = ?", name);
        	 }else
        	 {
        		 uri=Uri.parse(CONTENT_URI);
            	 resolver.update(uri, values, "userName = ?", name);
        	 }
		} catch (Exception e) {
			try {
				if(flag)
				{
					uri=Uri.parse(CONTENT_URI2);
	            	 resolver.update(uri, values, "userName = ?", name);
				}
			} catch (Exception e2) {
			}
		}
        
     }
     //查询
     public User  queryCurrent()
     {
    	 Uri uri=null;
    	 Cursor cursor=null;
    	 List<User> users = new ArrayList<User>();
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         if(flag)
    	 {
        	 uri=Uri.parse(CONTENT_URI1);
             cursor=resolver.query(uri, null, null, null, UserContentResover.SEX);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                 for(int i=0;i<cursor.getCount();i++)
                 {
                	 User user = new User();
                     cursor.moveToPosition(i);
                     user.setUserName(cursor.getString(0));
                     user.setPassword(cursor.getString(1));
                     user.setSex(cursor.getString(2));
                     user.setAddress(cursor.getString(3));
                     user.setNick(cursor.getString(4));
                     user.setUid(cursor.getString(5));
                     user.setIsYoukuAccount(cursor.getString(6));
                     user.setIsNewUser(cursor.getString(7));
                     user.setSession(cursor.getString(8));
                     user.setVerifyNo(cursor.getString(9));
                     user.setState(cursor.getString(10));
                     users.add(user);
                 }
             }
             if(users.size()>0){
            	return users.get(users.size()-1);
             }
             uri=Uri.parse(CONTENT_URI2);
             cursor=resolver.query(uri, null, null, null, UserContentResover.SEX);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                 for(int i=0;i<cursor.getCount();i++)
                 {
                	 User user = new User();
                     cursor.moveToPosition(i);
                     user.setUserName(cursor.getString(0));
                     user.setPassword(cursor.getString(1));
                     user.setSex(cursor.getString(2));
                     user.setAddress(cursor.getString(3));
                     user.setNick(cursor.getString(4));
                     user.setUid(cursor.getString(5));
                     user.setIsYoukuAccount(cursor.getString(6));
                     user.setIsNewUser(cursor.getString(7));
                     user.setSession(cursor.getString(8));
                     user.setVerifyNo(cursor.getString(9));
                     user.setState(cursor.getString(10));
                     users.add(user);
                 }
             }
             if(users.size()>0){
            	return users.get(users.size()-1);
             } 
        	 User user = new User();
        	 user.setUserName("");
        	 return  user;
          
    	 }else
    	 {
    		 uri=Uri.parse(CONTENT_URI);
             cursor=resolver.query(uri, null, null, null, UserContentResover.SEX);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                 for(int i=0;i<cursor.getCount();i++)
                 {
                	 User user = new User();
                     cursor.moveToPosition(i);
                     user.setUserName(cursor.getString(0));
                     user.setPassword(cursor.getString(1));
                     user.setSex(cursor.getString(2));
                     user.setAddress(cursor.getString(3));
                     user.setNick(cursor.getString(4));
                     user.setUid(cursor.getString(5));
                     user.setIsYoukuAccount(cursor.getString(6));
                     user.setIsNewUser(cursor.getString(7));
                     user.setSession(cursor.getString(8));
                     user.setVerifyNo(cursor.getString(9));
                     user.setState(cursor.getString(10));
                     users.add(user);
                 }
             }
             if(users.size()>0){
            	return users.get(users.size()-1);
             }else{
            	 User user = new User();
            	 user.setUserName("");
            	 return  user;
             }
    	 }
         
     }
     //查询
     public User queryByName(String name)
     {
    	 String []values = {name};
    	 Uri uri=null;
    	 User user = new User();
    	 Cursor cursor=null;
         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
         if(flag)
    	 { 
        	 uri=Uri.parse(CONTENT_URI1);
             cursor=resolver.query(uri, null, "userName = ? ", values, UserContentResover.SEX);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                 cursor.moveToPosition(cursor.getCount()-1);
                 user.setUserName(cursor.getString(0));
                 user.setPassword(cursor.getString(1));
                 user.setSex(cursor.getString(2));
                 user.setAddress(cursor.getString(3));
                 user.setNick(cursor.getString(4));
                 user.setUid(cursor.getString(5));
                 user.setIsYoukuAccount(cursor.getString(6));
                 user.setIsNewUser(cursor.getString(7));
                 user.setSession(cursor.getString(8));
                 user.setVerifyNo(cursor.getString(9));
                 user.setState(cursor.getString(10));
             } 
             if(!"".equals(user.getUserName()))
             {
            	 return user;
             } 
//            	 user=null;
//            	 user = new User();

             uri=Uri.parse(CONTENT_URI2);
             cursor=resolver.query(uri, null, "userName = ? ", values, UserContentResover.SEX);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                 cursor.moveToPosition(cursor.getCount()-1);
                 user.setUserName(cursor.getString(0));
                 user.setPassword(cursor.getString(1));
                 user.setSex(cursor.getString(2));
                 user.setAddress(cursor.getString(3));
                 user.setNick(cursor.getString(4));
                 user.setUid(cursor.getString(5));
                 user.setIsYoukuAccount(cursor.getString(6));
                 user.setIsNewUser(cursor.getString(7));
                 user.setSession(cursor.getString(8));
                 user.setVerifyNo(cursor.getString(9));
                 user.setState(cursor.getString(10));
             } 
             if(!"".equals(user.getUserName()))
             {
            	 return user;
             } else
             {
            	 return null;
             }
    	 }else
    	 {
    		 uri=Uri.parse(CONTENT_URI);
             cursor=resolver.query(uri, null, "userName = ? ", values, UserContentResover.SEX);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst())
             {
                 cursor.moveToPosition(cursor.getCount()-1);
                 user.setUserName(cursor.getString(0));
                 user.setPassword(cursor.getString(1));
                 user.setSex(cursor.getString(2));
                 user.setAddress(cursor.getString(3));
                 user.setNick(cursor.getString(4));
                 user.setUid(cursor.getString(5));
                 user.setIsYoukuAccount(cursor.getString(6));
                 user.setIsNewUser(cursor.getString(7));
                 user.setSession(cursor.getString(8));
                 user.setVerifyNo(cursor.getString(9));
                 user.setState(cursor.getString(10));
             }else{
            	 user = null;
             }
             return user;
    	 }
     }
     
     public void destroy(){
    	 app=null;
 	}
     
}

