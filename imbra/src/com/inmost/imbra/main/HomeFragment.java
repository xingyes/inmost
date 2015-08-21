package com.inmost.imbra.main;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.inmost.imbra.R;
import com.inmost.imbra.basic.BasicParamModel;
import com.inmost.imbra.blog.BlogVolleyActivity;
import com.inmost.imbra.collect.CollectPagerActivity;
import com.inmost.imbra.product.Product2RowAdapter;
import com.inmost.imbra.product.ProductModel;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
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
    private boolean bFinished = false;



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
    private boolean  bSearchFinished = false;


    private LayoutInflater    mInflater;
    private BasicParamModel mSearchParams;



    private class ParamViewHolder
    {
        public View paramLayout;
        public GridView sizeGroup;
        public int      filterSizeIdx;
        public SizeCupAdapter  sizeAdapter;

        public GridView cupGroup;
        public int     filterCupIdx;
        public SizeCupAdapter  cupAdapter;

        public View      extraLayout;
        private View    brandLayout;
        public TextView brandTv;
        private View    funcLLayout;
        public TextView  funcTv;
        private View     priceLayout;
        public TextView  priceTv;
        public View.OnTouchListener extraParamTouchListener;

        private TextView noHintView;

    };
    private ParamViewHolder  paramHolder;

    //end of tab 2

    private RadioGroup        mTabRg;
    private int               mTabIdx = -1;
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

        mSearchParams = IMbraApplication.globalBasicParams;

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
        mAjax = ServiceConfig.getAjax(braConfig.URL_SEARCH);
		if (null == mAjax) {
            mActivity.closeLoadingLayer();
            return;
        }

        if(paramHolder.filterSizeIdx>=0 && paramHolder.filterSizeIdx>=0)
        {
            String cs = mSearchParams.sizeModel.dtStrArray.get(paramHolder.filterSizeIdx) +
                    mSearchParams.cupModel.dtStrArray.get(paramHolder.filterCupIdx);
            mAjax.setData("cs",cs);
        }
        if(mSearchParams.filterBrandIdx>=0)
        {
            mAjax.setData("bid",mSearchParams.brandModel.dtArray.get(mSearchParams.filterBrandIdx).id);
        }
        if(mSearchParams.filterFuncIdx>=0)
        {
            mAjax.setData("bf",mSearchParams.funcModel.dtArray.get(mSearchParams.filterFuncIdx).id);
        }
        if(mSearchParams.filterHighPriceIdx>=0 && mSearchParams.filterLowPriceIdx>=0)
        {
            paramHolder.priceTv.setText("" + mSearchParams.pricerangeModel.dtStrArray.get(mSearchParams.filterLowPriceIdx) + "~"
                    + mSearchParams.pricerangeModel.dtStrArray.get(mSearchParams.filterHighPriceIdx));

            mAjax.setData("prs", mSearchParams.pricerangeModel.dtArray.get(mSearchParams.filterLowPriceIdx).id);
            mAjax.setData("pre",mSearchParams.pricerangeModel.dtArray.get(mSearchParams.filterHighPriceIdx).id);
        }

        mAjax.setData("pn", page);
        mAjax.setData("ps", 10);

        mAjax.setId(mTabIdx);
		mAjax.setOnSuccessListener(this);
		mAjax.setOnErrorListener(mActivity);
		mAjax.send();
	}


    private void requestTopic(int page) {
        mAjax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);
        if (null == mAjax) {
            mActivity.closeLoadingLayer();
            return;
        }

        int idx = mSearchParams.filterHomeIdx < 0 ? 0 : mSearchParams.filterHomeIdx;
        mAjax.setData("cat",(mSearchParams.optiontypeModel.dtArray.size() > idx) ?
                mSearchParams.optiontypeModel.dtArray.get(idx).id : 0);
        mAjax.setData("pn", page);

        mAjax.setData("v",ToolUtil.getApkVersionCode("com.inmost.imbra"));


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
                    if(mTabIdx==TAB_PRO)
                        return;
                    mTabIdx = TAB_PRO;
                    if(mProAdapter == null) {
                        mProAdapter = new Product2RowAdapter(mActivity, mImgLoader);
                        mProArray = new ArrayList<ProductModel>();
                    }
                    mListV.addHeaderView(paramHolder.paramLayout);
                    pullList.setAdapter(mProAdapter);
                    if(mProAdapter.getCount()<=0) {
                        researchPro(true);
                    }
                    if(null!=mSearchListener)
                        mSearchListener.onTabChecked(TAB_PRO);
                }
                else
                {
                    if(mTabIdx==TAB_HOME)
                        return;

                    mTabIdx = TAB_HOME;
                    if(mFloorAdapter == null)
                        mFloorAdapter = new HomeFloorAdapter(mActivity,mImgLoader);

                    mListV.removeHeaderView(paramHolder.paramLayout);
                    pullList.setAdapter(mFloorAdapter);
                    if(mFloorAdapter.getCount()<=0) {
                        mActivity.showLoadingLayer();
                        requestTopic(mFloorNextPageNum);
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
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && mFloorNextPageNum>1
                            && !bFinished) {
                        UiUtils.makeToast(mActivity, "first:" + firstVisibleItem + ",vis:" +
                            visibleItemCount + ",totalItemCount" + totalItemCount);
                    requestTopic(mFloorNextPageNum);
                    }
                }
                else {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && mProNextPageNum > 1
                            && !bSearchFinished) {
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
                    requestTopic(mFloorNextPageNum);
                }
                else {
                    researchPro(false);
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
        paramHolder.filterSizeIdx = -1;
        paramHolder.filterCupIdx = -1;
        paramHolder.paramLayout = mInflater.inflate(R.layout.search_param_panel,null);
        paramHolder.noHintView = (TextView)paramHolder.paramLayout.findViewById(R.id.no_hint);
        paramHolder.noHintView.setVisibility(View.GONE);

        paramHolder.sizeGroup = (GridView) paramHolder.paramLayout.findViewById(R.id.size_param_group);
        paramHolder.sizeGroup.setPadding(80,0,80,0);
        paramHolder.sizeAdapter = new SizeCupAdapter();
        paramHolder.sizeGroup.setAdapter(paramHolder.sizeAdapter);
        paramHolder.sizeGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(paramHolder.filterSizeIdx == position)
                {
                    paramHolder.filterSizeIdx = -1;
                }
                else
                    paramHolder.filterSizeIdx = position;

                paramHolder.sizeAdapter.setPick(paramHolder.filterSizeIdx);
                paramHolder.sizeAdapter.notifyDataSetChanged();

                if(paramHolder.filterCupIdx<0) {
                    paramHolder.filterCupIdx = 0;
                    paramHolder.cupAdapter.setPick(paramHolder.filterCupIdx);
                    paramHolder.cupAdapter.notifyDataSetChanged();
                }


                if(paramHolder.filterSizeIdx>=0) {
                    researchPro(true);
                }
            }
        });

        paramHolder.cupGroup = (GridView) paramHolder.paramLayout.findViewById(R.id.cup_param_group);
        paramHolder.cupAdapter = new SizeCupAdapter();
        paramHolder.cupGroup.setPadding(20,0,20,0);

        paramHolder.cupGroup.setAdapter(paramHolder.cupAdapter);
        paramHolder.cupGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(paramHolder.filterCupIdx == position)
                {
                    paramHolder.filterCupIdx = -1;
                }
                else
                    paramHolder.filterCupIdx = position;
                paramHolder.cupAdapter.setPick(paramHolder.filterCupIdx);
                paramHolder.cupAdapter.notifyDataSetChanged();

                if(paramHolder.sizeAdapter.getPickIdx()>=0) {
                    paramHolder.filterSizeIdx = -1;
                    paramHolder.sizeAdapter.setPick(-1);
                    paramHolder.sizeAdapter.notifyDataSetChanged();
                }

                //反选后重搜所有商品
                if(paramHolder.filterCupIdx<0)
                {
                    researchPro(true);
                }
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

                            researchPro(true);
                        }
                        else if (v == paramHolder.funcLLayout) {
                            mSearchParams.filterFuncIdx = -1;
                            mSearchListener.onFuncParamCancel();
                            researchPro(true);
                        }
                        else if (v == paramHolder.priceLayout) {
                            mSearchParams.filterHighPriceIdx = -1;
                            mSearchParams.filterLowPriceIdx = -1;
                            mSearchListener.onPriceParamCancel();
                            researchPro(true);
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

        mActivity.closeLoadingLayer();
        pullList.onRefreshComplete();

//        int err = v.optInt("err");
//        if(err!=0)
//        {
//            String msg =  v.optString("msg");
//            UiUtils.makeToast(mActivity, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg): msg);
//            return;
//        }
		if(response.getId() == AJAX_HOMEFLOOR) {

            JSONArray feeds = v.optJSONArray("dt");
            if (null != feeds && feeds.length()>0) {
                for (int i = 0; i < feeds.length(); i++) {
                    HomeFloorModel model = new HomeFloorModel();
                    model.parse(feeds.optJSONObject(i));
                    mHomeFloors.add(model);
                }
                bFinished = false;
                mFloorNextPageNum++;
            }
            else
                bFinished = true;

            mFloorAdapter.setData(mHomeFloors);
            mFloorAdapter.notifyDataSetChanged();
        }
        else if(response.getId()==AJAX_SEARCH) {

            JSONArray feeds = v.optJSONArray("dt");
            if (null != feeds && feeds.length()>0) {
                for (int i = 0; i < feeds.length(); i++) {
                    ProductModel pro = new ProductModel();
                    pro.parseSearch(feeds.optJSONObject(i));
                    mProArray.add(pro);
                }
                mProNextPageNum++;
                bSearchFinished = false;
            }
            else
                bSearchFinished = true;
            mProAdapter.setData(mProArray);
            mProAdapter.notifyDataSetChanged();

            paramHolder.noHintView.setVisibility(mProArray.size()>0 ? View.GONE:View.VISIBLE);
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
        if(item.type == HomeFloorModel.TYPE_LOOKBOOK)
        {
            Bundle bundle = new Bundle();
            bundle.putString(HTML5Activity.ORI_URL, "http://www.o2bra.com.cn/");
            UiUtils.startActivity(mActivity,HTML5Activity.class,bundle,true);
        }
        else if(item.type == HomeFloorModel.TYPE_COLLECTION)
        {
            Bundle bund = new Bundle();
            bund.putString(CollectPagerActivity.COLLECT_ID,item.id);
            UiUtils.startActivity(mActivity, CollectPagerActivity.class, bund, true);
        }
        else if(item.type == HomeFloorModel.TYPE_BLOG)
        {
            Bundle bund = new Bundle();
            bund.putString(BlogVolleyActivity.BLOG_ID,item.id);
            UiUtils.startActivity(mActivity, BlogVolleyActivity.class, bund, true);
        }
    }

    public void setImgLoader(ImageLoader imgLoader) {
        mImgLoader = imgLoader;
    }


    public void renderParamPanel() {
        paramHolder.sizeAdapter.setData(mSearchParams.sizeModel.dtStrArray, -1);
        paramHolder.sizeGroup.setNumColumns(paramHolder.sizeAdapter.getCount());
        paramHolder.sizeAdapter.notifyDataSetChanged();

        paramHolder.cupAdapter.setData(mSearchParams.cupModel.dtStrArray,-1);
        paramHolder.cupGroup.setNumColumns(paramHolder.cupAdapter.getCount());
        paramHolder.cupAdapter.notifyDataSetChanged();

//        mOptPanelHolder.brandAdapter.setList(mSearchParams.brandStringArray,-1);
//        mOptPanelHolder.brandAdapter.notifyDataSetChanged();
//        mOptPanelHolder.tagAdapter.setList(mSearchParams.tagStringArray,-1);
//        mOptPanelHolder.tagAdapter.notifyDataSetChanged();

    }


    private void initParams() {

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

        if (mTabIdx == TAB_HOME) {
            mHomeFloors.clear();
            mFloorNextPageNum = 1;
            mActivity.showLoadingLayer();
            requestTopic(mFloorNextPageNum);
            return;
        }
        if (paramHolder.priceLayout.getVisibility() == View.GONE &&
                paramHolder.brandLayout.getVisibility() == View.GONE &&
                paramHolder.funcLLayout.getVisibility() == View.GONE)
            paramHolder.extraLayout.setVisibility(View.GONE);
        else
            paramHolder.extraLayout.setVisibility(View.VISIBLE);

        researchPro(true);


    }


    private void researchPro(boolean showLoading)
    {
        mProArray.clear();
        mProNextPageNum = 1;
        if(showLoading)
            mActivity.showLoadingLayer();
        requestPage(mProNextPageNum);
    }



    public class SizeCupAdapter extends BaseAdapter
    {

        private ArrayList<String> dataset;
        public int mPickIdx = -1;
        private RelativeLayout.LayoutParams rl;
        public void setData(ArrayList<String> infos, int pick) {
            if (dataset == null)
                dataset = new ArrayList<String>();
            else
                dataset.clear();

            mPickIdx = pick;
            dataset.addAll(infos);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.choose_btn_focus);
            int w = bm.getWidth();

            int margin = (DPIUtil.getWidth() - infos.size()*w)/(infos.size()+1)/2;
            if(margin<0)
            {
                margin = 10;
                w = (DPIUtil.getWidth() - (infos.size()+1)*margin)/infos.size();
            }
            rl = new RelativeLayout.LayoutParams(w, w);

                rl.leftMargin = margin;
                rl.rightMargin = margin;

            }

        @Override
        public int getCount() {
            return (dataset==null) ? 0 : dataset.size();
        }

        public void setPick(int p)
        {
            mPickIdx = p;
        }

        public int getPickIdx()
        {
            return mPickIdx;
        }

        @Override
        public Object getItem(int position) {
            return (dataset==null) ? 0 : dataset.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

//            if (null == convertView)
//            {
//                convertView = View.inflate(mActivity, R.layout.radio_center_item, null);
//                holder = new ItemHolder();
//                holder.mName = (TextView) convertView.findViewById(com.xingy.R.id.radio_item_name);
//                holder.mName.setBackgroundResource(R.drawable.param_btn_choose_state);
//                holder.mName.setTextColor(mActivity.getResources().getColorStateList(R.color.txt_pink_white_selector));
//                convertView.setTag(holder);
//            }
//            else
//                holder = (ItemHolder) convertView.getTag();

            TextView tv = new TextView(mActivity);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(rl);

            String info = dataset.get(position);
            tv.setText(info);
            tv.setBackgroundResource(R.drawable.choose_btn_normal);
            tv.setTextColor(mActivity.getResources().getColor(R.color.global_pink));
            if(mPickIdx == position)
            {
                tv.setBackgroundResource(R.drawable.choose_btn_focus);
                tv.setTextColor(mActivity.getResources().getColor(R.color.white));
            }
            return tv;
        }
    }

}
