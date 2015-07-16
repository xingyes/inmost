/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.nineoldandroids.view.ViewHelper;
import com.xingy.R;

@SuppressLint("ViewConstructor")
public class JDLoadingLayout extends BaseLoadingLayout {

	static final String LOG_TAG = "PullToRefresh-LoadingLayout";

	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

	private TextView mTimeText;
	private ImageView mHeaderSpeed, mHeaderIcon, mHeaderGoods;
	private int mMinHeaderTranslation;
	/** Header的容器 */
	private RelativeLayout mHeaderLayout;

	private RelativeLayout mHeaderContent;

	// private AnimationDrawable mAnimation;

	// private boolean mUseIntrinsicAnimation;

	private Drawable peopleDrawable;
	private Drawable peopleAnimDrawable;
	private Drawable goodsDrawable;

	protected final Mode mMode;
	protected final Orientation mScrollDirection;

	private CharSequence mPullLabel;
	private CharSequence mRefreshingLabel;
	private CharSequence mReleaseLabel;

	public JDLoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
		super(context);
		mMode = mode;
		mScrollDirection = scrollDirection;

		switch (scrollDirection) {
		case HORIZONTAL:
			LayoutInflater.from(context).inflate(R.layout.jd_pull_to_refresh_header, this);
			break;
		case VERTICAL:
		default:
			LayoutInflater.from(context).inflate(R.layout.jd_pull_to_refresh_header, this);
			break;
		}

		mHeaderLayout = (RelativeLayout) findViewById(R.id.pull_header_layout);
		mHeaderContent = (RelativeLayout) findViewById(R.id.headerlayout);
		mHeaderSpeed = (ImageView) findViewById(R.id.speed);
		mHeaderIcon = (ImageView) findViewById(R.id.people);
		mHeaderGoods = (ImageView) findViewById(R.id.goods);
		mTimeText = (TextView) findViewById(R.id.refresh_time);

		peopleDrawable = context.getResources().getDrawable(R.drawable.app_refresh_people_0);
		peopleAnimDrawable = context.getResources().getDrawable(R.drawable.app_refresh_people);
		goodsDrawable = context.getResources().getDrawable(R.drawable.app_refresh_goods_0);

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mHeaderLayout.getLayoutParams();

		switch (mode) {
		case PULL_FROM_END:
			lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;

			// Load in labels
			mPullLabel = context.getString(R.string.pull_to_refresh_header_hint_normal2);
			mRefreshingLabel = context.getString(R.string.pull_to_refresh_header_hint_loading);
			mReleaseLabel = context.getString(R.string.pull_to_refresh_header_hint_ready);
			break;

		case PULL_FROM_START:
		default:
			lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;

			// Load in labels
			mPullLabel = context.getString(R.string.pull_to_refresh_header_hint_normal);
			mRefreshingLabel = context.getString(R.string.pull_to_refresh_header_hint_loading);
			mReleaseLabel = context.getString(R.string.pull_to_refresh_header_hint_ready);
			break;
		}

		// if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
		// Drawable background = attrs.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
		// if (null != background) {
		// ViewCompat.setBackground(this, background);
		// }
		// }
		//
		// if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance)) {
		// TypedValue styleID = new TypedValue();
		// attrs.getValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance, styleID);
		// setTextAppearance(styleID.data);
		// }
		// if (attrs.hasValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance)) {
		// TypedValue styleID = new TypedValue();
		// attrs.getValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance, styleID);
		// setSubTextAppearance(styleID.data);
		// }
		//
		// // Text Color attrs need to be set after TextAppearance attrs
		// if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextColor)) {
		// ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderTextColor);
		// if (null != colors) {
		// setTextColor(colors);
		// }
		// }
		// if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderSubTextColor)) {
		// ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderSubTextColor);
		// if (null != colors) {
		// setSubTextColor(colors);
		// }
		// }

		// Try and get defined drawable from Attrs
		// Drawable imageDrawable = null;
		// if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
		// imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawable);
		// }

		// Check Specific Drawable from Attrs, these overrite the generic
		// drawable attr above
		switch (mode) {
		case PULL_FROM_START:
		default:
			// if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableStart)) {
			// imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableStart);
			// } else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableTop)) {
			// Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
			// imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableTop);
			// }
			break;

		case PULL_FROM_END:
			// if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableEnd)) {
			// imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableEnd);
			// } else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableBottom)) {
			// Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
			// imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableBottom);
			// }
			break;
		}

		// If we don't have a user defined drawable, load the default
		// if (null == imageDrawable) {
		// imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId());
		// }

		// Set Drawable, and save width/height
		// setLoadingDrawable(imageDrawable);

		reset();
	}

	public final void setHeight(int height) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.height = height;
		requestLayout();
	}

	public final void setWidth(int width) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.width = width;
		requestLayout();
	}

	public final int getContentSize() {
		if (null != mHeaderContent) {

			switch (mScrollDirection) {
			case HORIZONTAL:
				return mHeaderContent.getWidth();
			case VERTICAL:
			default:
				return mHeaderContent.getHeight();
			}

		} else {
			return (int) (getResources().getDisplayMetrics().density * 60);

		}
	}

	@Override
	public void onScroll(int x, int y) {
		mMinHeaderTranslation = getContentSize();
		float scale = (float) y / mMinHeaderTranslation;
		float translation = y / PullToRefreshBase.OFFSET_RADIO;
		float offsetX = mMinHeaderTranslation / PullToRefreshBase.OFFSET_RADIO;
		float iconX = -offsetX + translation;
		float goodsX = offsetX - translation;
//		System.out.println("onScroll scale = " + scale + " iconX = " + iconX + " goodsX = " + goodsX);
		setView(mHeaderIcon, scale > 1f ? 1f : scale, scale, iconX);
		setView(mHeaderGoods, scale > 0.7f ? 0.7f : scale, scale, goodsX < 0 ? 0 : goodsX);

	}

	private void setView(View view, float scale, float alpha, float translation) {
		if (Build.VERSION.SDK_INT >= 11) {
//			view.setScaleX(scale);
//			view.setScaleY(scale);
//			view.setAlpha(alpha > 1f ? 1f : alpha);
//			view.setTranslationX(translation > 0 ? 0 : translation);
		} else {
			ViewHelper.setScaleX(view, scale);
			ViewHelper.setScaleY(view, scale);
			ViewHelper.setAlpha(view, scale > 1f ? 1f : scale);
			ViewHelper.setTranslationX(view, translation > 0 ? 0 : translation);
		}
	}

	private void resetDefalut(View view) {
		setView(view, 1f, 1f, 0);
	}

	public final void onPull(float scaleOfLayout) {
		// if (!mUseIntrinsicAnimation) {
		// onPullImpl(scaleOfLayout);
		// }
	}

	public final void pullToRefresh() {
		mHeaderGoods.setVisibility(View.VISIBLE);
//		mHeaderSpeed.setVisibility(View.INVISIBLE);
		mHeaderIcon.setImageDrawable(peopleDrawable);
		mHeaderGoods.setImageDrawable(goodsDrawable);
		mTimeText.setText(mPullLabel);
		// Now call the callback
		// pullToRefreshImpl();
	}

	private AnimationDrawable mAnimation;

	public final void refreshing() {
		mTimeText.setText(mRefreshingLabel);
		if (mAnimation == null) {
			try {
				mHeaderGoods.setVisibility(View.GONE);
				mHeaderGoods.setImageDrawable(new ColorDrawable(0x00000000));
				mAnimation = (AnimationDrawable) mHeaderIcon.getDrawable();
			} catch (ClassCastException e) {
				try {
					mHeaderIcon.setImageDrawable(peopleAnimDrawable);
					mAnimation = (AnimationDrawable) mHeaderIcon.getDrawable();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (mAnimation != null) {
//				mHeaderSpeed.setVisibility(View.VISIBLE);
				resetDefalut(mHeaderIcon);
				// resetDefalut(mHeaderGoods);
				mAnimation.start();
			}
		}
		// if (mUseIntrinsicAnimation) {
		// ((AnimationDrawable) mHeaderIcon.getDrawable()).start();
		// } else {
		// Now call the callback
		// refreshingImpl();
		// }

	}

	public final void releaseToRefresh() {
		mHeaderGoods.setVisibility(View.VISIBLE);
//		mHeaderSpeed.setVisibility(View.INVISIBLE);
		mTimeText.setText(mReleaseLabel);
		// Now call the callback
		// releaseToRefreshImpl();
	}

	public final void reset() {
		if (mAnimation != null) {
			mAnimation.stop();
		}
		mAnimation = null;
//		mHeaderSpeed.setVisibility(View.INVISIBLE);
		mHeaderGoods.setVisibility(View.VISIBLE);
		mHeaderIcon.setImageDrawable(peopleDrawable);
		mHeaderGoods.setImageDrawable(goodsDrawable);
		// if (mUseIntrinsicAnimation) {
		// ((AnimationDrawable) mHeaderIcon.getDrawable()).stop();
		// } else {
		// Now call the callback
		// resetImpl();
		// }

	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		mTimeText.setText(label);
	}

	public final void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable
		mHeaderIcon.setImageDrawable(imageDrawable);
		// mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);

		// Now call the callback
		// onLoadingDrawableSet(imageDrawable);
	}

	public void setPullLabel(CharSequence pullLabel) {
		mPullLabel = pullLabel;
	}

	public void setRefreshingLabel(CharSequence refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(CharSequence releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	@Override
	public void setTextTypeface(Typeface tf) {
	}

	@Override
	public void addHeaderView(View header, ViewGroup.LayoutParams params) {
		if (mHeaderLayout != null) {
			mHeaderLayout.addView(header, params);
		}
	}

	@Override
	public void showInvisibleViews() {

	}

	@Override
	public void hideAllViews() {

	}

	/**
	 * Callbacks for derivative Layouts
	 */
	//
	// protected abstract void onLoadingDrawableSet(Drawable imageDrawable);
	//
	// protected abstract void onPullImpl(float scaleOfLayout);
	//
	// protected abstract void pullToRefreshImpl();
	//
	// protected abstract void refreshingImpl();
	//
	// protected abstract void releaseToRefreshImpl();
	//
	// protected abstract void resetImpl();

}
