package com.inmost.imbra.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.coupon.CouponListActivity;
import com.inmost.imbra.coupon.CouponModel;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.VerifyLoginActivity;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.main.RadioGridAdapter;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductModel;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.RadioDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;


public class ShoppingActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

    public static final int  CODE_SELECT_ADDRESS = 101;
    public static final int  CODE_SELECT_COUPON = 102;

    private ImageLoader mImgLoader;
    private Ajax mAjax;
    private ProductModel proModel;
    private Account account;
//    public MyScrollView mScorllV;
//    public LinearLayout contentLayout;


    private boolean      bToSelf;
    private int          chooseIdx = 0;
    private int          buyNum = 1;
    private static final int MAX_BUY_NUM = 999;

    private AutoHeightImageView proIv;
    private TextView            proTitlev;
    private TextView            proPricev;
    private RadioGroup          shoppingRg;
    private EditText            msg2FriendEdit;

    private GridView          sizeGrid;
    private RadioGridAdapter  sizeAdapter;

    private EditText            numEditv;

    private    AddressModel addressModel;
    public class ReceiverHolder {
        public View hintLayout;
        public View receiverLayout;
        public TextView receiver;
        public TextView address;
        public TextView phone;
    }
    private ReceiverHolder recevierHolder = new ReceiverHolder() ;

    private RadioDialog.RadioAdapter aAdapter;

    private AppDialog     coupOptDialog;
    private CouponModel   couponModel;
    public class CouponHolder {
        public EditText hintTv;
    }
    private CouponHolder coupHolder = new CouponHolder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        account = ILogin.getActiveAccount();
        if(account == null)
        {
            UiUtils.startActivity(this, VerifyLoginActivity.class,true);
            finish();
            return;
        }
        Intent mIntent = getIntent();
        if (mIntent == null) {
            finish();
            return;
        }

        proModel = (ProductModel) mIntent.getSerializableExtra(ProductDetailActivity.PRO_MODEL);
        if(null == proModel)
        {
            finish();
            return;
        }

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        setContentView(R.layout.activity_shopping);

        loadNavBar(R.id.scroll_nav);

        initViews();

    }

    private void initViews()
    {
        proIv = (AutoHeightImageView)this.findViewById(R.id.main_pro_img);
        proIv.setImageUrl(proModel.front,mImgLoader);

        proTitlev = (TextView)this.findViewById(R.id.main_pro_title);
        proTitlev.setText(proModel.title);
        proPricev = (TextView)this.findViewById(R.id.main_pro_price);
        proPricev.setText(getString(R.string.rmb_price,proModel.sale_price));
        findViewById(R.id.main_opt_layout).setVisibility(View.GONE);

        shoppingRg = (RadioGroup)this.findViewById(R.id.shopping_opt_rg);
        shoppingRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.to_other) {
                    bToSelf = false;
                    msg2FriendEdit.setVisibility(View.VISIBLE);
                }
                else {
                    bToSelf = true;
                    msg2FriendEdit.setVisibility(View.GONE);
                }
            }
        });

        msg2FriendEdit =(EditText)this.findViewById(R.id.msg_to_friend);
        shoppingRg.check(R.id.to_self);
        bToSelf = true;

        sizeGrid = (GridView)this.findViewById(R.id.size_grid);
        sizeAdapter = new RadioGridAdapter(this);
        sizeAdapter.setList(proModel.choose,chooseIdx);
        if(proModel.choose.size() > 6)
            sizeGrid.setNumColumns(6);
        else
            sizeGrid.setNumColumns(proModel.choose.size());
        sizeGrid.setAdapter(sizeAdapter);
        sizeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseIdx = position;
                sizeAdapter.setPickIdx(chooseIdx);
                sizeAdapter.notifyDataSetChanged();
            }
        });

        numEditv = (EditText)this.findViewById(R.id.buy_count);
        numEditv.setText("" + buyNum);
        numEditv.addTextChangedListener(new TextWatcher() {
            String lastNum = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastNum = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String numStr = s.toString();
                if(numStr.equalsIgnoreCase(lastNum))
                    return;

                //同步到在线购物车
                if (TextUtils.isEmpty(numStr)) {
                    UiUtils.makeToast(ShoppingActivity.this, "min 1");
                    numEditv.setText("" + buyNum);
                } else {
                    int num = Integer.valueOf(numStr);
                    if(num  < 1 ) {
                        UiUtils.makeToast(ShoppingActivity.this, "min 1");
                        buyNum = 1;
                        numEditv.setText("" + buyNum);
                    }
                    else if(num > MAX_BUY_NUM)
                    {
                        UiUtils.makeToast(ShoppingActivity.this,"max 999");
                        buyNum = MAX_BUY_NUM;
                        numEditv.setText("" + buyNum);
                    }
                    else
                    {
                        buyNum = num;
                        numEditv.setText("" + buyNum);
                    }

                }

            }
        });

        findViewById(R.id.submit_order).setOnClickListener(this);
        this.findViewById(R.id.num_upBtn).setOnClickListener(this);
        this.findViewById(R.id.num_downBtn).setOnClickListener(this);

        /**
         * receiver
         */
        this.findViewById(R.id.receiver_layout).setOnClickListener(this);
        recevierHolder.hintLayout = findViewById(R.id.receiver_hint_tv);
        recevierHolder.receiverLayout = findViewById(R.id.address_info_layout);

        recevierHolder.receiver = (TextView)recevierHolder.receiverLayout.findViewById(R.id.receiver);
        recevierHolder.phone = (TextView)recevierHolder.receiverLayout.findViewById(R.id.phone);
        recevierHolder.address = (TextView)recevierHolder.receiverLayout.findViewById(R.id.address);

        recevierHolder.receiverLayout.setVisibility(View.GONE);

        /**
         * coupon
         */
        this.findViewById(R.id.coupon_layout).setOnClickListener(this);
        coupHolder.hintTv = (EditText)this.findViewById(R.id.coupon_hint_tv);

    }

    private void debugCreateOrder()
    {
        if(null == addressModel)
        {
            UiUtils.makeToast(this,R.string.pls_set_receiver);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("pid:" + proModel.id);
        sb.append(",ty:" + (bToSelf ? 1 : 2));
        sb.append(",qty:" + buyNum);
        sb.append(",size:" + proModel.choose.get(chooseIdx));
        sb.append(",province_id:" + addressModel.provinceId);
        sb.append(",city_id" + addressModel.cityId);
        sb.append(",town_id:" + addressModel.townId);
        sb.append(",addr:" + addressModel.address);
        sb.append(",consignee:" + addressModel.user);
        sb.append(",mob:" + addressModel.phone);
        sb.append(",pty:" + 2);

        if(!bToSelf)
            sb.append(",remark:" + msg2FriendEdit.getText().toString());
        if(couponModel!=null)
            sb.append("cpon:" + couponModel.id);
        sb.append(",token:"+account.token);
        AppDialog bd = UiUtils.showDialog(this,"Buy",sb.toString(),R.string.btn_ok,
                new AppDialog.OnClickListener() {
                    @Override
                    public void onDialogClick(int nButtonId) {
                        createOrder();
                    }
                });
    }

    private void createOrder()
    {
        mAjax = ServiceConfig.getAjax(braConfig.URL_CREATE_ORDER);
        if (null == mAjax)
            return;

        mAjax.setData("pid",proModel.id);
        mAjax.setData("ty",bToSelf ? 1 : 2);
        mAjax.setData("qty",buyNum);
        mAjax.setData("size",proModel.choose.get(chooseIdx));

        //area
        mAjax.setData("province_id",addressModel.provinceId);
        mAjax.setData("city_id",addressModel.cityId);
        mAjax.setData("town_id",addressModel.townId);
        mAjax.setData("addr",addressModel.address);
        mAjax.setData("consignee",addressModel.user);
        mAjax.setData("mob",addressModel.phone);
        mAjax.setData("pty",2); //1-货到付款，2-微信支付，3-支付宝
        if(!bToSelf)
            mAjax.setData("remark",msg2FriendEdit.getText().toString());

        if(couponModel!=null)
            mAjax.setData("cpon",couponModel.id);

        mAjax.setData("token",account.token);

        showLoadingLayer();

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }
//    private void requestData() {
//        mAjax = ServiceConfig.getAjax(braConfig.URL_PRODUCT_DETAIL);
//        if (null == mAjax)
//            return;
//        String url = mAjax.getUrl() + mProId;
//        mAjax.setUrl(url);
//
//        showLoadingLayer();
//
//        mAjax.setOnSuccessListener(this);
//        mAjax.setOnErrorListener(this);
//        mAjax.send();
//    }


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


        JSONObject info = jsonObject.optJSONObject("dt");

        Bundle bundle = new Bundle();
//        curOrder.orderid = info.optString("orderid");
//        curOrder.promodel = proModel;
//        curOrder.payexpire = info.optLong("expire");
//        curOrder.ccode = info.optString("ccode");

        bundle.putString(OrderActivity.ORDER_ID,info.optString("orderid"));
        bundle.putString(OrderActivity.ORDER_PAY_CODE, info.optString("ccode"));
        UiUtils.startActivity(ShoppingActivity.this,OrderActivity.class,bundle,true);
    }





    @Override
    public void onClick(View v)
    {
        Bundle bundle = new Bundle();
        Intent ait;
        switch (v.getId()) {
            case R.id.num_downBtn:
                if(buyNum <= 1 )
                    UiUtils.makeToast(ShoppingActivity.this,"min 1");
                else
                    numEditv.setText(""+(--buyNum));
                break;
            case R.id.num_upBtn:
                if(buyNum + 1 >= MAX_BUY_NUM)
                    UiUtils.makeToast(ShoppingActivity.this,"max 1000");
                else
                    numEditv.setText(""+(++buyNum));
                break;

            case R.id.receiver_layout:
                ait = new Intent(ShoppingActivity.this,AddressListActivity.class);
                if(null!=addressModel)
                    ait.putExtra(AddressEditActivity.ADDRESS_MODEL,addressModel);
                startActivityForResult(ait,CODE_SELECT_ADDRESS);
                break;
            case R.id.coupon_layout:
                if(null==couponModel)
                {
                    ait = new Intent(ShoppingActivity.this,CouponListActivity.class);
                    startActivityForResult(ait,CODE_SELECT_COUPON);
                }
                else
                {
                    if(coupOptDialog==null)
                    {
                        aAdapter = new RadioDialog.RadioAdapter(ShoppingActivity.this);
                        final String[] coupopt = {"不使用代金券","重新选择代金券"};
                        aAdapter.setList(coupopt,-1);
                        coupOptDialog = UiUtils.showListDialog(ShoppingActivity.this,"优惠券方式",aAdapter,
                                new RadioDialog.OnRadioSelectListener(){
                                    @Override
                                    public void onRadioItemClick(int which) {
                                        if(which==0)
                                        {
                                            couponModel = null;
                                            coupHolder.hintTv.setText("");
                                        }
                                        else
                                        {
                                            if(null!=couponModel) {
                                                Intent ait = new Intent(ShoppingActivity.this, CouponListActivity.class);
                                                ait.putExtra(CouponListActivity.COUPON_MODEL, couponModel);
                                                startActivityForResult(ait, CODE_SELECT_COUPON);
                                            }
                                        }

                                    }
                                });
                    }
                    aAdapter.setPickIdx(-1);
                    coupOptDialog.show();
                }
                break;
            case  R.id.submit_order:
                debugCreateOrder();//createOrder();
                break;
            default:
                super.onClick(v);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == CODE_SELECT_ADDRESS && resultCode == RESULT_OK && null!=data)
        {
            addressModel = (AddressModel)data.getSerializableExtra(AddressEditActivity.ADDRESS_MODEL);
            recevierHolder.receiver.setText(addressModel.user);
            recevierHolder.phone.setText(addressModel.phone);
            recevierHolder.address.setText(addressModel.address);
            recevierHolder.hintLayout.setVisibility(View.GONE);
            recevierHolder.receiverLayout.setVisibility(View.VISIBLE);
        }
        else if(requestCode == CODE_SELECT_ADDRESS && resultCode == RESULT_CANCELED && null!=data)
        {
            //del all
            if(0==data.getIntExtra(AddressEditActivity.ADDRESS_NUM,0))
            {
                addressModel = null;
                recevierHolder.hintLayout.setVisibility(View.VISIBLE);
                recevierHolder.receiverLayout.setVisibility(View.GONE);
            }
        }

        else if(requestCode == CODE_SELECT_COUPON && resultCode == RESULT_OK && null!=data )
        {
            couponModel = (CouponModel)data.getSerializableExtra(CouponListActivity.COUPON_MODEL);
            coupHolder.hintTv.setText(getString(R.string.coupon_x,couponModel.coupon_fee));
        }
        else
            super.onActivityResult(requestCode,resultCode,data);
    }

}


