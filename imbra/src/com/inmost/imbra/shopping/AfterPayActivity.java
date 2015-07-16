package com.inmost.imbra.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inmost.imbra.R;
import com.inmost.imbra.main.MainActivity;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;


public class AfterPayActivity extends BaseActivity{

    public static final String    ORDER_ID = "order_id";
    private String     mOrderId;

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
        if(TextUtils.isEmpty(mOrderId))
        {
            finish();
            return;
        }

        setContentView(R.layout.activity_afterpay);

        initViews();

    }

    private void initViews() {
        loadNavBar(R.id.afterpay_nav);

        payHintTv = (TextView) findViewById(R.id.pay_hint_txt);
        payHintIv = (ImageView) findViewById(R.id.pay_hint_img);

        findViewById(R.id.go_home).setOnClickListener(this);
        findViewById(R.id.go_orderdetail).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Bundle bundle = new Bundle();
        if(v.getId() == R.id.go_home)
        {
            UiUtils.startActivity(AfterPayActivity.this, MainActivity.class,true);
        }
        else if(v.getId() ==R.id.go_orderdetail)
        {
            bundle.putString(OrderActivity.ORDER_ID,mOrderId);
            bundle.putInt(OrderActivity.ORDER_STATUS,OrderActivity.STATUS_PAYED);
            UiUtils.startActivity(AfterPayActivity.this, OrderActivity.class,bundle,true);
            finish();
        }
        else
        {
            super.onClick(v);
        }
    }


}


