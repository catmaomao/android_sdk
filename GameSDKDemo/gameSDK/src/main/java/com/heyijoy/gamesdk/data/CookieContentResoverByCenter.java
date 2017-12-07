package com.heyijoy.gamesdk.data;

import com.heyijoy.gamesdk.act.GameSDKApplication;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * 
 * @author yutong
 * @version
 * @Date 2015-9-9
 */
public class CookieContentResoverByCenter {

	public static String CONTENT_URI = "content://com.youku.gamecenter.outer.GamePlayersProvider/gamecenter_cookie";
	private static CookieContentResoverByCenter app;
	private static String CONTENT_URI1 = "";
	private static String CONTENT_URI2 = "";
	private static boolean flag = false;

	private CookieContentResoverByCenter() {

	}

	public static String getCONTENT_URI() {
		return CONTENT_URI;
	}

	public static synchronized CookieContentResoverByCenter getInstance() {
		if (app == null) {
			app = new CookieContentResoverByCenter();
			String pack = GameSDKApplication.getInstance()
					.getPackFormGameCenter();
			if (!"".equals(pack)) {
				CONTENT_URI = "content://"
						+ pack
						+ ".gamecenter.outer.GamePlayersProvider/gamecenter_cookie";
			}
			if ("content://com.youku.gamecenter.outer.GamePlayersProvider/gamecenter_cookie"
					.equals(CONTENT_URI)) {
				flag = true;
				CONTENT_URI1 = "content://com.youku.gamecenter.outer.GamePlayersProvider/gamecenter_cookie";
				CONTENT_URI2 = "content://com.youku.phone.gamecenter.outer.GamePlayersProvider/gamecenter_cookie";
			}
		}
		return app;
	}

	private static ContentResolver resolver;
	public static final String NAME = "name";
	public static final String COOKIE = "cookies";

	// 查询
	public static GamePlayersCookieBean queryCookie() {
		Uri uri = null;
		Cursor cursor = null;
		String[] projection  = {NAME,COOKIE};
		resolver = GameSDKApplication.getInstance().getContext()
				.getContentResolver();
		if (flag) {
			uri = Uri.parse(CONTENT_URI1);
			cursor = resolver.query(uri, projection, null, null, null);
			// 判断游标是否为空
			if (cursor != null && cursor.moveToFirst()) {
				String name = cursor.getString(cursor.getColumnIndex(NAME));
				String cookie = cursor.getString(cursor.getColumnIndex(COOKIE));
				GamePlayersCookieBean gocb=new GamePlayersCookieBean();
				gocb.setCookie(cookie);
				gocb.setName(name);
				return gocb;
			}
			uri = Uri.parse(CONTENT_URI2);
			cursor = resolver.query(uri, projection, null, null, null);
			// 判断游标是否为空
			if (cursor != null && cursor.moveToFirst()) {
				String name = cursor.getString(cursor.getColumnIndex(NAME));
				String cookie = cursor.getString(cursor.getColumnIndex(COOKIE));
				GamePlayersCookieBean gocb=new GamePlayersCookieBean();
				gocb.setCookie(cookie);
				gocb.setName(name);
				return gocb;
			}
		} else {
			uri = Uri.parse(CONTENT_URI);
			cursor = resolver.query(uri, projection, null, null, null);
			// 判断游标是否为空
			if (cursor != null && cursor.moveToFirst()) {
				String name = cursor.getString(cursor.getColumnIndex(NAME));
				String cookie = cursor.getString(cursor.getColumnIndex(COOKIE));
				GamePlayersCookieBean gocb=new GamePlayersCookieBean();
				gocb.setCookie(cookie);
				gocb.setName(name);
				return gocb;
			}
		}
		return null;
	}

	public void destroy() {
		app = null;
	}

}
