package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xingy.R;

public class ShortcutView extends UiBase implements OnClickListener {
	/**
	 * onShortcutSelectListener
	 * @author xingyao
	 *
	 */
	public interface onShotcutSelectListner {
		/**
		 * onShortcutSelect
		 * @param nIndex
		 */
		public abstract void onShortcutSelect(int nIndex);
	}
	
	/**
	 * Constructor of ShortcutView
	 * @param context
	 * @param attrs
	 */
	public ShortcutView(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.view_shortcut);
	}
	
	public void setTheme(boolean bNightMode) {
		//mSlotmachine.setImageResource(bNightMode ? R.drawable.slot_night : R.drawable.slot_daytime);
		//mViewOrders.setImageResource(bNightMode ? R.drawable.orders_night : R.drawable.orders_daytime);
		//mRecharge.setImageResource(bNightMode ? R.drawable.recharge_night : R.drawable.recharge_daytime);
		//mCollect.setImageResource(bNightMode ? R.drawable.lottery_night : R.drawable.lottery_daytime);
	}
	
	public void setOnShortcutSelectListener(onShotcutSelectListner listener) {
		mListener = listener;
	}
	
	public void setRechargePromoIcon(boolean isVisible) {
		setRechargePromoIcon(null, isVisible);
	}
	
	public void setRechargePromoIcon(Bitmap aBitmap, boolean isVisible) {
		if(null == aBitmap || false == isVisible) {
			mRechargePromoIcon.setVisibility(View.GONE);
			return;
		}
		
		mRechargePromoIcon.setVisibility(View.VISIBLE);
		mRechargePromoIcon.setImageBitmap(aBitmap);
	}
	
	@Override
	public void onClick(View v) {
		if( null == mListener )
			return ;
		
		int idx = 0;
		if(v.getId() == R.id.shortcut_slotmachine)
			idx = 0;
		else if(v.getId() == R.id.shortcut_orders)
			idx = 1;
		else if(v.getId() == R.id.shortcut_collect)
			idx = 2;
		else if(v.getId() == R.id.shortcut_recharge)
			idx = 3;
		
		
		mListener.onShortcutSelect(idx);
	}
	
	/**
	 * initialize the instance.
	 */
	@Override
	protected void onInit(Context context) {
		// Get children components.
		mSlotmachine = (ImageView)findViewById(R.id.shortcut_slotmachine);
		mSlotmachine.setOnClickListener(this);
		mViewOrders = (ImageView)findViewById(R.id.shortcut_orders);
		mViewOrders.setOnClickListener(this);
		mRecharge = (ImageView)findViewById(R.id.shortcut_recharge);
		mRecharge.setOnClickListener(this);
		mCollect = (ImageView)findViewById(R.id.shortcut_collect);
		mCollect.setOnClickListener(this);
		mRechargePromoIcon = (ImageView) findViewById(R.id.shortcut_recharge_promo_icon);
	}
	
	
	private ImageView mSlotmachine;
	private ImageView mViewOrders;
	private ImageView mRecharge;
	private ImageView mCollect;
	private ImageView mRechargePromoIcon;
	private onShotcutSelectListner mListener;
}
