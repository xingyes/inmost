package com.inmost.imbra.collect;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-12.
 */
public class CollectCardModel {

    public static final String TYPE_BRAND = "BRAND";
    public static final String TYPE_PRODUCT = "PRODUCT";
    public String description;
    public String title;
    public String collection_id;
    public String id;
    public String type_id;
    public String status;
    public String imgUrl;
    public String type;

    public String brand_id;
    public String product_type_id;
    public String cost_price;
    public String sale_price;


    public void clear() {
        id = "";
        type = "";
        title = "";
        description = "";
        collection_id = "";
        status = "";
        imgUrl = "";
        type_id = "";

    }

    public void parse(JSONObject json) {
        clear();

        type = json.optString("type");
        id = json.optString("id");
        type_id = json.optString("type_id");
        title = json.optString("title");
        description = json.optString("description");
        collection_id = json.optString("collection_id");
        status = json.optString("status");
        imgUrl = json.optString("cover");

        JSONObject typejson = json.optJSONObject("type_object");
        brand_id = typejson.optString("id");
        cost_price = typejson.optString("cost_price");
        sale_price = typejson.optString("sale_price");
        product_type_id = typejson.optString("product_type_id");

    }
}
