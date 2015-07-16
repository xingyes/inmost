package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 此类主�?????�????�???��??�????�?
 * 
 * @author kunjiang
 * @Date 2012-03-01
 * 
 */
public class MyScrollLayout extends ViewGroup {
	Scroller mScroller;// �???��?��??
	VelocityTracker velocity;// ???�???��??
	int mCurScreen;// �????�?�?�?�?
	float mLastX;//???�????�????X??????
	public static final int SNAP_VELOCITY = 600;
	
	public MyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);// ???�??????????
	}

	public MyScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);// ???�??????????
	}

	public MyScrollLayout(Context context) {
		super(context);
		init(context);// ???�??????????
	}

	public void init(Context context) {
		mScroller = new Scroller(context);// ???建�????��?��?��??对象
		mCurScreen = 0;// �????�?�?�?0
	}

	/**
	 * 对�?????容大�?�?�?�?�?
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int childCount = getChildCount();// �???��??�?件�????��??
		int width = MeasureSpec.getSize(widthMeasureSpec);
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);// �???��??�?�?
			childView.measure(widthMeasureSpec, heightMeasureSpec);// �????measure为�??�?件大�?�????
		}

		
		scrollTo(mCurScreen * width, 0);// ???�????�???��??�?�?使�?��????��?��??�?�???????
	}

	/**
	 * 对�??容�??�?�?�?�?�?�?
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed){
			int childCount = getChildCount();//�???��??�?件�????��??
			int childLeft = 0;
			for(int i = 0; i < childCount; i ++){
				View childView = getChildAt(i);//�???��??�?�?
				int width = childView.getMeasuredWidth();//�???��??�?件�??宽度
				childView.layout(childLeft, 0, childLeft + width, childView.getMeasuredHeight());
				
				childLeft += width;
			}
		}
	}
	
	@Override
	public void computeScroll() {//�???��??件�??�?�?�?件�????��??�???��?��?��??
		if(mScroller.computeScrollOffset()){//�???��?�没??????止�??
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());//�??????��?�没??????�?  ??��??�???��?��?��??View??????
			postInvalidate();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float curX = event.getX();
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN://???�????�????
			if(velocity == null ){
				velocity = VelocityTracker.obtain();//???�???????�???��??
				velocity.addMovement(event);//�?�?�?�?件交�????�???��??
			}
			if(!mScroller.isFinished()){//�?没�?????�???��?��??  �????已�?��?��??�?
				mScroller.abortAnimation();//???止�?��??
			}
			mLastX = curX;
			break;

		case MotionEvent.ACTION_MOVE://移�?��??�????
			int distance_x = (int)(mLastX - curX);
			if(IsCanMove(distance_x)){//??��??????????�移???
				if(velocity != null ){
					velocity.addMovement(event);//�?�?�?�?件交�????�???��??
				}
				mLastX = curX;
				scrollBy(distance_x, 0);
			}
			break;
		case MotionEvent.ACTION_UP://???起�??�????
			int velocityX = 0;
            if (velocity != null)
            {
            	velocity.addMovement(event); 
            	velocity.computeCurrentVelocity(1000);  
            	velocityX = (int) velocity.getXVelocity();
            }
                    
                
            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {       
                // Fling enough to move left       
                snapToScreen(mCurScreen - 1);       
            } else if (velocityX < -SNAP_VELOCITY       
                    && mCurScreen < getChildCount() - 1) {       
                // Fling enough to move right       
                snapToScreen(mCurScreen + 1);       
            } else {       
                snapToDestination();       
            }      
            
           
            
            if (velocity != null) {       
            	velocity.recycle();       
            	velocity = null;       
            }       
            
            break;   
		}
		return true;//??????true  ???说�??   已�????????�?  �???��????��?????�?�?
	}

	 public void snapToDestination() {    
	        final int screenWidth = getWidth();    

	        final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;    
	        snapToScreen(destScreen);    
	 }  
	
	 public void snapToScreen(int whichScreen) {    
	
	        // get the valid layout page    
	        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));    
	        if (getScrollX() != (whichScreen*getWidth())) {    
	                
	            final int delta = whichScreen*getWidth()-getScrollX();    
	        
	            mScroller.startScroll(getScrollX(), 0,     
	                    delta, 0, Math.abs(delta)*2);

	            
	            mCurScreen = whichScreen;    
	            invalidate();       // Redraw the layout    
	            
	        }    
	    }    

	/**
	 * @param distance_x 移�?��??�?
	 * @return ????????��??移�??
	 */
	public boolean IsCanMove(int distance_x){
		//�???��????��?????�????distance_x�?�?0 并�?? ???移�??�?�?0�? ???�???��????��??
		if(distance_x < 0 && getScrollX() < 0){
			return false;
		}
		//�???��????��?????�????�???�大�?�?�?0 并�?? ???移�??大�??�???????�?件�??宽度?????? ??��?????�????false
		if(getScrollX() > (getChildCount() - 1) * getWidth() && distance_x > 0){
			return false;
		}
		return true;
	}
}
