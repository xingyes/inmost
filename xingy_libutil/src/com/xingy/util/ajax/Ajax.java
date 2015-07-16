package com.xingy.util.ajax;

import android.content.Context;
import android.text.TextUtils;

import com.xingy.lib.ILogin;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.Log;
import com.xingy.util.MyApplication;
import com.xingy.util.ToolUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 主�??�?�?�?�?�?ajax????????��??ajax??��???????��??�?????????????�?以�??�?�??????��??设置???�???��????��??�????????????��??设置?????��???????��?????�?�?类类似�??�?�?�???????javaBean
 * 1.该类???�?�?主�???????��?????send()??��??�????ajax??????请�?????�?�???��????��??�?该�?��??�?�????AjaxTask类�??execute()??��??�? �????�????ajax??�个?????��?��???????��??�?�????
 */
public class Ajax {

	private final static AtomicInteger mCount = new AtomicInteger(1);

	public final static int GET = 1;

	public final static int POST = 2;

	public final static int STREAM = 3;

	private static final String LOG_TAG =  Ajax.class.getName();

	private HttpRequest mHttpRequest;

	@SuppressWarnings("rawtypes")
	private Parser mParser;

	private String mUrl;

	private AjaxTask mTask;

	private OnBeforeListener mOnBeforeListener;

	@SuppressWarnings("rawtypes")
	private OnSuccessListener mOnSuccessListener;

	private OnErrorListener mOnErrorListener;

	private OnCancelListener mOnCancelListener;

	private OnProgressListener mOnProgressListener;

	private OnFinishListener mOnFinishListener;

	private Response mResponse;
	
	private int ajaxMethod;
	
	private Cookie mRequesetCookie;

	public Ajax(int ajaxMethod) {
		this.ajaxMethod = ajaxMethod;
		final Context context = MyApplication.app;
		mHttpRequest = ajaxMethod == POST ? new HttpPost(context) : (ajaxMethod == STREAM ? new HttpStream(context) : new HttpGet(context));
		mResponse = new Response();
		int id = mCount.getAndIncrement();
		setId(id);
	}

	public void setTimeout(int second) {
		if (ajaxMethod == GET) {
			mHttpRequest.setGetDataTimeout(second * 1000);
		} else {
			mHttpRequest.setPostDataTimeout(second * 1000);
		}
	}
	
	public void setTag(Object tag){
		mResponse.setTag(tag);
	}

	@SuppressWarnings("rawtypes")
	public void setParser(Parser parser) {
		mParser = parser;
	}

	@SuppressWarnings("rawtypes")
	public Parser getParser() {
		return mParser;
	}

	public void setOnBeforeListener(OnBeforeListener listener) {
		mOnBeforeListener = listener;
	}

	public void setOnSuccessListener(OnSuccessListener<?> listener) {
		mOnSuccessListener = listener;
	}

	public void setOnSuccessListener(final OnSuccessListener<JSONObject> listener, boolean loginCheck) {
		if (loginCheck) {
			mOnSuccessListener = new OnSuccessListener<JSONObject>() {
				@Override
				public void onSuccess(JSONObject v, Response response) {
					if (v.optInt("errno", -1) == 500) {
						ILogin.clearAccount();
						UiUtils.makeToast(MyApplication.app, "??�已?????��?��??�?请�?��????????�?.");
						performOnError();
						return;
					}

					listener.onSuccess(v, response);
				}
			};
		} else {
			mOnSuccessListener = listener;
		}
	}

	public void setOnCancelListener(OnCancelListener listener) {
		mOnCancelListener = listener;
	}

	public void setOnErrorListener(OnErrorListener listener) {
		mOnErrorListener = listener;
	}

	public void setOnFinishListener(OnFinishListener listener) {
		mOnFinishListener = listener;
	}

	public void setOnProgressListener(OnProgressListener listener) {
		mOnProgressListener = listener;
	}

	public void setAjaxListener(AjaxListener<?> listener) {
		if (listener == null)
			return;
		setOnBeforeListener(listener);
		setOnSuccessListener(listener);
		setOnErrorListener(listener);
		setOnCancelListener(listener);
		setOnFinishListener(listener);
	}

	public void setCookie(Cookie cookie) {
		mRequesetCookie = cookie;
		mHttpRequest.setCookie(mRequesetCookie);
	}

	public Cookie getRequesetCookie(){
		return mRequesetCookie;
	}
	public void setCookie(String name, String value) {
		if (mRequesetCookie == null){
			mRequesetCookie = new Cookie();
		}
		mRequesetCookie.set(name, value);
		mHttpRequest.setCookie(mRequesetCookie);
	}

	public OnBeforeListener getOnBeforeListener() {
		return mOnBeforeListener;
	}

	public OnSuccessListener<?> getOnSuccessListener() {
		return mOnSuccessListener;
	}

	public OnCancelListener getOnCancelListener() {
		return mOnCancelListener;
	}

	public OnErrorListener getOnErrorListener() {
		return mOnErrorListener;
	}

	public OnFinishListener getOnFinishListener() {
		return mOnFinishListener;
	}

	public OnProgressListener getOnProgressListener() {
		return mOnProgressListener;
	}

	public void setRequestCharset(String charset) {
		mHttpRequest.setRequestCharset(charset);
	}

	public void setResponseDefaultCharset(String charset) {
		mHttpRequest.setResponseDefaultCharset(charset);
	}

	public void setId(int id) {
		mResponse.setId(id);
		mHttpRequest.setId(id);
	}

	public int getId() {
		return mResponse.getId();
	}

	public void setUrl(String url)
	{
		this.setUrl(url, false);
	}
	
	public void setUrl(String strUrl, boolean bPlatform)
	{
		/*if( bPlatform )
		{
			// Append information for platform.
			final boolean bWithParams = strUrl.contains("?");
			strUrl += ((bWithParams ? "&" : "?") + ("appSource=android&appVersion=" + MyApplication.mVersionCode));
		}*/
		mUrl = strUrl;
		mResponse.setUrl(strUrl);
		mHttpRequest.setUrl(strUrl);
	}

	public String getUrl() {
		return mUrl;
	}
	
	public String getHost()
	{
		if( TextUtils.isEmpty(mUrl) )
			return "";
		
		final String strTag = "://";
		final int nStart = mUrl.indexOf(strTag);
		if( nStart > 0 ){
			final int nOffset = nStart + strTag.length();
			final int nEnd = mUrl.indexOf("/", nOffset);
			return mUrl.substring(nOffset, nEnd);
		}
		
		return "";
	}
	
	public void updateHost(String strOrigin, String strTarget)
	{
		if( (!TextUtils.isEmpty(strOrigin)) && (!TextUtils.isEmpty(strTarget)) )
		{
			mUrl = mUrl.replace(strOrigin, strTarget);
			mResponse.setUrl(mUrl);
			mHttpRequest.setUrl(mUrl);
		}
	}

	public void setRequestHeader(String key, String value) {
		mHttpRequest.setRequestHeader(key, value);
	}

	public void setData(HashMap<String, Object> data) {
		mHttpRequest.setData(data);
	}

	public void setIfModifiedSince(long milliseconds) {
		mHttpRequest.setIfModifiedSince(milliseconds);
	}

	public void setData(String key, Object value) {
		mHttpRequest.setData(key, String.valueOf(value));
	}

	public void setFile(String key, byte[] content) {
		mHttpRequest.setFile(key, content);
	}
	
	public void setFile(String key, byte[] content, String fileName) {
		mHttpRequest.setFile(key, content, fileName);
	}

	public void send() {
		mTask = new AjaxTask(this);
		mTask.execute();
	}

	public void cancel() {
		mTask.cancel();
	}

	public void performOnBefore() {
		if (mOnBeforeListener != null) {
			mOnBeforeListener.onBefore(mResponse);
		}
	}

	public void performOnError() {
		if (mOnErrorListener != null) {
			mOnErrorListener.onError(this, mResponse);
		}
	}

	@SuppressWarnings("unchecked")
	public void performOnSuccess(Object data) {
		if (mOnSuccessListener != null) {
			mOnSuccessListener.onSuccess(data, mResponse);
		}
	}

	public void performOnCancel() {
		if (mOnCancelListener != null) {
			mOnCancelListener.onCancel(mResponse);
		}
	}

	public void performOnProgress(int downLoaded, int totalSize) {
		if (mOnProgressListener != null) {
			mOnProgressListener.onProgress(mResponse, downLoaded, totalSize);
		}
	}

	public void performOnFinish() {
		if (mOnFinishListener != null) {
			mOnFinishListener.onFinish(mResponse);
		}
	}

	HttpRequest getHttpRequest() {
		return mHttpRequest;
	}

	public void abort() {
		mOnBeforeListener = null;
		mOnSuccessListener = null;
		mOnErrorListener = null;
		mOnCancelListener = null;
		mOnFinishListener = null;
		if (null != mTask) {
			mTask.destory();
			mTask = null;
		}
		mResponse = null;
	}

	/**
	 * 主�??�????�?�?response�?�??????��????��??设置�?以�??�????OnSuccessListener?????��??onSuccess??��?????�????
	 * @param content ???�?http请�??�???��??�?�?�?解�???????��??
	 */
	void run(Object content) {
		if (!mTask.isError()) {
			try {
				mResponse.setCookie(mHttpRequest.getCookie());
				mResponse.setCharset(mHttpRequest.getCharset());
				mResponse.setResponseHeader(mHttpRequest.getResponseHeader());
				mResponse.setHttpStatus(mHttpRequest.getHttpStatus());
				performOnSuccess(content);
			} catch (Exception ex) {
				Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
				performOnError();
			}
		} else {
			performOnError();
		}

		performOnFinish();
	}

}
