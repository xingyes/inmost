package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xingy.R;
import com.xingy.util.ToolUtil;

import java.util.Calendar;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;


public class DateTimePickerView extends UiBase implements OnWheelScrollListener, OnClickListener{
	private TextView  mValues;
	private AbstractWheel mYear;
	private AbstractWheel mMonth;
	private AbstractWheel mDate;
	
	private AbstractWheel mHour;
	private AbstractWheel mMin;
	
	private NumericWheelAdapter yearAdapter;
	private NumericWheelAdapter monthAdapter;
	private NumericWheelAdapter dateAdapter;
	
	private NumericWheelAdapter hourAdapter;
	private NumericWheelAdapter minAdapter;
	
	private OnWheelChangedListener mYearWheelListener;
	private OnWheelChangedListener mMonthWheelListener;
	private OnWheelChangedListener mDateWheelListener;
	private OnWheelChangedListener mHourWheelListener;
	private OnWheelChangedListener mMinWheelListener;
	
	private boolean   bIsDateOnly = false;
	// set current time
    private Calendar calendar;
    private int      lastMaxDateinMonth;
    private Context  mContext;
    private int      mStartYear;
    private int      mEndYear;
    
    /**
	 * @param context
	 * @param attrs
	 */
	public DateTimePickerView(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.date_time_picker);
		mContext = context;
		calendar = Calendar.getInstance();
		parseAttrs(attrs);
	}
	
	protected void parseAttrs(AttributeSet attrs) {
		if( null == attrs || null == mContext )
			return ;
		
		// Parse attributes.
		if( null != attrs ) {
			TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.xingy_attrs);
			mStartYear = UiUtils.getInteger(mContext, array, R.styleable.xingy_attrs_startYear);
			mEndYear = UiUtils.getInteger(mContext, array, R.styleable.xingy_attrs_endYear);
		}
	}
	
	public interface OnPickerListener
	{
		public void onSubmit();
	}
	
	private OnPickerListener  mListener;
	public void setListener(OnPickerListener aListener)
	{
		mListener = aListener;
	}
	
	
	public Calendar getCalendat()
	{
		return calendar;
	}

	@Override
	protected void onInit(Context context) {
		
		mValues = (TextView) this.findViewById(R.id.result_info);
		this.findViewById(R.id.ok_btn).setOnClickListener(this);
		this.findViewById(R.id.cancel_btn).setOnClickListener(this);
		
		if(bIsDateOnly)
			mValues.setText(ToolUtil.toDate(calendar.getTimeInMillis(), mContext.getString(R.string.x_day)));
		else
			mValues.setText(ToolUtil.toDate(calendar.getTimeInMillis(), mContext.getString(R.string.x_minite)));
		
		mYear = (AbstractWheel) findViewById(R.id.year);
        yearAdapter = new NumericWheelAdapter(context, mStartYear, mEndYear, "%d年");
        yearAdapter.setItemResource(R.layout.wheel_text_centered);
        yearAdapter.setItemTextResource(R.id.text);
        mYear.setViewAdapter(yearAdapter);
        mYear.setCurrentItem(calendar.get(Calendar.YEAR) - mStartYear);
        
        mMonth = (AbstractWheel) findViewById(R.id.month);
        monthAdapter = new NumericWheelAdapter(context, 1, 12, "%02d月");
        monthAdapter.setItemResource(R.layout.wheel_text_centered);
        monthAdapter.setItemTextResource(R.id.text);
        mMonth.setViewAdapter(monthAdapter);
       //mMonth.setCyclic(true);
        mMonth.setCurrentItem(calendar.get(Calendar.MONTH));
        
        lastMaxDateinMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        mDate = (AbstractWheel) findViewById(R.id.date);
        dateAdapter = new NumericWheelAdapter(context, 1,lastMaxDateinMonth ,  "%02d日");
        dateAdapter.setItemResource(R.layout.wheel_text_centered);
        dateAdapter.setItemTextResource(R.id.text);
        mDate.setViewAdapter(dateAdapter);
        //mDate.setCyclic(true);
        mDate.setCurrentItem(calendar.get(Calendar.DATE) - 1);
        
        
        mHour = (AbstractWheel) findViewById(R.id.hour);
        hourAdapter = new NumericWheelAdapter(context, 0, 23, "%02d时");
        hourAdapter.setItemResource(R.layout.wheel_text_centered);
        hourAdapter.setItemTextResource(R.id.text);
        mHour.setViewAdapter(hourAdapter);
        mHour.setCyclic(true);
        mHour.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        if(bIsDateOnly)
        {
        	mHour.setVisibility(View.GONE);
        	calendar.set(Calendar.HOUR_OF_DAY, 0);
        }
        

        mMin = (AbstractWheel) findViewById(R.id.min);
        minAdapter = new NumericWheelAdapter(context, 0, 59, "%02d分");
        minAdapter.setItemResource(R.layout.wheel_text_centered);
        minAdapter.setItemTextResource(R.id.text);
        mMin.setViewAdapter(minAdapter);
        mMin.setCyclic(true);
        mMin.setCurrentItem(calendar.get(Calendar.MINUTE));
        
        if(bIsDateOnly)
        {
        	mMin.setVisibility(View.GONE);
        	calendar.set(Calendar.MINUTE, 0);
        }
        
        
        mYearWheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            	calendar.set(Calendar.YEAR, (mStartYear + newValue));
            }
        };
        mYear.addChangingListener(mYearWheelListener);
        mYear.addScrollingListener(this);
        
        mMonthWheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            	//from 0~11
            	int olddate = calendar.get(Calendar.DATE);
            	calendar.set(Calendar.DATE, 1);
            	calendar.set(Calendar.MONTH, newValue);
            	int newmax = calendar.getActualMaximum(Calendar.DATE);
            	if(olddate >= newmax)
            		calendar.set(Calendar.DATE, newmax-1);
            	else
            		calendar.set(Calendar.DATE, olddate);
            }
        };
        mMonth.addChangingListener(mMonthWheelListener);
        mMonth.addScrollingListener(this);
        
        mDateWheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            	//0~x
            	calendar.set(Calendar.DAY_OF_MONTH, newValue + 1);
            }
        };
        mDate.addChangingListener(mDateWheelListener);
        mDate.addScrollingListener(this);
        
    
        //hour
        mHourWheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            	calendar.set(Calendar.HOUR_OF_DAY, newValue);
            }
        };
        mHour.addChangingListener(mHourWheelListener);
        mHour.addScrollingListener(this);
        
        //min
        mMinWheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            	calendar.set(Calendar.MINUTE, newValue);
            }
        };
        mMin.addChangingListener(mMinWheelListener);
        mMin.addScrollingListener(this);
	}



	@Override
	public void onScrollingStarted(AbstractWheel wheel) {
	}



	@Override
	public void onScrollingFinished(AbstractWheel wheel) {
		if(wheel == mMonth || wheel == mYear)
			updateDateNuminMonth();
		
		if(this.bIsDateOnly)
			mValues.setText(ToolUtil.toDate(calendar.getTimeInMillis(), mContext.getString(R.string.x_day)));
		else
			mValues.setText(ToolUtil.toDate(calendar.getTimeInMillis(), mContext.getString(R.string.x_minite)));
		
		//handler.sendEmptyMessageDelayed(MSG_TIMECHANGE, UPDATE_INTERVAL);
		//show text
    }



	private void updateDateNuminMonth() {
		android.util.Log.e("udpate", "old:" + lastMaxDateinMonth + ",new:" + calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		if(lastMaxDateinMonth != calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		{
			int pos = mDate.getCurrentItem();
			lastMaxDateinMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			dateAdapter = new NumericWheelAdapter(mContext, 1,lastMaxDateinMonth ,  "%02d日");
	        dateAdapter.setItemResource(R.layout.wheel_text_centered);
	        dateAdapter.setItemTextResource(R.id.text);
	        mDate.setViewAdapter(dateAdapter);
	        if(pos >=lastMaxDateinMonth)
	        	mDate.setCurrentItem(lastMaxDateinMonth - 1);
	    }
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ok_btn)
		{
			if(null!=mListener)
				mListener.onSubmit();
		}
		else if(v.getId() == R.id.cancel_btn)
		{
			this.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
	
	public void setDateOnly(boolean flag)
	{
		this.bIsDateOnly = flag;
		if(bIsDateOnly)
		{
			mHour.setVisibility(View.GONE);
			mMin.setVisibility(View.GONE);
			mValues.setText(ToolUtil.toDate(calendar.getTimeInMillis(), mContext.getString(R.string.x_day)));
		}
    }
	
	public String getValue()
	{
		return this.mValues.getText().toString();
	}
}
