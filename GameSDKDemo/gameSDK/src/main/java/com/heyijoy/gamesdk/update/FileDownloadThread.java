package com.heyijoy.gamesdk.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

import com.heyijoy.gamesdk.util.Logger;


public class FileDownloadThread extends Thread{
	private static final String TAG = "FileDownloadThread";
	/**缓冲区 */
	private static final int BUFF_SIZE = 1024;
	/**需要下载的URL*/
	private URL url;
	/**缓存的FIle*/
	private File file;
	/**开始位置*/
	private long startPosition;
	/**结束位置*/
	private long endPosition;
	/**当前位置*/
	private long curPosition;
	/**完成*/
	private boolean finished = false;
	/**已经下载多少*/
	private int downloadSize = 0;
	
	private boolean isFail;
	
	/***
	 * 分块文件下载，可以创建多线程模式
	 * @param url   下载的URL
	 * @param file  下载的文件
	 * @param startPosition 开始位置
	 * @param endPosition   结束位置
	 */
	public FileDownloadThread(URL url, File file, long startPosition,
			long endPosition) {
		this.url = url ;
		this.file = file;
		this.startPosition = startPosition;
		this.curPosition = startPosition;
		this.endPosition = endPosition;
		Logger.e(TAG, toString());
	}
	
	@Override
	public void run() {
		BufferedInputStream bis = null;
		RandomAccessFile rAccessFile = null;
		byte[] buf = new byte[BUFF_SIZE];
		URLConnection conn = null;
		try {
			conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setAllowUserInteraction(true);
					conn.setRequestProperty("Range", "bytes="+(startPosition)+"-"+endPosition);  //取剩余未下载的
					rAccessFile = new RandomAccessFile(file,"rwd");//读写
					 //设置从什么位置开始写入数据 
					rAccessFile.seek(startPosition);
					if(curPosition<endPosition){
						bis = new BufferedInputStream(conn.getInputStream(), BUFF_SIZE);
					}
					while(curPosition<endPosition)  //当前位置小于结束位置  继续下载
					{
						int len = bis.read(buf,0,BUFF_SIZE);
						if(len==-1)   //下载完成  
						{ 
							break;
						}
						rAccessFile.write(buf,0,len);
						curPosition = curPosition +len;
						if(curPosition > endPosition)
						{	//如果下载多了，则减去多余部分
							long extraLen = curPosition-endPosition;
							downloadSize += (len-extraLen+1);
						}else{
							downloadSize+=len;
						}
					}
					this.finished = true;  //当前阶段下载完成
					Logger.e(TAG, "当前"+this.getName()+"下载完成");
		}catch (ConnectException e) {
			isFail = true;
		} catch (Exception e) {
			isFail = true;
			Logger.e(TAG, "download error Exception "+e.getMessage());
			e.printStackTrace();
		}finally{
			//关闭流
			FileUtil.closeInputStream(bis);
			try {
				rAccessFile.close();
			} catch (IOException e) {
				Logger.e("AccessFile", "AccessFile IOException "+e.getMessage());
			}
		}
		super.run();
	}
	
	/**
	 * 是否完成当前段下载完成
	 * @return
	 */
	public boolean isFinished() {
		return finished;
	}
	/**
	 * 已经下载多少
	 * @return
	 */
	public int getDownloadSize() {
		return downloadSize;
	}

	public boolean getIsFail(){
		return this.isFail;
	}
	@Override
	public String toString() {
		return "FileDownloadThread [url=" + url + ", file=" + file
				+ ", startPosition=" + startPosition + ", endPosition="
				+ endPosition + ", curPosition=" + curPosition + ", finished="
				+ finished + ", downloadSize=" + downloadSize + "]";
	}
	
}
