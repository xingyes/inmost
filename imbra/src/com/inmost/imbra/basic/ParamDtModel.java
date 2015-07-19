package com.inmost.imbra.basic;

import org.json.JSONObject;

public class ParamDtModel {

    public String id;
    public long sortid;
    public String pic;
    public String info;

    public ParamDtModel(){ clear();}

	public void clear() {
        id = "";
        sortid = 0;
        pic = "";
        info = "";
    }

    public void parse(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("id");
        info = jsonObject.optString("n");
        sortid = jsonObject.optLong("st");
        pic = jsonObject.optString("pic");
    }

}
