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
import android.graphics.Bitmap;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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


	public void setContent(Spanned content) {
		mContent.setText(content);
	}
	
	
	/**
	 * @param content
	 */
	public void setContent(String content) {
		if(!TextUtils.isEmpty(content) ) {
            mContentString = content;
            mContent.setText(content);
            mContent.setVisibility(View.VISIBLE);
        }
        else
            mContent.setVisibility(View.GONE);
	}


	@Override
	protected void onInit(Context context) {
		// Get children components.
//		mContentLL = (LinearLayout) findViewById(R.id.textfield_content_layout);
        mPreIcon = (ImageView)findViewById(R.id.left_drawable);
        mPreIconLayout = findViewById(R.id.pre_icon_layout);
        mPreNetIcon = (CircleImageView)findViewById(R.id.left_net_drawable);

        if(mPreDrawableRid!=0) {
            mPreIconLayout.setVisibility(VISIBLE);
            mPreIcon.setImageResource(mPreDrawableRid);
        }
        else {
            mPreIconLayout.setVisibility(View.GONE);
        }


        mCaption = (TextView)findViewById(R.id.textfield_caption);
		if(TextUtils.isEmpty(mCaptionString))
            mCaption.setVisibility(View.GONE);
        else {
            mCaption.setVisibility(View.VISIBLE);
            mCaption.setText(mCaptionString);
        }

        mContent = (TextView)findViewById(R.id.textfiled_content);
        setContent(mContentString);
		

		if( mColor != 0 ) {
			mContent.setTextColor(mColor);
		}
		
		if( mCaptionSize !=0 ) {
			mCaption.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCaptionSize);
		}
		
		if( mContentSize != 0 ) {
			mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentSize);
		}
		
		mRightIcon = (ImageView)findViewById(R.id.textfield_drawable_right);
		if( mRightDrawableId > 0 ) {
			mRightIcon.setImageResource(mRightDrawableId);
			mRightIcon.setVisibility(View.VISIBLE);
		} else {
			mRightIcon.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void parseAttrs(Context aContext, TypedArray aArray) {
		// Parse attributes.
		mCaptionString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_caption);
		mContentString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_text);
        mRightDrawableId = UiUtils.getResId(aContext, aArray, R.styleable.xingy_attrs_drawableRight);
		mColor = UiUtils.getColor(aContext, aArray, R.styleable.xingy_attrs_contentColor);
		mContentSize = UiUtils.getDimension(aContext, aArray, R.styleable.xingy_attrs_contentSize);
		mCaptionSize = UiUtils.getDimension(aContext, aArray, R.styleable.xingy_attrs_captionSize);
        mPreDrawableRid = UiUtils.getResId(aContext, aArray, R.styleable.xingy_attrs_drawableLeft);
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

    public void setPreNetIconUrl(String url,ImageLoader imgloader)
    {
        mPreIconLayout.setVisibility(View.VISIBLE);
        mPreNetIcon.setUseShader(true);
        mPreNetIcon.setImageUrl(url, imgloader);
        mPreNetIcon.requestLayout();
    }


    public void setPreIconBitmap(Bitmap bm)
    {
        mPreIconLayout.setVisibility(View.VISIBLE);
        mPreNetIcon.setImageBitmap(bm);
    }

	
//	private LinearLayout mContentLL;
	private String       mCaptionString;
	private TextView     mCaption;

    private View         mPreIconLayout;
    private ImageView    mPreIcon;
    private CircleImageView mPreNetIcon;
    private int          mPreDrawableRid;
	private String       mContentString;
	private TextView     mContent;
	private int          mRightDrawableId;
	private ImageView    mRightIcon;
	private int          mColor;
	private float        mContentSize;
	private float        mCaptionSize;
}
