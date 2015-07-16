package com.xingy.util.ajax;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.xingy.util.Config;
import com.xingy.util.Escape;
import com.xingy.util.Log;
import com.xingy.util.MyApplication;
import com.xingy.util.ToolUtil;

import org.apache.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class HttpGet implements HttpRequest {

	protected static final String LOG_TAG = HttpGet.class.getName();

	protected static final int BUFFERSIZE = 1024;

	protected static final int PROGRESS_NOTIFY_OFFSET = 1000;

	protected final Context mContext;

	protected long requestStart = 0;

	protected long requestEnd = 0;

	protected int tryCount = 0;

	protected int mGetDataTimeout = Config.GET_DATA_TIME_OUT;

	protected int mPostDataTimeout = Config.POST_DATA_TIME_OUT;

	protected int mConnecTimeout = Config.CONNECT_TIME_OUT;

	protected int mTryLimit = 2;

	protected String mUrl;

	protected HashMap<String, Object> mData;

	protected HashMap<String, String> mRequestHeader;

	protected HashMap<String, String> mResponseHeader;

	protected OnProgressListener mOnProgressListener = null;

	protected boolean mCancel = false;

	protected byte[] responseData;

	private String responseDefaultCharSet = "utf-8";

	private int id;

	private long mIfModifiedSince;

	protected String requestCharset = "utf-8";

	protected int mHttpStatus;

	protected long lastProgressNotifyTime;

	protected boolean mIsNeedResponse = true;
	
	protected boolean mIsLimited;
	
	private static volatile String mProxyUser = null;
	
	private static volatile boolean mHaveInitProxyUser = false;
	
	private static volatile HttpUtil.NetworkState mLastNetState = null;

	public HttpGet(Context context) {
		mContext = context;
		mRequestHeader = new HashMap<String, String>();
		mResponseHeader = new HashMap<String, String>();
	}

	public static void initPorxyUser(HttpUtil.NetworkState state){
		synchronized(HttpGet.class){
			
			if( mLastNetState == null || mLastNetState != state ){
				mHaveInitProxyUser = false;
				mLastNetState = state;
			}
			
			if(mHaveInitProxyUser == false){
				mHaveInitProxyUser = true;
				
				Cursor apn = null;
				try{
					Uri uri = Uri.parse("content://telephony/carriers/preferapn");
					apn = MyApplication.app.getContentResolver().query(uri, null, null, null, null);
					
					if(apn!=null){
						if (apn.moveToNext()){
							String name = apn.getString(apn.getColumnIndex("user"));
							String pwd = apn.getString(apn.getColumnIndex("password"));
							String login = name + ":" + pwd;
							String encodedLogin = ToolUtil.base64Encode(login.getBytes());
							mProxyUser = "Basic " + encodedLogin;
						}
					}
				}catch(Exception ex)
				{
					
				}finally
				{
					if(null!=apn && !apn.isClosed())
						apn.close();
				}
			}
		}
	}
	@Override
	public long getRequestTime() {
		return requestEnd - requestStart;
	}

	@Override
	public void setResponseDefaultCharset(String charset) {
		responseDefaultCharSet = charset;
	}

	@Override
	public void setRequestCharset(String charset) {
		requestCharset = charset;
	}

	@Override
	public Cookie getCookie() {
		final HashMap<String, String> responseHeader = getResponseHeader();
		if (null == responseHeader)
			return null;

		String value = responseHeader.get("set-cookie");

		if (null == value)
			return null;

		String[] values = value.split("<--->");
		Pattern pattern = Pattern.compile("^([^\\=]+)=([^;]*)");

		final Cookie cookie = new Cookie();

		for (int i = 0, len = values.length; i < len; i++) {
			Matcher matcher = pattern.matcher(values[i]);
			if (matcher.find()) {
				cookie.set(matcher.group(1), matcher.group(2));
			}
		}

		return cookie;
	}

	protected HttpURLConnection getConnect(Context context, URL url) throws Exception {
		
		if (null == url) {
			throw new IOException(LOG_TAG + "|getConnect|url is empty");
		}
		
		final HttpUtil.NetworkState state = HttpUtil.getNetworkState(mContext);

		if (state == HttpUtil.NetworkState.UNAVAIL) {
			throw new HttpUnavailableException("net is unavailable");
		}
		
		initPorxyUser(state);
		
		HttpURLConnection conn = null;
		mIsLimited = false;
		
		if(state ==  HttpUtil.NetworkState.MOBILE){
			String proxyHost = android.net.Proxy.getDefaultHost();    
			if (proxyHost != null && proxyHost.length() > 0) {
				if(isCMCCServer(proxyHost)){
					mIsLimited = true;
					StringBuffer new_address = new StringBuffer(80);
					new_address.append("http://");
					new_address.append(android.net.Proxy.getDefaultHost());
					String file = url.getFile();
					if(file != null && file.startsWith("?")){
						new_address.append("/");
					}
					new_address.append(file);
					URL new_url = new URL(new_address.toString());   
					conn = (HttpURLConnection) new_url.openConnection();   
					conn.setRequestProperty("X-Online-Host", url.getHost());
				}else{
					java.net.Proxy p = null;
					p = new java.net.Proxy(java.net.Proxy.Type.HTTP,    
							new InetSocketAddress(android.net.Proxy.getDefaultHost(),android.net.Proxy.getDefaultPort()));  
					conn = (HttpURLConnection)url.openConnection(p);
					if(mProxyUser != null){
						conn.setRequestProperty("Proxy-Authorization", mProxyUser); 
					}
				}
			}
		}
		if(conn == null){
			conn = (HttpURLConnection)url.openConnection();
		}
		
		return conn;
	}
	
	private boolean isCMCCServer(String ip){
		boolean ret = false;
		Matcher m = Pattern.compile("^[0]{0,1}10\\.[0]{1,3}\\.[0]{1,3}\\.172$", Pattern.MULTILINE).matcher(ip);
		if(m.find()){
			ret = true;
		}else{
			ret = false;
		}
		return ret;
	}

	@Override
	public boolean send() {
		boolean isSuccess = false;

		HttpURLConnection conn = null;

		ByteArrayOutputStream output = null;

		InputStream input = null;
		String strNetTypeName= "";

		try {
			String sUrl = getUrl();
			if (sUrl == null) {
				throw new Exception("you have not set request url");
			}

			sUrl = sUrl + (getData() == null ? "" : ((sUrl.indexOf("?") > -1 ? (sUrl.endsWith("&") ? "" : "&") : "?") + getParamString()));

			Log.d(LOG_TAG, getId() + ":" + sUrl);

			URL url = new URL(sUrl);

			conn = getConnect(mContext, url);
			checkCacnelStatus();
			conn.setConnectTimeout(getConnectTimeout());
			conn.setReadTimeout(getGetDataTimeout());

			final HashMap<String, String> header = getRequestHeader();
			if (null != header) {
				final Iterator<Entry<String, String>> inerator = header.entrySet().iterator();
				while (inerator.hasNext()) {
					Entry<String, String> entry = inerator.next();
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				//	Log.d("||" + entry.getKey(), entry.getValue());
				}
			}

			if (mIfModifiedSince > 0) {
				conn.setIfModifiedSince(mIfModifiedSince);
			}

			requestStart = new Date().getTime();
			checkCacnelStatus();
			conn.connect();
			checkCacnelStatus();
			final int responseCode = conn.getResponseCode();

			mHttpStatus = responseCode;

			
			if (responseCode == HttpStatus.SC_NOT_MODIFIED) {
				setResponseHeader(conn);
				requestEnd = new Date().getTime();
				return true;
			}
		
			if (responseCode != HttpStatus.SC_OK) {
				Log.d(LOG_TAG, responseCode + "|||");
				throw new java.net.SocketException("http status is not 200 or 304");
			}

			if (conn.getContentType().contains("text/vnd.wap.wml") == true && tryCount < getTryLimit()) {
				tryCount++;
				return send();
			}

			output = new ByteArrayOutputStream();

			input = conn.getInputStream();

			final int totalSize = conn.getContentLength();

			int num = 0, downLoaded = 0;
			
			byte[] buf = new byte[BUFFERSIZE];
			while (mIsNeedResponse && !checkCacnelStatus() && (num = input.read(buf)) != -1) {
				output.write(buf, 0, num);
				downLoaded += num;
				if (mOnProgressListener != null) {
					long now = ToolUtil.getCurrentTime();
					if (now - lastProgressNotifyTime > PROGRESS_NOTIFY_OFFSET) {
						lastProgressNotifyTime = now;
						mOnProgressListener.onProgress(null, downLoaded, totalSize);
					}
				}
			}
			checkCacnelStatus();
			
			requestEnd = ToolUtil.getCurrentTime();
			checkCacnelStatus();
			setResult(conn, output);
			buf = null;
			isSuccess = true;
		} catch (CancelException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex));
		} catch (java.net.ConnectException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex) + " " + getId());
			strNetTypeName = HttpUtil.getNetTypeName();
		} catch (java.net.SocketException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex) + " " + getId());
			strNetTypeName  = HttpUtil.getNetTypeName();
		} catch (Exception ex) {
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
			strNetTypeName  = HttpUtil.getNetTypeName();
		} finally {
			try {
				if (input != null) {
					input.close();
					input = null;
				}
				if (output != null) {
					output.close();
					output = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			} catch (Exception ex) {
				Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
			}
		}

		return isSuccess;
	}

	@Override
	public int getHttpStatus() {
		return mHttpStatus;
	}

	@Override
	public void setOnProgressListener(OnProgressListener listener) {
		mOnProgressListener = listener;
	}

	public boolean checkCacnelStatus() throws CancelException {
		if (mCancel) {
			throw new CancelException("request have been canceled");
		}
		return mCancel;
	}

	public byte[] getResponseData() {
		return responseData;
	}

	protected void setResponseHeader(HttpURLConnection conn) {
		conn.getLastModified();
		Map<String, List<String>> header = conn.getHeaderFields();
		if (null == header)
			return;

		Iterator<Entry<String, List<String>>> iterator = header.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> entry = (Entry<String, List<String>>) iterator.next();
			List<String> values = entry.getValue();
			int i = 0;
			String value = "";
			for (String s : values) {
				if (i > 0) {
					value += "<--->";
				}
				value += s;
				i++;
			}

			mResponseHeader.put(String.valueOf(entry.getKey()).toLowerCase(Locale.getDefault()), value);
		}
	}

	@Override
	public HashMap<String, String> getResponseHeader() {
		return mResponseHeader;
	}

	@Override
	public void setRequestHeader(String key, String value) {
		mRequestHeader.put(key, value);
	}

	protected HashMap<String, String> getRequestHeader() {
		return mRequestHeader;
	}

	@Override
	public String getCharset() {

		final HashMap<String, String> mResponseHeader = getResponseHeader();
		if (null == mResponseHeader)
			return responseDefaultCharSet;

		String contentType = mResponseHeader.get("content-type");

		if (null == contentType)
			return responseDefaultCharSet;

		Matcher matcher = Pattern.compile("charset=([^\\s;]+)").matcher(contentType);
		if (matcher.find()) {
			return matcher.group(1);
		}

		return responseDefaultCharSet;
	}

	@Override
	public void setGetDataTimeout(int millsecond) {
		mGetDataTimeout = millsecond;
	}

	@Override
	public void setPostDataTimeout(int millsecond) {
		mPostDataTimeout = millsecond;
	}

	@Override
	public void setConnectTimeout(int millsecond) {
		mConnecTimeout = millsecond;
	}

	@Override
	public void setCookie(Cookie cookie) {
		if (cookie == null)
			return;

		Set<Entry<String, String>> entry = cookie.getAll();
		Iterator<Entry<String, String>> keys = entry.iterator();
		String val = "", split = "";
		while (keys.hasNext()) {
			Entry<String, String> item = keys.next();
		//	val += split + Escape.escape(item.getKey()) + "=" + Escape.escape(item.getValue());
			if( !TextUtils.isEmpty(item.getKey()) && !TextUtils.isEmpty(item.getValue()) ) {
				val += split + item.getKey() + "="  + Escape.escape(item.getValue());
				split = "; ";//djcity 特殊分隔符
			}
		}

		setRequestHeader("Cookie", val);
	}

	@Override
	public void setUrl(String url) {
		mUrl = url;
	}

	@Override
	public void setData(HashMap<String, Object> data) {
		mData = data;
	}

	@Override
	public void setData(String name, Object value) {
		if (mData == null)
			mData = new HashMap<String, Object>();

		mData.put(name, value);
	}

	@Override
	public void setTryLimit(int tryLimit) {
		mTryLimit = tryLimit;
	}

	@Override
	public void cancel() {
		mCancel = true;
	}

	protected int getTryLimit() {
		return mTryLimit;
	}

	public String getUrl() {
		return mUrl;
	}

	protected HashMap<String, Object> getData() {
		return mData;
	}

	protected int getGetDataTimeout() {
		return mGetDataTimeout;
	}

	protected int getPostDataTimeout() {
		return mPostDataTimeout;
	}

	protected int getConnectTimeout() {
		return mConnecTimeout;
	}

	protected void setResult(HttpURLConnection conn, ByteArrayOutputStream output) throws IOException {

		setResponseHeader(conn);

		byte[] ret = output.toByteArray();
		//����ぢ懊�????�����??��酶????
		if(ret == null|| ret.length==0){
			responseData = null;
			return ;
		}
		
		final HashMap<String, String> mResponseHeader = getResponseHeader();
		if (null != mResponseHeader) {
			String encode = mResponseHeader.get("content-encoding");

			if (null != encode && encode.contains("gzip")) {
				ByteArrayInputStream input = new ByteArrayInputStream(ret);
				ByteArrayOutputStream tmpOutput = new ByteArrayOutputStream(BUFFERSIZE);

				GZIPInputStream gin = new GZIPInputStream(input);
				int count;
				byte data[] = new byte[BUFFERSIZE];
				while ((count = gin.read(data, 0, BUFFERSIZE)) != -1) {
					tmpOutput.write(data, 0, count);
				}
				gin.close();

				ret = tmpOutput.toByteArray();
			}

		}

		responseData = ret;
	}

	public String getParamString() throws Exception {
		final StringBuffer buffer = new StringBuffer();
		final HashMap<String, Object> data = getData();
		if (null == data)
			return "";
		String and = "";

		int keysize = 0;
		String k="";
		String v="";
		Log.d(LOG_TAG, "-------------- param start ---------------------------");
		for (Entry<String, Object> entry : data.entrySet()) {
			k = entry.getKey();
			Object val = entry.getValue();
			v = val == null ? "" : String.valueOf(val);
			buffer.append(and);
			buffer.append(java.net.URLEncoder.encode(k, requestCharset));
			buffer.append("=");
			buffer.append(java.net.URLEncoder.encode(v, requestCharset));
			and = "&";
			Log.d(LOG_TAG, k + " : " + v);
			keysize++;
		}
		Log.d(LOG_TAG, "-------------- param end ---------------------------");

		//only one key don't need key＝value －－>=value
//		if(keysize==1)
//		{
//			return "="+v;
//		}
//		else
			return buffer.toString();
	}

	
	public void setFile(String name, byte[] data) {
		setFile(name, data, "file");
	}
	
	@Override
	public void setFile(String name, byte[] data, String fileName) {
		
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setIfModifiedSince(long milliseconds) {
		mIfModifiedSince = milliseconds;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setIsNeedResponse(boolean needResponse) {
		mIsNeedResponse = needResponse;
	}
}
