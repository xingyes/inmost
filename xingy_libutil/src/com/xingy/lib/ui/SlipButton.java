/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: FilterCategoryActivity.java
 * 
 * Description: 
 * Author: qingliang (qingliang@tencent.com)
 * Created: June 20, 2013
 * 
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.xingy.R;

public class SlipButton extends View implements OnTouchListener  {
	private boolean mNowChoose;
	private boolean mOnSlip;
	private float mDownX;
	private float mNowX;
	private Rect mButtonOn;
	private Rect mButtonOff;
	private OnChangedListener mOnChangedListener;
	private boolean isChangedListenerOn;
	private Bitmap mBackgroundOn;
	private Bitmap mBackgroundOff;
	private Bitmap mSlipButton;
	private Matrix mMatrix;
	private Paint mPaint;
	
	public SlipButton(Context context){
		super(context);
		init();
	}
	
	public SlipButton(Context context, AttributeSet attrs){
		super(context, attrs);
		init();
	}
	
	public boolean isChecked(){
		return mNowChoose;
	}
	
	public void setChecked(boolean check){
		mNowChoose = check;
		invalidate();
	}
	
	private void init() {
		mNowChoose = false;
		mOnSlip = false;
		isChangedListenerOn = false;
		
		mMatrix = new Matrix();
		mPaint = new Paint();
		
		mBackgroundOn = BitmapFactory.decodeResource(getResources(), R.drawable.vp_btn_press);
		mBackgroundOff = BitmapFactory.decodeResource(getResources(), R.drawable.vp_btn_nor);
		mSlipButton = BitmapFactory.decodeResource(getResources(), R.drawable.radio_on);
		
		mButtonOn = new Rect(0, 0, mSlipButton.getWidth(), mSlipButton.getHeight());
		mButtonOff = new Rect(mBackgroundOff.getWidth()-mSlipButton.getWidth(), 0, mBackgroundOff.getWidth(), mSlipButton.getHeight());
		
		setOnTouchListener(this);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if( null != mBackgroundOn ) {
			final int nWidth = mBackgroundOn.getWidth();
			final int nHeight = mBackgroundOn.getHeight();
			
			this.setMeasuredDimension(nWidth, nHeight);
			
			ViewGroup.LayoutParams pParams = this.getLayoutParams();
			if( null != pParams ) {
				pParams.width = nWidth;
				pParams.height = nHeight;
				this.setLayoutParams(pParams);
			}
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		float x = 0;
		if( (mNowX < (mBackgroundOn.getWidth()/2)) && !mNowChoose){
			canvas.drawBitmap(mBackgroundOff, mMatrix, mPaint);
		}else{
			canvas.drawBitmap(mBackgroundOn, mMatrix, mPaint);
		}
		
		if(mOnSlip) {
			if( mNowX >= mBackgroundOn.getWidth()) {
				x = mBackgroundOn.getWidth() - mSlipButton.getWidth()/2 ;
			}else{
				x = mNowX - mSlipButton.getWidth()/2;
			}
		}else{
			if(mNowChoose){
				x = mButtonOff.left;
			}else{
				x= mButtonOn.left;
			}
		}
		
		if(x < 0){
			x = 0;
		}else if(x > mBackgroundOn.getWidth() - mSlipButton.getWidth()){
			x = mBackgroundOn.getWidth() - mSlipButton.getWidth();
		}
		
		canvas.drawBitmap(mSlipButton, x, 0, mPaint);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			mNowX = event.getX();
			break;
		case MotionEvent.ACTION_DOWN:
			if(event.getX() > mBackgroundOn.getWidth() || event.getY() > mBackgroundOn.getHeight()){
				return false;
			}
			
			mOnSlip = true;
			mDownX = event.getX();
			mNowX = mDownX;
			break;
			
		case MotionEvent.ACTION_UP:
			mOnSlip = false;
			boolean lastChoose = mNowChoose;
			if(event.getX() >= mBackgroundOn.getWidth() / 2){
				mNowChoose = true;
			}else {
				mNowChoose = false;
			}
			
			if(isChangedListenerOn && (lastChoose != mNowChoose)) {
				mOnChangedListener.OnChanged(mNowChoose);
			}
		}
		
		invalidate();
		
		return true;
	}
	
	public void setOnChangedListener(OnChangedListener listener){
		isChangedListenerOn = true;
		mOnChangedListener = listener;
	}
	
	public interface OnChangedListener{
		abstract void OnChanged(boolean checkState);
	}

}


