package com.inmost.imbra.shopping;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;


public class OrderActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

    public static final String    ORDER_ID = "order_id";

    public static final String    ORDER_MODEL = "order_model";

    public static int             AJAX_FETCH_ORDERINFO = 1232;
    public static int             AJAX_CANCEL_ORDER = 1233;
    public static int             AJAX_SUBMIT_ORDER = 1234;


    private Dialog    cancelDialog;

    private ImageLoader mImgLoader;
    private String     mOrderId;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mIntent = getIntent();
        if (mIntent == null) {
            finish();
            return;
        }

        mOrderId = mIntent.getStringExtra(ORDER_ID);
        Object obj  = mIntent.getSerializableExtra(ORDER_MODEL);
        if(null == obj || TextUtils.isEmpty(mOrderId))
        {
            finish();
            return;
        }

        if(obj!=null) {
            orderModel = (OrderModel) obj;
            mOrderId = orderModel.orderid;
        }
        else
        {
            requestOrderInfo();
        }

        /**
         * this will not get this way.WIll FROM WEB
         */
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        setContentView(R.layout.activity_orderdetail);


        initViews();

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

        if(orderModel.status == OrderModel.STATE_WAITTING_PAY) {
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

        orderIdv.setText(orderModel.orderid);
        String hintQStr = getResources().getString(R.string.pay_hint_quick);
        SpannableStringBuilder style=new SpannableStringBuilder(hintQStr);

        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.global_pink)),2,4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        payHintTv.setText(style);

        if(orderModel.status == OrderModel.STATE_WAITTING_PAY ||
                orderModel.status == OrderModel.STATUS_PAYFAIL) {
            findViewById(R.id.submit_pay).setVisibility(View.VISIBLE);
            findViewById(R.id.submit_pay).setOnClickListener(this);
        }
        else
            findViewById(R.id.submit_pay).setVisibility(View.GONE);


        if(null!=orderModel)
            proIv.setImageUrl(HomeFloorModel.formBraUrl(orderModel.promodel.front),mImgLoader);
    }

    private void requestOrderInfo()
    {
        mAjax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);
        if (null == mAjax)
            return;
        showLoadingLayer();

        mAjax.setId(AJAX_FETCH_ORDERINFO);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }
    private void submitOrder() {
        mAjax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);
        if (null == mAjax)
            return;
        showLoadingLayer();

        mAjax.setId(AJAX_SUBMIT_ORDER);
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
        if(AJAX_FETCH_ORDERINFO == response.getId())
        {
            orderModel = new OrderModel();
            orderModel.parse(jsonObject);
            refreshOrderView();
        }
        if(AJAX_CANCEL_ORDER == response.getId())
        {
            finish();
        }
        else{
            finish();
            Bundle bundle = new Bundle();
            bundle.putString(AfterPayActivity.ORDER_ID, "121212");
            UiUtils.startActivity(OrderActivity.this, AfterPayActivity.class, bundle, true);
        }
    }





    @Override
    public void onClick(View v)
    {
        Bundle bundle = new Bundle();
        if(v.getId() == R.id.submit_pay)
        {
            submitOrder();
        }
        else if(v.getId() ==R.id.pro_img)
        {
            if(null!=orderModel) {
                bundle.putString(ProductDetailActivity.PRO_ID, orderModel.promodel.id);
                UiUtils.startActivity(OrderActivity.this, ProductDetailActivity.class, bundle, true);
            }
        }
        else
        {
            super.onClick(v);
        }
    }


}


