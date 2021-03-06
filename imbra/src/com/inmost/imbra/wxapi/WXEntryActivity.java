package com.inmost.imbra.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.inmost.imbra.splash.SplashActivity;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.Config;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WeixinUtil.getWXApi(this);
        Intent ait = getIntent();
        if(null == ait)
        {
        	finish();
            return;
        }
        api.handleIntent(ait, this);
    }

    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
		api.handleIntent(intent, this);
	}
    
	@Override
	public void onReq(BaseReq req) {
        switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			Intent newHome =new Intent(this, SplashActivity.class);
			newHome.setAction("WXEntry");
			startActivity(newHome);
			finish();
			break;
			default:
			break;
		}
		
	}

	@Override
	public void onResp(BaseResp resp) {
        switch (resp.getType())
		{
		case ConstantsAPI.COMMAND_PAY_BY_WX:
			handlePayResp(resp);
			break;
		case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
			handleSend2WxResp(resp);
			break;
		case ConstantsAPI.COMMAND_SENDAUTH:
			handleWXLoginResp(resp);
			break;
		
		default:
			break;
		}
		finish();
	}

	/**  
	* method Name:handlePayResp    
	* method Description:  
	* @param resp   
	* void  
	* @exception   
	* @since  1.0.0
    */
	
	private void handlePayResp(BaseResp resp) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", resp.getType());
        bundle.putInt("errcode", resp.errCode);
        bundle.putString("errstr", resp.errStr);
        Intent aIt = new Intent(WeixinUtil.BROADCAST_FROM_WXPAY);
        aIt.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(aIt);
    }

	/**  
	* method Name:handleSend2WxResp    
	* method Description:  
	* @param resp   
	* void  
	* @exception   
	* @since  1.0.0  
	*/
	private void handleSend2WxResp(BaseResp resp) 
	{
		Bundle bundle = new Bundle();
		bundle.putInt("type", resp.getType());
	    bundle.putInt("errcode", resp.errCode);
	    bundle.putString("errstr", resp.errStr);
	    Intent aIt = new Intent(WeixinUtil.BROADCAST_FROM_WXSHARE);
	    aIt.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(aIt);
	}
	
	
	/**
	 * handle response result of wechat login
	 */
	private void handleWXLoginResp(BaseResp resp) {
        Bundle pBundle = new Bundle();
        pBundle.putString("openId", resp.openId);
        pBundle.putInt("type", resp.getType());
		pBundle.putString("code", ((SendAuth.Resp)resp).code);
		pBundle.putString("state", ((SendAuth.Resp)resp).state);
		pBundle.putInt("errCode", resp.errCode);

        Intent pIntent = new Intent(WeixinUtil.BROADCAST_FROM_WXLOGIN);
		pIntent.putExtras(pBundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pIntent);
    }
}

	
