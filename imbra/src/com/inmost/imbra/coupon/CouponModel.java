package com.inmost.imbra.coupon;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class CouponModel implements Serializable {

    public String id;
    public String title;
    public int    discountNum;
    public long   expireTime;
    public String coupon_code;
    public String coupon_fee;


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

        coupon_code = jsonObject.optString("coupon_code");
        BigDecimal hun = new BigDecimal(100);
        BigDecimal it = new BigDecimal(jsonObject.optLong("coupon_fee"));
        it = it.divide(hun, 2);
        coupon_fee = it.toPlainString();
    }

}
