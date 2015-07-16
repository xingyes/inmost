package com.xingy.lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CubicRelativeLayout extends RelativeLayout {

    private int totalHeight;

    public CubicRelativeLayout(Context context) {
        super(context);
    }

    public CubicRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CubicRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        totalHeight = width;

        this.setMeasuredDimension(width, totalHeight);

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, mode),
                MeasureSpec.makeMeasureSpec(totalHeight, mode));

    }


}
