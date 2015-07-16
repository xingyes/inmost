package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	public MyScrollView(Context context) {
		super(context);
	}
	public MyScrollView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	

	public interface OnScrollListener {
        public void onScroll(boolean bIsScrolling);
    }
	
	private float xDistance, yDistance, lastX, lastY;
	private boolean mFling;
    private OnScrollListener mListener = null;
	
	
	
	public void setOnScrollListener(OnScrollListener aListener) {
		mListener = aListener;
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            xDistance = yDistance = 0f;
	            lastX = ev.getX();
	            lastY = ev.getY();
	            break;
	            
	        case MotionEvent.ACTION_MOVE:
	            final float curX = ev.getX();
	            final float curY = ev.getY();
	            xDistance += Math.abs(curX - lastX);
	            yDistance += Math.abs(curY - lastY);
	            lastX = curX;
	            lastY = curY;
	            //�????移�?��??�?>�???��??离�??�?�????�????
	            if(xDistance > yDistance)
	                return false;
	            break;
	    }

	    // ????????��?????java.lang.IllegalArgumentException: pointerIndex out of range??? Exception
	    boolean returnVal = false;
	    try {
	    	returnVal = super.onInterceptTouchEvent(ev);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return returnVal;
	}
	
	@Override
	public boolean onTouchEvent (MotionEvent ev) 
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if( null != mListener ) {
            	mListener.onScroll(true);
            }
			break;
		 case MotionEvent.ACTION_UP:
	        if( null != mListener ) {
	           	mListener.onScroll(false);
	        }
	        break;
		}
		
		
	    // ????????��?????java.lang.IllegalArgumentException: pointerIndex out of range??? Exception
	    boolean returnVal = false;
	    try {
	    	returnVal = super.onTouchEvent(ev);
	    } catch (IllegalArgumentException e) {
	    	e.printStackTrace();
	    }
	    return returnVal;
	}
	
	
	@Override
    public void fling(int velocityY) {
		final int nAccelerate = velocityY * 3 / 2;
        super.fling(nAccelerate);
        mFling = true;
        
        if( null != mListener ) {
        	mListener.onScroll(mFling);
        }
    }
	
	@Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (mFling) {
            if (Math.abs(y - oldY) < 2 || y >= getMeasuredHeight() || y == 0) {
                mFling = false;
                
                if (mListener != null) {
                	mListener.onScroll(mFling);
                }
            }
        }
    }
}
