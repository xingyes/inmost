package com.xingy.util;

import android.os.Looper;

import com.xingy.lib.ILogin;
import com.xingy.lib.IVersion;
import com.xingy.util.cache.FileStorage;
import com.xingy.util.cache.StorageFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

public final class Log {

	private static boolean write(String content) {
		if (content == null || content.trim().equals("")) {
			return false;
		}

		File file = null;
		FileStorage storage = StorageFactory.getFileStorage(MyApplication.app);
		if ((file = storage.CreateFileIfNotFound(Config.LOG_NAME)) == null) {
			return false;
		}
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath(), true)));
			out.write(content);
			
		} catch (IOException ex) {
			return false;
		}finally{
			try {
				if(out != null)
					out.close();
			} catch (Exception e) {
			}
		}

		return true;
	}

	private static String getCommonMessage() {
		StringBuilder mStringBuilder = new StringBuilder();
		mStringBuilder.append("time: " + ToolUtil.toDate(new Date().getTime(), "yyyy-MM-dd HH:mm:ss"));
		mStringBuilder.append("|");
		mStringBuilder.append("mobile: " + android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE);
		mStringBuilder.append("|");
		mStringBuilder.append("version: " + IVersion.getVersionName() + "(" + Config.COMPILE_TIME + ")");
		mStringBuilder.append("|");
		mStringBuilder.append("versionCode: " + IVersion.getVersionCode());
		mStringBuilder.append("|");
		mStringBuilder.append("uid: " + ILogin.getLoginUid());
		mStringBuilder.append("|");
		return mStringBuilder.toString();
	}

	public static void e(String tag, String msg) {
		String strTestTag = "";
		if(Config.isCustomerTestVersion){
			strTestTag = "----------------Customer test-------------------\n";
		}
		
		write("\n" + strTestTag + getCommonMessage() + tag + "|error|" + msg);
		if (Config.DEBUG) {
			android.util.Log.e(tag, msg);
		}
    }

	public static void e(String tag, Exception ex) {
		e(tag, ToolUtil.getStackTraceString(ex));
	}

	public static void w(String tag, String msg) {
		write("\n" + getCommonMessage() + tag + "|warn|" + msg);
		if (Config.DEBUG) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void d(String tag, Object msg) {
		if (!Config.DEBUG)
			return;

		android.util.Log.d(tag, String.valueOf(msg));
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (!Config.DEBUG)
			return;

		android.util.Log.d(tag, msg, tr);
	}
}
