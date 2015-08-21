package com.inmost.imbra.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.inmost.imbra.R;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.main.MainActivity;
import com.xingy.lib.ui.UiUtils;
import com.xingy.preference.Preference;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;

public class SplashActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
	
//	private RelativeLayout modal;
	
	private Preference    defaultPreference;
//	private List<Fragment> mFragmentList;
//	private ViewPageAdapter adapter;
    private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		defaultPreference = Preference.getInstance();
		
		if(IMbraApplication.APP_RUNNING  )
		{
			activityFinish(false);
			return;
		}
		
		setContentView(R.layout.activity_splash);
		
						
		//prestart here
		IMbraApplication.start();
					
		
		// no need to show guildViewPager again
		if (null == defaultPreference || IMbraApplication.mVersionCode <= defaultPreference.getProjVersion()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activityFinish(false);

                }
            },2500);
            return;
		}
		else
			initStartGuildPage();

	}

	
	@Override
	protected void onDestroy() {
		Preference.getInstance().setProjVersion(IMbraApplication.mVersionCode);
        Preference.getInstance().savePreference();
		// for mem
//		if (null != modal)
//			modal.removeAllViews();
//		modal = null;
		vpAdapter = null;
//		adapter = null;
		points = null;
		if (null != viewPager) {
			viewPager.setAdapter(null);
			viewPager.removeAllViews();
		}
		viewPager = null;
		//
		super.onDestroy();
	}

	protected synchronized void activityFinish(final boolean isGuildShowd) {
        UiUtils.startActivity(this, MainActivity.class, true);
        finish();
	}

	// 定义ViewPager对象
	private ViewPager viewPager;

	// 定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;

	// 引导图片资源
	private static final int[] pics = { //
			R.drawable.startup01, //
			R.drawable.startup02,
			R.drawable.startup01 //
			//
	};

	// 底部小点的图片
	private ImageView[] points;

	// 记录当前选中位置
	private int currentIndex;
	private LinearLayout linearLayout;

	private void initStartGuildPage() {
		// 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setVisibility(View.VISIBLE);
		
		// 设置数据
		vpAdapter = new ViewPagerAdapter(); 
		viewPager.setAdapter(vpAdapter);
		
//		viewPager.setOffscreenPageLimit(2);
//	    viewPager.setAdapter(adapter);
		 
		// 设置监听
		viewPager.setOnPageChangeListener(this);

		linearLayout = (LinearLayout) findViewById(R.id.view_idx_layout);

		points = new ImageView[pics.length];
		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			points[i] = new ImageView(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER_VERTICAL;
			int padding = DPIUtil.dip2px(4);
			lp.bottomMargin = DPIUtil.dip2px(6);
			points[i].setImageResource(R.drawable.page_indicator);
			points[i].setLayoutParams(lp);
			points[i].setPadding(padding, padding, padding, padding);

			linearLayout.addView(points[i]);

			// 默认都设为灰色
			points[i].setEnabled(false);
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}

		// 设置当面默认的位置
		currentIndex = 0;
		// 设置为白色，即选中状态
		points[currentIndex].setEnabled(true);
		
		if(pics.length>0)
			linearLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 当滑动状态改变时调用
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	/**
	 * 当当前页面被滑动时调用
	 */

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	private int currentPosition = 0;

	/**
	 * 当新的页面被选中时调用
	 */
	@Override
	public void onPageSelected(int position) {
		// 设置底部小点选中状态
		setCurDot(position);

		currentPosition = position;

		if (position == pics.length - 1) {
			linearLayout.setVisibility(View.GONE);
		} else {
			linearLayout.setVisibility(View.VISIBLE);
		}
		linearLayout.setVisibility(View.VISIBLE);
	}

	
	/**
	 * 设置当前的小点的位置
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}
		points[positon].setEnabled(true);
		points[currentIndex].setEnabled(false);

		currentIndex = positon;
	}
	
	
	
//	public class ViewPageAdapter extends FragmentPagerAdapter{
//		 List<Fragment> mFragementList = new ArrayList<Fragment>();
//		public ViewPageAdapter(FragmentManager fm, List<Fragment> fragementList) {
//			super(fm);
//			mFragementList = fragementList;
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			return (position < mFragementList.size())? mFragementList.get(position):null;
//		}
//
//		@Override
//		public int getCount() {
//			return mFragementList.size();
//		}
//		
//	 @Override
//	  public int getItemPosition(Object object) {
//	      return PagerAdapter.POSITION_NONE;
//	  }
//	}

	public class ViewPagerAdapter extends PagerAdapter {

		// 界面列表
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		/**
		 * 获得当前界面数
		 */
		@Override
		public int getCount() {
			if (pics != null) {
				return pics.length;
			}
			return 0;
		}

		@Override
		public void startUpdate(ViewGroup container) {
			// TODO Auto-generated method stub
			// 实现此抽象方法，防止出现AbstractMethodError
			super.startUpdate(container);
		}

		/**
		 * 初始化position位置的界面
		 */
		@Override
		public Object instantiateItem(View view, int position) {

			View v;

			if (position == getCount() - 1) {
				View page = LayoutInflater.from(getBaseContext()).inflate(R.layout.splash_go_btn, null);
				page.setBackgroundResource(pics[pics.length - 1]);
				View button = page.findViewById(R.id.startup_button_go);

//				RelativeLayout.LayoutParams params = (LayoutParams) button.getLayoutParams();
//				params.width = RelativeLayout.LayoutParams.FILL_PARENT;
//				params.height = DPIUtil.getHeight() / 3;
//				button.setLayoutParams(params);

				if (null != button) {
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// defaultPreference.edit().putBoolean(name, true).commit();
							activityFinish(true);

//							vpAdapter.cleanData();
							vpAdapter.notifyDataSetChanged();
							System.gc();
						}
					});
				}
				v = page;
				((ViewPager) view).addView(page);
			} else {
				ImageView iv = new ImageView(getBaseContext());
				iv.setLayoutParams(mParams);
				iv.setImageResource(pics[position]);
				iv.setScaleType(ImageView.ScaleType.FIT_XY);
				((ViewPager) view).addView(iv);
				
				v = iv;
			}

			return v;
		}

		/**
		 * 判断是否由对象生成界面
		 */
		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return (view == arg1);
		}

		/**
		 * 销毁position位置的界面
		 */
		@Override
		public void destroyItem(View view, int position, Object arg2) {
			((ViewPager) view).removeView((View) arg2);
			System.gc();
		}

//		public void cleanData() {
//			pics = new int[0];
//		}
	}

	private float downX;
	private float upX;
	private final float dinstance = 30;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (currentPosition == pics.length - 1) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = ev.getX();
				break;
			case MotionEvent.ACTION_UP:
				upX = ev.getX();

				if (downX - upX > dinstance) {

					// defaultPreference.edit().putBoolean(name, true).commit();
					activityFinish(true);
//					vpAdapter.cleanData();
//					vpAdapter.notifyDataSetChanged();  todo
					System.gc();
					return true;
				}

				break;
			}

		}
		return super.dispatchTouchEvent(ev);
	}
	
}
