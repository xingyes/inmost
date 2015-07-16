package com.xingy.lib.model;

import com.xingy.util.ToolUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChangeDistrictModel extends BaseModel{
	ArrayList<ChangeProvinceModel> mProvnceModels = new ArrayList<ChangeProvinceModel>();
	String strMD5;
	
	public ArrayList<ChangeProvinceModel> getProvinceModels(){
		return mProvnceModels;
	}
	
	public String getMD5Value(){
		return strMD5;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ChangeDistrictModel parse(JSONObject json) throws JSONException {
		if(null == json) {
			return null;
		}
		
		if(!ToolUtil.isEmptyList(json, "dict")) {
			JSONArray array = json.optJSONArray("dict");
			for(int i=0; i < array.length();i++)
			{
				ChangeProvinceModel model = new ChangeProvinceModel();
				model.parse(array.optJSONObject(i));
				mProvnceModels.add(model);
			}
				
				Collections.sort(mProvnceModels, new Comparator(){
					@Override
					public int compare(Object one, Object another) {
						ChangeProvinceModel a = (ChangeProvinceModel) one;
						ChangeProvinceModel b = (ChangeProvinceModel) another;
						return ToolUtil.compareInt(a.getSortId(), b.getSortId());
					}
				});
		}
		
		return null;
	}

}
