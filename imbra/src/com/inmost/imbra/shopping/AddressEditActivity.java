package com.inmost.imbra.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.inmost.imbra.R;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AreaPickerView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-7-10.
 */
public class AddressEditActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

    private boolean  bAddFlag = true;
    private Ajax mAjax;
    public static final String ADDRESS_MODEL = "address_model";

    private AreaPickerView mAreaPicker;
    private AreaPickerView.OnPickerListener mAreaPickerListener;

    private EditText  nameEt;
    private EditText  phoneEt;
    private EditText  areaEt;
    private EditText  areaCodeEt;
    private EditText  addressEt;
    private AddressModel addressModel;

    private Account  act;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_edit_address);

        loadNavBar(R.id.edit_add_nav);

        act = ILogin.getActiveAccount();
        Intent ait = getIntent();
        if(null == ait || null == act)
        {
            finish();
            return;
        }

        mAreaPicker = (AreaPickerView)findViewById(R.id.area_picker);
        mAreaPicker.setVisibility(View.INVISIBLE);

        nameEt = (EditText)findViewById(R.id.name);
        phoneEt = (EditText)findViewById(R.id.phone);
        addressEt = (EditText)findViewById(R.id.address_detail);
        areaCodeEt = (EditText)findViewById(R.id.area_code);

        areaEt = (EditText)findViewById(R.id.area);
        areaEt.setOnClickListener(this);

        this.findViewById(R.id.submit_address).setOnClickListener(this);

        Object item = ait.getSerializableExtra(AddressEditActivity.ADDRESS_MODEL);
        if(null!=item) {
            addressModel = (AddressModel) item;
            nameEt.setText(addressModel.user);
            phoneEt.setText(addressModel.phone);
            addressEt.setText(addressModel.address);
            areaEt.setText(addressModel.cityStr);
            bAddFlag = false;
        }
        else
            addressModel = new AddressModel();

        setResult(RESULT_CANCELED,null);


    }

    /*
     * 拉取三级地址信息
     */
//    private void initAreaInfo(){
//        IPageCache cache = new IPageCache();
//        String strMD5 = cache.get(braConfig.CACHE_FULL_DISTRICT_MD5);
//
//        mAjax = ServiceConfig.getAjax(braConfig.URL_FULL_DISTRICT);
//        if( null == mAjax )
//            return ;
//
//        final FullDistrictParser mFullDistrictParser = new ChangeDistrictModel();
//        if(!TextUtils.isEmpty(strMD5)) {
//            mAjax.setData("fileMD5", strMD5);
//        }
//
//        mAjax.setParser(mFullDistrictParser);
//        mAjax.setOnSuccessListener(new OnSuccessListener<ChangeDistrictModel>(){
//            @Override
//            public void onSuccess(ChangeDistrictModel v, Response response) {
//                if(mFullDistrictParser.isSuccess()) {
//                    IPageCache cache = new IPageCache();
//                    cache.set(braConfig.CACHE_FULL_DISTRICT, mFullDistrictParser.getData(), 0);
//                    cache.set(CacheKeyFactory.CACHE_FULL_DISTRICT_MD5, v.getMD5Value(), 0);
//                    IArea.setAreaModel(v.getProvinceModels());
//                }
//            }
//        });
//
//        addAjax(mAjax);
//        mAjax.send();
//    }

    public void submitEditAddress()
    {
        addressModel.postcode = areaCodeEt.getText().toString();
        addressModel.user = nameEt.getText().toString();
        addressModel.phone = phoneEt.getText().toString();
        addressModel.address = addressEt.getText().toString();

        if(bAddFlag)
            mAjax = ServiceConfig.getAjax(braConfig.URL_ADD_ADDRESS);
        else
            mAjax = ServiceConfig.getAjax(braConfig.URL_EDIT_ADDRESS);
        if (null == mAjax)
            return;


        if(!bAddFlag)
            mAjax.setData("uaid",addressModel.addid);
        mAjax.setData("province_id",addressModel.provinceId);
        mAjax.setData("city_id",addressModel.cityId);
        mAjax.setData("town_id",addressModel.townId);
        mAjax.setData("addr",addressModel.address);
        mAjax.setData("postcode",addressModel.postcode);
        mAjax.setData("consignee",addressModel.user);
        mAjax.setData("mobile",addressModel.phone);
        mAjax.setData("token",act.token);


        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()) {
            case R.id.area:
                UiUtils.hideSoftInput(AddressEditActivity.this,areaEt);
                mAreaPicker.setVisibility(View.VISIBLE);
                if(null == mAreaPickerListener)
                {
                    mAreaPickerListener = new AreaPickerView.OnPickerListener(){

                        @Override
                        public void onSubmit() {
                            addressModel.provinceId = mAreaPicker.getProvince().getProvinceId();

                            addressModel.cityId = mAreaPicker.getCity().getCityId();
                            addressModel.provinceStr = mAreaPicker.getProvince().getProvinceName();
                            addressModel.cityStr = mAreaPicker.getCity().getCityName();
                            addressModel.townStr = (null == mAreaPicker.getZone()  ? "" : mAreaPicker.getZone().getZoneName());
                            addressModel.townId = (null == mAreaPicker.getZone()  ? 0 : mAreaPicker.getZone().getZoneId());

                            areaEt.setText(addressModel.provinceStr + " " + addressModel.cityStr + " " + addressModel.townStr);
                            mAreaPicker.setVisibility(View.INVISIBLE);
                        }};
                }
                mAreaPicker.setListener(mAreaPickerListener);
                break;
            case R.id.submit_address:
                submitEditAddress();
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        int err = jsonObject.optInt("err");
        if (err != 0) {
            String msg = jsonObject.optString("msg");
            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg) : msg);
            return;
        }

        setResult(RESULT_OK,null);
        finish();
   }

    @Override
    public void onBackPressed()
    {
        if(mAreaPicker!=null && mAreaPicker.getVisibility() == View.VISIBLE)
        {
            mAreaPicker.setVisibility(View.GONE);
        }
        else
            super.onBackPressed();
    }
}
