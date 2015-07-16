package com.inmost.imbra.login;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.inmost.imbra.R;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.Product2RowAdapter;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductModel;
import com.inmost.imbra.shopping.AddressListActivity;
import com.inmost.imbra.shopping.OrderActivity;
import com.inmost.imbra.shopping.OrderAdapter;
import com.inmost.imbra.shopping.OrderModel;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.UploadPhotoUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MyInfoActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {

	private Intent  mIntent = null;
    private Ajax    mAjax;
    private PullToRefreshListView pullList;
    private ListView              mListV;
    private View headV;
    private Handler mHandler = new Handler();

    private RadioGroup  tabGroup;
    private NetworkImageView usrImgv;
    private TextView         usrNamev;
    private ImageLoader      mImgLoader;

    private Product2RowAdapter favAdapter;
    private ArrayList<ProductModel> mFavArray;
    private int  mFavNextPageNum;

    private int     mTabRid;

    private OrderAdapter       orderAdapter;
    private ArrayList<OrderModel> mOrderArray;
    private int  mOrderNextPageNum;

    private OrderAdapter       couponAdapter;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my);


        mIntent = this.getIntent();
        if(null == mIntent)
        {
            finish();
            return;
        }

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        // 初始化布局元素
        initViews();

        Account act = ILogin.getActiveAccount();

//        UiUtils.hideSoftInput(this, mPhone);

		if(null!=act)
		{
//			mHandler.sendEmptyMessageDelayed(FINISH_LOGIN, 1500);
		}

        mTabRid = -1;
        tabGroup.check(R.id.tab_favor);

	}

	private void initViews() {
        loadNavBar(R.id.my_nav);

        mNavBar.setRightInfo(R.string.manager_address,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.startActivity(MyInfoActivity.this, AddressListActivity.class,true);
            }
        });
        TextView nrtv = (TextView) mNavBar.findViewById(R.id.navigationbar_right_text);
        nrtv.setTextColor(getResources().getColor(R.color.global_pink));

        pullList = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
//        pullList.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                if(mTabIdx == TAB_HOME) {
//                    if (firstVisibleItem + visibleItemCount >= totalItemCount && mFloorNextPageNum>1) {
//                        UiUtils.makeToast(mActivity, "first:" + firstVisibleItem + ",vis:" +
//                                visibleItemCount + ",totalItemCount" + totalItemCount);
//                        requestPage(mFloorNextPageNum);
//                    }
//                }
//                else {
//                    if (firstVisibleItem + visibleItemCount >= totalItemCount && mProNextPageNum > 1) {
//                        UiUtils.makeToast(mActivity, "first:" + firstVisibleItem + ",vis:" +
//                                visibleItemCount + ",totalItemCount" + totalItemCount);
//                        requestPage(mProNextPageNum);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//        });

        pullList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>(){

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if(mTabRid == R.id.tab_favor) {
                    mFavArray.clear();
                    mFavNextPageNum = 1;
                    requestFav(mFavNextPageNum);
                }
                else {
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullList.onRefreshComplete();
                    }},5000);
            }
        });

        mListV = pullList.getRefreshableView();
        headV = getLayoutInflater().inflate(R.layout.myinfo_head_pg,null);
        mListV.setOnItemClickListener(this);
        usrImgv = (NetworkImageView)headV.findViewById(R.id.user_img);
        usrImgv.setImageUrl("http://img2.imgtn.bdimg.com/it/u=921607941,1665261509&fm=21&gp=0.jpg",mImgLoader);
        usrImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPhotoUtil.createUploadPhotoDlg(MyInfoActivity.this).show();
            }
        });
        usrNamev = (TextView)headV.findViewById(R.id.user_name);
        usrNamev.setText("King");

        tabGroup = (RadioGroup)headV.findViewById(R.id.my_tab_rg);
        tabGroup.setOnCheckedChangeListener(this);

        mListV.addHeaderView(headV);



	}

    private void requestFav(int page) {
        mAjax = ServiceConfig.getAjax(braConfig.URL_SEARCH);
        if (null == mAjax)
            return;

        mAjax.setData("page", page);
        showLoadingLayer();
        mAjax.setId(R.id.tab_favor);
        mAjax.setOnSuccessListener(this);
        mAjax.send();
    }

    private void requestOrderlist(int page) {
        mAjax = ServiceConfig.getAjax(braConfig.URL_SEARCH);
        if (null == mAjax)
            return;

        mAjax.setData("page", page);
        showLoadingLayer();
        mAjax.setId(R.id.tab_orderlist);
        mAjax.setOnSuccessListener(this);
        mAjax.send();
    }

	@Override
	public void onClick(View v)
	{
//        switch (v.getId())
//        {
//        }
	}

	private void requestLogin() {
//
//		if(!ToolUtil.isPhoneNum(phoneNum))
//		{
//			UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
//			return;
//		}
//		else if(ToolUtil.isEmpty(pswd))
//		{
//			UiUtils.makeToast(this, R.string.pswd_not_empty);
//			return;
//		}

		Ajax ajax = ServiceConfig.getAjax(braConfig.URL_LOGIN);
		if (null == ajax)
			return;

	}

	@Override
	public void onSuccess(JSONObject v, Response response) {
        pullList.onRefreshComplete();
        closeLoadingLayer();
        if(response.getId()==R.id.tab_favor) {

            mFavNextPageNum++;
            JSONArray feeds = v.optJSONArray("products");
            if (null != feeds) {
                for (int i = 0; i < feeds.length() - 1; i++) {
                    ProductModel pro = new ProductModel();
                    pro.parse(feeds.optJSONObject(i));
                    mFavArray.add(pro);
                }
            }
            favAdapter.setData(mFavArray);
            favAdapter.notifyDataSetChanged();
        }
        else if(response.getId() == R.id.tab_orderlist)
        {
            mOrderNextPageNum++;
            JSONArray feeds = v.optJSONArray("products");
            if (null != feeds) {
                for (int i = 0; i < feeds.length() - 1; i++) {
                    OrderModel order = new OrderModel();
                    order.orderid = ""+i;
                    order.parse(feeds.optJSONObject(i));
                    mOrderArray.add(order);
                }
            }
            orderAdapter.setData(mOrderArray);
            orderAdapter.notifyDataSetChanged();
        }

	}


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(mTabRid == checkedId)
            return;
        mTabRid = checkedId;
        if (checkedId == R.id.tab_favor)
        {
            if(favAdapter == null) {
                mFavNextPageNum = 1;
                favAdapter = new Product2RowAdapter(this, mImgLoader);
                requestFav(mFavNextPageNum);
                mFavArray = new ArrayList<ProductModel>();
            }
            pullList.setAdapter(favAdapter);
        }
        else if(checkedId == R.id.tab_coupon)
        {
            pullList.setAdapter(favAdapter);
        }
        else if(checkedId == R.id.tab_orderlist)
        {
            if(null==orderAdapter)
            {
                mOrderNextPageNum = 1;

                orderAdapter = new OrderAdapter(this,mImgLoader,new OrderAdapter.OrderClickListener() {
                    @Override
                    public void onProClick(int pos) {
                        OrderModel order =  mOrderArray.get(pos);
                        Bundle bundle = new Bundle();
                        bundle.putString(ProductDetailActivity.PRO_ID, order.promodel.id);
                        UiUtils.startActivity(MyInfoActivity.this, ProductDetailActivity.class, bundle, true);
                    }

                    @Override
                    public void onOrderDel(int pos) {
                        UiUtils.makeToast(MyInfoActivity.this,"orderdel:" + pos);
                    }

                });
                mOrderArray = new ArrayList<OrderModel>();
                requestOrderlist(mOrderNextPageNum);

            }
            pullList.setAdapter(orderAdapter);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mTabRid == R.id.tab_orderlist) {
            OrderModel order = mOrderArray.get(position - 2);
            Bundle bundle = new Bundle();
            bundle.putSerializable(OrderActivity.ORDER_MODEL,order);
            bundle.putInt(OrderActivity.ORDER_STATUS,order.status);
            UiUtils.startActivity(MyInfoActivity.this, OrderActivity.class, bundle, true);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == UploadPhotoUtil.PHOTO_PICKED_WITH_DATA && null != data)
        {
            // 雷军个傻逼
//            mIsProcessing = true;
            ToolUtil.showClipIntentWithData(this, data.getData());
        }
        else if (requestCode == UploadPhotoUtil.CAMERA_WITH_DATA)
        {
//            mIsProcessing = true;
            Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String path = UploadPhotoUtil.getImgPath(this, requestCode, resultCode, data);
            if(ToolUtil.isEmpty(path))
            {
                UiUtils.makeToast(MyInfoActivity.this, "照片路径获取失败");
                return;
            }

            File file = new File(path);
            Uri fileUri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    fileUri));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Cursor cursor = getContentResolver().query(imgUri, null,
                    MediaStore.Images.Media.DISPLAY_NAME + "='"
                            + file.getName() + "'",
                    null, null);
            Uri uri = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToLast();
                long id = cursor.getLong(0);
                uri = ContentUris.withAppendedId(imgUri, id);
            }
            if(null!=cursor && !cursor.isClosed())
                cursor.close();
            ToolUtil.showClipIntentWithData(this,uri);
        }
        else if(requestCode == ToolUtil.GO_CROP_ACTIVITY)
        {
            //final AutoHeightImageView curImg = (AutoHeightImageView) mPicImages.get(mCurPicIdx);
            //String localPath = curImg.mCustomInfo.get("localPath");

            final Bitmap bitmap =  (null == data) ? null : (Bitmap)data.getParcelableExtra("data");
            usrImgv.setImageBitmap(bitmap);
            if (null != bitmap) {
                ByteArrayOutputStream  stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

//				File file = new File(localPath);
//			    FileOutputStream fOut = new FileOutputStream(file);
//				bitmap.compress(CompressFormat.JPEG, 100, fOut);
//				fOut.flush();
//				fOut.close();

//                Ajax ajax = ServiceConfig.getAjax(Config.URL_IMAGE_STREAM_UPLOAD,
//                        (mUploadPhotoSrc == UPLOADPHOTO_SNAPSHOT ?  PARAM_UPLOAD_SNAPSHOT : PARAM_UPLOAD_COVER));
//                if( null != ajax ) {
//                    showLoadingLayer();
//                    ajax.setData("act_id",this.mEventId);
//                    ajax.setData("uid",mEvent.sponsorInfo.mUid);
//                    ajax.setFile("actimg", byteArray);
//                    ajax.setOnSuccessListener(this);
//                    ajax.setId(AJAX_UPLOAD_SNAPSHOT);
//                    ajax.setOnErrorListener(this);
//                    addAjax(ajax);
//                    ajax.send();
//                }
            }

//            mIsProcessing = false;
        }
    }
}
