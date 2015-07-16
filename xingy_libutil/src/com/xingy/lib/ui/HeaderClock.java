package com.xingy.lib.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xingy.R;

public class HeaderClock extends LinearLayout implements Runnable {
	
	public static final int MESSAGE_CURRENT = 100121;

	private static final long[] UNITS = { 60 * 1000, 60, 24 };

	private static final long[] TIME_OFFSTE = new long[UNITS.length + 1];

	private long mEndTime;

	private int mClockMinLength = 3;

	private Runnable mRunnable;

	private TextView day;
	private TextView dayLabel;

	private TextView hour;
	private TextView hourLabel;

	private TextView min;
	private TextView minLabel;

	private TextView sec;

	private TextView secLabel;
	
	private Handler mHandler;

	private long mOffset = 0;
	
	private Context mContext;

	public HeaderClock(Context context) {
		super(context);
		mContext = context;
	}

	public HeaderClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public void setHandler(Handler handler){
		mHandler = handler;
	}

	public void setLayout(int layoutId) {
		
		final Context context = mContext;
		
		View view = inflate(context, layoutId, null);
		addView(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		day = (TextView) findViewById(R.id.global_header_textview_day);
		dayLabel = (TextView) findViewById(R.id.global_header_textview_day_label);

		hour = (TextView) findViewById(R.id.global_header_textview_hour);
		hourLabel = (TextView) findViewById(R.id.global_header_textview_hour_label);

		min = (TextView) findViewById(R.id.global_header_textview_minute);
		minLabel = (TextView) findViewById(R.id.global_header_textview_minute_label);

		sec = (TextView) findViewById(R.id.global_header_textview_second);
		secLabel = (TextView) findViewById(R.id.global_header_textview_second_label);

	}

	public void setCurrentTime(long milliseconds) {
		mOffset = System.currentTimeMillis() - milliseconds;
	}

	public void setEndTime(long milliseconds) {
		mEndTime = milliseconds;
	}

	public void setClockMinLength(int minLength) {
		mClockMinLength = minLength;
	}

	public void setOnArriveListener(Runnable runnable) {
		mRunnable = runnable;
	}

	@Override
	final public void run() {
		long now = System.currentTimeMillis() - mOffset;
		long offset = mEndTime - now;
		
		if (offset <=0) {
			if (mRunnable != null) {
				mRunnable.run();
			}
			TIME_OFFSTE[0] = TIME_OFFSTE[1] = TIME_OFFSTE[2] = TIME_OFFSTE[3] = 0;
		} else {
			for (int i = 0, len = UNITS.length; i < len; i++) {
				TIME_OFFSTE[i] = offset % (UNITS[i]);
				offset = (offset - TIME_OFFSTE[i]) / UNITS[i];
			}
			TIME_OFFSTE[UNITS.length] = offset;
			postDelayed(this, 1000);
		}

		int status;
		status = TIME_OFFSTE[0] != 0 || mClockMinLength > 0 ? View.VISIBLE : View.GONE;
		if(null==sec|| null == secLabel)
			return;
		
		sec.setVisibility(status);
		secLabel.setVisibility(status);
		if (status == View.VISIBLE) {
			sec.setText(String.format("%1$02d", TIME_OFFSTE[0] / 1000));
		}

		status = TIME_OFFSTE[1] != 0 || mClockMinLength > 1 ? View.VISIBLE : View.GONE;
		if(null==min|| null == minLabel)
			return;
		min.setVisibility(status);
		minLabel.setVisibility(status);
		if (status == View.VISIBLE) {
			min.setText(String.format("%1$02d", TIME_OFFSTE[1]));
		}

		status = TIME_OFFSTE[2] != 0 || mClockMinLength > 2 ? View.VISIBLE : View.GONE;
		if(null==hour|| null == hourLabel)
			return;
		hour.setVisibility(status);
		hourLabel.setVisibility(status);
		if (status == View.VISIBLE) {
			hour.setText(String.format("%1$02d", TIME_OFFSTE[2]));
		}

		status = (TIME_OFFSTE[3] != 0 || mClockMinLength > 3) ? View.VISIBLE : View.GONE;
		if(null==day|| null == dayLabel)
			return;
		day.setVisibility(status);
		dayLabel.setVisibility(status);
		if (status == View.VISIBLE) {
			day.setText(String.format("%1$2d", TIME_OFFSTE[3]));
		}
		
		if( mHandler != null ){
			Message message = Message.obtain();
			message.what = MESSAGE_CURRENT;
			message.obj = Long.valueOf(now);
			mHandler.sendMessage(message);
		}
	}

	public void destroy() {
		removeCallbacks(this);
		mRunnable = null;
		day = null;
		dayLabel = null;
		hour = null;
		hourLabel = null;
		min = null;
		minLabel = null;
		sec = null;
		secLabel = null;
	}
}
