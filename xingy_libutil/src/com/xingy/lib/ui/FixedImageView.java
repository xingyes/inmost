package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FixedImageView extends ImageView {
	public FixedImageView(Context context) {
		super(context);
	}
	
	public FixedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FixedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		final int width = MeasureSpec.getSize(widthMeasureSpec);
//		final int nHeight = width * Config.PROINFO_HEIGHT / Config.PROINFO_WIDTH;
		final int nHeight = width * 11 / 17;

		this.setMeasuredDimension(width, nHeight);
		ViewGroup.LayoutParams pParams = this.getLayoutParams();
		if( null != pParams ) {
			pParams.width = width;
			pParams.height = nHeight;
			this.setLayoutParams(pParams);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
