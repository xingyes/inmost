package com.inmost.imbra.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.inmost.imbra.R;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.VerifyLoginActivity;
import com.inmost.imbra.shopping.AddressAdapter;
import com.inmost.imbra.shopping.AddressEditActivity;
import com.inmost.imbra.shopping.AddressModel;
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

import java.util.ArrayList;

/**
 * Created by xingyao on 15-7-10.
 */
public class CouponListActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
        AdapterView.OnItemClickListener {

    public static int AJAX_GET_COUPON =  12330;
    public static int AJAX_COUPON_LIST =  12331;

    public static final String COUPON_MODEL = "coupon_model";
    private Handler mHandler = new Handler();
    private Intent backIntent;
    private Ajax mAjax;
    private ArrayList<CouponModel> couponArray;
    private CouponModel couponPicked;
    private ListView mList;
    private CouponAdapter couponAdapter;
    private TextView noHintView;
    private Account act;


    private EditText  codeEdit;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_list_coupon);
        act = ILogin.getActiveAccount();
        if (act == null) {
            UiUtils.startActivity(this, VerifyLoginActivity.class, true);
            finish();
            return;
        }
        Intent ait = getIntent();
        if (null == ait) {
            finish();
            return;
        }

        couponArray = new ArrayList<CouponModel>();
        Object item = ait.getSerializableExtra(COUPON_MODEL);
        if (null != item)
            couponPicked = (CouponModel) item;

        loadNavBar(R.id.coupon_list_navigation_bar);

        mList = (ListView) this.findViewById(R.id.coupon_listView);
        codeEdit = (EditText)this.findViewById(R.id.input_coupon_code);
        couponAdapter = new CouponAdapter(this, -1);
        mList.setAdapter(couponAdapter);
        mList.setOnItemClickListener(this);
        noHintView = (TextView) findViewById(R.id.no_hint);
        noHintView.setVisibility(View.GONE);
        this.findViewById(R.id.coupon_usage).setOnClickListener(this);
        this.findViewById(R.id.submit_coupon).setOnClickListener(this);

        requestCoupon();

        backIntent = new Intent();
    }

    private void requestCoupon() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_COUPON_LIST);
        if (null == mAjax)
            return;

        showLoadingLayer();
        mAjax.setId(AJAX_COUPON_LIST);
        mAjax.setData("token", act.token);
        mAjax.setOnSuccessListener(this);
        mAjax.send();
    }

    private void getCoupon()
    {
        String coup = codeEdit.getText().toString();
        if(TextUtils.isEmpty(coup))
        {
            UiUtils.makeToast(this,R.string.no_empty_allowed);
            return;
        }
        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_COUPON);
        if (null == mAjax)
            return;


        showLoadingLayer();
        mAjax.setId(AJAX_GET_COUPON);

        mAjax.setData("cpon",codeEdit.getText().toString());
        mAjax.setData("token",act.token);

        mAjax.setOnSuccessListener(this);
        mAjax.send();
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {

        closeLoadingLayer();
        int err = jsonObject.optInt("err");
        if (err != 0) {
            String msg = jsonObject.optString("msg");
            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg) : msg);
            return;
        }

        if(response.getId() == AJAX_GET_COUPON)
        {
            UiUtils.makeToast(CouponListActivity.this,R.string.submit_succ);
            couponArray.clear();
            couponAdapter.notifyDataSetChanged();

            requestCoupon();
            return;
        }
//        if(response.getId() ==)
        JSONArray dt = jsonObject.optJSONArray("dt");
        if (null != dt && dt.length() > 0) {
            for (int i = 0; i < dt.length(); i++) {
                CouponModel coup = new CouponModel();
                coup.parse(dt.optJSONObject(i));
//                    coup.id = ""+i;
//                    coup.discountNum = 10*i;
//                    coup.title = "本券仅限手机端购买使用，逾期自动作废";
//                    coup.expireTime = 0;
                if (null != couponPicked && couponPicked.id.equals(coup.id))
                    couponAdapter.setPick(i);
                couponArray.add(coup);
            }
        }
        couponAdapter.setData(couponArray);
        couponAdapter.notifyDataSetChanged();
        if (couponArray.size() <= 0) {
            noHintView.setText(R.string.no_coupon_hint);
            noHintView.setVisibility(View.VISIBLE);
        } else
            noHintView.setVisibility(View.GONE);
    }










    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                getCoupon();
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
