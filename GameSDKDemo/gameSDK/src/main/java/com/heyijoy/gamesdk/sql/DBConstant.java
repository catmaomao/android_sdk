package com.heyijoy.gamesdk.sql;


/**
 * @author xuqi
 * @version
 * @Date 2015-8-1
 */
public class DBConstant {
	
	/** 数据库名 */
	public final static String DB_NAME = "yk_sdk_msg.db";
	public final static int DB_VERSION = 11; 
	 
	 
	/** 表名 */
	public final static String TABLE_MSG = "yk_msg";
	public final static String TABLE_ID = "yk_id";
	
	/**字段名 */
	
	public final static String PRI_ID = "_id";
	public final static String MSG_ID = "msg_id";//msgId-1
	public final static String MSG_TYPE = "msg_type";//msgType-2
	public final static String MAIN_TITLE = "main_title";//mainTitle-3
	public final static String SUB_TITLE = "sub_title";//subTitle-4
	public final static String LINK_URL = "link_url";//linkUrl-5
	public final static String CONTENT = "content";//content-6
	public final static String BUTTON_TITLE = "button_title";//buttTitle-7
	public final static String APP_IDS = "app_ids";//appIds-8
	public final static String MID = "mid";//mid-9
	public final static String IS_DISPLAYED = "is_displayed";//isDisplayed-10
	public final static String SEND_TIME = "send_time";//sendTime-11
	public final static String OVERDUE_TIME = "overdue_time";//overduetime-12
	public final static String ID_URL = "id_url";//idUrl-13
	public final static String ICON = "icon";//icon-14
	public final static String PACKAGENAME = "package_name";//package_name-15
	public final static String ARRIVE_TIME = "arrive_time";//arriveTime-16
	
	
	/** 表名 */
	public final static String TABLE_VIDEO = "yk_video";
	
	/**字段名 */
	public final static String VIDEO_ID = "_id";
	public final static String VIDEO_NAME = "VIDEO_NAME";//视频名称
	public final static String VIDEO_LENGTH = "VIDEO_LENGTH";//时长
	public final static String VIDEO_SIZE = "VIDEO_SIZE";//大小
	public final static String VIDEO_INFO = "VIDEO_INFO";//简介
	public final static String VIDEO_STATUS = "VIDEO_STATUS";//上传状态
	public final static String VIDEO_PROGESS = "VIDEO_PROGESS";//上传进度
	public final static String VIDEO_BG_ADDRESS = "VIDEO_BG_ADDRESS";//后台地址
	public final static String VIDEO_PATH = "VIDEO_PATH";//本地路径
	public final static String VIDEO_CREATE_TIME = "VIDEO_CREATE_TIME";//创建时间
	public final static String VIDEO_USER = "VIDEO_USER";//用户
	public final static String VIDEO_UPLOAD_TOKEN = "VIDEO_UPLOAD_TOKEN";//续传token
	public final static String VIDEO_BG_ID = "VIDEO_BG_ID";//上传成功后的 后台播放id
	public final static String VIDEO_BACK_UP_1 = "VIDEO_BACK_UP_1";//备用字段
	public final static String VIDEO_BACK_UP_2 = "VIDEO_BACK_UP_2";//备用字段
	public final static String VIDEO_BACK_UP_3 = "VIDEO_BACK_UP_3";//备用字段
	public final static String VIDEO_BACK_UP_4 = "VIDEO_BACK_UP_4";//备用字段



	/** 创建表 */
	public final static String CREATE_TABLE_MSG = 
			"create table IF NOT EXISTS "+TABLE_MSG+"("+
					PRI_ID+" integer primary key autoincrement,"+
					MSG_ID+" varchar,"+	//1
					MSG_TYPE+" varchar, "+	//2
					MAIN_TITLE+" varchar, "+	//3
					SUB_TITLE+" varchar, "+	//4
					LINK_URL+" varchar, "+	//5
					CONTENT+" varchar, "+	//6
					BUTTON_TITLE+" varchar, "+	//7
					APP_IDS+" varchar,"+		//8
					MID+" varchar,"+		//9
					IS_DISPLAYED+" varchar, "+	//10
					SEND_TIME+" varchar, "+	//11
					OVERDUE_TIME+" varchar, "+	//12
					ID_URL+" varchar, "+	//13
					ICON+" varchar, "+	//14
					PACKAGENAME+" varchar, "+	//15
					ARRIVE_TIME+" varchar);";	//16
	
	public final static String CREATE_TABLE_VIDEO = 
			" create table IF NOT EXISTS "+TABLE_VIDEO+"("+
					VIDEO_ID+" integer primary key autoincrement,"+
					VIDEO_NAME+" varchar,"+	//1
					VIDEO_LENGTH+" FLOAT, "+	//2
					VIDEO_SIZE+" FLOAT, "+	//3
					VIDEO_INFO+" varchar, "+	//4
					VIDEO_STATUS+" varchar, "+	//5
					VIDEO_PROGESS+" int, "+	//6
					VIDEO_BG_ADDRESS+" varchar, "+	//7
					VIDEO_CREATE_TIME+" FLOAT,"+		//8
					VIDEO_PATH+" varchar,"+	//9
					VIDEO_USER+" varchar,"+//10
					VIDEO_UPLOAD_TOKEN+" varchar,"+//11
					VIDEO_BG_ID+" varchar,"+//12
					VIDEO_BACK_UP_1+" varchar,"+//13
					VIDEO_BACK_UP_2+" varchar,"+//14
					VIDEO_BACK_UP_3+" varchar,"+//15
					VIDEO_BACK_UP_4+" varchar);";	//16
	public final static String DROP_TABLE_VIDEO = 
			" DROP TABLE IF EXISTS "+TABLE_VIDEO+" ;";	
	
}
