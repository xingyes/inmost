/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: icson
 * FileName: ItemImageView.java
 * 
 * Description: 
 * Author: xingyao (xingyao@tencent.com)
 * Created: 2013-3-29
 */
package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

/**
 * 
*   
* Class Name:ImageGallery 
* Class Description: 
* Author: xingyao 
* Modify: xingyao 
* Modify Date: 2013-3-29 下午03:43:46 
* Modify Remarks: 
* @version 1.0.0
*
 */
public class
        ImageGallery extends Gallery {
	private GestureDetector gestureScanner;
	private static float popBackDuration = 50f;
	private static int heightPopMargin = 50;
	private ItemImageView imageView;

	public static float  screenWidth;
	public static float  screenHeight;
	
	/**
	 * 
	* Create a new Instance ImageGallery.  
	*  
	* @param context
	 */
	public ImageGallery(Context context) {
		super(context);
	}

	/**
	 * 
	* Create a new Instance ImageGallery.  
	*  
	* @param context
	* @param attrs
	* @param defStyle
	 */
	public ImageGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	public void setWidthHeight(float w, float h)
	{
		screenWidth = w;
		screenHeight = h;
	}
	/**
	 * 
	* Create a new Instance ImageGallery.  
	*  
	* @param context
	* @param attrs
	 */
	public ImageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.setLongClickable(false);
		
		gestureScanner = new GestureDetector(new doubleTabDetector());//new GestureDetector(this);
		this.setOnTouchListener(new OnTouchListener() {
			//两手指之间的距离
			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = ImageGallery.this.getSelectedView();
				if (view instanceof ItemImageView) {
					imageView = (ItemImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						//2 points --> Zoom
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							//distance
							float value = (float) Math.sqrt(x * x + y * y);

							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;
								// scale the image
								imageView.zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));
							}
						}
					}
				}
				return false;
			}

		});
	}
	
	@Override
	public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
		return false;
	}
	/**
	 *  inside  ItemImageView : shift pic
	 *  otherwise ImageGallery.scroll
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		View view = ImageGallery.this.getSelectedView();
		if (view instanceof ItemImageView) {
			imageView = (ItemImageView) view;

			//get matrix []
			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			
			float left, right;
			float top, bottom;
			//get current width & height
			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();
			
			//inner just scroll gallery
			if ((int) width <= screenWidth && (int) height <= screenHeight)
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			}
			else
			{
				left = v[Matrix.MTRANS_X];
				top = v[Matrix.MTRANS_Y];
				right = left + width;
				bottom = top + height;
				float adjustDistanceY =  distanceY/imageView.getScale();
				if (distanceX > 0)//scroll to left,next
				{
					//no part out of sight.
					//left || right
					if (right < screenWidth)
					{
						super.onScroll(e1, e2, distanceX, distanceY);
					}
					else
					{
						if(adjustDistanceY < 0) //down
						{
							if((int)(top - adjustDistanceY) >= heightPopMargin)
							{
								adjustDistanceY =  top - heightPopMargin;// - top;
							}
						}
						else//up
						{
							if((int)(bottom - adjustDistanceY) <= screenHeight - heightPopMargin)
							{
								adjustDistanceY =  bottom - screenHeight + heightPopMargin;
							}
						}
						if((int) height <= screenHeight - heightPopMargin*2)
						{
							adjustDistanceY = 0;
						}
						imageView.postTranslate(-distanceX/imageView.getScale(), -adjustDistanceY);
						//android.util.Log.i("distanceY",""+ adjustDistanceY);
					}
				}
				else if (distanceX < 0)// to left,previous
				{
					if (left > 0)// || right > ItemImageActivity.screenWidth)
					{
						super.onScroll(e1, e2, distanceX, distanceY);
					}
					else
					{
						if(adjustDistanceY < 0) //down
						{
							if((int)(top - adjustDistanceY) >= heightPopMargin)
							{
								adjustDistanceY =  top - heightPopMargin;
							}
						}
						else//up
						{
							if((int)(bottom - adjustDistanceY) <= screenHeight - heightPopMargin)
							{
								adjustDistanceY =  bottom - screenHeight + heightPopMargin;
							}
						}
						if((int) height <= screenHeight - heightPopMargin*2)
						{
							adjustDistanceY = 0;
						}
						
					imageView.postTranslate(-distanceX/imageView.getScale(), -adjustDistanceY);
					//android.util.Log.i("Top Bottom",""+ top + "," + bottom);
					//android.util.Log.i("distanceY",""+ adjustDistanceY);
					}
				}
			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}
	
		@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			//deal with image beyond top & bottom frame. Pop back
			View view = ImageGallery.this.getSelectedView();
			if (view instanceof ItemImageView) {
				imageView = (ItemImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				if ((int) width <= screenWidth && (int) height <= screenHeight)// 濡傛灉鍥剧墖褰撳墠澶у皬<灞忓箷澶у皬锛屽垽鏂竟鐣�
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				if(height <=screenHeight)
				{
					if (bottom > screenHeight)
						imageView.postTranslateDur(screenHeight - bottom, popBackDuration);
					else if (top < 0) 
						imageView.postTranslateDur(-top, popBackDuration);
				}
				else
				{
					if (top > 0) 
						imageView.postTranslateDur(-top, popBackDuration);
					else if (bottom < screenHeight) 
						imageView.postTranslateDur(screenHeight - bottom, popBackDuration);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 
	*   
	* Class Name:doubleTabDetector 
	* Class Description: Double tap -->zoom*2;Double tap--> zoom*1 
	* Author: xingyao 
	* Modify: xingyao 
	* Modify Date: 2013-4-1 下午03:41:19 
	* Modify Remarks: 
	* @version 1.0.0
	*
	 */
	private class doubleTabDetector extends SimpleOnGestureListener
	{
		@Override
		public boolean onDoubleTap (MotionEvent e) 
		{
			View view = ImageGallery.this.getSelectedView();
			if (view instanceof ItemImageView)
			{
				imageView = (ItemImageView) view;
				float scaleNow = imageView.getScale();
				if(scaleNow >= ItemImageView.defaultZoomOutRate)
					scaleNow = imageView.caculateRate();
				else
					scaleNow = ItemImageView.defaultZoomOutRate;
				imageView.zoomTo(scaleNow, e.getX(), e.getY());
				return true;
			}
			return false;
		}
		
	}
	
}
