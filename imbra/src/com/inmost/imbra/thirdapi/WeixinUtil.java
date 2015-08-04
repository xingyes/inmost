package com.inmost.imbra.thirdapi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.toolbox.ImageLoader;
import com.inmost.imbra.R;
import com.inmost.imbra.util.BMUtil;
import com.inmost.imbra.util.ShareInfo;
import com.inmost.imbra.util.ShareUtil;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import org.json.JSONObject;

/**
 * 微信相关的API
 */
public class WeixinUtil {

	private static final String TAG = "WeixinUtil";

	/**
	 * 唯一标识，应用的id，在微信开放平台注册时获得
	 */
	public static final String APP_ID = "wx37345d2c77e1491c";

    public static final String BROADCAST_FROM_WXSHARE = "broadcast_wx_share";
    public static final String BROADCAST_FROM_WXLOGIN = "broadcast_wx_login";
    public static final String BROADCAST_FROM_WXPAY = "broadcast_wx_pay";

    public static final int WX_SHARE_IMG_THUMB_SIZE = 80; // 微信分享图片尺寸大小
	public static final int WX_SHARE_IMG_LIMIT = 32 * 1024; // 微信分享图片文件大小限制，32K
	public static final int WX_SHARE_TITLE_LIMIT = 512; // 微信分享标题长度限制
	public static final int WX_SHARE_DESCRIPTION_LIMIT = 1024; // 微信分享内容长度限制

	/**
	 * 微信api接口类
	 */
	private static IWXAPI wxApi;

	/**
	 * 向微信创建注册app
	 *
	 * @param context
	 */
	public static void createAndRegisterWX(Context context) {
		try {
			wxApi = WXAPIFactory.createWXAPI(context, APP_ID);
			wxApi.registerApp(APP_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向微信创建注册app，校验签名方式
	 *
	 * @param context
	 * @param checkSign
	 */
	public static void createAndRegisterWX(Context context, Boolean checkSign) {
		try {
			wxApi = WXAPIFactory.createWXAPI(context, APP_ID, checkSign);
			wxApi.registerApp(APP_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得微信API接口
	 *
	 * @return
	 */
	public static IWXAPI getWXApi(final Context context) {
		if (wxApi == null) {
			createAndRegisterWX(context.getApplicationContext(), true);
		}
		return wxApi;
	}

	/**
	 * 是否安装了微信客户端
	 *
	 * @return
	 */
	public static boolean isWXInstalled(Context context) {
		return getWXApi(context).isWXAppInstalled();
	}

	/**
	 * 当前微信客户端是否支持微信支付
	 *
	 * @return
	 */
	public static boolean isWXSupportPay(Context context) {
		return getWXApi(context).getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
	}

	/**
	 * 当前微信客户端是否支持分享
	 *
	 * @return
	 */
	public static boolean isWXSupportShare(Context context) {
		return getWXApi(context).isWXAppSupportAPI();
	}

	/**
	 * 微信登录
	 */
	public static void doWXLogin(Context context) {
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "imbra";

		getWXApi(context).sendReq(req);
	}

	/**
	 * 微信支付
	 *
	 * @param json
	 */
	public static void doWXPay(final Context context,final JSONObject json) {
		PayReq req = new PayReq();
		req.appId = APP_ID;
		req.partnerId = json.optString("partnerid");//商家向财付通申请的商家id
		req.prepayId = json.optString("prepayid");//预支付订单
		req.nonceStr = json.optString("noncestr");//随机串，防重发
		req.timeStamp = json.optString("timestamp");//时间戳，防重发
		req.packageValue = json.optString("package");//商家根据财付通文档填写的数据和签名

        req.options = new com.tencent.mm.sdk.modelpay.PayReq.Options();
        req.options.callbackClassName = "com.inmost.imbra.wxapi.WXEntryActivity";
        req.sign = json.optString("sign");//商家根据微信开放平台文档对数据做的签名

        getWXApi(context).sendReq(req);
	}

	/**
	 * 分享前预处理
	 *
	 * @param shareInfo
	 */
	private static void prepareForShareToWeixin(ShareInfo shareInfo) {
		if (shareInfo.title.length() > WX_SHARE_TITLE_LIMIT) {
			shareInfo.title = shareInfo.title.substring(0, (WX_SHARE_TITLE_LIMIT - 3)) + "...";
		}
		if (shareInfo.wxcontent.length() > WX_SHARE_DESCRIPTION_LIMIT) {
			shareInfo.wxcontent = (shareInfo.wxcontent.substring(0, (WX_SHARE_DESCRIPTION_LIMIT - 3)) + "...");
		}
		if (shareInfo.wxMomentsContent.length() > WX_SHARE_DESCRIPTION_LIMIT) {
			shareInfo.wxMomentsContent = (shareInfo.wxMomentsContent.substring(0, (WX_SHARE_DESCRIPTION_LIMIT - 3)) + "...");
		}
	}

	/**
	 * 微信分享
	 *
	 * @param shareInfo
	 * @param isScene, true:好友; false:朋友圈
	 */
	public static void doWXShare(final BaseActivity activity,ShareInfo shareInfo, boolean isScene, ImageLoader imgloader) {
		prepareForShareToWeixin(shareInfo);

		WXWebpageObject webPageObj = new WXWebpageObject();
		webPageObj.webpageUrl = shareInfo.url;

        if(shareInfo.getShareLogo()==null && !TextUtils.isEmpty(shareInfo.iconUrl))
        {
            ShareUtil.fetchImageThenShare(activity,shareInfo,isScene,imgloader);
            return;
        }
		// 已设置的分享图片为空或过大则使用默认图片
		Bitmap shareBitmap = ShareUtil.checkShareBitmap(activity, shareInfo.getShareLogo(),
                WX_SHARE_IMG_LIMIT, shareInfo.eventFrom);

		WXMediaMessage wxMsg = new WXMediaMessage();
		wxMsg.mediaObject = webPageObj;
		wxMsg.title = shareInfo.title;
		if (isScene) {
			wxMsg.description = shareInfo.wxcontent;
		} else {
			wxMsg.description = shareInfo.wxMomentsContent;
		}
		wxMsg.thumbData = BMUtil.bmpToByteArray(shareBitmap, false);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = shareInfo.transaction;
		req.message = wxMsg;
		req.scene = isScene ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

		getWXApi(activity).sendReq(req);
	}


    public static void informWXShareResult(final Context aParent, int aErrcode) {
		String strInfo= "";


		if(aErrcode == BaseResp.ErrCode.ERR_UNSUPPORT)
		{
			UiUtils.showDialog(aParent,
                    aParent.getString(com.xingy.R.string.no_support_weixin),
                    aParent.getString(com.xingy.R.string.install_newest_weixin),
                    aParent.getString(com.xingy.R.string.install_weixin_yes),
                    aParent.getString(com.xingy.R.string.btn_cancel),
                    new AppDialog.OnClickListener() {

                        @Override
                        public void onDialogClick(int nButtonId) {
                            if (nButtonId == AppDialog.BUTTON_POSITIVE) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://weixin.qq.com/"));
                                aParent.startActivity(intent);
                            }

                        }
                    }
            );

			return;
		}

		switch (aErrcode)
		{
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			strInfo = "\n" + aParent.getString(com.xingy.R.string.share_fail_title) + "\n\n"
					+ aParent.getString(com.xingy.R.string.share_auth_denied) +"\n";
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			strInfo = "\n" + aParent.getString(com.xingy.R.string.share_fail_title) + "\n\n"
				+ aParent.getString(com.xingy.R.string.share_fail_net) +"\n";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			strInfo = "\n" + aParent.getString(com.xingy.R.string.share_fail_title) + "\n\n"
				+ aParent.getString(com.xingy.R.string.share_user_cancel) +"\n";
			break;
		case BaseResp.ErrCode.ERR_OK:
			strInfo = aParent.getString(com.xingy.R.string.share_succ_title);
			break;
		}
		UiUtils.makeToast(aParent, strInfo);
	}


	/**
	 *
	 * @param aParent
	 * @param aErrcode
	 */
	public static void informWXLoginResult(final Context aParent, int aErrcode) {
		String strInfo= "";

		if(aErrcode == BaseResp.ErrCode.ERR_UNSUPPORT)
		{
			UiUtils.showDialog(aParent,
					aParent.getString(com.xingy.R.string.no_support_weixin),
					aParent.getString(com.xingy.R.string.install_newest_weixin),
					aParent.getString(com.xingy.R.string.install_weixin_yes),
					aParent.getString(com.xingy.R.string.btn_cancel),
					new AppDialog.OnClickListener() {

						@Override
						public void onDialogClick(int nButtonId) {
							if (nButtonId == AppDialog.BUTTON_POSITIVE)
							{
								Intent intent = new Intent(Intent. ACTION_VIEW);
								intent.setData(Uri.parse("http://weixin.qq.com/"));
								aParent.startActivity(intent);
							}

						}}
				);

			return;
		}

		switch (aErrcode)
		{
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			strInfo = "\n" + aParent.getString(com.xingy.R.string.login_fail_title) + "\n\n"
					+ aParent.getString(com.xingy.R.string.login_auth_denied) +"\n";
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			strInfo = "\n" + aParent.getString(com.xingy.R.string.login_fail_title) + "\n\n"
				+ aParent.getString(com.xingy.R.string.login_fail_net) +"\n";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			strInfo = "\n" + aParent.getString(com.xingy.R.string.login_fail_title) + "\n\n"
				+ aParent.getString(com.xingy.R.string.login_user_cancel) +"\n";
			break;
		default:
			strInfo = aParent.getString(com.xingy.R.string.login_fail_title);
			break;
		}

		UiUtils.makeToast(aParent, strInfo);
	}


    public static void informWXPayResult(final Context aParent, int aErrcode) {
        if (aErrcode == BaseResp.ErrCode.ERR_UNSUPPORT) {
            UiUtils.showDialog(aParent,
                    aParent.getString(com.xingy.R.string.no_support_weixin),
                    aParent.getString(com.xingy.R.string.install_newest_weixin),
                    aParent.getString(com.xingy.R.string.install_weixin_yes),
                    aParent.getString(com.xingy.R.string.btn_cancel),
                    new AppDialog.OnClickListener() {

                        @Override
                        public void onDialogClick(int nButtonId) {
                            if (nButtonId == AppDialog.BUTTON_POSITIVE) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://weixin.qq.com/"));
                                aParent.startActivity(intent);
                            }
                        }
                    }
            );
            return;
        }

        int strRid = R.string.pay_fail;
        switch (aErrcode) {
            //Pay resp
//            case BaseResp.ErrCode.ERR_OK:
//                strRid = R.string.pay_succ;
//                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                strRid = R.string.pay_auth_denied;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                strRid = R.string.pay_unsupport;
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                strRid = R.string.pay_send_failed;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                strRid = R.string.pay_cancel;
                break;
            default:
                break;
        }

        UiUtils.makeToast(aParent, strRid);
    }

    public static boolean checkWX(final Activity aParent, int baseApiLevel)
    {
        IWXAPI pWechatApi = WXAPIFactory.createWXAPI(aParent, APP_ID);
        int apiLevel =  pWechatApi.getWXAppSupportAPI();
        if(apiLevel <= baseApiLevel)
        {
            UiUtils.showDialog(aParent,
                    aParent.getString(R.string.no_support_weixin),
                    aParent.getString(R.string.install_newest_weixin),
                    aParent.getString(R.string.install_weixin_yes),
                    aParent.getString(R.string.btn_cancel),
                    new AppDialog.OnClickListener() {

                        @Override
                        public void onDialogClick(int nButtonId) {
                            if(nButtonId == DialogInterface.BUTTON_POSITIVE)
                            {
                                Intent intent = new Intent(Intent. ACTION_VIEW);
                                intent.setData(Uri.parse("http://weixin.qq.com/"));
                                aParent.startActivity(intent);
                            }

                        }}
            );

            return false;
        }

        return true;
    }



}
