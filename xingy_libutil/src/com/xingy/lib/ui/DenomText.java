package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xingy.R;
import com.xingy.util.ImageHelper;

public class DenomText extends TextView {
	public DenomText(Context context) {
		super(context);
		init();
	}

	public DenomText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DenomText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Draw the tick if necessary.
		if( mSelected && null != mSelect ) {
			final int nWidth = this.getWidth();
			final int nHeight = this.getHeight();
			mDest.bottom = nHeight;
			mDest.right = nWidth;
			mDest.left = nWidth - mSelect.getWidth();
			mDest.top = nHeight - mSelect.getHeight();
			canvas.drawBitmap(mSelect, null, mDest, mPaint);
		}
	}
	
	public void setSelected(boolean bSelected) {
		mSelected = bSelected;
		this.setBackgroundResource(mSelected ? R.drawable.denom_on_shape : R.drawable.denom_off_shape);
	}
	
	private void init() {
		mPaint = new Paint();
		mDest = new Rect();
		mSelect = ImageHelper.getResBitmap(this.getContext(), R.drawable.denom_select);
		
	}
	
	private Bitmap  mSelect;
	private boolean mSelected;
	private Paint   mPaint;
	private Rect    mDest;
}
