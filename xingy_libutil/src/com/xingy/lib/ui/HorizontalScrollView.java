/**
 * Copyright (C) 2012 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: ICSON Android
 * FileName: HorizontalScrollView.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: 09/23, 2012
 */

package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.xingy.lib.ui.FlingGesture.FlingListener;


/**
 * The Class HorizontalScrollView.
 */
public class HorizontalScrollView extends ViewGroup implements FlingListener{

	/**
	 * The listener interface for receiving onIndicatorUpdate events.
	 * The class that is interested in processing a onIndicatorUpdate
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnIndicatorUpdateListener<code> method. When
	 * the onIndicatorUpdate event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnIndicatorUpdateEvent
	 */
	public interface OnIndicatorUpdateListener
	{
		
		/**
		 * On indicator change.
		 *
		 * @param percent the percent
		 */
		public abstract void onIndicatorChange(float percent);
		
		/**
		 * On indicator full.
		 *
		 * @param index the index
		 */
		public abstract void onIndicatorFull(int index);
		
		/**
		 * On indicator init.
		 *
		 * @param total the total
		 */
		public abstract void onIndicatorInit(int total);
	}
	
	/**
	 * The listener interface for receiving onItemClick events.
	 * The class that is interested in processing a onItemClick
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnItemClickListener<code> method. When
	 * the onItemClick event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnItemClickEvent
	 */
	public interface OnItemClickListener
	{
		
		/**
		 * On item click.
		 *
		 * @param v the v
		 * @param index the index
		 */
		public abstract void onItemClick(View v, int index);
	}
	
	/**
	 * Instantiates a new base horizontal scroll view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public HorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	/**
	 * Instantiates a new base horizontal scroll view.
	 *
	 * @param context the context
	 */
	public HorizontalScrollView(Context context) {
		super(context);
		init(context);
	}


	/**
	 * Instantiates a new base horizontal scroll view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public HorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	/**
	 * Inits the.
	 *
	 * @param context the context
	 */
	protected void init(Context context){
		mScroller = new Scroller(getContext(), new DecelerateInterpolator());
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        
        mFlingGesture = new FlingGesture();
        mFlingGesture.setListener(this);
        
        mDetector = new GestureDetector(new FakeGestureDetector());
        
      //disable auto snap to be sticky.
  		this.setSnapOnLayout(false);
  		//snap factor.
  		this.setSnapFactor(0.1f);
	}

	/*  
	 * Description:
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childLeft = 0;
        final int mTop=getPaddingTop();
        final int mBottom=getPaddingBottom();
		final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, mTop, childLeft + childWidth, mTop+child.getMeasuredHeight()-mBottom);
                childLeft += childWidth;
            }
        }
        
        //calibrate scroll
        if(mSnapOnLayout){
	        Log.d(TAG, "onLayout mCurrentScreen: "+mCurrentScreen);
	        snapToScreen(mCurrentScreen);
        }
	}

	/*  
	 * Description:
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
	}
	
	/**
	 * important.
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			
		} else {
			if(null != mIndicator){
            	mIndicator.onIndicatorFull(mCurrentScreen);
            }
			if(mAutoScroll == true){
				onScrollDone();
				mAutoScroll = false;
			}
            //Log.d(TAG, "computeScroll mCurrentScreen: "+mCurrentScreen);
		}
		
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		//notify.
		if(this.mIndicator!= null){
			float range = computeHorizontalScrollRange();
			float offset = getScrollX();
			float percent = offset/range;
			//Log.d(TAG, "percent: "+percent);
			this.mIndicator.onIndicatorChange(percent);
		}
	}
	
	@Override
	protected int computeHorizontalScrollRange() {
		final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - getWidth();
		
		return availableToScroll;
	}

	
	/*  
	 * Description:
	 * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		//pass to TouchInterceptor
		if(this.mTouchListener != null){
			this.mTouchListener.onTouch(this, ev);
		}
		
//        if (ev.getPointerCount() >= 2) {
//        	Log.d(TAG, "Mutitouch, **ignoire...***");
//        	return false;
//        }
        
	    int nPointer = getEventPointerCount(ev); 
		if(nPointer >= 2){
			Log.d(TAG, "Mutitouch, **ignoire...***");
        	return false;
		}
        
		/*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
		final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
        case MotionEvent.ACTION_MOVE:
        	/*
             * Locally do absolute value. mLastMotionX is set to the y value
             * of the down event.
             */
            final int xDiff = (int) Math.abs(x - mLastMotionX);
            final int yDiff = (int) Math.abs(y - mLastMotionY);

            final int touchSlop = mTouchSlop;
            boolean xMoved = xDiff > touchSlop;
            boolean yMoved = yDiff > touchSlop;
            if (xMoved || yMoved) {
                // If xDiff > yDiff means the finger path pitch is smaller than 45deg so we assume the user want to scroll X axis
                if (xDiff > yDiff) {
                    // Scroll if the user moved far enough along the X axis
                	Log.d(TAG, "TOUCH_STATE_SCROLLING");
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
            }
        	break;
        case MotionEvent.ACTION_DOWN:
            // Remember location of down touch
            mLastMotionX = x;
            mLastMotionY = y;
            
            /*
             * If being flinged and user touches the screen, initiate drag;
             * otherwise don't.  mScroller.isFinished should be false when
             * being flinged.
             */
            mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
            break;
            
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
        	mTouchState = TOUCH_STATE_REST;
            break;
        }
		
		return mTouchState != TOUCH_STATE_REST;
	}
	
	/**
	 * Scroll done.
	 */
	protected void onScrollDone(){
		
	}
	
	protected void onSnap(int whichScreen){
		
	}
	
	/**
	 * Begin to scroll left.
	 */
	protected void onScrollLeft(){
		
	}
	
	/**
	 * Begin to scroll right.
	 */
	protected void onScrollRight(){
		
	}
	
	protected void onOverScrollLeft(){
		
	}
	
	protected void onOverScrollRight(){
		
	}
    
	/*  
	 * Description:
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//check if disable touch status
		if(!mTouchStatus){
			Log.d(TAG, "touch disable");
			mTouchState = TOUCH_STATE_REST;
			return false;
		}
		
		//do not handle multi touch.
//		if (event.getPointerCount() >= 2) {
//		    Log.d(TAG, "onTouchEvent Mutitouch, **ignoire...***");
//		    return false;
//		}
		
	    int nPointer = getEventPointerCount(event); 
		if(nPointer >= 2){
			Log.d(TAG, "Mutitouch, **ignoire...***");
        	return false;
		}
		
		//Detect unused gesture..
		if(mDetector.onTouchEvent(event)){
			Log.d(TAG, "filter gesture..");
			return false;
		}
		
		final int action = event.getAction();
		final float x = event.getX();
		
		//Check if fling.
		mFlingGesture.ForwardTouchEvent(event);
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(mScroller.isFinished() == false){
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mDownMotionX = x;
			
			break;
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);
            final int touchSlop = mTouchSlop;
            //Log.d(TAG, "Slop: "+mTouchSlop);
            
            boolean xMoved = xDiff > touchSlop;
            if (xMoved ) {
                mTouchState = TOUCH_STATE_SCROLLING;
            }
            
			if(TOUCH_STATE_SCROLLING == mTouchState){
				final int deltaX = (int)(mLastMotionX - x);
				mLastMotionX = x;
     			//Horizontal distance to travel. Positive numbers will scroll the content to the left.
				if (deltaX < 0) { //right
                    if (getScrollX() > touchSlop) {
                    	//notify
                    	onScrollRight();
                    	scrollBy(Math.max(-1*getScrollX(), deltaX), 0);                        
                    } else {
                    	//Over scroll
                    	if(mOverScrollStatus){
	                    	Log.d(TAG, "Over Scroll");
	                    	onOverScrollRight();
	                    	scrollBy(deltaX/2, 0);
                    	} else {
                    		mTouchState = TOUCH_STATE_REST;
                    	}
                    }
                } else if (deltaX > 0) { //left
                	if(getChildCount() > 0){
	                    final int availableToScroll = getChildAt(getChildCount() - 1).getRight() -
	                            getScrollX() - getWidth();
	                    if (availableToScroll > touchSlop) {
	                    	//notify
	                    	onScrollLeft();
	                    	scrollBy(Math.min(availableToScroll, deltaX), 0);
	                    } else {
	                    	//Over scroll
	                    	if(mOverScrollStatus){
		                    	Log.d(TAG, "Over Scroll");
		                    	onOverScrollLeft();
		                    	scrollBy(deltaX/2, 0);
	                    	} else {
	                    		mTouchState = TOUCH_STATE_REST;
	                    	}
	                    }
                    }
                }
			}
			break;
		case MotionEvent.ACTION_UP:
			if( (TOUCH_STATE_REST == mTouchState) && (null != mClickListener) )
			{
				final int nDeltaX = (int) Math.abs(mDownMotionX - x);
				final int nMinDelta = 5;
				if( nDeltaX <= nMinDelta )
				{
					final int nIndex = Math.min(mCurrentScreen, getChildCount() - 1);
					View pChild = getChildAt(nIndex);
					mClickListener.onItemClick(pChild, nIndex);
				}
			}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			Log.d(TAG, "Snap");
			snapToDestination();
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}


	/**
	 * 
	* method Name:getEventPointerCount, adapt to 1.6 sdk.    
	* method Description:  
	* @param event
	* @return   
	* int  
	* @exception   
	* @since  1.0.0
	 */
	protected int getEventPointerCount(MotionEvent event) {
	//	boolean hasMultiTouch = Build.VERSION.SDK_INT >= 5;  
	 //   int nPointer = hasMultiTouch ? MultiTouchWrap.getPointerCount(event) : 1;
	//	return nPointer;
		return 1;
	}
	
	/*  
	 * Description:
	 * @see com.soso.views.FlingGesture.FlingListener#OnFling(int, int, int)
	 */
	@Override
	public void OnFling(int Direction, int velocityX, int velocityY) {
		if (mFlingStatus && mTouchState == TOUCH_STATE_SCROLLING) {
			Log.d(TAG, "Fling");
			if (Direction == FlingGesture.FLING_LEFT && mCurrentScreen > 0) {
				Log.d(TAG, "Fling snapToScreen" +(mCurrentScreen - 1));
				snapToScreen(mCurrentScreen - 1);
	        } else if (Direction == FlingGesture.FLING_RIGHT && mCurrentScreen < getChildCount() - 1) {
	        	Log.d(TAG, "Fling snapToScreen" + (mCurrentScreen + 1));
	        	snapToScreen(mCurrentScreen + 1);
	        } 
			//ONLY Fling left or right...
	        /*else {
	        	snapToDestination();
	        }*/
		}
	}
	
	/**
	 * snapLeft
	 * @return
	 */
	public boolean snapLeft()
	{
		if( 0 >= mCurrentScreen )
			return false;
		
		snapToScreen(mCurrentScreen - 1);
		return true;
	}
	
	/**
	 * snapRight
	 * @return
	 */
	public boolean snapRight()
	{
		if( mCurrentScreen >= this.getChildCount() - 1 )
			return false;
		
		snapToScreen(mCurrentScreen + 1);
		return true;
	}
	
	/**
	 * set snap trigger factor.
	 * @param factor
	 */
	protected void setSnapFactor(float factor){
		if( factor > 0.f && factor < 1.f){
			this.mSnapFactor = factor;
		}
	}
	
	/**
	 * Snap to destination.
	 */
	protected void snapToDestination() {
		if(getChildCount() <= 0){
			return;
		}
		
		Log.d(TAG, "snapToDestination");
		//check current child if child width > screen width
		int index = Math.min(mCurrentScreen, getChildCount() - 1);
		View child = getChildAt(index);
		final int childRight = child.getRight();
		final int childLeft = child.getLeft();
		final int childWidth = child.getRight() - child.getLeft();
		final int screenWidth = getWidth();
		final int scrollX = getScrollX();
		if( childWidth <= screenWidth ){
			//factor width->next
			if(scrollX + screenWidth*(1.f - mSnapFactor) > childRight ){
				snapToScreen(index +1);
			} else if( scrollX < childLeft -  screenWidth* mSnapFactor){
				snapToScreen(index -1);
			} else {
				snapToScreen(index);
			}
			
		} else {
			//next visible-> next
			if(scrollX + screenWidth > childRight ){
				snapToScreen(index +1);
			} else if( scrollX < childLeft){
				snapToScreen(index -1);
			}
		}
		
/*		final int screenWidth = getWidth();
		//half width .
		final int whichScreen = (getScrollX() + (screenWidth / 2))
				/ screenWidth;
		snapToScreen(whichScreen);*/
	}

	/**
	 * Snap to screen.
	 *
	 * @param whichScreen the which screen
	 */
	protected void snapToScreen(int whichScreen) {
		if (!mScroller.isFinished()){
			Log.d(TAG, "scroller not finished..");
			return;
		}
		
		//callback.
		onSnap(whichScreen);

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		boolean changingScreens = whichScreen != mCurrentScreen;

		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingScreens
				&& focusedChild == getChildAt(mCurrentScreen)) {
			Log.d(TAG, "clearFocus");
			focusedChild.clearFocus();
		}
		
		mCurrentScreen = whichScreen;

		//start scroll
		mAutoScroll = true;
		final int cx = getScrollX();
		//final int newX = whichScreen * getWidth();
		final int newX = getChildAt(whichScreen).getRight() - getWidth();
		final int delta = newX - cx;
		if(Math.abs(delta) > mTouchSlop){
			mScroller.startScroll(cx, 0, delta, 0);
		} else {
			Log.d(TAG, "small delta, just scroll..");
			scrollBy(delta, 0);
		}
		postInvalidate();
	}
	
	/**
	 * 
	* method Name:scrollToScreen    
	* method Description:  direct scroll, no animation.
	* @param whichScreen   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	protected void scrollToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		boolean changingScreens = whichScreen != mCurrentScreen;

		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingScreens
				&& focusedChild == getChildAt(mCurrentScreen)) {
			Log.d(TAG, "clearFocus");
			focusedChild.clearFocus();
		}
		
		mCurrentScreen = whichScreen;
		final int cx = getScrollX();
		//final int newX = whichScreen * getWidth();
		final int newX = getChildAt(whichScreen).getRight() - getWidth();
		final int delta = newX - cx;
		Log.d(TAG, "scrollToScreen, just scroll..");
		scrollBy(delta, 0);
		postInvalidate();
	}
	
	/**
	 * Move to default screen.
	 */
	protected void snapToDefaultScreen() {
    	mDefaultScreen = getChildCount()/2;
        snapToScreen(mDefaultScreen);
        getChildAt(mDefaultScreen).requestFocus();
    }
	
	protected void setTouchStatus(boolean status){
		this.mTouchStatus = status;
	}
	
	protected boolean getTouchStatus(){
		return this.mTouchStatus;
	}
	
	protected boolean getSnapOnLayout() {
		return mSnapOnLayout;
	}

	protected void setSnapOnLayout(boolean status) {
		this.mSnapOnLayout = status;
	}

	protected boolean getOverScrollStatus() {
		return mOverScrollStatus;
	}
	
	protected void setOverScrollStatus(boolean status) {
		this.mOverScrollStatus = status;
	}


	/**
	 * Gets the indicator.
	 *
	 * @return the indicator
	 */
	public OnIndicatorUpdateListener getIndicator() {
		return mIndicator;
	}

	/**
	 * Sets the indicator.
	 *
	 * @param indicator the new indicator
	 */
	public void setIndicator(OnIndicatorUpdateListener indicator) {
		this.mIndicator = indicator;
		final int count = this.getChildCount();
		this.mIndicator.onIndicatorInit(count);
	}
	
	/**
	 * Gets the on item click listener.
	 *
	 * @return the on item click listener
	 */
	public OnItemClickListener getOnItemClickListener() {
		return mClickListener;
	}
	
	/**
	 * Sets the on item click listener.
	 *
	 * @param listener the new on item click listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mClickListener = listener;
	}
	
	public void setTouchInterceptor(View.OnTouchListener listener){
		this.mTouchListener = listener;
	}
	
	public View.OnTouchListener getTouchInterceptor(){
		return this.mTouchListener;
	}
	
	private class FakeGestureDetector extends GestureDetector.SimpleOnGestureListener {

		public FakeGestureDetector() {
			super();
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return true;
		}
	}

	
	/** The m scroller. */
	private Scroller mScroller;
	
	/** The m auto scroll. */
	private boolean mAutoScroll = false;
	
	private boolean mSnapOnLayout = true;
	
	private boolean mTouchStatus = true;
	
	private float mSnapFactor = 0.5f;

	/** The m last motion x. */
	private float mLastMotionX;
	
	/** The m last motion y. */
	private float mLastMotionY;
	
	/** The down motion. */
	private float mDownMotionX;
	
	/** The m touch slop. */
	private int mTouchSlop;
	
	private boolean mFlingStatus = true;
	
	private boolean mOverScrollStatus = false;
	
	/** The m fling gesture. */
	private FlingGesture mFlingGesture;
	
	private GestureDetector mDetector;
    
	/** The Constant TAG. */
	private final static String TAG = "ScrollView";
	
	/** The m default screen. */
	protected int mDefaultScreen = 0;
	
	/** The m current screen. */
	protected int mCurrentScreen;
	
	/** The m indicator. */
	protected OnIndicatorUpdateListener mIndicator = null;
	
	/** The m click listener. */
	protected OnItemClickListener mClickListener = null;
	
	protected View.OnTouchListener mTouchListener = null;
	
	/** The m touch state. */
	protected int mTouchState = TOUCH_STATE_REST;
	
	/** The Constant TOUCH_STATE_REST. */
	protected final static int TOUCH_STATE_REST = 0;
	
	/** The Constant TOUCH_STATE_SCROLLING. */
	protected final static int TOUCH_STATE_SCROLLING = 1;

	protected final static int TOUCH_STATE_OVERSCROLL = 2;
}
