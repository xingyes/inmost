package com.inmost.imbra.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.inmost.imbra.R;
import com.inmost.imbra.basic.BasicParamModel;
import com.inmost.imbra.basic.ParamDtModel;
import com.inmost.imbra.blog.BlogVolleyActivity;
import com.inmost.imbra.collect.CollectPagerActivity;
import com.inmost.imbra.product.Product2RowAdapter;
import com.inmost.imbra.product.ProductModel;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnSuccessListener<JSONObject>,
        AdapterView.OnItemClickListener,OnItemLongClickListener
{
	
	private MainActivity    mActivity;
	private View  mRootView;
	
	private PullToRefreshListView pullList;
    private ListView              mListV;
	private Ajax mAjax;




    /**
     * tab 1
     */
	private ArrayList<HomeFloorModel> mHomeFloors;
    private HomeFloorAdapter mFloorAdapter;
    private int  mFloorNextPageNum;


    /**
     * tab 2
     */
    private ArrayList<ProductModel> mProArray;
    private Product2RowAdapter mProAdapter;
    private int  mProNextPageNum;


    private LayoutInflater    mInflater;
    private BasicParamModel mSearchParams;



    private class ParamViewHolder
    {
        public View paramLayout;
        public RadioGroup sizeGroup;
        public int        sizeIdx = -1;
        public RadioGroup cupGroup;
        public int        cupIdx = -1;

        public View      extraLayout;
        private View    brandLayout;
        public TextView brandTv;
        private View    funcLLayout;
        public TextView  funcTv;
        private View     priceLayout;
        public TextView  priceTv;
        public View.OnTouchListener extraParamTouchListener;
    };
    private ParamViewHolder  paramHolder;

    //end of tab 2

    private RadioGroup        mTabRg;
    private int               mTabIdx = TAB_HOME;
    public static int        TAB_HOME = 0;
    public static int        TAB_PRO = 1;

    public static final int  AJAX_HOMEFLOOR = TAB_HOME;
    public static final int  AJAX_SEARCH = TAB_PRO;


    private ImageLoader mImgLoader;
    private Handler mHandler = new Handler();

    private SearchCheckListener   mSearchListener;
    public interface SearchCheckListener{
        public void onTabChecked(int tabidx);
        public void onBrandParamCancel();
        public void onFuncParamCancel();
        public void onPriceParamCancel();
    }
    public void setTabListener(SearchCheckListener listener)
    {
        mSearchListener = listener;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
		
		mRootView = inflater.inflate(R.layout.pg_home_floor, container, false);


        initView();
        initParams();

        mFloorNextPageNum = 1;
        mProNextPageNum = 1;

        mHomeFloors = new ArrayList<HomeFloorModel>();

     	return mRootView;
    }

	@Override
	public void onAttach(Activity activity)
	{
		mActivity = (MainActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	private void requestPage(int page) {
        mAjax = ServiceConfig.getAjax(mTabIdx == TAB_HOME ? braConfig.URL_HOME_FLOOR : braConfig.URL_SEARCH);
		if (null == mAjax) {
            mActivity.closeLoadingLayer();
            return;
        }

        mAjax.setData("page", page);
		mAjax.setId(mTabIdx);
		mAjax.setOnSuccessListener(this);
		mAjax.setOnErrorListener(mActivity);
		mAjax.send();
	}

	@Override
	public void onDestroy()
	{
		if(mAjax!=null)
		{
			mAjax.abort();
			mAjax = null;
		}
        super.onDestroy();
	}
	
	
	private void initView() {
        mTabRg = (RadioGroup) mRootView.findViewById(R.id.home_tab_rg);
        mTabRg.setVisibility(View.VISIBLE);
        mTabRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.pro_list_rb)
                {
                    mTabIdx = TAB_PRO;
                    if(mProAdapter == null) {
                        mProAdapter = new Product2RowAdapter(mActivity, mImgLoader);
                        mProArray = new ArrayList<ProductModel>();
                    }
                    mListV.addHeaderView(paramHolder.paramLayout);
                    pullList.setAdapter(mProAdapter);
                    if(mProAdapter.getCount()<=0) {
                        mActivity.showLoadingLayer();
                        requestPage(mProNextPageNum);
                    }
                    if(null!=mSearchListener)
                        mSearchListener.onTabChecked(TAB_PRO);
                }
                else
                {
                    mTabIdx = TAB_HOME;
                    if(mFloorAdapter == null)
                        mFloorAdapter = new HomeFloorAdapter(mActivity,mImgLoader);

                    mListV.removeHeaderView(paramHolder.paramLayout);
                    pullList.setAdapter(mFloorAdapter);
                    if(mFloorAdapter.getCount()<=0) {
                        mActivity.showLoadingLayer();
                        requestPage(mFloorNextPageNum);
                    }
                    if(null!=mSearchListener)
                        mSearchListener.onTabChecked(TAB_HOME);

                }


            }
        });
        pullList = (PullToRefreshListView) mRootView.findViewById(R.id.pull_refresh_list);
		pullList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(mTabIdx == TAB_HOME) {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && mFloorNextPageNum>1) {
                        UiUtils.makeToast(mActivity, "first:" + firstVisibleItem + ",vis:" +
                            visibleItemCount + ",totalItemCount" + totalItemCount);
                    requestPage(mFloorNextPageNum);
                    }
                }
                else {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && mProNextPageNum > 1) {
                        UiUtils.makeToast(mActivity, "first:" + firstVisibleItem + ",vis:" +
                                visibleItemCount + ",totalItemCount" + totalItemCount);
                        requestPage(mProNextPageNum);
                    }
                }

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });

        pullList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>(){

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if(mTabIdx == TAB_HOME) {
                    mHomeFloors.clear();
                    mFloorNextPageNum = 1;
                    requestPage(mFloorNextPageNum);
                }
                else {
                    mProArray.clear();
                    mProNextPageNum = 1;
                    requestPage(mProNextPageNum);
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullList.onRefreshComplete();
                    }},5000);
            }
        });

        /**
         * headview
         */
        paramHolder = new ParamViewHolder();
        mInflater = mActivity.getLayoutInflater();

        paramHolder.paramLayout = mInflater.inflate(R.layout.search_param_panel,null);
        paramHolder.sizeGroup = (RadioGroup) paramHolder.paramLayout.findViewById(R.id.size_param_group);
        paramHolder.sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (paramHolder.sizeIdx == checkedId)
                    return;

                RadioButton curRB = (RadioButton) group.findViewById(checkedId);
                if (paramHolder.sizeIdx >= 0) {
                    RadioButton oldRB = (RadioButton) group.findViewById(paramHolder.sizeIdx);
                    oldRB.setChecked(false);
                }

                curRB.setChecked(true);
                paramHolder.sizeIdx = checkedId;
            }
        });
        paramHolder.cupGroup = (RadioGroup) paramHolder.paramLayout.findViewById(R.id.cup_param_group);
        paramHolder.cupGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (paramHolder.cupIdx == checkedId)
                    return;

                RadioButton curRB = (RadioButton) group.findViewById(checkedId);
                if (paramHolder.cupIdx >= 0) {
                    RadioButton oldRB = (RadioButton) group.findViewById(paramHolder.cupIdx);
                    oldRB.setChecked(false);
                }

                curRB.setChecked(true);
                paramHolder.cupIdx = checkedId;
            }
        });
        paramHolder.extraLayout = paramHolder.paramLayout.findViewById(R.id.extra_param_layout);
        paramHolder.extraLayout.setVisibility(View.GONE);
        paramHolder.brandTv = (TextView)paramHolder.paramLayout.findViewById(R.id.brand_param_tv);
        paramHolder.funcTv = (TextView)paramHolder.paramLayout.findViewById(R.id.func_param_tv);
        paramHolder.priceTv = (TextView)paramHolder.paramLayout.findViewById(R.id.price_param_tv);

        paramHolder.brandLayout = paramHolder.paramLayout.findViewById(R.id.brand_param_layout);
        paramHolder.funcLLayout = paramHolder.paramLayout.findViewById(R.id.func_param_layout);
        paramHolder.priceLayout = paramHolder.paramLayout.findViewById(R.id.price_param_layout);

        paramHolder.brandLayout.setVisibility(View.GONE);
        paramHolder.funcLLayout.setVisibility(View.GONE);
        paramHolder.priceLayout.setVisibility(View.GONE);
        paramHolder.extraParamTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setVisibility(View.GONE);
                    if (paramHolder.priceLayout.getVisibility() == View.GONE &&
                            paramHolder.brandLayout.getVisibility() == View.GONE &&
                            paramHolder.funcLLayout.getVisibility() == View.GONE)
                        paramHolder.extraLayout.setVisibility(View.GONE);
                    else
                        paramHolder.extraLayout.setVisibility(View.VISIBLE);


                    if (null != mSearchListener) {
                        if (v == paramHolder.brandLayout) {
                            mSearchParams.filterBrandIdx = -1;
                            mSearchListener.onBrandParamCancel();
                        }
                        else if (v == paramHolder.funcLLayout) {
                            mSearchParams.filterFuncIdx = -1;
                            mSearchListener.onFuncParamCancel();
                        }
                        else if (v == paramHolder.priceLayout) {
                            mSearchParams.filterHighPriceIdx = -1;
                            mSearchParams.filterLowPriceIdx = -1;
                            mSearchListener.onPriceParamCancel();
                        }
                    }
                }
                return false;
            }
        };

        paramHolder.brandLayout.setOnTouchListener(paramHolder.extraParamTouchListener);
        paramHolder.funcLLayout.setOnTouchListener(paramHolder.extraParamTouchListener);
        paramHolder.priceLayout.setOnTouchListener(paramHolder.extraParamTouchListener);
        // end of headview---------

        mListV = pullList.getRefreshableView();
        mListV.addHeaderView(paramHolder.paramLayout);

        pullList.setOnItemClickListener(this);
        mFloorAdapter = new HomeFloorAdapter(mActivity,mImgLoader);
		pullList.setAdapter(mFloorAdapter);

        mListV.removeHeaderView(paramHolder.paramLayout);

        mTabRg.check(R.id.home_floor_rb);

    }



	@Override
	public void onSuccess(JSONObject v, Response response) {

        pullList.onRefreshComplete();
		mActivity.closeLoadingLayer();
        if(response.getId() == AJAX_HOMEFLOOR) {
            mFloorNextPageNum++;
            JSONArray feeds = v.optJSONArray("feeds");
            if (null != feeds) {
                for (int i = 0; i < feeds.length(); i++) {
                    HomeFloorModel model = new HomeFloorModel();
                    model.parse(feeds.optJSONObject(i));
                    mHomeFloors.add(model);
                }
            }

            mFloorAdapter.setData(mHomeFloors);
            mFloorAdapter.notifyDataSetChanged();
        }
        else if(response.getId()==AJAX_SEARCH) {
            UiUtils.makeToast(mActivity, "searched " + mProNextPageNum);

            mProNextPageNum++;
            JSONArray feeds = v.optJSONArray("products");
            if (null != feeds) {
                for (int i = 0; i < feeds.length(); i++) {
                    ProductModel pro = new ProductModel();
                    pro.parse(feeds.optJSONObject(i));
                    mProArray.add(pro);
                }
            }
            mProAdapter.setData(mProArray);
            mProAdapter.notifyDataSetChanged();
        }

//		final int ret = v.optInt("error");
//		if(ret != 0 )
//		{
//			String msg =  v.optString("data");
//			UiUtils.makeToast(mActivity, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg): msg);
//			return;
//		}
//
//		JSONObject data = v.optJSONObject("data");
//		if(null == data)
//		{
//			UiUtils.makeToast(mActivity, getString(R.string.parser_error_msg));
//			return;
//		}


    }

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
	
		return true;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        HomeFloorModel item = mHomeFloors.get(position-1);
        if(item.type.equalsIgnoreCase(HomeFloorModel.TYPE_LOOKBOOK))
            UiUtils.makeToast(mActivity,"look");
        else if(item.type.equalsIgnoreCase(HomeFloorModel.TYPE_COLLECTION))
        {
            Bundle bund = new Bundle();
            bund.putString(CollectPagerActivity.COLLECT_ID,item.type_id);
            UiUtils.startActivity(mActivity, CollectPagerActivity.class, bund, true);
        }
        else if(item.type.equalsIgnoreCase(HomeFloorModel.TYPE_BLOG))
        {
            Bundle bund = new Bundle();
            bund.putString(BlogVolleyActivity.BLOG_ID,item.type_id);
            UiUtils.startActivity(mActivity, BlogVolleyActivity.class, bund, true);
        }
    }

    public void setImgLoader(ImageLoader imgLoader) {
        mImgLoader = imgLoader;
    }


    public void renderParamPanel() {
        RadioGroup.LayoutParams rl;
        ArrayList<ParamDtModel> numArray;
        numArray = mSearchParams.sizeModel.dtArray;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.choose_btn_focus);
        int w = bm.getWidth();
        if(numArray != null && numArray.size()>0) {
            paramHolder.sizeGroup.removeAllViews();

            int margin = (DPIUtil.getWidth() - numArray.size()*w)/(numArray.size()+1)/2;
            if(margin<0)
            {
                margin = 10;
                w = (DPIUtil.getWidth() - (numArray.size()+1)*margin)/numArray.size();
            }
            rl = new RadioGroup.LayoutParams(w, w);

            rl.leftMargin = margin;
            rl.rightMargin = margin;

            for (int i = 0; null != numArray && i < numArray.size(); i++) {
                RadioButton rb = new RadioButton(mActivity);
                rb.setBackgroundResource(R.drawable.param_btn_choose_state);
                rb.setTextColor(mActivity.getResources().getColorStateList(R.color.txt_pink_white_selector));
                rb.setTextSize(DPIUtil.px2sp(mActivity,w/2));

                rb.setButtonDrawable(R.drawable.none);
                rb.setSingleLine(true);
                rb.setGravity(Gravity.CENTER);
                rb.setText(numArray.get(i).info);

                paramHolder.sizeGroup.addView(rb, rl);
            }
        }


        ArrayList<ParamDtModel> paramArray;
        paramArray = mSearchParams.cupModel.dtArray;
        w = bm.getWidth();
        bm.recycle();
        bm = null;
        if(paramArray != null && paramArray.size()>0) {
            int margin = (DPIUtil.getWidth() - paramArray.size() * w) / (paramArray.size() + 1) / 2;
            if (margin < 0) {
                margin = 10;
                w = (DPIUtil.getWidth() - (paramArray.size() + 1) * margin) / paramArray.size();
            }
            rl = new RadioGroup.LayoutParams(w, w);

            rl.leftMargin = margin;
            rl.rightMargin = margin;
            paramHolder.cupGroup.removeAllViews();
            for (int i = 0; null != paramArray && i < paramArray.size(); i++) {
                RadioButton rb = new RadioButton(mActivity);
                rb.setText(paramArray.get(i).info);
                rb.setTextSize(DPIUtil.px2sp(mActivity,w/2));
                rb.setBackgroundResource(R.drawable.param_btn_choose_state);
                rb.setTextColor(mActivity.getResources().getColorStateList(R.color.txt_pink_white_selector));
                rb.setButtonDrawable(R.drawable.none);
                rb.setSingleLine(true);
                rb.setGravity(Gravity.CENTER);

                paramHolder.cupGroup.addView(rb, rl);
            }
        }
//        mOptPanelHolder.brandAdapter.setList(mSearchParams.brandStringArray,-1);
//        mOptPanelHolder.brandAdapter.notifyDataSetChanged();
//        mOptPanelHolder.tagAdapter.setList(mSearchParams.tagStringArray,-1);
//        mOptPanelHolder.tagAdapter.notifyDataSetChanged();

    }


    private void initParams() {

        mSearchParams = mActivity.mSearchParams;
        renderParamPanel();

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
//        {
//            mAjax = ServiceConfig.getAjax(braConfig.URL_BASIC_PARAMS);
//            if (null == mAjax)
//                return;
//
//            mAjax.setId(AJAX_PARAM);
//            mAjax.setOnSuccessListener(this);
//            mAjax.send();
//        }

    }



    public void onFilterSubmit() {
        if (mSearchParams.filterFuncIdx < 0)
            paramHolder.funcLLayout.setVisibility(View.GONE);
        else {
            paramHolder.funcTv.setText(mSearchParams.funcModel.dtStrArray.get(mSearchParams.filterFuncIdx));
            paramHolder.funcLLayout.setVisibility(View.VISIBLE);
        }

        if (mSearchParams.filterBrandIdx < 0)
            paramHolder.brandLayout.setVisibility(View.GONE);
        else {
            paramHolder.brandTv.setText(mSearchParams.brandModel.dtStrArray.get(mSearchParams.filterBrandIdx));
            paramHolder.brandLayout.setVisibility(View.VISIBLE);
        }


        if (mSearchParams.filterLowPriceIdx >= 0 && mSearchParams.filterHighPriceIdx >= 0) {
            paramHolder.priceLayout.setVisibility(View.VISIBLE);
            paramHolder.priceTv.setText("" + mSearchParams.pricerangeModel.dtStrArray.get(mSearchParams.filterLowPriceIdx) + "~"
                    + mSearchParams.pricerangeModel.dtStrArray.get(mSearchParams.filterHighPriceIdx));
        } else
            paramHolder.priceLayout.setVisibility(View.GONE);

        if (mTabIdx != TAB_PRO)
            return;

        if (paramHolder.priceLayout.getVisibility() == View.GONE &&
                paramHolder.brandLayout.getVisibility() == View.GONE &&
                paramHolder.funcLLayout.getVisibility() == View.GONE)
            paramHolder.extraLayout.setVisibility(View.GONE);
        else
            paramHolder.extraLayout.setVisibility(View.VISIBLE);

        mProNextPageNum = 1;
        requestPage(mFloorNextPageNum);

    }
}
