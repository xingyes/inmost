package com.xingy.util.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.xingy.R;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.NavigationBar;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.MyApplication;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnErrorListener;
import com.xingy.util.ajax.Parser;
import com.xingy.util.ajax.Response;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class BaseActivity extends FragmentActivity implements OnErrorListener, OnClickListener {
//	private HashMap<Integer, LoadingSwitcher> mLoadingSwitchers;
	private SparseArray<LoadingSwitcher> mLoadingSwitchers;
	private boolean   beenSeen;
	private Dialog    errorDialog;
	public String     reportExtraInfo;
	public String     reportPid;
	public boolean 	  isReportPV = true;
	private long      backTimemark = -1;
	private Handler   backHandler;
	private static final int MSG_RESET_BACKTIME = 10101;
	public boolean isBeenSeen()
	{
		return beenSeen;
	}
	
	public BaseActivity() {
		reportExtraInfo = "";
		reportPid = "";
		this.destroyListenerList = new ArrayList<DestroyListener>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.addActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onRestart();
		beenSeen = true;
		
		super.onResume();
   }
	
	
	@Override
	protected void onPause() {
		super.onPause();
		beenSeen = false;
		UiUtils.cancelToast();
		
		closeProgressLayer();
		
   }
	
	/*
	@Override
	public void setContentView (int layoutResID) {
		setContentView(layoutResID, true);
	}
	
	public void setContentView(int layoutResID, boolean enableTheme) {
		super.setContentView(layoutResID);

		//if( enableTheme ) {
			// Update background configuration.
		//	this.setThemeConfig();
		//}
	}
	*/
	
	protected void loadNavBar(int nViewId) {
		loadNavBar(nViewId, 0);
	}
	
	protected void loadNavBar(int nViewId, int nTextId) {
		initNavBar(nViewId);
		
		if( (null != mNavBar) && (nTextId > 0) ) {
			mNavBar.setText(nTextId);
		}
	}
	
	protected void loadNavBar(int nViewId, String strText) {
		initNavBar(nViewId);
		
		if( null != mNavBar )
			mNavBar.setText(strText);
	}
	
	/**
	 * init nav bar
	 * @param nViewId
	 */
	private void initNavBar(int nViewId) {
		if( (null == mNavBar) && (nViewId > 0) ) {
			mNavBar = (NavigationBar)findViewById(nViewId);
			
			mNavBar.setOnLeftButtonClickListener(new NavigationBar.OnLeftButtonClickListener() {
				@Override
				public void onClick() {
					onBackPressed();
				}
			});
		}
	}
	
	public void setNavBarText(int nResId) {
		setNavBarText(getString(nResId));
	}
	
	public void setNavBarText(String strText) {
		if( null != mNavBar ) {
			mNavBar.setText(strText);
		}
	}
	
	public void setNavBarRightVisibility(int  pVisibilit){
		if( null != mNavBar ) {
			mNavBar.setRightVisibility(pVisibilit);
		}
	}
	
	public void setNavBarRightText(int nResId) {
		setNavBarRightText(getString(nResId));
	}
	
	public void setNavBarRightText(String strText){
		setNavBarRightText(strText, null);
	}
	
	public void setNavBarRightText(String strText, Drawable pDrawable){
		if( null != mNavBar){
			mNavBar.setRightText(strText, pDrawable);
		}
	}
	
	public void showProgressLayer(String title, String message) {
		closeProgressLayer();
		
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置�???�为???形�??�????

		if (title != null) {
			mProgressDialog.setTitle("???�?");// 设置???�?
		}

		// mProgressDialog.setIcon(R.drawable.icon);//设置??��??
		if (message != null) {
			mProgressDialog.setMessage(message);
		}

		mProgressDialog.setIndeterminate(true);// 设置�?�???��?????为�?????�?
		mProgressDialog.setCancelable(true);// 设置�?�???��????????以�??????????????�?
		if(!isBeenSeen())
			return;
		
		mProgressDialog.show();
	}

	public void showProgressLayer() {
		showProgressLayer(null, getString(R.string.wait_for_submit));
	}

	public void showProgressLayer(String message) {
		showProgressLayer(null, message);
	}

	private class LoadingSwitcher {
		View contentContainer;
		View loadingContainer;
	}

	public void setLoadingSwitcher(int id, View contentContainer, View loadingContainer) {
		if (mLoadingSwitchers == null) {
			mLoadingSwitchers = new SparseArray<LoadingSwitcher>();
		}

		LoadingSwitcher switcher = new LoadingSwitcher();
		switcher.contentContainer = contentContainer;
		switcher.loadingContainer = loadingContainer;
		mLoadingSwitchers.put(id, switcher);
	}

	public void setDefaultBodyContainer(View view) {
		mDefaultBodyContainer = view;
	}

	public void setDefaultLoadingContainer(View view) {
		mDefaultLoadingContainer = view;
	}

	public void showLoadingLayer(int id, boolean hideContent) {
		if (mLoadingSwitchers == null) {
			setLoadingSwitcher(id, mDefaultBodyContainer,
					mDefaultLoadingContainer == null ? findViewById(R.id.global_loading) : mDefaultLoadingContainer);
		}
		LoadingSwitcher switcher = mLoadingSwitchers.get(id);
		if (switcher != null) {
			if (switcher.loadingContainer != null) {
				switcher.loadingContainer.setVisibility(View.VISIBLE);
				AnimationDrawable ad = (AnimationDrawable)findViewById(R.id.myprogressBar).getBackground();
				ad.start();
			}
			if (switcher.contentContainer != null) {
				switcher.contentContainer.setVisibility(hideContent ? View.GONE : View.VISIBLE);
				AnimationDrawable ad = (AnimationDrawable)findViewById(R.id.myprogressBar).getBackground();
				ad.start();
			}
		}
	}

	public void showLoadingLayer(int id) {
		showLoadingLayer(id, true);
	}

	public void showLoadingLayer(boolean hideContent) {
		showLoadingLayer(LOADING_SWITCHER_FLAG_DEFAULT, hideContent);
	}

	public void showLoadingLayer() {
		showLoadingLayer(LOADING_SWITCHER_FLAG_DEFAULT, true);
	}

	public void closeLoadingLayer(int id, boolean hideContent) {
		if (mLoadingSwitchers == null)
			return;
		
		final int nKey = mLoadingSwitchers.indexOfKey(id) >= 0 ? id : LOADING_SWITCHER_FLAG_DEFAULT;
		LoadingSwitcher switcher = mLoadingSwitchers.get(nKey);
		if (switcher != null) {
			if (switcher.loadingContainer != null) {
				switcher.loadingContainer.setVisibility(View.GONE);
			}

			if (switcher.contentContainer != null) {
				switcher.contentContainer.setVisibility(hideContent ? View.GONE : View.VISIBLE);
			}
		}
	}

	public void closeLoadingLayer(int id) {
		closeLoadingLayer(id, false);
	}

	public void closeLoadingLayer(boolean hidenContent) {
		closeLoadingLayer(LOADING_SWITCHER_FLAG_DEFAULT, hidenContent);
	}

	public void closeLoadingLayer() {
		closeLoadingLayer(LOADING_SWITCHER_FLAG_DEFAULT, false);
	}

	public void closeProgressLayer() {
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
			mProgressDialog = null;
		}
	}

	public void addDestroyListener(DestroyListener destroyListener) {
		if (this.destroyListenerList == null)
			return;

		this.destroyListenerList.add(destroyListener);
	}

	public void addPauseListener(PauseListener paramPauseListener) {
		if (this.pauseListenerList == null) {
			this.pauseListenerList = new ArrayList<BaseActivity.PauseListener>();
		}
		this.pauseListenerList.add(paramPauseListener);
	}

	public void addResumeListener(ResumeListener paramResumeListener) {
		if (this.resumeListenerList == null) {
			this.resumeListenerList = new ArrayList<BaseActivity.ResumeListener>();
		}

		this.resumeListenerList.add(paramResumeListener);
	}

	public void addAjax(Ajax ajax) {
		if (null == mAjaxs) {
			mAjaxs = new ArrayList<Ajax>();
		}

		mAjaxs.add(ajax);
	}
	
	protected Ajax getAjax(int nPos)
	{
		if( (null == mAjaxs) || (0 > nPos) )
			return null;
		final int nSize = mAjaxs.size();
		return (nPos < nSize ? mAjaxs.get(nPos) : null);
	}

    @Override
	protected void onDestroy() {
		
		
		mLoadingSwitchers = null;

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}

		if (null != this.destroyListenerList) {
			Iterator<DestroyListener> destroyListenerIterator = this.destroyListenerList.iterator();
			while (destroyListenerIterator.hasNext()) {
				destroyListenerIterator.next().onDestroy();
			}

			destroyListenerIterator = null;
			destroyListenerList = null;

			cleanAllAjaxs();
			
		}
		
		MyApplication.removeActivity(this);
		
		super.onDestroy();
	}

	public void cleanAllAjaxs()
	{
		if (mAjaxs != null) {
			for (Ajax ajax : mAjaxs) {
				if (ajax != null) {
					ajax.abort();
					ajax = null;
				}
			}
			mAjaxs.clear();
			mAjaxs = null;
		}
	}
	
	public interface DestroyListener {
		public void onDestroy();
	}

	public interface PauseListener {
		public void onPause();
	}

	public interface ResumeListener {
		public void onResume();
	}

	@Override
	public void onError(final Ajax ajax, final Response response) {
		this.onError(ajax, response, null);
	}
	
	/**
	 * @param ajax
	 * @param aResponse
	 * @param strErrMsg
	 */
	public void onError(final Ajax ajax, final Response aResponse, String strErrMsg)
	{
		boolean isPostOperation = mProgressDialog != null;
		closeProgressLayer();

		if (ajax == null)
			return;

		@SuppressWarnings("rawtypes")
		Parser pParser = ajax.getParser();
		String pErrMsg = null == pParser ? getString(R.string.network_error_info) : pParser.getErrMsg();
		
		String strToastMsg = getString(R.string.title_network_error);
		if (isPostOperation) {
			UiUtils.makeToast(this, strToastMsg);
			return;
		}
		
		//make sure this Activity is seend.Otherwise WindowManager$BadTokenException
		if(!isBeenSeen())
			return;
		
		String strMessage = TextUtils.isEmpty(strErrMsg) ? (TextUtils.isEmpty(pErrMsg) ? this.getString(R.string.network_error_info): pErrMsg) : strErrMsg;
		String strTitle = getString(R.string.network_error);
		if(null!=errorDialog && errorDialog.isShowing())
		{
			errorDialog.dismiss();
		}
		
		errorDialog = UiUtils.showDialog(this, strTitle, strMessage, R.string.btn_retry, R.string.btn_exit, new AppDialog.OnClickListener() {
			@Override
			public void onDialogClick(int nButtonId) {
				if (nButtonId == AppDialog.BUTTON_POSITIVE) {
					ajax.send();
				} else {
					closeLoadingLayer(aResponse.getId(), true);
					onErrorDialogCacneled(ajax, aResponse);
//					MyApplication.exitall();
				}
			}
			});
		errorDialog.setCancelable(false);
		errorDialog.show();
		
		
	}
	
	public void onErrorDialogCacneled(final Ajax ajax, final Response response) {
	}

	@Override
	public void onClick(View v) {
	}

	public boolean registerBottomMenu() {
		//return true;
		return false;
	}

	public boolean isShowSearchPanel() {
		return true;
	}

	
	/**
	 *  to do that. Because of sdk_int >= 11 onBackPressed() will mFragments.popBackStackImmediate() and call checkStateLoss() and dispatchStop()
	 *  set mStateSaved = true, which will cause onSaveinstanceState() illegalStateException
	 *  
	 *  Another way to solve this problem
	 *  1. onSaveinstanceState just don't call super.onSaveinstanceState()  -->some view state will lost
	 *  2.onSaveinstanceState(Bundle outState)
	 *  {
	 *  	super.onSaveinstanceState(outState);
	 *  	invokeFragmentManagerNoteStateNotSaved();
	 *  }
	 *  
	 *  // to set mStateSaved = false in public void noteStateNotSaved()
	 *  private void invokeFragmentManagerNoteStateNotSaved()
	 *  {
	 *  	if(Build.VERSION.SDK_INT <11)
	 *  		return;
	 *  
	 *  	try
	 *  	{
	 *  		Class cls = getClass();
	 *  		do{
	 *  			cls = cls.getSuperclass();
	 *  		}while(!"Activity".equals(cls.getSimpleName()));
	 *  
	 *  		Field fragmentMgrField = cls.getDeclaredField("mFragments");
	 *  		fragmentMgrField.setAccessible(true);
	 *  
	 *  		Object fragmentMgr = fragmentMgrField.get(this);
	 *     		cls = fragmentMgr.getClass();
	 *     
	 *      	Method noteStateNotSavedMethod = cls.getDeclaredMethod("noteStateNotSaved",
	 *      				new Class[]{ noteStateNotSavedMethod.invoke(fragmentMgr, new Object[]{}};
	 *      
	 *      	Log.d("DLoutState", "SUcc call for noteStateNotSaved");
	 *      }catch(Exception ex)
	 *      {
	 *      } 
	 *}
	 */
	@Override
	public void onBackPressed()
	{
//		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//		int x = am.getRunningTasks(1).get(0).numActivities;
//		if(x==1)
//		{
//			long cur = System.currentTimeMillis();
//			if(backTimemark < 0)
//			{
//				backTimemark = cur;
//				if(null == backHandler)
//				{
//					backHandler = new Handler(){
//						public void handleMessage(Message msg)
//						{
//							if(msg.what == MSG_RESET_BACKTIME)
//								backTimemark = -1;
//							else
//								super.handleMessage(msg);
//						}
//					};
//				}
//				backHandler.sendEmptyMessageDelayed(MSG_RESET_BACKTIME, 1000);
//				Toast.makeText(this, R.string.back_ag_to_exit, Toast.LENGTH_SHORT).show();
//			}
//			else if(cur - backTimemark < 1000)
//				finish();
//		}
//		else
			finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	protected final static String LOG_TAG = BaseActivity.class.getName();
	public static final int LOADING_SWITCHER_FLAG_DEFAULT = 0;
	private ArrayList<DestroyListener> destroyListenerList;
	private ArrayList<PauseListener> pauseListenerList;
	private ArrayList<ResumeListener> resumeListenerList;
	private ArrayList<Ajax> mAjaxs;
	private ProgressDialog mProgressDialog;
	private View mDefaultBodyContainer;
	private View mDefaultLoadingContainer;
	protected NavigationBar mNavBar;



}
