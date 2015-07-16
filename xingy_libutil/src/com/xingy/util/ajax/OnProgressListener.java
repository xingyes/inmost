package com.xingy.util.ajax;

public interface OnProgressListener {
	void onProgress(Response response, int downLoaded, int totalSize);
}
