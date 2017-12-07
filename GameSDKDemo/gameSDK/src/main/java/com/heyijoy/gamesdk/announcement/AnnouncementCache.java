/**
 * AnnouncementCache.java
 * com.heyijoy.gamesdk.announcement
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-9-26 		msh
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.announcement;

import java.util.List;

import android.graphics.Bitmap;

/**
 * ClassName:AnnouncementCache
 *
 * @author   msh
 * @version  
 * @since    Ver 1.1
 * @Date	 2014-9-26		下午5:20:37
 */
public class AnnouncementCache implements ImageCache , Skipable{
	private static AnnouncementCache announcementCache;
	private String imgurl;
	private String imgadd;
	private Bitmap bannar;
	private String skipType;//跳转类型
	private String id;
	private String title;
private String intentId;
	
	public String getIntentId() {
		return intentId;
	}
	public void setIntentId(String intentId) {
		this.intentId = intentId;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	private List<Announcement> announcements;

	private  AnnouncementCache(){
		
	}
	
	public static synchronized AnnouncementCache getInstance(){
		if(announcementCache == null){
			announcementCache = new AnnouncementCache();
		}
		return announcementCache;
	}



	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getImgadd() {
		return imgadd;
	}

	public void setImgadd(String imgadd) {
		this.imgadd = imgadd;
	}

	public List<Announcement> getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(List<Announcement> announcements) {
		this.announcements = announcements;
	}
	
	public void destroy(){
		this.announcements = null;
		if(this.bannar!=null){
			this.bannar.recycle();
		}
		this.bannar = null;
	}

	public Bitmap getBannar() {
		return bannar;
	}

	public void setBannar(Bitmap bannar) {
		this.bannar = bannar;
	}

	public String getSkipType() {
		return skipType;
	}

	public void setSkipType(String skipType) {
		this.skipType = skipType;
	}

	@Override
	public String getImageAdd() {
		return this.getImgadd();
	}

	@Override
	public void setBitImage(Bitmap bitmap) {
		this.setBannar(bitmap);
	}

	@Override
	public String getUrl() {
		return getImgurl();
		
	}

	@Override
	public String getId() {
		return id;
		
	}
	@Override
	public String getVideoId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getVideoHink() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

