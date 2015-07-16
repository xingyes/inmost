package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingy.R;

public class CheckBox extends UiBase {

	private boolean isChecked;
	private int mImageOnId;
	private int mImageOffId;
	private String mTextContent;
	private TextView mText;
	private ImageView mButton;
	private int	mTextColor;
	private float mTextSize;
	private View mCheckBoxView;
	private OnCheckedChangeListener mOnCheckedChangeListener;

	public CheckBox(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.checkbox_layout);
	}
	
	public CheckBox(Context context) {
		super(context, R.layout.checkbox_layout);
	}

	@Override
	protected void parseAttrs(Context pContext, TypedArray array) {
		// Parse attributes.
		isChecked = UiUtils.getBoolean(pContext, array, R.styleable.xingy_attrs_isChecked, true);
		mImageOnId = UiUtils.getResId(pContext, array, R.styleable.xingy_attrs_checkboxImageOn, 0);
		mImageOffId = UiUtils.getResId(pContext, array, R.styleable.xingy_attrs_checkboxImageOff, 0);
		mTextContent = UiUtils.getString(pContext, array, R.styleable.xingy_attrs_text);
		mTextColor = UiUtils.getColor(pContext, array, R.styleable.xingy_attrs_contentColor);
		mTextSize = UiUtils.getDimension(pContext, array, R.styleable.xingy_attrs_contentSize);
	}


	public void setTextContent(String aContent)
	{
		mTextContent = aContent;
		mText.setVisibility(View.VISIBLE);
		mText.setText(mTextContent);
		mText.setTextColor(mTextColor);
		mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
	}
	@Override
	protected void onInit(Context aContext) {
		super.onInit(aContext);
		mButton = (ImageView) findViewById(R.id.checkbox_button);
		mText = (TextView) findViewById(R.id.checkbox_text);
		mCheckBoxView = findViewById(R.id.checkbox_view);
		
		if(mImageOnId > 0 && mImageOffId > 0) {
			if(isChecked) {
				mButton.setImageDrawable(getResources().getDrawable(mImageOnId));
			}else{
				mButton.setImageDrawable(getResources().getDrawable(mImageOffId));
			}
		}
		
		if(!TextUtils.isEmpty(mTextContent)) {
			mText.setVisibility(View.VISIBLE);
			mText.setText(mTextContent);
			mText.setTextColor(mTextColor);
			mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		}else{
			mText.setVisibility(View.GONE);
		}
		
		mCheckBoxView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				setChecked(!isChecked);
			}
		});
		
		setChecked(isChecked);
	}
	
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
        	isChecked = checked;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChange(isChecked);
            }
            
            if(mImageOnId > 0 && mImageOffId > 0) {
    			if(isChecked) {
    				mButton.setImageDrawable(getResources().getDrawable(mImageOnId));
    			}else{
    				mButton.setImageDrawable(getResources().getDrawable(mImageOffId));
    			}
    		}
        }
    }
    
    public boolean isChecked(){
    	return this.isChecked;
    }

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}
	
	public interface OnCheckedChangeListener{
		public void onCheckedChange(Boolean isChecked);
	}

}
