package com.xingy.lib;

import android.content.Context;
import android.text.TextUtils;

import com.xingy.util.Log;
import com.xingy.util.MyApplication;
import com.xingy.util.ToolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class AppStorage {
	// Scope
	public static final String SCOPE_DEFAULT   = "default";
	// Key
	public static final String KEY_UID        = "uid";
	public static final String KEY_VERSION     = "version";
	public static final String KEY_DEVICEID    = "devid";
	public static final String KEY_PLATFORM    = "platform";
	public static final String KEY_CHANNEL     = "channel";
	public static final String KEY_SOURCE      = "src";
	public static final String KEY_WAP_BACK    = "wapBack"; // Back to APP home.
	public static final String KEY_MINE_RELOAD = "reload_mine";
	public static final String KEY_CART_RELOAD = "reload_cart";
	
	
	public AppStorage(Context aContext)
	{
		mReference = new WeakReference<Context>(aContext);
		
		//load data from cache
		load();
	}
	
	/**
	 * @param strKey
	 */
	public static void delData(String strKey) {
		AppStorage.delData(SCOPE_DEFAULT, strKey);
	}
	
	public static void delData(String strScope, String strKey) {
		AppStorage pSelf = MyApplication.mStorage;
		if( null != pSelf )
			pSelf.remove(strScope, strKey);
	}
	
	/*
	 * set a pair of value
	 * @param String strScope: the scope of the value
	 * @param String strKey: the key
	 * @param String strVal: the value
	 * @param boolean bPermanet: whether store the value or not
	 * 
	 */
	public static void setData(String strScope, String strKey, String strVal, boolean bPermanent){
		AppStorage pSelf = MyApplication.mStorage;
		if( null != pSelf )
			pSelf.set(strScope, strKey, strVal, bPermanent);
	}
	
	public static void setData(String strKey, String strVal, boolean bPermanent){
		AppStorage.setData(null, strKey, strVal, bPermanent);
	}
	
	private void remove(String strScope, String strKey) {
		if( (TextUtils.isEmpty(strKey)) || (null == mTable) )
			return ;
		
		if( TextUtils.isEmpty(strScope) ) {
			strScope = AppStorage.SCOPE_DEFAULT;
		}
		
		ScopeInfo pScopeInfo = mTable.get(strScope);
		if( null == pScopeInfo )
			return ;
		pScopeInfo.delete(strKey);
	}
	
	private void set(String strScope, String strKey, String strVal, boolean bPermanent) {
		//if strScope is null, give it a default value
		if(null == strScope ){
			strScope = AppStorage.SCOPE_DEFAULT;
		}
		
		if(null == mTable){
			mTable = new Hashtable<String, ScopeInfo>();
		}
		
		ScopeInfo pScopeInfo = mTable.get(strScope);
		if(null == pScopeInfo){
			pScopeInfo = new ScopeInfo();
		}
		
		pScopeInfo.put(strKey, strVal, bPermanent);
		mTable.put(strScope, pScopeInfo);
	}
	
	/*
	 * get a pair of value
	 * @param String strScope: the scope of the value
	 * @param String strKey: the key
	 * 
	 */
	public static String getData(String strScope, final String strKey){
		AppStorage pSelf = MyApplication.mStorage;
		if( null != pSelf ) {
			return pSelf.get(strScope, strKey);
		}
		return null;
	}
	
	public static String getData(final String strKey){
		return getData(null, strKey);
	}
	
	private String get(String strScope, final String strKey) {
		//Check the scope first.
		if(null == strScope){
			strScope = AppStorage.SCOPE_DEFAULT;
		}
		
		ScopeInfo pInfo = (null != mTable ? mTable.get(strScope) : null);
		String strValue = "";
		if( strScope.equals(AppStorage.SCOPE_DEFAULT) ) {
			// Check the scope first.
			strValue = (null != pInfo && pInfo.containsKey(strKey)) ? pInfo.get(strKey) : getDefault(strKey);
		} else {
			strValue = (null != pInfo ? pInfo.get(strKey) : "");
		}
				
		return strValue;
	}
	
	private String getDefault(String strKey) {
		if( TextUtils.isEmpty(strKey) )
			return "";
		
		String strValue = "";
		boolean bSaveIt = false;
		// Load default information.
		if(strKey.equals(AppStorage.KEY_UID)){
			strValue = String.valueOf(ILogin.getLoginUid());
		}else if(strKey.equals(AppStorage.KEY_DEVICEID)){
			strValue = ToolUtil.getDeviceUid(mReference.get());
			bSaveIt = true;
		}else if(strKey.equals(AppStorage.KEY_VERSION)){
			strValue = String.valueOf(MyApplication.mVersionCode);
			bSaveIt = true;
		}else if(strKey.equals(AppStorage.KEY_PLATFORM)){
			strValue = "Android";
			bSaveIt = true;
		}else if(strKey.equals(AppStorage.KEY_CHANNEL)){
			strValue = ToolUtil.getChannel();
			bSaveIt = true;
		}
		
		// Set a default value.
		if( (bSaveIt && !TextUtils.isEmpty(strKey)) && (!TextUtils.isEmpty(strValue)) ) {
			AppStorage.setData(AppStorage.SCOPE_DEFAULT, strKey, strValue, false);
		}
		
		return strValue;
	}
	
	final class ValueInfo {
		ValueInfo(String strValue, boolean bPermanent) {
			mValue = strValue;
			mPermanent = bPermanent;
		}
		String mValue = "";
		boolean mPermanent = false;
	}

	/*
	 * a class of Scrope
	 */
	final class ScopeInfo {
		boolean put(String strKey, String strVal, boolean bPermanent) {
			if( TextUtils.isEmpty(strKey) )
				return false;
			
			if( null == mMap )
				mMap = new HashMap<String, ValueInfo>();
			
			// Set data.
			ValueInfo entity = new ValueInfo(strVal, bPermanent);
			mMap.put(strKey, entity);
			
			return true;
		}
		
		void delete(String strKey) {
			if( null == mMap )
				return ;
			mMap.remove(strKey);
		}
		
		boolean containsKey(String strKey) {
			return (null != mMap ? mMap.containsKey(strKey) : false);
		}
		
		String get(String strKey) {
			if( (TextUtils.isEmpty(strKey)) || (null == mMap) )
				return null;
			
			ValueInfo entity = mMap.get(strKey);
			return (null != entity ? entity.mValue : null);
		}
		
		JSONObject toJson() {
			if (null == mMap)
				return null;
			
			// Remove non-permanent value info.
			HashMap<String, String> aStrMap = new HashMap<String, String>();
			Iterator<String> it = mMap.keySet().iterator();
			while( it.hasNext() ) {
				String strKey = it.next();
				ValueInfo entity = mMap.get(strKey);
				if( (null != entity) && (entity.mPermanent) )
					aStrMap.put(strKey, entity.mValue);
			}
			
			// Create the json entity.
			JSONObject json = new JSONObject(aStrMap);
			
			// Clean up.
			aStrMap.clear();
			aStrMap = null;
			
			return json;
		}
		
		/*
		 * convert JSONObject to ScopeInfo
		 */
		@SuppressWarnings("unchecked")
		boolean fromJson(JSONObject aObject) {
			if( null == aObject )
				return false;
			
			if(null == mMap) {
				mMap = new HashMap<String, ValueInfo>();
			}
			
			Iterator<String> keyIter = aObject.keys();
			String strKey, strValue;
			while(keyIter.hasNext()){
				try {
					strKey = keyIter.next();
					strValue = (String) aObject.get(strKey);
					ValueInfo entity = new ValueInfo(strValue, true);
					mMap.put(strKey, entity);
				} catch (JSONException e) {
					Log.e(LOG_TAG, e);
				}
			}
			return true;
		}
		
		void cleanup() {
			if( null != mMap )
				mMap.clear();
		}
		
		HashMap<String, ValueInfo> mMap;
	}
	
	/*
	 * save to cache file
	 */
	public void save() {
		Context pContext = mReference.get();
		if( null == pContext )
			return ;
		
		FileOutputStream pOutputStream = null;
		try {
			JSONObject pJSONObject = toJson();
			if( null != pJSONObject ) {
				pOutputStream = pContext.openFileOutput(CACHE_FILE, Context.MODE_PRIVATE);
				pOutputStream.write(pJSONObject.toString().getBytes());
			}
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}catch(IOException  ex){
			ex.printStackTrace();
		}finally{
			if(null != pOutputStream){
				try {
					pOutputStream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * load data from cache file
	 */
	private void load() {
		Context context = mReference.get();
		if(null == context){
			return ;
		}
		
		String strContent = null;
		FileInputStream pInputStream = null;
		
		try{
			String [] fs = context.fileList();
			if(null==fs || fs.length <=0)
				return;
			
			boolean hint = false;
			for(String fileitem : fs)
			{
				if(fileitem.equals(CACHE_FILE))
				{
					hint = true;
					break;
				}
			}
			
			if(!hint)
				return;
			
			pInputStream = context.openFileInput(CACHE_FILE);
			byte aBytes[] = new byte[pInputStream.available()];
			
			pInputStream.read(aBytes);
			strContent = new String(aBytes);
			
			JSONObject pJSONObject = new JSONObject(strContent);
			fromJson(pJSONObject);
			
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
			strContent = null;
		}catch(IOException  ex){
			ex.printStackTrace();
			strContent = null;
		}catch (JSONException ex) {
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
			ex.printStackTrace();
		}finally {
			if( null != pInputStream){
				try {
					pInputStream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
				pInputStream = null;
			}
		}
	}
	
	/*
	 * convert HashTable to JSONObject
	 */
	JSONObject toJson() {
		if (null == mTable)
			return null;
		
		Iterator<String> mTableIter = mTable.keySet().iterator();
		
		JSONObject jsonTable = new JSONObject();
		JSONObject jsonMap = new JSONObject();
		ScopeInfo mScopeInfo;
		String mTablekey;
		try {
		    while(mTableIter.hasNext()) {  
		    	mTablekey = mTableIter.next();  
		    	mScopeInfo = mTable.get(mTablekey);
		    	if( null != mScopeInfo ) {
		    		jsonMap = mScopeInfo.toJson();
		    		if( null != jsonMap ) {
		    			jsonTable.put(mTablekey, jsonMap);
		    		}
		    	}
		    } 
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	
		return jsonTable;
	}
	
	/*
	 * convert JSONObject to Hashtable
	 */
	@SuppressWarnings("unchecked")
	boolean fromJson(JSONObject pJSONObject) {
		if( null == pJSONObject )
			return false;
		
		if(null == mTable) {
			mTable = new Hashtable<String, ScopeInfo>();
		}
		
		Iterator<String> keyIter = pJSONObject.keys();
		String strScope;
		ScopeInfo pScopeInfo;
		try {
			while(keyIter.hasNext()){
				strScope = keyIter.next();
				JSONObject pObject = (JSONObject) pJSONObject.get(strScope);
				
				if( null != pObject ) {
					pScopeInfo = new ScopeInfo();
					pScopeInfo.fromJson(pObject);
					mTable.put(strScope, pScopeInfo);
				}
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
		return true;
	}
	
	private WeakReference<Context>       mReference;
	private Hashtable<String, ScopeInfo> mTable;
	private static String LOG_TAG = AppStorage.class.getName();
	private static final String CACHE_FILE = "icson_local_storage.cache";
}
