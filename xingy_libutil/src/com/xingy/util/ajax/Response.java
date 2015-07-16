package com.xingy.util.ajax;

import com.xingy.util.ToolUtil;

import java.util.HashMap;
import java.util.Locale;

public class Response {

	private Cookie mCookie;

	private int mId;

	private HashMap<String, String> responseHeader;

	private String mCharset;

	private String mUrl;

	private int mHttpstatus;
	
	private Object tag;
	
	void setTag(Object tag){
		this.tag = tag;
	}
	
	public Object getTag(){
		return tag;
	}

	public int getHttpStatus() {
		return mHttpstatus;
	}

	void setHttpStatus(int httpStatus) {
		mHttpstatus = httpStatus;
	}

	void setResponseHeader(HashMap<String, String> header) {
		responseHeader = header;
	}

	void setCookie(Cookie cookie) {
		mCookie = cookie;
	}

	void setCharset(String charset) {
		mCharset = charset;
	}
	
	String getCharset()
	{
		return mCharset;
	}

	void setId(int id) {
		mId = id;
	}

	public int getId() {
		return mId;
	}

	public String getUrl() {
		return mUrl;
	}

	void setUrl(String url) {
		mUrl = url;
	}

	public Cookie getCookie() {
		return mCookie;
	}

	public HashMap<String, String> getResponseHeader() {
		return responseHeader;
	}

	public String getResponseHeaderField(String field) {
		return getResponseHeader() != null ? getResponseHeader().get(field.toLowerCase(Locale.getDefault())) : null;
	}

	public long getResponseHeaderDate(String field, long defaultValue) {
		String date = getResponseHeaderField(field);
		if (date == null) {
			return defaultValue;
		}
		return ToolUtil.parseDate(date);
	}

	public long getLastModified() {
		return getResponseHeaderDate("Last-Modified", 0);
	}

	public String getCookie(String name) {
		return mCookie == null ? null : mCookie.get(name);
	}
}
