package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xingy.R;
import com.xingy.util.ToolUtil;

import java.util.ArrayList;

public class QuickAnchorView extends View{

	private ArrayList<String> anchors;
	private Paint             paint;
	private onAnchorTouchListener   listener;
	private int screenW;
	private int screenH;
	private int singleHeight = 30;
	private int bgColor;
	private int sumHeight;
	private int topY;
	public QuickAnchorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		parseAttrs(context, attrs);
		init(context);
	}
	public QuickAnchorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		parseAttrs(context, attrs);
		init(context);
	}
	public QuickAnchorView(Context context) {
		super(context);
		init(context);
	}
	
	public void setListener(onAnchorTouchListener alistener)
	{
		this.listener = alistener;
	}
	
	public interface onAnchorTouchListener
	{
		public void onAnchorChanged(String lab);
	};
	
	private void init(Context context) {
		anchors = new ArrayList<String>();
		paint = new Paint();
		
		screenW = ToolUtil.getEquipmentWidth(context);
		screenH = ToolUtil.getEquipmentHeight(context);
		
		bgColor = context.getResources().getColor(R.color.global_gray_light);

	}
	public void addAnchor(String lab)
	{
		anchors.add(lab);
	}
	
	public void setAnchor(ArrayList<String> aSet)
	{
		anchors.addAll(aSet);
	}
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if(null == anchors || anchors.size() <= 0)
			return;
		
		// 每个字母的高度
		sumHeight = singleHeight * anchors.size();
		
		if(sumHeight >= screenH)
		{
			singleHeight = screenH / anchors.size();
		}
		
		sumHeight = singleHeight * anchors.size();
		topY = (screenH - sumHeight )/2;
		
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setTextSize(singleHeight-10f);
		int wi = getWidth();
		for (int i = 0; i < anchors.size(); i++) {

			String it = anchors.get(i);
			float x = (wi - paint.measureText(it))/2;
			float y = topY + singleHeight * i;
			canvas.drawText(it, x, y, paint);

		}
		paint.reset();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float y = event.getY();
		if(y < topY || y > topY + sumHeight)
		{
			setBackgroundColor(0);
            return false;
		}
		
		//通过y方向坐标，转换为字母数组的索引
		int c = (int) (y - topY) / singleHeight;
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			this.setBackgroundColor(bgColor);
            listener.onAnchorChanged(anchors.get(c));
			break;

		case MotionEvent.ACTION_MOVE:
			this.setBackgroundColor(bgColor);
            listener.onAnchorChanged(anchors.get(c));
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		default:
			setBackgroundColor(0);
            
			break;
		}
		invalidate();
		
		return true;
	}
	
	
	
	private void parseAttrs(Context context ,AttributeSet attrs) {
		if( null == attrs || null == context )
			return ;
		
		// Parse attributes.
		if( null != attrs ) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.xingy_attrs);
			singleHeight = UiUtils.getInteger(context, array, R.styleable.xingy_attrs_anchorHeight);
		}
	}
}
