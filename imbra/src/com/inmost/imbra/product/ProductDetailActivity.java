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
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.VerifyLoginActivity;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.shopping.ShoppingActivity;
import com.inmost.imbra.util.ShareUtil;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.MyScrollView;
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
import java.util.HashMap;
import java.util.Iterator;


public class ProductDetailActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

    public static final int  AJX_ADD_FAV = 1123;
    public static final int  AJX_REMOVE_FAV = 1124;
    public static final int  AJX_PRO_DETAIL = 1125;

    public static final String PRO_ID = "pro_id";
    public static final String PRO_MODEL = "pro_model";
    private ImageLoader mImgLoader;
    private String mProId;
    private Ajax mAjax;
    private ProductModel proModel;

    public MyScrollView mScorllV;
    public LinearLayout contentLayout;


    private ArrayList<String> imgUrlArray;
    private HashMap<ImageView, Integer> imgvIdxMap;
    private Account act;
    private ShareInfo  shareinfo;
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

        act = ILogin.getActiveAccount();
        requestData();

    }


    @Override
    protected void onResume()
    {
        if(null == act) {
            act = ILogin.getActiveAccount();
            requestData();
        }

        super.onResume();
    }

    private void requestData() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_PRODUCT_DETAIL);
        if (null == mAjax)
            return;
        mAjax.setData("pid",mProId);

        if(null!=act)
            mAjax.setData("uid",act.uid);
        showLoadingLayer();
        mAjax.setId(AJX_PRO_DETAIL);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    /**
     * fav modify need login
     */
    private void removeFav() {
        if(act == null)
        {
            UiUtils.startActivity(ProductDetailActivity.this,VerifyLoginActivity.class,true);
            return;
        }
        mAjax = ServiceConfig.getAjax(braConfig.URL_MODIFY_FAV);
        if (null == mAjax)
            return;

        showLoadingLayer();
        mAjax.setId(AJX_REMOVE_FAV);
        mAjax.setData("cur_id", mProId);
        mAjax.setData("token",act.token);
        mAjax.setData("act",2); //2 for del;

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void addFav() {
        if(act == null)
        {
            UiUtils.startActivity(ProductDetailActivity.this,VerifyLoginActivity.class,true);
            return;
        }

        mAjax = ServiceConfig.getAjax(braConfig.URL_MODIFY_FAV);
        if (null == mAjax)
            return;

        showLoadingLayer();
        mAjax.setId(AJX_ADD_FAV);
        mAjax.setData("cur_id", mProId);
        mAjax.setData("token",act.token);
        mAjax.setData("act",1); //1 for add;

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {

        closeLoadingLayer();
        int err = jsonObject.optInt("err");
        if(err!=0)
        {
            String msg =  jsonObject.optString("msg");
            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg): msg);
            return;
        }
        if(response.getId() == AJX_PRO_DETAIL) {
            JSONObject projson = jsonObject.optJSONObject("dt");
            proModel = new ProductModel();
            proModel.parseBra(projson);

            shareinfo = new ShareInfo();
            shareinfo.title = proModel.title;
            shareinfo.iconUrl = proModel.front;
            shareinfo.wxMomentsContent = proModel.title;
            shareinfo.wxcontent = proModel.title;
            shareinfo.url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.nuomi";

            initProHead();

            initContent(projson);

            initBrand();

            initShopping();

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
        proHolder.av.setImageUrl(proModel.front, mImgLoader);
        proHolder.titlev = (TextView) proHolder.productLayout.findViewById(R.id.main_pro_title);
        proHolder.titlev.setText(proModel.title);
        proHolder.pricev = (TextView) proHolder.productLayout.findViewById(R.id.main_pro_price);
        proHolder.pricev.setText(getString(R.string.rmb_price,proModel.sale_price));

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
    }


    /**
     * content part
     */
    private void initContent(JSONObject json) {
        if(null == imgUrlArray) {
            imgvIdxMap = new HashMap<ImageView, Integer>();
            imgUrlArray = new ArrayList<String>();
        }
        JSONArray ary = json.optJSONArray("content");
        for (int i = 0; null != ary && i < ary.length(); i++) {
            JSONObject contentJson = ary.optJSONObject(i);
            Iterator<String> keyIter = contentJson.keys();
            while (keyIter.hasNext()) {
                String keystr = keyIter.next();
                if (keystr.equals("txt")) {
                    BlogVolleyActivity.addArticleContent(contentLayout, this, contentJson.optString("txt"), DPIUtil.dip2px(15));
                } else if (keystr.equals("img")) {
                    JSONObject imgjson = contentJson.optJSONObject("img");
                    int w = imgjson.optInt("w");
                    int h = imgjson.optInt("h");
                    String imgurl = imgjson.optString("url");
                    if (TextUtils.isEmpty(imgurl))
                        continue;
                    else {
                        NetworkImageView v;
                        if (h == 0 || w == 0)
                            v = BlogVolleyActivity.addArticleImg(contentLayout, this, 640, 640);
                        else
                            v = BlogVolleyActivity.addArticleImg(contentLayout, this, w, h);
                        v.setImageUrl(imgurl, mImgLoader);
                        imgvIdxMap.put(v, imgUrlArray.size());
                        imgUrlArray.add(imgurl);
                        v.setOnClickListener(this);
                    }
                }
            }
        }
    }

    private void initBrand()
    {
        if(TextUtils.isEmpty(proModel.brandid))
            return;

        if (null == proHolder)
            proHolder = new ProViewHolder();

        proHolder.brandLayout = getLayoutInflater().inflate(R.layout.brand_brief, null);
        proHolder.brandv = (TextView) proHolder.brandLayout.findViewById(R.id.brand_info);
        proHolder.brandv.setText(proModel.brandname);

        proHolder.brandLayout.findViewById(R.id.brand_go_btn).setOnClickListener(this);
        contentLayout.addView(proHolder.brandLayout);
    }


    private void initShopping()
    {
        if (null == proHolder)
            proHolder = new ProViewHolder();

        proHolder.shoppingLayout = getLayoutInflater().inflate(R.layout.shopping_pg, null);
        proHolder.shopping_status_imgv = (ImageView)proHolder.shoppingLayout.findViewById(R.id.status_img);
        proHolder.shopping_status_infov = (TextView) proHolder.shoppingLayout.findViewById(R.id.status_info);
        proHolder.shopping_sizev = (TextView) proHolder.shoppingLayout.findViewById(R.id.shopping_sizes_tv);

        proHolder.shopping_status_infov.setText("商品状态：" + proModel.state);
        proHolder.shopping_sizev.setText(proModel.chooseStr);
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
                    bundle.putString(BrandInfoActivity.BRAND_ID,proModel.brandid);
                    UiUtils.startActivity(ProductDetailActivity.this,BrandInfoActivity.class,bundle,true);
                    break;
                case R.id.share_btn:
                    if(shareinfo!=null)
                    {
                        ShareUtil.shareInfoOut(ProductDetailActivity.this,shareinfo,mImgLoader);
                    }
                    break;
                case R.id.fav_btn:
                    if(proModel.fav)
                        removeFav();
                    else
                        addFav();
                    break;
                case R.id.shopping_btn:
                case R.id.shopping_go_btn:
                    if(ILogin.getActiveAccount()==null) {
                        UiUtils.startActivity(ProductDetailActivity.this, VerifyLoginActivity.class, true);
                        break;
                    }
                    bundle = new Bundle();
                    bundle.putSerializable(PRO_MODEL,proModel);
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
