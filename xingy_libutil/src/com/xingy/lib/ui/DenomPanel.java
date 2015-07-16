/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: DenomPanel.java
 * 
 * Description: 
 * Author: xingyao (xingyao@tencent.com)
 * Created: Jul 03, 2013
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.xingy.R;

import java.util.ArrayList;
import java.util.List;


public class DenomPanel extends UiBase implements OnClickListener {
	/**
	 * onDenomSelectListener
	 * @author xingyao
	 *
	 */
	public interface OnDenomSelectListener {
		/**
		 * onDenomSelect
		 * @param nIndex
		 */
		public abstract void onDenomSelect(int nDenom);
	}
	
	/**
	 * Constructor of DenomPanel
	 * @param context
	 * @param attrs
	 */
	public DenomPanel(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.view_denom_panel);
	}
	
	public int getDenom() {
		return mCurrent >= 0 ? mDenoms[mCurrent] : 0;
	}
	
	public void setOnDenomSelectListener(OnDenomSelectListener listener) {
		mListener = listener;
	}

	@Override
	protected void onInit(Context aContext) {
		if( null == mOptions )
			mOptions = new ArrayList<DenomText>(6);
		mCurrent = 3;
		
		final int aResIds[] = {R.id.denom_option_1, R.id.denom_option_2, R.id.denom_option_3, R.id.denom_option_4, R.id.denom_option_5, R.id.denom_option_6};
		final int nLength = aResIds.length;
		for( int nIdx = 0; nIdx < nLength; nIdx++ ) {
			DenomText pOption = (DenomText)findViewById(aResIds[nIdx]);
			pOption.setOnClickListener(this);
			if( nIdx == mCurrent ) {
				pOption.setSelected(true);
			}
			mOptions.add(pOption);
		}
	}
	
	@Override
	public void onClick(View v) {
		final int nPos = mOptions.indexOf(v);
		if( nPos != mCurrent )
		{
			// Remove previous content.
			if( mCurrent >= 0 ) {
				DenomText pPrev = mOptions.get(mCurrent);
				pPrev.setSelected(false);
			}
			
			DenomText pCurrent = (DenomText)v;
			pCurrent.setSelected(true);
			
			// Update current index.
			mCurrent = nPos;
			
			// Notify the event for selection
			if( null != mListener ) {
				mListener.onDenomSelect(mDenoms[mCurrent]);
			}
		}
	}
	
	private List<DenomText>  mOptions;
	private int              mCurrent;
	private OnDenomSelectListener mListener;
	private static final int mDenoms[] = {10, 20, 30, 50, 100, 300};
}
