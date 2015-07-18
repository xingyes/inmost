package com.inmost.imbra.login;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.inmost.imbra.R;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;


public class AlterInfoActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

	public static final String INPUT_PHONE_NUM = "input_phone_num";
	
	public static final String ACTIVITY_LABEL = "activity_label";
	public static final String ALTER_ITEM = "alter_label";
	public static final String SERVICE_URL_KEY = "service_url_key";
	public static final String PARAM_KEY = "param_key";

    public static final String  ORI_INFO = "ORI_INFO";

    private String   mOriInfo;
	private String   mParam;
	private String   mServiceUrlKey;
	private EditText mContentEdit;
	private String   mItemLabel;
	private String   mActivityLabel;
	
	private boolean  bIsInputPhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		Intent ait = getIntent();
		if(null == ait)
		{
			finish();
			return;
		}
		mActivityLabel = ait.getStringExtra(ACTIVITY_LABEL);
		
		bIsInputPhone = ait.getBooleanExtra(INPUT_PHONE_NUM, false);
		mItemLabel = ait.getStringExtra(ALTER_ITEM);
		mServiceUrlKey= ait.getStringExtra(SERVICE_URL_KEY);
		mParam= ait.getStringExtra(PARAM_KEY);
        mOriInfo = ait.getStringExtra(ORI_INFO);
		if(ToolUtil.isEmpty(mServiceUrlKey) ||
				ToolUtil.isEmpty(mParam))
		{
			finish();
			return;
		}
		setContentView(R.layout.activity_submit_alterinfo);
		loadNavBar(R.id.submit_info_navbar);
		
		TextView tv = (TextView) this.findViewById(R.id.submit_btn);
		tv.setText(R.string.submit);
		
		if(!ToolUtil.isEmpty(mActivityLabel))
			mNavBar.setText(mActivityLabel);
		
		if(!ToolUtil.isEmpty(mItemLabel)) 
		{
			tv = (TextView) this.findViewById(R.id.alter_label);
			tv.setText(mItemLabel);
		}

		mContentEdit = (EditText) this.findViewById(R.id.input_content);
		if(bIsInputPhone)
			mContentEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

        if(!ToolUtil.isEmpty(mOriInfo))
        {
            mContentEdit.setHint(mOriInfo);
        }
			
		
		findViewById(R.id.submit_btn).setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.submit_btn)
		{
			submitAlterInfo();
		}
		super.onClick(v);
	}


	private void submitAlterInfo() {
		String strContent = mContentEdit.getText().toString();
		if(ToolUtil.isEmpty(strContent))
		{
			UiUtils.makeToast(this, R.string.no_empty_allowed);
			return;
		}
		if(bIsInputPhone && ToolUtil.isPhoneNum(strContent))
		{
			UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
			return;
		}
		
		Ajax ajax = ServiceConfig.getAjax(mServiceUrlKey);

        UiUtils.makeToast(this,R.string.submit_succ);
        setResult(RESULT_OK);
        finish();
		if (null == ajax)
			return;
		
		ajax.setData(mParam, strContent);
		ajax.setOnSuccessListener(this);
		
		this.addAjax(ajax);
		ajax.send();
	}


	@Override
	public void onSuccess(JSONObject v, Response response) {
		final int ret = v.optInt("error");
		if(ret != 0 )
		{
			String msg =  v.optString("data");
			UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg): msg);
			return;
		}

		UiUtils.makeToast(this,R.string.submit_succ);
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void onBackPressed()
	{
		setResult(RESULT_CANCELED);
		finish();
	}
}
	
	