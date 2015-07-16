package com.xingy.lib.ui;

/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: SettingCellView??Ω.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: Jun 17, 2013
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.xingy.R;

public class SettingCellView extends UiBase {
	/**
	 * Constructor of SettingCellView
	 * @param context
	 * @param attrs
	 */
	public SettingCellView(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.view_setting_cell);
	}
	
	/**
	 * @param strContent
	 */
	public void setContent(String strContent) {
		if( null != mContent ) {
			mContent.setText(strContent);
		}
	}
	
	@Override
	protected void onInit(Context aContext) {
		// Get children components.
		mCaption = (TextView)findViewById(R.id.setting_cell_caption);
		mCaption.setText(mCaptionString);
		mHint = (TextView)findViewById(R.id.setting_cell_hint);
		if( TextUtils.isEmpty(mHintString) ) {
			mHint.setVisibility(View.GONE);
		} else {
			mHint.setText(mHintString);
		}
		mContent = (TextView)findViewById(R.id.setting_cell_content);
	}
	
	@Override
	protected void parseAttrs(Context aContext, TypedArray aArray) {
		// Parse attributes.
		mCaptionString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_caption);
		mHintString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_hint);
	}

	private String   mCaptionString;
	private String   mHintString;
	private TextView mCaption;
	private TextView mHint;
	private TextView mContent;
}
