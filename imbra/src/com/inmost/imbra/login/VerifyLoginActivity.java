package com.inmost.imbra.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.inmost.imbra.R;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.imbra.util.braConfig;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.Config;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.Date;


public class VerifyLoginActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

    public static final String    FLAG_CHANGE_PHONE = "change_phone";
    private boolean flagChangePhone = false;

    public static final int   ACTIVITY_CODE_LOGIN = 5555;
	public static final int   COUTING_DOWN_SECOND = 120;
	public static final int   MSG_INTERVAL = 0x100;

    public static final int FLAG_RESULT_LOGIN_SUCCESS = 2222;

	private EditText    mPhonev;
	private EditText    mInputVerifyCode;

	private TextView    mRequestVerifyBtn;
    private TextView    mSubmitBtn;
	private boolean     bSending;
	private int         mCounting = COUTING_DOWN_SECOND;

	public static final int   REQ_REGISTER = 1;
	public static final int   REQ_SMS = 2;
    public static final int   REQ_CHANGE_PHONE = 3;

    private Ajax  mAjax;
    private Account act;
    private String  phoneStr;
    /**
     * weixin
     */
    private WXLoginResponseReceiver mWXLoginResponseReceiver;


	private Handler     mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == MSG_INTERVAL)
			{
				mCounting--;
				if(mCounting > 0)
				{
					mRequestVerifyBtn.setText(getString(R.string.left_second,mCounting));
					mHandler.sendEmptyMessageDelayed(MSG_INTERVAL,1000);
				}
				else
				{
					mRequestVerifyBtn.setText(getString(R.string.send));
					mCounting = COUTING_DOWN_SECOND;
					mRequestVerifyBtn.setEnabled(true);
                    mRequestVerifyBtn.setBackgroundResource(R.drawable.plant_pink_rond);

                    bSending = false;
				}

			}
			else
				super.handleMessage(msg);
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        act = ILogin.getActiveAccount();
        Intent ait = getIntent();
        if(null==ait)
        {
            finish();
            return;
        }
        flagChangePhone = ait.getBooleanExtra(FLAG_CHANGE_PHONE,false);
        if(flagChangePhone && null==act)
        {
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_verify_login);
        // 初始化布局元素
        initViews();
        setResult(RESULT_CANCELED);
        bSending = false;
    }

	private void initViews() {
        loadNavBar(R.id.login_nav);
        mPhonev = (EditText) this.findViewById(R.id.phone);
        mPhonev.setOnClickListener(this);
        mPhonev.requestFocus();

		mInputVerifyCode = (EditText) this.findViewById(R.id.input_verify);
		mRequestVerifyBtn = (TextView) this.findViewById(R.id.request_verify_code);
		mRequestVerifyBtn.setOnClickListener(this);

        mSubmitBtn = (TextView)findViewById(R.id.verify_login);
        mSubmitBtn.setOnClickListener(this);
        findViewById(R.id.weixin_login).setOnClickListener(this);

        if(flagChangePhone) {
            findViewById(R.id.weixin_login).setVisibility(View.GONE);
            mNavBar.setText(R.string.change_phone_num);
            mSubmitBtn.setText(R.string.submit);
        }

	}


	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.request_verify_code)
		{
			if(!bSending)
			{
				if(!requestVerifyCode())
				{
					return;
				}

				mRequestVerifyBtn.setEnabled(false);
                mRequestVerifyBtn.setBackgroundResource(R.drawable.plant_pink_dis_round);
				mRequestVerifyBtn.setText(getString(R.string.left_second,mCounting));

				bSending = true;

				mHandler.sendEmptyMessageDelayed(MSG_INTERVAL,1000);

			}
		}
		else if(v.getId() == R.id.verify_login)
		{
            if(flagChangePhone)
                changePhoneNum();
            else
                verifyAndLogin();
		}
        else if(v.getId() == R.id.weixin_login)
        {
            loginWX();
        }
		else
			super.onClick(v);
	}

    private void changePhoneNum()
    {
        phoneStr = mPhonev.getText().toString();
        if(!ToolUtil.isPhoneNum(phoneStr))
        {
            UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
            return;
        }
        String verifycode = mInputVerifyCode.getText().toString();
        if(ToolUtil.isEmpty(verifycode))
        {
            UiUtils.makeToast(this, R.string.please_input_verifycode);
            return;
        }

        if(null!=mAjax)
            mAjax.abort();
        mAjax = ServiceConfig.getAjax(braConfig.URL_CHANGE_PHONE);
        if (null == mAjax)
            return;
        showLoadingLayer();
        mAjax.setId(REQ_CHANGE_PHONE);
        mAjax.setData("token",act.token);
        mAjax.setData("mob", phoneStr);
        mAjax.setData("vcode", verifycode);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

	private void verifyAndLogin() {

		String phoneNum = mPhonev.getText().toString();
		if(!ToolUtil.isPhoneNum(phoneNum))
		{
			UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
			return;
		}
		String verifycode = mInputVerifyCode.getText().toString();
		if(ToolUtil.isEmpty(verifycode))
		{
			UiUtils.makeToast(this, R.string.please_input_verifycode);
			return;
		}

        if(null!=mAjax)
            mAjax.abort();
        mAjax = ServiceConfig.getAjax(braConfig.URL_VERIFY_LOGIN);
		if (null == mAjax)
			return;
        showLoadingLayer();
        mAjax.setId(REQ_REGISTER);
        mAjax.setData("type",3);
        mAjax.setData("mobile", phoneNum);
        mAjax.setData("vcode", verifycode);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
	}

	private boolean requestVerifyCode() {
		String phoneNum = mPhonev.getText().toString();
		if(ToolUtil.isPhoneNum(phoneNum))
		{
            if(mAjax!=null)
                mAjax.abort();
            mAjax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);//URL_VERIFYCODE_SMS
            if (null == mAjax)
				return false;

            showLoadingLayer();
            mAjax.setId(REQ_SMS);
            mAjax.setData("phone_number", phoneNum);

            mAjax.setOnSuccessListener(this);
            mAjax.setOnErrorListener(this);
            mAjax.send();
			return true;
		}
		else
		{
			UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
		}
		return false;
	}

	@Override
	public void onSuccess(JSONObject v, Response response) {
        closeLoadingLayer();
        final int ret = v.optInt("err");
		if(ret != 0 )
		{
			String msg =  v.optString("data");
			UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? this.getString(R.string.parser_error_msg): msg);
			return;
		}

        if(response.getId() == REQ_CHANGE_PHONE)
        {
            UiUtils.makeToast(this,R.string.submit_succ);
            act.phone = phoneStr;
            ILogin.setActiveAccount(act);
            ILogin.saveIdentity(act);
            setResult(RESULT_OK);
            finish();
        }
		else if(response.getId() == REQ_REGISTER)
		{
            JSONObject data = v.optJSONObject("dt");
			if(null == data)
			{
				UiUtils.makeToast(this, this.getString(R.string.parser_error_msg));
				return;
			}
            UiUtils.makeToast(VerifyLoginActivity.this,R.string.login_succ);
            Account account = new Account();
			account.uid = data.optString("uid");
            account.nickName = data.optString("nickname");
            account.token = data.optString("token");
            account.iconUrl = data.optString("himg");
            account.phone = data.optString("phone");
            account.rowCreateTime = new Date().getTime();

            ILogin.setActiveAccount(account);
            ILogin.saveIdentity(account);
            finish();
		}
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			finish();
//			return true;
//		}
//		else
//			return super.onKeyDown(keyCode, event);
//	}

    /**
     *  weixin  login -------------
     */

    public void loginWX()
    {
        if(null == mWXLoginResponseReceiver) {
            mWXLoginResponseReceiver = new WXLoginResponseReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WeixinUtil.BROADCAST_FROM_WXLOGIN);
            LocalBroadcastManager.getInstance(VerifyLoginActivity.this).registerReceiver(mWXLoginResponseReceiver, filter);
        }
        if (WeixinUtil.checkWX(this, 0)) {
            showLoadingLayer();
            WeixinUtil.doWXLogin(this);
        }
    }


    public class WXLoginResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeLoadingLayer();
            int nErrCode = intent.getIntExtra("errCode", ConstantsAPI.COMMAND_UNKNOWN);
            int nType = intent.getIntExtra("type", -1);
            final String strCode = intent.getStringExtra("code");
            final String strState = intent.getStringExtra("state");
            String strOpenId = intent.getStringExtra("openId");

//            AppDialog ad = UiUtils.showDialog(VerifyLoginActivity.this, "Login",
//                    "Weixin login errcode:" + nErrCode + ",type:" + nType + ",state:" + strState + ",code:" + strCode +
//                            "openid:" + strOpenId,
//                    R.string.btn_ok, new AppDialog.OnClickListener() {
//                        @Override
//                        public void onDialogClick(int nButtonId) {
//                            wxLoginCallBack(strCode, strState);
//                        }
//                    });


            if(nType == ConstantsAPI.COMMAND_SENDAUTH)
            {
                if(nErrCode == BaseResp.ErrCode.ERR_OK)
                {

                    wxLoginCallBack(strCode, strState);
                }
                else
                {
                    WeixinUtil.informWXLoginResult(VerifyLoginActivity.this,nErrCode);
                }
            }
        }
    }

    private void wxLoginCallBack(String code, String state){

        if(mAjax!=null)
            mAjax.abort();

        mAjax = ServiceConfig.getAjax(braConfig.URL_VERIFY_LOGIN);
        if (null == mAjax)
            return;
        mAjax.setId(REQ_REGISTER);
        mAjax.setData("type",4);
        mAjax.setData("code", code);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

        /**
         * weixinlogin ajax -- lkey
         */
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
//        if(requestCode == Constants.REQUEST_API)
//        {
//            if(resultCode == Constants.RESULT_LOGIN)
//                mTencent.handleLoginData(data,mTencentListener);
//        }
//        else if (mSsoHandler != null) {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }

        super.onActivityResult(requestCode,resultCode,data);
    }


    @Override
    protected void onDestroy()
    {
        if(null!=mAjax)
        {
            mAjax.abort();
            mAjax = null;
        }

        if(null != mWXLoginResponseReceiver)
        {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mWXLoginResponseReceiver);
            mWXLoginResponseReceiver = null;
        }
        super.onDestroy();
    }
}
