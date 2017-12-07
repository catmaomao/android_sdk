/**
 * Announcement.java
 * com.heyijoy.gamesdk.data
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-9-24 		msh
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.announcement;

import android.graphics.Bitmap;

/**
 * ClassName:Announcement
 * @author   msh
 * @version  
 * @since    Ver 1.1
 * @Date	 2014-9-24		上午10:03:50
 */
public class PopUpBean implements ImageCache,Skipable{

	private String title;
	private String id;
	private String skipType;//跳转类型
	private String imageurl;//图片指向的链接
	private String imageadd;//图片地址
	
	private String video_link;
	private String video_vid;
	private Bitmap bannar;
	private String intentId;
	private static PopUpBean popUpBean;
	private PopUpBean(){
		
	}
	public static synchronized PopUpBean getInstance(){
		if(popUpBean == null){
			popUpBean = new PopUpBean();
		}
		return popUpBean;
	}
	
	
	public String getVideo_link() {
		return video_link;
	}
	public void setVideo_link(String video_link) {
		this.video_link = video_link;
	}
	public String getVideo_vid() {
		return video_vid;
	}
	public void setVideo_vid(String video_vid) {
		this.video_vid = video_vid;
	}
	public String getIntentId() {
		return intentId;
	}
	public void setIntentId(String intentId) {
		this.intentId = intentId;
	}
	public String getSkipType() {
		return skipType;
	}
	public void setSkipType(String skipType) {
		this.skipType = skipType;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	public String getImageadd() {
		return imageadd;
	}
	public void setImageadd(String imageadd) {
		this.imageadd = imageadd;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Bitmap getBannar() {
		return bannar;
	}
	public void setBannar(Bitmap bannar) {
		this.bannar = bannar;
	}
	public void destroy(){
		if(this.bannar!=null){
			this.bannar.recycle();
		}
		this.bannar = null;
	}
	@Override
	public String getImageAdd() {
		return this.getImageadd();
		
	}
	@Override
	public void setBitImage(Bitmap bitmap) {
		this.setBannar(bitmap);
	}
	@Override
	public String getUrl() {
		return getImageurl();
		
	}
	@Override
	public String getVideoId() {
		// TODO Auto-generated method stub
		return getVideo_vid();
	}
	@Override
	public String getVideoHink() {
		// TODO Auto-generated method stub
		return getVideo_link();
	}
}

