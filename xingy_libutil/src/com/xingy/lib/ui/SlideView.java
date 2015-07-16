/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: 51Buy
 * FileName: SlideView.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: Jun 12, 2013
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

import com.xingy.R;
import com.xingy.util.ImageLoadListener;
import com.xingy.util.ImageLoader;

import java.util.HashMap;

public class SlideView extends ViewFlipper implements ImageLoadListener {
	/**
	 * Slide event listener
	 * @author lorenchen
	 *
	 */
	public interface OnSlideEventListener
	{
		/**
		 * On item click.
		 *
		 * @param v the v
		 * @param index the index
		 */
		public abstract void onItemClick(View aView, int nIndex);
		
		/**
		 * @param nIndex
		 * @param nTotal
		 */
		public abstract void onPostionUpdate(int nIndex, int nTotal);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.parseAttrs(attrs);
	}
	
	/**
	 * start slide animation.
	 */
	public void startSlide() {
		final int nSize = this.getChildCount();
		if( nSize <= 1 )
			return ;
		
		if( null == mRunnable ) {
			mRunnable = new Runnable(){
				@Override
				public void run() {
					animToNext(false);
				}
			};
		}
		
		mHandler.postDelayed(mRunnable, INTERVAL);
	}
	
	public void stopSlide() {
		if( (null != mHandler) && (null != mRunnable) ) {
			mHandler.removeCallbacks(mRunnable);
		}
	}
	
	/**
	 * @param listener
	 */
	public void setOnSlideEventListener(OnSlideEventListener listener) {
		mEventListener = listener;
	}
	
	/**
	 * addImageView to slideview
	 * @param strPicUrl
	 */
	public void addImageView(String strPicUrl, int nPos) {
		Context pContext = this.getContext();
		ImageView pImageView = new ImageView(pContext);
		pImageView.setScaleType(ScaleType.FIT_XY);
		
		// Load image.
		UiUtils.loadImage(pImageView, strPicUrl, mLoader, mHashMap, this, R.drawable.banner_loading);
		
		this.addChild(pImageView, nPos);
	}
	
	/**
	 * 
	 * @param aView
	 * @param nIndex
	 */
	private void addChild(View aView, int nIndex) {
		if( null == aView || 0 > nIndex )
			return ;
		
		// Update layout for image.
	//	aView.setLayoutParams(mParams);
		super.addView(aView, nIndex);
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
	
	@Override
	public void onLoaded(Bitmap aBitmap, String strUrl) {
		UiUtils.updateImage(aBitmap, strUrl, mHashMap);
	}

	@Override
	public void onError(String strUrl) {
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}
	
	@Override    
	public boolean onTouchEvent(MotionEvent motion) {
		return null != mDetector ? mDetector.onTouchEvent(motion) : false;
	}
	
	protected void parseAttrs(AttributeSet attrs) {
		// Set the animation type.
		Context context = getContext();
		
		// Load animation information.
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.xingy_attrs);
		final int nNextIn = UiUtils.getResId(context, array, R.styleable.xingy_attrs_animNextIn, android.R.anim.slide_in_left);
		mNextIn = AnimationUtils.loadAnimation(context, nNextIn);
		mNextIn.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				if( null != mEventListener ) {
					mEventListener.onPostionUpdate(getDisplayedChild(), getChildCount());
				}
				mHandler.removeCallbacks(mRunnable);
				mHandler.postDelayed(mRunnable, INTERVAL);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		final int nNextOut = UiUtils.getResId(context, array, R.styleable.xingy_attrs_animNextOut, android.R.anim.slide_out_right);
		mNextOut = AnimationUtils.loadAnimation(context, nNextOut);
		final int nPrevIn = UiUtils.getResId(context, array, R.styleable.xingy_attrs_animPrevIn, android.R.anim.slide_in_left);
		mPrevIn = AnimationUtils.loadAnimation(context, nPrevIn);
		mPrevIn.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				if( null != mEventListener ) {
					mEventListener.onPostionUpdate(getDisplayedChild(), getChildCount());
				}
				mHandler.removeCallbacks(mRunnable);
				mHandler.postDelayed(mRunnable, INTERVAL);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		final int nPrevOut = UiUtils.getResId(context, array, R.styleable.xingy_attrs_animPrevOut, android.R.anim.slide_out_right);
		mPrevOut = AnimationUtils.loadAnimation(context, nPrevOut);
		mReferWidth = UiUtils.getInteger(context, array, R.styleable.xingy_attrs_referWidth, 0);
		mReferHeight = UiUtils.getInteger(context, array, R.styleable.xingy_attrs_referHeight, 0);
		
		array.recycle();
	}
	
	/**
	 * initialize the SlideView instance.
	 */
	private void init() {
		// Set guest support.
		mDetector = new GestureDetector(new OnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if( Math.abs(velocityX) > mMinVelcocity ) {
					int currentIndex = getDisplayedChild();
					int maxPos = getChildCount() - 1;
					int minPos = 0;
					if( velocityX > 0 ) {
						if(currentIndex == minPos) {
							currentIndex = maxPos;
						} else {
							currentIndex = currentIndex - 1;
						}
						animToPrev(true);
					}
					else {
						if(currentIndex == maxPos) {
							currentIndex = minPos;
						} else {
							currentIndex = currentIndex + 1;
						}
						animToNext(true);
					}
					//�??????��?��?????�????�???��?��??
					if( null != mEventListener ) {
						mEventListener.onPostionUpdate(currentIndex, getChildCount());
					}
					
					return true;
				}
				return false;
			}
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return onItemClick();
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
			}
		});
		
		// Initialize the image loader for image loader.
		//mLoader = new ImageLoader(this.getContext(), Config.PIC_CACHE_DIR, true);
	}
	
	private void animToNext(boolean bRestart) {
		setInAnimation(mNextIn);
		setOutAnimation(mNextOut);
		
		showNext();
		
		if( bRestart )
			this.restart();
	}
	
	private void animToPrev(boolean bRestart) {
		setInAnimation(mPrevIn);
		setOutAnimation(mPrevOut);
		
		showPrevious();
		
		if( bRestart )
			restart();
	}
	
	private void restart() {
		stopSlide();
		startSlide();
	}
	
	/**
	 * onItemClick
	 * @return
	 */
	private boolean onItemClick() {
		if( null == mEventListener )
			return false;
		
		// Get current item
		View current = this.getCurrentView();
		if( null != current ) {
			final int nIndex = this.getDisplayedChild();
			mEventListener.onItemClick(current, nIndex);
			
			return true;
		}
		
		return false;
	}
	
	/**  
	* method Name:setHandler    
	* method Description:  
	* @param mWholeHandler   
	* void  
	* @exception   
	* @since  1.0.0  
	*/
	public void setHandler(Handler aWholeHandler) {
		mHandler = aWholeHandler;
	}
	
	public void setImageLoader(ImageLoader aLoader) {
		mLoader = aLoader;
	}
	
	protected int mReferWidth = 0;
	protected int mReferHeight = 0;
	private Animation mNextIn;
	private Animation mNextOut;
	private Animation mPrevIn;
	private Animation mPrevOut;
	private GestureDetector mDetector;
	private OnSlideEventListener mEventListener;
	private ImageLoader mLoader;
	private HashMap<String, ImageView> mHashMap = new HashMap<String, ImageView>();

	private static int mMinVelcocity = 200;
	private Handler mHandler;
	private Runnable mRunnable = null;
	private static final int INTERVAL = 5000; // Auto scrolling every 5 seconds.
}
