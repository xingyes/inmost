package com.inmost.imbra.main;


import com.android.volley.utils.MemDiskImageCache;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.xingy.util.MyApplication;
import com.xingy.util.ServiceConfig;

public class IMbraApplication extends MyApplication {

    public static MemDiskImageCache globalMDCache;

    public void onCreate()
    {
        globalMDCache = new MemDiskImageCache(MyApplication.app);
        WeixinUtil.getWXApi(this);

        super.onCreate();
    }
	public static void startPush() {
		//Context pContext = MyApplication.app.getApplicationContext();
		//PushAssistor.setTask(pContext, true);
	}
	
}
