package com.xingy.lib.model;

import com.xingy.util.ToolUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ChangeProvinceModel extends BaseModel  implements Serializable{
	private String mProvinceName;
	private int   mSortId;
	private ArrayList<CityModel> mCityModels = new ArrayList<CityModel>();
	
	
	public String getProvinceName(){
		return this.mProvinceName;
	}
	
	public ArrayList<CityModel> getCityModels() {
		return this.mCityModels;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void parse(JSONObject json) throws JSONException {
		if(null == json){
			return;
		}
		
		mProvinceName = json.optString("string", "");
		mSortId = json.optInt("sortid",0);
		if(!ToolUtil.isEmptyList(json, "array")) {
			JSONArray array = json.optJSONObject("array").optJSONArray("dict");
			
			if( null != array ) {
				for(int i = 0; null!= array && i < array.length();i++)
				{
					CityModel pCityModel = new CityModel();
					pCityModel.parse(array.getJSONObject(i));
					mCityModels.add(0,pCityModel);
				}
				
				/*
				Collections.sort(mCityModels, new Comparator(){
					@Override
					public int compare(Object one, Object another) {
						CityModel a = (CityModel) one;
						CityModel b = (CityModel) another;
						return ToolUtil.compareInt(a.getCitySortId(), b.getCitySortId());
					}
				});*/
			}
		}
	}
	
	public int getSortId() {
		return mSortId;
	}

	public void setSortId(int mSortId) {
		this.mSortId = mSortId;
	}

	public class CityModel  implements Serializable{
		private String mCityName;
		private ArrayList<ZoneModel> mZoneModels = new ArrayList<ZoneModel>();
		
		public String getCityName(){
			return this.mCityName;
		}
		
		public ArrayList<ZoneModel> getZoneModels () {
			return this.mZoneModels;
		}
		
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void parse(JSONObject json) throws JSONException {
			if(null == json){
				return;
			}
			
			mCityName = json.optString("string", "");
			
			if(!ToolUtil.isEmptyList(json, "array")) {
				JSONArray array = json.getJSONObject("array").optJSONArray("string");
				for(int i=0 ; null!=array && i<array.length();i++)
				{
					ZoneModel model = new ZoneModel();
					model.mZoneName = array.optString(i);
					mZoneModels.add(model);
				}
				/*{
				ZoneModel model = new ZoneModel();
				mZoneModels.add(model);
				}*/
				
				/*
				Collections.sort(mZoneModels, new Comparator(){
					@Override
					public int compare(Object one, Object another) {
						ZoneModel a = (ZoneModel) one;
						ZoneModel b = (ZoneModel) another;
						return ToolUtil.compareInt(a.getZoneSortId(), b.getZoneSortId());
					}
				});*/
			}
		}
		
		
		public class ZoneModel {
			private String mZoneName;
			
			
			
			public String getZoneName(){
				return this.mZoneName;
			}
			
			
			
			public void parse(JSONObject json) {
				if(null == json){
					return;
				}
				
				mZoneName = json.optString("name", "");
			}
		}
		
	}

}


