package com.inmost.imbra.shopping;

import com.inmost.imbra.product.ProductModel;

import org.json.JSONObject;

import java.io.Serializable;

public class OrderModel implements Serializable{

    public ProductModel promodel;

    public int    status;
    public String statuStr;
    public String orderid;
    public  long  ordertime;

    public OrderModel()
    {
        clear();
    }

    public void clear() {
        if (null != promodel)
            promodel.clear();

        status = 1;
        statuStr = "";
        orderid = "";
        ordertime = 0;
    }

	public void parse(JSONObject json){
        if(promodel == null)
            promodel = new ProductModel();
        promodel.parse(json);

	}
	
}
