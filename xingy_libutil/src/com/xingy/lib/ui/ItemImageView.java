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
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**  
 *   
 * Class Name:ItemImageView 
 * Class Description: 
 * Author: xingyao 
 * Modify: xingyao 
 * Modify Date: 2013-3-29 下午04:20:19 
 * Modify Remarks: 
 * @version 1.0.0
 *   
 */
public class ItemImageView extends ImageView{

	private float scaleRate;	//放大倍数
	private int   imageWidth;	//图片宽度px
	private int   imageHeight;	//图片高度px
	private Bitmap oriBitmap; 	//原始图片
	//function Matrix
	private Matrix mSuppMatrix = new Matrix();
	
	/**
	 * 单位矩阵
	 *  | 1,0,0|
	 *  | 0,1,0|
	 *  | 0,0,1|
	 */
	private Matrix mBaseMatrix = new Matrix();
	
	/*
	 * final display matrix  mDisplayMatrix ＝ mBaseMatrix*mSuppMatrix
	 * 每次改变图片的时候，mDisplayMatrix先变成单位矩阵，然后再和mSuppMatrix相乘
	 */
	private Matrix mDisplayMatrix = new Matrix();
	
	private final float[] mMatrixValues = new float[9];
	
	public final static float defaultZoomOutRate = 2.0f; //默认放大倍数，用于双击
	private float maxRate = 8.0f;   //最大放大倍数
	private float minRate;			//最小放大倍数
	
	private Handler mHandler = new Handler();
	private float _dy;
	/**
	 * 
	* Create a new Instance ItemImageView.  
	*  
	* @param context
	 */
	public ItemImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	* Create a new Instance ItemImageView.  
	*  
	* @param context
	* @param attrs
	 */
	public ItemImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 
	}
	
	/**  
	* Create a new Instance ItemImageView.  
	*  
	* @param context
	* @param attrs
	* @param defStyle  
	*/
	public ItemImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void setImageBitmap (Bitmap bm) 
	{
		super.setImageBitmap(bm);
		oriBitmap = bm;
		
		if(oriBitmap != null) {

			this.imageHeight = oriBitmap.getHeight();
			this.imageWidth = oriBitmap.getWidth();
		}
		
		scaleRate = this.caculateRate();
		
		this.setScaleType(ScaleType.FIT_CENTER);
		this.zoomTo(scaleRate);//, ItemImageActivity.screenWidth / 2f, ItemImageActivity.screenHeight / 2f);
		
		layoutToCenter();
	}
	
	/**
	 * 
	* method Name:caculateRate    
	* method Description:  
	* @return   
	* float  
	* @exception   
	* @since  1.0.0
	 */
	public float caculateRate()
	{
		float scaleWidth = ImageGallery.screenWidth / (float) imageWidth;
		float scaleHeight = ImageGallery.screenHeight / (float) imageHeight;
		return Math.min(scaleWidth, scaleHeight);
		
	}
	/**  
	* method Name:getScale    
	* method Description:  
	* @return   
	* float  
	* @exception   
	* @since  1.0.0  
	*/
	public float getScale() {
		return getScale(mSuppMatrix);
	}


	/**  
	* method Name:getScale    
	* method Description:  
	* @param mSuppMatrix2
	* @return   
	* float  
	* @exception   
	* @since  1.0.0  
	*/
	private float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	/**  
	* method Name:getValue    
	* method Description:  
	* @param matrix
	* @param mscaleX
	* @return   
	* float  
	* @exception   
	* @since  1.0.0  
	*/
	private float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		minRate =( ImageGallery.screenWidth/2f)/imageWidth;
		
		return mMatrixValues[whichValue];
	}

	/**  
	* method Name:zoomTo    
	* method Description:  
	* @param f
	* @param g
	* @param h   
	* void  
	* @exception   
	* @since  1.0.0  
	*/
	public void zoomTo(float scale, float centerX, float centerY) {
		this.setScaleType(ScaleType.MATRIX);
		if (scale > maxRate) {
			scale = maxRate;
		} else if (scale < minRate) {
			scale = minRate;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		centerIfInside(true, true);
	}

	/**
	 * 
	* method Name:zoomTo    
	* method Description: scale at (0,0)  
	* @param scale   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void zoomTo(float scale) {
		this.setScaleType(ScaleType.MATRIX);
		if (scale > maxRate) {
			scale = maxRate;
		} else if (scale < minRate) {
			scale = minRate;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		//0,0 poi
		mSuppMatrix.postScale(deltaScale, deltaScale);
		setImageMatrix(getImageViewMatrix());
	}
	/**
	 * 图片居中
	* method Name:layoutToCenter    
	* method Description:     
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void layoutToCenter()
	{
		float width = imageWidth*getScale();
		float height = imageHeight*getScale();
		
		float fill_width = ImageGallery.screenWidth - width;
		float fill_height = ImageGallery.screenHeight - height;
		
		float tran_width = 0f;
		float tran_height = 0f;
		
		if(fill_width>0)
			tran_width = fill_width/2;
		if(fill_height>0)
			tran_height = fill_height/2;
			
		
		postTranslate(tran_width, tran_height);
		Log.i("tran_width,tran_height", "" + tran_width + "," + tran_height);
		setImageMatrix(getImageViewMatrix());
	}
	
	/**
	 * 
	 * 图片放大或者缩小后（可能被移到不居中位置），使图片移到居中位置
	* method Name:centerIfInside    
	* method Description:  
	* @param horizontal
	* @param vertical   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	private void centerIfInside(boolean horizontal, boolean vertical) {
		if (oriBitmap == null) {
			return;
		}

		Matrix m = getImageViewMatrix();

		//原始图片大小的矩形
		RectF rect = new RectF(0, 0, oriBitmap.getWidth(), oriBitmap.getHeight());
//		RectF rect = new RectF(0, 0, imageWidth*getScale(), imageHeight*getScale());

		//放大或缩小后图片大小的矩形
		m.mapRect(rect);

		//放大或缩小后图片的高和宽
		float height = rect.height();
		float width = rect.width();

		//水平（垂直）方向需要偏移量	
		float deltaX = 0, deltaY = 0;

		//垂直方向上计算偏移量
		if (vertical) {
			//ImageView的高度（对于商详大图，viewHeight＝屏幕分辨率高度）
			int viewHeight = getHeight();
			//放大或缩小后图片高度没有超过屏幕高度
			if (height < viewHeight) {
				deltaY = viewHeight / 2  - ((height) / 2 + rect.top);
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < viewHeight) {
				deltaY = getHeight() - rect.bottom;
			}
			// rect.top <=0 &&  rect.bottom >= viewHeight
			// do not need shift bitmap
		}

		//水平方向上计算偏移量
		if (horizontal) {
			//ImageView的宽度（对于商详大图，viewWidth＝屏幕分辨率宽度）
			int viewWidth = getWidth();
			//放大或缩小后图片宽度没有超过屏幕宽度
			if (width < viewWidth) {
				//水平方向上图片中心和屏幕中心的距离
				deltaX = viewWidth /2 - ((width) / 2 + rect.left);
			} else if (rect.left > 0) {
				//放大或缩小后图片宽度超过屏幕宽度，且图片左侧在屏幕内
				deltaX = -rect.left;
			} else if (rect.right < viewWidth) {
				//放大或缩小后图片宽度超过屏幕宽度，且图片左侧不在屏幕内，图片右侧在屏幕内
				deltaX = viewWidth - rect.right;
			}
			// rect.left <=0 && rect.right>= viewWidth
			// do not need shift bitmap
		}

		
		if(deltaX==0.0 && deltaY==0.0)
			return;
		
		postTranslate(deltaX, deltaY);
		setImageMatrix(getImageViewMatrix());
		
	}

	/**  
	* method Name:getImageViewMatrix    
	* method Description:  
	* @return   
	* Matrix  
	* @exception   
	* @since  1.0.0  
	*/
	private Matrix getImageViewMatrix() {
		// The final matrix is computed as the concatentation of the base matrix
		// and the supplementary matrix.
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
		 
	}

	/**  
	* method Name:getImageWidth    
	* method Description:  
	* @return   
	* float  
	* @exception   
	* @since  1.0.0  
	*/
	public float getImageWidth() {
		return imageWidth;
	}

	/**  
	* method Name:getImageHeight    
	* method Description:  
	* @return   
	* float  
	* @exception   
	* @since  1.0.0  
	*/
	public float getImageHeight() {
		return imageHeight;
	}

	/**  
	* method Name:postTranslate    
	* method Description:  
	* @param f
	* @param g   
	* void  
	* @exception   
	* @since  1.0.0  
	*/
	public void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
		
	}

	
	
	/**  
	* method Name:postTranslateDur    
	* method Description:  
	* @param f
	* @param popBackDuration   
	* void  
	* @exception   
	* @since  1.0.0  
	*/
	public void postTranslateDur(float dy, final float durationMs) {
		_dy=0.0f;
		final float incrementPerMs = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				
				postTranslate(0, incrementPerMs*currentMs-_dy);
				_dy=incrementPerMs*currentMs;

				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
		
	}
}
