package com.inmost.imbra.thirdapi;

import android.content.Context;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.xingy.util.MyApplication;

public class WeiboUtil {

	private static final String TAG = "WeiboUtil";

	public static final int WB_SHARE_IMG_THUMB_SIZE = 400; // 微博分享图片尺寸大小
	public static final int WB_SHARE_IMG_LIMIT = 512 * 1024; // 微博分享图片文件大小限制
	public static final int WB_SHARE_TITLE_LIMIT = 60; // 微信分享标题长度限制
	public static final int WB_SHARE_SUMMARY_LIMIT = 80; // 微信分享内容长度限制

	/**
	 * 唯一标识，应用的id，在微博开放平台注册时获得
	 */
	public static final String WB_APP_KEY = "3721412749";

    /**
     * login scope
     */
    public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    public static final String REDIRECT_URL = "http://api.hello-app.cn";

	/**
	 * 微博微博分享接口实例
	 */
	private static IWeiboShareAPI mWBShareApi;


	/**
	 * 创建微博分享接口实例
	 * 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
	 * NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
	 *
	 * @param context
	 */
	public static void createWBApi(Context context) {
		try {
			mWBShareApi = WeiboShareSDK.createWeiboAPI(context, WB_APP_KEY);
			mWBShareApi.registerApp();
		} catch (Exception e) {
            e.printStackTrace();
        }

	}

	/**
	 * 获取微博分享接口实例，若为空则新建
	 *
	 * @return
	 */
	public static IWeiboShareAPI getWBShareApi() {
		if (mWBShareApi == null) {
			createWBApi(MyApplication.app);
		}
		return mWBShareApi;
	}

	/**
	 * 是否安装了微博客户端
	 *
	 * @return
	 */
	public static boolean isWBInstalled() {
		return getWBShareApi().isWeiboAppInstalled();
	}

	/**
	 * 当前微博客户端是否支持分享
	 *
	 * @return
	 */
	public static boolean isWBSupportShare() {
		return getWBShareApi().isWeiboAppSupportAPI();
	}
}
