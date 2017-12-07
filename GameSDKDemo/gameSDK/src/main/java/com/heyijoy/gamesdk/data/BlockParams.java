
package com.heyijoy.gamesdk.data;

import java.io.Serializable;

/**
 * 
 * @author mashaohu
 *
 */
public class BlockParams implements Serializable{
	
	private String block_device_id;
	private String block_username;
	private String block_password;
	
	public String getBlock_device_id() {
		return block_device_id;
	}
	public void setBlock_device_id(String block_device_id) {
		this.block_device_id = block_device_id;
	}
	public String getBlock_username() {
		return block_username;
	}
	public void setBlock_username(String block_username) {
		this.block_username = block_username;
	}
	public String getBlock_password() {
		return block_password;
	}
	public void setBlock_password(String block_password) {
		this.block_password = block_password;
	}
	
}

