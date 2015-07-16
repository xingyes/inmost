package com.inmost.imbra.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.inmost.imbra.R;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.thirdapi.AccessTokenKeeper;
import com.inmost.imbra.thirdapi.WeiboUtil;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.imbra.util.braConfig;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.Config;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactUsActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

	private Intent  mIntent = null;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_us);

     	mIntent = this.getIntent();
        if(null == mIntent)
        {
            finish();
            return;
        }
        // 初始化布局元素
        initViews();
    }

	private void initViews() {

	}

	@Override
	public void onClick(View v)
	{
//        switch (v.getId())
//        {
//
//		}
	}

	private void requestLogin() {
		Ajax ajax = ServiceConfig.getAjax(braConfig.URL_LOGIN);
		if (null == ajax)
			return;

	}

	@Override
	public void onSuccess(JSONObject v, Response response) {


	}

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }


}
