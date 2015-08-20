package com.inmost.imbra.shopping;

import com.inmost.imbra.R;
import com.inmost.imbra.coupon.CouponModel;
import com.inmost.imbra.product.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class OrderModel implements Serializable{

    public ArrayList<ProductModel> proList;
    public AddressModel usrmodel;
    public CouponModel  coup;

    public String orderid;
    public boolean toSelf;
    public String total_price;
    public String discount_price;
    public String       ship_fee;
    public String  other_fee;


    public static final int PAY_T_FACE = 1;
    public static final int PAY_T_WX = 2;
    public static final int PAY_T_ALI = 3;
    public int pay_type; //1=货到付款，2=微信支付，3=支付宝

    public static final int PAY_STAT_WAITING = 0;
    public static final int PAY_STAT_ING = 1;
    public static final int PAY_STAT_SUCC = 2;
    public static final int PAY_STAT_TIMEOUT = -1;//ccode timeout
    public static final int PAY_STAT_FAIL = -2;
    public int pay_stat; //0=待支付，1=支付中，2=支付成功，-1=支付超时，-2=支付失败

    public static final int ORDER_STAT_WAITING = 0;
    public static final int ORDER_STAT_TO_PAY = 1;
    public static final int ORDER_STAT_TO_OUTSTORE = 2;
    public static final int ORDER_STAT_PACKAGING = 3;
    public static final int ORDER_STAT_DELIVER = 4;
    public static final int ORDER_STAT_TO_RECEIVE = 5;
    public static final int ORDER_STAT_RECEIVED = 6;
    public static final int ORDER_STAT_SYS_DEL = -1;
    public static final int ORDER_STAT_PERSON_DEL = -2;
    public static final int ORDER_STAT_CUSTOMER_DEL = -3;
    public static final int ORDER_STAT_PART_RETURN = -4;
    public static final int ORDER_STAT_FULL_RETURN = -5;
    public int order_stat; //0=待审核，1=审核通过待支付，2=支付成功待出库，3=出库打包中，4=已出库，5=待签收，6=已签收，-1=系统作废，-2=客服作废，-3=用户作废，-4=部分退货，-5=全部退货

    public static final int[] orderStateStrRid = {R.string.order_stat_0,R.string.order_stat_1,
            R.string.order_stat_2,R.string.order_stat_3,R.string.order_stat_4,R.string.order_stat_5,R.string.order_stat_6};

    public static final int[] orderStateErrStrRid = {R.string.order_err_0,R.string.order_err_1,
            R.string.order_err_2,R.string.order_err_3,R.string.order_err_4,R.string.order_err_5};

    public int ship_stat; //0=待发货，1=已收件，2=发货中，3=已签收，-1=用户拒收

    public String remark;


    public  String  strAddtime;
    public long   last_update;
    public long   payexpire;
    public String ccode;

    public OrderModel()
    {
        clear();
    }

    public void clear() {
        if (null == proList)
            proList = new ArrayList<ProductModel>();
        else
            proList.clear();
        if(null==usrmodel)
            usrmodel = new AddressModel();
        else
            usrmodel.clear();
        if(null == coup)
            coup = new CouponModel();
        else
            coup.clear();

        orderid = "";
        toSelf = true;

        total_price = "";
        discount_price = "";
        ship_fee = "";
        other_fee = "";

        pay_type = 0;
        pay_stat = 0;
        order_stat = 0;
        ship_stat = 0;

        remark = "";

        strAddtime = "";
        last_update = 0;
        payexpire = 0;
        ccode = "";
    }

    /**
     *
     * @param json
     */
	public void parse(JSONObject json){
        /**{"oid":"143858464979086",
            "type":"1", "consignee":"张三1",
            "mobile":"13940401231","uid":"3",
            "province_id":"11","city_id":"52","town_id":"24","district_id":"0","addr":"淞虹路1111号1楼","postcode":"0",

         "total_price":"89900","discount_price":"0",
         "ship_fee":"0","other_fee":"0","coupon_fee":"0","coupon_code":"","integral_amt":"0",
         "promotion_amt":"0","paid_fee":"0","pay_type":"2","pay_stat":"0","order_stat":"0","ship_stat":"0","remark":"","extra":null,
         "addtime":"1438584649","last_update":null,

         "product_list":[{"opid":"41",
                          "oid":"143858464979086",
                          "pid":"1",
                          "pname":"Lascivious奶油色胸衣和情趣内裤套装","buy_qty":"1","size":"70B","price":"89900",
         "credit":"0","credit_type":"0","discount_amt":"0","return_qty":"0","credit_cost":"0","last_update":null}]
         }
         */

        orderid = json.optString("oid");
        toSelf = (json.optInt("type")==1);
        usrmodel.parse(json);
        BigDecimal hun = new BigDecimal(100);
        BigDecimal it = new BigDecimal(json.optLong("total_price"));
        it = it.divide(hun ,2);
        total_price =  it.toPlainString();

        it = new BigDecimal(json.optLong("discount_price"));
        it = it.divide(hun ,2);
        discount_price =  it.toPlainString();

        it = new BigDecimal(json.optLong("ship_fee"));
        it = it.divide(hun ,2);
        ship_fee =  it.toPlainString();

        it = new BigDecimal(json.optLong("other_fee"));
        it = it.divide(hun ,2);
        other_fee =  it.toPlainString();

        coup.parse(json);
        pay_type = json.optInt("pay_type");
        pay_stat = json.optInt("pay_stat");
        order_stat = json.optInt("order_stat");
        ship_stat = json.optInt("ship_stat");
        remark = json.optString("remark");

        strAddtime = json.optString("addtime");
        last_update = json.optLong("last_update");

        JSONArray plist = json.optJSONArray("product_list");
        for(int i = 0 ; null!=plist && i < plist.length();i++)
        {
            ProductModel pro = new ProductModel();
            pro.parseFromOrder(plist.optJSONObject(i));
            proList.add(pro);
        }

	}
	
}
