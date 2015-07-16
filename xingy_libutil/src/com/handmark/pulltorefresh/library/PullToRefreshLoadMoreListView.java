package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.LoadingMoreLayout.FooterState;

/**
 * 下滑到底部自动加载更多
 * @author litingchang
 *
 */
public class PullToRefreshLoadMoreListView extends PullToRefreshListView implements ILoadMore{
	
	private Context mContext;

    private LoadMoreListener mLoadMoreListener;
    private LoadingMoreLayout mLoadingMoreLayout;

	public PullToRefreshLoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public PullToRefreshLoadMoreListView(Context context) {
		this(context, null);
	}
	
	private void init() {
		
		this.setMode(Mode.PULL_FROM_START);
		
		mLoadingMoreLayout = new LoadingMoreLayout(mContext);
		mLoadingMoreLayout.setOnRetryListener(new LoadingMoreLayout.RetryListener() {
            
            @Override
            public void onRetry() {
                if (mLoadMoreListener != null) {
//                    if(!NetworkUtil.isNetworkAvailable(mContext)) {
//                        ToastUtil.shortToast(mContext, R.string.lottery_no_network);
//                        return;
//                    }
                    mLoadingMoreLayout.setFootersState(FooterState.LOADING);
                    mLoadMoreListener.loadMore();
                }
            }
        });

        this.getRefreshableView().addFooterView(mLoadingMoreLayout, null, false);
		
		this.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
			    if (mLoadingMoreLayout.getFooterState() == FooterState.LOADING || 
                        mLoadingMoreLayout.getFooterState() == FooterState.REACH_END ||
                        mLoadingMoreLayout.getFooterState() == FooterState.LOADING_FAILED) {
                    return;
                }

			    if (mLoadMoreListener != null) {
			        mLoadingMoreLayout.setFootersState(FooterState.LOADING);
                    mLoadMoreListener.loadMore();
                }
			}
		});
	}

	@Override
	public void resetFooter() {
	    mLoadingMoreLayout.setFootersState(FooterState.RESET);
	}
	
	@Override
	public void setLoadingMoreSucceed() {
	    mLoadingMoreLayout.setFootersState(FooterState.LOADING_SUCCESS);
    }
    
	@Override
    public void setLoadingMoreFailed() {
        mLoadingMoreLayout.setFootersState(FooterState.LOADING_FAILED);
    }
	
	@Override
    public void setReachEnd() {
        mLoadingMoreLayout.setFootersState(FooterState.REACH_END);
    }

	@Override
    public void setOnLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }
}
