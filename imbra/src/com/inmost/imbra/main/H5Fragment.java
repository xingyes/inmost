package com.inmost.imbra.main;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.inmost.imbra.R;
import com.inmost.imbra.blog.BlogVolleyActivity;
import com.inmost.imbra.imgallery.ImageCheckActivity;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.VerifyLoginActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class H5Fragment extends Fragment {

    private BaseActivity mActivity;
    private View mRootView;

    private WebView webview;
    private String  oriUrl = "";
    private boolean zoomable = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mRootView = inflater.inflate(R.layout.activity_webview, container, false);

        initView();

        return mRootView;
    }


    @Override
    public void onAttach(Activity activity) {
        mActivity = (BaseActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        webview.loadUrl(oriUrl);
    }





    private void initView() {
        mRootView.findViewById(R.id.html5_navbar).setVisibility(View.GONE);

        webview = (WebView) mRootView.findViewById(R.id.web_container);
        WebSettings mWebSettings = webview.getSettings();

        mWebSettings.setBuiltInZoomControls(zoomable);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setSupportZoom(true);


        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("tel:")) {
                    HTML5Activity.callTel(mActivity,url);
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

                    ToolUtil.startActivity(mActivity, VerifyLoginActivity.class, null, HTML5Activity.REQUEST_FLAG_LOGIN_RELOAD);
                    return true;
                }
                if(null != view) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mActivity.showLoadingLayer();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mActivity.closeLoadingLayer();
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
                if(mActivity.isBeenSeen())
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
                if(mActivity.isBeenSeen())
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
                if(mActivity.isBeenSeen())
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


    public void setZoomable(boolean ab)
    {
        zoomable = ab;
    }
    public void setUrl(final String url)
    {
        oriUrl = url;
    }

}
