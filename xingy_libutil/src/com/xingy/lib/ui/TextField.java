/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: TextField.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: Jan 08, 2013
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xingy.R;

public class TextField extends UiBase {
	/**
	 * Constructor of EditField
	 * @param context
	 * @param attrs
	 */
	public TextField(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.textfield_layout);
	}

	/**
	 * @param content
	 */
	public void setContent(String content) {
		setContent(content, null);
	}
	
	public void setContent(Spanned content) {
		if( null != mContent ) {
			mContent.setText(content);
		}
	}
	
	
	/**
	 * @param content
	 * @param info
	 */
	public void setContent(String content, String info) {
		if( null != mContent && !TextUtils.isEmpty(content) ) {
			mContentString = content;
			mContent.setText(content);
		}
		
		if( null != mInfo ) {
			mInfoString = info;
			if( TextUtils.isEmpty(info) ) {
				mInfo.setVisibility(View.GONE);
			} else {
				mInfo.setText(info);
				mInfo.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setSubCaption(String aSubCap)
	{
		if(null!=mSubCaption)
		{
			if( TextUtils.isEmpty(aSubCap) ) {
				mSubCaption.setVisibility(View.GONE);
			} else {
				mSubCaption.setText(aSubCap);
				mSubCaption.setVisibility(View.VISIBLE);
				mContentLL.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	protected void onInit(Context context) {
		// Get children components.
		mContentLL = (LinearLayout) findViewById(R.id.textfield_content_layout);
		mCaption = (TextView)findViewById(R.id.textfield_caption);
		mCaption.setText(mCaptionString);
		mSubCaption = (TextView)findViewById(R.id.textfield_sub_caption);
		mContent = (TextView)findViewById(R.id.textfiled_content);
		mInfo = (TextView)findViewById(R.id.textfield_info);
		setContent(mContentString, mInfoString);
		
		// Set gravity.
		if( mGravity > 0 ) {
			mContent.setGravity(mGravity);
			mInfo.setGravity(mGravity);
		}
		
		if( mColor != 0 ) {
			mContent.setTextColor(mColor);
		}
		
		if( mCaptionSize !=0 ) {
			mCaption.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCaptionSize);
		}
		
		if( mContentSize != 0 ) {
			mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentSize);
		}
		
		mIconRight = (ImageView)findViewById(R.id.textfield_drawable_right);
		if( mDrawableId > 0 ) {
			mIconRight.setImageResource(mDrawableId);
			mIconRight.setVisibility(View.VISIBLE);
		} else {
			mIconRight.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void parseAttrs(Context aContext, TypedArray aArray) {
		// Parse attributes.
		mCaptionString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_caption);
		mContentString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_text);
		mInfoString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_info);
		mDrawableId = UiUtils.getResId(aContext, aArray, R.styleable.xingy_attrs_drawableRight);
		mColor = UiUtils.getColor(aContext, aArray, R.styleable.xingy_attrs_contentColor);
		mGravity = UiUtils.getInteger(aContext, aArray, R.styleable.xingy_attrs_contentGravity);
		mContentSize = UiUtils.getDimension(aContext, aArray, R.styleable.xingy_attrs_contentSize);
		mCaptionSize = UiUtils.getDimension(aContext, aArray, R.styleable.xingy_attrs_captionSize);
		
	}
	
	public void setCaption(final String ainfo)
	{
		mContentString = ainfo;
		if(!TextUtils.isEmpty(mContentString))
		{
			mCaption.setText(mCaptionString);
			mCaption.invalidate();
		}
	}
	
	private LinearLayout mContentLL;
	private String       mCaptionString;
	private TextView     mCaption;
	private TextView     mSubCaption;
	private String       mContentString;
	private TextView     mContent;
	private String       mInfoString;
	private TextView     mInfo;
	private int          mDrawableId;
	private ImageView    mIconRight;
	private int          mGravity;
	private int          mColor;
	private float        mContentSize;
	private float        mCaptionSize;
}
