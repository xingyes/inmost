package com.xingy.util.activity;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xingy.util.Log;

public class WebViewActivity extends BaseActivity {

	private static final String LOG_TAG = WebViewActivity.class.getName();
	private WebView webView;

	public void setWebView(WebView webView) {

		if (null == webView) {
			Log.e(LOG_TAG, "setWebView: webview is empty");
			return;
		}

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				WebViewActivity.this.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				WebViewActivity.this.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				WebViewActivity.this.onReceivedError(view, errorCode, description, failingUrl);
			}
			
			//https 处理
			/*
			@Override  
	        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {  
	            handler.proceed();  
//            handler.cancel();  
//            handler.handleMessage(null); 
	        } */ 
		});

		this.webView = webView;
	}

	public void loadUrl(String url) {
		if (webView != null) {
			webView.loadUrl(url);
		}
	}

	public void onPageStarted(WebView view, String url, Bitmap favicon) {

	}

	public void onPageFinished(WebView view, String url) {

	}

	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

	}

	public boolean getWebViewCanGoBack() {
		return true;
	}

	public void setWebView(int webViewId) {

		View view = findViewById(webViewId);

		if (null == view) {
			Log.e(LOG_TAG, "setWebView: view is empty");
			return;
		}

		this.setWebView((WebView) view);
	}

	public WebView getWebView() {
		return this.webView;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (null != webView && (keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack() && getWebViewCanGoBack()) {
			webView.goBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy()
	{
		if(null != webView)
		{
			ViewGroup pViewGroup = (ViewGroup) webView.getParent();
			if(null != pViewGroup) {
				pViewGroup.removeView(webView);
				webView.destroy();
			}
			webView = null;
		}
		super.onDestroy();
	}
	
}
