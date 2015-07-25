package com.inmost.imbra.product;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;

public class ProductModel implements Serializable {

    public String id;    //skuid
    public String title;  //pn
    public String sale_price;
    public String ori_price;
    public String front;   //pic
    public String brandname; //bdesc
    public String brandid;
    public boolean fav;

	public void clear() {
        id = "";
        title = "";
        sale_price = "";
        ori_price = "";
        front = "";
        brandname = "";
        fav = false;
        brandid = "";
    }

    public void parseBra(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("skuid");
        title = jsonObject.optString("pn");
        front = jsonObject.optString("pic");
        brandname = jsonObject.optString("bdesc");
        brandid = jsonObject.optString("bid");

        sale_price = jsonObject.optString("sale_price");
        ori_price = jsonObject.optString("ori_price");
        fav = jsonObject.optBoolean("fav");
    }

    public void parse(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("id");
        title = jsonObject.optString("title");
        sale_price = jsonObject.optString("sale_price");
        ori_price = jsonObject.optString("ori_price");
        front = jsonObject.optString("front");
        brandname = jsonObject.optString("brandname");
        fav = jsonObject.optBoolean("fav");
    }


    public void parseRelative(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("id");
        title = jsonObject.optString("title");
        sale_price = jsonObject.optString("sale_price");
        front = jsonObject.optString("cover");
        if(TextUtils.isEmpty(front))
            front = jsonObject.optString("front");
        brandname = jsonObject.optString("brand");
    }

}
