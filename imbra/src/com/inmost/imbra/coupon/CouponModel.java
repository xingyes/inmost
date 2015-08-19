package com.inmost.imbra.coupon;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class CouponModel implements Serializable {

    public String id;
    public String title;
    public long   expireTime;
    public long   startTime;
    public String coupon_fee;


    public void clear() {
        id = "";
        title = "";
        expireTime = 0;
    }

    //coupon_code：优惠券码
    // title：优惠券描述
    // begin_time：起始时间，以时间戳为单位，显示格式由APP处理
    // end_time：结束时间，以时间戳为单位
    public void parse(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("coupon_code");
        title = jsonObject.optString("title");

        BigDecimal hun = new BigDecimal(100);
        BigDecimal it = new BigDecimal(jsonObject.optLong("quota"));
        it = it.divide(hun, 2);
        coupon_fee = it.toPlainString();

        startTime = jsonObject.optLong("begin_time");
        expireTime = jsonObject.optLong("end_time");

    }

}
