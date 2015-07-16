package com.inmost.imbra.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.inmost.imbra.R;
import com.inmost.imbra.imgallery.ImageCheckActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.IArea;
import com.xingy.lib.IPageCache;
import com.xingy.lib.model.ChangeDistrictModel;
import com.xingy.lib.model.FullDistrictModel;
import com.xingy.lib.ui.AreaPickerView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-7-10.
 */
public class AddressEditActivity extends BaseActivity {

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
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_edit_address);

        loadNavBar(R.id.edit_add_nav);

        Intent ait = getIntent();
        if(null == ait)
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
        }

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


    @Override
    public void onClick(View v)
    {
        switch(v.getId()) {
            case R.id.area:
                mAreaPicker.setVisibility(View.VISIBLE);
                if(null == mAreaPickerListener)
                {
                    mAreaPickerListener = new AreaPickerView.OnPickerListener(){

                        @Override
                        public void onSubmit() {
                            String province_name = mAreaPicker.getProvince().getProvinceName();
                            String city_name = mAreaPicker.getCity().getCityName();
                            String district_name = (null == mAreaPicker.getZone()  ? "" : mAreaPicker.getZone().getZoneName());

                            areaEt.setText(province_name + " " + city_name + " " + district_name);
                            mAreaPicker.setVisibility(View.INVISIBLE);
                        }};
                }
                mAreaPicker.setListener(mAreaPickerListener);
                break;
            case R.id.submit_address:
                setResult(RESULT_OK,null);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }



}
