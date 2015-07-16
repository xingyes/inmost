package com.inmost.imbra.main;


import com.android.volley.utils.MemDiskImageCache;
import com.xingy.util.MyApplication;

public class IMbraApplication extends MyApplication {

    public static MemDiskImageCache globalMDCache;

    public void onCreate()
    {
        globalMDCache = new MemDiskImageCache(MyApplication.app);
        super.onCreate();
    }
	public static void startPush() {
		//Context pContext = MyApplication.app.getApplicationContext();
		//PushAssistor.setTask(pContext, true);
	}
	
}
