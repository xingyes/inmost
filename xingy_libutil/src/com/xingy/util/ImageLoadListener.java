package com.xingy.util;

import android.graphics.Bitmap;

/**
 * ImageLoadListener
 * @author lorenchen
 */
public interface ImageLoadListener 
{
	/**
	 * onLoaded
	 * @param aBitmap
	 * @param strUrl
	 */
	abstract void onLoaded(Bitmap aBitmap, String strUrl);
	
	/**
	 * onError
	 * @param strUrl
	 */
	abstract void onError(String strUrl);
}
