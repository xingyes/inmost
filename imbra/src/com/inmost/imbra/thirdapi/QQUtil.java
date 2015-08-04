package com.inmost.imbra.thirdapi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.inmost.imbra.util.ShareUtil;
import com.inmost.imbra.util.braConfig;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xingy.util.MyApplication;
import com.xingy.util.ShareInfo;

import java.util.ArrayList;

public class QQUtil {

	private static final String TAG = "QQUtil";
    private static final int QQ_SHARE_TITLE_LIMIT = 30; // 分享到QQ标题限制长度
	private static final int QQ_SHARE_SUMMARY_LIMIT = 30; // 分享到QQ摘要限制长度
	private static final int QZONE_SHARE_TITLE_LIMIT = 200; // 分享到QZone标题限制长度
	private static final int QZONE_SHARE_SUMMARY_LIMIT = 600; // 分享到QZone摘要限制长度

	/**
	 * 唯一标识，应用的id，在QQ开放平台注册时获得
	 */
	private static final String APP_ID = braConfig.QQ_APP_ID;

	private static QQUtil mSelf;

	private static Tencent mTencent;

	public static QQUtil getInstance() {
		if (mSelf == null) {
			mSelf = new QQUtil();
		}
		return mSelf;
	}

	/**
	 * 创建Tencent实例
	 *
	 * @param context
	 */
	public static void createTencentInstance(Context context) {
		mTencent = Tencent.createInstance(APP_ID, context);
	}

	/**
	 * 获得Tencent实例
	 *
	 * @return
	 */
	public static Tencent getTencentInstance(Context context) {
		if (mTencent == null) {
			createTencentInstance(context);
		}
		return mTencent;
	}

	/**
	 * 分享到QQ好友
	 *
	 * @param activity
	 * @param shareInfo
	 */
	public void shareToQQ(Activity activity, ShareInfo shareInfo) {
		prepareForShareToQQ(shareInfo);

		final Bundle params = new Bundle();
		params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_EXT_INT, com.tencent.connect.share.QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
		params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.url);
		params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TITLE, shareInfo.title);
		params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.summary);
		params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.iconUrl);
		params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_APP_NAME, "返回");

		BaseUiListener listener = new BaseUiListener();
		listener.flag = ShareUtil.F_QQ;
		listener.transaction = shareInfo.transaction;

		getTencentInstance(activity).shareToQQ(activity, params, listener);
	}

	/**
	 * 分享到QQ空间
	 *
	 * @param activity
	 * @param shareInfo
	 */
	public void shareToQzone(Activity activity, ShareInfo shareInfo) {
		prepareForShareToQzone(shareInfo);

		ArrayList<String> imgUrlList = new ArrayList();
		if (!TextUtils.isEmpty(shareInfo.iconUrl)) {
			imgUrlList.add(shareInfo.iconUrl);
		}

		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareInfo.title);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareInfo.summary);
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareInfo.url);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);

		BaseUiListener listener = new BaseUiListener();
		listener.flag = ShareUtil.F_QZONE;
		listener.transaction = shareInfo.transaction;

		getTencentInstance(activity).shareToQzone(activity, params, listener);
	}

	/**
	 * 分享回调接口
	 */
	private static class BaseUiListener implements IUiListener {
		public String flag; // 分享的渠道
		public String transaction; // 分享事务ID，作为回调凭证

		@Override
		public void onComplete(Object obj) {
			ShareUtil.shareComplete(transaction, flag);
		}

		@Override
		public void onError(UiError e) {
			ShareUtil.shareError(transaction, e.errorMessage, flag);
		}

		@Override
		public void onCancel() {
			ShareUtil.shareCancel(transaction, flag);
		}
	}

	/**
	 * 分享到QQ的参数预处理
	 *
	 * @param shareInfo
	 */
	private static void prepareForShareToQQ(ShareInfo shareInfo) {
		if (shareInfo.title.length() > QQ_SHARE_TITLE_LIMIT) {
			shareInfo.title = (shareInfo.title.substring(0, (QQ_SHARE_TITLE_LIMIT - 3)) + "...");
		}
		if (shareInfo.summary.length() > QQ_SHARE_SUMMARY_LIMIT) {
			shareInfo.summary = (shareInfo.summary.substring(0, (QQ_SHARE_SUMMARY_LIMIT - 3)) + "...");
		}
	}

	/**
	 * 分享到Qzone的参数预处理
	 *
	 * @param shareInfo
	 */
	private static void prepareForShareToQzone(ShareInfo shareInfo) {
		if (shareInfo.title.length() > QZONE_SHARE_TITLE_LIMIT) {
			shareInfo.title = (shareInfo.title.substring(0, (QZONE_SHARE_TITLE_LIMIT - 3)) + "...");
		}
		if (shareInfo.summary.length() > QZONE_SHARE_SUMMARY_LIMIT) {
			shareInfo.summary = (shareInfo.summary.substring(0, (QZONE_SHARE_SUMMARY_LIMIT - 3)) + "...");
		}
	}
}
