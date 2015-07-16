package com.xingy.lib.model;

import com.xingy.util.ToolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

@SuppressWarnings("serial")
public class ProvinceModel extends BaseModel  implements Serializable{
	private int mProvinceId;
	private int mProvinceIPId;
	private String mProvinceName;
	private int mProSortId;
	private ArrayList<CityModel> mCityModels = new ArrayList<CityModel>();
	
	public int getProvinceId() {
		return this.mProvinceId;
	}
	
	public int getProvinceIPId(){
		return this.mProvinceIPId;
	}
	
	public String getProvinceName(){
		return this.mProvinceName;
	}
	
	public ArrayList<CityModel> getCityModels() {
		return this.mCityModels;
	}
	
	public int getProvinceSortId(){
		return this.mProSortId;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void parse(JSONObject json) throws JSONException {
		if(null == json){
			return;
		}
		
		mProvinceId = json.optInt("id", 0);
		mProvinceIPId = json.optInt("ip_id", 0);
		mProvinceName = json.optString("name", "");
		mProSortId = json.optInt("sortId", 0);
		
		if(!ToolUtil.isEmptyList(json, "city")) {
			JSONObject pCityJson = json.optJSONObject("city");
			if( null != pCityJson ) {
				Iterator<String> iter = pCityJson.keys();
				while( iter.hasNext() ) {
					CityModel pCityModel = new CityModel();
					String key = iter.next();
					pCityModel.parse(pCityJson.getJSONObject(key));
					mCityModels.add(pCityModel);
				}
				
				Collections.sort(mCityModels, new Comparator(){
					@Override
					public int compare(Object one, Object another) {
						CityModel a = (CityModel) one;
						CityModel b = (CityModel) another;
						return ToolUtil.compareInt(a.getCitySortId(), b.getCitySortId());
					}
				});
			}
		}
	}
	
	public class CityModel  implements Serializable{
		private int mCityId;
		private String mCityName;
		private int mCitySortId;
		private ArrayList<ZoneModel> mZoneModels = new ArrayList<ZoneModel>();
		
		public int getCityId() {
			return this.mCityId;
		}
		
		public String getCityName(){
			return this.mCityName;
		}
		
		public ArrayList<ZoneModel> getZoneModels () {
			return this.mZoneModels;
		}
		
		public int getCitySortId(){
			return this.mCitySortId;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void parse(JSONObject json) throws JSONException {
			if(null == json){
				return;
			}
			
			mCityId = json.optInt("id", 0);
			mCityName = json.optString("name", "");
			mCitySortId = json.optInt("sortId", 0);
			
			if(!ToolUtil.isEmptyList(json, "district")) {
				JSONObject pZoneJson = json.getJSONObject("district");
				Iterator<String> iter = pZoneJson.keys();
				while(iter.hasNext()) {
					ZoneModel model = new ZoneModel();
					String key = iter.next();
					model.parse(pZoneJson.getJSONObject(key));
					mZoneModels.add(model);
				}
				
				Collections.sort(mZoneModels, new Comparator(){
					@Override
					public int compare(Object one, Object another) {
						ZoneModel a = (ZoneModel) one;
						ZoneModel b = (ZoneModel) another;
						return ToolUtil.compareInt(a.getZoneSortId(), b.getZoneSortId());
					}
				});
			}
		}
		
		
		public class ZoneModel {
			private int mZoneId;
			private String mZoneName;
			private int mZoneSortId;
			
			public int getZoneId(){
				return this.mZoneId;
			}
			
			public String getZoneName(){
				return this.mZoneName;
			}
			
			public int getZoneSortId(){
				return mZoneSortId;
			}
			
			public void parse(JSONObject json) {
				if(null == json){
					return;
				}
				
				mZoneId = json.optInt("id", 0);
				mZoneName = json.optString("name", "");
				mZoneSortId = json.optInt("sortId", 0);
			}
		}
		
	}

}


