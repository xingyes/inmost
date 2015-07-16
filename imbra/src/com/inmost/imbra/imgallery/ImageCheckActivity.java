package com.inmost.imbra.imgallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.inmost.imbra.R;
import com.inmost.imbra.imgallery.ImgPgAdapter.ImgFuncListener;
import com.inmost.imbra.main.IMbraApplication;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

public class ImageCheckActivity extends BaseActivity implements ImgFuncListener {

    private static final String LOG_TAG = ImageCheckActivity.class.getName();

    public static final String REQUEST_PIC_INDEX = "pic_index";
    public static final String REQUEST_IMGURL_LIST = "imgurl_list";
    private ImageLoader  mImageLoader;
//	public class menuHolder
//	{
//		public RelativeLayout mDetailLayout;
//
//		public View           mRootView;
//		public NavigationBar  mNavBar;
//		public TextView      mFavBtn;
//		public ImageView     mShowDetailBtn;
//	}
//	private menuHolder    mMenuHolder;
//
//
//	public class detailHolder
//	{
//		public View               detailRootV;
//		public TranslateAnimation detailInAnim;
//		public TranslateAnimation detailOutAnim;
//
//		public ScrollView         detailScrollV;
//	};
    /**
     * mDetailLayout.addview(detailRootV)
     */
//	private detailHolder  mDetailHolder;

    private ImgViewPager mPger;

    private int mPicIndex;
    private ArrayList<String> mUrls;

    //private  Gallery mThumbGallery;
    //private  ItemGalleryAdapter mThumbAdapter;

    public interface DetailHideListener {
        public void onDetailHide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPicIndex = intent.getIntExtra(REQUEST_PIC_INDEX, 0);
        mUrls = intent.getStringArrayListExtra(REQUEST_IMGURL_LIST);


        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);
        ImgPgAdapter mAdapter = new ImgPgAdapter(this, mUrls,mImageLoader);
        if (mAdapter == null || mPicIndex >= mAdapter.getCount()) {
            UiUtils.makeToast(this, R.string.bigimg_error, true);
            finish();
            return;
        }

        setContentView(R.layout.activity_img_vp);

//		mMenuHolder =new menuHolder();
//		mMenuHolder.mNavBar = (NavigationBar) this.findViewById(R.id.img_navbar);
//		mMenuHolder.mDetailLayout = (RelativeLayout) this.findViewById(R.id.img_detail_layout);
//		mMenuHolder.mRootView = this.findViewById(R.id.menu_layout);
//		mMenuHolder.mFavBtn = (TextView) this.findViewById(R.id.fav_btn);
//		findViewById(R.id.share_btn).setOnClickListener(this);
//		mMenuHolder.mShowDetailBtn = (ImageView) this.findViewById(R.id.detail_btn);
//		mMenuHolder.mShowDetailBtn.setOnClickListener(this);
//		mMenuHolder.mFavBtn = (TextView) this.findViewById(R.id.fav_btn);

        mPger = (ImgViewPager) findViewById(R.id.item_img_vp);

        initDetailPg();

        mAdapter.setShowListener(this);

        mPger.setAdapter(mAdapter);
        mPger.setCurrentItem(mPicIndex);

//		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, iColorConfig.WB_APP_KEY);
//		
//		mWeiboShareAPI.registerApp();
//		
//	    mAuthInfo = new AuthInfo(this, iColorConfig.WB_APP_KEY,iColorConfig.REDIRECT_URL, iColorConfig.WB_SCOPE);
//	    mSsoHandler = new SsoHandler(this, mAuthInfo);
//        
//        if (savedInstanceState != null) {
//            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//        }
    }


    private void initDetailPg() {
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

//		mDetailHolder = new detailHolder();
//		mDetailHolder.detailRootV = LayoutInflater.from(this).inflate(R.layout.pg_img_detail, null);
//		mDetailHolder.detailRootV.setLayoutParams(rl);
//		mDetailHolder.detailRootV.setVisibility(View.GONE);
//		mMenuHolder.mDetailLayout.addView(mDetailHolder.detailRootV, rl);
//
//
//		mDetailHolder.detailInAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,0,
//        		Animation.RELATIVE_TO_SELF, 1,Animation.RELATIVE_TO_SELF,0);
//		mDetailHolder.detailInAnim.setDuration(1000);
//		mDetailHolder.detailOutAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,0,
//        		Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,1);
//		mDetailHolder.detailOutAnim.setDuration(500);
//		mDetailHolder.detailOutAnim.setAnimationListener(new AnimationListener(){
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				mDetailHolder.detailRootV.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}});
//
//		mDetailHolder.detailScrollV = (ScrollView) mDetailHolder.detailRootV.findViewById(R.id.scroll_layout);
//		mDetailHolder.detailScrollV.setOnTouchListener(new OnTouchListener(){
//
//			public float downY;
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				android.util.Log.e("scrollv Touch", "action:" + event.getAction() + " (" + downY + "/"
//						+event.getY() + ")" + " Y:"+ mDetailHolder.detailScrollV.getScrollY());
//				switch(event.getAction())
//				{
//				case MotionEvent.ACTION_DOWN:
//					downY = event.getY();
//					break;
//				case MotionEvent.ACTION_MOVE:
//					int xy = mDetailHolder.detailScrollV.getScrollY();
//					if(event.getY() - downY > 30 &&  xy <=0)
//					{
//						mDetailHolder.detailRootV.startAnimation(mDetailHolder.detailOutAnim);
//					}
//					break;
//				default:
//					break;
//				}
//				return false;
//			}});
    }

    //
//
    @Override
    public void onDetailShow() {
//		mDetailHolder.detailRootV.startAnimation(mDetailHolder.detailInAnim);
//		mDetailHolder.detailRootV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMenuShiftShowOrHide() {
//		if(mMenuHolder.mRootView.getVisibility() == View.VISIBLE)
//			mMenuHolder.mRootView.setVisibility(View.GONE);
//		else
//			mMenuHolder.mRootView.setVisibility(View.VISIBLE);
    }
//
//	@Override
//	public void onClick(View v)
//	{
//		switch(v.getId())
//		{
//		case R.id.share_btn:
//			//logint--logout
//			/*
//			mAccessToken = AccessTokenKeeper.readAccessToken(this);
//	        if (mAccessToken.isSessionValid()) {
//	            UiUtils.makeToast(this, "logout");
//	            AccessTokenKeeper.clear(getApplicationContext());
//                mAccessToken = new Oauth2AccessToken();
//            }
//	        else
//	        	mSsoHandler.authorize(new AuthListener());
//	        */
//
//			//wb share
////			this.sendMultiMessage("1111", BitmapFactory.decodeResource(this.getResources(), R.drawable.bird_0));
//
//
////
//
//			break;
//		case R.id.detail_btn:
//			if(mDetailHolder.detailRootV.getVisibility()==View.GONE)
//			{
//				this.onDetailShow();
//			}
//			else
//				mDetailHolder.detailRootV.startAnimation(mDetailHolder.detailOutAnim);
//		default:
//			super.onClick(v);
//			break;
//		}
//	}


//	/** 微博微博分享接口实例 */
//	private IWeiboShareAPI mWeiboShareAPI = null;
//	private AuthInfo mAuthInfo;
//	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
//	private Oauth2AccessToken mAccessToken;
//	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
//	private SsoHandler mSsoHandler;
//
//	/**
//	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
//	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
//	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
//	 * 
//	 * @param hasText
//	 *            分享的内容是否有文本
//	 * @param hasImage
//	 *            分享的内容是否有图片
//	 * @param hasWebpage
//	 *            分享的内容是否有网页
//	 * @param hasMusic
//	 *            分享的内容是否有音乐
//	 * @param hasVideo
//	 *            分享的内容是否有视频
//	 * @param hasVoice
//	 *            分享的内容是否有声音
//	 */
//	private void sendMultiMessage(final String shareText, final Bitmap bm) {
//		boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
//		int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();
//		UiUtils.makeToast(this, "installed:" + isInstalledWeibo
//				+ " supportApiLevel:" + supportApiLevel);
//
//		if (supportApiLevel < 10351) {
//			this.sendSingleMessage(shareText);
//			return;
//		}
//		// 1. 初始化微博的分享消息
//		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//		if (!TextUtils.isEmpty(shareText)) {
//			TextObject textObject = new TextObject();
//			textObject.text = shareText;
//			weiboMessage.textObject = textObject;
//		}
//
//		if (null != bm) {
//			ImageObject imageObject = new ImageObject();
//			imageObject.setImageObject(bm);
//			weiboMessage.imageObject = imageObject;
//		}
//
//		// 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
//		// if (hasWebpage) {
//		// weiboMessage.mediaObject = getWebpageObj();
//		// }
//
//		// 2. 初始化从第三方到微博的消息请求
//		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//		// 用transaction唯一标识一个请求
//		request.transaction = String.valueOf(System.currentTimeMillis());
//		request.multiMessage = weiboMessage;
//
//		// 3. 发送请求消息到微博，唤起微博分享界面
//		mWeiboShareAPI.sendRequest(this, request);
//	}
//
//	private void sendSingleMessage(final String shareText) {
//
//		// 1. 初始化微博的分享消息
//		// 用户可以分享文本、图片、网页、音乐、视频中的一种
//		WeiboMessage weiboMessage = new WeiboMessage();
//		TextObject textObject = new TextObject();
//		textObject.text = shareText;
//		weiboMessage.mediaObject = textObject;
//
//		// 2. 初始化从第三方到微博的消息请求
//		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
//		// 用transaction唯一标识一个请求
//		request.transaction = String.valueOf(System.currentTimeMillis());
//		request.message = weiboMessage;
//
//		// 3. 发送请求消息到微博，唤起微博分享界面
//		mWeiboShareAPI.sendRequest(this, request);
//	}
//
//	@Override
//	public void onResponse(BaseResponse baseResp) {
//		switch (baseResp.errCode) {
//		case WBConstants.ErrorCode.ERR_OK:
//			UiUtils.makeToast(this, "weibo succ");
//			break;
//		case WBConstants.ErrorCode.ERR_CANCEL:
//			UiUtils.makeToast(this, "weibo cancel");
//			break;
//		case WBConstants.ErrorCode.ERR_FAIL:
//			UiUtils.makeToast(this, "weibo fail:" + baseResp.errMsg);
//			break;
//		}
//	}
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//
//		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
//		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
//		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
//		mWeiboShareAPI.handleWeiboResponse(intent, this);
//	}
//
//	/**
//	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
//	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
//	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
//	 * SharedPreferences 中。
//	 */
//	class AuthListener implements WeiboAuthListener {
//
//		@Override
//		public void onComplete(Bundle values) {
//			// 从 Bundle 中解析 Token
//			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
//			if (mAccessToken.isSessionValid()) {
//				// 显示 Token
//				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
//						.format(new java.util.Date(mAccessToken
//								.getExpiresTime()));
//				String format = "Token：%1$s \n有效期：%2$s";
//				String strInfo = String.format(format, mAccessToken.getToken(),
//						date);
//
//				UiUtils.makeToast(ImageCheckActivity.this, strInfo);
//				// 保存 Token 到 SharedPreferences
//				AccessTokenKeeper.writeAccessToken(ImageCheckActivity.this,
//						mAccessToken);
//			} else {
//				// 以下几种情况，您会收到 Code：
//				// 1. 当您未在平台上注册的应用程序的包名与签名时；
//				// 2. 当您注册的应用程序包名与签名不正确时；
//				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//				String code = values.getString("code");
//				String message = "Fail ";
//				if (!TextUtils.isEmpty(code)) {
//					message = message + "\nObtained the code: " + code;
//				}
//				Toast.makeText(ImageCheckActivity.this, message, Toast.LENGTH_LONG)
//						.show();
//			}
//		}
//
//		@Override
//		public void onCancel() {
//			Toast.makeText(ImageCheckActivity.this, "Canceled", Toast.LENGTH_LONG)
//					.show();
//		}
//
//		@Override
//		public void onWeiboException(WeiboException e) {
//			Toast.makeText(ImageCheckActivity.this,
//					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
//					.show();
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		// SSO 授权回调
//		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
//		if (mSsoHandler != null) {
//			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//		}
//	}
//}


}