package com.inmost.imbra.brand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.blog.BlogVolleyActivity;
import com.inmost.imbra.collect.CollectPagerActivity;
import com.inmost.imbra.main.HomeFloorAdapter;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductVPAdapter;
import com.inmost.imbra.util.braConfig;
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


public class BrandInfoActivity extends BaseActivity implements OnSuccessListener<JSONObject>, AdapterView.OnItemClickListener {

    public static final String BRAND_ID = "brand_id";
    private ImageLoader mImgLoader;
    private String mBrandId;
    private Ajax mAjax;

    private ArrayList<HomeFloorModel> mHomeFloors;
    private ArrayList<HomeFloorModel> mFirstShowHomeFloors;

    private HomeFloorAdapter mFloorAdapter;

    private ListView         mFloorListV;
    private LinearLayout             mMoreFloorView;
    public static final int  SHOW_BLOG_SIZE = 3;

    private View.OnClickListener  mMoreLessListener;
    private Boolean               bShowAll = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mIntent = getIntent();
        if (mIntent == null) {
            finish();
            return;
        }

        mBrandId = mIntent.getStringExtra(BRAND_ID);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        setContentView(R.layout.activity_listcontent);

        loadNavBar(R.id.list_nav);


        mFloorListV = (ListView) this.findViewById(R.id.list_content);
        mFloorListV.setOnItemClickListener(this);

        mFloorAdapter = new HomeFloorAdapter(this,mImgLoader);
        mHomeFloors = new ArrayList<HomeFloorModel>();
        mFirstShowHomeFloors = new ArrayList<HomeFloorModel>();

        requestData();

    }


    private void requestData() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_BRAND_INFO);
        if (null == mAjax)
            return;
        String url = mAjax.getUrl() + mBrandId;
        mAjax.setUrl(url);

        showLoadingLayer();

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();

        JSONObject brandjson = jsonObject.optJSONObject("brand");
        mNavBar.setText(brandjson.optString("name"));

        LinearLayout ll = new LinearLayout(this);
        BlogVolleyActivity.addArticleContent(ll, this, brandjson.optString("description"), DPIUtil.dip2px(15));
        mFloorListV.addHeaderView(ll);


        ll = new LinearLayout(this);
        BlogVolleyActivity.addCuttingLine(ll, this, this.getString(R.string.brand_blog),
                getResources().getColor(R.color.transparent),
                getResources().getColor(R.color.global_pink), -2, DPIUtil.dip2px(15));
        mFloorListV.addHeaderView(ll);

        JSONArray feeds = jsonObject.optJSONArray("feeds");
        if (null != feeds) {
            for (int i = 0; i < feeds.length(); i++) {
                HomeFloorModel model = new HomeFloorModel();
                model.parseBrandFloor(feeds.optJSONObject(i));
                mHomeFloors.add(model);
                if (i < SHOW_BLOG_SIZE)
                    mFirstShowHomeFloors.add(model);
            }
            mFloorAdapter.setData(mFirstShowHomeFloors);

            /**
             * show more all less
             */
            bShowAll = true;
            if (mHomeFloors.size() >= SHOW_BLOG_SIZE) {
                mMoreFloorView = new LinearLayout(this);
                mFloorListV.addFooterView(mMoreFloorView);
                BlogVolleyActivity.addMoreClick(mMoreFloorView, this, "展开其他" + (mHomeFloors.size() - SHOW_BLOG_SIZE) + "个");
                bShowAll = false;
                if(null==mMoreLessListener) {
                    mMoreLessListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bShowAll) {
                                mMoreFloorView.removeAllViews();
                                BlogVolleyActivity.addMoreClick(mMoreFloorView, BrandInfoActivity.this, "展开其他" + (mHomeFloors.size() - SHOW_BLOG_SIZE) + "个");
                                bShowAll = false;
                                mFloorAdapter.setData(mFirstShowHomeFloors);
                                mFloorAdapter.notifyDataSetChanged();
                            } else {
                                mMoreFloorView.removeAllViews();
//                                BlogVolleyActivity.addLessClick(mMoreFloorView, BrandInfoActivity.this, "收起");
                                bShowAll = true;
                                mFloorAdapter.setData(mHomeFloors);
                                mFloorAdapter.notifyDataSetChanged();
                            }
                        }
                    };
                }

                mMoreFloorView.setOnClickListener(mMoreLessListener);
            }
        }


        ll = new LinearLayout(this);
        BlogVolleyActivity.addRelativePro(ll, this, jsonObject.optJSONArray("products"), mImgLoader,
                new ProductVPAdapter.OnVPItemClickListener() {
                    @Override
                    public void onVPItemClick(int pos, String id) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ProductDetailActivity.PRO_ID,id);
                        UiUtils.startActivity(BrandInfoActivity.this,ProductDetailActivity.class,bundle,true);
                    }
                });
        mFloorListV.addFooterView(ll);

        mFloorListV.setAdapter(mFloorAdapter);


    }


    @Override
    public void onClick(View v)
    {
//        if(imgvIdxMap.containsKey(v)) {
//            Bundle bundle = new Bundle();
//            bundle.putStringArrayList(ImageCheckActivity.REQUEST_IMGURL_LIST, imgUrlArray);
//            bundle.putInt(ImageCheckActivity.REQUEST_PIC_INDEX, imgvIdxMap.get(v));
//            UiUtils.startActivity(this, ImageCheckActivity.class, bundle, true);
//        }
//        else {
            switch (v.getId()) {
                case R.id.brand_go_btn:
                    UiUtils.makeToast(this, "go brand");
                    break;
                case R.id.share_btn:
                    UiUtils.makeToast(this, "go share");
                    break;
                case R.id.fav_btn:
                    UiUtils.makeToast(this, "go fav");
                    break;
                case R.id.shopping_btn:
                case R.id.shopping_go_btn:
                    UiUtils.makeToast(this, "go shopping");
                    break;
                default:
                    super.onClick(v);
            }


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == mFloorListV )
        {
            HomeFloorModel item = mHomeFloors.get(position-1);
            if(item.type == HomeFloorModel.TYPE_LOOKBOOK)
                UiUtils.makeToast(this,"look");
            else if(item.type == HomeFloorModel.TYPE_COLLECTION)
            {
                Bundle bund = new Bundle();
                bund.putString(CollectPagerActivity.COLLECT_ID,item.type_id);
                UiUtils.startActivity(this, CollectPagerActivity.class, bund, true);
            }
            else if(item.type == HomeFloorModel.TYPE_BLOG)
            {
                Bundle bund = new Bundle();
                bund.putString(BlogVolleyActivity.BLOG_ID,item.type_id);
                UiUtils.startActivity(this, BlogVolleyActivity.class, bund, true);
            }
        }
    }
}
