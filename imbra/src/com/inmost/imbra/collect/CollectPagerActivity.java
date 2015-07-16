package com.inmost.imbra.collect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.brand.BrandInfoActivity;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollectPagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        OnSuccessListener<JSONObject> {


    public static final String COLLECT_ID = "collect_id";

    private String  mId;
    private ImageLoader mImgLoader;
    private NetworkImageView bgImgView;

    private ArrayList<CollectCardModel> Cards;
    private CollectTitlePgModel coverModel;
    private Ajax mAjax;
    // 定义ViewPager对象
    private ViewPager viewPager;

    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;


    // 记录当前选中位置
    private int currentIndex;



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        setContentView(R.layout.activity_glance_vp);

        coverModel = new CollectTitlePgModel();
        Cards = new ArrayList<CollectCardModel>();

        Intent intent = getIntent();
        if(intent == null)
        {
            finish();
            return;
        }
        mId = intent.getStringExtra(COLLECT_ID);

        requestData();
        initPager();

	}

    private void requestData() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_COLLECT);
        if (null == mAjax)
            return;
        String url = mAjax.getUrl() + mId;
        mAjax.setUrl(url);

        showLoadingLayer();

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    @Override
	protected void onDestroy() {
		vpAdapter = null;
		if (null != viewPager) {
			viewPager.setAdapter(null);
			viewPager.removeAllViews();
		}
		viewPager = null;
        super.onDestroy();
	}


	private void initPager() {
        loadNavBar(R.id.pg_nav);
        mNavBar.setVisibility(View.VISIBLE);

        bgImgView = (NetworkImageView) this.findViewById(R.id.pg_bg_imv);


        // 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setVisibility(View.GONE);

        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPageMargin(-DPIUtil.dip2px(40));

		// 设置监听
		viewPager.setOnPageChangeListener(this);

		// 设置当面默认的位置
		currentIndex = 0;
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
        currentIndex = position;
		currentPosition = position;
	}

    @Override
    public void onSuccess(JSONObject json, Response response) {
        closeLoadingLayer();


        coverModel.parse(json.optJSONObject("collection"));

        bgImgView.setImageUrl(HomeFloorModel.formBraUrl(coverModel.bgUrl),mImgLoader);


        JSONArray cardary = json.optJSONArray("cards");
        for(int i = 0 ; i < cardary.length(); i++)
        {
            CollectCardModel item = new CollectCardModel();
            item.parse(cardary.optJSONObject(i));
            Cards.add(item);
        }

        viewPager.setVisibility(View.VISIBLE);
        // 设置数据
        vpAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(vpAdapter);


    }


    public class ViewPagerAdapter extends PagerAdapter {

		// 界面列表
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		/**
		 * 获得当前界面数
		 */
		@Override
		public int getCount() {
			if (Cards != null && Cards.size()>0) {
				return Cards.size()+1;
            }
			return 0;
		}

//        @Override
//        public float getPageWidth(int position)
//        {
//            return (float)0.8;
//        }
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
            View page = null;
            if(position == 0) {
                page = LayoutInflater.from(getBaseContext()).inflate(R.layout.collect_title_pg, null);
                TextView titlev = (TextView)page.findViewById(R.id.title);
                TextView authorv = (TextView)page.findViewById(R.id.author);
                TextView introv = (TextView)page.findViewById(R.id.intro);
                TextView viewctv = (TextView)page.findViewById(R.id.view_count_tv);
                titlev.setText(coverModel.title);
                authorv.setText(coverModel.editor.name);
                introv.setText(coverModel.intro);
                viewctv.setText(coverModel.view_count + "次浏览");
            }
            else
            {
                final CollectCardModel item = Cards.get(position-1);
                if(item.type.equalsIgnoreCase(CollectCardModel.TYPE_BRAND))
                {
                    page = LayoutInflater.from(getBaseContext()).inflate(R.layout.collect_brand_pg, null);
                    NetworkImageView proImgV = (NetworkImageView)page.findViewById(R.id.brand_img);

                    proImgV.setImageUrl(HomeFloorModel.formBraUrl(item.imgUrl),mImgLoader);
                    TextView brandStatusTv = (TextView)page.findViewById(R.id.brand_status);

                    brandStatusTv.setText(R.string.click_go);

                    page.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString(BrandInfoActivity.BRAND_ID,item.brand_id);
                            UiUtils.startActivity(CollectPagerActivity.this,BrandInfoActivity.class,bundle,true);
                        }
                    });
                }
			    else{
                    page = LayoutInflater.from(getBaseContext()).inflate(R.layout.collect_card_pg, null);
                    CheckBox favBtn = (CheckBox) page.findViewById(R.id.fav_btn);
                    AutoHeightImageView proImgV = (AutoHeightImageView) page.findViewById(R.id.pro_img);
                    proImgV.setImageUrl(HomeFloorModel.formBraUrl(item.imgUrl), mImgLoader);
                    TextView proIntroTv = (TextView) page.findViewById(R.id.pro_intro);
                    TextView proPriceTv = (TextView) page.findViewById(R.id.pro_price);
                    TextView proStatusTv = (TextView) page.findViewById(R.id.pro_status);

                    proIntroTv.setText(item.description);
                    proPriceTv.setText(item.sale_price);
                    proStatusTv.setText(item.status);
                    page.setTag(item.type_id);
                    page.setOnClickListener(itemListener);
                }


            }
            v = page;
            ((ViewPager) view).addView(page);

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

    public class CardViewHolder
    {
        NetworkImageView proImgv;
        CheckBox         favBtn;

    }

//	private float downX;
//	private float upX;
//	private final float dinstance = 30;
//
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if (currentPosition == Cards.size() - 1) {
//			switch (ev.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				downX = ev.getX();
//				break;
//			case MotionEvent.ACTION_UP:
//				upX = ev.getX();
//
//				if (downX - upX > dinstance) {
//
//					// defaultPreference.edit().putBoolean(name, true).commit();
//                    finish();
////					vpAdapter.cleanData();
////					vpAdapter.notifyDataSetChanged();  todo
//					System.gc();
//					return true;
//				}
//
//				break;
//			}
//
//		}
//		return super.dispatchTouchEvent(ev);
//	}


    private View.OnClickListener itemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String proid = (String)v.getTag();
            Bundle bundle = new Bundle();
            bundle.putString(ProductDetailActivity.PRO_ID,proid);
            UiUtils.startActivity(CollectPagerActivity.this,ProductDetailActivity.class,bundle,true);
        }
    };


    @Override
    public void onNewIntent(Intent intent)
    {
        mId = intent.getStringExtra(COLLECT_ID);
        Cards.clear();
        vpAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        bgImgView.setImageDrawable(null);
        requestData();

        super.onNewIntent(intent);
    }
	
}
