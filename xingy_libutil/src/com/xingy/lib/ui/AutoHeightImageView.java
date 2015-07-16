package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.xingy.R;

public class AutoHeightImageView extends NetworkImageView {

	//public HashMap<String, String>	mCustomInfo = new HashMap<String, String>();
    private int  baseWidth = 0;
    private int  baseHeight = 0;

    public AutoHeightImageView(Context context) {
		super(context);
	}
	
	public AutoHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(attrs);
    }

    protected void parseAttrs(AttributeSet attrs) {
        // Parse attributes.
        Context pContext = this.getContext();

        TypedArray array = pContext.obtainStyledAttributes(attrs, R.styleable.auto_height_attrs);
        if(null!=array) {
            baseWidth = UiUtils.getInteger(pContext, array, R.styleable.auto_height_attrs_baseWidth, 0);
            baseHeight = UiUtils.getInteger(pContext, array, R.styleable.auto_height_attrs_baseWidth, 0);
            array.recycle();
        }
    }
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(baseWidth >0 && baseHeight>0)
        {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height  = baseHeight * width/baseWidth;

            this.setMeasuredDimension(width, height);

            int mode = MeasureSpec.getMode(widthMeasureSpec);
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, mode),
                    MeasureSpec.makeMeasureSpec(height, mode));
        }
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
