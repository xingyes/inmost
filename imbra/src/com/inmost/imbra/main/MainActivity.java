package com.inmost.imbra.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.basic.BasicParamModel;
import com.inmost.imbra.basic.ParamDtModel;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.MyInfoActivity;
import com.inmost.imbra.login.VerifyLoginActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.IPageCache;
import com.xingy.lib.ui.NavigationBar.OnLeftButtonClickListener;
import com.xingy.lib.ui.UiUtils;
import com.xingy.lib.ui.VerticalRangeSeekBar;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
        AdapterView.OnItemClickListener,HomeFragment.SearchCheckListener,
        RadioGroup.OnCheckedChangeListener{

	public static final String REQUEST_SEARCH_MODEL = "search_model";

//	private Handler  mHandler = new Handler(){
//		@Override
//		public void handleMessage(Message msg)
//		{
//			Animation itemPopOutAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_oversize);
//			switch(msg.what)
//			{
//			case ITEM_ANIM_MSG_BASE:
//				menuViews.to_main.startAnimation(itemPopOutAnim);
//				menuViews.to_main.setVisibility(View.VISIBLE);
//				break;
//			case ITEM_ANIM_MSG_BASE+1:
//				menuViews.to_spirit.startAnimation(itemPopOutAnim);
//				menuViews.to_spirit.setVisibility(View.VISIBLE);
//				break;
//			case ITEM_ANIM_MSG_BASE+2:
//				menuViews.to_qa.startAnimation(itemPopOutAnim);
//				menuViews.to_qa.setVisibility(View.VISIBLE);
//				break;
//			case ITEM_ANIM_MSG_BASE+3:
//				menuViews.to_gallery.startAnimation(itemPopOutAnim);
//				menuViews.to_gallery.setVisibility(View.VISIBLE);
//				break;
//			case ITEM_ANIM_MSG_BASE+4:
//				menuViews.to_designer.startAnimation(itemPopOutAnim);
//				menuViews.to_designer.setVisibility(View.VISIBLE);
//				break;
//			case ITEM_ANIM_MSG_BASE+5:
//				menuViews.to_hot_product.startAnimation(itemPopOutAnim);
//				menuViews.to_hot_product.setVisibility(View.VISIBLE);
//				break;
//			case ITEM_ANIM_MSG_BASE+6:
//				menuViews.to_my.startAnimation(itemPopOutAnim);
//				menuViews.to_my.setVisibility(View.VISIBLE);
//				break;
//			default:
//				break;
//			}
//			if(msg.what >=ITEM_ANIM_MSG_BASE+6)
//				removeCallbacksAndMessages(null);
//			else
//				this.sendEmptyMessageDelayed(msg.what+1, menuViews.itemAnimInterval);
//
//			super.handleMessage(msg);
//		}
//	};

    private Ajax mAjax;

    private FragmentManager      mFragmentManager;
    private int                  pgIdx;
    private static final int     PG_IDX_HOME = 0;
    private static final int     PG_IDX_BLOG = 1;
    private static final int     PG_IDX_LOOK = 2;
    //recomend -- home
	private HomeFragment         mHomePg;

    //blog
    private BlogFragment         mBlogPg;

    //looklook
    private LookFragment         mLookPg;

    //o2o
    private O2OFragment mO2OPg;

    private ImageLoader mImgLoader;

    private TextView     navRightView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.activity_main);

        Intent ait = getIntent();
        if(null!=ait)
        {
//        	mEventId = ait.getStringExtra(EventDetailActivity.EVENT_ID);
        }

        initParams();

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        loadNavBar(R.id.main_navbar);

        mNavBar.setOnLeftButtonClickListener(new OnLeftButtonClickListener(){

			@Override
			public void onClick() {
				if(menuViews.menu_layout.getVisibility() != View.VISIBLE)
				{
					menuViews.left_layout.startAnimation(menuViews.menuInAnim);
					menuViews.menu_layout.setVisibility(View.VISIBLE);
				}
				else
				{
					hideLeftMenu();
				}
			}});

        mNavBar.setRightInfo(R.string.filter,this);
        navRightView = (TextView) mNavBar.findViewById(R.id.navigationbar_right_text);
        navRightView.setTextColor(getResources().getColor(R.color.global_pink));
        initLeftMenu();




        mFragmentManager = this.getSupportFragmentManager();

        pgIdx = PG_IDX_HOME;
        showHomePg();

	}


    /**
	 * 
	 * @return 
	 */
	private FragmentTransaction hideFragments() {
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况  
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if(null!=mBlogPg && mBlogPg.isAdded())
            transaction.hide(mBlogPg);
        if(null!=mLookPg && mLookPg.isAdded())
            transaction.hide(mLookPg);
        if(null!=mHomePg && mHomePg.isAdded())
            transaction.hide(mHomePg);
        if(null!=mO2OPg && mO2OPg.isAdded())
            transaction.hide(mO2OPg);


        return transaction;
    }

	/**
	 * 
	 */
	private void showHomePg() {
        if(null == mHomePg)
        {
            mHomePg = new HomeFragment();
            mHomePg.setImgLoader(mImgLoader);
            mHomePg.setTabListener(this);
            hideFragments().add(R.id.main_content,mHomePg).show(mHomePg).commitAllowingStateLoss();
        }
        else
            hideFragments().show(mHomePg).commitAllowingStateLoss();
        navRightView.setVisibility(View.VISIBLE);

    }

	private void showLookPg() {
        if(null == mLookPg)
        {
            mLookPg = new LookFragment();
            mLookPg.setImgLoader(mImgLoader);
            hideFragments().add(R.id.main_content,mLookPg).show(mLookPg).commitAllowingStateLoss();
        }
        else
            hideFragments().show(mLookPg).commitAllowingStateLoss();
        navRightView.setVisibility(View.GONE);
	}

    private void showBlogPg() {
        if(null == mBlogPg)
        {
            mBlogPg   = new BlogFragment();
            mBlogPg.setImgLoader(mImgLoader);
            hideFragments().add(R.id.main_content,mBlogPg).show(mBlogPg).commitAllowingStateLoss();
        }
        else
            hideFragments().show(mBlogPg).commitAllowingStateLoss();
        navRightView.setVisibility(View.GONE);
    }

    private void showO2OPg() {
        if(null == mO2OPg)
        {
            mO2OPg = new O2OFragment();
            mO2OPg.setImgLoader(mImgLoader);
            hideFragments().add(R.id.main_content,mO2OPg).show(mO2OPg).commitAllowingStateLoss();
        }
        else
            hideFragments().show(mO2OPg).commitAllowingStateLoss();
        navRightView.setVisibility(View.GONE);

    }

	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigationbar_right_text:
                initOptPanel();
                if(mOptPanelHolder.optLayout.getVisibility()==View.VISIBLE)
                    hideOptLayout();
                else
                    showOptLayout();
                break;
            /**
             * filter panel click
             */
            case R.id.clear_filter:
                clearFilter();
                break;
            case R.id.submit_filter:
                hideOptLayout();
                if(mOptPanelHolder.optPager.getCurrentItem() == PARAM_TAB_PRICE ||
                        mOptPanelHolder.priceActive) {

                    mSearchParams.filterLowPriceIdx = mOptPanelHolder.priceSeekbar.getSelectedMinValue() /
                            mOptPanelHolder.priceSeekbar.getStep();

                    mSearchParams.filterHighPriceIdx = mOptPanelHolder.priceSeekbar.getSelectedMaxValue()/
                            mOptPanelHolder.priceSeekbar.getStep();
                }
                if(null!=mHomePg)
                    mHomePg.onFilterSubmit();
                break;
            /**
             * menu layout click
             */
            case R.id.call_help_tv:
                UiUtils.startActivity(this,ContactUsActivity.class,true);
                hideLeftMenu();
                break;
            case R.id.user_layout:
                Account act = ILogin.getActiveAccount();
//                if(null==act)
//                    UiUtils.startActivity(this,LoginActivity.class,true);
//                else
                    UiUtils.startActivity(this,MyInfoActivity.class,true);
                hideLeftMenu();
                break;

            default:
                hideOptLayout();
                break;
        }
    }

	public void onBackPressed()
	{
		if(menuViews.menu_layout.getVisibility() == View.VISIBLE)
		{
			this.hideLeftMenu();
		}
        else if(null!=mOptPanelHolder && mOptPanelHolder.optLayout.getVisibility()==View.VISIBLE)
            hideOptLayout();
        else
			super.onBackPressed();
	}
	
	@Override
	protected void onNewIntent(Intent ait)
	{
		if(null == ait)
			return;

//		mSearchModel = (SearchModel) ait.getSerializableExtra(REQUEST_SEARCH_MODEL);
//		if(null != mSearchModel)
//		{
//			hideLeftMenu();
//			mResultPg.dataType = ResultListFragment.TYPE_SPIRIT;
//			mResultPg.mSearchModel = this.mSearchModel;
//			showLookPg();
//		}
		
		super.onNewIntent(ait);
	}

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(mOptPanelHolder.optTabIdx == checkedId)
            return;

        mOptPanelHolder.optTabIdx = checkedId;

            switch (checkedId)
        {
            case R.id.tab_tag_rb:
                mOptPanelHolder.optPager.setCurrentItem(PARAM_TAB_TAG);
                break;
            case R.id.tab_price_rb:
                mOptPanelHolder.priceActive = true;
                mOptPanelHolder.optPager.setCurrentItem(PARAM_TAB_PRICE);
                break;
            case R.id.tab_brand_rb:
            default:
                mOptPanelHolder.optPager.setCurrentItem(PARAM_TAB_BRAND);
                break;
        }
    }


    /**
     * Menu layout ----------------------------------------------------------------------------------------------------
     */
    public class menuViewHolder
    {
        public RelativeLayout menu_layout;
        public View left_layout;

        public ImageView userImg;
        public TextView  userNameTv;
        public ListView menuListView;
        public MenuAdapter tagAdapter;


        public TranslateAnimation menuInAnim;
        protected long menuInInterval = 600;

        public TranslateAnimation menuOutAnim;
        protected long menuOutInterval = 300;

//		protected long itemAnimInterval = 20;
//		protected long itemInInterval = 600;
//		public TranslateAnimation itemOutAnim;
    };
    private menuViewHolder menuViews;

    /**
     * menu item
     */
    public class MenuItemHolder
    {
        ImageView   iv;
        TextView    tv;
    }
    public class MenuAdapter extends BaseAdapter{
        public   int iconRes[] = {R.drawable.icon_bramax_nor,
                R.drawable.icon_blog_nor,R.drawable.icon_look_nor,
                R.drawable.icon_o2o_nor,R.drawable.icon_setting_nor};
        public  int [] menuName = {R.string.menu_bra_max,R.string.menu_blog,
                R.string.menu_look, R.string.menu_o2o,R.string.menu_setting};
        @Override
        public int getCount() {return 5;}

        @Override
        public Object getItem(int position) { return position;}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItemHolder holder = null;
            if (null == convertView)
            {
                convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.menu_item, null);
                holder = new MenuItemHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.item_icon);
                holder.tv = (TextView) convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            }
            else
            {
                holder = (MenuItemHolder) convertView.getTag();
            }
            holder.iv.setImageResource(iconRes[position]);
            holder.tv.setText(menuName[position]);

            return convertView;
        }
    };

    protected void hideLeftMenu() {
        menuViews.left_layout.startAnimation(menuViews.menuOutAnim);
    }

    private void initLeftMenu() {
        menuViews = new menuViewHolder();
        menuViews.menu_layout = (RelativeLayout) this.findViewById(R.id.menu_layout);
        menuViews.menu_layout.setVisibility(View.GONE);

        findViewById(R.id.right_layout).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menuViews.menu_layout.getVisibility() == View.GONE)
                    return false;
                //else visiable
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideLeftMenu();
                }
                return true;
            }
        });

        menuViews.userImg = (ImageView)this.findViewById(R.id.user_img);
        menuViews.userNameTv = (TextView)this.findViewById(R.id.user_name);
        findViewById(R.id.user_layout).setOnClickListener(this);

        menuViews.left_layout = this.findViewById(R.id.left_layout);
        menuViews.left_layout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menuViews.menu_layout.getVisibility() == View.GONE)
                    return false;
                else
                    return true;
            }
        });

        findViewById(R.id.call_help_tv).setOnClickListener(this);
        menuViews.menuListView = (ListView) this.findViewById(R.id.menu_list_view);
        menuViews.tagAdapter =  new MenuAdapter();
        menuViews.menuListView.setAdapter(menuViews.tagAdapter);
        menuViews.menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0: //look
                        hideLeftMenu();
                        showLookPg();
                        break;
                    case 1: //blog
                        hideLeftMenu();
                        showBlogPg();
                        break;
                    case 3: //o2o
                        hideLeftMenu();
                        showO2OPg();
                        break;
                    case 4:
                        UiUtils.startActivity(MainActivity.this, VerifyLoginActivity.class,true);
                        // setting
                        break;
                    case 2:
                    default://home
                        hideLeftMenu();
                        showHomePg();
                        break;
                }
            }
        });


        menuViews.menuInAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1,Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,0);
        menuViews.menuInAnim.setFillAfter(true);
        menuViews.menuInAnim.setDuration(menuViews.menuInInterval);

        menuViews.menuOutAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,-1,
                Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,0);
        menuViews.menuOutAnim.setFillAfter(true);
        menuViews.menuOutAnim.setDuration(menuViews.menuOutInterval);
        menuViews.menuOutAnim.setAnimationListener(new AnimationListener(){

            @Override
            public void onAnimationEnd(Animation animation) {
                menuViews.menu_layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }
    /**
     *  end of menu layout ------------------------------------------------------------------------
     */






    /**
     * global opt params  ---------------------------------------------------------
     */
    public class optPanelHolder
    {
        public View optLayout;
        public RelativeLayout optContent;

        public RadioGroup optTab;
        public int        optTabIdx;
        public ViewPager optPager;
        public ListView        brandListv;
        public ListView        tagListv;
        public View            priceView;
        public boolean         priceActive;
        public VerticalRangeSeekBar<Integer> priceSeekbar;
        public OptPanelAdapter brandAdapter;
        public OptPanelAdapter tagAdapter;
        public View            filterBtnLayout;

        public GridView homeTagGridV;
        public HomeTagAdapter homeTagAdapter;


        public TranslateAnimation optInAnim;
        public TranslateAnimation optOutAnim;
    }
    public BasicParamModel mSearchParams;

    private optPanelHolder mOptPanelHolder;
    private ArrayList<View> pageViews;
    private OptPanelPagerAdapter mPanelPgAdapter;

    public static final int PARAM_TAB_BRAND = 0;
    public static final int PARAM_TAB_TAG = 1;
    public static final int PARAM_TAB_PRICE = 2;
    private int tabIDs[] = {R.id.tab_brand_rb,R.id.tab_tag_rb,R.id.tab_price_rb};

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(parent == mOptPanelHolder.tagListv)
        {
            mOptPanelHolder.tagAdapter.setPickIdx(position);
            mSearchParams.filterFuncIdx = position;
            mOptPanelHolder.tagAdapter.notifyDataSetChanged();
        }
        else if(parent == mOptPanelHolder.brandListv)
        {
            mOptPanelHolder.brandAdapter.setPickIdx(position);
            mSearchParams.filterBrandIdx = position;
            mOptPanelHolder.brandAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTabChecked(int tabidx) {
        if(null == mOptPanelHolder)
            initOptPanel();

        if(tabidx == HomeFragment.TAB_PRO)
        {
            mOptPanelHolder.optPager.setVisibility(View.VISIBLE);
            mOptPanelHolder.optTab.setVisibility(View.VISIBLE);
            mOptPanelHolder.filterBtnLayout.setVisibility(View.VISIBLE);
            mOptPanelHolder.homeTagGridV.setVisibility(View.GONE);
        }
        else
        {
            mOptPanelHolder.optPager.setVisibility(View.GONE);
            mOptPanelHolder.optTab.setVisibility(View.GONE);
            mOptPanelHolder.filterBtnLayout.setVisibility(View.GONE);
            mOptPanelHolder.homeTagGridV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBrandParamCancel() {
        mSearchParams.filterBrandIdx = -1;
        mOptPanelHolder.brandAdapter.setPickIdx(-1);
        mOptPanelHolder.brandAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFuncParamCancel() {
        mSearchParams.filterFuncIdx = -1;
        mOptPanelHolder.tagAdapter.setPickIdx(-1);
        mOptPanelHolder.tagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPriceParamCancel() {
        mOptPanelHolder.priceActive = false;
        mSearchParams.filterHighPriceIdx = -1;
        mSearchParams.filterLowPriceIdx = -1;
    }


    /**
     *
     */
    private void initOptPanel()
    {
        if(mOptPanelHolder!=null)
            return;

        /**
         * opt panel
         */
        mOptPanelHolder =  new optPanelHolder();
        mOptPanelHolder.optLayout = findViewById(R.id.option_layout);
        mOptPanelHolder.optContent = (RelativeLayout)findViewById(R.id.opt_content);
        mOptPanelHolder.optTab = (RadioGroup)findViewById(R.id.extra_opt_tab);
        mOptPanelHolder.optTab.setOnCheckedChangeListener(this);

        /**
         * home tag panel
         */
        mOptPanelHolder.homeTagGridV = (GridView) findViewById(R.id.home_tag_grid);
        mOptPanelHolder.homeTagAdapter = new HomeTagAdapter();
        mOptPanelHolder.homeTagGridV.setAdapter(mOptPanelHolder.homeTagAdapter);
        mOptPanelHolder.homeTagGridV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOptPanelHolder.homeTagAdapter.mPickIdx = position;
                mOptPanelHolder.homeTagAdapter.notifyDataSetChanged();
                mSearchParams.filterHomeIdx = position;

                mHomePg.onFilterSubmit();

                hideOptLayout();
            }
        });
        /**
         * brand listview
         */
        pageViews = new ArrayList<View>();
        mOptPanelHolder.brandListv = new ListView(this);
        mOptPanelHolder.brandListv.setDividerHeight(0);
        mOptPanelHolder.brandListv.setSelector(R.color.transparent);

        mOptPanelHolder.brandAdapter = new OptPanelAdapter(this);
        mOptPanelHolder.brandListv.setAdapter(mOptPanelHolder.brandAdapter);
        mOptPanelHolder.brandListv.setOnItemClickListener(this);
        mOptPanelHolder.tagListv = new ListView(this);
        mOptPanelHolder.tagListv.setDividerHeight(0);
        mOptPanelHolder.tagListv.setSelector(R.color.transparent);

        mOptPanelHolder.tagAdapter = new OptPanelAdapter(this);
        mOptPanelHolder.tagListv.setOnItemClickListener(this);
        mOptPanelHolder.tagListv.setAdapter(mOptPanelHolder.tagAdapter);

        mOptPanelHolder.priceView = getLayoutInflater().inflate(R.layout.opt_price_pg,null);
        mOptPanelHolder.priceSeekbar= (VerticalRangeSeekBar)mOptPanelHolder.priceView.findViewById(R.id.price_seekbar);
//        mOptPanelHolder.priceSeekbar.setOnRangeSeekBarChangeListener(new VerticalRangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
//            @Override
//            public void onRangeSeekBarValuesChanged(VerticalRangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
//                mSearchParams.filterLowPrice = minValue;
//                mSearchParams.filterHighPrice = maxValue;
//            }
//        });


        pageViews.add(mOptPanelHolder.brandListv);
        pageViews.add(mOptPanelHolder.tagListv);
        pageViews.add(mOptPanelHolder.priceView);
        mPanelPgAdapter = new OptPanelPagerAdapter(pageViews);

        mOptPanelHolder.optPager = (ViewPager)findViewById(R.id.tab_vpager);
        mOptPanelHolder.optPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                onCheckedChanged(mOptPanelHolder.optTab,tabIDs[i]);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mOptPanelHolder.optPager.setAdapter(mPanelPgAdapter);

        mOptPanelHolder.filterBtnLayout = findViewById(R.id.filter_button_layout);
        findViewById(R.id.clear_filter).setOnClickListener(this);
        findViewById(R.id.submit_filter).setOnClickListener(this);

        mOptPanelHolder.optLayout.setVisibility(View.GONE);

        findViewById(R.id.opt_blank).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOptPanelHolder.optLayout.getVisibility() == View.GONE)
                    return false;
                //else visiable
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideOptLayout();
                }
                return true;
            }
        });

        mOptPanelHolder.optContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOptPanelHolder.optLayout.getVisibility() == View.GONE)
                    return false;
                else
                    return true;
            }
        });



    }


    /**
     *
     */
    private void hideOptLayout() {
        if(mOptPanelHolder.optOutAnim == null)
        {
            mOptPanelHolder.optOutAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,1,
                    Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,0);
            mOptPanelHolder.optOutAnim.setDuration(menuViews.menuOutInterval);
            mOptPanelHolder.optOutAnim.setFillAfter(true);
            mOptPanelHolder.optOutAnim.setAnimationListener(new Animation.AnimationListener(){

                @Override
                public void onAnimationEnd(Animation animation) {
                    mOptPanelHolder.optLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }});
        }
        mOptPanelHolder.optContent.startAnimation(mOptPanelHolder.optOutAnim);
    }

    private void showOptLayout() {
        mOptPanelHolder.optLayout.setVisibility(View.VISIBLE);
        if(mOptPanelHolder.optInAnim == null)
        {
            mOptPanelHolder.optInAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f,Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,0);
            mOptPanelHolder.optInAnim.setDuration(menuViews.menuOutInterval);
            mOptPanelHolder.optInAnim.setFillAfter(true);

        }
        mOptPanelHolder.optTab.check(R.id.tab_brand_rb);
        mOptPanelHolder.optContent.startAnimation(mOptPanelHolder.optInAnim);
        mOptPanelHolder.optContent.setVisibility(View.VISIBLE);
    }

    /**
     * end of optPanel ------------------------------------------------------------------------
     */

    private void initParams() {

        mSearchParams = new BasicParamModel();

//        IPageCache cache = new IPageCache();
//        String content = cache.get(BasicParamModel.CACHE_KEY);
//        if(!TextUtils.isEmpty(content) && !cache.isExpire(BasicParamModel.CACHE_KEY))
//        {
//            try {
//                JSONObject json = new JSONObject(content);
//                mSearchParams.parse(json);
//                renderParamPanel();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        else
        {
            mAjax = ServiceConfig.getAjax(braConfig.URL_BASIC_PARAMS);
            if (null == mAjax)
                return;

            mAjax.setOnSuccessListener(this);
            mAjax.send();
        }
    }


    private void renderParamPanel() {
        if(null == mOptPanelHolder)
            initOptPanel();
        mOptPanelHolder.brandAdapter.setList(mSearchParams.brandModel.dtStrArray,-1);
        mOptPanelHolder.brandAdapter.notifyDataSetChanged();
        mOptPanelHolder.tagAdapter.setList(mSearchParams.funcModel.dtStrArray,-1);
        mOptPanelHolder.tagAdapter.notifyDataSetChanged();

        mOptPanelHolder.homeTagAdapter.mPickIdx = 0;
        mOptPanelHolder.homeTagAdapter.notifyDataSetChanged();


        mOptPanelHolder.priceSeekbar.setStep(200);
        mOptPanelHolder.priceSeekbar.setRangeValues(0, 200*(mSearchParams.pricerangeModel.dtArray.size()-1));

        mOptPanelHolder.priceSeekbar.setLabels(mSearchParams.pricerangeModel.dtStrArray);
        mOptPanelHolder.priceSeekbar.invalidate();
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        mSearchParams.parse(jsonObject);

        IPageCache cache = new IPageCache();
        cache.set(BasicParamModel.CACHE_KEY, jsonObject.toString(), 86400);

        renderParamPanel();
        mHomePg.renderParamPanel();

        return;
    }




    private void clearFilter() {
        mOptPanelHolder.brandAdapter.setPickIdx(-1);
        mOptPanelHolder.tagAdapter.setPickIdx(-1);
        mOptPanelHolder.brandAdapter.notifyDataSetChanged();
        mOptPanelHolder.tagAdapter.notifyDataSetChanged();
        mOptPanelHolder.priceSeekbar.resetSelectedValues();
        mSearchParams.filterBrandIdx = -1;
        mSearchParams.filterFuncIdx = -1;
        mSearchParams.filterLowPriceIdx = -1;
        mSearchParams.filterHighPriceIdx = -1;
        mOptPanelHolder.priceActive = false;

    }

    /**
     *  home tag gridadapter
     */
    public class HomeTagAdapter extends BaseAdapter
    {

        public int mPickIdx = -1;
        @Override
        public int getCount() {
            return (mSearchParams==null || mSearchParams.optiontypeModel.dtArray==null) ? 0 : mSearchParams.optiontypeModel.dtArray.size();
       }

        @Override
        public Object getItem(int position) {
            return (mSearchParams==null || mSearchParams.optiontypeModel.dtArray==null) ? null : mSearchParams.optiontypeModel.dtArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;
            if (null == convertView)
            {
                convertView = View.inflate(MainActivity.this, R.layout.home_tag_item, null);
                holder = new ItemHolder();
                holder.mName = (TextView) convertView.findViewById(com.xingy.R.id.radio_item_name);
                convertView.setTag(holder);
            }
            else
                holder = (ItemHolder) convertView.getTag();

            // set data
            ParamDtModel dtitem = mSearchParams.optiontypeModel.dtArray.get(position);
            holder.mName.setText(dtitem.info);
            holder.mName.setEnabled(mPickIdx == position);
            return convertView;
        }
    }
    private class ItemHolder
    {
        TextView  mName;
    }



    public class OptPanelPagerAdapter extends PagerAdapter {


        public List<View> viewList;
        public OptPanelPagerAdapter(List<View> dataset)
        {
            viewList = dataset;
        }

        @Override
        public int getCount() {
            return (null == viewList ? 0 : viewList.size());
        }

        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return (view == arg1);
        }

        @Override
        public Object instantiateItem(View view, int position) {
            View pagerView = viewList.get(position);
            ((ViewPager) view).addView(pagerView);

            return pagerView;
        }

        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ((ViewPager) view).removeView((View) arg2);
            System.gc();

        }
    }
}
	
