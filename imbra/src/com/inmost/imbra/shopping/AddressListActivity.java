package com.inmost.imbra.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.util.braConfig;
import com.inmost.imbra.R;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
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
public class AddressListActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
        AddressAdapter.AddressEditListener, AdapterView.OnItemClickListener {

    public static final int    ADDRESS_EDIT = 1003;
    public static final int    ADDRESS_ADD = 1004;

    public static final int    AJAX_LIST = 101;
    public static final int    AJAX_DEL_ADDRESS = 102;

    private Handler mHandler = new Handler();
    private Intent backIntent;
    private Ajax mAjax;
    private ArrayList<AddressModel> addressArray;
    private AddressModel            addPicked;
    private ListView mList;
    private AddressAdapter addressAdapter;
    private Account act;
    private View    emptyView;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_list_address);

        act = ILogin.getActiveAccount();
        Intent ait = getIntent();
        if(null==ait || act==null)
        {
            finish();
            return;
        }

        Object item = ait.getSerializableExtra(AddressEditActivity.ADDRESS_MODEL);
        if(null!=item)
            addPicked = (AddressModel)item;

        loadNavBar(R.id.address_list_navigation_bar);

        emptyView = this.findViewById(R.id.address_list_listView_empty);
        emptyView.setVisibility(View.GONE);
        mList = (ListView)this.findViewById(R.id.address_list_listView);
        addressAdapter = new AddressAdapter(this,-1,this);
        mList.setAdapter(addressAdapter);
        mList.setOnItemClickListener(this);

        findViewById(R.id.add_btn).setOnClickListener(this);
        requestAddress();

        backIntent = new Intent();
    }

    /**
     *
     */
    private void requestAddress()
    {
        mAjax = ServiceConfig.getAjax(braConfig.URL_GET_ADDRESSLIST);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(AJAX_LIST);
        mAjax.setData("token",act.token);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    /**
     *
     */
    private void delAddress(final String addid)
    {
        mAjax = ServiceConfig.getAjax(braConfig.URL_DEL_ADDRESS);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(AJAX_DEL_ADDRESS);
        mAjax.setData("uaid",addid);
        mAjax.setData("token",act.token);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
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

        if(response.getId() == AJAX_DEL_ADDRESS)
        {
            requestAddress();
            return;
        }
        JSONArray dt = jsonObject.optJSONArray("dt");

        if(null==addressArray)
        {
            addressArray = new ArrayList<AddressModel>();
        }
        addressArray.clear();
        for(int i=0;i<dt.length();i++)
        {
            JSONObject itemjson = dt.optJSONObject(i);
            AddressModel addr = new AddressModel();
            addr.parse(itemjson);
            addressArray.add(addr);
            if (null != addPicked && addr.addid.equals(addPicked.addid))
                addressAdapter.setPick(i);
        }
        addressAdapter.setData(addressArray);
        addressAdapter.notifyDataSetChanged();

        emptyView.setVisibility(addressArray.size()<=0 ? View.VISIBLE:View.GONE);

        backIntent.putExtra(AddressEditActivity.ADDRESS_NUM,addressArray.size());
        setResult(RESULT_CANCELED, backIntent);

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

        AddressModel addr = addressArray.get(pos);
        if(addr!=null)
            delAddress(addr.addid);
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
                requestAddress();
            else
                UiUtils.makeToast(this,"canceled");
        }
        else
            super.onActivityResult(requestCode,resultCode,data);
    }
}
