/**
 * Copyright (C) 2012 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: ICSON Android
 * FileName: FlingGesture.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: 09/23, 2012
 */

package com.xingy.lib.ui;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/**
 * The Class FlingGesture.
 */
public class FlingGesture {

	/**
	 * The listener interface for receiving fling events.
	 * The class that is interested in processing a fling
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addFlingListener<code> method. When
	 * the fling event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see FlingEvent
	 */
	public interface FlingListener {
		
		/**
		 * On fling.
		 *
		 * @param Direction the direction
		 * @param velocityX the velocity x
		 * @param velocityY the velocity y
		 */
		public void OnFling(int Direction, int velocityX, int velocityY);
	}


	/**
	 * Instantiates a new fling gesture.
	 */
	public FlingGesture() {
		mMaximumVelocity = ViewConfiguration.getMaximumFlingVelocity();
	}

	/**
	 * Sets the listener.
	 *
	 * @param aListener the new listener
	 */
	public void setListener(FlingListener aListener) {
		mListener = aListener;
	}

	/**
	 * Forward touch event.
	 *
	 * @param ev the ev
	 */
	public void ForwardTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
            final int velocityX = (int) mVelocityTracker.getXVelocity();
            final int velocityY = (int) mVelocityTracker.getYVelocity();

            if (mListener != null) {
            	int Direction = FLING_NONE;
	            if (velocityX > SNAP_VELOCITY) {
	            	Direction = FLING_LEFT;
	            } else if (velocityX < -SNAP_VELOCITY) {
	            	Direction = FLING_RIGHT;
	            } else if (velocityY > SNAP_VELOCITY) {
	            	Direction = FLING_DOWN;
	            } else if (velocityY < -SNAP_VELOCITY) {
	            	Direction = FLING_UP;
	            }
	            mListener.OnFling(Direction, Math.abs(velocityX), Math.abs(velocityY));
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }
	}

	/** The m maximum velocity. */
	private final int mMaximumVelocity;
	
	/** The m velocity tracker. */
	private VelocityTracker mVelocityTracker = null;
	
	/** The m listener. */
	private FlingListener mListener = null;
	

    /** The velocity at which a fling gesture will cause us to snap to the next screen. */
    private static final int SNAP_VELOCITY = 500;

    /** The Constant FLING_NONE. */
    public static final int FLING_NONE = 0;
    
    /** The Constant FLING_LEFT. */
    public static final int FLING_LEFT = 1;
    
    /** The Constant FLING_RIGHT. */
    public static final int FLING_RIGHT = 2;
    
    /** The Constant FLING_UP. */
    public static final int FLING_UP = 3;
    
    /** The Constant FLING_DOWN. */
    public static final int FLING_DOWN = 4;
}
