package com.xingy.util.ajax;

import java.util.HashMap;

public interface HttpRequest {
	
	long getRequestTime();

	boolean send();

	Cookie getCookie();

	void setCookie(Cookie cookie);

	String getCharset();

	void setOnProgressListener(OnProgressListener listener);

	void cancel();

	void setGetDataTimeout(int millsecond);

	void setPostDataTimeout(int millsecond);

	void setConnectTimeout(int millsecond);

	void setTryLimit(int tryLimit);

	HashMap<String, String> getResponseHeader();

	void setRequestHeader(String key, String value);

	void setUrl(String url);
	
	void setIfModifiedSince(long milliseconds);

	void setData(String name, Object value);

	void setData(HashMap<String, Object> param);

	byte[] getResponseData();

	void setFile(String name, byte[] data);
	
	void setFile(String name, byte[] data, String fileName);

	void setResponseDefaultCharset(String charset);

	void setRequestCharset(String charset);
	
	void setId(int id);

	int getId();
	
	int getHttpStatus();
	
	void setIsNeedResponse(boolean needResponse);
	
}
