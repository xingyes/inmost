package com.xingy.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.xingy.R;
import com.xingy.lib.ILogin;
import com.xingy.lib.ui.UiUtils;
import com.xingy.preference.Preference;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.JSONParser;
import com.xingy.util.ajax.OnErrorListener;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Parser;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public final class ServiceConfig implements OnSuccessListener<JSONObject> {
	
	public static String getUrl(String strKey) {
		return ServiceConfig.getUrl(strKey, null);
	}
	
	/**
	 * ???锟�?锟�???锟斤拷??key(URL锟�?锟�?涓插父???锟�?锟�???锟斤拷?锟斤拷?????url锟�?骞讹拷??锟�??????????淇★拷??
	 * @param strKey  锟�???锟斤拷??key(URL)甯革拷??
	 * @param aInfo	  ?????????url淇★拷??
	 * @return	锟�??????????锟�????url
	 */
	public static String getUrl(String strKey, Object aInfo) {
		JSONObject pObject = (null != mSelf ? mSelf.getObject(strKey) : null);
		
		String strUrl = null;
		if( null != pObject ) {
			strUrl = pObject.optString(TAG_URL);
			
			if( null != aInfo ) {
				strUrl = strUrl + aInfo.toString();
			}
		}
		
		return strUrl;
	}
	
	public static Ajax getAjax(String strKey) {
		return ServiceConfig.getAjax(strKey, null, null, null);
	}
	
	public static Ajax getAjax(String strKey, Object aInfo) {
		return ServiceConfig.getAjax(strKey, aInfo, null, null);
	}
	
	public static Ajax getAjax(String strKey, Object aInfo, Parser<?, ?> pParser) {
		return ServiceConfig.getAjax(strKey, aInfo, pParser, null);
	}
	
	/**
	 * 锟�???锟斤拷?锟斤拷?????ajax瀵硅薄锟�?骞讹拷?锟界疆锟�?ajax???锟�?璁わ拷????锟斤拷??url锟�?method锟�?parse锟�?cookie,Charset锟�?锟�?
	 * @param strKey ???锟�?锟�?key??锟斤拷????锟斤拷??锟�?锟�?Url???method???jason瀵硅薄
	 * @param aInfo	 ?????????url淇★拷??
	 * @param pParser  ??锟斤拷?锟借В???锟�???????jason??锟斤拷????????Parse
	 * @param strHack  璇凤拷??????????锟斤拷?锟斤拷?????锟�?beta锟�?test,w3sg
	 * @return	锟�????璇凤拷?????ajax瀵硅薄
	 */
	public static Ajax getAjax(String strKey, Object aInfo, Parser<?, ?> pParser, String strHack) {
		JSONObject pObject = null != mSelf ? mSelf.getObject(strKey) : null;
		if( null == pObject )
			return null;
		
		// Check whether configuration enabled.
		if( !mSelf.mEnabled ) {
			mSelf.showMessage(mSelf.mMessage);
			return null;
		}
		
		String strUrl = pObject.optString(TAG_URL);
		String strMethod = pObject.optString(TAG_METHOD);
		if( TextUtils.isEmpty(strUrl) || TextUtils.isEmpty(strMethod) )
			return null;
		
		// Check whether current url is disable or not.
		final boolean bEnable = pObject.optBoolean(TAG_ENABLE, true);
		if( !bEnable ) {
			// Check whether there is message.
			String strMessage = pObject.optString(TAG_MESSAGE);
			mSelf.showMessage(strMessage);
			
			return null;
		}
		
		// Check whether hacked or not.
		String strPrev = mSelf.mHack;
		if( !TextUtils.isEmpty(strHack) ) {
			mSelf.mHack = strHack;
		}
		
		if( !TextUtils.isEmpty(mSelf.mHack) ) {
			// Get prefix, such as http://, https://, wap://
			strUrl = strUrl.replace("https://", "http://");
			final String strHttp = "http://";
			final int nStart = strHttp.length(); // length of http://
			final int nOffset = strUrl.indexOf("/", nStart);
			String strHost = strUrl.substring(0, nOffset).toLowerCase(Locale.getDefault());
//			if( !strHost.equals(ST_ICSON_COM) && !strHost.equals(PAY_ICSON_COM) ) {
//				// 1. Get the module name.
//				final int nPos = strUrl.indexOf(".", nStart);
//				String strModule = strUrl.substring(nStart, nPos);
//				
//				// 2. Replace xxx.51buy.com to hack.m.51buy.com/xxx
//				String strInfo = strUrl.substring(nOffset);
//				strHost = strHost.replaceFirst(strModule, mSelf.mHack + ".m");
//				
//				strUrl = strHost + "/" + strModule + strInfo;
//			}
		}
		
		if( null != aInfo ) {
			strUrl = strUrl + aInfo.toString();
		}
		
		// Restore the previous value.
		mSelf.mHack = strPrev;
		
		// Make sure current application version code is available.
		MyApplication.getVersionCode(mSelf.mContext);
		
		
		Ajax pResult = null;
		if( null == pParser )
			pParser = new JSONParser();
		switch( ServiceConfig.getMethod(strMethod) )
		{
		case Ajax.GET:
			pResult = AjaxUtil.get(strUrl);
			break;
			
		case Ajax.POST:
			pResult = AjaxUtil.post(strUrl);
			break;
			
		case Ajax.STREAM:
			pResult = AjaxUtil.stream(strUrl);
			break;
			
		default:
			pResult = AjaxUtil.get(strUrl);
			break;
		}
		
		// Get exTag.
		//String exTag = ServiceConfig.getToken();
		//if( !TextUtils.isEmpty(exTag) ) {
		//	pResult.setData("exAppTag", exTag);
		//}
		pResult.setParser(pParser);
		
		return pResult;
	}
	
	/**
	 * get error message specified by errno
	 * @param strKey
	 * @param nErrNo
	 * @return
	 */
	public static String getErrMsg(String strKey, int nErrNo) {
		JSONObject pObject = null != mSelf ? mSelf.getObject(strKey) : null;
		if( null == pObject )
			return "";
		
		// Get error array configuration.
		JSONArray pArray = pObject.optJSONArray(TAG_MSG_ARRAY);
		final int nLength = (null != pArray ? pArray.length() : 0);
		try {
			for( int nIdx = 0; nIdx < nLength; nIdx++ ) {
				JSONObject entity;
				entity = pArray.getJSONObject(nIdx);
				final int errno = entity.optInt(TAG_ERR_NO);
				if( errno == nErrNo ) {
					return entity.optString(TAG_MESSAGE);
				}
			}
		} catch( JSONException aException ) {
			aException.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * Get Display information.
	 * @return
	 */
	public static String getInfo(){
		if( (null == mSelf) || (TextUtils.isEmpty(mSelf.mAlias)) )
			return "";
		
		return "(ALIAS: " + mSelf.mAlias + ", NUM: " + mSelf.mVersion + ")";
	}
	
	private void showMessage(String strMessage) {
		if( (!TextUtils.isEmpty(strMessage)) && (null != mContext) ) {
			// Show message for message disabled.
			UiUtils.showDialog(mContext, "", strMessage, R.string.btn_ok);
		}
	}
	
	/**
	 * ???锟�?key锟�?mConfig锟�?锟�???锟斤拷??锟�????url???method???jason瀵硅薄
	 * @param strKey 锟�?锟�?涓插父???key
	 * @return 锟�?锟�????url???method???jason瀵硅薄
	 */
	private JSONObject getObject(String strKey) {
		if( (null == mConfig) || (TextUtils.isEmpty(strKey)) )
			return null;
		
		return mConfig.optJSONObject(strKey);
	}
	
	/**
	 * check configuration version.
	 */
	public static void checkConfig() {
		// Check latst configuration from server.
		if( null == mSelf || mSelf.mDebug )
			return ;
		
		mSelf.updateCheckAjax = AjaxUtil.post(ServiceConfig.GET_INTERFACE_URL + mSelf.mVersion);
		mSelf.updateCheckAjax.setData("deviceid", ToolUtil.getDeviceUid(mSelf.mContext));
		mSelf.updateCheckAjax.setData("userid", ILogin.getLoginUid());
		mSelf.updateCheckAjax.setParser(new JSONParser());
		mSelf.updateCheckAjax.setOnSuccessListener(mSelf);
		mSelf.updateCheckAjax.setOnErrorListener(new OnErrorListener(){

			@Override
			public void onError(Ajax ajax, Response response) {
				cleanUpdateAjax();
				
			}});
		mSelf.updateCheckAjax.send();
	}
	
	@Override
	public void onSuccess(JSONObject object, Response response) {
		if( null != object ) {
			try {
				// Parse the err number.
				final int errno = object.getInt("errno");
				if( errno == 0 ) {
					// Parse Info
					parseInfo(object);
						
					// Save configuration.
					saveConfig();
				}
			} catch (JSONException aException) {
				aException.printStackTrace();
			}finally
			{
				cleanUpdateAjax();
			}
			
		}
	}
	
	private static void cleanUpdateAjax()
	{
		if(null!=mSelf.updateCheckAjax)
		{
			mSelf.updateCheckAjax.abort();
			mSelf.updateCheckAjax = null;
		}
	}
	
	private void parseInfo(JSONObject aObject) {
		if( null != aObject ) {
			mVersion = aObject.optInt(TAG_VERSION);
			mEnabled = aObject.optBoolean(TAG_ENABLE, true);
			mMessage = aObject.optString(TAG_MESSAGE);
			mConfig = aObject.optJSONObject(TAG_DATA);
			mAlias = aObject.optString(TAG_ALIAS);
			mAutoRelogin = aObject.optInt(TAG_AUTO_RELOGIN, 0);
		}
	}
	
	/**
	 * Set value to hack default host.
	 * @param strHack
	 */
	private void setHack(String strHack) {
		if( TextUtils.isEmpty(strHack) ) {
			mHack = null;
			return ;
		}

		final int nLength = mHacks.length;
		for( int nIdx = 0; nIdx < nLength; nIdx++ ) {
			if( strHack.equalsIgnoreCase(mHacks[nIdx]) ) {
				mHack = strHack;
				return ;
			}
		}
	}
	
	/**
	 * Default constructor of ServerAdapter
	 */
	private ServiceConfig(Context aContext) {
		mContext = aContext;
		loadConfig(mDebug);
	}
	
	/**
	 * Set context for instance.
	 * @param aContext
	 */
	public static void setContext(Context aContext) {
		if( null == mSelf )
			mSelf = new ServiceConfig(aContext);
		else
			mSelf.mContext = aContext;
	}
	
	/**
	 * get request method.
	 * @param strMethod
	 * @return
	 */
	private static int getMethod(String strMethod) {
		int nMethod = 0;
		if( strMethod.equalsIgnoreCase("get") ) {
			nMethod = Ajax.GET;
		} else if( strMethod.equalsIgnoreCase("post") ) {
			nMethod = Ajax.POST;
		} else if( strMethod.equalsIgnoreCase("stream") ) {
			nMethod = Ajax.STREAM;
		}
		
		return nMethod;
	}
	
	/**
	 * Load configuration from storage, if not exists, set default.
	 */
	private void loadConfig(boolean bTestOnly)
	{
		if( null == mContext )
			return ;
		
		// Check the version of application.
		Preference pPreference = Preference.getInstance();
		final int nPrevious = pPreference.getProjVersion();
		
		String strContent = null;
		// Check the timetag.
		File pFile = mContext.getFileStreamPath(CACHE_FILE);
		if( (null != pFile) && (!bTestOnly) && (MyApplication.mVersionCode == nPrevious) )
		{
			String strTag = "" + pFile.lastModified();
			String strPrev = pPreference.getConfigTag();
			if( strTag.equals(strPrev) ) {
				// Load the server configuration from local storage.
				FileInputStream pInputStream = null;
				try {
					pInputStream = mContext.openFileInput(CACHE_FILE);
					
					byte aBytes[] = new byte[pInputStream.available()];
					pInputStream.read(aBytes);
					
					strContent = new String(aBytes);
					
					// Parse the json object.
					JSONObject pRoot = new JSONObject(strContent);
					parseInfo(pRoot);
				} catch (FileNotFoundException aException) {
					aException.printStackTrace();
					strContent = null;
				} catch (IOException aException) {
					aException.printStackTrace();
					strContent = null;
				} catch (JSONException aException) {
					aException.printStackTrace();
				} finally {
					if( null != pInputStream ) {
						try {
							pInputStream.close();
						} catch (IOException aException) {
							aException.printStackTrace();
						}
						pInputStream = null;
					}
				}
			}
		}
		
		// Check need to build up default values.
		if( TextUtils.isEmpty(strContent) ) {
			// Build default configuration.
			loadDefault(bTestOnly);
		}
		
		// Set hack.
		this.setHack(null);
	}
	
	private boolean loadRawInfo(boolean bDisable) {
		if( null == mContext || bDisable )
			return false;
		
		InputStream pInputStream  = null;
		boolean bSuccess = true;
		try {
			Resources pResources = mContext.getResources();
			pInputStream = pResources.openRawResource(R.raw.config);
			byte[] aBytes = new byte[pInputStream.available()];
			pInputStream.read(aBytes);
			
			// Compose JSON object info
			String strContect = new String(aBytes);
			JSONObject pObject = new JSONObject(strContect);
			
			// Parse information.
			parseInfo(pObject);
			
			// Save information.
			saveConfig();
		} catch (IOException e) {
			e.printStackTrace();
			bSuccess = false;
		} catch (JSONException aException) {
			aException.printStackTrace();
			bSuccess = false;
		}finally
		{
			if(null!= pInputStream)
			{
				try {
					pInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					bSuccess = false;
				}
				pInputStream = null;
			}
		}
		
		return bSuccess;
	}
	
	/**
	 * 锟�?锟�???????Url??锟斤拷?????璇凤拷????锟斤拷??锟�?锟�???锟斤拷??锟�?jason瀵硅薄锟�?锟�???锟斤拷??锟�?锟�?锟�?锟�?锟�?涓插父???锟�?锟�?key锟�?锟�?锟�????jason瀵硅薄锟�?锟�?alue锟�?锟�?锟�???锟斤拷??锟�???锟斤拷??jason瀵硅薄mConfig锟�?
	 * @param bDisable
	 */
	private void loadDefault(boolean bDisable) {
		mVersion = 0;
		
		// 1. Try to load information from raw file.
		if( !loadRawInfo(bDisable) ) {
			if( null == mConfig )
				mConfig = new JSONObject();
			
			// Key and url list.
			try {
				//mConfig.put(Config.URL_CHECK_VERSION, getChild("https://msp.alipay.com/x.htm", METHOD_POST));
				
				mConfig.put("test", getChild("http://61.174.8.117/user/verification_code", METHOD_POST));
				
				
			} catch( JSONException aException ) {
				aException.printStackTrace();
			}
		}
	}
	
	private JSONObject getChild(String strUrl, String strMethod) throws JSONException {
		JSONObject pChild = new JSONObject();
		pChild.put(TAG_URL, strUrl);
		pChild.put(TAG_METHOD, strMethod);
		
		return pChild;
	}
	
	/**
	 * Save server configuration to local storage.
	 */
	private boolean saveConfig() {
		if( (0 >= mVersion) || (null == mConfig) || (null == mContext) )
			return false;
		
		boolean bSuccess = false;
		FileOutputStream pOutputStream = null;
		try {
			// Compose root json object
			JSONObject pRoot = new JSONObject();
			pRoot.put(TAG_VERSION, mVersion);
			pRoot.put(TAG_ENABLE, mEnabled);
			if( !TextUtils.isEmpty(mMessage) )
				pRoot.put(TAG_MESSAGE, mMessage);
			pRoot.put(TAG_DATA, mConfig);
			pRoot.put(TAG_AUTO_RELOGIN, mAutoRelogin);
			
			// Save the the output to local storage.
			pOutputStream = mContext.openFileOutput(CACHE_FILE, Context.MODE_PRIVATE);
			pOutputStream.write(pRoot.toString().getBytes());
			bSuccess = true;
		} catch( JSONException aException ) {
			aException.printStackTrace();
		} catch (FileNotFoundException aException) {
			aException.printStackTrace();
		} catch (IOException aException) {
			aException.printStackTrace();
		} finally {
			if( null != pOutputStream ) {
				try {
					pOutputStream.close();
				} catch (IOException aException) {
					aException.printStackTrace();
				}
				pOutputStream = null;
			}
		}
		
		// Save last time tag to preference.
		File pFile = mContext.getFileStreamPath(CACHE_FILE);
		if( null != pFile ) {
			Preference.getInstance().setConfigTag("" + pFile.lastModified());
		}
		
		return bSuccess;
	}
	
	/*
	G.util.token = {
			//缁?杩???ュ??涓?token
			addToken : function(url,type){
				//type???璇?璇锋???????瑰??,jq???璇?jquery锛?lk???璇?????????炬??,fr???璇?form琛ㄥ??,ow???寮???扮?????
				var token=this.getToken();
				//?????????http???https???璁?锛?褰?url涓???????璁?澶寸????跺??锛?搴?璇ユ????ュ?????椤甸?㈢?????璁?澶?
				if(url=="" || (url.indexOf("://")<0?location.href:url).indexOf("http")!=0){
					return url;
				}
				if(url.indexOf("#")!=-1){
					var f1=url.match(/\?.+\#/);
					 if(f1){
						var t=f1[0].split("#"),newPara=[t[0],"&g_tk=",token,"&g_ty=",type,"#",t[1]].join("");
						return url.replace(f1[0],newPara);
					 }else{
						var t=url.split("#");
						return [t[0],"?g_tk=",token,"&g_ty=",type,"#",t[1]].join("");
					 }
				}
				//???璁哄??浣???芥??g_ty甯?涓?锛???ㄦ?锋????″?ㄧ????ゆ??璇锋?????绫诲??
				return token==""?(url+(url.indexOf("?")!=-1?"&":"?")+"g_ty="+type):(url+(url.indexOf("?")!=-1?"&":"?")+"g_tk="+token+"&g_ty="+type);
			},
			//??峰??杞???㈠?????token
			getToken : function(){
				var skey=G.util.cookie.get("skey"),
					token=skey==null?"":this.time33(skey);
					return token;
			},
			//skey杞?token
			time33 : function(str){
				//???甯?time33绠?娉?
				for(var i = 0, len = str.length,hash = 5381; i < len; ++i){
				   hash += (hash << 5) + str.charAt(i).charCodeAt();
				};
				return hash & 0x7fffffff;
			}
		}
	
	private static String getToken() {
		String skey = ILogin.getLoginPskey();
		String token = TextUtils.isEmpty(skey) ? "" : "" + ServiceConfig.getTime33(skey);
		
		return token;
	}
	
	private static long getTime33(String skey) {
		long hash = 5381;
		final int length = TextUtils.isEmpty(skey) ? 0 : skey.length();
		for( int i = 0; i < length; i++ ) {
			hash += (hash << 5) + skey.codePointAt(i);
		}
		
		return hash & 0x7fffffff;
	}
	*/
	
	
	public static boolean isAutoRelogin()
	{
		return (mSelf.mAutoRelogin > 0 );
	}
	
	
	// Member instance.
	private static ServiceConfig mSelf = null;
	private Context    mContext;
	private int        mVersion; // Latest time tag.
	private boolean    mEnabled = true;  // Configuration is enabled or not.
	private int        mAutoRelogin = 0; //Only qq has this 
	private String     mMessage = ""; // Message.
	private String     mAlias = "";
	private JSONObject mConfig;
	private String     mHack = null;
	private static final String[] mHacks = {"w3sg", "test", "beta"};
	private final boolean mDebug = false;
	private Ajax   updateCheckAjax;
	
	// Constants definition in Server Adapter.
	private static final String METHOD_GET    = "get";
	private static final String METHOD_POST   = "post";
	private static final String METHOD_WAP    = "wap";
	private static final String METHOD_STREAM = "stream";
	private static final String TAG_DATA      = "data";
	private static final String TAG_VERSION   = "version";
	private static final String TAG_ALIAS     = "alias";
	private static final String TAG_URL       = "url";
	private static final String TAG_METHOD    = "method";
	private static final String TAG_ENABLE    = "enable";
	private static final String TAG_MESSAGE   = "message";
	private static final String TAG_MSG_ARRAY = "msg_arr";
	private static final String TAG_ERR_NO    = "errno";
	private static final String TAG_AUTO_RELOGIN    = "autoResume";
	private static final String CACHE_FILE    = "clubook_config.cache";
	
	// Default host configuration.

	public static final String BASE_HOST = "http://61.174.8.117/";
	private static final String GET_INTERFACE_URL = "http://mb.51buy.com/json.php?mod=main&act=getinterface&cfgver=";
	//private static final String GET_INTERFACE_URL = "http://mb.51buy.com/json.php?mod=main&act=getinterface_new&app=2&cfgver=";
}
