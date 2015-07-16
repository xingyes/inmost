//package com.inmost.imbra.blog;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.inmost.imbra.R;
//import com.inmost.imbra.imgallery.ImageCheckActivity;
//import com.inmost.imbra.main.HomeFloorModel;
//import com.inmost.imbra.util.braConfig;
//import com.xingy.lib.ui.MyScrollView;
//import com.xingy.lib.ui.UiUtils;
//import com.xingy.util.DPIUtil;
//import com.xingy.util.ImageLoadListener;
//import com.xingy.util.ImageLoader;
//import com.xingy.util.ServiceConfig;
//import com.xingy.util.activity.BaseActivity;
//import com.xingy.util.ajax.Ajax;
//import com.xingy.util.ajax.OnSuccessListener;
//import com.xingy.util.ajax.Response;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class BlogActivity extends BaseActivity implements ImageLoadListener,
//        OnSuccessListener<JSONObject> {
//
//
//    public static final String BLOG_ID = "blog_id";
//
//    private String  mId;
//    private ImageLoader mImgLoader;
//
////    private ArrayList<TextImgModel> contentList;
//
//    private HashMap<String,ImageView> imgvUrlMap;
//    private ArrayList<String>         imgUrlArray;
//    private HashMap<ImageView, Integer> imgvIdxMap;
//    public static final int  MSG_DRAW_IMG = 1001;
//
//    public MyScrollView mScorllV;
//    public LinearLayout contentLayout;
//
//    public boolean scrollIng = false;
//
//    private Ajax mAjax;
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
//                            mImgLoader.get(iurl,BlogActivity.this);
//                    }
//                }
//            }
//            else
//                super.handleMessage(msg);
//
//        }
//    };
//
//
//
//    @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//        mImgLoader = new ImageLoader(this, braConfig.PIC_CACHE_DIR);
//        setContentView(R.layout.activity_scrollcontent);
//
////        contentList = new ArrayList<TextImgModel>();
//
//        imgvUrlMap = new HashMap<String, ImageView>();
//        Intent intent = getIntent();
//        if(intent == null)
//        {
//            finish();
//            return;
//        }
//        mId = intent.getStringExtra(BLOG_ID);
//
//        requestData();
//        initviews();
//	}
//
//    private void requestData() {
//        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_BLOG);
//        if (null == mAjax)
//            return;
//        String url = mAjax.getUrl() + mId;
//        mAjax.setUrl(url);
//
//        showLoadingLayer();
//
//        mAjax.setOnSuccessListener(this);
//        mAjax.setOnErrorListener(this);
//        mAjax.send();
//    }
//
//
//    @Override
//	protected void onDestroy() {
//		if(null!=mImgLoader)
//            mImgLoader.cleanup();
//        mImgLoader = null;
//        super.onDestroy();
//	}
//
//
//	private void initviews() {
//        loadNavBar(R.id.scroll_nav);
//
//        mScorllV = (MyScrollView) this.findViewById(R.id.scroll_v);
////        mScorllV.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                if(event.getAction()==MotionEvent.ACTION_CANCEL
////                        || event.getAction()==MotionEvent.ACTION_UP) {
////                    mHandler.removeCallbacksAndMessages(null);
////                    mHandler.sendEmptyMessage(MSG_DRAW_IMG);
////                    touchIng = false;
////                }
////                else
////                    touchIng = true;
////                return false;
////            }
////        });
//        mScorllV.setOnScrollListener(new MyScrollView.OnScrollListener() {
//            @Override
//            public void onScroll(boolean bIsScrolling) {
//                scrollIng = bIsScrolling;
//                if (!bIsScrolling) {
//                    mHandler.removeCallbacksAndMessages(null);
//                    mHandler.sendEmptyMessageDelayed(MSG_DRAW_IMG,400);
//                }
//                else
//                    mHandler.removeCallbacksAndMessages(null);
//            }
//
//        });
//        contentLayout = (LinearLayout) this.findViewById(R.id.scroll_content);
//    }
//
//
//    @Override
//    public void onLoaded(Bitmap bm, String strUrl) {
//        ImageView v = imgvUrlMap.get(strUrl);
//        v.setImageBitmap(bm);
//        v.setTag(strUrl);
//    }
//
//    @Override
//    public void onError(String strUrl) {
//
//    }
//
//    @Override
//    public void onSuccess(JSONObject json, Response response) {
//        closeLoadingLayer();
//        JSONObject blogjson = json.optJSONObject("blog");
//
//        if(imgvIdxMap == null) {
//            imgUrlArray = new ArrayList<String>();
//            imgvIdxMap = new HashMap<ImageView, Integer>();
//        }
//        contentLayout.removeAllViews();
//        addArticleTitle(contentLayout,BlogActivity.this,blogjson.optString("title"));
//
//        JSONObject ob = blogjson.optJSONObject("author");
//        addArticleAuthor(contentLayout, BlogActivity.this, ob.optString("name"));
//
//        addArticleContent(contentLayout,BlogActivity.this,blogjson.optString("content"));
//        JSONArray ary = blogjson.optJSONArray("pictures");
//        for(int i =0 ;null!=ary && i < ary.length();i++) {
//            JSONObject pi = ary.optJSONObject(i);
//            //img
//            String imgurl = HomeFloorModel.formBraUrl(pi.optString("original"));
//            int w = pi.optInt("width");
//            int h = pi.optInt("height");
//
//            String txt = pi.optString("description");
//
//            ImageView v = addArticleImg(contentLayout,BlogActivity.this,w,h);
//            imgUrlArray.add(imgurl);
//            imgvIdxMap.put(v, imgvUrlMap.size());
//            v.setOnClickListener(this);
//            imgvUrlMap.put(imgurl,v);
//
//            addArticleContent(contentLayout,BlogActivity.this,txt);
//        }
//
//        mHandler.sendEmptyMessageDelayed(MSG_DRAW_IMG,400);
//
//    }
//
//    @Override
//    public void onClick(View v)
//    {
//        if(imgvIdxMap.containsKey(v)) {
//            Bundle bundle = new Bundle();
//            bundle.putStringArrayList(ImageCheckActivity.REQUEST_IMGURL_LIST, imgUrlArray);
//            bundle.putInt(ImageCheckActivity.REQUEST_PIC_INDEX, imgvIdxMap.get(v));
//            UiUtils.startActivity(BlogActivity.this, ImageCheckActivity.class, bundle, true);
//        }
//        else
//            super.onClick(v);
//
//    }
//
//
//    /**
//     * static api --------------------------------------------------
//     */
//    /**
//     *
//     * @param container
//     * @param context
//     * @param content
//     * @return
//     */
//    public static View addArticleTitle(LinearLayout container,Context context, final String content)
//    {
//        if(TextUtils.isEmpty(content))
//            return null;
//
//        TextView tv = new TextView(context);
//        tv.setText(content);
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
//        tv.setTextColor(context.getResources().getColor(R.color.global_text_gray));
//        ll.leftMargin =  0;
//        ll.rightMargin =  0;
//        ll.topMargin =  DPIUtil.dip2px(3);
//        ll.bottomMargin  = DPIUtil.dip2px(3);
//
//        container.addView(tv,ll);
//        return tv;
//    }
//
//    public static View addArticleAuthor(LinearLayout container,Context context, final String content)
//    {
//        if(TextUtils.isEmpty(content))
//            return null;
//
//        TextView tv = new TextView(context);
//        tv.setText(content);
//        tv.setTextSize(DPIUtil.px2sp(context,DPIUtil.dip2px(20)));
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
//        tv.setTextColor(context.getResources().getColor(R.color.global_nav_green));
//        ll.leftMargin =  0;
//        ll.rightMargin =  0;
//        ll.topMargin =  0;
//        ll.bottomMargin  = DPIUtil.dip2px(8);
//        container.addView(tv,ll);
//        return tv;
//    }
//
//    public static View addArticleContent(LinearLayout container,Context context, final String content)
//    {
//        if(TextUtils.isEmpty(content))
//            return null;
//        TextView tv = new TextView(context);
//        tv.setText(content);
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        tv.setGravity(Gravity.LEFT |Gravity.CENTER_VERTICAL);
//        tv.setLineSpacing(DPIUtil.dip2px(2),1.25f);  //2.5
//        tv.setTextColor(context.getResources().getColor(R.color.global_text_gray));
//        ll.leftMargin =  0;
//        ll.rightMargin =  0;
//        ll.topMargin =  DPIUtil.dip2px(7);
//        ll.bottomMargin  = DPIUtil.dip2px(7);
//
//        container.addView(tv,ll);
//        return tv;
//    }
//
//
//    public static ImageView addArticleImg(LinearLayout container,Context context, int w, int h)
//    {
//        ImageView iv = new ImageView(context);
//        int  nw = container.getWidth() - container.getPaddingLeft()*2;
//        int nh = nw * h / w;
//
//        iv.setBackgroundColor(Color.WHITE);
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
//                nw,nh);
//        ll.leftMargin =  0;
//        ll.rightMargin =  0;
//        ll.topMargin =  DPIUtil.dip2px(10);
//        ll.bottomMargin  = DPIUtil.dip2px(10);
//
//        iv.setLayoutParams(ll);
//        container.addView(iv,ll);
//        return iv;
//    }
//
//}
