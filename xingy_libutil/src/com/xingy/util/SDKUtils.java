package com.xingy.util;

import android.content.pm.PackageInfo;
import android.os.Build;


public class SDKUtils {

	/**
	 * 当前的SDK版本是不是大于1.6
	 * @return
	 */
	public static boolean isSDKVersionMoreThan16() {
		return getSDKVersion() > 4;
	}
	
	
	/**
	 * 当前的SDK版本是不是大于2.2
	 * @return
	 */
	public static boolean isSDKVersionMoreThan21() {
		return getSDKVersion() > 7;
	}
	
	/**
	 * 当前的SDK版本是不是大于2.2
	 * @return
	 */
	public static boolean isSDKVersionMoreThan20() {
		return getSDKVersion() > 6;
	}
	
	
	/**
	 * 获取当前的版本号
	 * @return
	 */
	public static int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}


	/**
	 * 当前的SDK版本是不是大于3.0
	 * @return
	 */
	public static boolean isSDKVersionMoreThan23() {
		return getSDKVersion() > 10;
	}
	
	
	/**
	 * 得到当前版本信息
	 * 
	 * @return
	 */
	public static String getSoftwareVersionName() {
		try {
			PackageInfo packageInfo = MyApplication.app.getPackageManager().getPackageInfo(MyApplication.app.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (Exception e) {
			return null;
		}

	}
	
}
