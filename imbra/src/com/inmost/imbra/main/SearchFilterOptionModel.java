package com.inmost.imbra.main;

import com.xingy.lib.model.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SearchFilterOptionModel extends BaseModel  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;

	public String name;

	public boolean isSelect;

	public SearchFilterOptionModel()
	{
		clear();
	}
	public void clear()
	{
		id = "";
		name="";
		isSelect = false;
	}
	public void parse(JSONObject arr) throws JSONException{
		isSelect = false;
		id = arr.optString("attrValueId");
		name = arr.optString("attrValueName");
	}
}
