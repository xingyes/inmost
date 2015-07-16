package com.handmark.pulltorefresh.library;

public interface ILoadMore {
    
    public void resetFooter();
    
    public void setLoadingMoreSucceed();
    
    public void setLoadingMoreFailed();

    public void setReachEnd();

    public void setOnLoadMoreListener(LoadMoreListener loadMoreListener);
}
