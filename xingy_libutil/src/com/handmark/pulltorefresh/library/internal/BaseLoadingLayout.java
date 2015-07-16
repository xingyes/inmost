package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.handmark.pulltorefresh.library.ILoadingLayout;

public abstract class BaseLoadingLayout extends FrameLayout implements ILoadingLayout {

	public BaseLoadingLayout(Context context) {
		super(context);
	}

	public BaseLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseLoadingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {

	}

	@Override
	public void setLoadingDrawable(Drawable drawable) {

	}

	@Override
	public void setPullLabel(CharSequence pullLabel) {

	}

	@Override
	public void setRefreshingLabel(CharSequence refreshingLabel) {

	}

	@Override
	public void setReleaseLabel(CharSequence releaseLabel) {

	}

	@Override
	public void setTextTypeface(Typeface tf) {

	}

	public abstract void onPull(float scale);

	public abstract void onScroll(int x, int y);

	public abstract void addHeaderView(View header, android.view.ViewGroup.LayoutParams params);

	public abstract void setHeight(int maximumPullScroll);

	public abstract void setWidth(int maximumPullScroll);

	public abstract void reset();

	public abstract void releaseToRefresh();

	public abstract void refreshing();

	public abstract void pullToRefresh();

	public abstract int getContentSize();

	public abstract void showInvisibleViews();

	public abstract void hideAllViews();

}
