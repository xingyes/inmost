package com.inmost.imbra.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.VerifyLoginActivity;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.imbra.util.ShareInfo;
import com.inmost.imbra.util.ShareUtil;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class HTML5Activity extends BaseActivity{

    public static final String ORI_URL = "ori_url";
    public static final String ENABLE_ZOOM = "enable_zoom";

    private static final int REQUEST_FLAG_LOGIN = 1;
    private static final int REQUEST_FLAG_LOGIN_RELOAD = 2;

    private WebView webview;
    private RequestQueue mQueue;
    private ImageLoader mImgLoader;

    private String oriUrl;
    private boolean enableZoom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);

        Intent ait = getIntent();
        if(null!=ait) {
            oriUrl = ait.getStringExtra(ORI_URL);
            if (TextUtils.isEmpty(oriUrl)) {
                finish();
                return;
            }
            enableZoom = ait.getBooleanExtra(ENABLE_ZOOM, true);

        }


        loadNavBar(R.id.html5_navbar);


        mNavBar.setRightInfo(R.string.share_title,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == mImgLoader) {
                    mQueue = Volley.newRequestQueue(HTML5Activity.this);
                    mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);
                }
                ShareInfo si = new ShareInfo();
                si.title = "imbra";
                si.iconUrl="http://img2.imgtn.bdimg.com/it/u=921607941,1665261509&fm=21&gp=0.jpg";
                si.url = "http://www.o2bra.com.cn/";
                si.wxcontent = "快来看看imbra";
                si.wxMomentsContent = "最迷人的爱啵秀，令你满意";
                ShareUtil.shareInfoOut(HTML5Activity.this,si,mImgLoader);
            }
        });

        TextView navRightView = (TextView) mNavBar.findViewById(R.id.navigationbar_right_text);
        navRightView.setTextColor(getResources().getColor(R.color.global_pink));

        webview = (WebView)this.findViewById(R.id.web_container);


        WebSettings mWebSettings = webview.getSettings();
        mWebSettings.setBuiltInZoomControls(enableZoom);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setSupportZoom(true);


        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("tel:")) {
                    callTel(url);
                    return true;
                }
//                }else if(url.startsWith("icson://copyString?"))
//                {
//                    String copyStr = url.substring(("icson://copyString?").length());
//                    String strKey = copyStr.substring(copyStr.indexOf("=")+1);
//                    ClipboardManager mCm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                    mCm.setText(strKey);
//                    UiUtils.makeToast(HTML5LinkActivity.this,R.string.preferences_copy_to_clipboard_title);
//                    return true;
//                }else if(url.startsWith("yixunapp://back"))
//                {
//                    pressBack();
//                    return true;
//                }
                else if(url.startsWith("yixunapp://qqLogin"))
                {
                    ILogin.clearAccount();

                    ToolUtil.startActivity(HTML5Activity.this, VerifyLoginActivity.class, null, HTML5Activity.REQUEST_FLAG_LOGIN_RELOAD);
                    return true;
                }
                if(null != view) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoadingLayer();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                closeLoadingLayer();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
            }
        });



        webview.setWebChromeClient(new WebChromeClient(){
            /*
             * override javaScript funtion: alert
             */
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                if(HTML5Activity.this.isBeenSeen())
                {
                    Dialog pDialog = UiUtils.showDialog(view.getContext(), getString(R.string.caption_hint), message,
                            R.string.btn_ok, new AppDialog.OnClickListener() {
                        @Override
                        public void onDialogClick(int nButtonId) {
                            result.confirm();
                        }
                    });

                    pDialog.setCancelable(false);
                }
                return true;
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url,
                                            String message, JsResult result) {
                return super.onJsBeforeUnload(view, url, message, result);
            }

            /*
             * override javaScript funtion: confirm
             */
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                if(HTML5Activity.this.isBeenSeen())
                {
                    Dialog pDialog = UiUtils.showDialog(view.getContext(), getString(R.string.caption_hint), message, R.string.btn_ok, R.string.btn_cancel, new AppDialog.OnClickListener() {
                        @Override
                        public void onDialogClick(int nButtonId) {
                            if( AppDialog.BUTTON_POSITIVE == nButtonId )
                                result.confirm();
                            else if( AppDialog.BUTTON_NEGATIVE == nButtonId )
                                result.cancel();
                        }
                    });

                    pDialog.setCancelable(false);
                }
                return true;
            }

            /*
             * override javaScript funtion: prompt
             */
            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {
                final EditText pText = new EditText(view.getContext());
                pText.setSingleLine();
                pText.setText(defaultValue);
                if(HTML5Activity.this.isBeenSeen())
                {
                    Dialog pDialog = UiUtils.showDialog(view.getContext(), getString(R.string.caption_hint), message, R.string.btn_ok, R.string.btn_cancel, new AppDialog.OnClickListener() {
                        @Override
                        public void onDialogClick(int nButtonId) {
                            if( AppDialog.BUTTON_POSITIVE == nButtonId ) {
                                result.confirm(pText.getText().toString());
                            } else if ( AppDialog.BUTTON_NEGATIVE == nButtonId ) {
                                result.cancel();
                            }
                        }
                    });

                    pDialog.setCancelable(false);
                }
                return true;
            }
        });

        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webview.loadUrl(oriUrl);
	}



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_FLAG_LOGIN:
                if (resultCode == VerifyLoginActivity.FLAG_RESULT_LOGIN_SUCCESS) {
                    JSONObject accountMsg = accontMsgToJSON();
                    if(null != webview) {
                        webview.loadUrl("javascript:loginCallBack(" + accountMsg + ")");
                    }
                }
                break;
            case REQUEST_FLAG_LOGIN_RELOAD:
                if (resultCode == VerifyLoginActivity.FLAG_RESULT_LOGIN_SUCCESS) {
//                    setupWebCookie();
                    if(null != webview) {
                        webview.reload();
                        //mWebView.loadUrl(mOrigUrl);
                    }
                }
                break;
        }
    }


    protected void callTel(String url) {
        Intent pIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
        ToolUtil.checkAndCall(this, pIntent);
    }


    private JSONObject accontMsgToJSON(){
        String uid = ILogin.getLoginUid();
//        String skey = ILogin.g.getLoginSkey();
//        String token = ILogin.getLoginToken();

        JSONObject account = new JSONObject();
        try{
            account.put("uid", uid);
//            account.put("skey", skey);
//            account.put("token", token);
        } catch (JSONException ex) {
        }

        return account;
    }


}
	
