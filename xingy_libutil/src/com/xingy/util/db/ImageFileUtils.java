package com.xingy.util.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.xingy.util.Config;
import com.xingy.util.ImageHelper;
import com.xingy.util.Log;
import com.xingy.util.ToolUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;

/**
 * Created by xingyao on 15-7-1.
 */
public class ImageFileUtils {

    private String mRoot;     // Root path depends on SD card exits.

    private String mCachePath; // Required cache path.

//        private WeakReference<Context> mContext;  // Current active context, instead of Activity instance.


    public ImageFileUtils(Context context, String strCacheDir) {
        if (TextUtils.isEmpty(mRoot)) {
            if (ToolUtil.isSDExists())
                mRoot = Environment.getExternalStorageDirectory() + "/" + Config.TMPDIRNAME + "/";
            else
                mRoot = context.getCacheDir() + "/" + Config.TMPDIRNAME + "/";
        }
        mCachePath = createPath(strCacheDir);
    }


    private String createPath(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        // 1. Firstly, check whether directory already exits or not.
        if (strPath.startsWith(File.separator))
            strPath = strPath.substring(1);
        if (strPath.endsWith(File.separator))
            strPath = strPath.substring(0, strPath.length() - 1);

        String[] dirs = strPath.split("\\" + File.separator);
        String pre = "";
        for (String dir : dirs) {
            pre += ((pre.equals("") ? "" : File.separator) + dir);
            createDir(pre);
        }
        return strPath;
    }


    private void createDir(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return;

        if (TextUtils.isEmpty(mRoot))
            return;

        // Check whether current path exits.
        String strFullPath = mRoot + strPath;
        File pFile = new File(strFullPath);
        if (!pFile.exists())
            pFile.mkdir();

        // Clean up.
        pFile = null;
    }


    private String getMD5FileName(String url) {
        String strFileName = mCachePath + "/" + "a" + ToolUtil.getMD5(url) + ToolUtil.getExtension(url) + ".cache";
        return mRoot + strFileName;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param url
     * @param bitmap
     * @throws IOException
     */
    public void savaBitmap(String url, Bitmap bitmap) throws IOException {
        if (bitmap == null)
            return;

        File file = new File(getMD5FileName(url));
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从手机或者sd卡获取Bitmap
     *
     * @param url
     * @return
     */
    public Bitmap getBitmap(String url) {
        return ImageHelper.makeBitmapFromFile(getMD5FileName(url), 480);
    }

    /**
     * 判断文件是否存在
     *
     * @param url
     * @return
     */
    public boolean isFileExists(String url) {
        return new File(getMD5FileName(url)).exists();
    }

    /**
     * 获取文件的大小
     *
     * @param url
     * @return
     */
    public long getFileSize(String url) {
        return new File(getMD5FileName(url)).length();
    }


    private File getFile(String strFileName) {
        strFileName = !strFileName.equals("") && strFileName.startsWith(File.separator) ? strFileName.substring(1) : strFileName;
        String strFullPath = mRoot + strFileName;
        File pFile = new File(strFullPath);

        if (pFile.exists())
            return pFile;

        pFile = null;
        return null;
    }


    /**
     * removeFolder
     */
    private void removeFolder(String strFolder) {
        delAllFile(strFolder);
        removeFile(strFolder);
    }

    private boolean removeFile(String fileName) {
        File file = getFile(fileName);

        if (null != file) {
            try {
                return file.delete();
            } catch (Exception ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * delAllFile
     *
     * @param path
     */
    private void delAllFile(String path) {
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
        if (null == tempList || tempList.length <= 0)
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
                delAllFile(path + "/" + tempList[i]);
                removeFolder(path + "/" + tempList[i]);
            }
        }
    }


    public void clearDiskCache()
    {
        // Remove folder.
        if( !TextUtils.isEmpty(mCachePath))
        {
            this.removeFolder(mCachePath);
        }
    }


    public String getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(mRoot + mCachePath);
//        .getCacheDir());
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            cacheSize += getFolderSize(context.getExternalCacheDir());
//        }
        return getFormatSize(cacheSize);
    }

    public static long getFolderSize(String filepath){
        File file = new File(filepath);
        return getFolderSize(file);
    }

    public static long getFolderSize(File file){
        if(null==file || !file.exists())
            return 0;
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

}

