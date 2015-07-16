/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: icson
 * FileName: ItemImageView.java
 * 
 * Description: 
 * Author: xingyao (xingyao@tencent.com)
 * Created: 2013-3-29
 */
package com.inmost.imbra.imgallery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class ImgViewPager extends ViewPager {
	/**
	 * 
	* Create a new Instance ImageGallery.  
	*  
	* @param context
	 */
	public ImgViewPager(Context context) {
		super(context);
	}

	
	/**
	 * 
	* Create a new Instance ImageGallery.  
	*  
	* @param context
	* @param attrs
	 */
	public ImgViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setLongClickable(false);
	}
	
	public View getCurrentView()
	{
		int ix = getCurrentItem();
		return getChildAt(ix);
	}
}
