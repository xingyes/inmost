package com.xingy.util.cache;

import android.content.Context;

import com.xingy.util.ToolUtil;

public class StorageFactory {
	public static FileStorage getFileStorage(Context context) {
		return ( context == null || ToolUtil.isSDExists()) ? new SDCache() : new InnerCache(context);
	}
}
