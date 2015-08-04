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
