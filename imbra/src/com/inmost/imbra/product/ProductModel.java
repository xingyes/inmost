package com.inmost.imbra.product;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ProductModel implements Serializable {

    public static final int FAV_OK = 1;
    public static final int FAV_NO = 0;

    public String fid;    //fav_id
    public long   timemark;
    public String id;    //skuid
    public String title;  //pn
    public String sale_price;
    public String ori_price;
    public String front;   //pic
    public String brandname; //bdesc
    public String brandid;
    public String state;
    public ArrayList<String> choose;
    public String  chooseStr;
    public boolean fav;

    public String orderid;
    public long   buy_qty;
    public String buy_size;

    public ProductModel(){clear();}
	public void clear() {
        id = "";
        title = "";
        sale_price = "";
        ori_price = "";
        front = "";
        brandname = "";
        fav = false;
        brandid = "";
        chooseStr = "";
        if(null==choose)
            choose = new ArrayList<String>();
        else
            choose.clear();
    }

    public void parseBra(JSONObject jsonObject) {
        clear();
        fid = jsonObject.optString("fid");
        id = jsonObject.optString("skuid");
        title = jsonObject.optString("pn");
        front = jsonObject.optString("pic");
        brandname = jsonObject.optString("bdesc");
        brandid = jsonObject.optString("bid");

        state = jsonObject.optString("stat");
        chooseStr = jsonObject.optString("size");
        if(!TextUtils.isEmpty(chooseStr))
        {
            String items[] = chooseStr.split(",",-1);
            for(String item : items )
                choose.add(item);
        }
        BigDecimal hun = new BigDecimal(100);
        BigDecimal it = new BigDecimal(jsonObject.optLong("price"));
        it = it.divide(hun ,2);
        sale_price =  it.toPlainString();
        it = new BigDecimal(jsonObject.optLong("dprice"));
        it = it.divide(hun ,2);
        ori_price = it.toPlainString();
        fav = (jsonObject.optInt("is_fav")==FAV_OK);
    }


    public void parseSearch(JSONObject jsonObject) {
        clear();
        fid = jsonObject.optString("fid");
        id = jsonObject.optString("pid");
        brandid = jsonObject.optString("bfid");
        if(jsonObject.has("title"))
            title = jsonObject.optString("title");
        else if(jsonObject.has("tit"))
            title = jsonObject.optString("tit");
        front = jsonObject.optString("pic");
        if(jsonObject.has("time"))
            timemark = jsonObject.optLong("time");
        brandname = jsonObject.optString("bdesc");

        state = jsonObject.optString("stat");
        chooseStr = jsonObject.optString("cs");
        if(!TextUtils.isEmpty(chooseStr))
        {
            String items[] = chooseStr.split(",",-1);
            for(String item : items )
                choose.add(item);
        }
        BigDecimal hun = new BigDecimal(100);
        BigDecimal it = new BigDecimal(jsonObject.optLong("price"));
        it = it.divide(hun ,2);
        sale_price =  it.toPlainString();
        it = new BigDecimal(jsonObject.optLong("dprice"));
        it = it.divide(hun ,2);
        ori_price = it.toPlainString();
        fav = (jsonObject.optInt("is_fav")==1);
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


    /**
     * {"opid":"41",
     "oid":"143858464979086",
     "pid":"1",
     "pname":"Lascivious奶油色胸衣和情趣内裤套装","buy_qty":"1","size":"70B","price":"89900",
     pic:"",
     "credit":"0","credit_type":"0","discount_amt":"0","return_qty":"0","credit_cost":"0","last_update":null}]
     * @param jsonObject
     */
    public void parseFromOrder(JSONObject jsonObject) {
        clear();
        id = jsonObject.optString("pid");
        title = jsonObject.optString("pname");
        buy_qty = jsonObject.optInt("buy_qty");
        buy_size = jsonObject.optString("size");
        BigDecimal hun = new BigDecimal(100);
        BigDecimal it = new BigDecimal(jsonObject.optLong("price"));
        it = it.divide(hun ,2);
        sale_price =  it.toPlainString();
        front = jsonObject.optString("pic");
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
