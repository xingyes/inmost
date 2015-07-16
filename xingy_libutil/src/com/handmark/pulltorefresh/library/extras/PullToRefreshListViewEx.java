package com.handmark.pulltorefresh.library.extras;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by kangzhihong on 10/14/14.
 * Email:kangzhihong@jd.com
 *
 * 可以单独设置顶部和底部的文字
 */
public class PullToRefreshListViewEx extends PullToRefreshListView {

    private ILoadingLayout headerLoadingLayout, footerLoadingLayout;

    public PullToRefreshListViewEx(Context context) {
        super(context);
        getLoadingLayouts();
    }

    public PullToRefreshListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        getLoadingLayouts();
    }

    public PullToRefreshListViewEx(Context context, Mode mode) {
        super(context, mode);
        getLoadingLayouts();
    }

    public PullToRefreshListViewEx(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        getLoadingLayouts();
    }

    private void getLoadingLayouts() {
        headerLoadingLayout = getHeaderLayout();
        footerLoadingLayout = getFooterLayout();
    }

    public void setHeaderPullLabel(CharSequence s) {
        if (headerLoadingLayout != null) {
            headerLoadingLayout.setPullLabel(s);
        }
    }

    public void setHeaderReleaseLabel(CharSequence s) {
        if (headerLoadingLayout != null) {
            headerLoadingLayout.setReleaseLabel(s);
        }
    }

    public void setHeaderRefreshingLabel(CharSequence s) {
        if (headerLoadingLayout != null) {
            headerLoadingLayout.setRefreshingLabel(s);
        }
    }

    public void setFooterPullLabel(CharSequence s) {
        if (footerLoadingLayout != null) {
            footerLoadingLayout.setPullLabel(s);
        }
    }

    public void setFooterReleaseLabel(CharSequence s) {
        if (footerLoadingLayout != null) {
            footerLoadingLayout.setReleaseLabel(s);
        }
    }

    public void setFooterRefreshingLabel(CharSequence s) {
        if (footerLoadingLayout != null) {
            footerLoadingLayout.setRefreshingLabel(s);
        }
    }
}
