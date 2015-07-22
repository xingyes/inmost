package com.inmost.imbra.product;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.blog.BlogVolleyActivity;
import com.inmost.imbra.brand.BrandInfoActivity;
import com.inmost.imbra.imgallery.ImageCheckActivity;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.shopping.ShoppingActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.MyScrollView;
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
import java.util.HashMap;


public class ProductDetailActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

    public static final int  AJX_ADD_FAV = 1123;
    public static final int  AJX_REMOVE_FAV = 1124;
    public static final int  AJX_PRO_DETAIL = 1125;

    public static final String PRO_ID = "pro_id";
    private ImageLoader mImgLoader;
    private String mProId;
    private String mBrandId;
    private Ajax mAjax;
    private ProductModel proModel;

    public MyScrollView mScorllV;
    public LinearLayout contentLayout;


    private ArrayList<String> imgUrlArray;
    private HashMap<ImageView, Integer> imgvIdxMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mIntent = getIntent();
        if (mIntent == null) {
            finish();
            return;
        }

        mProId = mIntent.getStringExtra(PRO_ID);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        setContentView(R.layout.activity_scrollcontent);

        loadNavBar(R.id.scroll_nav);

        mScorllV = (MyScrollView) this.findViewById(R.id.scroll_v);
        contentLayout = (LinearLayout) this.findViewById(R.id.scroll_content);

        requestData();
//
//        initProDetail();
//
//        initRecommend();
//
//        initBrand();
//
//        initShopping();
//
//        initShoppingHint();


    }


    private void requestData() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_PRODUCT_DETAIL);
        if (null == mAjax)
            return;
        String url = mAjax.getUrl() + mProId;
        mAjax.setUrl(url);

        showLoadingLayer();
        mAjax.setId(AJX_PRO_DETAIL);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void removeFav() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_PRODUCT_DETAIL);
        if (null == mAjax)
            return;
        String url = mAjax.getUrl() + mProId;
        mAjax.setUrl(url);

        showLoadingLayer();
        mAjax.setId(AJX_REMOVE_FAV);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void addFav() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_PRODUCT_DETAIL);
        if (null == mAjax)
            return;
        String url = mAjax.getUrl() + mProId;
        mAjax.setUrl(url);

        showLoadingLayer();
        mAjax.setId(AJX_ADD_FAV);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {

        closeLoadingLayer();
        if(response.getId() == AJX_PRO_DETAIL) {
            JSONObject projson = jsonObject.optJSONObject("product");
            proModel = new ProductModel();
            proModel.parse(projson);

            initProHead();

            initContent(projson);

            initBrand(projson.optJSONObject("brand"));

            initShopping(projson.optJSONObject("stock"));

            BlogVolleyActivity.addShoppingHint(contentLayout, ProductDetailActivity.this, mImgLoader, DPIUtil.dip2px(15));
        }
        else if(response.getId()== AJX_ADD_FAV)
        {
            proModel.fav = true;
            proHolder.favbtn.setCompoundDrawables(null,proHolder.favonDrawable,null,null);
            favChanged = !favChanged;
            if(favChanged)
                setResult(RESULT_OK);
            else
                setResult(RESULT_CANCELED);
        }
        else if(response.getId()== AJX_REMOVE_FAV)
        {
            proModel.fav = false;
            proHolder.favbtn.setCompoundDrawables(null,proHolder.favoffDrawable, null, null);
            favChanged = !favChanged;
            if(favChanged)
                setResult(RESULT_OK);
            else
                setResult(RESULT_CANCELED);
        }



    }


    /**
     * Head part
     */
    private class ProViewHolder {
        View productLayout;
        AutoHeightImageView av;
        TextView titlev;
        TextView pricev;
        TextView favbtn;

        Drawable favonDrawable;
        Drawable favoffDrawable;

        View     brandLayout;
        TextView brandv;

        View     shoppingLayout;
        ImageView shopping_status_imgv;
        TextView shopping_status_infov;
        TextView shopping_sizev;

        View     relativeLayout;
        ViewPager relateivePager;

    }

    ProViewHolder proHolder;
    private boolean favChanged = false;

    private void initProHead() {
        if (null == proHolder)
            proHolder = new ProViewHolder();

        proHolder.productLayout = getLayoutInflater().inflate(R.layout.product_brief, null);
        proHolder.av = (AutoHeightImageView) proHolder.productLayout.findViewById(R.id.main_pro_img);
        proHolder.av.setImageUrl(HomeFloorModel.formBraUrl(proModel.front), mImgLoader);
        proHolder.titlev = (TextView) proHolder.productLayout.findViewById(R.id.main_pro_title);
        proHolder.titlev.setText(proModel.title);
        proHolder.pricev = (TextView) proHolder.productLayout.findViewById(R.id.main_pro_price);
        proHolder.pricev.setText(proModel.sale_price);

        proHolder.favbtn = (TextView)proHolder.productLayout.findViewById(R.id.fav_btn);
        proHolder.favonDrawable = getResources().getDrawable(R.drawable.fav_on);
        proHolder.favonDrawable.setBounds(0, 0, proHolder.favonDrawable.getMinimumWidth(), proHolder.favonDrawable.getMinimumHeight());
        proHolder.favoffDrawable = getResources().getDrawable(R.drawable.fav_off);
        proHolder.favoffDrawable.setBounds(0, 0, proHolder.favoffDrawable.getMinimumWidth(), proHolder.favoffDrawable.getMinimumHeight());

        if(proModel.fav)
            proHolder.favbtn.setCompoundDrawables(null,proHolder.favonDrawable ,null,null);
        else
            proHolder.favbtn.setCompoundDrawables(null,proHolder.favoffDrawable, null, null);

        proHolder.productLayout.findViewById(R.id.share_btn).setOnClickListener(this);
        proHolder.productLayout.findViewById(R.id.fav_btn).setOnClickListener(this);
        proHolder.productLayout.findViewById(R.id.shopping_btn).setOnClickListener(this);

        contentLayout.addView(proHolder.productLayout);
        UiUtils.makeToast(this, "proid:" + proModel.id);
    }


    /**
     * content part
     */
    private void initContent(JSONObject json) {
        BlogVolleyActivity.addArticleContent(contentLayout, this, json.optString("description"),DPIUtil.dip2px(15));
        if(null == imgUrlArray) {
            imgvIdxMap = new HashMap<ImageView, Integer>();
            imgUrlArray = new ArrayList<String>();
        }
        JSONArray ary = json.optJSONArray("pictures");
        for (int i = 0; null != ary && i < ary.length(); i++) {
            JSONObject pi = ary.optJSONObject(i);
            //img
            String imgurl = HomeFloorModel.formBraUrl(pi.optString("original"));
            int w = pi.optInt("width");
            int h = pi.optInt("height");

            String txt = pi.optString("description");

            NetworkImageView v = BlogVolleyActivity.addArticleImg(contentLayout, this, w, h);
            v.setImageUrl(imgurl, mImgLoader);
            imgvIdxMap.put(v, imgUrlArray.size());
            imgUrlArray.add(imgurl);
            v.setOnClickListener(this);

            BlogVolleyActivity.addArticleContent(contentLayout, this, txt,DPIUtil.dip2px(15));
        }
    }

    private void initBrand(JSONObject json)
    {
        mBrandId = json.optString("id");
        if(TextUtils.isEmpty(mBrandId))
            return;

        if (null == proHolder)
            proHolder = new ProViewHolder();

        proHolder.brandLayout = getLayoutInflater().inflate(R.layout.brand_brief, null);
        proHolder.brandv = (TextView) proHolder.brandLayout.findViewById(R.id.brand_info);
        proHolder.brandv.setText(json.optString("description"));

        proHolder.brandLayout.findViewById(R.id.brand_go_btn).setOnClickListener(this);
        contentLayout.addView(proHolder.brandLayout);
    }


    private void initShopping(JSONObject json)
    {
        if (null == proHolder)
            proHolder = new ProViewHolder();

        proHolder.shoppingLayout = getLayoutInflater().inflate(R.layout.shopping_pg, null);
        proHolder.shopping_status_imgv = (ImageView)proHolder.shoppingLayout.findViewById(R.id.status_img);
        proHolder.shopping_status_infov = (TextView) proHolder.shoppingLayout.findViewById(R.id.status_info);
        proHolder.shopping_sizev = (TextView) proHolder.shoppingLayout.findViewById(R.id.shopping_sizes_tv);

        proHolder.shopping_status_infov.setText("商品状态：" + json.optString("desc"));
        JSONArray sizeArray = json.optJSONArray("stocks");
        String sizeStr = "";
        String space = "   ";
        for(int i = 0; i< sizeArray.length();i++)
        {
            sizeStr += sizeArray.optJSONObject(i).optString("size");
            if(i+1<sizeArray.length())
                sizeStr += space;
        }
        proHolder.shopping_sizev.setText(sizeStr);
        proHolder.shoppingLayout.findViewById(R.id.shopping_go_btn).setOnClickListener(this);
        contentLayout.addView(proHolder.shoppingLayout);
    }



    @Override
    public void onClick(View v)
    {
        if(imgvIdxMap.containsKey(v)) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(ImageCheckActivity.REQUEST_IMGURL_LIST, imgUrlArray);
            bundle.putInt(ImageCheckActivity.REQUEST_PIC_INDEX, imgvIdxMap.get(v));
            UiUtils.startActivity(this, ImageCheckActivity.class, bundle, true);
        }
        else {
            Bundle bundle = null;
            switch (v.getId()) {
                case R.id.brand_go_btn:
                    bundle = new Bundle();
                    bundle.putString(BrandInfoActivity.BRAND_ID,mBrandId);
                    UiUtils.startActivity(ProductDetailActivity.this,BrandInfoActivity.class,bundle,true);
                    break;
                case R.id.share_btn:
                    UiUtils.makeToast(this, "go share");
                    break;
                case R.id.fav_btn:
                    if(proModel.fav)
                        removeFav();
                    else
                        addFav();
                    UiUtils.makeToast(this, "go fav");
                    break;
                case R.id.shopping_btn:
                case R.id.shopping_go_btn:
                    bundle = new Bundle();
                    bundle.putString(PRO_ID, mProId);
                    UiUtils.startActivity(ProductDetailActivity.this, ShoppingActivity.class, bundle, true);
                    break;
                default:
                    super.onClick(v);
            }
        }

    }



    @Override
    public void onNewIntent(Intent intent)
    {
        mProId = intent.getStringExtra(PRO_ID);
        contentLayout.removeAllViews();
        requestData();

        super.onNewIntent(intent);
    }



}
