package com.xingy.util.ajax;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xingy.util.MyApplication;
import com.xingy.util.ToolUtil;

import java.util.Locale;

public class HttpUtil {
	private static final String LOG_TAG = HttpUtil.class.getName();
	public static enum NetworkState{UNAVAIL,WIFI,MOBILE};
	public static final int WIFI = 1;
	public static final int WAP = 2;
	public static final int NET = 3;
	public static final int UNAVAILABLE = 4;

	public static int getNetType(Context context) {
		int netType;
		try {
			NetworkInfo netWork = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

			if (netWork != null) {
				boolean isAvailable = netWork.isAvailable();

				if (isAvailable) {
					String whichType = netWork.getTypeName().toUpperCase(Locale.getDefault());
					if (whichType.equals("WIFI")) {
						netType = WIFI;
					} else {
						String proxyHost = android.net.Proxy.getDefaultHost();
						if (proxyHost != null && proxyHost.length() > 0) {
							return WAP;
						} else {
							return NET;
						}
					}
				} else {
					netType = UNAVAILABLE;
				}
			} else {
				netType = UNAVAILABLE;
			}
		} catch (Exception ex) {
			Log.e(LOG_TAG, "getNetType|" + ToolUtil.getStackTraceString(ex));
			netType = UNAVAILABLE;
		}
		return netType;
	}
	
	
	public static String getNetTypeName() {
		final int netType = HttpUtil.getNetType(MyApplication.app);
		String sNetType = ( netType == HttpUtil.WIFI ? "WIFI" :(  netType == HttpUtil.NET ? "3G" : ( netType == HttpUtil.WAP ? "WAP" : "NONE" ) ) );
	
		TelephonyManager telManager = (TelephonyManager) MyApplication.app.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId();
		String name = "no_sim_card";
		 if(imsi!=null){ 
			 if(imsi.startsWith("46000") || imsi.startsWith("46002")){ 
				 name = "CMCC";
			 }else if(imsi.startsWith("46001")){
				 name = "CUCC";
			 }else if(imsi.startsWith("46003")){
				 name = "CT";
			 } 
		} 
		 
		sNetType = name + " " + sNetType;
		
		return sNetType;
	}
	
	
	public static NetworkState getNetworkState(Context context){
		NetworkInfo networkinfo = null;
		NetworkState ret = NetworkState.UNAVAIL;
		try{
			ConnectivityManager pManager = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);    
			networkinfo = pManager.getActiveNetworkInfo();
			
			if( null == networkinfo ) {
				// Get current active network information.
				NetworkInfo[] aAllInfo = pManager.getAllNetworkInfo();
				final int nSize = (null != aAllInfo ? aAllInfo.length : 0);
				for ( int nIdx = 0; nIdx < nSize; nIdx++ )
				{
					NetworkInfo pEntity = aAllInfo[nIdx];
					if ( (null != pEntity) && (pEntity.isAvailable() && (pEntity.isConnectedOrConnecting())) )
					{
						networkinfo = pEntity;
						break;
					}
				}
				
				aAllInfo = null;
			}
			
			if( null != networkinfo && networkinfo.isAvailable() ) {
				ret = (networkinfo.getType() == ConnectivityManager.TYPE_WIFI ? NetworkState.WIFI : NetworkState.MOBILE);
			}
			
		}catch(Exception ex){
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
		}
		return ret;
	}
	
	/**
	 * Check whether is using wifi now
	 * @param aContext
	 * @return
	 */
	public static boolean isUsingWifi(Context aContext)
	{
		if( null == aContext )
			return false;
		
		ConnectivityManager pManager = (ConnectivityManager)aContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		return HttpUtil.isWifiConnected(pManager, aContext);
	}
	
	/**
	 * isNetworkAvailable
	 * @param aContext
	 * @return
	 */
	public static boolean isNetworkAvailable(Context aContext) {
		if( null == aContext )
			return false;
		
		// Firstly, we check whether is airplane mode.
		final boolean bAirplane = (1 == Settings.System.getInt(aContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0));
		
		// Get the connectivity manager.
		ConnectivityManager pManager = (ConnectivityManager)aContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		return bAirplane ? HttpUtil.isWifiConnected(pManager, aContext) : HttpUtil.hasActiveNetwork(pManager, aContext);
	}
	
	/**
	 * isWifiConnected
	 * @param aContext
	 * @return
	 */
	private static boolean isWifiConnected(ConnectivityManager aManager, Context aContext) {
		if( (null == aManager) || (null == aContext) )
			return false;
		
		NetworkInfo pInfo = aManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if( null == pInfo )
			return false;
		
		NetworkInfo.State state = pInfo.getState();
		
		return (NetworkInfo.State.CONNECTED == state);
	}
	
	/**
	 * hasActiveNetwork
	 * @param aManager
	 * @param aContext
	 * @return
	 */
	private static boolean hasActiveNetwork(ConnectivityManager aManager, Context aContext) {
		if( (null == aManager) || (null == aContext) )
			return false;
		
		NetworkInfo pInfo = aManager.getActiveNetworkInfo();
		if( null != pInfo )
			return pInfo.isAvailable();
		
		// Check current active network information.
		NetworkInfo[] aInfo = aManager.getAllNetworkInfo();
		final int nSize = (null != aInfo ? aInfo.length : 0);
		for( int nIdx = 0; nIdx < nSize; nIdx++ ) {
			NetworkInfo pEntity = aInfo[nIdx];
			if( (null != pEntity) && (pEntity.isAvailable()) && (pEntity.isConnectedOrConnecting())) {
				return true;
			}
		}
		
		return false;
	}
}
