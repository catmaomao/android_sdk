package com.heyijoy.gamesdk.data;
import com.heyijoy.gamesdk.util.Logger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class GamePlayersProvider extends ContentProvider {

	private static final String TAG = "PlayersProvider";

	private static final int DATABASE_VERSION = 1;

	public static final int PLAYERS = 1;

	private static final String DATABASE_NAME = "game_players.db";

	public static final String TABLE_NAME = "players";
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.youku.gamecenter.outer.GamePlayersProvider";

	public static final String COL_NAME_USERNAME = "userName";
	public static final String COL_NAME_PASSWORD = "password";
	public static final String COL_NAME_SEX = "sex";
	public static final String COL_NAME_ADDRESS = "address";
	public static final String COL_NAME_NICK = "nick";
	public static final String COL_NAME_UID = "uid";
	public static final String COL_NAME_IS_YOUKU_ACCOUNT = "isYoukuAccount";
	public static final String COL_NAME_IS_NEW_USER = "isNewUser";
	public static final String COL_NAME_SESSION = "session";
	public static final String COL_NAME_VERIFY_NO = "verifyNo";
	public static final String COL_NAME_STATE = "state";

	private DataBaseHeper mOpenHelper;
	private SQLiteDatabase db;

	private UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	@Override
	public boolean onCreate() {

		Logger.d(TAG, "onCreate");

		mOpenHelper = new DataBaseHeper(getContext());

		db = mOpenHelper.getWritableDatabase();

		mMatcher.addURI("com.youku.gamecenter.outer.GamePlayersProvider",
				"players", 1);

		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		Logger.d(TAG, "insert value ");

		long id = 0;

		try {

			if (mMatcher.match(uri) != PLAYERS) {
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			switch (mMatcher.match(uri)) {
			case PLAYERS:
				id = db.insert(TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			getContext().getContentResolver().notifyChange(uri, null);
		} catch (Exception e) {
			Logger.d(TAG, "insert error!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}

		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		Logger.d(TAG, "delete value ");

		int count = 0;

		try {
			switch (mMatcher.match(uri)) {
			case PLAYERS:
				count = db.delete(TABLE_NAME, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			getContext().getContentResolver().notifyChange(uri, null);

		} catch (Exception e) {
			Logger.d(TAG, "delete error!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		Logger.d(TAG, "update value ");

		int count = 0;

		try {
			switch (mMatcher.match(uri)) {
			case PLAYERS:
				count = db.update(TABLE_NAME, values, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			getContext().getContentResolver().notifyChange(uri, null);

		} catch (Exception e) {
			Logger.d(TAG, "update error!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}

		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Logger.d(TAG, "query value ");

		Cursor c = null;

		try {
			switch (mMatcher.match(uri)) {
			case PLAYERS:
				c = db.query(TABLE_NAME, projection, selection, selectionArgs,
						null, null, sortOrder);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

		} catch (Exception e) {
			Logger.d(TAG, "query error!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}

		return c;
	}

	@Override
	public String getType(Uri uri) {

		Logger.d(TAG, "getType ");

		try {
			switch (mMatcher.match(uri)) {
			case PLAYERS:
				return CONTENT_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
		} catch (Exception e) {
			Logger.d(TAG, "getType error!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}
		return null;
	}

	static class DataBaseHeper extends SQLiteOpenHelper {

		public DataBaseHeper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			Logger.d(TAG, "DataBaseHeper -> onCreate ");

			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_NAME_USERNAME
					+ " TEXT PRIMARY KEY," + COL_NAME_PASSWORD + " TEXT,"
					+ COL_NAME_SEX + " TEXT," + COL_NAME_ADDRESS + " TEXT,"
					+ COL_NAME_NICK + " TEXT," + COL_NAME_UID + " TEXT,"
					+ COL_NAME_IS_YOUKU_ACCOUNT + " TEXT,"
					+ COL_NAME_IS_NEW_USER + " TEXT," + COL_NAME_SESSION
					+ " TEXT," + COL_NAME_VERIFY_NO + " TEXT," + COL_NAME_STATE
					+ " TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Logger.d(TAG, "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

			onCreate(db);
		}

	}

}
