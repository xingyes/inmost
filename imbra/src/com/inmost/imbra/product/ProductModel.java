package com.inmost.imbra.product;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ProductModel implements Serializable {

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
