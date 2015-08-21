package com.inmost.imbra.shopping;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.imbra.util.braConfig;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;


public class OrderActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

    public static final String    ORDER_ID = "order_id";
    public static final String    ORDER_PAY_CODE = "ORDER_PAY_CODE";

    public static int             AJAX_FETCH_ORDERINFO = 1232;
    public static int             AJAX_CANCEL_ORDER = 1233;
    public static int             AJAX_PREPAY_ORDER = 1234;


    private Dialog    cancelDialog;

    private ImageLoader mImgLoader;
    private String     mOrderId;
    private String     mCcode;
    private Ajax mAjax;
    private OrderModel orderModel;

    private NetworkImageView proIv;
    private TextView            orderStatusv;
    private TextView            orderIdv;
    private TextView            orderInfov;

    private TextView            toWhomv;
    private TextView            sizeV;
    private TextView            numV;

    private TextView            payMethodv;
    public class ReceiverHolder {
        public View receiverLayout;
        public TextView receiver;
        public TextView address;
        public TextView phone;
    }
    private ReceiverHolder recevierHolder = new ReceiverHolder() ;

    private ImageView    payHintIv;
    private TextView     payHintTv;

    private Account account;
    public class WXPayResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int aErrcode = intent.getIntExtra("errcode",-1);
            WeixinUtil.informWXPayResult(context,aErrcode);
            if(aErrcode == BaseResp.ErrCode.ERR_OK)
            {
                Bundle bundle = new Bundle();
                bundle.putString(AfterPayActivity.ORDER_ID,mOrderId);
                UiUtils.startActivity(OrderActivity.this, AfterPayActivity.class, bundle,true);
                finish();
            }
        }
    }

    private WXPayResponseReceiver wxPayReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mIntent = getIntent();
        if (mIntent == null) {
            finish();
            return;
        }

        account = ILogin.getActiveAccount();
        mOrderId = mIntent.getStringExtra(ORDER_ID);
        mCcode = mIntent.getStringExtra(ORDER_PAY_CODE);
        if(TextUtils.isEmpty(mOrderId) || account==null)
        {
            finish();
            return;
        }

        requestOrderInfo();


        /**
         * this will not get this way.WIll FROM WEB
         */
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        setContentView(R.layout.activity_orderdetail);

        initViews();

        if(null==wxPayReceiver) {
            wxPayReceiver = new WXPayResponseReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WeixinUtil.BROADCAST_FROM_WXPAY);
            LocalBroadcastManager.getInstance(this).registerReceiver(wxPayReceiver, filter);
        }
    }

    @Override
    protected void onDestroy()
    {
        if(null != wxPayReceiver)
        {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(wxPayReceiver);
            wxPayReceiver = null;
        }
        super.onDestroy();
    }


    private void initViews() {
        loadNavBar(R.id.order_nav);

        proIv = (NetworkImageView) this.findViewById(R.id.pro_img);
        proIv.setOnClickListener(this);
        orderStatusv = (TextView) findViewById(R.id.order_status);
        orderIdv = (TextView) findViewById(R.id.order_id);

        orderInfov = (TextView) findViewById(R.id.order_info);

        toWhomv = (TextView) findViewById(R.id.to_whom);
        sizeV = (TextView) findViewById(R.id.size_x);
        numV = (TextView) findViewById(R.id.num_x);

        recevierHolder.receiverLayout = findViewById(R.id.order_address);
        recevierHolder.receiver = (TextView) recevierHolder.receiverLayout.findViewById(R.id.receiver);
        recevierHolder.phone = (TextView) recevierHolder.receiverLayout.findViewById(R.id.phone);
        recevierHolder.address = (TextView) recevierHolder.receiverLayout.findViewById(R.id.address);

        payMethodv = (TextView) findViewById(R.id.pay_method);

        payHintIv = (ImageView) findViewById(R.id.pay_hint_img);
        payHintTv = (TextView) findViewById(R.id.pay_hint_txt);

        refreshOrderView();
    }

    private void refreshOrderView()
    {
        if(orderModel==null)
            return;

        if(orderModel.order_stat < OrderModel.ORDER_STAT_DELIVER) {
            mNavBar.setRightInfo(R.string.cancel_order, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null==cancelDialog) {
                        cancelDialog = UiUtils.showDialog(OrderActivity.this, "取消订单", "您确认取消么",
                                com.xingy.R.string.btn_ok, com.xingy.R.string.btn_cancel,
                                new AppDialog.OnClickListener() {
                                    @Override
                                    public void onDialogClick(int nButtonId) {
                                        if (nButtonId == AppDialog.BUTTON_POSITIVE) {
                                            cancelOrder();
                                        }
                                    }
                                });
                    }
                    cancelDialog.setCancelable(false);
                    cancelDialog.show();
                }
            });
            TextView navRightView = (TextView) mNavBar.findViewById(R.id.navigationbar_right_text);
            navRightView.setTextColor(getResources().getColor(R.color.global_pink));
        }

        orderIdv.setText(getString(R.string.order_x,orderModel.orderid));
        orderInfov.setText(getString(R.string.fee_x,orderModel.total_price));
        toWhomv.setText(orderModel.toSelf ? R.string.buy_self : R.string.buy_friend);
        sizeV.setText(getString(R.string.size_x,orderModel.proList.get(0).buy_size));
        numV.setText(getString(R.string.num_x,orderModel.proList.get(0).buy_qty));


        recevierHolder.receiver.setText(orderModel.usrmodel.user);
        recevierHolder.phone.setText(orderModel.usrmodel.phone);
        recevierHolder.address.setText(orderModel.usrmodel.address);



        if(orderModel.pay_stat == OrderModel.PAY_STAT_WAITING){
            findViewById(R.id.submit_pay).setVisibility(View.VISIBLE);
            findViewById(R.id.submit_pay).setOnClickListener(this);
            String hintQStr = getResources().getString(R.string.pay_hint_quick);
            SpannableStringBuilder style = new SpannableStringBuilder(hintQStr);

            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.global_pink)), 2, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            payHintTv.setText(style);
        }
        else
            findViewById(R.id.submit_pay).setVisibility(View.GONE);


        if(null!=orderModel)
            proIv.setImageUrl(HomeFloorModel.formBraUrl(orderModel.proList.get(0).front),mImgLoader);
    }

    private void requestOrderInfo()
    {
        mAjax = ServiceConfig.getAjax(braConfig.URL_ORDER_DETAIL);
        if (null == mAjax)
            return;
        showLoadingLayer();

        mAjax.setData("order_id",mOrderId);
        mAjax.setData("token",account.token);

        mAjax.setId(AJAX_FETCH_ORDERINFO);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }
    private void payOrder() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_PAY_ORDER);
        if (null == mAjax)
            return;
        showLoadingLayer();

        mAjax.setData("ccode",mCcode);
        mAjax.setData("token",account.token);
        mAjax.setId(AJAX_PREPAY_ORDER);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void cancelOrder() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);
        if (null == mAjax)
            return;
        showLoadingLayer();

        mAjax.setId(AJAX_CANCEL_ORDER);
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
            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg) : msg);
            return;
        }
        if(AJAX_FETCH_ORDERINFO == response.getId())
        {
            orderModel = new OrderModel();
            orderModel.parse(jsonObject.optJSONObject("dt"));
            refreshOrderView();
            return;
        }
        else if(AJAX_PREPAY_ORDER == response.getId())
        {
            boolean supp = WeixinUtil.isWXSupportPay(this);
            if(!supp)
            {
                UiUtils.makeToast(this,"not support weixinpay");
                return;
            }
            showLoadingLayer();
            WeixinUtil.doWXPay(OrderActivity.this, jsonObject.optJSONObject("dt"));
        }
        if(AJAX_CANCEL_ORDER == response.getId())
        {
            finish();
            return;
        }
//        else{
//            finish();
//            Bundle bundle = new Bundle();
//            bundle.putString(AfterPayActivity.ORDER_ID, "121212");
//            UiUtils.startActivity(OrderActivity.this, AfterPayActivity.class, bundle, true);
//        }
    }





    @Override
    public void onClick(View v)
    {
        Bundle bundle = new Bundle();
        if(v.getId() == R.id.submit_pay)
        {
            payOrder();
        }
        else if(v.getId() ==R.id.pro_img)
        {
            if(null!=orderModel) {
                bundle.putString(ProductDetailActivity.PRO_ID, orderModel.proList.get(0).id);
                UiUtils.startActivity(OrderActivity.this, ProductDetailActivity.class, bundle, true);
            }
        }
        else
        {
            super.onClick(v);
        }
    }


}


