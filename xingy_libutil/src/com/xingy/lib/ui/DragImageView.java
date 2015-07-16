package com.xingy.lib.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import com.xingy.util.Log;

public class DragImageView extends View {
	private static final int DISAPPEAR_TIME = 2000;
	private Bitmap mBitmap = null;
	private Bitmap mResizeBitmap = null;
	private int mViewWidth = 0, mViewHeight = 0;
	private float mResizedWidth,mResizedHeight;
	private float mScale;
	private float mInitScale;
	private boolean mIsTouched = false;
	private OnSizeChangedListener mListener = null;
	private OnClickListener mClick = null;
	private DecelerateAnimation mAnimation;
	private GestureDetector mGestureDetector; 
	private Interpolator mDecelerateInterpolater = AnimationUtils.loadInterpolator(this.getContext(), android.R.anim.decelerate_interpolator);
	Handler mhandler = new Handler();
	
	public DragImageView(Context context) {
		super(context);
		initData();
	}
	
	public DragImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData();
	}
	public DragImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData();
	}
	
	/**
	 * ??��??resize�?????????��??
	 * @return
	 */
	public Bitmap getResizeBmp() {
		return mResizeBitmap;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(event);
	}

	Runnable runnable=new Runnable(){
		/**
		 * ??????�???��??
		 */
		@Override
		public void run() {
			setHorizontalScrollBarEnabled(false);
			setVerticalScrollBarEnabled(false); 
			invalidate();
		} 
	};
		
	/**
	 * �????�?�?�?�?
	 */
	@Override    
	public boolean onTouchEvent(MotionEvent me)
	{  
		if (mGestureDetector.onTouchEvent(me)){  
			return true;
		}    
		int action = me.getAction();      
		switch (action)
		{ 
		case MotionEvent.ACTION_DOWN:
	    	int width = getWidth();
	    	int height = getHeight();
	    	mhandler.removeCallbacks(runnable);
	    	
	    	if(mResizedWidth > width){
	    		setHorizontalScrollBarEnabled(true);
	    	}else{
	    		setHorizontalScrollBarEnabled(false);
	    	}
	    	
	    	if(mResizedHeight > height){
	    		setVerticalScrollBarEnabled(true);
	    	}else{
	    		setVerticalScrollBarEnabled(false);
	    	}
	    	
	    	invalidate();
	    	mIsTouched = true;
			break;
		case MotionEvent.ACTION_MOVE: 
			break;  
		case MotionEvent.ACTION_UP:  
			mhandler.removeCallbacks(runnable);
			mhandler.postDelayed(runnable, DISAPPEAR_TIME);
			mIsTouched = false;
			invalidate();  
			break;  
		} 
		return super.onTouchEvent(me);
	} 

	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		if(changed == true){
			mViewWidth = right - left;
			mViewHeight = bottom - top;
			if(mBitmap != null && !mBitmap.isRecycled()){
				float sx = 1;
				float sy = 1;
				if(mBitmap.getWidth() > 0){
					sx = (float)(mViewWidth)/mBitmap.getWidth();
				}
				if(mBitmap.getHeight() > 0){
					sy = (float)(mViewHeight)/mBitmap.getHeight();
				}
	    		mScale = (sx < sy) ? sx : sy;
	    		mInitScale = mScale;
				resizeBitmap();			
			}
			if(mListener != null){
				mListener.sizeChanged(canZoomIn(), canZoomOut());
			}
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	public void setOnSizeChangedListener(OnSizeChangedListener listener){
		mListener = listener;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {  
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
			Log.e("DragImageView", ex);
		}
	}

    private void initData()
    {
    	mResizedWidth = 0;
    	mResizedHeight = 0;
    	mScale = 1;
    	mInitScale = 1;
    	
    	mAnimation = new DecelerateAnimation();
    	setHorizontalFadingEdgeEnabled(false); 
    	setVerticalFadingEdgeEnabled(false); 
    	setHorizontalScrollBarEnabled(false);
	    setVerticalScrollBarEnabled(false); 
    	setWillNotDraw(false);
	  	this.scrollTo(0, 0);
		mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){  
			/**
			 * �????�?�?
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				if(Math.abs(velocityX) > 200 || Math.abs(velocityY) > 200){
					mAnimation.prepareAnimation(velocityX, velocityY);
					DragImageView.this.startAnimation(mAnimation);
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				if(mClick != null){
					mClick.onClick(DragImageView.this);
				}
				return super.onSingleTapUp(e);
			}

			@Override 
			public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) 
			{
				int sx = getScrollX();
				if(mResizedWidth > DragImageView.this.getWidth()) {
					sx += distanceX;
					if(sx < 0){  
						sx = 0;  
					}  
					if(sx + getWidth() > mResizedWidth) {                      
						sx = (int)(mResizedWidth - getWidth());  
					} 
				}
				int sy = getScrollY(); 
				if(DragImageView.this.mResizedHeight > DragImageView.this.getHeight()){
					sy += distanceY;
					if(sy < 0){  
						sy = 0;  
					}  
					if(sy + getHeight() > mResizedHeight) {                      
						sy = (int)(mResizedHeight - getHeight());
					} 
				}
				if(sx != getScrollX() || sy != getScrollY()){
					scrollTo(sx, sy);
					invalidate();
				}
				return true;
			}
		});
	}
    
	@Override
	protected int computeHorizontalScrollRange() {
		// TODO Auto-generated method stub
		return (int)mResizedWidth;
	}

	@Override
	protected int computeVerticalScrollRange() {
		// TODO Auto-generated method stub
		return (int)mResizedHeight;
	}

	/**
	 * ????????�件?????��??�?�?
	 */
	public void releaseBitmap(){	
		mBitmap = null;
		if(mResizeBitmap != null && !mResizeBitmap.isRecycled()){
			mResizeBitmap.recycle();
		}
		mResizeBitmap = null;
	}
	
	/**
	 * ??��????��??
	 * @return
	 */
	public Bitmap getImageBitmap(){
		return mBitmap;
	}
	
	/**
	 * 设置??��?��??
	 * @param bitmap
	 */
	public void setImageBitmap(Bitmap bitmap) {
		if(mAnimation.getIsAnimationInProgre()){
			mAnimation.stopAnimation();
		}
		releaseBitmap();
		if(bitmap != null && !bitmap.isRecycled()) {
	    	mBitmap = bitmap;
	    	if(mViewWidth !=0 && mViewHeight != 0){
	    		float sx = 1;
	    		float sy = 1;
	    		if(mBitmap.getWidth() > 0){
	    			sx = (float)(mViewWidth)/mBitmap.getWidth();
	    		}
	    		if(mBitmap.getHeight() > 0){
	    			sy = (float)(mViewHeight)/mBitmap.getHeight();
	    		}
	    		mScale = (sx > sy) ? sy:sx;
	    		mInitScale = mScale;
	    		resizeBitmap();
	    	}
		}else{
			mBitmap = null;
			mResizeBitmap = null;
			mResizedWidth = 0;
			mResizedHeight = 0;
			mScale = mInitScale = 1;
			mIsTouched = false;
		}
		if(mListener != null){
			mListener.sizeChanged(canZoomIn(), canZoomOut());
		}
		super.invalidate();
    	return;
    }
	
	/**
	 * ??�大??��??
	 */
	public boolean zoomInBitmap(){
		mScale = mScale*1.25f;
		resizeBitmap();
		return canZoomIn();
	}
	
	/**
	 * 缩�????��??
	 */
	public boolean zoomOutBitmap(){
		mScale = mScale * 0.8f;
		if(mScale < mInitScale){
			mScale = mInitScale;
		}
		resizeBitmap();
		return canZoomOut();
	}
	
	/**
	 * ??��??????????��?�大
	 * @return
	 */
	private boolean canZoomIn(){
		if(mBitmap != null && !mBitmap.isRecycled() && mResizeBitmap != null && !mResizeBitmap.isRecycled()){
			if(mResizeBitmap.getWidth() * mResizeBitmap.getHeight() <= 800 * 800 && mScale <= 10){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * ??��??????????�缩�?
	 * @return
	 */
	private boolean canZoomOut(){
		if(mResizeBitmap != null && !mResizeBitmap.isRecycled()){
			if(mResizeBitmap.getWidth() > mViewWidth || mResizeBitmap.getHeight() > mViewHeight){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	private void resizeBitmap() {
		try {
			Matrix matrix = new Matrix();
			matrix.postScale(mScale, mScale);
			if(mResizeBitmap != null && mResizeBitmap != mBitmap 
					&& !mResizeBitmap.isRecycled()){
				mResizeBitmap.recycle();
			}
			mResizeBitmap=null;

			if(mScale != 1){
				mResizeBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			}else{
				mResizeBitmap = mBitmap;
			}
			mResizedWidth = mResizeBitmap.getWidth();
	    	mResizedHeight = mResizeBitmap.getHeight();
	    	
			int sx = getScrollX();
			if(mResizedWidth > getWidth()){                      
				if(sx + getWidth() > mResizedWidth){
					sx = (int)(mResizedWidth - getWidth());  
				}
			}else{
				sx = 0;
			}
			int sy = getScrollY(); 
			if(mResizedHeight > getHeight()) {   
				if(sy + getHeight() > mResizedHeight){
					sy = (int)(mResizedHeight - getHeight());
				}
			}else{
				sy = 0;
			}
			setHorizontalScrollBarEnabled(false);
		    setVerticalScrollBarEnabled(false); 
			scrollTo(sx, sy);
			super.invalidate();
		}
		catch(Exception ex){
			Log.e("DragImageView", ex);
		}
		return;
	}
	
	/**
	 * �???��?????�???��??
	 * @author zhaolin02
	 *
	 */
    private class DecelerateAnimation extends Animation{
    	private boolean mIsAnimationInProgres;
    	private boolean mStop;
    	private long velocityX, velocityY;
    	private int mStartX, mStartY;
    	private long mTimeX, mTimeY;
    	static final long Decelerate = 2500;

    	public DecelerateAnimation(){
    		mIsAnimationInProgres = false;
    		mStop = false;
    	}
    	
    	public void prepareAnimation(float velocityX, float velocityY){
			// Configure base animation properties
    		if(velocityX > 1500){
    			velocityX = 1500;
    		}else if(velocityX < -1500){
    			velocityX = -1500;
    		}
    		if(velocityY > 1500){
    			velocityY = 1500;
    		}else if(velocityY < -1500){
    			velocityY = -1500;
    		}
    		this.velocityX = (long)velocityX;
    		this.velocityY = (long)velocityY;
    		mTimeX = (long)Math.abs(velocityX*1000/Decelerate);
    		mTimeY = (long)Math.abs(velocityY*1000/Decelerate);
    		long max_time = Math.max(mTimeX, mTimeY);
			this.setDuration(max_time);
			this.setInterpolator(mDecelerateInterpolater);
			mStartX = DragImageView.this.getScrollX();
			mStartY = DragImageView.this.getScrollY();
			mIsAnimationInProgres = true;
		}

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        	// Ensure interpolatedTime does not over-shoot then calculate new offset
        	interpolatedTime = (interpolatedTime > 1.0f) ? 1.0f : interpolatedTime;
        	long time = 0;
        	float tmp = 0;
        	if(mTimeX > mTimeY){
        		time = (long)(interpolatedTime * mTimeX);
        	}else{
        		time = (long)(interpolatedTime * mTimeY);
        	}
        	int distanceX = 0;
        	tmp = ((time > mTimeX)?mTimeX:time)/(float)1000;
			if(velocityX > 0){
				distanceX = mStartX - (int) (tmp * (velocityX - Decelerate * tmp / 2));					
			}else{
				distanceX = mStartX - (int) (tmp * (velocityX + Decelerate * tmp / 2));		
			}
			
			
        	int distanceY = 0;
        	tmp = ((time > mTimeY)?mTimeY:time)/(float)1000;
        	if(velocityY > 0){
    			distanceY = mStartY - (int) (tmp * (velocityY - Decelerate * tmp / 2));
        	}else{
    			distanceY = mStartY - (int) (tmp * (velocityY + Decelerate * tmp / 2));  		
        	}
			
			if(mResizedHeight > getHeight()) {   
				if(distanceY + getHeight() > mResizedHeight){
					distanceY = (int)(mResizedHeight - getHeight());
				}
				if(distanceY < 0){
					distanceY = 0;
				}
			}else{
				distanceY = 0;
			}
			if(mResizedWidth > getWidth()) {   
				if(distanceX + getWidth() > mResizedWidth){
					distanceX = (int)(mResizedWidth - getWidth());
				}
				if(distanceX < 0){
					distanceX= 0;
				}
			}else{
				distanceX = 0;
			}
			DragImageView.this.scrollTo(distanceX, distanceY);
			DragImageView.this.invalidate();
        }

        @Override
        public boolean getTransformation(long currentTime, Transformation outTransformation)
        {
        	if(mStop == true){
        		mStop = false;
        		mIsAnimationInProgres = false;
        		return false;
        	}
        	// Cancel if the screen touched
        	if (mIsTouched){
        		mIsAnimationInProgres = false;
        		return false;
        	}
        	if (super.getTransformation(currentTime, outTransformation) == false){
        		mIsAnimationInProgres = false;
				return false;
        	}
        	return true;
        }
        
        public boolean getIsAnimationInProgre(){
        	return mIsAnimationInProgres;
        }
        
        public void stopAnimation(){
        	mStop = true;
        }
    }
    
    public void setImageOnClickListener(OnClickListener click){
    	mClick = click;
    }
    
	public interface OnSizeChangedListener {  
		public void sizeChanged(boolean canZoomIn, boolean canZoomOut);  
	}
	
}
