package com.xingy.util.cache;

import android.content.Context;

import com.xingy.util.Config;

import java.io.File;

public class InnerCache extends FileStorage {

	private String mRoot;

	public InnerCache(Context context) {
		mRoot = context.getCacheDir() + "/" + Config.TMPDIRNAME;

		File file = new File(mRoot);

		if (!file.exists()) {
			file.mkdir();
		}
	}

	@Override
	public String getRootPath() {
		return mRoot + "/";
	}
}
