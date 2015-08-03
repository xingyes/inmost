package com.inmost.imbra.login;
/**
 * 
 * @author xingyao
 *
 */
public class Account 
{
	//public static final int TYPE_PHONE = 0;
	//public static final int TYPE_QQ = 1;
	//public static final int TYPE_WECHAT = 2;
	
	
	// Private part.
	public  String uid="";

    public String token="";

    public String nickName="";

    public String iconUrl = "";
    public long rowCreateTime;
    public String phone="";

    public Account()
    {
        uid = "";
        token="";
        nickName = "";
        iconUrl = "";
        rowCreateTime = 0;
        phone = "";
    }
    //private int  type = TYPE_PHONE;
}
