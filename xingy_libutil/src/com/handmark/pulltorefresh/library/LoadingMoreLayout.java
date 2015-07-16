package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xingy.R;

public class LoadingMoreLayout extends FrameLayout{

    public static enum FooterState {
        RESET,
        LOADING,
        LOADING_SUCCESS,
        LOADING_FAILED,
        REACH_END
    }
    
    private FooterState mFooterState;
    
    private View mFooterLoadingView;
    public View mFootRetryView;
    private View mFootReachEndView;
    
    private RetryListener mRetryListener;
    
    public LoadingMoreLayout(Context context) {
        this(context, null);
    }

    public LoadingMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        LayoutInflater.from(context).inflate(
                R.layout.ptr_footer, this, true);
        
        mFooterLoadingView = findViewById(R.id.loading_layout);
        mFootRetryView = findViewById(R.id.footer_retry_view);
        mFootReachEndView = findViewById(R.id.footer_reach_end_view);
        
        mFootRetryView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(mRetryListener != null) {
                    mRetryListener.onRetry();
                }
            }
        });
        
        mFooterState = FooterState.RESET;
    }

    public FooterState getFooterState() {
        return mFooterState;
    }
    
    public void setFootersState(FooterState state) {
        mFooterState = state;
        
        switch (state) {
            case RESET:
            case LOADING:
            case LOADING_SUCCESS:
                mFooterLoadingView.setVisibility(View.VISIBLE);
                mFootRetryView.setVisibility(View.GONE);
                mFootReachEndView.setVisibility(View.GONE);
                break;
            case LOADING_FAILED:
                mFooterLoadingView.setVisibility(View.GONE);
                mFootRetryView.setVisibility(View.VISIBLE);
                mFootReachEndView.setVisibility(View.GONE);
                break;
            case REACH_END:
                mFooterLoadingView.setVisibility(View.GONE);
                mFootRetryView.setVisibility(View.GONE);
                mFootReachEndView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
    
    public void setOnRetryListener(RetryListener retryListener) {
        mRetryListener = retryListener;
    }
    
    public interface RetryListener {
        public void onRetry();
    }
}
