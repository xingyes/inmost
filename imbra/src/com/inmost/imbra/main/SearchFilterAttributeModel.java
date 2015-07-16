package com.inmost.imbra.main;

import com.xingy.lib.model.BaseModel;
import com.xingy.util.ToolUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchFilterAttributeModel extends BaseModel  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;

	public String name;

	public ArrayList<SearchFilterOptionModel> mSearchFilterOptionModels;

	public SearchFilterAttributeModel()
	{
		clear();
	}

	public void clear()
	{
		id = "";
		name = "";
		mSearchFilterOptionModels = new ArrayList<SearchFilterOptionModel>();
	}
	public void parse(JSONObject v) throws JSONException{
		id =  v.optString("attrId") ;
		name = v.optString("attrName") ;
		mSearchFilterOptionModels.clear();
		
		if( !ToolUtil.isEmptyList(v, "AttrValue") ){
			JSONArray attrs = v.getJSONArray("AttrValue");	
			int nSize = attrs.length();
			for(int i = 0; i < nSize ; i++){
				SearchFilterOptionModel model = new SearchFilterOptionModel();
				model.parse( attrs.getJSONObject(i) );
				mSearchFilterOptionModels.add(model);
				
			}
		}
	}
}
