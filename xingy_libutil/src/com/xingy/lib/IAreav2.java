package com.xingy.lib;

import android.content.res.Resources;

import com.xingy.R;
import com.xingy.lib.model.ChangeDistrictModel;
import com.xingy.lib.model.ChangeProvinceModel;
import com.xingy.util.Log;
import com.xingy.util.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class IAreav2 {
	private static final String LOG_TAG = IAreav2.class.getName();
	private static ArrayList<ChangeProvinceModel> areaModels;
	
	public static ArrayList<ChangeProvinceModel> getAreaModels() {
		if (null != areaModels) {
			return areaModels;
		}

		/*IPageCache cache = new IPageCache();
		String str = cache.get(CacheKeyFactory.CACHE_FULL_DISTRICT);
		if (!TextUtils.isEmpty(str)) {
			try{
				JSONObject json = new JSONObject(str);
				FullDistrictModel model  = new FullDistrictModel();
				model.parse(json);
				areaModels = model.getProvinceModels();
			}catch (JSONException ex) {
				Log.e(LOG_TAG, ex);
				areaModels = null;
			}			
		}*/
			
		if(null == areaModels) {
			//readFromRawFile();
			changeFromRawFile();
		}

		return areaModels;
	}
	
	
	private static void changeFromRawFile() {
		Resources pResources = MyApplication.app.getResources();
		InputStream pInputStream = pResources.openRawResource(R.raw.changeprovinces);
		
		try {
			byte[] aBytes = new byte[pInputStream.available()];
			pInputStream.read(aBytes);
			String strRaw = new String(aBytes);
			if (strRaw != null && !strRaw.equals("")) {
				JSONObject json = new JSONObject(strRaw);
				ChangeDistrictModel model  = new ChangeDistrictModel();
				model.parse(json);
				areaModels = model.getProvinceModels();
			}
		} catch (IOException ex) {
			Log.e(LOG_TAG, ex);
			areaModels = null;
		} catch (JSONException ex) {
			Log.e(LOG_TAG, ex);
			areaModels = null;
		} finally {
			if(null != pInputStream) {
				try {
					pInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				pInputStream = null;
			}
		}
		
	}


	public static void setAreaModel(ArrayList<ChangeProvinceModel> models) {
		if(null == models) {
			return;
		}
		
		areaModels = models;
	}
	/*
	private static void readFromRawFile(){
		Resources pResources = MyApplication.app.getResources();
		InputStream pInputStream = pResources.openRawResource(R.raw.fulldistrict);
		
		try {
			byte[] aBytes = new byte[pInputStream.available()];
			pInputStream.read(aBytes);
			String strRaw = new String(aBytes);
			if (strRaw != null && !strRaw.equals("")) {
				JSONObject json = new JSONObject(strRaw);
				FullDistrictModel model  = new FullDistrictModel();
				model.parse(json.getJSONObject("data"));
				areaModels = model.getProvinceModels();
			}
		} catch (IOException ex) {
			Log.e(LOG_TAG, ex);
			areaModels = null;
		} catch (JSONException ex) {
			Log.e(LOG_TAG, ex);
			areaModels = null;
		} finally {
			if(null != pInputStream) {
				try {
					pInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				pInputStream = null;
			}
		}
		
	}
	*/
	/*
	 * 
	 */
	public static void clean()
	{
		if(null!=areaModels)
			areaModels.clear();
		areaModels = null;
	}
}
