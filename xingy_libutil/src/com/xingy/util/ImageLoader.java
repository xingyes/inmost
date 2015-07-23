package com.xingy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.xingy.R;
import com.xingy.util.activity.BaseActivity.DestroyListener;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnErrorListener;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Vector;

public class ImageLoader implements DestroyListener, OnSuccessListener<Bitmap>, OnErrorListener
{	
	

	/**
	 * ImageLoader
	 * @param aContext
	 * @param bCheckMode
	 */
	public ImageLoader(Context aContext)
	{
		this(aContext, "", false);
	}
	
	/**
	 * @param aContext
	 * @param strCacheDir
	 * @param bCheckMode
	 */
	public ImageLoader(Context aContext, String strCacheDir)
	{
		this(aContext, strCacheDir, false);
	}
	
	/**
	 * Default constructor of ImageLoader
	 */
	public ImageLoader(Context aContext, String strCacheDir, boolean bRemoveOnExit) 
	{
		mContext = new WeakReference<Context>(aContext);
		mCachePath = this.createPath(strCacheDir);
		mRemoveOnExit = bRemoveOnExit;
		mMaxCache = DEFAULT_MAX_CACHE;
		mMemCache = new Vector<CacheEntity>(mMaxCache);
		mRequests = new Vector<RequestEntity>();
		mNameMap = new HashMap<String, String>();
		mEmptyMap = new HashMap<String, String>();
		mCurrent = null;
		mAjax = null;
		mTask = null;
	}
	
	/**
	 * setMaxCache
	 * @param nMaxCache
	 */
	public void setMaxCache(int nMaxCache)
	{
		if( 0 >= nMaxCache )
			return ;
		
		if( nMaxCache > DEFAULT_MAX_CACHE )
			nMaxCache = DEFAULT_MAX_CACHE;
		
		if( nMaxCache == mMaxCache )
			return ;
		
		this.clearCache();
		mMaxCache = nMaxCache;
		mMemCache = new Vector<CacheEntity>(mMaxCache);
	}
	
	/**
	 * getLoadingId
	 * @return
	 */
	public int getLoadingId()
	{
		return R.drawable.i_global_loading;
	}
	
	public Bitmap getLoadingBitmap(Context pContext){
		if(null == pContext) {
			return null;
		}
		
		Bitmap pBitmap = ImageHelper.getResBitmap(pContext, R.drawable.i_global_loading);
		
		return pBitmap;
	}
	
	public Bitmap getBitmap(String strUrl, int nMaxSize)
	{
		if( TextUtils.isEmpty(strUrl) )
			return null;
		
		// Check whether is in request now.
		if( (null != mCurrent) && (strUrl.equals(mCurrent.mUrl)) )
			return null;
		
		// Check whether is in empty item.
		if( null != mEmptyMap.get(strUrl) )
		{
			return null;
		}
		
		// Check memory first.
		Bitmap pBitmap = this.checkMem(strUrl);
		if( null != pBitmap )
		{
			return pBitmap;
		}
		
		String strFileName = this.getFileName(strUrl);
		if( TextUtils.isEmpty(strFileName) )
			return null;
		
		String strFullPath = mRoot + strFileName;
		File pFile = new File(strFullPath);
		if( null == pFile || !pFile.exists() )
			return null;
		
		if(null==opt)
		{
			opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;  
			opt.inInputShareable = true;
		}
		// Try to decode bitmap from local file.
		try {
			pBitmap = BitmapFactory.decodeFile(strFullPath,opt);
		}catch (OutOfMemoryError aException) {
			aException.printStackTrace();
			System.gc();
			pBitmap = null;
		}
		
		// Try to resize.
		return resize(pBitmap, nMaxSize);
	}
	
	public static Bitmap resize(Bitmap aBitmap, int nMaxSize) {
		if( (null != aBitmap) && (nMaxSize > 0) ){
			final int nWidth = aBitmap.getWidth();
			final int nHeight = aBitmap.getHeight();
			final int nCurrent = Math.max(nWidth, nHeight);
			if( nCurrent > nMaxSize ){
				// Calculate the new size.
				final int nOther = Math.min(nWidth, nHeight);
				final int nMinSize = nOther * nMaxSize / nCurrent;
				
				final boolean bHorizontal = nWidth > nHeight;
				final int nNewWidth = bHorizontal ? nMaxSize : nMinSize;
				final int nNewHeight = bHorizontal ? nMinSize : nMaxSize;
				
				// scale the bitmap.
				Bitmap pTarget = Bitmap.createScaledBitmap(aBitmap, nNewWidth, nNewHeight, true);
				if( null != pTarget ) {
//					aBitmap.recycle();
					aBitmap = pTarget;
				}
			}
		}
		
		return aBitmap;
	}
	
	/**
	 * getUrl
	 * @param strUrl
	 */
	public Bitmap get(String strUrl)
	{
		return this.get(strUrl, null);
	}
	
	/**
	 * get
	 * @param strUrl
	 * @param aListener
	 */
	public Bitmap get(String strUrl, ImageLoadListener aListener)
	{
		if( TextUtils.isEmpty(strUrl) )
		{
			return null;
		}
		
		// Check whether is in request now.
		if( (null != mCurrent) && (strUrl.equals(mCurrent.mUrl)) )
		{
			mCurrent.mListener = aListener;
			return null;
		}
		
		// Check whether is in empty item.
		if( null != mEmptyMap.get(strUrl) )
		{
			Bitmap pEmpty = getEmptyBitmap(false);
			if( null != aListener )
			{
				aListener.onLoaded(pEmpty, strUrl);
			}
			
			return pEmpty;
		}
		
		// Check memory first.
		Bitmap pBitmap = this.checkMem(strUrl);
		if( null != pBitmap )
		{
			if( null != aListener )
			{
				aListener.onLoaded(pBitmap, strUrl);
			}
			
			return pBitmap;
		}
		
		// Check cache in local file system.
		if( TextUtils.isEmpty(mCachePath) )
		{
			return checkMode(strUrl, aListener);
		}
		
		final boolean bRequesting = this.checkFile(strUrl, aListener);
		if( !bRequesting )
		{
			return checkMode(strUrl, aListener);
		}
		
		return null;
	}
	
	/**
	 * isEmptyBitmap
	 * @param strUrl
	 * @return
	 */
	public boolean isEmptyBitmap(String strUrl) 
	{
		return null != mEmptyMap.get(strUrl);
	}
	
	/**
	 * checkMode
	 * @param strUrl
	 * @param aListener
	 * @return
	 */
	private Bitmap checkMode(String strUrl, ImageLoadListener aListener)
	{	
		// Default, we should request for network requesting.
		if( null != aListener )
		{
			// Send request for image from network.
			this.appendRequest(strUrl, aListener, false);
		}
		
		return null;
	}
	
	/**
	 * cleanup
	 * Clean up the memory and content to release resources.
	 */
	public void cleanup()
	{
		if( null != mAjax )
		{
			mAjax.abort();
			mAjax = null;
		}
		
		if( null != mTask )
		{
			mTask.cancel(true);
			mTask = null;
		}
		
		if( null != mRequests )
		{
			mRequests.clear();
			mRequests = null;
		}
		
		mCurrent = null;
		
		// Clear the memory cache.
		this.clearCache();
		
		// Clean up the array.
		if( null != mNameMap )
		{
			mNameMap.clear();
			mNameMap = null;
		}
		
		if( null != mEmptyMap )
		{
			mEmptyMap.clear();
			mEmptyMap = null;
		}
		
		// Remove folder.
		if( !TextUtils.isEmpty(mCachePath) && mRemoveOnExit )
		{
			this.removeFolder(mCachePath);
		}
		
		if( null != mEmpty )
		{
			Bitmap pEmpty = mEmpty.get();
			if( null != pEmpty && !pEmpty.isRecycled() )
			{
				pEmpty.recycle();
			}
			pEmpty = null;
			mEmpty.clear();
			mEmpty = null;
		}
		
		if( null != mDefault )
		{
			Bitmap pDefault = mDefault.get();
			if( null != pDefault && !pDefault.isRecycled() )
			{
				pDefault.recycle();
			}
			pDefault = null;
			mDefault.clear();
			mDefault = null;
		}
		
		if( null != mContext )
		{
			mContext.clear();
			mContext = null;
		}
	}
	
	/**
	 * clearCache
	 */
	private void clearCache()
	{
		if( null != mMemCache )
		{
			int nSize = mMemCache.size();
			for( int nIdx = 0; nIdx < nSize; nIdx++ )
			{
				CacheEntity pEntity = mMemCache.elementAt(nIdx);
				pEntity.cleanup();
			}
			
			mMemCache.clear();
			mMemCache = null;
		}
	}

	@Override
	public void onDestroy() 
	{
		this.cleanup();
	}

	@Override
	public void onError(Ajax ajax, Response response) 
	{
		if(null!=mAjax)
		{
			mAjax.abort();
			mAjax = null;
		}
		
		if( null != mCurrent )
		{
			String strUrl = mCurrent.mUrl;
			ImageLoadListener pListener = mCurrent.mListener;
			mCurrent = null;
			
			// Notify the listener.
			if(null!=pListener)
				pListener.onError(strUrl);
			
			// Save to empty item.
			if(null!=mEmpty)
				mEmptyMap.put(strUrl, "1");
		}
		
		// Process for next request.
		this.processRequest();		
	}

	@Override
	public void onSuccess(Bitmap aBitmap, Response aResponse)
	{
		this.onNewResult(aBitmap, true);
		
	}
	
	/**
	 * onNewResult
	 * @param aBitmap
	 */
	private synchronized void onNewResult(Bitmap aBitmap, boolean bFromNetwork)
	{
		if( null != mCurrent )
		{
			String strUrl = mCurrent.mUrl;
			ImageLoadListener pListener = mCurrent.mListener;
			mCurrent = null;
			
			// Process the data.
			if( (null != aBitmap) && (!aBitmap.isRecycled()) )
			{
				// Save the bitmap to request.
				this.addMemCache(strUrl, aBitmap);
				
				if( bFromNetwork )
				{
					// Save image to local storage.
					this.save2Local(strUrl, aBitmap);
				}
					
				// Notify the listener.
				if(null!=pListener)
					pListener.onLoaded(aBitmap, strUrl);
			}
			else
			{
				// Notify the listener.
				if(null!=pListener)
					pListener.onError(strUrl);
			//	.onLoaded(getEmptyBitmap(false), strUrl);
			}
		}
		
		if(null!=mAjax)
		{
			mAjax.abort();
			mAjax = null;
		}
		
		// Process for next request.
		this.processRequest();
	}
	
	/**
	 * appendRequest
	 * @param strUrl
	 * @param aListener
	 */
	private boolean appendRequest(String strUrl, ImageLoadListener aListener, boolean bLocal)
	{
		if( (TextUtils.isEmpty(strUrl)) || (null == aListener) || (null == mRequests) )
			return false;
		
		// Check whether previous instance already exits in request queue.
		final int nSize = (null != mRequests ? mRequests.size() : 0);
		for( int nIdx = 0; nIdx < nSize; nIdx++ )
		{
			RequestEntity pRequest = mRequests.elementAt(nIdx);
			if( (null != pRequest) && (pRequest.mUrl.equals(strUrl)) )
			{
				pRequest.mListener = aListener;
				// Already exits.
				return false;
			}
		}
		
		RequestEntity pRequest = new RequestEntity(strUrl, aListener, bLocal);
		mRequests.add(pRequest);
		
		// Process request.
		this.processRequest();
		return true;
	}
	
	private Object mSyncObj = new Object();
	private static final String TAG = "ImageLoader";
	private boolean mIsPaused;
	public void pauseDecode() {

		synchronized (mSyncObj) {
			Log.e(TAG, "[pauseDecode] +");
			mIsPaused = true;
			Log.e(TAG, "[pauseDecode] -");
		}
	}
	
	public void resumeDecode() {

		synchronized (mSyncObj) {
			Log.e(TAG, "[resumeDecode] +");
			mIsPaused = false;
			mSyncObj.notify();
			Log.e(TAG, "[resumeDecode] -");
		}
	}
	
	private void checkStatus() {
		synchronized (mSyncObj) {
			if(mIsPaused) {
				try {
					mSyncObj.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * processRequest
	 * @return
	 */
	private synchronized boolean processRequest()
	{
		// Pick up the top instance.
		if( (null == mRequests) || (0 >= mRequests.size()) || (null != mCurrent) )
			return false;
		
		
		// Pick up the first item.
		mCurrent = mRequests.remove(0);
		mTask = null;
		if( mCurrent.mLocal )
		{
			mTask = new AsyncTask<String, Void, Bitmap>()
			{
				@Override
				protected Bitmap doInBackground(String... aParams) 
				{
					String strUrl = aParams[0];
					String strFileName = getFileName(strUrl);
					String strFullPath = mRoot + strFileName;
					Bitmap pBitmap = null;
					if(null==opt)
					{
						opt = new BitmapFactory.Options();
						opt.inPreferredConfig = Bitmap.Config.RGB_565;
						opt.inPurgeable = true;  
						opt.inInputShareable = true;
					}
					try {
						pBitmap = BitmapFactory.decodeFile(strFullPath,opt);
					} catch( OutOfMemoryError aError ) {
						aError.printStackTrace();
						System.gc();
						pBitmap = null;
					}
					
					checkStatus();
					return pBitmap;
					
				}
				
				@Override
				protected void onPostExecute(Bitmap aBitmap)
				{
					onNewResult(aBitmap, false);
				}
			};
			mTask.execute(mCurrent.mUrl);
		}
		else
		{
			mAjax = AjaxUtil.getImage(mCurrent.mUrl);
			mAjax.setOnSuccessListener(this);
			mAjax.setOnErrorListener(this);
			mAjax.setTimeout(10);
			mAjax.send();
		}
		
		return true;
	}
	
	/**
	 * checkMem
	 * @param strUrl
	 * @return
	 */
	private Bitmap checkMem(String strUrl)
	{
		final int nSize = (null != mMemCache ? mMemCache.size() : 0);
		for( int nIdx = 0; nIdx < nSize; nIdx++ )
		{
			CacheEntity pEntity = mMemCache.elementAt(nIdx);
			if( pEntity.mUrl.equals(strUrl) )
			{
				Bitmap pBitmap = pEntity.getBitmap();
				if( null != pBitmap )
				{
					// Exchange the position to the last one.
					mMemCache.remove(nIdx);
					mMemCache.add(pEntity);
				}
				
				return pBitmap;
			}
		}
		
		return null;
	}
	
	/**
	 * checkFile
	 * @param strUrl
	 * @return
	 */
	private boolean checkFile(String strUrl, ImageLoadListener aListener)
	{
		String strFileName = this.getFileName(strUrl);
		if( TextUtils.isEmpty(strFileName) )
			return false;
		
		// Load file content.
		File pFile = this.getFile(strFileName);

		if (null == pFile)
			return false;
		
		// Append request for get local file.
		return this.appendRequest(strUrl, aListener, true);
	}
	
	public boolean delFile(String strUrl)
	{
		String strFileName = this.getFileName(strUrl);
		if( TextUtils.isEmpty(strFileName) )
			return false;
		
		// Load file content.
		File pFile = this.getFile(strFileName);
		
		if (null == pFile)
			return false;
		
		return pFile.delete();
	}
	/**
	 * getEmptyBitmap
	 * @return
	 */
	private Bitmap getEmptyBitmap(boolean bNoImageMode) 
	{
		Context pContext = mContext.get();
		if( null != pContext )
		{
			if( bNoImageMode )
			{
				if( (null == mDefault) || (null == mDefault.get()) )
				{
					Bitmap pDefault = ImageHelper.getResBitmap(mContext.get(), R.drawable.i_global_image_none);
					mDefault = null;
					mDefault = new WeakReference<Bitmap>(pDefault);
				}
			}
			else if( (null == mEmpty) || (null == mEmpty.get()) )
			{
				Bitmap pEmpty = ImageHelper.getResBitmap(mContext.get(), R.drawable.i_global_image_none);
				mEmpty = null;
				mEmpty = new WeakReference<Bitmap>(pEmpty);
			}
		}
		
		return bNoImageMode ? mDefault.get() : mEmpty.get();
	}
	
	/**
	 * removeFolder
	 */
	private void removeFolder(String strFolder)
	{
		delAllFile(strFolder);
		removeFile(strFolder);
	}
	
	/**
	 * delAllFile
	 * @param path
	 */
	private void delAllFile(String path) 
	{
		File file = getFile(path);
		if (file == null)
			return;
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		if(null==tempList || tempList.length <=0)
			return;
		
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = getFile(path + tempList[i]);
			} else {
				temp = getFile(path + File.separator + tempList[i]);
			}
			if (null != temp && temp.isFile()) {
				temp.delete();
			}
			if (null != temp && temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);//????????��??件夹?????��?????�?
				removeFolder(path + "/" + tempList[i]);//????????�空???件夹
			}
		}
	}
	
	private boolean removeFile(String fileName) 
	{
		File file = getFile(fileName);

		if (null != file) {
			try {
				return file.delete();
			} catch (Exception ex) {
				Log.d(ImageLoader.class.toString(), ex.toString());
				return false;
			}
		}

		return true;
	}
	
	/**
	 * getFile
	 * @param strFileName
	 * @return
	 */
	private File getFile(String strFileName)
	{
		strFileName = !strFileName.equals("") && strFileName.startsWith(File.separator) ? strFileName.substring(1) : strFileName;
		String strFullPath = mRoot + strFileName;
		File pFile = new File(strFullPath);
		
		if( pFile.exists() )
			return pFile;
		
		pFile = null;
		return null;
	}
	
	/**
	 * addMemCache
	 * @param strUrl
	 * @param aBitmap
	 */
	private void addMemCache(String strUrl, Bitmap aBitmap)
	{
		if( null == aBitmap )
			return ;



		final int nSize = mMemCache.size();
		if( nSize < mMaxCache )
		{
			CacheEntity pEntity = new CacheEntity(strUrl, aBitmap);
			mMemCache.add(pEntity);
		}
		else
		{
			int nPos = 0;
			for( int nIdx = 0; nIdx < nSize; nIdx++ )
			{
				CacheEntity pEntity = mMemCache.elementAt(nIdx);
				if( CacheEntity.CACHE_STATUS_OKAY != pEntity.mStatus )
				{
					nPos = nIdx;
					break;
				}
			}
			
			CacheEntity pUpdate = mMemCache.elementAt(nPos);
			pUpdate.cleanup();
			pUpdate.save(strUrl, aBitmap, CacheEntity.CACHE_STATUS_OKAY);
			
			// Exchange the position to last one.
			mMemCache.remove(nPos);
			mMemCache.add(pUpdate);
		}
	}
	
	/**
	 * save2Local
	 * @param strUrl
	 * @param aBitmap
	 */
	private boolean save2Local(String strUrl, Bitmap aBitmap)
	{
		if( (TextUtils.isEmpty(mCachePath)) || (TextUtils.isEmpty(strUrl)) || (null == aBitmap) )
			return false;
		
		// Get target file name.
		String strFileName = this.getFileName(strUrl);
		File pTarget = createFile(strFileName);
		if( null == pTarget )
			return false;

		boolean bSuccess = true;
		FileOutputStream output = null;
		try 
		{
			output = new FileOutputStream(pTarget);
			aBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
			output.flush();
		}
		catch (Exception aException) 
		{
			Log.d(ImageLoader.class.toString(), aException.toString());
			bSuccess = false;
		}
		finally
		{
			if( null != output )
			{
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				output = null;
			}
		}
		
		return bSuccess;
	}
	
	/**
	 * createFile
	 * @param filename
	 * @return
	 */
	private File createFile(String strFileName)
	{
		String strFullPath = mRoot + strFileName;
		File pFile = new File(strFullPath);
		try {
			if (pFile.exists()) {
				pFile.delete();
			}
			
			if( !pFile.createNewFile() )
			{
				pFile = null;
			}
		}
		catch (Exception aException) 
		{
			Log.d(ImageLoader.class.toString(), aException.toString());
			pFile = null;
		}
		
		return pFile;
	}
	
	/**
	 * getFileName
	 * @param strUrl
	 * @return
	 */
	private String getFileName(String strUrl)
	{
		if( TextUtils.isEmpty(strUrl) )
			return null;
		
		String strResult = mNameMap.get(strUrl);
		if( !TextUtils.isEmpty(strResult) )
			return strResult;
		
		// Encode a new file name.
		String strFileName = "a" + ToolUtil.getMD5(strUrl);
		strResult = mCachePath + "/" + strFileName + ToolUtil.getExtension(strUrl) + ".cache";
		mNameMap.put(strUrl, strResult);
		
		return strResult;
	}
	
	/**
	 * 
	 */
	private String createPath(String strPath)
	{
		if( TextUtils.isEmpty(strPath) )
			return null;
		
		// 1. Firstly, check whether directory already exits or not.
		if (strPath.startsWith(File.separator)) {
			strPath = strPath.substring(1);
		}

		if (strPath.endsWith(File.separator)) {
			strPath = strPath.substring(0, strPath.length() - 1);
		}

		String[] dirs = strPath.split("\\" + File.separator);
		String pre = "";
		for (String dir : dirs) 
		{
			pre += ((pre.equals("") ? "" : File.separator) + dir);
			this.createDir(pre);
		}

		return strPath;
	}
	
	/**
	 * @param strPath
	 */
	private void createDir(String strPath)
	{
		if ( TextUtils.isEmpty(strPath) )
			return ;
		
		// 1. Get the root path.
		if( TextUtils.isEmpty(mRoot) )
		{
			Context pContext = mContext.get();
			if( ToolUtil.isSDExists() )
			{
				mRoot = Environment.getExternalStorageDirectory() + "/" + Config.TMPDIRNAME + "/";
			}
			else
			{
				mRoot = pContext.getCacheDir() + "/" + Config.TMPDIRNAME + "/";
			}
		}
		
		if( TextUtils.isEmpty(mRoot) )
			return ;
		
		// 2. Check whether current path exits.
		String strFullPath = mRoot + strPath;
		File pFile = new File(strFullPath);
		if( !pFile.exists() )
		{
			pFile.mkdir();
		}
		
		// Clean up.
		pFile = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isRequestEmpty()
	{
		if(null == mRequests)
			return true;
		return (mRequests.size() <= 0 );
	}
	
	private WeakReference<Context>  mContext;  // Current active context, instead of Activity instance.
	private String                  mRoot;     // Root path depends on SD card exits.
	private String                  mCachePath; // Required cache path.
	private boolean                 mRemoveOnExit; // Indicates whether remove cache folder when destroyed.
	private Vector<CacheEntity>     mMemCache;    // Memory cache for bitmap instances.
	private Vector<RequestEntity>   mRequests;    // Array for pending requests.
	private HashMap<String, String> mNameMap;     // URL -> Encoded file name.
	private HashMap<String, String> mEmptyMap;    // URL without any image.
	private WeakReference<Bitmap>   mEmpty = null;
	private WeakReference<Bitmap>   mDefault = null;
	private int                     mMaxCache;
	private RequestEntity           mCurrent;     // Current request entity instance.
	private Ajax                    mAjax;        // Current active network task.
	private AsyncTask<String,Void,Bitmap>   mTask; // Current asynchronous task.
	private BitmapFactory.Options opt;
	// Max memory cache.
	private static final int        DEFAULT_MAX_CACHE = 64;
	
	// Class for ImageCache.
	private static final class CacheEntity
	{
		// Cache entity status definition.
		private static final int CACHE_STATUS_NONE    = 0x0000;
		private static final int CACHE_STATUS_OKAY    = (CACHE_STATUS_NONE + 1);
		private static final int CACHE_STATUS_DISCARD = (CACHE_STATUS_NONE + 2);
		
		public CacheEntity(String strUrl, Bitmap aBitmap)
		{
			mUrl = strUrl;
			mStatus = (null != aBitmap ? CACHE_STATUS_OKAY : CACHE_STATUS_NONE);
			mBitmap = aBitmap;
		}
		
		/**
		 * getBitmap
		 * @return
		 */
		public Bitmap getBitmap()
		{
			if( (null != mBitmap) && (!mBitmap.isRecycled()) && (CACHE_STATUS_OKAY == mStatus) )
				return mBitmap;
			
			return null;
		}
		
		/**
		 * save
		 * @param strUrl
		 * @param aBitmap
		 * @param nStatus
		 */
		public void save(String strUrl, Bitmap aBitmap, int nStatus)
		{
			mBitmap = aBitmap;
			mUrl = strUrl;
			mStatus = nStatus;
		}
		
		/**
		 * cleanup
		 */
		public void cleanup()
		{
			if( null != mBitmap )
			{
				if( !mBitmap.isRecycled() )
				{
					mBitmap.recycle();
				}
				
				// Clean up.
				mBitmap = null;
			}
			
			// Reset the status.
			mStatus = CACHE_STATUS_DISCARD;
		}
		
		public String  mUrl;
		public int     mStatus;
		private Bitmap mBitmap;
	}
	
	
	public void loadImage(final ImageView view, String url) {
		final Bitmap data = get(url);
		if (data != null) {
			view.setImageBitmap(data);
			return;
		}
//		view.setImageResource(mAsyncImageLoader.getLoadingId());
		view.setImageBitmap(getLoadingBitmap(mContext.get()));
		get(url, new ImageLoadListener() {
			
			@Override
			public void onLoaded(Bitmap aBitmap, String strUrl) {
				view.setImageBitmap(aBitmap);
			}
			
			@Override
			public void onError(String strUrl) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	/**
	 * class for RequestEntity
	 * @author lorenchen
	 */
	private static final class RequestEntity
	{
		/**
		 * Default constructor of RequestEntity
		 * @param strUrl
		 * @param aListener
		 */
		public RequestEntity(String strUrl, ImageLoadListener aListener, boolean bLocal)
		{
			mUrl = strUrl;
			mListener = aListener;
			mLocal = bLocal;
		}
		
		public boolean           mLocal;
		public String            mUrl;
		public ImageLoadListener mListener;
	}
}
