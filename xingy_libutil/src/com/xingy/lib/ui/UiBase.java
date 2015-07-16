/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: UiBase.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: Jan 13, 2013
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xingy.R;

public class UiBase extends LinearLayout {
	/**
	 * Constructor of UiBase
	 * @param context
	 * @param attrs
	 */
	protected UiBase(Context context, AttributeSet attrs, int layoutId) {
		super(context, attrs);
		mLayoutId = layoutId;
		this.parseAttrs(attrs);
	}
	
	/**
	 * @param context
	 * @param layoutId
	 */
	protected UiBase(Context context, int layoutId) {
		super(context);
		mLayoutId = layoutId;
		View.inflate(context, layoutId, this);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Context pContext = this.getContext();
		if( (null != pContext) && (mLayoutId > 0) ) {
			mRoot = inflate(pContext, mLayoutId, null);
			addView(mRoot, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
			
			// Initialize the data.
			onInit(pContext);
		}
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		if ( (mReferWidth > 0) && (mReferHeight > 0) ) {
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			final int nHeight = width * mReferHeight / mReferWidth;
			this.setMeasuredDimension(width, nHeight);
			ViewGroup.LayoutParams pParams = this.getLayoutParams();
			if( null != pParams ) {
				pParams.width = width;
				pParams.height = nHeight;
				this.setLayoutParams(pParams);
			}
		}		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	protected void parseAttrs(Context aContext, TypedArray aArray) {
		// Inherited class should override the class.
	}
	
	protected void onInit(Context aContext) {
		// Inherited class should override the class.
	}
	
	/**
	 * parse attributes
	 * @param attrs
	 */
	private void parseAttrs(AttributeSet attrs) {
		Context pContext = this.getContext();
		if( null == pContext )
			return ;
		
		TypedArray array = pContext.obtainStyledAttributes(attrs, R.styleable.xingy_attrs);
		mReferWidth = UiUtils.getInteger(pContext, array, R.styleable.xingy_attrs_referWidth, 0);
		mReferHeight = UiUtils.getInteger(pContext, array, R.styleable.xingy_attrs_referHeight, 0);
		
		// Other attributes.
		parseAttrs(pContext, array);
	}

	protected View mRoot;
	private int    mLayoutId;
	protected int  mReferWidth;
	protected int  mReferHeight;
}
