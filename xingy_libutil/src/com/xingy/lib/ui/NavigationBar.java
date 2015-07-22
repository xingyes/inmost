package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingy.R;

public class NavigationBar extends RelativeLayout implements OnClickListener {
	
	public interface OnLeftButtonClickListener {
		public abstract void onClick();
	}
	
	/**
	 * Constructor of NavigationBar
	 * @param context
	 * @param attrs
	 */
	public NavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		parseAttrs(attrs);
	}
	
	public ImageView getLeftBack()
	{
		return mLeftBack;
	}
	
	public ImageView getIndicator()
	{
		return mIndicator;
	}
	
	public void setText(int nResId) {
		this.setText(mContext.getString(nResId));
	}

    public void setTextColor(int color)
    {
        if( null != mText) {
            mText.setTextColor(color);
        }
    }
	public void setText(String strText) {
		if( null != mText && !TextUtils.isEmpty(strText) ) {
			mCaption = strText;
			mText.setText(strText);
		}
	}
	
	public void setRightText(String strText) {
		setRightText(strText, null);
	}
	
	public void setRightText(String strText, Drawable pDrawable) {
		if( TextUtils.isEmpty(strText) ) {
			mAction.setVisibility(View.GONE);
			mIndicator.setImageDrawable(pDrawable);
			mIndicator.setVisibility(View.VISIBLE);
		} else {
			mIndicator.setVisibility(View.GONE);
			mAction.setVisibility(View.VISIBLE);
			mAction.setText(strText);
			
			mAction.setCompoundDrawables(null != pDrawable ? pDrawable : null, null, null, null);
		}
	}
	
	public void setLeftText(String strText) {
		if( null != mLeftText && !TextUtils.isEmpty(strText) ) {
			tLeftText = strText;
			mLeftText.setText(strText);
		}
	}
	
	
	public void setRightInfo(int nResId, OnClickListener listener) {
		setRightInfo(getContext().getString(nResId), listener);
	}
	
	public void setRightInfo(String strText, OnClickListener listener) {
		if( null != mAction && !TextUtils.isEmpty(strText) ) {
			mAction.setVisibility(View.VISIBLE);
			mAction.setText(strText);
			mAction.setOnClickListener(listener);
		}
	}
	
	public void setOnIndicatorClickListener(OnClickListener listener) {
		if( null != mIndicator ) {
            mIndicator.setOnClickListener(null);
			mIndicator.setOnClickListener(listener);
        }
	}
	
	public void setRightVisibility(int pVisibilit) {
		if( null != mAction  ) {
			mAction.setVisibility( pVisibilit );
		}
		
		if( null != mIndicator  ) {
			mIndicator.setVisibility( pVisibilit );
		}
	}
	
	public void setLeftVisibility(int pVisibilit) {
		if( null != mLeftBack  ) {
			mLeftBack.setVisibility( pVisibilit );
		}
	}
	
	public void setOnDrawableRightClickListener(OnClickListener listener) {
		if( null != mAction ) {
			mAction.setOnClickListener(listener);
		}
		
		if( null != mIndicator ) {
			mIndicator.setOnClickListener(listener);
		}
	}
	
	public void setOnLeftButtonClickListener(OnLeftButtonClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onClick(View v) {
		//if(v.getId() == R.id.navigationbar_text ||
		if(v.getId() == R.id.navbar_back)
		{
			if( (null != mListener) && (null != mLeftBack) && (View.VISIBLE == mLeftBack.getVisibility()) )
				mListener.onClick();
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init(mContext);
	}
	
	private int ID_NAV_LAYOUT = 1001;
	private void init(Context context) {
		// Inflate.
		if( null == context )
			return ;
		
		setBackgroundColor(0xFFF6F6F6);
		mRootView = inflate(context, R.layout.navigationbar_layout, null);
		mRootView.setBackgroundColor(mBgColor);
		mRootView.setId(ID_NAV_LAYOUT);
		addView(mRootView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		View divider = new View(context);//inflate(context, R.layout.divider_1, null);
		divider.setBackgroundColor(0xeee9e9e9);
//		divider.setBackgroundColor(Color.RED);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 2);
		params.addRule(BELOW, ID_NAV_LAYOUT);
		divider.setLayoutParams(params);
		addView(divider, params);
		
		mLeftText = (TextView)findViewById(R.id.navigationbar_text_left);
		if(TextUtils.isEmpty(tLeftText))
		{
			mLeftText.setVisibility(View.GONE);
		}
		else
		{
			mLeftText.setText(tLeftText);
			mLeftText.setTextColor(mTextColor);
			mLeftText.setVisibility(View.VISIBLE);
		}
		
		// Get children components.
		mText = (TextView)findViewById(R.id.navigationbar_text);
		mText.setTextColor(mTextColor);
		mText.setText(mCaption);
		mText.setOnClickListener(this);
		
		mAction = (TextView)findViewById(R.id.navigationbar_right_text);
		if(mActionBg == 0)
			findViewById(R.id.right_layout).setBackgroundResource(R.drawable.global_transparent_shadow_click_state);
		else
            findViewById(R.id.right_layout).setBackgroundResource(mActionBg);
		
		mAction.setTextColor(mTextColor);
		mIndicator = (ImageView)findViewById(R.id.navigationbar_right_icon);
		if( mDrawableId != 0 ) {
			mIndicator.setImageResource(mDrawableId);
			mIndicator.setVisibility(View.VISIBLE);
		}

		mLeftBack = (ImageView) findViewById(R.id.navigationbar_drawable_left);
		if( mLeftDrawableId != 0 ) {
			mLeftBack.setImageResource(mLeftDrawableId);
		}
		findViewById(R.id.navbar_back).setOnClickListener(this);
		findViewById(R.id.navbar_back).setVisibility(bLeftVisiable ? View.VISIBLE : View.INVISIBLE);
		
	}
	
	private void parseAttrs(AttributeSet attrs) {
		if( null == attrs || null == mContext )
			return ;
		
		// Parse attributes.
		if( null != attrs ) {
			TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.xingy_attrs);
			mTextColor = UiUtils.getColor(mContext, array, R.styleable.xingy_attrs_textColor);
			mBgColor = UiUtils.getColor(mContext, array, R.styleable.xingy_attrs_bgColor);
			mCaption = UiUtils.getString(mContext, array, R.styleable.xingy_attrs_text);
			mDrawableId = UiUtils.getResId(mContext, array, R.styleable.xingy_attrs_drawableRight);
			mLeftDrawableId = UiUtils.getResId(mContext, array, R.styleable.xingy_attrs_drawableLeft);
			bLeftVisiable = UiUtils.getBoolean(mContext, array, R.styleable.xingy_attrs_leftVisiable);
			tLeftText = UiUtils.getString(mContext, array, R.styleable.xingy_attrs_leftext);
			mActionBg = UiUtils.getResId(mContext, array, R.styleable.xingy_attrs_actionBg);

            array.recycle();
		}
	}
	
	public void setBgColor(int color)
	{
		mBgColor = color;
		if(null!=mRootView)
		{
		mRootView.setBackgroundColor(mBgColor);
		mRootView.invalidate();
		}
	}
	
	private View           mRootView;
	private Context        mContext;
	private String         mCaption;
	private TextView       mText;
	private TextView       mLeftText;
	private int            mBgColor = 0;
	private int            mTextColor = 0;
	private int            mDrawableId = 0;
	private int 		   mLeftDrawableId = 0;
	private boolean        bLeftVisiable = false;
	private String         tLeftText;
	private TextView       mAction;
	private int            mActionBg = 0;
	private ImageView      mIndicator;
	private ImageView      mLeftBack;
	private OnLeftButtonClickListener mListener;	
}
