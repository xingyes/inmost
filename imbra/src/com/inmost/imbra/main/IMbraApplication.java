package com.inmost.imbra.main;


import com.android.volley.utils.MemDiskImageCache;
import com.inmost.imbra.basic.BasicParamModel;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.IArea;
import com.xingy.util.Config;
import com.xingy.util.MyApplication;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnErrorListener;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

public class IMbraApplication extends MyApplication {

    public static MemDiskImageCache globalMDCache;
    public static BasicParamModel   globalBasicParams;
    private Ajax mAjax;
    public void onCreate()
    {
        globalMDCache = new MemDiskImageCache(MyApplication.app);
        WeixinUtil.getWXApi(this);

        super.onCreate();
        refreshToken();
        initParams();

    }

    private void initParams() {

        globalBasicParams = new BasicParamModel();
        globalBasicParams.loadCache();

        {
            mAjax = ServiceConfig.getAjax(braConfig.URL_BASIC_PARAMS);
            if (null == mAjax)
                return;
            if(null!=globalBasicParams) {
                mAjax.setData("bv",globalBasicParams.brandModel.ver);
                mAjax.setData("bfv",globalBasicParams.funcModel.ver);
                mAjax.setData("prv",globalBasicParams.pricerangeModel.ver);
                mAjax.setData("gv",globalBasicParams.guideModel.ver);
                mAjax.setData("otv",globalBasicParams.optiontypeModel.ver);
//                mAjax.setData("csv",mSearchParams.brandModel.ver);
                mAjax.setData("smv",globalBasicParams.storeMapdModel.ver);
                mAjax.setData("adv", globalBasicParams.areaVer);
            }
            mAjax.setOnSuccessListener(new OnSuccessListener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject jsonObject, Response response) {
                    globalBasicParams.parse(jsonObject);
                }
            });
            mAjax.send();
        }
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

        mAjax.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(Ajax ajax, Response response) {
                ILogin.clearAccount();
            }
        });
        mAjax.setOnSuccessListener(new OnSuccessListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject, Response response) {
                int err = jsonObject.optInt("err",-1);
                if(err!=0)
                {
                    ILogin.clearAccount();
                }
            }
        });


        mAjax.send();
    }

    public static void startPush() {
		//Context pContext = MyApplication.app.getApplicationContext();
		//PushAssistor.setTask(pContext, true);
	}
	
}
