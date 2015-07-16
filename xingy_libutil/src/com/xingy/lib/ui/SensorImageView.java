package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SensorImageView extends ImageView {

	public SensorImageView(Context context) {
		super(context);
		initData();
	}

	public SensorImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData();
	}

	public SensorImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData();
	}

	private void initData() {

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
/*		if (changed == true) {
			mViewWidth = right - left;
			mViewHeight = bottom - top;
			if (mBitmap != null && !mBitmap.isRecycled()) {
				float sx = 1;
				float sy = 1;
				if (mBitmap.getWidth() > 0) {
					sx = (float) (mViewWidth) / mBitmap.getWidth();
				}
				if (mBitmap.getHeight() > 0) {
					sy = (float) (mViewHeight) / mBitmap.getHeight();
				}
				mScale = (sx < sy) ? sx : sy;
				mInitScale = mScale;
				resizeBitmap();
			}
		}*/
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {  
		
	//	Animation mAnimation = new DecelerateAnimation();
/*		Drawable drawable = getDrawable();
		drawable.draw(canvas);
		canvas.draw
		super.onDraw(canvas);
		try
		{
	    	float x = 0,y = 0;
	    	int width = getWidth();
	    	int height = getHeight();
	    	if(mResizedWidth < width)
	    	{
	    		x = (width - mResizedWidth)/2;
	    	}
	    	if(mResizedHeight < height)
	    	{
	    		y = (height - mResizedHeight)/2;
	    	}
	    	if(mResizeBitmap != null){
	    		canvas.drawBitmap(mResizeBitmap, x, y, null);
	    	}
		}
		catch(Exception ex){
			TiebaLog.e("DragImageView", "onDraw", "error = " + ex.getMessage());
		}*/
	}
	

}
