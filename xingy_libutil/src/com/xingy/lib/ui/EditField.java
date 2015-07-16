/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: EditField.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: May 30, 2013
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingy.R;

public class EditField extends UiBase {
	/**
	 * Constructor of EditField
	 * @param context
	 * @param attrs
	 */
	public EditField(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.editfield_layout);
	}
	
	public void setCaption(String strCaption) {
		if( null != mCaption )
			mCaption.setText(strCaption);
	}
	
	public void setContent(String strContent) {
		if( null != mContent ) {
			mContent.setText(strContent);
			if( (mMinLines > 1) && (!TextUtils.isEmpty(strContent)) ) {
				mContent.setSelection(strContent.length());
			}
		}
	}
	
	public void setContent(Spanned strContent) {
		if( null != mContent )
			mContent.setText(strContent);
	}
	
	public String getContent() {
		return getContent(true);
	}
	
	public String getContent(boolean autoTrim) {
		String strContent = (null != mContent ? mContent.getEditableText().toString() : "");
		if( autoTrim )
			strContent = strContent.trim();
		
		return strContent;
	}
	
	public void setOnDrawableRightClickListener(OnClickListener listener) {
		if( null != mContent && mHasRightDrawable ) {
			mDrawableClickListener = listener;
			mButton.setOnClickListener( mDrawableClickListener);
			mContent.setOnClickListener(mDrawableClickListener);
		}
	}
	
	
	@Override
	protected void onInit(Context aContext) {
		mContentPostFix = (TextView)findViewById(R.id.editfield_postfix);
		if(TextUtils.isEmpty(mRightPostfixString))
		{
			mContentPostFix.setVisibility(View.GONE);
		}
		else
		{
			mContentPostFix.setVisibility(View.VISIBLE);
			mContentPostFix.setText(mRightPostfixString);
		}
		// Get children components.
		mCaption = (TextView)findViewById(R.id.editfield_caption);
		mCaption.setText(mCaptionString);
		mContent = (EditText)findViewById(R.id.editfield_content);
		mButton = (ImageView) findViewById(R.id.editfield_button);
		if( !TextUtils.isEmpty(mHintString) ) 
			mContent.setHint(mHintString);
		
		if( !TextUtils.isEmpty(mContentString) )
			mContent.setText(mContentString);
		
		if( !mHasRightDrawable ) {
			mButton.setVisibility(View.GONE);
		}else{
			final int nLeft = mContent.getPaddingLeft();
			final int nTop = mContent.getPaddingTop();
			final int nRight = mContent.getPaddingRight();
			final int nBottom = mContent.getPaddingBottom();
			//mContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_bg_no_right));
			
			// Restore the padding.
			mContent.setPadding(nLeft, nTop, nRight, nBottom);
		}
		
		if( !mEditable ) {
			mContent.setKeyListener(null);
		}
		
		// Update configuration.
		setConfig(mMinLines, mMaxLines, mMaxLength);
	}
	
	
	public void setRightPostFix(String content)
	{
		mRightPostfixString = content;
		if(TextUtils.isEmpty(mRightPostfixString))
		{
			mContentPostFix.setVisibility(View.GONE);
		}
		else
		{
			mContentPostFix.setVisibility(View.VISIBLE);
			mContentPostFix.setText(mRightPostfixString);
		}
	}
	@Override
	protected void parseAttrs(Context aContext, TypedArray aArray) {
		// Parse attributes.
		mCaptionString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_caption);
		mContentString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_text);
		mHintString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_hint);
		mHasRightDrawable = UiUtils.getBoolean(aContext, aArray, R.styleable.xingy_attrs_hasDrawableRight);
		mEditable = UiUtils.getBoolean(aContext, aArray, R.styleable.xingy_attrs_editable);
		mMinLines = UiUtils.getInteger(aContext, aArray, R.styleable.xingy_attrs_minLines);
		mMaxLines = UiUtils.getInteger(aContext, aArray, R.styleable.xingy_attrs_maxLines);
		mMaxLength = UiUtils.getInteger(aContext, aArray, R.styleable.xingy_attrs_maxLength);
		
		mRightPostfixString = UiUtils.getString(aContext, aArray, R.styleable.xingy_attrs_editPostfix);
		
	}
	
	private void setConfig(int nMinLines, int nMaxLines, int nMaxLength) {
		if( nMinLines > 0 ) {
			mContent.setMinLines(nMinLines);
			mContent.setSingleLine(false);
			mContent.setGravity(Gravity.TOP | Gravity.LEFT);
			
			// Height restore to wrap content if minimal lines required.
			ViewGroup.LayoutParams pParams = mContent.getLayoutParams();
			pParams.height = LayoutParams.WRAP_CONTENT;
			mContent.setLayoutParams(pParams);
		}
		
		if( nMaxLines > 0 && nMaxLines > nMinLines )
			mContent.setMaxLines(nMaxLines);
		
		if( nMaxLength > 0 ) {
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(nMaxLength);
			mContent.setFilters(FilterArray);
		}
	}
	
	/**
	 * @param aView
	 * @param aDrawable
	 * @param aEvent
	 * @return
	 */
	public static boolean isRightDrawableClicked(View aView, Drawable aDrawable, MotionEvent aEvent) {
		if( null == aDrawable || MotionEvent.ACTION_DOWN != aEvent.getAction() )
			return false;
		
		final int x = (int)aEvent.getX() + aView.getLeft();
		final int y = (int)aEvent.getY();
		final Rect rect = aDrawable.getBounds();
		if( null == rect )
			return false;
		
		final int width = rect.width();
		rect.right = aView.getRight() - aView.getPaddingRight();;
		rect.top = aView.getTop() + aView.getPaddingTop();
		rect.bottom = aView.getBottom() - aView.getPaddingBottom();
		rect.left = rect.right - width;
		
		return rect.contains(x, y);
	}
		
	public void setEditInputType(int type)
	{
		if(null!=mContent)
			mContent.setInputType(type);
	}
//	private class RightDrawableOnClickListener implements OnTouchListener {
//		RightDrawableOnClickListener(EditText aEditText) {
//			super();
//			final Drawable[] drawables = aEditText.getCompoundDrawables();
//			mRight = (null != drawables && drawables.length > 2 ? drawables[2] : null);
//		}
//
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//			if( null == mDrawableClickListener )
//				return false;
//			boolean bClicked = false;
//			if( (null != mRight) && (MotionEvent.ACTION_DOWN == event.getAction()) ) {
//				bClicked = mEditable ? isRightDrawableClicked(v, mRight, event) : true;
//			}
//			
//			if( bClicked )
//				mDrawableClickListener.onRightDrawableClick();
//			
//			return false;
//		}
//		
//		private Drawable mRight = null;
//	}
	
	private TextView     mContentPostFix;
	private TextView     mCaption;
	private EditText     mContent;
	private ImageView	 mButton;
	private boolean      mHasRightDrawable;
	private boolean      mEditable;
	private String       mCaptionString;
	private String       mContentString;
	private String       mRightPostfixString;
	private String       mHintString;
	private int          mMinLines;
	private int          mMaxLines;
	private int          mMaxLength;
	private OnClickListener mDrawableClickListener = null;
	/**  
	* method Name:setTagAtDrawableRight    
	* method Description:  
	* @param position   
	* void  
	* @exception   
	* @since  1.0.0  
	*/
	public void setTagAtDrawableRight(int position) {
		if(null!=mButton)
			mButton.setTag(position);
	}
}
