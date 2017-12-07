package com.heyijoy.gamesdk.sql;

import java.util.ArrayList;
import java.util.List;

import com.heyijoy.gamesdk.data.MsgBean;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author xuqi
 * @version
 * @Date 2015-8-1
 */
public class MsgDBTool {

	// private static MsgDBTool app;
	private static DBHelper dbHelper;
	private static SQLiteDatabase db;

	public MsgDBTool(Context context) {
		dbHelper = new DBHelper(context);
	}

	// public static synchronized MsgDBTool getInstance(Context context) {
	// if (app == null) {
	// app = new MsgDBTool(context);
	// if(dbHelper == null) dbHelper = new DBHelper(context);
	// }
	// return app;
	// }

	/**
	 * 判断msg是否存在
	 * 
	 * @return List<MsgBean>
	 */
	public synchronized int isHasMsg(String msgId) {
		if (dbHelper == null) {
			return -1;
		}
		try{
			db = dbHelper.getReadableDatabase();
			if (db.isOpen()) {
				Cursor cursor = db.query(DBConstant.TABLE_MSG, null,
						DBConstant.MSG_ID + "=?", new String[] { msgId }, null,
						null, null);
				if (cursor != null && cursor.getCount() > 0) {
					db.close();
					return 1;
				} else {
					db.close();
					return 0;
				}
			}
			db.close();
		}catch(Exception e){
			e.printStackTrace();
			Logger.d("pack&ver","查询msg是否存在错误");
		}
		return -1;
	}

//	/**
//	 * 得到msg列表信息
//	 * 
//	 * @return List<MsgBean>
//	 */
//	public synchronized int getMsgList(Boolean isDisplayed) {
//		List<MsgBean> msgBeanList = new ArrayList<MsgBean>();
//		List<MsgBean> mList = new ArrayList<MsgBean>();
//		msgBeanList = getMsgList();
//		for (MsgBean msgBean : msgBeanList) {
//			if (msgBean.getIsDisplayed() == isDisplayed) {
//				mList.add(msgBean);
//			}
//		}
//		return mList.size();
//	}

	/**
	 * 得到msg列表信息
	 * 
	 * @return List<MsgBean>
	 */
	public synchronized List<MsgBean> getMsgList() {
		 if(dbHelper==null)
		 {
		 return null;
		 }
		 List<MsgBean> msgBeanList = new ArrayList<MsgBean>();
		 
		 try{
			 db = dbHelper.getReadableDatabase();
			 MsgBean msgBean = null;
			 Cursor cursor = db.query(DBConstant.TABLE_MSG, null, null, null, null,
					 null, DBConstant.PRI_ID + " desc");
			 if (cursor != null && cursor.getCount() > 0) {
				 while (cursor.moveToNext()) {
					 msgBean = getMsgFromCursor(cursor);
					 if (msgBean != null) {
						 msgBeanList.add(msgBean);
					 }
				 }
			 }
			 cursor.close();
			 db.close();
			 if (msgBeanList.size() > 30) {
				 msgBeanList = alterList(msgBeanList);
			 }
		}catch(Exception e){
			e.printStackTrace();
			Logger.d("pack&ver","消息列表获取错误");
		}
		return msgBeanList;
	}

	/**
	 * 得到未展示过信息的数量
	 * 
	 * @return int
	 */

	public int getNeverShowMsgCount() {
		if (dbHelper == null) {
			return 0;
		}
		try{
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.query(DBConstant.TABLE_MSG, null,
					DBConstant.IS_DISPLAYED + "=?", new String[] { "0" }, null,
					null, null);
			if (cursor != null && cursor.getCount() > 0) {
				return cursor.getCount();
			}
			cursor.close();
			db.close();
		}catch(Exception e){
			e.printStackTrace();
			Logger.d("pack&ver","得到未展示过信息的数量错误");
		}
		return 0;
	}

	public List<MsgBean> alterList(List<MsgBean> msgBeanList) {
		int size = msgBeanList.size();
		int num = size - 30;
		MsgBean msgBean = null;
		for (int i = 1; i <= num; i++) {
			msgBean = msgBeanList.get(size - i);
			removeMsg(msgBean.getMsgId());
			msgBeanList.remove(size - i);
		}
		return msgBeanList;
	}

	public MsgBean getMsgFromCursor(Cursor cursor) throws Exception{
		int i = 1;
		MsgBean msgBean = new MsgBean();
		String msgId = cursor.getString(i);
		msgBean.setMsgId(msgId);// 1
		String type = cursor.getString(++i);
		msgBean.setMsgType(type);// 2
		msgBean.setMainTitle(cursor.getString(++i));// 3
		msgBean.setSubTitle(cursor.getString(++i));// 4
		msgBean.setLinkUrl(cursor.getString(++i));// 5
		msgBean.setContent(cursor.getString(++i));// 6
		msgBean.setButtTitle(cursor.getString(++i));// 7
		msgBean.setAppIds(cursor.getString(++i));// 8
		msgBean.setMid(cursor.getString(++i));// 9
		msgBean.setIsDisplayed(cursor.getString(++i).equals("0") ? false : true);// 10
		msgBean.setSendTime(cursor.getString(++i));// 11
		String overdueTime = cursor.getString(++i);
		msgBean.setOverdueTime(overdueTime);// 12
		msgBean.setIdUrl(cursor.getString(++i));// 13
		msgBean.setIcon(cursor.getString(++i));// 14
		msgBean.setPackageName(cursor.getString(++i));// 15
		msgBean.setArriveTime(cursor.getString(++i));// 16
		if ("2".equals(type) && !"".equals(overdueTime) && overdueTime != null) {
			try {
				if (Util.DateCompare(Util.getCurrentTime(), overdueTime)) {
					removeMsg(msgId);
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msgBean;
	}

	/**
	 * 修改点击状态
	 * 
	 * @param msgId
	 * @return
	 */
	public synchronized long updataIsDisplayed(String msgId) {
		try{
			if (dbHelper == null) {
				return -1;
			}
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBConstant.IS_DISPLAYED, "1");// 1即为点击过了
			long x = db.update(DBConstant.TABLE_MSG, values, DBConstant.MSG_ID
					+ "=?", new String[] { msgId });
			db.close();
			return x;
		}catch(Exception e){
			e.printStackTrace();
			Logger.d("pack&ver","消息未读状态修改错误");
		}
		return -1;
	}

	/**
	 * 存储下载的值， 返回>0，就成功
	 * 
	 * @param msgBean
	 * @return
	 */
	public synchronized long addMsg(MsgBean msgBean) {
		if (dbHelper == null) {
			return -1;
		}
		try{
			if (isHasMsg(msgBean.getMsgId()) == 0) {
				db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				// 存储数据
				values.put(DBConstant.MSG_ID, msgBean.getMsgId());// 1
				values.put(DBConstant.MSG_TYPE, msgBean.getMsgType());// 2
				values.put(DBConstant.MAIN_TITLE, msgBean.getMainTitle());// 3
				values.put(DBConstant.SUB_TITLE, msgBean.getSubTitle());// 4
				values.put(DBConstant.LINK_URL, msgBean.getLinkUrl());// 5
				values.put(DBConstant.CONTENT, msgBean.getContent());// 6
				values.put(DBConstant.BUTTON_TITLE, msgBean.getButtTitle());// 7
				values.put(DBConstant.APP_IDS, msgBean.getAppIds());// 8
				values.put(DBConstant.MID, msgBean.getMid());// 9
				values.put(DBConstant.IS_DISPLAYED, msgBean.getIsDisplayed() ? "1"
						: "0");// 10
				values.put(DBConstant.SEND_TIME, msgBean.getSendTime());// 11
				values.put(DBConstant.OVERDUE_TIME, msgBean.getOverdueTime());// 12
				values.put(DBConstant.ID_URL, msgBean.getIdUrl());// 13
				values.put(DBConstant.ICON, msgBean.getIcon());// 14
				values.put(DBConstant.PACKAGENAME, msgBean.getPackageName());// 15
				values.put(DBConstant.ARRIVE_TIME, msgBean.getArriveTime());// 16
				long x = db.insert(DBConstant.TABLE_MSG, null, values);
				db.close();
				return x;
			}
		}catch(Exception e){
			e.printStackTrace();
			Logger.d("pack&ver","消息存储错误");
		}
		return -1;
	}

	public static synchronized int removeMsg(String msgId) {
		if (dbHelper == null) {
			return -1;
		}
		try{
			db = dbHelper.getWritableDatabase();
			if (db.isOpen()) {
				int x = db.delete(DBConstant.TABLE_MSG, DBConstant.MSG_ID + "=?",
						new String[] { msgId });
				db.close();
				return x;
			}
			db.close();
		}catch(Exception e){
			e.printStackTrace();
			Logger.d("pack&ver","消息remove错误");
		}
		return -1;
	}

	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		if (dbHelper == null) {
			return;
		}
		dbHelper.close();
	}
}
