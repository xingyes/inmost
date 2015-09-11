package com.inmost.imbra.blog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.imgallery.ImageCheckActivity;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductModel;
import com.inmost.imbra.product.ProductVPAdapter;
import com.inmost.imbra.util.ShareUtil;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.model.ProvinceModel;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.MyScrollView;
import com.xingy.lib.ui.ScrollStopableViewPager;
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

public class BlogVolleyActivity extends BaseActivity implements
        OnSuccessListener<JSONObject> {


    public static final String BLOG_ID = "blog_id";

    private String  mId;
    private RequestQueue mQueue;
    private ImageLoader mImgLoader;

//    private ArrayList<TextImgModel> contentList;

    private HashMap<String,ImageView> imgvUrlMap;
    private ArrayList<String>         imgUrlArray;
    private HashMap<ImageView, Integer> imgvIdxMap;
    public static final int  MSG_DRAW_IMG = 1001;

    public MyScrollView mScorllV;
    public LinearLayout contentLayout;

    public boolean scrollIng = false;
    private Account   account;
    private Ajax mAjax;
    private ShareInfo shareinfo;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg)
//        {
//            if(msg.what == MSG_DRAW_IMG)
//            {
//                ImageView v = null;
//                boolean fetching = false;
//                for (int i = 0; i < imgUrlArray.size(); i++) {
//                    String iurl = imgUrlArray.get(i);
//                    if (TextUtils.isEmpty(iurl))
//                        continue;
//                    v = imgvUrlMap.get(iurl);
//                    if (null == v || v.getTag()!=null)
//                        continue;
//                    int top = v.getTop();
//                    int bottom = v.getBottom();
//                    int containH = mScorllV.getHeight();
//                    int sy = mScorllV.getScrollY();
//                    if(containH + sy > top && sy < bottom && !scrollIng)
//                    {
//                        if(fetching)
//                        {
//                            this.removeCallbacksAndMessages(null);
//                            this.sendEmptyMessageDelayed(MSG_DRAW_IMG,400);
//                            break;
//                        }
//                        else
////                            mImgLoader.get(iurl,BlogVolleyActivity.this);
//                    }
//                }
//            }
//            else
//                super.handleMessage(msg);
//
//        }
//    };



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);
        IMbraApplication.globalMDCache.clearMemCache();

        account = ILogin.getActiveAccount();
        setContentView(R.layout.activity_scrollcontent);

//        contentList = new ArrayList<TextImgModel>();

        imgvUrlMap = new HashMap<String, ImageView>();
        Intent intent = getIntent();
        if(intent == null)
        {
            finish();
            return;
        }
        mId = intent.getStringExtra(BLOG_ID);
        requestData();
        initviews();
	}

    private void requestData() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_BLOG);
        if (null == mAjax)
            return;

        mAjax.setData("pn", 1);
        mAjax.setData("ps", 10);
        mAjax.setData("dp", ToolUtil.getAppWidth() + "*" + ToolUtil.getAppHeight());
        if(null!=account)
            mAjax.setData("uid",account.uid);

        mAjax.setData("id",mId);


        showLoadingLayer();

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    @Override
	protected void onDestroy() {
//		if(null!=mImgLoader)
//            mImgLoader.cleanup();
//        mImgLoader = null;
        super.onDestroy();
	}


	private void initviews() {
        loadNavBar(R.id.scroll_nav);
        mNavBar.setText(R.string.menu_blog);
        mNavBar.setRightInfo(R.string.share_loading, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != shareinfo)
                    ShareUtil.shareInfoOut(BlogVolleyActivity.this, shareinfo, mImgLoader);
            }
        });

        mScorllV = (MyScrollView) this.findViewById(R.id.scroll_v);
//        mScorllV.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction()==MotionEvent.ACTION_CANCEL
//                        || event.getAction()==MotionEvent.ACTION_UP) {
//                    mHandler.removeCallbacksAndMessages(null);
//                    mHandler.sendEmptyMessage(MSG_DRAW_IMG);
//                    touchIng = false;
//                }
//                else
//                    touchIng = true;
//                return false;
//            }
//        });
        mScorllV.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(boolean bIsScrolling) {
                scrollIng = bIsScrolling;
//                if (!bIsScrolling) {
//                    mHandler.removeCallbacksAndMessages(null);
//                    mHandler.sendEmptyMessageDelayed(MSG_DRAW_IMG,400);
//                }
//                else
//                    mHandler.removeCallbacksAndMessages(null);
            }

        });
        contentLayout = (LinearLayout) this.findViewById(R.id.scroll_content);
    }


//    @Override
//    public void onLoaded(Bitmap bm, String strUrl) {
//        ImageView v = imgvUrlMap.get(strUrl);
//        v.setImageBitmap(bm);
//        v.setTag(strUrl);
//    }

//    @Override
//    public void onError(String strUrl) {
//
//    }

    @Override
    public void onSuccess(JSONObject json, Response response) {
        closeLoadingLayer();

        int err = json.optInt("err");
        if(err!=0)
        {
            String msg =  json.optString("msg");
            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg): msg);
            return;
        }

        JSONArray ar = json.optJSONArray("dt");
        if(null==ar || ar.length()<=0)
            return;

        JSONObject blogjson = ar.optJSONObject(0);

        if(imgvIdxMap == null) {
            imgUrlArray = new ArrayList<String>();
            imgvIdxMap = new HashMap<ImageView, Integer>();
        }
        contentLayout.removeAllViews();

        String strTitle = blogjson.optString("tit");
        addArticleTitle(contentLayout,BlogVolleyActivity.this,strTitle);
        shareinfo = new ShareInfo();
        shareinfo.title = strTitle;

        addArticleAuthor(contentLayout, BlogVolleyActivity.this, blogjson.optString("usr"));

        JSONArray ary = blogjson.optJSONArray("dt");
        for(int i =0 ;null!=ary && i < ary.length();i++) {
            JSONObject pi = ary.optJSONObject(i);
            Iterator<String> iter = pi.keys();
            while(iter.hasNext()) {
                String key = iter.next();
                if (key.equals("txt")) {
                    String txt = pi.optString("txt");
                    addArticleContent(contentLayout, BlogVolleyActivity.this, txt, DPIUtil.dip2px(15));
                    if(TextUtils.isEmpty(shareinfo.wxMomentsContent))
                    {
                        shareinfo.wxMomentsContent = txt;
                        shareinfo.wxcontent = txt;
                        shareinfo.url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.nuomi";
                    }

                }
                if (key.equals("img")) {
                    //img
                    JSONObject imgb = pi.optJSONObject(key);
                    String imgurl = HomeFloorModel.formBraUrl(imgb.optString("url"));
                    int w = imgb.optInt("w");
                    int h = imgb.optInt("h");

                    NetworkImageView v = addArticleImg(contentLayout, BlogVolleyActivity.this, w, h);
                    v.setImageUrl(imgurl, mImgLoader);
                    imgvIdxMap.put(v, imgvUrlMap.size());
                    imgUrlArray.add(imgurl);
                    v.setOnClickListener(this);
                    imgvUrlMap.put(imgurl, v);

                    if(TextUtils.isEmpty(shareinfo.iconUrl))
                        shareinfo.iconUrl = imgurl;

                }
            }
        }



        final JSONArray relarry = blogjson.optJSONArray("pl");
        addRelativePro(contentLayout,BlogVolleyActivity.this,relarry,mImgLoader,
                new ProductVPAdapter.OnVPItemClickListener() {
                    @Override
                    public void onVPItemClick(int pos, String id) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ProductDetailActivity.PRO_ID,id);
                        UiUtils.startActivity(BlogVolleyActivity.this,ProductDetailActivity.class,bundle,true);
                    }
                });


    }

    @Override
    public void onClick(View v)
    {
        if(imgvIdxMap.containsKey(v)) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(ImageCheckActivity.REQUEST_IMGURL_LIST, imgUrlArray);
            bundle.putInt(ImageCheckActivity.REQUEST_PIC_INDEX, imgvIdxMap.get(v));
            UiUtils.startActivity(BlogVolleyActivity.this, ImageCheckActivity.class, bundle, true);
        }
        else
            super.onClick(v);

    }


    /**
     * static api --------------------------------------------------
     */
    /**
     *
     * @param container
     * @param context
     * @param content
     * @return
     */
    public static View addArticleTitle(LinearLayout container,Context context, final String content)
    {
        if(TextUtils.isEmpty(content))
            return null;

        TextView tv = new TextView(context);
//        SpannableStringBuilder style=new SpannableStringBuilder(content);
//        style.setSpan(new BackgroundColorSpan(Color.BLACK),0,content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        tv.setText(style);
        tv.setText(content);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        float txtPx = context.getResources().getDimension(R.dimen.bar_font_38);
        int txtSp = DPIUtil.px2sp(context, txtPx);
        tv.setTextSize(txtSp);
        tv.setLineSpacing(DPIUtil.dip2px(1), 1.25f);  //2.5

        tv.setTextColor(context.getResources().getColor(R.color.global_text_gray));
        tv.setPadding(DPIUtil.dip2px(5),DPIUtil.dip2px(5),DPIUtil.dip2px(5),DPIUtil.dip2px(5));

        ll.leftMargin =  DPIUtil.dip2px(15);
        ll.rightMargin =  DPIUtil.dip2px(55);
        ll.topMargin =  DPIUtil.dip2px(30);
        ll.bottomMargin  = DPIUtil.dip2px(3);

        container.addView(tv,ll);
        return tv;
    }

    public static View addArticleAuthor(LinearLayout container,Context context, final String content)
    {
        if(TextUtils.isEmpty(content))
            return null;

        TextView tv = new TextView(context);
        tv.setText(content);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        tv.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        float txtPx = context.getResources().getDimension(R.dimen.bar_font_36);
        int txtSp = DPIUtil.px2sp(context,txtPx);
        tv.setTextSize(txtSp);

        tv.setTextColor(context.getResources().getColor(R.color.global_pink));
        tv.setPadding(DPIUtil.dip2px(5),DPIUtil.dip2px(5),DPIUtil.dip2px(5),DPIUtil.dip2px(5));

        ll.leftMargin =  DPIUtil.dip2px(15);
        ll.rightMargin =  DPIUtil.dip2px(55);
        ll.topMargin =  DPIUtil.dip2px(4);
        ll.bottomMargin  = DPIUtil.dip2px(21);

        container.addView(tv,ll);
        return tv;
    }

    public static View addArticleContent(LinearLayout container,Context context, final String content,int padding)
    {
        if(TextUtils.isEmpty(content))
            return null;
        TextView tv = new TextView(context);
        tv.setClickable(false);
        tv.setText(content);
        float txtPx = context.getResources().getDimension(R.dimen.bar_font_24);
        int txtSp = DPIUtil.px2sp(context,txtPx);
        tv.setTextSize(txtSp);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv.setGravity(Gravity.LEFT |Gravity.CENTER_VERTICAL);
        tv.setLineSpacing(DPIUtil.dip2px(2), 1.25f);  //2.5
        tv.setTextColor(context.getResources().getColor(R.color.global_text_gray));
        ll.leftMargin =  padding;
        ll.rightMargin =  padding;
        ll.topMargin =  DPIUtil.dip2px(7);
        ll.bottomMargin  = DPIUtil.dip2px(7);

        container.addView(tv,ll);
        return tv;
    }


    public static View addCuttingLine(LinearLayout container,Context context, final String content, int bgcolor,int txtcolor,
                                      int height, int padding)
    {
        if(TextUtils.isEmpty(content))
            return null;

        LayoutInflater li = LayoutInflater.from(context);
        View root = li.inflate(R.layout.cutting_line_with_string, null);
        root.setBackgroundColor(bgcolor);
        TextView tv = (TextView)root.findViewById(R.id.cutting_tv);
        tv.setClickable(false);
        tv.setText(content);
        tv.setTextColor(txtcolor);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(height >0)
        {
            ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                height);

//            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, height);
//            root.setLayoutParams(rl);
        }

        ll.leftMargin =  padding;
        ll.rightMargin =  padding;
        ll.topMargin =  DPIUtil.dip2px(7);
        ll.bottomMargin  = padding;

        container.addView(root,ll);
        return root;
    }



    public static View addMoreClick(LinearLayout container,Context context, final String content)
    {
        if(TextUtils.isEmpty(content))
            return null;

        LayoutInflater li = LayoutInflater.from(context);
        View root = li.inflate(R.layout.cutting_line_with_string, null);

        TextView tv = new TextView(context);
        tv.setText(content);
        tv.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        tv.setTextColor(context.getResources().getColor(R.color.global_pink));
        tv.setCompoundDrawablePadding(DPIUtil.dip2px(5));
        Drawable db = context.getResources().getDrawable(R.drawable.icon_down_arrow);
        db.setBounds(0, 0, db.getMinimumWidth(), db.getMinimumHeight());
        tv.setCompoundDrawables(null, null, null, db);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ll.leftMargin =  0;
        ll.rightMargin =  0;
        ll.topMargin =  DPIUtil.dip2px(7);
        ll.bottomMargin  = DPIUtil.dip2px(7);

        container.addView(tv,ll);
        return tv;
    }


//    public static View addLessClick(LinearLayout container,Context context, final String content)
//    {
//        if(TextUtils.isEmpty(content))
//            return null;
//
//        LayoutInflater li = LayoutInflater.from(context);
//        View root = li.inflate(R.layout.cutting_line_with_string, null);
//
//        TextView tv = new TextView(context);
//        tv.setText(content);
//        tv.setBackgroundColor(context.getResources().getColor(R.color.transparent));
//        tv.setTextColor(context.getResources().getColor(R.color.global_pink));
//        tv.setCompoundDrawablePadding(DPIUtil.dip2px(5));
//        Drawable db = context.getResources().getDrawable(R.drawable.icon_down_arrow);
//        db.setBounds(0, 0, db.getMinimumWidth(), db.getMinimumHeight());
//        tv.setCompoundDrawables(null, null, null, db);
//        tv.setGravity(Gravity.CENTER);
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        ll.leftMargin =  0;
//        ll.rightMargin =  0;
//        ll.topMargin =  DPIUtil.dip2px(7);
//        ll.bottomMargin  = DPIUtil.dip2px(7);
//
//        container.addView(tv,ll);
//        return tv;
//    }


    public static View addRelativePro(LinearLayout container,Context context, final JSONArray array, ImageLoader imgLoader,
                                      ProductVPAdapter.OnVPItemClickListener listener) {
        if(null == array || array.length()<=0)
            return null;

        LayoutInflater li = LayoutInflater.from(context);
        View root = li.inflate(R.layout.relative_pro_vp, null);

        ArrayList<ProductModel> relativeProArra = new ArrayList<ProductModel>();
        for(int i = 0 ; i < array.length();i++) {
            ProductModel proMod = new ProductModel();
            proMod.parseRelative(array.optJSONObject(i));
            relativeProArra.add(proMod);
        }


        int height = (int)(ToolUtil.getAppWidthDip() / 2.5) + (6 + 15) + (10 + 16) + (10);
        RelativeLayout.LayoutParams  rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                DPIUtil.dip2px(height));
        rl.addRule(RelativeLayout.BELOW,R.id.relative_title_layout);
        ScrollStopableViewPager vp = (ScrollStopableViewPager)root.findViewById(R.id.viewpager);

        vp.setClipChildren(false);
        vp.setOffscreenPageLimit(2);
        vp.setLayoutParams(rl);

        ProductVPAdapter pvpAdapter = new ProductVPAdapter(context,imgLoader,relativeProArra,listener);
        vp.setAdapter(pvpAdapter);
        if(pvpAdapter.getCount() <3)
            vp.setScanScroll(false);
        pvpAdapter.notifyDataSetChanged();

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ll.leftMargin = 0;
        ll.rightMargin = 0;
        ll.topMargin = DPIUtil.dip2px(7);
        ll.bottomMargin = DPIUtil.dip2px(7);

        container.addView(root, ll);
        return root;

    }

    public static NetworkImageView addArticleImg(LinearLayout container,Context context, int w, int h)
    {
        NetworkImageView iv = new NetworkImageView(context);
        int  nw = container.getWidth() - DPIUtil.dip2px(15)*2;
        int nh = nw * h / w;

        iv.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                nw,nh);
        ll.leftMargin =  DPIUtil.dip2px(15);
        ll.rightMargin =  DPIUtil.dip2px(15);
        ll.topMargin =  DPIUtil.dip2px(10);
        ll.bottomMargin  = DPIUtil.dip2px(10);

        iv.setLayoutParams(ll);
        container.addView(iv,ll);
        return iv;
    }


    public static View addShoppingHint(final LinearLayout container,Context context,ImageLoader imgloader,int padding)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View root = li.inflate(R.layout.shopping_hint_page, null);


        final  String flowImgUrl = "http://www.o2bra.com.cn/assets/site/img/step-buy.jpg";

        final View  hinTv = root.findViewById(R.id.shopping_hint_tv);
        final View  retreaTv = root.findViewById(R.id.shopping_retreat_tv);
        final NetworkImageView  flowImgv = (NetworkImageView)root.findViewById(R.id.shopping_flow_imgv);
        flowImgv.setImageUrl(flowImgUrl,imgloader);

        RadioGroup rg = (RadioGroup)root.findViewById(R.id.shopping_hint_rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            private int lastCheckId = -1;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(lastCheckId == checkedId)
                    return;
                lastCheckId = checkedId;
                switch (checkedId)
                {
                    case R.id.tab_flow_rb:
                        hinTv.setVisibility(View.INVISIBLE);
                        retreaTv.setVisibility(View.INVISIBLE);
                        flowImgv.setVisibility(View.VISIBLE);
                        break;
                    case R.id.tab_retreat_rb:
                        hinTv.setVisibility(View.INVISIBLE);
                        retreaTv.setVisibility(View.VISIBLE);
                        flowImgv.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.tab_hint_rb:
                    default:
                        hinTv.setVisibility(View.VISIBLE);
                        retreaTv.setVisibility(View.INVISIBLE);
                        flowImgv.setVisibility(View.INVISIBLE);
                        break;
                }
        }});



        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ll.leftMargin = padding;
        ll.rightMargin = padding;
        ll.topMargin = DPIUtil.dip2px(7);
        ll.bottomMargin = DPIUtil.dip2px(7);

        container.addView(root, ll);
        rg.check(R.id.tab_hint_rb);
        return root;
    }

    public static void addO2oShop(LinearLayout container, Context context, JSONObject shoplist, int padding) {
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(ll);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setClickable(false);

        ImageView iconv = new ImageView(context);
        iconv.setImageResource(R.drawable.icon_pos);
        ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        iconv.setLayoutParams(ll);
        ll.gravity = Gravity.TOP;
        ll.topMargin = DPIUtil.dip2px(4);
        ll.rightMargin = DPIUtil.dip2px(2);
        layout.addView(iconv,ll);


        TextView tv = new TextView(context);
        StringBuilder sb = new  StringBuilder();
        sb.append(shoplist.optString("area"));
        sb.append("\n");
        JSONArray jary = shoplist.optJSONArray("shoplist");
        for(int i = 0 ; null!=jary && i < jary.length(); i++)
        {
            JSONObject item = jary.optJSONObject(i);
            sb.append(item.optString("name"));
            sb.append("\n");
        }

        tv.setText(sb.toString());

        float txtPx = context.getResources().getDimension(R.dimen.bar_font_24);
        int txtSp = DPIUtil.px2sp(context,txtPx);
        tv.setTextSize(txtSp);
        ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv.setGravity(Gravity.LEFT |Gravity.TOP);
        tv.setLineSpacing(DPIUtil.dip2px(2), 1.25f);  //2.5
        tv.setTextColor(context.getResources().getColor(R.color.global_text_gray));
        layout.addView(tv,ll);

        ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ll.leftMargin =  padding;
        ll.rightMargin =  padding;
        ll.topMargin =  DPIUtil.dip2px(7);
        ll.bottomMargin  = DPIUtil.dip2px(7);


        container.addView(layout,ll);
    }



//    public class BitmapCache implements ImageLoader.ImageCache {
//
//        private LruCache<String, Bitmap> mCache;
//
//        public BitmapCache() {
//            int maxSize = 10 * 1024 * 1024;
//            mCache = new LruCache<String, Bitmap>(maxSize) {
//                @Override
//                protected long sizeOf(String key, Bitmap bitmap) {
//                    return bitmap.getRowBytes() * bitmap.getHeight();
//                }
//            };
//        }
//
//        @Override
//        public Bitmap getBitmap(String url) {
//            return mCache.get(url);
//        }
//
//        @Override
//        public void putBitmap(String url, Bitmap bitmap) {
//            mCache.put(url, bitmap);
//        }
//    }
}
