package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FillWidthImageView extends ImageView {
	public FillWidthImageView(Context context) {
		super(context);
	}
	
	public FillWidthImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FillWidthImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		if ( (mReferWidth > 0) && (mReferHeight > 0) ) {
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			final int nHeight = width * mReferHeight / mReferWidth;
			this.setMeasuredDimension(width, nHeight);
			ViewGroup.LayoutParams params = this.getLayoutParams();
			if( null != params ) {
				params.width = width;
				params.height = nHeight;
				this.setLayoutParams(params);
			}
		}		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	
	protected int mReferWidth = 82;
	protected int mReferHeight = 640;
}
