package com.inmost.imbra.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.inmost.imbra.R;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-7-10.
 */
public class AddressListActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
        AddressAdapter.AddressEditListener, AdapterView.OnItemClickListener {

    public static final int    ADDRESS_EDIT = 1003;
    public static final int    ADDRESS_ADD = 1004;

    private Handler mHandler = new Handler();
    private Intent backIntent;
    private Ajax mAjax;
    private ArrayList<AddressModel> addressArray;
    private AddressModel            addPicked;
    private ListView mList;
    private AddressAdapter addressAdapter;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_list_address);

        Intent ait = getIntent();
        if(null==ait)
        {
            finish();
            return;
        }

        Object item = ait.getSerializableExtra(AddressEditActivity.ADDRESS_MODEL);
        if(null!=item)
            addPicked = (AddressModel)item;

        loadNavBar(R.id.address_list_navigation_bar);

        mList = (ListView)this.findViewById(R.id.address_list_listView);
        addressAdapter = new AddressAdapter(this,-1,this);
        mList.setAdapter(addressAdapter);
        mList.setOnItemClickListener(this);

        findViewById(R.id.add_btn).setOnClickListener(this);
        requestAddress();

        backIntent = new Intent();
    }

    private void requestAddress()
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

        addressArray = new ArrayList<AddressModel>();
        for(int i = 0 ; i< 4 ; i++)
        {
            AddressModel item = new AddressModel();
            item.provinceId = 10 + i;
            item.cityId = 50 + i*2;
            item.townId = 23 + i;
            item.address = "淞虹路1111号" + i + "楼";
            item.user = "张三" + i;
            item.phone = "1394040123" + i;
            item.cityStr = "上海市徐汇区";
            item.addid = ""+i;
            addressArray.add(item);

            if(null!=addPicked && item.addid.equals(addPicked.addid))
                addressAdapter.setPick(i);
        }

        addressAdapter.setData(addressArray);
        addressAdapter.notifyDataSetChanged();

    }


    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {


    }

    @Override
    public void onEditAddress(int pos) {
        AddressModel item = addressArray.get(pos);
        Intent it = new Intent(AddressListActivity.this,AddressEditActivity.class);
        it.putExtra(AddressEditActivity.ADDRESS_MODEL,item);
        startActivityForResult(it,ADDRESS_EDIT);
    }

    @Override
    public void onDelAddress(int pos) {
        addressArray.remove(pos);
        addressAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        addressAdapter.setPick(position);
        addressAdapter.notifyDataSetChanged();

        AddressModel item = addressArray.get(position);
        backIntent.putExtra(AddressEditActivity.ADDRESS_MODEL,item);
        setResult(RESULT_OK, backIntent);

        delayFinish();
    }



    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.add_btn)
        {
            Intent it = new Intent(AddressListActivity.this,AddressEditActivity.class);
            startActivityForResult(it,ADDRESS_ADD);
        }
        else
            super.onClick(v);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == ADDRESS_ADD ||
                requestCode == ADDRESS_EDIT)
        {
            if(resultCode ==RESULT_OK)
                UiUtils.makeToast(this,"refresh addresslist");
            else
                UiUtils.makeToast(this,"canceled");
        }
        else
            super.onActivityResult(requestCode,resultCode,data);
    }
}
