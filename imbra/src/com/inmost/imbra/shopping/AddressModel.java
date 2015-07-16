package com.inmost.imbra.shopping;

import org.json.JSONObject;

import java.io.Serializable;

public class AddressModel implements Serializable{

    public String addid;
    public String user;
    public String address;
    public String phone;
    public String  cityStr;

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
        cityStr = "";
    }

	public void parse(JSONObject json){

	}
	
}
