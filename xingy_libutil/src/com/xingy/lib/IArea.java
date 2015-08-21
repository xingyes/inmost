package com.xingy.lib;

import android.content.res.Resources;
import android.text.TextUtils;

import com.xingy.R;
import com.xingy.lib.model.ChangeDistrictModel;
import com.xingy.lib.model.ChangeProvinceModel;
import com.xingy.lib.model.FullDistrictModel;
import com.xingy.lib.model.ProvinceModel;
import com.xingy.util.Log;
import com.xingy.util.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class IArea {
    public static String ver;
	private static final String LOG_TAG = IArea.class.getName();
	private static ArrayList<ProvinceModel> provinceModels;
	
	public static ArrayList<ProvinceModel> getProvinces(final String cacheKey) {
		if (null != provinceModels) {
			return provinceModels;
		}

		IPageCache cache = new IPageCache();
		String str = cache.get(cacheKey);
		if (!TextUtils.isEmpty(str)) {
			try{
				JSONObject json = new JSONObject(str);
				FullDistrictModel model  = new FullDistrictModel();
				model.parse(json);
                ver = model.getMD5Value();
                provinceModels = model.getProvinceModels();
			}catch (JSONException ex) {
				Log.e(LOG_TAG, ex);
                provinceModels = null;
			}
		}

		if(null == provinceModels) {
			readFromRawFile();
		}

		return provinceModels;
	}
	
	
	public static void setProvinceModels(ArrayList<ProvinceModel> models) {
		if(null == models) {
			return;
		}

        provinceModels = models;
	}


	private static void readFromRawFile(){
		Resources pResources = MyApplication.app.getResources();
		InputStream pInputStream = pResources.openRawResource(R.raw.fulldistrict);
		
		try {
			byte[] aBytes = new byte[pInputStream.available()];
			pInputStream.read(aBytes);
			String strRaw = new String(aBytes);
			if (!TextUtils.isEmpty(strRaw))
            {
                JSONObject json = new JSONObject(strRaw);
				FullDistrictModel model  = new FullDistrictModel();
				model.parse(json);
                provinceModels = model.getProvinceModels();
			}
		} catch (IOException ex) {
			Log.e(LOG_TAG, ex);
            provinceModels = null;
		} catch (JSONException ex) {
			Log.e(LOG_TAG, ex);
            provinceModels = null;
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

	/*
	 * 
	 */
	public static void clean()
	{
		if(null!=provinceModels)
            provinceModels.clear();
        provinceModels = null;
	}
}
