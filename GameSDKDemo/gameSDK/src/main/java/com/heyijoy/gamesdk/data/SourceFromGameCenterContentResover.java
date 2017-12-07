package com.heyijoy.gamesdk.data;

import java.util.HashMap;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.lib.HYConstant;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class SourceFromGameCenterContentResover {
	public static String CONTENT_URI="content://com.youku.phone.gamecenter.outer.GamePlayersProvider/players";
	private static SourceFromGameCenterContentResover app;
	private static ContentResolver resolver;
	public static final String COL_NAME_GAME_TRACE_GAME_ID = "game_id";// game_id
	private HashMap<String , String> gameCenterSourceMap = new HashMap<String , String>();
	private boolean flag = true;
	private String source_1 = "";
	private String source_2 = "";
	public static synchronized SourceFromGameCenterContentResover getInstance() {
		if (app == null) {
			app = new SourceFromGameCenterContentResover();
			 String pack=GameSDKApplication.getInstance().getPackFormGameCenter();
			 if(!"".equals(pack)){
				 CONTENT_URI="content://"+pack+".gamecenter.outer.GamePlayersProvider/game_trace";
			 } 
		}
		return app;
	}
	public HashMap<String , String> getSource(){
		if(gameCenterSourceMap == null || gameCenterSourceMap.isEmpty()){
			gameCenterSourceMap = GameSDKApplication.getInstance().getGameFromSource();
		}
		return gameCenterSourceMap;
	}
	public String getSource1 (){
		if(source_1 == null || source_1.equals("")){
			source_1 = getSource().get(HYConstant.COL_NAME_GAME_TRACE_SOURCE_1);
			source_2 = getSource().get(HYConstant.COL_NAME_GAME_TRACE_SOURCE_2);
		}
		return source_1;
	}
	public String getSource2 (){
		if(source_2 == null || source_2.equals("")){
			source_1 = getSource().get(HYConstant.COL_NAME_GAME_TRACE_SOURCE_1);
			source_2 = getSource().get(HYConstant.COL_NAME_GAME_TRACE_SOURCE_2);
		}
		return source_2;
	}
	
	//查询并存储source(从合乐智趣主客来源位置信息)
	public void setSource(){
	    	 String []values = {GameSDKApplication.getInstance().getAppid()};
	    	 Uri uri=null;
	    	 Cursor cursor=null;
	         resolver=GameSDKApplication.getInstance().getContext().getContentResolver();
        	 uri = Uri.parse(CONTENT_URI);
             cursor = resolver.query(uri, null, COL_NAME_GAME_TRACE_GAME_ID+" = ? ", values, null);
             //判断游标是否为空
             if(cursor!=null&&cursor.moveToFirst()){
            	 source_1 = cursor.getString(2);
            	 source_2 = cursor.getString(3);
            	 gameCenterSourceMap.put(HYConstant.COL_NAME_GAME_TRACE_SOURCE_1, source_1);
            	 gameCenterSourceMap.put(HYConstant.COL_NAME_GAME_TRACE_SOURCE_2, source_2);
             }
		 if(gameCenterSourceMap==null || gameCenterSourceMap.isEmpty()) return;
		 GameSDKApplication.getInstance().setGameFromSource(gameCenterSourceMap);
	}
     
     
     public void destroy(){
    	 app=null;
 	}
     
}
