//package com.inmost.imbra.login;
//
//import java.util.Date;
//
//import org.json.JSONObject;
//
//import com.xingy.R;
//import com.xingy.lib.ui.UiUtils;
//import com.xingy.util.ServiceConfig;
//import com.xingy.util.ToolUtil;
//import com.xingy.util.activity.BaseActivity;
//import com.xingy.util.ajax.Ajax;
//import com.xingy.util.ajax.OnSuccessListener;
//import com.xingy.util.ajax.Response;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.TextView;
//
//
//public class RegisterActivity extends BaseActivity implements OnSuccessListener<JSONObject> {
//
//	public static final int   COUTING_DOWN_SECOND = 120;
//	public static final int   MSG_INTERVAL = 0x100;
//
//	private EditText    mInputNum;
//	private EditText    mInputPwd;
//	private EditText    mInputVerifyCode;
//
//	private TextView    mRequestVerifyBtn;
//	private boolean     bSending;
//	private int         mCounting = COUTING_DOWN_SECOND;
//
//	public static final int   REQ_REGISTER = 1;
//	public static final int   REQ_SMS = 2;
//
//
//
//	private Handler     mHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg)
//		{
//			if(msg.what == MSG_INTERVAL)
//			{
//				mCounting--;
//				if(mCounting > 0)
//				{
//					mRequestVerifyBtn.setText(getString(R.string.left_second,mCounting));
//					mHandler.sendEmptyMessageDelayed(MSG_INTERVAL,1000);
//				}
//				else
//				{
//					mRequestVerifyBtn.setText(getString(R.string.send));
//					mCounting = COUTING_DOWN_SECOND;
//					mRequestVerifyBtn.setEnabled(true);
//					bSending = false;
//				}
//
//			}
//			else
//				super.handleMessage(msg);
//		}
//
//	};
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_register);
//        // 初始化布局元素
//        initViews();
//        loadNavBar(R.id.register_navbar);
//        ServiceConfig.setContext(this.getApplicationContext());
//
//        bSending = false;
//    }
//
//	private void initViews() {
//		mInputNum = (EditText) this.findViewById(R.id.input_num);
//		mInputNum.setOnClickListener(this);
//		mInputNum.requestFocus();
//
//		mInputVerifyCode = (EditText) this.findViewById(R.id.input_verify);
//		mInputPwd = (EditText) this.findViewById(R.id.input_psw);
//
//		mRequestVerifyBtn = (TextView) this.findViewById(R.id.request_verify_code);
//		mRequestVerifyBtn.setOnClickListener(this);
//
//		findViewById(R.id.register_btn).setOnClickListener(this);;
//
//	}
//
//
//	@Override
//	public void onClick(View v)
//	{
//		if(v.getId() == R.id.request_verify_code)
//		{
//			if(!bSending)
//			{
//				if(!requestVerifyCode())
//				{
//					return;
//				}
//
//				mRequestVerifyBtn.setEnabled(false);
//				mRequestVerifyBtn.setText(getString(R.string.left_second,mCounting));
//
//				bSending = true;
//
//				mHandler.sendEmptyMessageDelayed(MSG_INTERVAL,1000);
//
//			}
//		}
//		else if(v.getId() == R.id.register_btn)
//		{
//			requestRegister();
//		}
//		else
//			super.onClick(v);
//	}
//
//	private void requestRegister() {
//
//		String phoneNum = mInputNum.getText().toString();
//		if(!ToolUtil.isPhoneNum(phoneNum))
//		{
//			UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
//			return;
//		}
//		String psw = this.mInputPwd.getText().toString();
//		if(phoneNum.length()< 6 || phoneNum.length() >16 )
//		{
//			UiUtils.makeToast(this, R.string.please_input_correct_psw);
//			return;
//		}
//		String verifycode = this.mInputVerifyCode.getText().toString();
//		if(ToolUtil.isEmpty(verifycode))
//		{
//			UiUtils.makeToast(this, R.string.please_input_verifycode);
//			return;
//		}
//
//		Ajax ajax = ServiceConfig.getAjax(iColorConfig.URL_REGISTER);
//		if (null == ajax)
//			return;
//		ajax.setId(REQ_REGISTER);
//		ajax.setData("phone_number", phoneNum);
//		ajax.setData("password", psw);
//		ajax.setData("vcode", verifycode);
//
//		ajax.setOnSuccessListener(this);
//		ajax.setOnErrorListener(this);
//		ajax.send();
//		addAjax(ajax);
//
//	}
//
//	private boolean requestVerifyCode() {
//		String phoneNum = mInputNum.getText().toString();
//		if(ToolUtil.isPhoneNum(phoneNum))
//		{
//			Ajax ajax = ServiceConfig.getAjax(iColorConfig.URL_VERIFYCODE_SMS);
//			if (null == ajax)
//				return false;
//
//			ajax.setId(REQ_SMS);
//			ajax.setData("phone_number", phoneNum);
//
//			ajax.setOnSuccessListener(this);
//			ajax.setOnErrorListener(this);
//			ajax.send();
//			addAjax(ajax);
//			return true;
//		}
//		else
//		{
//			UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
//		}
//		return false;
//	}
//
//	@Override
//	public void onSuccess(JSONObject v, Response response) {
//		final int ret = v.optInt("error");
//		if(ret != 0 )
//		{
//			String msg =  v.optString("data");
//			UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? this.getString(R.string.parser_error_msg): msg);
//			return;
//		}
//
//		if(response.getId() == REQ_REGISTER)
//		{
//			/**{"error":0,
//			 * "data":{"uid":"MQ==","jl_skey":"248971"}}
//			 *
//			 */
//			JSONObject data = v.optJSONObject("data");
//			if(null == data)
//			{
//				UiUtils.makeToast(this, this.getString(R.string.parser_error_msg));
//				return;
//			}
//			Account account = new Account();
//			account.setUid(data.optString("uid"));
//			account.setSkey(data.optString("jl_skey"));
//			account.setRowCreateTime(new Date().getTime());
//			ILogin.setActiveAccount(account);
//			ILogin.saveIdentity(account);
//
//			Intent intent = new Intent();
//			setResult(RESULT_OK, intent);
//			finish();
//
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			Intent intent = new Intent();
//			setResult(RESULT_CANCELED, intent);
//			finish();
//			return true;
//		}
//		else
//			return super.onKeyDown(keyCode, event);
//	}
//
//
//}
