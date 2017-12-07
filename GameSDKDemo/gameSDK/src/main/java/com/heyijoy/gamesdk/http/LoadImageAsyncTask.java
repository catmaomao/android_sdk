/**
 * LoadImageAsyncTask.java
 * com.heyijoy.gamesdk.http
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-9-28 		msh
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.heyijoy.gamesdk.announcement.ImageCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * ClassName:LoadImageAsyncTask
 *
 * @author msh
 * @version
 * @since Ver 1.1
 * @Date 2014-9-28 上午10:30:26
 */
public class LoadImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {

	/**
	 * 异步加载图片
	 */

	private ImageCache imageCache;

	public LoadImageAsyncTask(ImageCache imageCache) {
		this.imageCache = imageCache;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		imageCache.setBitImage(bitmap);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = GetBitmapByUrl(imageCache.getImageAdd());
		return bitmap;
	}

	/**
	 * 获取Bitmap
	 * 
	 * 
	 * @param uri
	 *            图片地址
	 * @return
	 */
	public static Bitmap GetBitmapByUrl(String uri) {

		Bitmap bitmap;
		InputStream is;
		try {

			is = GetImageByUrl(uri);

			bitmap = BitmapFactory.decodeStream(is);
			if (is != null) {
				is.close();
			}

			return bitmap;

		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取图片流
	 * 
	 * @param uri
	 *            图片地址
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public static InputStream GetImageByUrl(String uri) throws MalformedURLException {
		URL url = new URL(uri);
		URLConnection conn;
		InputStream is;
		try {
			conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();

			// 或者用如下方法

			// is=(InputStream)url.getContent();
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
