/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: ClockView.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: Jun 19, 2013
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xingy.R;


public class ClockView extends UiBase implements Runnable {
	private TextView[] mValues;
	private long mOffset;
	private long mEndSecs;
	private Runnable mCallback;
	private static final int ITEM_COUNT = 6;
	private static final long[] UNITS = { 60, 60, 24 };
	private static final long[] TIME_OFFSTE = new long[UNITS.length + 1];
	
	/**
	 * @param context
	 * @param attrs
	 */
	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.view_timing);
	}
	
	/**
	 * @param nCurrentSecs
	 * @param nEndSecs
	 */
	public void setTiming(long nCurrentSecs, long nEndSecs, Runnable aCallback) {
		mOffset = (System.currentTimeMillis() / 1000 - nCurrentSecs);
		mEndSecs = nEndSecs;
		mCallback = aCallback;
		
		// Check whether to start now.
		this.process();
	}
	
	private void setHours(long nHours) {
		this.setValue(nHours, 0);
	}
	
	private void setMinutes(long nMinutes) {
		this.setValue(nMinutes, 2);
	}
	
	private void setSeconds(long nSeconds) {
		this.setValue(nSeconds, 4);
	}
	
	/**
	 * setValue
	 * @param nValue
	 */
	private void setValue(long nValue, int nOffset) {
		nValue = nValue % 100;
		
		// Calculate ten value.
		TextView pView = mValues[nOffset];
		pView.setText("" + (nValue / 10));
		// Set single value.
		pView = mValues[nOffset + 1];
		pView.setText("" + (nValue % 10));
	}
	
	private void process() {
		final long nCurrent = System.currentTimeMillis() / 1000 - mOffset;
		long nLeft = mEndSecs - nCurrent;
		
		if (nLeft <= 0) {
			if (mCallback != null) {
				mCallback.run();
			}
			TIME_OFFSTE[0] = TIME_OFFSTE[1] = TIME_OFFSTE[2] = TIME_OFFSTE[3] = 0;
		} else {
			for (int i = 0, len = UNITS.length; i < len; i++) {
				TIME_OFFSTE[i] = nLeft % (UNITS[i]);
				nLeft = (nLeft - TIME_OFFSTE[i]) / UNITS[i];
			}
			TIME_OFFSTE[UNITS.length] = nLeft;
			postDelayed(this, 1000);
		}
		
		// Update time value.
		this.setHours(TIME_OFFSTE[2]);
		this.setMinutes(TIME_OFFSTE[1]);
		this.setSeconds(TIME_OFFSTE[0]);
	}
	
	@Override
	public void run() {
		this.process();
	}

	@Override
	protected void onInit(Context context) {
		mValues = new TextView[ITEM_COUNT];
		final int aResId[] = {R.id.timing_hours_ten, R.id.timing_hours_single, R.id.timing_minutes_ten, R.id.timing_minutes_single, R.id.timing_seconds_ten, R.id.timing_seconds_single};
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/alphabet.ttf");
		for( int nIdx = 0; nIdx < ITEM_COUNT; nIdx++ ) {
			TextView entity = (TextView)findViewById(aResId[nIdx]);
			entity.setTypeface(typeFace);
			mValues[nIdx] = entity;
		}
	}
}
