package com.inmost.imbra.product;

import android.text.TextUtils;

import org.json.JSONObject;

public class SearchParamModel {

    public String id;
    public String info;

	public void clear() {
        id = "";
        info = "";
    }

    public void parse(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("id");

        info = jsonObject.optString("name");
        if(TextUtils.isEmpty(info))
            info = jsonObject.optString("text");

    }

}
