package com.heyijoy.gamesdk.update;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import com.heyijoy.gamesdk.util.Logger;

import android.os.Handler;
import android.os.Message;

/**
 *多线程下载，UI更新类 
 *@author young
 * */
public class MultiThreadDownload extends Thread{
	private static final String TAG = "MultiThreadDownload";
	/**每一个线程需要下载的大小 */
	private long blockSize;
	/*** 线程数量<br> 默认为1个线程下载   同时根据已经下载的文件大小来进行断点续传*/ 
	private int threadNum = 1;
	/*** 文件大小 */
	private long fileSize;
	/** * 已经下载多少 */
	private long downloadSize;
	/**文件的url,线程编号，文件名称*/
	private String UrlStr,ThreadNo,fileName;
	/***保存的路径*/
	private String savePath;
	/**下载的百分比*/
	private long downloadPercent = 0;
	/**下载的 平均速度*/
	private int downloadSpeed = 0;
	/**下载用的时间*/
	private int usedTime = 0;
	/**当前时间*/
	private long curTime;
	/**是否已经下载完成*/
	private boolean completed = false ;
	private Handler handler ;
	private long localSize;
	
	private boolean isFail;
	/**
	 * 下载的构造函数  
	 * @param url  请求下载的URL
	 * @param handler   UI更新使用
	 * @param savePath  保存文件的路径
	 */
	public MultiThreadDownload(Handler handler,String url,String savePath,String fileName)
	{
		this.handler = handler;
		this.UrlStr = url;
		this.savePath = savePath;
		this.fileName = fileName;
		Logger.e(TAG, toString());
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		FileDownloadThread[] fds = new FileDownloadThread[threadNum];//设置线程数量
		try {
			URL url = new URL(UrlStr);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(15*1000);
			fileSize = conn.getContentLength();
		
			
			if(fileSize<0){
				sendMsg(FileUtil.updateFail);
				return;
			}
			
			if(!FileUtil.isAvaiableSpace(fileSize)){
				sendMsg(FileUtil.sdkFail);
				return;
			}
			
			//只创建一个文件，saveFile下载内容
			File saveFile = new File(savePath+"/"+fileName);
			Logger.e(TAG, "文件一共："+fileSize+" savePath "+savePath+"  fileName  "+fileName);
			
			
			localSize = saveFile.length();
			
			if(localSize == fileSize){
				sendMsg(FileUtil.endDownloadMeg);
				return;
			}
			
			
			RandomAccessFile accessFile = new RandomAccessFile(saveFile,"rwd");
			//设置本地文件的长度和下载文件相同   
//			accessFile.setLength(fileSize);  
			accessFile.close();
			//Handler更新UI，发送消息
			sendMsg(FileUtil.startDownloadMeg);
			//每块线程下载数据
			blockSize = ((fileSize%threadNum)==0)?(fileSize/threadNum):(fileSize/threadNum+1);
			Logger.e(TAG, "每个线程分别下载 ："+blockSize);
			
			
			
			for (int i = 0; i < threadNum; i++) {
				long curThreadEndPosition = (i+1)!=threadNum ? ((i+1)*blockSize-1) : fileSize;
				FileDownloadThread fdt = new FileDownloadThread(url, saveFile, i*blockSize+localSize, curThreadEndPosition);
				fdt.setName("thread"+i);
				fdt.start();
				fds[i]=fdt;
			}
			/**
			 * 获取数据，更新UI，直到所有下载线程都下载完成。
			 */
			boolean finished = false;
			//开始时间，放在循环外，求解的usedTime就是总时间
			long startTime = System.currentTimeMillis();
			while(!finished)
			{
				downloadSize = 0;
				finished = true;
				for (int i = 0; i < fds.length; i++) {
					downloadSize+= fds[i].getDownloadSize();
					if(fds[i].getIsFail()){
						isFail = true;
					}
					if(!fds[i].isFinished())
					{
						finished = false;
					}
				}
				downloadPercent = ((downloadSize+localSize)*100)/fileSize;
				curTime = System.currentTimeMillis();
				usedTime = (int) ((curTime-startTime)/1000);
				
				if(usedTime==0)usedTime = 1;  
				downloadSpeed = (int) ((downloadSize/usedTime)/1024);
				sleep(1000);/*1秒钟刷新一次界面*/
				if(isFail){
					sendMsg(FileUtil.updateFail);
					return;
				}else{
					sendMsg(FileUtil.updateDownloadMeg);
				}
			}
			Logger.e(TAG, "下载完成");
			completed = true;
			
			//下载完成删除之前版本
			FileUtil.deletePreVersion();
			sendMsg(FileUtil.endDownloadMeg);
		} catch (Exception e) {
			Logger.e(TAG, "multi file error  Exception  "+e.getMessage());
			e.printStackTrace();
			sendMsg(FileUtil.sdkFail);
		}
		super.run();
	}
	/**
	 * 得到文件的大小
	 * @return
	 */
	public long getFileSize()
	{
		return this.fileSize;
	}
	/**
	 * 得到已经下载的数量
	 * @return
	 */
	public int getDownloadSize()
	{
		return (int)this.downloadSize + (int)localSize;
	}
    /**
     * 获取下载百分比
     * @return
     */
	public int getDownloadPercent(){
		if(this.downloadPercent<0){
			Logger.v("downloadPercent<0");
		}
		return (int)this.downloadPercent;
	}
   /**
    * 获取下载速度
    * @return
    */
	public int getDownloadSpeed(){
		return this.downloadSpeed;
	}
	/**
	 * 修改默认线程数
	 * @param threadNum
	 */
	public void setThreadNum(int threadNum){
		this.threadNum = threadNum;
	}
    /**
     * 分块下载完成的标志
     * @return
     */
	public boolean isCompleted(){
		return this.completed;
	}
	@Override
	public String toString() {
		return "MultiThreadDownload [threadNum=" + threadNum + ", fileSize="
				+ fileSize + ", UrlStr=" + UrlStr + ", ThreadNo=" + ThreadNo
				+ ", savePath=" + savePath + "]";
	}
	
	/**
	 * 发送消息，用户提示
	 * */
	private void sendMsg(int what)
	{
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}
	
	public long getLocalSize(){
		return localSize;
	}
	
	/*public int getLocalPercent(){
		return (int)localSize*100/fileSize;
	}*/
}
