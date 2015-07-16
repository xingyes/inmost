package com.inmost.imbra.thirdapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.inmost.imbra.util.ShareUtil;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

public class WeiboShareResponseActivity extends Activity implements IWeiboHandler.Response {

	private static final String TAG = "WeiboShareResponseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 必须先创建实例
		WeiboUtil.createWBApi(this);

		if (savedInstanceState != null) {
			WeiboUtil.getWBShareApi().handleWeiboResponse(getIntent(), this);
		}

		// 向微博分享SDK提交内容
		Intent intent = getIntent();
		if (intent.hasExtra("shareUrl")) {
			String shareUrl = intent.getExtras().getString("shareUrl");
			String shareTitle = intent.getExtras().getString("shareTitle");
			String shareSummary = intent.getExtras().getString("shareSummary");
			String shareTransaction = intent.getExtras().getString("shareTransaction");
			String shareEventFrom = intent.getExtras().getString("shareEventFrom");
			Bitmap shareBitmap = intent.getBundleExtra("shareBitmap").getParcelable("shareBitmap");
			shareBitmap = ShareUtil.checkShareBitmap(this,shareBitmap, WeiboUtil.WB_SHARE_IMG_LIMIT, shareEventFrom);

			// 分享内容长度处理
			if (shareTitle.length() > WeiboUtil.WB_SHARE_TITLE_LIMIT) {
				shareTitle = shareTitle.substring(0, (WeiboUtil.WB_SHARE_TITLE_LIMIT - 3)) + "...";
			}
			if (shareSummary.length() > WeiboUtil.WB_SHARE_SUMMARY_LIMIT) {
				shareSummary = shareSummary.substring(0, (WeiboUtil.WB_SHARE_SUMMARY_LIMIT - 3)) + "...";
			}

			WebpageObject obj = new WebpageObject();
			obj.identify = Utility.generateGUID();
//			// 添加公共尾部
//			if (!shareTitle.contains("@")) {
//				shareTitle = shareTitle + getResources().getString(R.string.share_at_jingdong);
//			}
			obj.title = shareTitle;
			obj.actionUrl = shareUrl;
			obj.defaultText = shareTitle;
			obj.setThumbImage(shareBitmap);

			if (WeiboUtil.getWBShareApi().getWeiboAppSupportAPI() >= 10351) {
				TextObject textObject = new TextObject();
				textObject.text = shareSummary;

				ImageObject imageObject = new ImageObject();
				imageObject.setImageObject(shareBitmap);

				obj.description = shareSummary;

				sendMultiMessage(textObject, imageObject, obj, shareTransaction);
			} else {
				// 为呈现需要的效果做适配处理
				obj.description = shareSummary + shareTitle;

				sendMessage(obj, shareTransaction);
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		WeiboUtil.getWBShareApi().handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse resp) {
		switch (resp.errCode) {
			case WBConstants.ErrorCode.ERR_OK:
				ShareUtil.shareComplete(resp.transaction, ShareUtil.F_WEIBO);
				break;
			case WBConstants.ErrorCode.ERR_CANCEL:
				ShareUtil.shareCancel(resp.transaction, ShareUtil.F_WEIBO);
				break;
			case WBConstants.ErrorCode.ERR_FAIL:
				ShareUtil.shareError(resp.transaction, resp.errMsg, ShareUtil.F_WEIBO);
				break;
			default:
		}
		finish();
	}

	/**
	 * 当 {@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时调用
	 *
	 * @param obj
	 * @param transaction
	 */
	private void sendMessage(WebpageObject obj, String transaction) {
		WeiboMessage msg = new WeiboMessage();
		msg.mediaObject = obj;

		SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
		req.transaction = transaction;
		req.message = msg;

		WeiboUtil.getWBShareApi().sendRequest(this,req);
	}

	/**
	 * 当 {@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时调用
	 *
	 * @param textObject
	 * @param imageObject
	 * @param obj
	 * @param transaction
	 */
	private void sendMultiMessage(TextObject textObject, ImageObject imageObject,
								  WebpageObject obj, String transaction) {
		WeiboMultiMessage msg = new WeiboMultiMessage();
		msg.textObject = textObject;
		msg.imageObject = imageObject;
		msg.mediaObject = obj;

		SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
		req.transaction = transaction;
		req.multiMessage = msg;

		WeiboUtil.getWBShareApi().sendRequest(this,req);
	}
}
