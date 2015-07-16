package com.xingy.lib.model;

import com.xingy.util.ToolUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionModel extends BaseModel {
	private int version;
	private String url;
	private boolean forceUpdate;
	private long expireTime;
	private String desc;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void parse(JSONObject json) throws JSONException {
		setVersion(json.getInt("version"));
		setUrl(json.getString("url"));
		setForceUpdate(json.getInt("force_update") == 1);
		setExpireTime(json.getLong("expire_time"));
		setDesc(json.getString("desc"));

		if (!isForceUpdate() && ToolUtil.getCurrentTime() >= getExpireTime()) {
			setForceUpdate(true);
		}
	}
}
