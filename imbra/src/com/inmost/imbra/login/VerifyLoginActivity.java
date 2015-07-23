package com.inmost.imbra.login;

import java.util.Date;

import org.json.JSONObject;

import com.inmost.imbra.R;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.imbra.util.braConfig;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.Config;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;


public class VerifyLoginActivity extends BaseActivity implements OnSuccessListener<JSONObject> {

	public static final int   COUTING_DOWN_SECOND = 120;
	public static final int   MSG_INTERVAL = 0x100;

    public static final int FLAG_RESULT_LOGIN_SUCCESS = 2222;

	private EditText    mPhonev;
	private EditText    mInputVerifyCode;

	private TextView    mRequestVerifyBtn;
	private boolean     bSending;
	private int         mCounting = COUTING_DOWN_SECOND;

	public static final int   REQ_REGISTER = 1;
	public static final int   REQ_SMS = 2;

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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_verify_login);

        // 初始化布局元素
        initViews();

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

		findViewById(R.id.verify_login).setOnClickListener(this);
        findViewById(R.id.weixin_login).setOnClickListener(this);;

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
            verifyAndLogin();
		}
        else if(v.getId() == R.id.weixin_login)
        {
            loginWX();
        }
		else
			super.onClick(v);
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

		Ajax ajax = ServiceConfig.getAjax(braConfig.URL_VERIFY_LOGIN);
		if (null == ajax)
			return;
		ajax.setId(REQ_REGISTER);
		ajax.setData("phone_number", phoneNum);
		ajax.setData("vcode", verifycode);

		ajax.setOnSuccessListener(this);
		ajax.setOnErrorListener(this);
		ajax.send();
		addAjax(ajax);
	}

	private boolean requestVerifyCode() {
		String phoneNum = mPhonev.getText().toString();
		if(ToolUtil.isPhoneNum(phoneNum))
		{
			Ajax ajax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);
            if (null == ajax)
				return false;

			ajax.setId(REQ_SMS);
			ajax.setData("phone_number", phoneNum);

			ajax.setOnSuccessListener(this);
			ajax.setOnErrorListener(this);
			ajax.send();
			addAjax(ajax);
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
		final int ret = v.optInt("error");
		if(ret != 0 )
		{
			String msg =  v.optString("data");
			UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? this.getString(R.string.parser_error_msg): msg);
			return;
		}

		if(response.getId() == REQ_REGISTER)
		{
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

			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
			return true;
		}
		else
			return super.onKeyDown(keyCode, event);
	}

    /**
     *  weixin  login -------------
     */

    public void loginWX()
    {
        if(null == mWXLoginResponseReceiver)
        {
            WeixinUtil.createAndRegisterWX(this);

            mWXLoginResponseReceiver = new WXLoginResponseReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(braConfig.BROADCAST_FROM_WXLOGIN);
            registerReceiver(mWXLoginResponseReceiver, filter, Config.SLEF_BROADCAST_PERMISSION,null);

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
                    WeixinUtil.informWXLoginResult(VerifyLoginActivity.this,nErrCode);
                }
            }
        }
    }

    private void wxLoginCallBack(String code, String state){
        UiUtils.makeToast(this,"Weixin login state :" + state + ", code" + code);
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
        if(null != mWXLoginResponseReceiver)
        {
            unregisterReceiver(mWXLoginResponseReceiver);
        }
        super.onDestroy();
    }
}
