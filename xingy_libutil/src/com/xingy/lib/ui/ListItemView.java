/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: CouponView.java
 * 
 * Description: 
 * Author: qingliang (qingliang@tencent.com)
 * Created: July 4, 2013
 * 
 */
package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xingy.R;
import com.xingy.util.ImageHelper;

public class ListItemView extends LinearLayout{
	private Boolean isSelected;
	private Paint   mPaint;
	private Rect    mDest;
	private Bitmap  mSelectIcon;

	public ListItemView(Context context) {
		super(context);
		init();
	}
	
	public ListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Draw the tick if necessary.
		if( isSelected && null != mSelectIcon ) {
			final int nWidth = this.getWidth();
			final int nHeight = this.getHeight();
			
			mDest.bottom = nHeight;
			mDest.right = nWidth;
			mDest.left = nWidth - mSelectIcon.getWidth();
			mDest.top = nHeight - mSelectIcon.getHeight();
			canvas.drawBitmap(mSelectIcon, null, mDest, mPaint);
		}
	}
	
	private void init(){
		mPaint = new Paint();
		mDest = new Rect();
		mSelectIcon = ImageHelper.getResBitmap(this.getContext(), R.drawable.denom_select);
	}
	
	public void setSelected(boolean bSelected) {
		isSelected = bSelected;
		this.setBackgroundResource( isSelected ? R.drawable.denom_on : R.drawable.denom_off2);
	}

}
