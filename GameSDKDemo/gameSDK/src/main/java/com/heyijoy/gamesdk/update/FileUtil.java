package com.heyijoy.gamesdk.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Calendar;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
/**
 * 文件工具类
 *
 */
public class FileUtil {
	/**
	 * 开始消息提示常量
	 * */
	public static final int startDownloadMeg = 1;
	
	/**
	 * 更新消息提示常量
	 * */
	public static final int updateDownloadMeg = 2;
	
	
	/**
	 * updateFail:下载异常常量
	 *
	 * @since Ver 1.1
	 */
	public static final int updateFail = 4;
	
	public static final int sdkFail = 5;
	
	/**
	 * 完成消息提示常量
	 * */
	public static final int endDownloadMeg = 3;
	
	/**
	 * 检验SDcard状态  
	 * @return boolean
	 */
	public static boolean  checkSDCard()
	{
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 保存文件文件到目录
	 * @param context
	 * @return  文件保存的目录
	 */
	public static String setMkdir(Context context)
	{
		String filePath;
		if(checkSDCard())
		{
			filePath = Environment.getExternalStorageDirectory()+File.separator+HYConstant.FILE_SAVE_PATH;
		}else{
			filePath = context.getCacheDir().getAbsolutePath()+File.separator+HYConstant.FILE_SAVE_PATH;
		}
		File file = new File(filePath);
		if(!file.exists())
		{
			boolean b = file.mkdirs();
			Logger.v("file","文件不存在  创建文件    "+b);
		}else{
			Logger.v("file", "文件存在");
		}
		return filePath;
	}
	
	
	/**
	 * 保存文件文件到目录
	 * @param context
	 * @return  文件保存的目录
	 */
	public static String getVideoSavePath(Context context)
	{
		String filePath;
		if(checkSDCard())
		{
			filePath = Environment.getExternalStorageDirectory()+File.separator+HYConstant.VIDEO_FILE_SAVE_PATH+File.separator+GameSDKApplication.getInstance().getAppname();
		}else{
			filePath = context.getCacheDir().getAbsolutePath()+File.separator+HYConstant.VIDEO_FILE_SAVE_PATH;
		}
		File file = new File(filePath);
		if(!file.exists())
		{
			boolean b = file.mkdirs();
			Logger.v("file","文件不存在  创建文件    "+b);
		}else{
			Logger.v("file", "文件存在");
		}
		return filePath;
	}
	
	/**
	 * log后门
	 * @param context
	 * @return  开关
	 */
	public static boolean isOpenBackDoreLog(){
		String filePath;
		Calendar now = Calendar.getInstance(); 
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DAY_OF_MONTH);
		int code = month + day;
		if(checkSDCard())
		{
			filePath = Environment.getExternalStorageDirectory()+File.separator+"hy"+code;
			File file = new File(filePath);
			if(file.exists()){
				return true;
			}
		}
		return false;
	}
		
	
	/** 
	 * 得到文件的名称
	 * @return
	 * @throws IOException
	 */
	public static  String getFileName(String url)
	{
		String name= null;
		try {
			name = url.substring(url.lastIndexOf("/")+1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	/** 
	 * 得到文件的名称
	 * @return
	 * @throws IOException
	 */
	public static  String getFileType(String url)
	{
		String name= null;
		try {
			name = url.substring(url.lastIndexOf(".")+1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	 /**
		 * 关闭输入流>/br>
		 * @param InputStream
		 */
		public static void closeInputStream(InputStream input){
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					Logger.e("InputStream", "InputStream IOException "+e.getMessage());
				}
			}
		} 
		/**
		 * 关闭输出流</br>
		 * @param output
		 */
		public static void closeOutputStream(OutputStream output){
			if(output != null){
				try {
					output.close();
				} catch (IOException e) {
					Logger.e("OutputStream", "OutputStream IOException "+e.getMessage());
				}
			}
		}

		public static void deletePreVersion(){
			Context context = GameSDKApplication.getInstance().getContext();
			if(context == null){
				return;
			}
			String path = setMkdir(context);
			int versionCode = 0;
			try {
		    	PackageManager mangager =  context.getPackageManager();
		        PackageInfo info  =mangager.getPackageInfo(context.getPackageName(), 0);
		        		versionCode = info.versionCode;
		    } catch (NameNotFoundException e) {
		    	Logger.e(e.getMessage());
		    }
		 
			String fileName = GameSDKApplication.getInstance().getAppname()+"#"+versionCode+".apk";
			File saveFile = new File(path+"/"+fileName);
			if(saveFile.exists()){
				saveFile.delete();
			}
		}
		
			public static boolean isAvaiableSpace(long sizeb) {
				boolean ishasSpace = false;
				if (android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					String sdcard = Environment.getExternalStorageDirectory().getPath();
					StatFs statFs = new StatFs(sdcard);
					long blockSize = statFs.getBlockSize();
					long blocks = statFs.getAvailableBlocks();
					long availableSpare = blocks * blockSize;
					if (availableSpare > sizeb) {
						ishasSpace = true;
					}
				}else{
					String path = setMkdir(GameSDKApplication.getInstance().getContext());
					StatFs fileStats = new StatFs(path);  
			        fileStats.restat(path);  
			        long availableSpare = fileStats.getAvailableBlocks() * fileStats.getBlockSize(); // 注意与fileStats.getFreeBlocks()的区别  
			        if (availableSpare > sizeb) {
						ishasSpace = true;
					}
				}
				return ishasSpace;
			}
			
			public static long getLocalSize() {
				Context context = GameSDKApplication.getInstance().getContext();
				long localSize = 0;
				try {
					SharedPreferences preferences = context.getSharedPreferences(HYConstant.PREF_FILE_UPDATE_LOAD, Context.MODE_PRIVATE);
					String installFileName =  preferences.getString(HYConstant.PREF_FILE_UPDATE_INSTLL_FILE_NAME, "");
					File saveFile = new File(FileUtil.setMkdir(context)+"/"+installFileName);
					localSize = saveFile.length();
				} catch (Exception e) {
					return 0;
				}
				return localSize;
			}
			
			public static boolean a(String paramString, int paramInt1, int paramInt2,
					int paramInt3) {
				try {
					Class localClass = Class.forName("android.os.FileUtils");
					Method localMethod = localClass.getMethod("setPermissions",
							new Class[] { String.class, Integer.TYPE, Integer.TYPE,
									Integer.TYPE });
					localMethod.invoke(null,
							new Object[] { paramString, Integer.valueOf(paramInt1),
									Integer.valueOf(-1), Integer.valueOf(-1) });
					return true;
				} catch (Exception localClassNotFoundException) {
				} 
				return false;
			}

}
