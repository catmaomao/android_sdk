package com.heyijoy.gamesdk.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * @author xuqi
 * @version
 * @Date 2015-8-1
 */
public class DBHelper extends SQLiteOpenHelper {
	

	public DBHelper(Context context) {
		super(context, DBConstant.DB_NAME, null, DBConstant.DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBConstant.CREATE_TABLE_MSG);
		db.execSQL(DBConstant.CREATE_TABLE_VIDEO);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DBConstant.CREATE_TABLE_MSG);
		db.execSQL(DBConstant.DROP_TABLE_VIDEO);
		db.execSQL(DBConstant.CREATE_TABLE_VIDEO);
	}


}
