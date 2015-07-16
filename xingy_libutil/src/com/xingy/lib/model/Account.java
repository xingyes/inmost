package com.xingy.lib.model;
/**
 * 
 * @author xingyao
 *
 */
public class Account 
{
	//public static final int TYPE_PHONE = 0;
	//public static final int TYPE_QQ = 1;
	//public static final int TYPE_WECHAT = 2;
	
	
	// Private part.
	private String uid="";
	
	private String skey="";
	
	private String nickName="";
	
	private long rowCreateTime;
	
	//private int  type = TYPE_PHONE;
	
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String aid) {
		this.uid = aid;
	}
	
	public String getSkey() {
		return skey;
	}
	public void setSkey(String akey) {
		this.skey = akey;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public long getRowCreateTime() {
		return rowCreateTime;
	}
	public void setRowCreateTime(long rowCreateTime) {
		this.rowCreateTime = rowCreateTime;
	}
	
	
}
