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
/**
 * ClassName:Announcement
 * @author   msh
 * @version  
 * @since    Ver 1.1
 * @Date	 2014-9-24		上午10:03:50
 */
public class Announcement implements Skipable{

	private String title;
	private String id;
	private String content;
	private String urlAdd;
	private String urlContent;
	private String skipType;//跳转类型
	private String intentId;
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrlAdd() {
		return urlAdd;
	}
	public void setUrlAdd(String urlAdd) {
		this.urlAdd = urlAdd;
	}
	public String getUrlContent() {
		return urlContent;
	}
	public void setUrlContent(String urlContent) {
		this.urlContent = urlContent;
	}
	@Override
	public String getUrl() {
		return getUrlAdd();
		
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

