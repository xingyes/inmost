package com.xingy.lib.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
/**
 * 
 * @author apple
 * LinearLayout to implement all api of listview. In order to put this in scrollview
 */
public class LinearListView extends LinearLayout {

	private BaseAdapter mBaseAdapter;

	private View mFooterView;
	private View mHeaderView;


	private boolean rendered = false;
	private boolean hasHeaderView = false;

	private OnItemClickListener mOnItemClickListener;

	private AdapterDataSetObserver mAdapterDataSetObserver;

	public LinearListView(Context context) {
		super(context);
	}

	public LinearListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setAdapter(BaseAdapter adpater) {
		if (null != mBaseAdapter) {
			mBaseAdapter.unregisterDataSetObserver(mAdapterDataSetObserver);
		}

		mBaseAdapter = adpater;
		mAdapterDataSetObserver = new AdapterDataSetObserver();
		mBaseAdapter.registerDataSetObserver(mAdapterDataSetObserver);
		bindLinearLayout();
	}

	private void bindLinearLayout() {
		rendered = true;

		removeAllViews();
		
		if (mHeaderView != null) {
			addView(mHeaderView);
		}
		
		final int count = mBaseAdapter.getCount();
		for (int i = 0; i < count; i++) {
			final View v = mBaseAdapter.getView(i, null, null);
			bindItemClickListener(v, i);
			addView(v);
		}

		if (mFooterView != null) {
			addView(mFooterView);
		}
	}

	public View[] getChildren() {
		int childCount = getChildCount();
		if (mFooterView != null && childCount > 0 && getChildAt(childCount - 1) == mFooterView) {
			childCount--;
		}
		
		if (mHeaderView != null && childCount > 0 && getChildAt(0) == mHeaderView) {
			childCount--;
			hasHeaderView = true;
		}
		
		if (childCount == 0)
			return null;

		View[] views = new View[childCount];

		for (int i = 0 ; i < childCount; i++) {
			if(hasHeaderView)
				views[i] = getChildAt(i+1);
			else
				views[i] = getChildAt(i);

		}

		return views;

	}

	private void bindItemClickListener(final View view, final int position) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( null != mOnItemClickListener) {
					mOnItemClickListener.onItemClick(null, view, position, mBaseAdapter.getItemId(position));
				}
			}
		});
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
		if (rendered == true) {
			final int count = getChildCount() - (mFooterView == null ? 0 : 1) - (mHeaderView == null ? 0 : 1);
			for (int i = 0; i < count; i++) {
				if(hasHeaderView)
					bindItemClickListener(getChildAt(i+1), i+1);
				else
					bindItemClickListener(getChildAt(i), i);
			}
		}
	}

	public void addFooterView(View view) {
		if (mFooterView != null) {
			removeFooterView();
		}
		mFooterView = view;
		addView(view);
	}
	
	public void removeFooterView() {
		if (mFooterView != null) {
			removeView(mFooterView);
		}
	}
	
	public View getFooterView() {
		return mFooterView;
	}
	
	public void addHeaderView(View view) {
		if (mHeaderView != null) {
			removeFooterView();
		}
		
		mHeaderView = view;
		hasHeaderView = true;
		addView(view);
	}
	
	public void removeHeaderView() {
		if (mHeaderView != null) {
			removeView(mHeaderView);
			hasHeaderView = false;
		}
	}
	
	public View getHeaderView() {
		return mHeaderView;
	}
	
	
	public BaseAdapter getAdpater() {
		return mBaseAdapter;
	}

	class AdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			bindLinearLayout();
		}

		@Override
		public void onInvalidated() {
			bindLinearLayout();
		}
	}
}
