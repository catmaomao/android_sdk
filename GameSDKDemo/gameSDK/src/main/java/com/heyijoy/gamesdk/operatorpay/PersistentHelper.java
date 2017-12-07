package com.heyijoy.gamesdk.operatorpay;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistentHelper {
	public static final String DB_NAME = "tudouDB2"; // tudou Database
	public static final String DB_INFO_TABLE = "user_info"; // 用户信息
	public static final String DB_DOWNUP_TABLE = "up_download"; // upload download info
	public static final String DB_WATCHED_TABLE = "watched_video"; // 已观看过的video
	public static final String DB_SPECIALPIC_TABLE = "specialday_pic"; // 节日pic
	public static final String DB_DECODER_INFO_TABLE = "decoder_info"; 
	public static final String DB_COLUMN_TABLE="column";//频道信息
	
	public static final int DB_VERSION = 5;

	// 各个表的列
	private static final String[] COLUMN_COLS = new String[]{"_id", "columnId", "columnName"};


	private SQLiteDatabase db = null;
	private final DBOpenHelper dbOpenHelper;

	// 
	// inner classes
	//

	public static class ColumnInfo {
		public String mcolumnId = null;
		public String mcolumnName = null;
		public int mId=0;

		public ColumnInfo(String mcolumnId, String mcolumnName,int id) {
			this.mcolumnId = mcolumnId;
			this.mcolumnName = mcolumnName;
			this.mId=id;
		}

	}
	
	 

	// +-http://www.chinaandroid.com/thread-102-1-1.html
	private static class DBOpenHelper extends SQLiteOpenHelper { // _id设置为主键，自增
		private static final String DB_COLUMN_CREATE = "CREATE TABLE " + PersistentHelper.DB_COLUMN_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, columnId TEXT  UNIQUE NOT NULL  , columnname TEXT);";

		public DBOpenHelper(final Context context) {
			super(context, PersistentHelper.DB_NAME, null, PersistentHelper.DB_VERSION);
		}

		@Override // use db.execSQL to execute the SQL statements(to create table)
		public void onCreate(final SQLiteDatabase db) {
			try {
				db.execSQL(DBOpenHelper.DB_COLUMN_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			 
			}
		}

		@Override
		public void onOpen(final SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override // used for update the database by Version, delete the tables when DatabaseVersion is Changed
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + PersistentHelper.DB_COLUMN_TABLE);
			onCreate(db);
		}
	}

	//
	// end inner classes
	//

	public PersistentHelper(final Context context) {
 		this.dbOpenHelper = new DBOpenHelper(context);
		establishDb();
	}

	private void establishDb() {
		if (this.db == null) {
			this.db = this.dbOpenHelper.getWritableDatabase();
		}
	}

	public void cleanup() {
		if (this.db != null) {
			this.db.close();
			this.db = null;
		}
	}

	public void deleteColumn() {
		this.db.delete(PersistentHelper.DB_COLUMN_TABLE, null, null);
	}
	public void getColumn(Vector taskQueue) {
		
		
		Cursor c = null;
		try {
			c = this.db.query(true, PersistentHelper.DB_COLUMN_TABLE, PersistentHelper.COLUMN_COLS, null, null, null, null, null, null);
			if (c.getCount() > 0) {
				int numRows = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < numRows; ++i) {
					ColumnInfo entity = new ColumnInfo( c.getString(2), c.getString(1),c.getInt(0));
					taskQueue.add(entity);
					c.moveToNext();
				}

			}
		} catch (SQLException e) {
			
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			
		}
	}
	public void insert(ColumnInfo entity) { // 一个实体 其实就是一行记录。
		db.beginTransaction();  
		deleteColumn();
		ContentValues values = new ContentValues();
		values.put("columnId", entity.mcolumnId);
		values.put("columnName", entity.mcolumnName);
		 
		this.db.insert(DB_COLUMN_TABLE, null, values);
		Cursor c = null;
		try {
			c = this.db.query(true, PersistentHelper.DB_COLUMN_TABLE, PersistentHelper.COLUMN_COLS, null, null, null, null, null, null);
			if (c.getCount() > 0) {
				c.moveToLast();
				entity.mId= c.getInt(0);
			}
		} catch (SQLException e) {
			
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			 //结束事务  
	        db.endTransaction();  
		}
	}
	
 

	public void delete(final String ci) {
		this.db.delete(PersistentHelper.DB_INFO_TABLE, "CI='" + ci + "'", null);
	}

	// public void delete(final int id) {
	// this.db.delete(PersistentHelper.DB_INFO_TABLE, "_id='" + id + "'", null);
	// }

	public void deleteSpecialPic() {
		this.db.delete(PersistentHelper.DB_SPECIALPIC_TABLE, null, null);
	}

//	public void delete(LoadItemEntity mCurrentItem) {
//		this.db.delete(PersistentHelper.DB_DOWNUP_TABLE, "_id='" + mCurrentItem.getID() + "'", null);
//	}

}
