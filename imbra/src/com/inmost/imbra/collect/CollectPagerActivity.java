package com.inmost.imbra.collect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.brand.BrandInfoActivity;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.VerifyLoginActivity;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductModel;
import com.inmost.imbra.util.ShareUtil;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.share.ShareInfo;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollectPagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        OnSuccessListener<JSONObject> {

    public static final int  AJX_ADD_FAV = 1123;
    public static final int  AJX_REMOVE_FAV = 1124;

    public static final String COLLECT_ID = "collect_id";
    public static final int    CODE_GO_DETAIL = 1203;
    private String  mId;
    private ImageLoader mImgLoader;
    private NetworkImageView bgImgView;

    private ArrayList<CollectCardModel> Cards;
    private CollectTitlePgModel coverModel;
    private Ajax mAjax;
    // 定义ViewPager对象
    private ViewPager viewPager;
    private int lastIdx = 0;
    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    private Account   account;

    private ShareInfo   shareinfo;


    // 记录当前选中位置
    private int currentIndex;
    private Drawable favOnDb;
    private Drawable favOffDb;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        setContentView(R.layout.activity_glance_vp);
        account = ILogin.getActiveAccount();
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
        favOnDb = getResources().getDrawable(R.drawable.fav_on);
        favOffDb = getResources().getDrawable(R.drawable.fav_off);
        favOnDb.setBounds(0, 0, favOnDb.getMinimumWidth(), favOnDb.getMinimumHeight());
        favOffDb.setBounds(0, 0, favOffDb.getMinimumWidth(), favOffDb.getMinimumHeight());

        initPager();

	}

    private void requestData() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_COLLECT);
        if (null == mAjax)
            return;

        mAjax.setData("pn", 1);
        mAjax.setData("ps", 10);
        mAjax.setData("dp", ToolUtil.getAppWidth() + "*" + ToolUtil.getAppHeight());

        mAjax.setData("id",mId);
        if(null!=account)
            mAjax.setData("uid",account.uid);
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
        mNavBar.setRightInfo(R.string.share_loading,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=shareinfo)
                    ShareUtil.shareInfoOut(CollectPagerActivity.this,shareinfo,mImgLoader);
            }
        });
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
        final int ret = json.optInt("error");
        if(ret != 0 )
        {
            String msg =  json.optString("data");
            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg): msg);
            return;
        }

        if(response.getId() == AJX_ADD_FAV)
        {
            CollectCardModel curItem = Cards.get(currentIndex-1);
            curItem.fav_cnt++;
            curItem.fav = ProductModel.FAV_OK;
            viewPager.removeAllViews();
            vpAdapter = new ViewPagerAdapter();
            viewPager.setAdapter(vpAdapter);

            viewPager.setCurrentItem(currentIndex);

            return;
        }
        else if(response.getId() == AJX_REMOVE_FAV)
        {
            CollectCardModel curItem = Cards.get(currentIndex-1);
            curItem.fav_cnt--;
            curItem.fav = ProductModel.FAV_NO;

            viewPager.removeAllViews();
            vpAdapter = new ViewPagerAdapter();
            viewPager.setAdapter(vpAdapter);

            viewPager.setCurrentItem(currentIndex);

            return;
        }

        JSONArray ar = json.optJSONArray("dt");
        if(null==ar)
            return;
        JSONObject dt = ar.optJSONObject(0);
        coverModel.parse(dt);

        shareinfo = new ShareInfo();
        shareinfo.title = coverModel.title;
        shareinfo.iconUrl = coverModel.bgUrl;
        shareinfo.wxMomentsContent = coverModel.intro;
        shareinfo.wxcontent = coverModel.intro;
        shareinfo.url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.nuomi";

        bgImgView.setImageUrl(HomeFloorModel.formBraUrl(coverModel.bgUrl),mImgLoader);


        JSONArray cardary = dt.optJSONArray("pl");
        for(int i = 0 ; i < cardary.length(); i++)
        {
            CollectCardModel item = new CollectCardModel();
            item.parseCollect(cardary.optJSONObject(i));
            Cards.add(item);
        }

        JSONObject bd = dt.optJSONObject("bd");
        if(bd!=null)
        {
            CollectCardModel item = new CollectCardModel();
            item.parseBrand(bd);
            Cards.add(item);
        }


        viewPager.setVisibility(View.VISIBLE);
        // 设置数据
        vpAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(vpAdapter);

        viewPager.setCurrentItem(lastIdx);


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
                final CollectCardModel curItem = Cards.get(position-1);
                if(curItem.type==CollectCardModel.TYPE_BRAND)
                {
                    page = LayoutInflater.from(getBaseContext()).inflate(R.layout.collect_brand_pg, null);
                    NetworkImageView proImgV = (NetworkImageView)page.findViewById(R.id.brand_img);

                    proImgV.setImageUrl(HomeFloorModel.formBraUrl(curItem.imgUrl),mImgLoader);
                    TextView brandStatusTv = (TextView)page.findViewById(R.id.brand_status);

                    brandStatusTv.setText(R.string.click_go);

                    page.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString(BrandInfoActivity.BRAND_ID,curItem.brand_id);
                            UiUtils.startActivity(CollectPagerActivity.this,BrandInfoActivity.class,bundle,true);
                        }
                    });
                }
			    else{
                    page = LayoutInflater.from(getBaseContext()).inflate(R.layout.collect_card_pg, null);

                    AutoHeightImageView proImgV = (AutoHeightImageView) page.findViewById(R.id.pro_img);
                    proImgV.setImageUrl(HomeFloorModel.formBraUrl(curItem.imgUrl), mImgLoader);
                    TextView proIntroTv = (TextView) page.findViewById(R.id.pro_intro);
                    TextView proPriceTv = (TextView) page.findViewById(R.id.pro_price);
                    TextView proStatusTv = (TextView) page.findViewById(R.id.pro_status);
                    TextView favCnTv = (TextView)page.findViewById(R.id.fav_cnt);
                    favCnTv.setOnClickListener(favCheckListener);
                    favCnTv.setTag(curItem.id);

                    if(curItem.fav==ProductModel.FAV_OK)
                    {
                        favCnTv.setCompoundDrawables(null,favOnDb,null,null);
                    }
                    else {
                        favCnTv.setCompoundDrawables(null, favOffDb, null, null);
                    }
                    proIntroTv.setText(curItem.title);
                    proPriceTv.setText(curItem.sale_price);
                    proStatusTv.setText(curItem.status);
                    favCnTv.setText(""+curItem.fav_cnt);
                    page.setTag(curItem.id);
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
            Intent ait = new Intent(CollectPagerActivity.this,ProductDetailActivity.class);
            ait.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ait.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ait.putExtra(ProductDetailActivity.PRO_ID,proid);
            CollectPagerActivity.this.startActivityForResult(ait,CODE_GO_DETAIL);
        }
    };


    @Override
    public void onNewIntent(Intent intent)
    {
        mId = intent.getStringExtra(COLLECT_ID);
        Cards.clear();
        vpAdapter.notifyDataSetChanged();
        viewPager.removeAllViews();
        lastIdx = 0;
        bgImgView.setImageDrawable(null);
        requestData();

        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
    {
        if(requestCode == CODE_GO_DETAIL && resultCode == RESULT_OK)
        {
            lastIdx = viewPager.getCurrentItem();
            Cards.clear();
            viewPager.removeAllViews();
            vpAdapter.notifyDataSetChanged();
            bgImgView.setImageDrawable(null);
            requestData();
        }
    }


    /**
     * fav modify need login
     */
    private void removeFav(final String proid) {
        if(account == null)
        {
            UiUtils.startActivity(this,VerifyLoginActivity.class,true);
            return;
        }
        mAjax = ServiceConfig.getAjax(braConfig.URL_MODIFY_FAV);
        if (null == mAjax)
            return;

        showLoadingLayer();
        mAjax.setId(AJX_REMOVE_FAV);
        mAjax.setData("cur_id", proid);
        mAjax.setData("token",account.token);
        mAjax.setData("act",2); //2 for del;

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void addFav(final String proid) {
        if(account == null)
        {
            UiUtils.startActivity(this,VerifyLoginActivity.class,true);
            return;
        }

        mAjax = ServiceConfig.getAjax(braConfig.URL_MODIFY_FAV);
        if (null == mAjax)
            return;

        showLoadingLayer();
        mAjax.setId(AJX_ADD_FAV);
        mAjax.setData("cur_id", proid);
        mAjax.setData("token",account.token);
        mAjax.setData("act",1); //1 for add;

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    private View.OnClickListener favCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if(null!=obj && obj instanceof String)
            {
                CollectCardModel curItem = Cards.get(currentIndex-1);
                if(curItem.fav==ProductModel.FAV_NO)
                {
                    ((TextView)v).setCompoundDrawables(null,favOnDb,null,null);
                    addFav((String)obj);
                }
                else {
                    ((TextView) v).setCompoundDrawables(null, favOffDb, null, null);
                    removeFav((String) obj);
                }
            }
        }
    };

}
