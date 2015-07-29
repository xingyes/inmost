package com.inmost.imbra.shopping;

import com.inmost.imbra.product.ProductModel;

import org.json.JSONObject;

import java.io.Serializable;

public class OrderModel implements Serializable{

    public ProductModel promodel;
    public static int STATE_WAITTING_PAY = 1;
    public static int STATUS_PAYFAIL = 2;
    public static int STATE_PAYED = 5;
    public static int STATE_FINISH = 6;
    public static int STATE_CANCELING = 7;
    public static int STATE_CANCELED = 8;





    public int    status;
    public String statuStr;
    public String orderid;
    public  long  ordertime;
    public long   payexpire;
    public String ccode;

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

        ccode = "";
        payexpire = 0;
    }

	public void parse(JSONObject json){
        if(promodel == null)
            promodel = new ProductModel();
        promodel.parse(json);

	}
	
}
