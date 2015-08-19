package com.xingy.lib.model;

import android.content.Intent;
import android.text.TextUtils;

import com.xingy.util.ToolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class FullDistrictModel extends BaseModel{
	ArrayList<ProvinceModel> mProvnceModels = new ArrayList<ProvinceModel>();
	String strMD5;
	
	public ArrayList<ProvinceModel> getProvinceModels(){
		return mProvnceModels;
	}
	
	public String getMD5Value(){
		return strMD5;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FullDistrictModel parse(JSONObject json) throws JSONException {
		if(null == json) {
			return null;
		}
		
		strMD5 = json.optString("v", "");
		JSONObject data = json.getJSONObject("dt");
		if(null != data && data.length()>0 ) {
		    Iterator<String> iter = data.keys();
			while(iter.hasNext()) {
			    ProvinceModel model = new ProvinceModel();
			    String key = iter.next();
				model.parse(data.optJSONObject(key));
                if(TextUtils.isDigitsOnly(key)) {
                    model.mProvinceId = Integer.valueOf(key);
                    model.mProSortId = Integer.valueOf(key);
                }
                mProvnceModels.add(model);
			}
				
			Collections.sort(mProvnceModels, new Comparator(){
					@Override
					public int compare(Object one, Object another) {
						ProvinceModel a = (ProvinceModel) one;
						ProvinceModel b = (ProvinceModel) another;
						return ToolUtil.compareInt(a.getProvinceSortId(), b.getProvinceSortId());
					}
			});
		}
		
		return null;
	}

}
