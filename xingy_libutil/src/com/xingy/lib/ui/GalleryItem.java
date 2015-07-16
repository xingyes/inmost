package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class GalleryItem extends RelativeLayout {

	public GalleryItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GalleryItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GalleryItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void dispatchSetPressed(boolean pressed) {
		//  super.dispatchSetPressed(pressed);
	}
}
