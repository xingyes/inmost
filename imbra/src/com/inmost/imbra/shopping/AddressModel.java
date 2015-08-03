package com.inmost.imbra.shopping;

import org.json.JSONObject;

import java.io.Serializable;

public class AddressModel implements Serializable{

    public String addid;
    public String user;
    public String address;
    public String phone;

    public long   provinceId;
    public String provinceStr;
    public long   cityId;
    public String  cityStr;
    public long  townId;
    public String townStr;

    public String postcode;
    public AddressModel()
    {
        clear();
    }

    public void clear()
    {
        addid = "";
        user = "";
        address="";
        phone="";
        provinceId = 0;
        cityId = 0;
        townId = 0;
        provinceStr = "";
        cityStr = "";
        townStr = "";

        postcode = "";

    }

    /* "consignee":"张三1",
       "mobile":"13940401231","uid":"3",
       "province_id":"11","city_id":"52","town_id":"24","district_id":"0","addr":"淞虹路1111号1楼","postcode":"0",
       */
    /**
     *
     * @param json
     */
	public void parse(JSONObject json){
        user = json.optString("consignee");
        phone = json.optString("mobile");
        provinceId = json.optLong("province_id");
        cityId = json.optLong("city_id");
        townId = json.optLong("town_id");
        address = json.optString("addr");

        postcode = json.optString("postcode");

	}
	
}
