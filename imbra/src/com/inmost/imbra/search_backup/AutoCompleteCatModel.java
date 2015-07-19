package com.inmost.imbra.search_backup;

import org.json.JSONArray;
import org.json.JSONException;

class AutoCompleteCatModel {
	
	private String keyWords;
	private String path;
	private String categoryName;
	
	public String getKeyWords()
	{
		return keyWords;
	}
	public void setKeyWords(String keyword)
	{
		this.keyWords = keyword;
	}
	
	public String getPath()
	{
		return this.path;
	}
	public void setPathId(String path)
	{
		this.path = path;
	}
	
	public String getCategoryName()
	{
		return this.categoryName;
	}
	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}
	
	public void parse(JSONArray arr) throws JSONException{
		setKeyWords(arr.getString(0));
		setPathId(arr.getString(1));
		setCategoryName(arr.getString(2));
	}
	
}
