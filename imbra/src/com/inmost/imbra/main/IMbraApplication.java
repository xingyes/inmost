package com.inmost.imbra.main;


import com.android.volley.utils.MemDiskImageCache;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.imbra.util.braConfig;
import com.xingy.util.MyApplication;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

public class IMbraApplication extends MyApplication {

    public static MemDiskImageCache globalMDCache;
    private Ajax mAjax;
    public void onCreate()
    {
        globalMDCache = new MemDiskImageCache(MyApplication.app);
        WeixinUtil.getWXApi(this);

        super.onCreate();
        refreshToken();

    }

    private void refreshToken()
    {
        Account act = ILogin.getActiveAccount();
        if(act==null)
            return;

        act.iconUrl = "http://img2.imgtn.bdimg.com/it/u=921607941,1665261509&fm=21&gp=0.jpg";
        act.nickName = "手机用户111";

        mAjax = ServiceConfig.getAjax(braConfig.URL_CHECK_TOKEN);
        if (null == mAjax)
            return;
        mAjax.setData("token",act.token);
        mAjax.setData("uid",act.uid);

        mAjax.setOnSuccessListener(new OnSuccessListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject, Response response) {

            }
        });


        mAjax.send();
    }

    public static void startPush() {
		//Context pContext = MyApplication.app.getApplicationContext();
		//PushAssistor.setTask(pContext, true);
	}
	
}
