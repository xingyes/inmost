package com.inmost.imbra.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.inmost.imbra.R;
import com.inmost.imbra.blog.BlogVolleyActivity;
import com.inmost.imbra.collect.CollectPagerActivity;
import com.inmost.imbra.imgallery.ImageCheckActivity;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductVPAdapter;
import com.inmost.imbra.search.SearchActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.MyScrollView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class O2OFragment extends Fragment implements OnSuccessListener<JSONObject>, View.OnClickListener {

    private BaseActivity mActivity;
    private View mRootView;

    private Ajax mAjax;

    private LinearLayout contentLayout;
    private HashMap<String,ImageView> imgvUrlMap;
    private ArrayList<String>         imgUrlArray;
    private HashMap<ImageView, Integer> imgvIdxMap;

    private ImageLoader mImgLoader;
    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mRootView = inflater.inflate(R.layout.activity_scrollcontent, container, false);

        initView();

        requestO2O();

        return mRootView;
    }


    @Override
    public void onAttach(Activity activity) {
        mActivity = (BaseActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void requestO2O() {

        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_BLOG);
        if (null == mAjax)
            return;
        String url = mAjax.getUrl() + "85";

        mAjax.setUrl(url);

        mActivity.showLoadingLayer();

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(mActivity);
        mAjax.send();

    }

    @Override
    public void onDestroy() {
        if (mAjax != null) {
            mAjax.abort();
            mAjax = null;
        }
        super.onDestroy();
    }


    private void initView() {
        mRootView.findViewById(R.id.scroll_nav).setVisibility(View.GONE);
        contentLayout = (LinearLayout) mRootView.findViewById(R.id.scroll_content);
    }


    @Override
    public void onSuccess(JSONObject json, Response response) {

        mActivity.closeLoadingLayer();
        JSONObject blogjson = json.optJSONObject("blog");

        if(imgvIdxMap == null) {
            imgvUrlMap = new HashMap<String,ImageView>();
            imgUrlArray = new ArrayList<String>();
            imgvIdxMap = new HashMap<ImageView, Integer>();
        }
        contentLayout.removeAllViews();

        BlogVolleyActivity.addArticleContent(contentLayout, mActivity, blogjson.optString("content"), DPIUtil.dip2px(15));
        JSONArray ary = blogjson.optJSONArray("pictures");
        for(int i =0 ;null!=ary && i < ary.length();i++) {
            JSONObject pi = ary.optJSONObject(i);
            //img
            String imgurl = HomeFloorModel.formBraUrl(pi.optString("original"));
            int w = pi.optInt("width");
            int h = pi.optInt("height");

            String txt = pi.optString("description");

            NetworkImageView v = BlogVolleyActivity.addArticleImg(contentLayout, mActivity, w, h);
            v.setImageUrl(imgurl,mImgLoader);
            imgvIdxMap.put(v, imgvUrlMap.size());
            imgUrlArray.add(imgurl);
            v.setOnClickListener(this);
            imgvUrlMap.put(imgurl,v);

            BlogVolleyActivity.addArticleContent(contentLayout, mActivity, txt, DPIUtil.dip2px(15));
        }


        for(int i =0 ;null!=ary && i < 4;i++) {
            String shop1 = "{\"name\":\"XXX省XXX市某某商场2F\"}";
            String shop2 = "{\"name\":\"XXX省XXX市某某商场3F\"}";
            String shop3 = "{\"name\":\"XXX省XXX市某某商场4F\"}";
            String shop4 = "{\"name\":\"XXX省XXX市某某商场5F\"}";

            String pa = "{\"area\":\"华东" + i + "区\",\"shoplist\":[  " +
                    shop1 + "," + shop2 + "," + shop3 +"," + shop4 +
                    "]}";

            try {
                JSONObject pi = new JSONObject(pa);
                BlogVolleyActivity.addO2oShop(contentLayout,mActivity,pi,DPIUtil.dip2px(15));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        BlogVolleyActivity.addCuttingLine(contentLayout, mActivity, mActivity.getString(R.string.more_o2o_shop),
                mActivity.getResources().getColor(R.color.global_pink),
                getResources().getColor(R.color.global_nav_text),
                DPIUtil.dip2px(60),0);

    }

    public void setImgLoader(ImageLoader imgLoader) {
        this.mImgLoader = imgLoader;
    }


    @Override
    public void onClick(View v) {
        if(imgvIdxMap.containsKey(v)) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(ImageCheckActivity.REQUEST_IMGURL_LIST, imgUrlArray);
            bundle.putInt(ImageCheckActivity.REQUEST_PIC_INDEX, imgvIdxMap.get(v));
            UiUtils.startActivity(mActivity, ImageCheckActivity.class, bundle, true);
        }
    }
}
