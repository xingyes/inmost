package com.android.volley.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.xingy.util.db.ImageFileUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xingyao on 15-7-1.
 */
public class MemDiskImageCache implements ImageLoader.ImageCache{

    private LruCache<String,Bitmap> mMemoryCache;
    /**
     * 操作文件相关类对象的引用
     */
    private ImageFileUtils fileUtils;
    /**
     * 下载Image的线程池
     */
    private ExecutorService mImageThreadPool = null;



    public MemDiskImageCache(Context context)
    {
        fileUtils = new ImageFileUtils(context, "BRA_PIC");
        int mCacheSize = 4*1024*1024;
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };

    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     *
     * @return SingleThread
     */
    public ExecutorService getThreadPool(){
        if(mImageThreadPool == null){
            synchronized(ExecutorService.class){
                if(mImageThreadPool == null){
                    mImageThreadPool = Executors.newSingleThreadExecutor();
                }
            }
        }

        return mImageThreadPool;

    }


    @Override
    public void putBitmap(String url, Bitmap bm) {
        addBitmapToMemoryCache(url, bm);
        final String imUrl = url;
        final Bitmap imBm = bm;

        getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    //保存在SD卡或者手机目录
                    fileUtils.savaBitmap(imUrl, imBm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void clearDiskCache()
    {
        if(null!=fileUtils)
        {
            fileUtils.clearDiskCache();
        }
    }

    public String getImgCacheSize(Context context)
    {
        if(null!=fileUtils)
            return fileUtils.getTotalCacheSize(context);
        else
            return "";
    }







    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bm = mMemoryCache.get(url);
        if(bm==null && fileUtils.isFileExists(url) && fileUtils.getFileSize(url) != 0){
            //从SD卡获取手机里面获取Bitmap
            bm = fileUtils.getBitmap(url);

            //将Bitmap 加入内存缓存
            addBitmapToMemoryCache(url, bm);
        }
        return bm;
    }

    public void clearMemCache()
    {
        if(null !=mMemoryCache)
            mMemoryCache.evictAll();
    }

}
