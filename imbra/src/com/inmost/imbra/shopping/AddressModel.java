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

    }

	public void parse(JSONObject json){

	}
	
}
