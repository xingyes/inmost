//package com.xingy.util;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.database.DataSetObserver;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.xingy.R;
//import com.xingy.lib.ui.AppDialog;
//import com.xingy.lib.ui.RadioDialog;
//import com.xingy.lib.ui.UiUtils;
//import com.xingy.preference.Preference;
//import com.tencent.mm.sdk.modelbase.BaseResp;
//import com.tencent.mm.sdk.modelmsg.SendAuth;
//import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
//import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;
//
//public class AppUtils {
//	public static final String[] sharePackages = {
//			"com.tencent.mobileqq",
//			"com.tencent.WBlog",
//			"com.android.mms",
//			"com.sina.weibo"};
//	public static final String SHARE_WEIXNI = "com.tencent.mm";
//	
//	/**
//	 * ???述产?????? 
//	 */
//	public interface DescProvider {
//		/**
//		 * @param strPackageName: ?????��??�?�?�????package??????�?
//		 * @return
//		 */
//		public String getDesc(String strPackageName);
//	}
//	
//	/**
//	 * 
//	 * @param aContext
//	 * @param strTitle
//	 * @param strLinkUrl
//	 * @param strPicUrl
//	 * @param aProvider
//	 */
//	public static void shareAppInfo(final Context aContext, final String strTitle, final String strLinkUrl, final String strPicUrl, final DescProvider aProvider){
//		if( (null == aContext) || (null == aProvider) ){
//			return ;
//		}
//		
//		Intent pIntent = new Intent(Intent.ACTION_SEND);
//		pIntent.putExtra(Intent.EXTRA_TEXT, "");
//		pIntent.setType("text/plain");
//		
//		PackageManager pManager = aContext.getPackageManager();
//		List<ResolveInfo> aResult = pManager.queryIntentActivities(pIntent, PackageManager.MATCH_DEFAULT_ONLY);
//		final int nSize = (null != aResult ? aResult.size() : 0);
//		if( 0 >= nSize )
//			return ;
//		
//		final List<Sharable> aSharables = new ArrayList<Sharable>(nSize);
//		int mark[] = new int[sharePackages.length];
//		for(int i=0;i<sharePackages.length;i++)
//			mark[i]=0;
//		for(ResolveInfo pInfo : aResult)
//		{
//			ApplicationInfo pAppInfo = pInfo.activityInfo.applicationInfo;
//			Sharable pEntity = new Sharable();
//			pEntity.mLabel = (String) pManager.getApplicationLabel(pAppInfo);
//			pEntity.mIcon = pManager.getApplicationIcon(pAppInfo);
//			pEntity.mPackageName = pInfo.activityInfo.packageName;
//			
//			for(int i=0; i < sharePackages.length; i++)
//			{
//				if(pEntity.mPackageName.equalsIgnoreCase(sharePackages[i]) && mark[i]==0)
//				{
//					mark[i]=1;
//					aSharables.add(pEntity);
//				}
//			}
//		}
//		
//		
//		IWXAPI pWechatApi = WXAPIFactory.createWXAPI(aContext, Config.APP_ID);
//		final int apiLevel =  pWechatApi.getWXAppSupportAPI();
//		if(apiLevel >0) //has weixin
//		{
//			Sharable pEntity = new Sharable();
//			pEntity.mPackageName = SHARE_WEIXNI;
//			pEntity.mLabel = aContext.getString(R.string.weixin_someone);//(String) pManager.getApplicationLabel(pInfo.applicationInfo);
//			pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_weixin);
//			aSharables.add(0,pEntity);
//			if(apiLevel >=0x21020001)
//			{
//				pEntity = new Sharable();
//				pEntity.mPackageName = SHARE_WEIXNI;
//				pEntity.mLabel = aContext.getString(R.string.weixin_circle);
//				pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_time_line_icon);
//				aSharables.add(0,pEntity);
//			}
//		}
//		
//		SharableAdapter pAdapter = new SharableAdapter(aContext, aSharables);
//		UiUtils.showListDialog(aContext, aContext.getString(R.string.share_title), pAdapter, new RadioDialog.OnRadioSelectListener() {
//			@Override
//			public void onRadioItemClick(int which) {
//				if( null != aSharables )
//				{
//					Sharable pSelected = aSharables.get(which);
//					// Compose the content for sharing here.
//					String strDesc = aProvider.getDesc(pSelected.mPackageName);
//					if( !TextUtils.isEmpty(strDesc) ) {
//						if(pSelected.mPackageName.equals(SHARE_WEIXNI))
//						{
//							if(apiLevel >=0x21020001 && which==0)
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, strPicUrl,true);
//							else
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, strPicUrl,false);
//						}else{
//							Intent pIntent = new Intent(Intent.ACTION_SEND);
//							pIntent.setPackage(pSelected.mPackageName);
//							pIntent.putExtra(Intent.EXTRA_TEXT, strDesc);
//							pIntent.setType("text/plain");
//							aContext.startActivity(pIntent);
//						}
//					}
//				}
//			}
//		});
//	}
//	
//	
//	public static void shareAppInfo(final Context aContext, final String strTitle, final String strLinkUrl, final int nImgRid, final DescProvider aProvider){
//		if( (null == aContext) || (null == aProvider) ){
//			return ;
//		}
//		
//		Intent pIntent = new Intent(Intent.ACTION_SEND);
//		pIntent.putExtra(Intent.EXTRA_TEXT, "");
//		pIntent.setType("text/plain");
//		
//		PackageManager pManager = aContext.getPackageManager();
//		List<ResolveInfo> aResult = pManager.queryIntentActivities(pIntent, PackageManager.MATCH_DEFAULT_ONLY);
//		final int nSize = (null != aResult ? aResult.size() : 0);
//		if( 0 >= nSize )
//			return ;
//		
//		final List<Sharable> aSharables = new ArrayList<Sharable>(nSize);
//		int mark[] = new int[sharePackages.length];
//		for(int i=0;i<sharePackages.length;i++)
//			mark[i]=0;
//		for(ResolveInfo pInfo : aResult)
//		{
//			ApplicationInfo pAppInfo = pInfo.activityInfo.applicationInfo;
//			Sharable pEntity = new Sharable();
//			pEntity.mLabel = (String) pManager.getApplicationLabel(pAppInfo);
//			pEntity.mIcon = pManager.getApplicationIcon(pAppInfo);
//			pEntity.mPackageName = pInfo.activityInfo.packageName;
//			
//			for(int i=0; i < sharePackages.length; i++)
//			{
//				if(pEntity.mPackageName.equalsIgnoreCase(sharePackages[i]) && mark[i]==0)
//				{
//					mark[i]=1;
//					aSharables.add(pEntity);
//				}
//			}
//		}
//		
//		
//		IWXAPI pWechatApi = WXAPIFactory.createWXAPI(aContext, Config.APP_ID);
//		final int apiLevel =  pWechatApi.getWXAppSupportAPI();
//		if(apiLevel >0) //has weixin
//		{
//			Sharable pEntity = new Sharable();
//			pEntity.mPackageName = SHARE_WEIXNI;
//			pEntity.mLabel = aContext.getString(R.string.weixin_someone);//(String) pManager.getApplicationLabel(pInfo.applicationInfo);
//			pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_weixin);
//			aSharables.add(0,pEntity);
//			if(apiLevel >=0x21020001)
//			{
//				pEntity = new Sharable();
//				pEntity.mPackageName = SHARE_WEIXNI;
//				pEntity.mLabel = aContext.getString(R.string.weixin_circle);
//				pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_time_line_icon);
//				aSharables.add(0,pEntity);
//			}
//		}
//		
//		final Bitmap drawImg = ImageHelper.getResBitmap(aContext, nImgRid);
//		
//		SharableAdapter pAdapter = new SharableAdapter(aContext, aSharables);
//		UiUtils.showListDialog(aContext, aContext.getString(R.string.share_title), pAdapter, new RadioDialog.OnRadioSelectListener() {
//			@Override
//			public void onRadioItemClick(int which) {
//				if( null != aSharables )
//				{
//					Sharable pSelected = aSharables.get(which);
//					// Compose the content for sharing here.
//					String strDesc = aProvider.getDesc(pSelected.mPackageName);
//					if( !TextUtils.isEmpty(strDesc) ) {
//						if(pSelected.mPackageName.equals(SHARE_WEIXNI))
//						{
//							if(apiLevel >=0x21020001 && which==0)
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, drawImg,true,false);
//							else
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, drawImg,false,false);
//						}else{
//							Intent pIntent = new Intent(Intent.ACTION_SEND);
//							pIntent.setPackage(pSelected.mPackageName);
//							pIntent.putExtra(Intent.EXTRA_TEXT, strDesc);
//							pIntent.setType("text/plain");
//							aContext.startActivity(pIntent);
//						}
//					}
//				}
//			}
//		});
//	}
//	
//	
//	/**
//	 * 
//	 * @param aParent
//	 * @return
//	 */
//	public static boolean checkWX(final Activity aParent)
//	{
//		return checkWX(aParent, 0);
//	}
//	/**
//	 * 
//	 * @param aParent
//	 * @return
//	 */
//	public static boolean checkWX(final Activity aParent, int baseApiLevel)
//	{
//		IWXAPI pWechatApi = WXAPIFactory.createWXAPI(aParent, Config.APP_ID);
//		int apiLevel =  pWechatApi.getWXAppSupportAPI();
//		if(apiLevel <= baseApiLevel)
//		{
//			UiUtils.showDialog(aParent, 
//					aParent.getString(R.string.no_support_weixin),
//					aParent.getString(R.string.install_newest_weixin),
//					aParent.getString(R.string.install_weixin_yes),
//					aParent.getString(R.string.btn_cancel),
//					new AppDialog.OnClickListener() {
//
//						@Override
//						public void onDialogClick(int nButtonId) {
//							if(nButtonId == DialogInterface.BUTTON_POSITIVE)
//							{
//								Intent intent = new Intent(Intent. ACTION_VIEW); 
//								intent.setData(Uri.parse("http://weixin.qq.com/"));
//								aParent.startActivity(intent);
//							}
//							
//						}}
//				);
//			
//			return false;
//		}
//		
//		return true;
//	}
//	
//	
//	public static void sendWXLogin(final Activity pActivity){
//		if(!AppUtils.checkWX(pActivity)) // ??????code ??????
//			return;
//			
//		IWXAPI pWechatApi = WXAPIFactory.createWXAPI(pActivity, Config.APP_ID);
//		final SendAuth.Req pReq = new SendAuth.Req();
//		pReq.scope = "snsapi_userinfo";
//		pReq.state = "yixunlogin";
//		
//		pWechatApi.sendReq(pReq);
//	}
//	
//	/**
//	 * 
//	 * @param aParent
//	 * @param aErrcode
//	 */
//	public static void informWXShareResult(final Activity aParent, int aErrcode) {
//		String strInfo= "";
//		
//		 
//		if(aErrcode == BaseResp.ErrCode.ERR_UNSUPPORT)
//		{
//			UiUtils.showDialog(aParent, 
//					aParent.getString(R.string.no_support_weixin),
//					aParent.getString(R.string.install_newest_weixin),
//					aParent.getString(R.string.install_weixin_yes),
//					aParent.getString(R.string.btn_cancel),
//					new AppDialog.OnClickListener() {
//
//						@Override
//						public void onDialogClick(int nButtonId) {
//							if (nButtonId == AppDialog.BUTTON_POSITIVE)
//							{
//								Intent intent = new Intent(Intent. ACTION_VIEW); 
//								intent.setData(Uri.parse("http://weixin.qq.com/"));
//								aParent.startActivity(intent);
//							}
//							
//						}}
//				);
//			
//			return;
//		}
//		
//		switch (aErrcode)
//		{
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			strInfo = "\n" + aParent.getString(R.string.share_fail_title) + "\n\n"
//					+ aParent.getString(R.string.share_auth_denied) +"\n";
//			break;
//		case BaseResp.ErrCode.ERR_SENT_FAILED:
//			strInfo = "\n" + aParent.getString(R.string.share_fail_title) + "\n\n"
//				+ aParent.getString(R.string.share_fail_net) +"\n";
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			strInfo = "\n" + aParent.getString(R.string.share_fail_title) + "\n\n"
//				+ aParent.getString(R.string.share_user_cancel) +"\n";
//			break;
//		case BaseResp.ErrCode.ERR_OK:
//			strInfo = aParent.getString(R.string.share_succ_title);
//			break;
//		}
//		UiUtils.makeToast(aParent, strInfo);
//	}
//	
//	
//	/**
//	 * 
//	 * @param aParent
//	 * @param aErrcode
//	 */
//	public static void informWXLoginResult(final Activity aParent, int aErrcode) {
//		String strInfo= "";
//		 
//		if(aErrcode == BaseResp.ErrCode.ERR_UNSUPPORT)
//		{
//			UiUtils.showDialog(aParent, 
//					aParent.getString(R.string.no_support_weixin),
//					aParent.getString(R.string.install_newest_weixin),
//					aParent.getString(R.string.install_weixin_yes),
//					aParent.getString(R.string.btn_cancel),
//					new AppDialog.OnClickListener() {
//
//						@Override
//						public void onDialogClick(int nButtonId) {
//							if (nButtonId == AppDialog.BUTTON_POSITIVE)
//							{
//								Intent intent = new Intent(Intent. ACTION_VIEW); 
//								intent.setData(Uri.parse("http://weixin.qq.com/"));
//								aParent.startActivity(intent);
//							}
//							
//						}}
//				);
//			
//			return;
//		}
//		
//		switch (aErrcode)
//		{
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			strInfo = "\n" + aParent.getString(R.string.login_fail_title) + "\n\n"
//					+ aParent.getString(R.string.login_auth_denied) +"\n";
//			break;
//		case BaseResp.ErrCode.ERR_SENT_FAILED:
//			strInfo = "\n" + aParent.getString(R.string.login_fail_title) + "\n\n"
//				+ aParent.getString(R.string.login_fail_net) +"\n";
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			strInfo = "\n" + aParent.getString(R.string.login_fail_title) + "\n\n"
//				+ aParent.getString(R.string.login_user_cancel) +"\n";
//			break;
//		default:
//			strInfo = aParent.getString(R.string.login_fail_title);
//			break;
//		}
//		
//		UiUtils.makeToast(aParent, strInfo);
//	}
//	
//	/**
//	 * @param aContext
//	 * @param strTitle
//	 * @param strLinkUrl
//	 * @param strPicUrl
//	 * @param aProvider
//	 */
//	public static void shareSlotInfo(final Context aContext, final String strTitle, final String strLinkUrl, final String strPicUrl, final DescProvider aProvider){
//		if( (null == aContext) || (null == aProvider) ){
//			return ;
//		}
//		
//		IWXAPI pWechatApi = WXAPIFactory.createWXAPI(aContext, Config.APP_ID);
//		int apiLevel =  pWechatApi.getWXAppSupportAPI();
//		if(apiLevel <=0)
//		{
//			return;
//		}
//		//0x21020001
//		//just ???�?�?�? �? �?信�????????
//		/*
//		PackageManager pManager = aContext.getPackageManager();
//		PackageInfo pInfo = null;
//		try 
//		{
//			pInfo = pManager.getPackageInfo("com.tencent.mm", 0);
//		}
//		catch (NameNotFoundException e) 
//		{
//			e.printStackTrace();
//			pInfo = null;
//			return;
//		}*/
//		if(apiLevel <0x21020001)
//		{
//			String strDesc = aProvider.getDesc("com.tencent.mm");
//			AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, strPicUrl,false);
//			return;
//		}
//	
//		final List<Sharable> aSharables = new ArrayList<Sharable>(2);
//		Sharable pEntity = new Sharable();
//		pEntity.mPackageName = "com.tencent.mm";
//		pEntity.mLabel = aContext.getString(R.string.weixin_someone);//(String) pManager.getApplicationLabel(pInfo.applicationInfo);
//		pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_weixin);
//		aSharables.add(pEntity);
//		
//		pEntity = new Sharable();
//		pEntity.mPackageName = "com.tencent.mm";
//		pEntity.mLabel = aContext.getString(R.string.weixin_circle);
//		pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_time_line_icon);
//		aSharables.add(0,pEntity);
//		
//	
//		SharableAdapter pAdapter = new SharableAdapter(aContext, aSharables);
//		UiUtils.showListDialog(aContext, aContext.getString(R.string.share_title), pAdapter, new RadioDialog.OnRadioSelectListener() {
//			@Override
//			public void onRadioItemClick(int which) {
//				if( null != aSharables )
//				{
//					Sharable pSelected = aSharables.get(which);
//					// Compose the content for sharing here.
//					String strDesc = aProvider.getDesc(pSelected.mPackageName);
//					if( !TextUtils.isEmpty(strDesc) ) {
//						if(pSelected.mPackageName.equals("com.tencent.mm"))
//						{
//							if(which==0)//time_line
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, strPicUrl,true);
//							else
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, strPicUrl,false);
//						}
//					}
//				}
//			}
//		});
//	}
//	
//
//	public static void shareSlotInfo(final Context aContext, final String strTitle, final String strLinkUrl, 
//			final int aImgRid, final DescProvider aProvider) {
//		
//		if( (null == aContext) || (null == aProvider) ){
//			return ;
//		}
//		
//		Bitmap drawImg = ImageHelper.getResBitmap(aContext, aImgRid);
//		//Drawable drawImg = BitmapFactory.decodeResource(aImgRid, id);//aContext.getResources().getDrawable(aImgRid);
//		//Bitmap   aImgBM;
//		if(null!=drawImg)
//		{
//			AppUtils.shareSlotInfo(aContext,strTitle,strLinkUrl, drawImg ,aProvider, true);
//		}
//	}
//	/**
//	 * 
//	* method Name:shareSlotInfo    
//	* method Description:  
//	* @param aContext
//	* @param strTitle
//	* @param strLinkUrl
//	* @param aImg
//	* @param aProvider   
//	* void  
//	* @exception   
//	* @since  1.0.0
//	 */
//	public static void shareSlotInfo(final Context aContext, final String strTitle, final String strLinkUrl, 
//			final Bitmap aImg, final DescProvider aProvider, final boolean needRecycle){
//		if( (null == aContext) || (null == aProvider) ){
//			return ;
//		}
//		
//		IWXAPI pWechatApi = WXAPIFactory.createWXAPI(aContext, Config.APP_ID);
//		int apiLevel =  pWechatApi.getWXAppSupportAPI();
//		if(apiLevel <=0)
//		{
//			return;
//		}
//		
//		//just ???�?�?�? �? �?信�????????
//		/*PackageManager pManager = aContext.getPackageManager();
//		//PackageInfo pInfo = null;
//		try 
//		{
//			PackageInfo pInfo = pManager.getPackageInfo("com.tencent.mm", 0);
//		}
//		catch (NameNotFoundException e) 
//		{
//			e.printStackTrace();
//			//pInfo = null;
//			return;
//		}
//		*/
//		//dont support Time_Line 
//		if(apiLevel <0x21020001)
//		{
//			String strDesc = aProvider.getDesc("com.tencent.mm");
//			AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, aImg,false,needRecycle);
//			return;
//		}
//	
//		final List<Sharable> aSharables = new ArrayList<Sharable>(2);
//		Sharable pEntity = new Sharable();
//		pEntity.mPackageName = "com.tencent.mm";
//		pEntity.mLabel = aContext.getString(R.string.weixin_someone);//(String) pManager.getApplicationLabel(pInfo.applicationInfo);
//		pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_weixin);
//		aSharables.add(pEntity);
//		
//		pEntity = new Sharable();
//		pEntity.mPackageName = "com.tencent.mm";
//		pEntity.mLabel = aContext.getString(R.string.weixin_circle);
//		pEntity.mIcon = aContext.getResources().getDrawable(R.drawable.share_to_time_line_icon);
//		aSharables.add(0,pEntity);
//		
//		SharableAdapter pAdapter = new SharableAdapter(aContext, aSharables);
//		UiUtils.showListDialog(aContext, aContext.getString(R.string.share_title), pAdapter, new RadioDialog.OnRadioSelectListener() {
//			@Override
//			public void onRadioItemClick(int which) {
//				if( null != aSharables )
//				{
//					Sharable pSelected = aSharables.get(which);
//					// Compose the content for sharing here.
//					String strDesc = aProvider.getDesc(pSelected.mPackageName);
//					if( !TextUtils.isEmpty(strDesc) ) {
//						if(pSelected.mPackageName.equals("com.tencent.mm"))
//						{
//							if(which==0)//time_line
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, aImg,true, needRecycle);
//							else
//								AppUtils.sendToWX(aContext, strDesc, strTitle, strLinkUrl, aImg,false, needRecycle);
//						}
//					}
//				}
//			}
//		});
//	}
//	
//	
//	private static void sendToWX(final Context aContext, final String strDesc, final String strTitle, 
//			final String strLinkUrl,  final String strPicUrl,
//			final boolean bTimeLineFlag){
//		// Check the bitmap.
//		final ImageLoader pLoader = new ImageLoader(aContext, Config.CHANNEL_PIC_DIR,false);
//		final int nMaxSize = 80;
//		Bitmap pThumb = pLoader.getBitmap(strPicUrl, nMaxSize);
//		if( null == pThumb ) {
//			// Try to download the real image.
//			final ProgressDialog pDialog = AppUtils.showProgressDialog(aContext);
//			pLoader.get(strPicUrl, new ImageLoadListener(){
//				@Override
//				public void onLoaded(Bitmap aBitmap, String strUrl) {
//					// 1. Resize the bitmap.
//					Bitmap pResult = pLoader.resize(aBitmap, nMaxSize);
//					
//					AppUtils.hideProgressDialog(pDialog);
//					
//					// 2. Send request.
//					AppUtils.sendWechatReq(aContext, strDesc, strTitle, strLinkUrl, pResult,bTimeLineFlag,true);
//					pLoader.cleanup();
//				}
//
//				@Override
//				public void onError(String strUrl) {
//					Bitmap pDefault = ImageHelper.getResBitmap(aContext, R.drawable.i_global_loading);
//					AppUtils.hideProgressDialog(pDialog);
//					
//					AppUtils.sendWechatReq(aContext, strDesc, strTitle, strLinkUrl, pDefault,bTimeLineFlag,true);
//					
//					pLoader.cleanup();
//				}
//			});
//			
//		} else {
//			AppUtils.sendWechatReq(aContext, strDesc, strTitle, strLinkUrl, pThumb,bTimeLineFlag,true);
//			pLoader.cleanup();
//		}
//	}
//	
//	private static void sendToWX(final Context aContext, final String strDesc, final String strTitle, 
//			final String strLinkUrl,  final Bitmap aImg, final boolean bTimeLineFlag, boolean needRecycle){
//		// Check the bitmap.
//		if(null == aImg)
//		{
//			UiUtils.makeToast(aContext,R.string.wx_share_thumb_fail);
//			return;
//		}
//		  
//		//final int nMaxSize = 80;
//		//Bitmap pThumb =  ImageHelper.resizeBitmap(aImg, nMaxSize,false);
//		AppUtils.sendWechatReq(aContext, strDesc, strTitle, strLinkUrl, aImg,bTimeLineFlag,needRecycle);
//	}
//	
//	/**
//	 * Send wechat share content with bitmap.
//	 * @param aContext
//	 * @param strDesc
//	 * @param strTitle
//	 * @param strLinkUrl
//	 * @param aThumb
//	 */
//	private static void sendWechatReq(Context aContext, String strDesc, String strTitle, String strLinkUrl, Bitmap aThumb,
//			boolean bTimeLineFlag, boolean needRecycle) {
//		if( null == aThumb )
//			return ;
//		
//		WXWebpageObject webpage = new WXWebpageObject();
//		webpage.webpageUrl = strLinkUrl;
//		WXMediaMessage msg = new WXMediaMessage(webpage);
//		msg.title = strTitle;
//		msg.description = strDesc;
//		msg.thumbData = ToolUtil.bmpToByteArray(aThumb, needRecycle);
//		
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = String.valueOf(System.currentTimeMillis()); // transaction�ֶ�����Ψһ��ʶһ������
//		req.message = msg;
//		if(bTimeLineFlag)
//			req.scene = SendMessageToWX.Req.WXSceneTimeline;
//		else
//			req.scene = SendMessageToWX.Req.WXSceneSession;
//		// Create wechat api.
//		IWXAPI pWechatApi = WXAPIFactory.createWXAPI(aContext, Config.APP_ID);
//		if( null != pWechatApi ) {
//			pWechatApi.sendReq(req);
//		}
//
//		pWechatApi = null;
//	}
//	
//	private static ProgressDialog showProgressDialog(Context aContext) {
//		ProgressDialog pProgressDialog = new ProgressDialog(aContext);
//		pProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置�???�为???形�??�????
//		
//		String strTitle = aContext.getString(R.string.share_loading);
//
//		pProgressDialog.setTitle(strTitle);
//
//		// mProgressDialog.setIcon(R.drawable.icon);//设置??��??
//		pProgressDialog.setMessage(aContext.getString(R.string.initializing_content));
//
//		pProgressDialog.setIndeterminate(true);// 设置�?�???��?????为�?????�?
//		pProgressDialog.setCancelable(false);// 设置�?�???��????????以�??????????????�?
//		pProgressDialog.show();
//		
//		return pProgressDialog;
//	}
//	
//	private static void hideProgressDialog(ProgressDialog aDialog) {
//		if( null != aDialog )
//		{
//			aDialog.cancel();
//			aDialog = null;
//		}
//	}
//	
//	////////////////////////////////////////////////////////////////////////////////
//	/**
//	* Implementation for Sharable list
//	*
//	*/
//	public static class Sharable
//	{
//		public String   mLabel;
//		public String   mPackageName;
//		public Drawable mIcon;
//	}
//	
//	private static class SharableHolder
//	{
//		public ImageView  mIcon;
//		public TextView   mLabel;
//	}
//	
//	public static class SharableAdapter extends RadioDialog.RadioAdapter
//	{
//		public SharableAdapter(Context aContext, List<Sharable> aSharables)
//		{
//			super(aContext);
//			mSharables = aSharables;
//		}
//		
//		@Override
//		public int getCount() 
//		{
//			return (null != mSharables ? mSharables.size() : 0);
//		}
//		
//		@Override
//		public Object getItem(int position) 
//		{
//			return null;
//		}
//	
//		@Override
//		public long getItemId(int position) 
//		{
//			return 0;
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) 
//		{
//			SharableHolder holder = null;
//			if (null == convertView)
//			{
//				convertView = View.inflate(mContext, R.layout.share_item, null);
//				holder = new SharableHolder();
//				holder.mIcon = (ImageView) convertView.findViewById(R.id.item_icon);
//				holder.mLabel = (TextView) convertView.findViewById(R.id.item_label);
//				convertView.setTag(holder);
//			}
//			else
//			{
//				holder = (SharableHolder) convertView.getTag();
//			}
//		
//			// set data
//			if(null != mSharables)
//			{
//				Sharable pEntity = mSharables.get(position);
//				holder.mLabel.setText(pEntity.mLabel);
//				holder.mIcon.setImageDrawable(pEntity.mIcon);
//			}
//			return convertView;
//		}
//	
//		@Override
//		public int getItemViewType(int position) {
//			return 0;
//		}
//		
//		@Override
//		public int getViewTypeCount() {
//			return 1;
//		}
//		
//		@Override
//		public boolean hasStableIds() {
//			return false;
//		}
//	
//		@Override
//		public boolean isEmpty() {
//			return false;
//		}
//		
//		@Override
//		public void registerDataSetObserver(DataSetObserver observer) {
//		
//		}
//		
//		@Override
//		public void unregisterDataSetObserver(DataSetObserver observer) {
//		
//		}
//	
//		@Override
//		public boolean areAllItemsEnabled() {
//			return true;
//		}
//		
//		@Override
//		public boolean isEnabled(int position) {
//			return true;
//		}
//	
//		private List<Sharable>          mSharables;
//	}
//	
//	/**
//	 * Instance of AppUtils is forbidden
//	 */
//	private AppUtils() {
//	}
//
//
//	public static boolean checkAndCall(final Context aContext, final Intent intent) {
//		if(aContext == null || null == intent)
//			return false;
//		
//		if(null != intent.resolveActivity(aContext.getPackageManager())) {
//			
//			if(Preference.getInstance().needCallAccess())
//			{
//				UiUtils.showDialog(aContext,
//					R.string.permission_title, R.string.permission_hint_call,R.string.permission_agree, R.string.permission_disagree,
//					new AppDialog.OnClickListener() {
//					@Override
//					public void onDialogClick(int nButtonId) {
//						if (nButtonId == AppDialog.BUTTON_POSITIVE)
//						{
//							Preference.getInstance().setCallAccess(Preference.ACCESSED);
//							aContext.startActivity(intent);
//						}
//					}
//				});
//			}
//			else
//			{
//				aContext.startActivity(intent);
//			}
//		}else
//		{
//			UiUtils.makeToast(aContext, R.string.phone_app_not_found);
//		}
//		
//		return true;
//		
//	}
//
//	public  static String getConstellation(Context context,Calendar cad) {
//		
//		if(null==cad)
//			return "";
//		int month = cad.get(Calendar.MONTH) + 1;
//		int date = cad.get(Calendar.DATE);
//		switch (month)
//		{
//		case 1:
//			return context.getString(date < 20 ? R.string.capricorn : R.string.aquarius);
//		case 2:
//			return context.getString(date < 19 ? R.string.aquarius : R.string.pisces);
//		case 3:
//			return context.getString(date < 21 ? R.string.pisces : R.string.aries);
//		case 4:
//			return context.getString(date < 21 ? R.string.aries : R.string.taurus);
//		case 5:
//			return context.getString(date < 21 ? R.string.taurus : R.string.gemini);
//		case 6:
//			return context.getString(date < 22 ? R.string.gemini : R.string.cancer);
//		case 7:
//			return context.getString(date < 23 ? R.string.cancer : R.string.leo);
//		case 8:
//			return context.getString(date < 23 ? R.string.leo : R.string.virgo);
//		case 9:
//			return context.getString(date < 23 ? R.string.virgo : R.string.libra);
//		case 10:
//			return context.getString(date < 24 ? R.string.libra : R.string.scorpio);
//		case 11:
//			return context.getString(date < 22 ? R.string.scorpio : R.string.sagittarius);
//		case 12:
//			return context.getString(date < 22 ? R.string.sagittarius : R.string.capricorn);
//		default:
//			return "";
//		}
//	}
//
//	
//	public static String getAnimalZodiac(Context context,Calendar cad)
//	{
//		int year = cad.get(Calendar.YEAR);
//		int mod = (year - 1900)%12;
//		switch (mod)
//		{
//		case 0:
//			return context.getString(R.string.animal_1);
//		case 1:
//			return context.getString(R.string.animal_2);
//		case 2:
//			return context.getString(R.string.animal_3);
//		case 3:
//			return context.getString(R.string.animal_4);
//		case 4:
//			return context.getString(R.string.animal_5);
//		case 5:
//			return context.getString(R.string.animal_6);
//		case 6:
//			return context.getString(R.string.animal_7);
//		case 7:
//			return context.getString(R.string.animal_8);
//		case 8:
//			return context.getString(R.string.animal_9);
//		case 9:
//			return context.getString(R.string.animal_10);
//		case 10:
//			return context.getString(R.string.animal_11);
//		case 11:
//			return context.getString(R.string.animal_12);
//		default:
//			return "";
//		}
//	}
//}
