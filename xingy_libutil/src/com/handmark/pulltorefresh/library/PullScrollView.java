package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class PullScrollView extends ScrollView {

	public PullScrollView(Context context) {
		super(context);
	}

	public PullScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(onScrollListener!=null){
			onScrollListener.onScroll(l, t, oldl, oldt);
		}
	}

	private OnMyScrollListener onScrollListener;

	public OnMyScrollListener getOnScrollListener() {
		return onScrollListener;
	}

	public void setOnMyScrollListener(OnMyScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	public interface OnMyScrollListener {
		void onScroll(int l, int t, int oldl, int oldt);
	}
}
