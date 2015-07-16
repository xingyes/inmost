/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: FilterCategoryActivity.java
 * 
 * Description: 
 * Author: qingliang (qingliang@tencent.com)
 * Created: September 12, 2013
 * 
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class MyWebView extends WebView {

	public MyWebView(Context context) {
		super(context);
	}
	
	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public boolean canSrollHorizon(int direction) {
		final int nOffSet = computeHorizontalScrollOffset();
		final int nRange = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
		
		if(0 == nRange) {
			return false;
		}
		
		if(direction < 0) {
			return nOffSet > 0;
		}else{
			return nOffSet < nRange - 1;
		}
		
		
	}
	
	@Override
	public void destroy()
	{
		//this.getSettings().setBuiltInZoomControls(true);
		super.destroy();
	}

}
