package com.inmost.imbra.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.inmost.imbra.R;
import com.inmost.imbra.thirdapi.WeiboShareResponseActivity;
import com.inmost.imbra.thirdapi.WeixinUtil;
import com.inmost.novoda.imageloader.core.bitmap.BitmapUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xingy.lib.ui.RadioDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShareUtil {

    public static final String[] sharePackages = {
            "com.tencent.mobileqq",
            "com.tencent.WBlog",
            "com.android.mms",
            "com.sina.weibo"};
    public static final String SHARE_WEIXNI = "com.tencent.mm";


    public  static final String F_QQ = "qq";
	public  static final String F_QZONE = "qzone";
	public  static final String F_WEIBO = "weibo";
	public  static final String F_WEIXIN = "weixin";

	private static final String TAG = "ShareUtil";

	private static int thumbSize = 80; // 分享图片尺寸

	private static HashMap<String, Object> mCallbackList = new HashMap<String, Object>();

	/**
	 * 检查分享图片，若为空或者文件过大则用默认图片
	 *
	 * @param bitmap
	 * @param limitSize
	 * @param eventFrom
	 */
	public static Bitmap checkShareBitmap(Context context,Bitmap bitmap, int limitSize, String eventFrom) {
		int bitmapSize = 0;
		if (bitmap != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
				bitmapSize = bitmap.getByteCount();
			} else {
				bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
			}
		}
		if (bitmapSize == 0 || bitmapSize > limitSize) {
			Drawable drawable;
//			if (!TextUtils.isEmpty(eventFrom) && eventFrom.equals(ClickConstant.CLICK_SHARE_VALUE_HB)) {
//				drawable = context.getApplicationContext().getResources().getDrawable(R.drawable.share_wx_hb);
//			} else {
//				drawable = context.getApplicationContext().getResources().getDrawable(R.drawable.share_default_icon);
//			}
//			bitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		return bitmap;
	}

	/**
	 * 检查分享图片url，若为空则用默认的url
	 *
	 * @param shareInfo
	 */
//	public static void checkShareIconUrl(ShareInfo shareInfo) {
//		if (TextUtils.isEmpty(shareInfo.iconUrl)) {
//			shareInfo.setIconUrl(BaseApplication.getInstance().getResources().getString(R.string.share_default_iconurl));
//		}
//	}

	/**
	 * 根据不同途径发出分享
	 *
	 * @param myActivity
	 * @param shareInfo
	 * @param channel
	 */
	private static void shareByChannel(final BaseActivity myActivity, final ShareInfo shareInfo, final int channel,
                                       ImageLoader imgloader) {
		switch (channel) {
			case 1:
				WeixinUtil.doWXShare(myActivity, shareInfo, true,imgloader);
				break;
			case 2:
				WeixinUtil.doWXShare(myActivity,shareInfo, false,imgloader);
				break;
			case 3:
				shareToWeibo(myActivity, shareInfo);
				break;
			default:

		}
	}

	/**
	 * 生成缩略图，能避免图片太大引起的OOM问题
	 *
	 * @param path
	 * @return
	 */
	public static Bitmap makeBitmapFromFile(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int width = options.outWidth;
		int height = options.outHeight;
		if (width > height) {
			options.inSampleSize = Math.round((float) width / (float) thumbSize);
		} else {
			options.inSampleSize = Math.round((float) height / (float) thumbSize);
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		// 图片过大需要缩放
		if (options.inSampleSize > 1) {
			BitmapUtil bitmapUtil = new BitmapUtil();
			bitmap = bitmapUtil.scaleBitmap(bitmap, thumbSize, thumbSize);
		}
		return bitmap;
	}

	/**
	 * 通过url获取图片的Bitmap，然后分享
	 *
	 * @param myActivity
	 * @param shareInfo
     * @param imgLoader
	 */
	public static void fetchImageThenShare(final BaseActivity myActivity, final ShareInfo shareInfo,final boolean isScene,
                                           final ImageLoader imgLoader) {

        Bitmap bitmap = null;
        final int nMaxSize = 80;
        imgLoader.get(shareInfo.iconUrl,new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                if(bm==null)
                    return;
                bm = com.xingy.util.ImageLoader.resize(bm, nMaxSize);

                myActivity.closeLoadingLayer();

                shareInfo.setShareLogo(bm);
                // 2. Send request.
                WeixinUtil.doWXShare(myActivity, shareInfo, isScene,imgLoader);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
	}

	/**
	 * 提交微博分享
	 *
	 * @param parent
	 * @param shareInfo
	 */
	private static void shareToWeibo(final BaseActivity parent, ShareInfo shareInfo) {
		Intent intent = new Intent(parent, WeiboShareResponseActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("shareBitmap", shareInfo.getShareLogo());
		intent.putExtra("shareBitmap", bundle);
		intent.putExtra("shareUrl", shareInfo.url);
		intent.putExtra("shareTitle", shareInfo.title);
		intent.putExtra("shareSummary", shareInfo.summary);
		intent.putExtra("shareTransaction", shareInfo.transaction);
		intent.putExtra("shareEventFrom", shareInfo.eventFrom);
        parent.startActivity(intent);
	}

	/**
	 * 普通分享
	 *
	 * @param context
	 * @param shareInfo
	 */
	private static void shareToMore(final Context context, final ShareInfo shareInfo) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, shareInfo.title);
		intent.putExtra(Intent.EXTRA_TEXT, shareInfo.normalText);

		context.startActivity(Intent.createChooser(intent, "分享到"));
	}



    public static void shareInfoOut(final BaseActivity activity, final ShareInfo shareInfo,final ImageLoader imgloader){
        if( (null == activity) || (null == shareInfo) || null == imgloader ){
            return ;
        }
//        Intent pIntent = new Intent(Intent.ACTION_SEND);
//        pIntent.putExtra(Intent.EXTRA_TEXT, "");
//        pIntent.setType("text/plain");
//
//        PackageManager pManager = aContext.getPackageManager();
//        List<ResolveInfo> aResult = pManager.queryIntentActivities(pIntent, PackageManager.MATCH_DEFAULT_ONLY);
//        final int nSize = (null != aResult ? aResult.size() : 0);
//        if( 0 >= nSize )
//            return ;
//
//        final List<Sharable> aSharables = new ArrayList<Sharable>(nSize);
//        int mark[] = new int[sharePackages.length];
//        for(int i=0;i<sharePackages.length;i++)
//            mark[i]=0;
//        for(ResolveInfo pInfo : aResult)
//        {
//            ApplicationInfo pAppInfo = pInfo.activityInfo.applicationInfo;
//            Sharable pEntity = new Sharable();
//            pEntity.mLabel = (String) pManager.getApplicationLabel(pAppInfo);
//            pEntity.mIcon = pManager.getApplicationIcon(pAppInfo);
//            pEntity.mPackageName = pInfo.activityInfo.packageName;
//
//            for(int i=0; i < sharePackages.length; i++)
//            {
//                if(pEntity.mPackageName.equalsIgnoreCase(sharePackages[i]) && mark[i]==0)
//                {
//                    mark[i]=1;
//                    aSharables.add(pEntity);
//                }
//            }
//        }

        final List<Sharable> aSharables = new ArrayList<Sharable>();
        IWXAPI pWechatApi = WXAPIFactory.createWXAPI(activity, WeixinUtil.APP_ID);
        final int apiLevel =  pWechatApi.getWXAppSupportAPI();
        if(apiLevel >0) //has weixin
        {
            Sharable pEntity = new Sharable();
            pEntity.mPackageName = SHARE_WEIXNI;
            pEntity.mLabel = activity.getString(R.string.weixin_someone);//(String) pManager.getApplicationLabel(pInfo.applicationInfo);
            pEntity.mIcon = activity.getResources().getDrawable(R.drawable.share_to_weixin);
            aSharables.add(0,pEntity);
            if(apiLevel >=0x21020001)
            {
                pEntity = new Sharable();
                pEntity.mPackageName = SHARE_WEIXNI;
                pEntity.mLabel = activity.getString(R.string.weixin_circle);
                pEntity.mIcon = activity.getResources().getDrawable(R.drawable.share_to_time_line_icon);
                aSharables.add(0,pEntity);
            }
        }

        SharableAdapter pAdapter = new SharableAdapter(activity, aSharables);
        UiUtils.showListDialog(activity, activity.getString(R.string.share_title), pAdapter, new RadioDialog.OnRadioSelectListener() {
            @Override
            public void onRadioItemClick(int which) {
                if (null != aSharables) {
                    Sharable pSelected = aSharables.get(which);
                    if (apiLevel >= 0x21020001 && which == 0)
                        WeixinUtil.doWXShare(activity,shareInfo,false,imgloader);
                    else if(which == 1)
                        WeixinUtil.doWXShare(activity,shareInfo,true,imgloader);
                    else {
                            Intent pIntent = new Intent(Intent.ACTION_SEND);
                            pIntent.setPackage(pSelected.mPackageName);
                            pIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.wxcontent);
                            pIntent.setType("text/plain");
                            activity.startActivity(pIntent);
                        }
                    }
                }
        });
    }


//	private static void alertDialog(final MyActivity context, final ShareInfo shareInfo) {
//		View inflate = ImageUtil.inflate(R.layout.share_layout, null);
//		inflate.setMinimumWidth(10000);
//
//		final Dialog alertDialog = new Dialog(context, R.style.fill_order_dialog);
//		WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
//		lp.x = 0;
//		lp.y = -1000;
//		lp.gravity = Gravity.BOTTOM;
//		alertDialog.onWindowAttributesChanged(lp); // dialog 屏幕底部显示
//		alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				closeTransferActivity(context);
//			}
//		});
//		alertDialog.show();
//		alertDialog.setContentView(inflate, new ViewGroup.LayoutParams(DPIUtil.getWidth(), DPIUtil.dip2px(275)));
//
//		RelativeLayout shareToWxFriends = (RelativeLayout) inflate.findViewById(R.id.share_to_wx_friends);
//		RelativeLayout shareToWxCircle = (RelativeLayout) inflate.findViewById(R.id.share_to_wx_circle);
//		RelativeLayout shareToWeibo = (RelativeLayout) inflate.findViewById(R.id.share_to_weibo);
//		RelativeLayout shareToQQFriends = (RelativeLayout) inflate.findViewById(R.id.share_to_qq_friends);
//		RelativeLayout shareToQZone = (RelativeLayout) inflate.findViewById(R.id.share_to_qzone);
//		RelativeLayout shareToMore = (RelativeLayout) inflate.findViewById(R.id.share_to_more);
//		RelativeLayout shareToCancle = (RelativeLayout) inflate.findViewById(R.id.share_to_cancle);
//
//		// 分享到微信好友
//		shareToWxFriends.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				JDMtaUtils.onClick(context, "Share_Wxfriends", context.getClass().getName(), shareInfo.getUrl());
//				if (!isWXCanShare(context)) {
//					return;
//				}
//				// 若已有分享的Bitmap或者分享图片url为空则直接发起分享
//				if (shareInfo.getShareLogo() != null || TextUtils.isEmpty(shareInfo.getIconUrl())) {
//					WeixinUtil.doWXShare(shareInfo, true);
//				} else {
//					thumbSize = WeixinUtil.WX_SHARE_IMG_THUMB_SIZE;
//					fetchImageForShare(context, shareInfo, 1);
//				}
//				alertDialog.dismiss();
//			}
//		});
//		// 分享到微信朋友圈
//		shareToWxCircle.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				JDMtaUtils.onClick(context, "Share_Wxmoments", context.getClass().getName(), shareInfo.getUrl());
//				if (!isWXCanShare(context)) {
//					return;
//				}
//				// 若已有分享的Bitmap或者分享图片url为空则直接发起分享
//				if (shareInfo.getShareLogo() != null || TextUtils.isEmpty(shareInfo.getIconUrl())) {
//					WeixinUtil.doWXShare(shareInfo, false);
//				} else {
//					thumbSize = WeixinUtil.WX_SHARE_IMG_THUMB_SIZE;
//					fetchImageForShare(context, shareInfo, 2);
//				}
//				alertDialog.dismiss();
//			}
//		});
//		// 分享到微博
//		shareToWeibo.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				JDMtaUtils.onClick(context, "Share_Sinaweibo", context.getClass().getName(), shareInfo.getUrl());
//				// 微博初始化
//				WeiboUtil.createWBApi(context);
//				// 检查是否可以通过微博分享
//				if (!WeiboUtil.isWBInstalled() || !WeiboUtil.isWBSupportShare()) {
////					ToastUtils.showToastY(R.string.weibo_can_not_share);
//					return;
//				}
//				// 若已有分享的Bitmap或者分享图片url为空则直接发起分享
//				if (shareInfo.getShareLogo() != null || TextUtils.isEmpty(shareInfo.getIconUrl())) {
//					shareToWeibo(context, shareInfo);
//				} else {
//					thumbSize = WeiboUtil.WB_SHARE_IMG_THUMB_SIZE;
//					fetchImageForShare(context, shareInfo, 3);
//				}
//				alertDialog.dismiss();
//			}
//		});
//		// 分享到QQ好友
//		shareToQQFriends.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				JDMtaUtils.onClick(context, "Share_QQfriends", context.getClass().getName(), shareInfo.getUrl());
//				checkShareIconUrl(shareInfo);
//				QQUtil.getInstance().shareToQQ(context, shareInfo);
//				alertDialog.dismiss();
//			}
//		});
//		// 分享到QQ空间
//		shareToQZone.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				JDMtaUtils.onClick(context, "Share_QQzone", context.getClass().getName(), shareInfo.getUrl());
//				checkShareIconUrl(shareInfo);
//				QQUtil.getInstance().shareToQzone(context, shareInfo);
//				alertDialog.dismiss();
//			}
//		});
//		// 分享到更多
//		shareToMore.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				JDMtaUtils.onClick(context, "Share_Moreshare", context.getClass().getName(), shareInfo.getUrl());
//				shareToMore(context, shareInfo);
//				alertDialog.dismiss();
//			}
//		});
//		// 分享取消
//		shareToCancle.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				JDMtaUtils.onClick(context, "Share_Cancel", context.getClass().getName(), shareInfo.getUrl());
//				if (alertDialog.isShowing()) alertDialog.dismiss();
//			}
//		});
//	}

	/**
	 * 分享弹层外部调用, 增加回调方法
	 *
	 * @param myActivity
	 * @param shareInfo
	 * @param callbackListener
	 */
//	public static void showShareDialog(final MyActivity myActivity, final ShareInfo shareInfo, CallbackListener callbackListener) {
//		JDMtaUtils.onClick(myActivity, "Layout_Share", myActivity.getClass().getName(), shareInfo.getUrl());
//		if (myActivity == null) {
//			return;
//		}
//		if (TextUtils.isEmpty(shareInfo.getUrl())) {
//			closeTransferActivity(myActivity);
//			ToastUtils.showToastY(R.string.share_cant_empty);
//			return;
//		}
//		if (myActivity instanceof Activity && myActivity.isFinishing()) {
//			return;
//		}
//
//		// 若为空设置默认值
//		if (TextUtils.isEmpty(shareInfo.getTitle())) {
//			shareInfo.setTitle(myActivity.getResources().getString(R.string.jingdong));
//		}
//		if (TextUtils.isEmpty(shareInfo.getSummary())) {
//			shareInfo.setSummary(myActivity.getResources().getString(R.string.share_defaut_summary));
//		}
//		if (TextUtils.isEmpty(shareInfo.getWxcontent())) {
//			shareInfo.setWxcontent(shareInfo.getSummary());
//		}
//		if (TextUtils.isEmpty(shareInfo.getWxMomentsContent())) {
//			shareInfo.setWxMomentsContent(shareInfo.getSummary());
//		}
//		if (TextUtils.isEmpty(shareInfo.getNormalText())) {
//			shareInfo.setNormalText(shareInfo.getTitle() + " " + shareInfo.getSummary() + " " + shareInfo.getUrl());
//		}
//
//		String transaction = String.valueOf(System.currentTimeMillis());
//		shareInfo.setTransaction(transaction);
//		// 存放回调接口
//		if (callbackListener != null) {
//			mCallbackList.put(transaction, callbackListener);
//		}
//
//		myActivity.post(new Runnable() {
//			public void run() {
//				alertDialog(myActivity, shareInfo);
//			}
//		});
//	}

	/**
	 * 根据分享事务ID取回调处理，若无则用通用处理
	 *
	 * @param transaction
	 * @return
	 */
	private static CallbackListener getCallbackListener(String transaction) {
		CallbackListener callbackListener;
		if (mCallbackList.containsKey(transaction)) {
			callbackListener = (CallbackListener) mCallbackList.get(transaction);
			mCallbackList.remove(transaction); // 移除已回调过的
		} else {
			callbackListener = new CallbackListener() {
				@Override
				public void onComplete(Object obj) {
				}

				@Override
				public void onError(String msg) {
				}

				@Override
				public void onCancel() {
				}
			};
		}
		return callbackListener;
	}

	/**
	 * 分享完成回调
	 *
	 * @param transaction
	 * @param obj
	 */
	public static void shareComplete(String transaction, Object obj) {
		getCallbackListener(transaction).onComplete(obj);
	}

	/**
	 * 分享失败回调
	 *
	 * @param transaction
	 * @param msg
	 */
	public static void shareError(String transaction, String msg, String flag) {
		getCallbackListener(transaction).onError(msg);
	}

	/**
	 * 分享取消回调
	 *
	 * @param transaction
	 */
	public static void shareCancel(String transaction, String flag) {
		getCallbackListener(transaction).onCancel();
	}

	/**
	 * 供分享回调的接口
	 */
	public interface CallbackListener {
		void onComplete(Object obj);
		void onError(String msg);
		void onCancel();
	}


    public static class Sharable
    {
        public String   mLabel;
        public String   mPackageName;
        public Drawable mIcon;
    }

    private static class SharableHolder
    {
        public ImageView mIcon;
        public TextView mLabel;
    }

    public static class SharableAdapter extends RadioDialog.RadioAdapter
    {
        public SharableAdapter(Context aContext, List<Sharable> aSharables)
        {
            super(aContext);
            mSharables = aSharables;
        }

        @Override
        public int getCount()
        {
            return (null != mSharables ? mSharables.size() : 0);
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            SharableHolder holder = null;
            if (null == convertView)
            {
                convertView = View.inflate(mContext, R.layout.share_item, null);
                holder = new SharableHolder();
                holder.mIcon = (ImageView) convertView.findViewById(R.id.item_icon);
                holder.mLabel = (TextView) convertView.findViewById(R.id.item_label);
                convertView.setTag(holder);
            }
            else
            {
                holder = (SharableHolder) convertView.getTag();
            }

            // set data
            if(null != mSharables)
            {
                Sharable pEntity = mSharables.get(position);
                holder.mLabel.setText(pEntity.mLabel);
                holder.mIcon.setImageDrawable(pEntity.mIcon);
            }
            return convertView;
        }


        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }


        private List<Sharable>          mSharables;
    }


}
