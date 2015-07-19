package com.inmost.imbra.search_backup;

import org.json.JSONArray;
import org.json.JSONException;

public class AutoCompleteModel {
	private String name;
	private long num;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public void parse(JSONArray arr) throws JSONException{
		setName(arr.getString(0));
		setNum(arr.getLong(1));
	}
}
