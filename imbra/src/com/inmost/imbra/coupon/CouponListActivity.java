package com.inmost.imbra.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.inmost.imbra.R;
import com.inmost.imbra.shopping.AddressAdapter;
import com.inmost.imbra.shopping.AddressEditActivity;
import com.inmost.imbra.shopping.AddressModel;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.UploadPhotoUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-7-10.
 */
public class CouponListActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
        AdapterView.OnItemClickListener {

    public static  final String COUPON_MODEL = "coupon_model";
    private Handler mHandler = new Handler();
    private Intent backIntent;
    private Ajax mAjax;
    private ArrayList<CouponModel>  couponArray;
    private CouponModel             couponPicked;
    private ListView mList;
    private CouponAdapter           couponAdapter;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_list_coupon);

        Intent ait = getIntent();
        if(null==ait)
        {
            finish();
            return;
        }

        Object item = ait.getSerializableExtra(COUPON_MODEL);
        if(null!=item)
            couponPicked = (CouponModel)item;

        loadNavBar(R.id.coupon_list_navigation_bar);

        mList = (ListView)this.findViewById(R.id.coupon_listView);
        couponAdapter = new CouponAdapter(this,-1);
        mList.setAdapter(couponAdapter);
        mList.setOnItemClickListener(this);

        this.findViewById(R.id.coupon_usage).setOnClickListener(this);
        this.findViewById(R.id.submit_coupon).setOnClickListener(this);

        requestCoupon();

        backIntent = new Intent();
    }

    private void requestCoupon()
    {
//        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_ADDRESSLIST);
//        if (null == mAjax)
//            return;
//
//        showLoadingLayer();
//
//        mAjax.setOnSuccessListener(this);
//        mAjax.setOnErrorListener(this);
//        mAjax.send();

        couponArray = new ArrayList<CouponModel>();
        int pick = -1;
        for(int i = 0 ; i< 4 ; i++)
        {
            CouponModel coup = new CouponModel();
            coup.id = "123456789"+i;
            coup.discountNum = 10*i;
            coup.title = "本券仅限手机端购买使用，逾期自动作废";
            coup.expireTime = 0;

            if(null!=couponPicked && couponPicked.id.equals(coup.id) )
                pick = i;
            couponArray.add(coup);


        }
        couponAdapter.setData(couponArray);
        couponAdapter.setPick(pick);
        couponAdapter.notifyDataSetChanged();
//        if(couponArray.size() <=0)
//        {
//            noHintView.setText(R.string.no_coupon_hint);
//            noHintView.setVisibility(View.VISIBLE);
//        }
//        else
//            noHintView.setVisibility(View.GONE);

    }


    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {


    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position %3 == 1)
        {
            UiUtils.makeToast(this,"过期或者无效代金券");
            return;
        }//disable coupon

        couponAdapter.setPick(position);
        couponAdapter.notifyDataSetChanged();

        CouponModel item = couponArray.get(position);
        backIntent.putExtra(COUPON_MODEL,item);
        setResult(RESULT_OK, backIntent);

        delayFinish();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coupon_usage:
                UiUtils.makeToast(this, "Show coupon usage");
                break;
            case R.id.submit_coupon:
                UiUtils.makeToast(this, "Submit coupon code");
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    private void delayFinish()
    {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },200);
    }

}
