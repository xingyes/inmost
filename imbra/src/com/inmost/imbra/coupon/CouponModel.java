package com.inmost.imbra.coupon;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;

public class CouponModel implements Serializable {

    public String id;
    public String title;
    public int    discountNum;
    public long   expireTime;

	public void clear() {
        id = "";
        title = "";
        expireTime = 0;
        discountNum = 20;
    }

    public void parse(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("id");
        title = jsonObject.optString("title");
    }

}
