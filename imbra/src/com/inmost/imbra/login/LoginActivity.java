package com.inmost.imbra.login;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

	private Intent  mIntent = null;
    private TextView  qqLoginBtn;
    private TextView  wbLoginBtn;
    private TextView  wxLoginBtn;
    private TextView  smsLoginBtn;
    private TextView  regBtn;

    /**
     * qqlogin
     */
    private Tencent  mTencent;
    private IUiListener mTencentListener;

    /**
     * weibo
     */
    private AuthInfo mAuthInfo;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private AuthListener mWBListener;


    /**
     * weixin
     */
    private WXLoginResponseReceiver mWXLoginResponseReceiver;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

     	mIntent = this.getIntent();
        if(null == mIntent)
        {
            finish();
            return;
        }
        // 初始化布局元素
        initViews();

        Account act = ILogin.getActiveAccount();

//        UiUtils.hideSoftInput(this, mPhone);

		if(null!=act)
		{
//			mHandler.sendEmptyMessageDelayed(FINISH_LOGIN, 1500);
		}

	}

	private void initViews() {
        this.findViewById(R.id.qq_login_btn).setOnClickListener(this);
        this.findViewById(R.id.wb_login_btn).setOnClickListener(this);
        this.findViewById(R.id.wx_login_btn).setOnClickListener(this);
        this.findViewById(R.id.sms_login_btn).setOnClickListener(this);
    	this.findViewById(R.id.go_register).setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
        switch (v.getId())
        {
            case R.id.qq_login_btn:
                loginQQ();
                break;
            case R.id.wb_login_btn:
                loginWB();
                break;
            case R.id.wx_login_btn:
                loginWX();
                break;
            case R.id.sms_login_btn:
                requestLogin();
                break;
            case R.id.go_register:
                break;
            default:
                break;
		}
	}

	private void requestLogin() {
//
//		if(!ToolUtil.isPhoneNum(phoneNum))
//		{
//			UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
//			return;
//		}
//		else if(ToolUtil.isEmpty(pswd))
//		{
//			UiUtils.makeToast(this, R.string.pswd_not_empty);
//			return;
//		}

		Ajax ajax = ServiceConfig.getAjax(braConfig.URL_LOGIN);
		if (null == ajax)
			return;

	}

	@Override
	public void onSuccess(JSONObject v, Response response) {
		/**Error:	{"error":"2","data":"\u53c2\u6570\u4e0d\u5408\u6cd5"}
			Success:	{"error":0,"data":{"uid":"MQ==","jl_skey":"248971"}}
			*/
		final int ret = v.optInt("error");
		if(ret != 0 )
		{
			String msg =  v.optString("data");
			UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? this.getString(R.string.parser_error_msg): msg);
			return;
		}


		/**{"error":0,
		 * "data":{"uid":"MQ==","jl_skey":"248971"}}
		 *
		 */
		JSONObject data = v.optJSONObject("data");
		if(null == data)
		{
			UiUtils.makeToast(this, this.getString(R.string.parser_error_msg));
			return;
		}
		Account account = new Account();
		account.setUid(data.optString("uid"));
		account.setSkey(data.optString("jl_skey"));
		account.setRowCreateTime(new Date().getTime());
		ILogin.setActiveAccount(account);
		ILogin.saveIdentity(account);

	}

    /**
     ****************************    qq about    *************************************************************
     */
    private void loginQQ()
    {
        if(null == mTencent) {
            mTencent = Tencent.createInstance(braConfig.QQ_APP_ID, this.getApplicationContext());
            mTencentListener = new BaseUiListener();
        }
        mTencent.login(this, "all", mTencentListener);
    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response)
        {
            try{
                JSONObject json = (JSONObject)response;
                if(json.has("ret") && json.has("openid") && json.has("access_token"))
                {
                    String openid = json.optString("openid");
                    String access_token = json.optString("access_token");
                    //use info to login or register bra
                }

            }catch (Exception e)
            {}

        }

        @Override
        public void onError(UiError uiError)
        {
            UiUtils.makeToast(LoginActivity.this,uiError.errorMessage);
        }

        @Override
        public void onCancel(){
            UiUtils.makeToast(LoginActivity.this,"Cancel");
        }
    }

    /**
     * ********************     weibo  about ****************************************************************************************
     */


    private void loginWB()
    {
        if(null==mAuthInfo)
        {
            mAuthInfo = new AuthInfo(this, WeiboUtil.WB_APP_KEY,WeiboUtil.REDIRECT_URL, WeiboUtil.WB_SCOPE);
            mSsoHandler = new SsoHandler(this, mAuthInfo);
            mWBListener = new AuthListener();
        }
        mSsoHandler.authorize(mWBListener);
    }


    /**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	private class AuthListener implements WeiboAuthListener{

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new java.util.Date(mAccessToken
								.getExpiresTime()));
				String format = "Token：%1$s \n有效期：%2$s";
				String strInfo = String.format(format, mAccessToken.getToken(),
						date);

				UiUtils.makeToast(LoginActivity.this, strInfo);
				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(LoginActivity.this,
                        mAccessToken);
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = "Fail ";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				UiUtils.makeToast(LoginActivity.this, message);
            }
		}

		@Override
		public void onCancel() {
            UiUtils.makeToast(LoginActivity.this, "Canceled");
    	}

		@Override
		public void onWeiboException(WeiboException e) {
            UiUtils.makeToast(LoginActivity.this, "Auth exception : " + e.getMessage());

        }
	}

    /**
     ****************************    wx about    *************************************************************
     */

    public void loginWX()
    {
        if(null == mWXLoginResponseReceiver)
        {
            WeixinUtil.createAndRegisterWX(this);

            mWXLoginResponseReceiver = new WXLoginResponseReceiver();

        }
        if(WeixinUtil.isWXInstalled(this)) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WeixinUtil.BROADCAST_FROM_WXLOGIN);
            this.registerReceiver(mWXLoginResponseReceiver, filter, Config.SLEF_BROADCAST_PERMISSION, null);

            WeixinUtil.doWXLogin(this);

        }
        else
            UiUtils.makeToast(this,"no weixin");

    }


    public class WXLoginResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nErrCode = intent.getIntExtra("errCode", ConstantsAPI.COMMAND_UNKNOWN);
            int nType = intent.getIntExtra("type", -1);
            String strCode = intent.getStringExtra("code");
            String strState = intent.getStringExtra("state");

            if(nType == ConstantsAPI.COMMAND_SENDAUTH)
            {
                if(nErrCode == BaseResp.ErrCode.ERR_OK)
                {
                    wxLoginCallBack(strCode, strState);
                }
                else
                {
                    WeixinUtil.informWXLoginResult(LoginActivity.this,nErrCode);
                }
            }
        }
    }

    private void wxLoginCallBack(String code, String state){

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == Constants.REQUEST_API)
        {
            if(resultCode == Constants.RESULT_LOGIN)
                mTencent.handleLoginData(data,mTencentListener);
        }
        else if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode,resultCode,data);
    }


    @Override
    protected void onDestroy()
    {
        if(null!=mWXLoginResponseReceiver)
            this.unregisterReceiver(mWXLoginResponseReceiver);
        mWXLoginResponseReceiver = null;

        super.onDestroy();
    }


}
