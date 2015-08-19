package com.inmost.imbra.collect;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-12.
 */
public class CollectCardModel {

    public static final int TYPE_PRODUCT = 0;
    public static final int TYPE_BRAND = 1;
    public String description;
    public String title;
    public String collection_id;
    public String id;
    public String status;
    public String imgUrl;
    public int    type;
    public int  fav;
    public int  fav_cnt;

    public String brand_id;
    public String brand_name;

    public String product_type_id;
    public String cost_price;
    public String sale_price;


    public void clear() {
        fav = 0;
        fav_cnt = 0;
        id = "";
        title = "";
        description = "";
        collection_id = "";
        status = "";
        imgUrl = "";
        type = TYPE_PRODUCT;
    }

    public void parseCollect(JSONObject json) {
        clear();

        type = TYPE_PRODUCT;
        id = json.optString("pid");
        title = json.optString("title");
        description = json.optString("description");
        status = json.optString("status");
        imgUrl = json.optString("pic");
        fav = json.optInt("is_fav");
        fav_cnt = json.optInt("fav_cnt");

        BigDecimal hun = new BigDecimal(100);
        BigDecimal it = new BigDecimal(json.optLong("price"));
        it = it.divide(hun, 2);
        sale_price =  it.toPlainString();

    }

    public void parseBrand(JSONObject json)
    {
        clear();

        type = TYPE_BRAND;
        brand_id = json.optString("bid");
        brand_name = json.optString("bname");
        imgUrl = json.optString("pic");
    }
}
