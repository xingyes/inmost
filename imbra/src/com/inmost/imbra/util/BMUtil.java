package com.inmost.imbra.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inmost.novoda.imageloader.core.bitmap.BitmapUtil;
import com.xingy.util.DPIUtil;
import com.xingy.util.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class BMUtil {

	private static final String TAG = "BitmapNewUtil";

	public static byte[] bmpToByteArray(final Bitmap bmp, boolean isRecycle) {

		if (null == bmp) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, out);
		if (isRecycle) {
			bmp.recycle();
		}

		byte[] result = out.toByteArray();
		try {
			out.close();
		} catch (IOException e) {
		}

		return result;

	}

    public static Bitmap makeBitmapFromFile(String path, final int thumbSize) {
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


    public static final int DEFAULT_ROUND = 6;

    public static final int IMAGE_MAX_WIDTH = 666;
    public static final int IMAGE_MAX_HEIGHT = 666;

    /**
     * 此方法应用于非主界面的布局加载，如果使用全局Context可能会显示在主界面中
     *
     * @param context
     * @param resource
     * @param root
     * @return
     */
    public static View inflate(Context context, int resource, ViewGroup root) {
        try {
            return getLayoutInflater(context).inflate(resource, root);
        } catch (Throwable e) {
            GlobalImageCache.getLruBitmapCache().clean();
        }
        return getLayoutInflater(context).inflate(resource, root);
    }


//    private static LayoutInflater getLayoutInflater() {
//
//        IMyActivity currentMyActivity = BaseApplication.getInstance().getCurrentMyActivity();
//        IMainActivity mainFrameActivity = BaseApplication.getInstance().getMainFrameActivity();
//        if (null != currentMyActivity) {
//            return getLayoutInflater(currentMyActivity.getThisActivity());
//        } else if (null != mainFrameActivity) {
//            return getLayoutInflater(mainFrameActivity.getThisActivity());
//        }
//        return getLayoutInflater(BaseApplication.getInstance());
//    }

    private static LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap
     *            传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap, int dw, int dh) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > height) {
            dw = dh = height;
        } else {
            dw = dh = width;
        }
        Bitmap tempBitmap = bitmap;
        if (dw < width || dh < height) {
            int size = dw < dh ? dw : dh;
            try {
                tempBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
            } catch (Throwable e) {
                GlobalImageCache.getLruBitmapCache().clean();
                try {
                    tempBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                } catch (Throwable e2) {
                }
            }
        }

        int rw = width > dw ? dw : width;
        int rh = height > dh ? dh : height;
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = rw / 2;
            top = 0;
            bottom = rw;
            left = 0;
            right = rw;
            height = rw;
            dst_left = 0;
            dst_top = 0;
            dst_right = rw;
            dst_bottom = rw;
        } else {
            roundPx = rh / 2;
            float clip = (rw - rh) / 2;
            left = clip;
            right = rw - clip;
            top = 0;
            bottom = rh;
            width = rh;
            dst_left = 0;
            dst_top = 0;
            dst_right = rh;
            dst_bottom = rh;
        }

        Bitmap output = Bitmap.createBitmap(rw, rh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(tempBitmap, src, dst, paint);

        return output;
    }

    /**
     *
     * @param drawable
     *            drawable图片
     * @param roundPx
     *            角度
     * @return
     * @Description:// 获得圆角图片的方法
     */

    public static Bitmap getRoundedCornerBitmap(Drawable drawable, float roundPx) {
        Bitmap bitmap = drawableToBitmap(drawable);

        return getRoundedCornerBitmap(bitmap, roundPx);
    }

    /**
     *
     * @param bitmap
     *            bitmap
     * @param roundPx
     *            角度
     * @return
     * @Description:// 获得圆角图片的方法
     */

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }

    /**
     *
     * @param drawable
     * @return
     * @Description:将Drawable转化为Bitmap
     */

    public static Bitmap drawableToBitmap(Drawable drawable) {

        int width = drawable.getIntrinsicWidth();

        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height,

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);

        drawable.draw(canvas);

        return bitmap;

    }

    /**
     *
     * @param drawable
     * @return
     * @Description:将Drawable转化为Bitmap
     */

    public static Drawable drawableToDrawable16(Drawable drawable) {
        Drawable newDrawable;
        int width = drawable.getIntrinsicWidth();

        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);

        drawable.draw(canvas);

        newDrawable = new BitmapDrawable(bitmap);
        return newDrawable;

    }

    /**
     * 根据bitmapDigest得到bitmap
     */
    public static Bitmap createBitmap(InputWay inputWay,GlobalImageCache.BitmapDigest bd) {

        if (bd.isLarge()) {
            GlobalImageCache.getLruBitmapCache().cleanMost();
        }

        int width = bd.getWidth();
        int height = bd.getHeight();

        Bitmap bitmap = createBitmap(inputWay, width, height);

        if (null == bitmap) {
            return null;
        }

        if (0 != bd.getRound()) {
            bitmap = toRoundCorner(bitmap, bd.getRound());
        }

        return bitmap;
    }

    /**
     * 根据宽高得到bitmap
     */
    public static Bitmap createBitmap(InputWay inputWay, int width, int height) {

        if (width > DPIUtil.dip2px(IMAGE_MAX_WIDTH)) {
            width = DPIUtil.dip2px(IMAGE_MAX_WIDTH);
        }

        if (height > DPIUtil.dip2px(IMAGE_MAX_HEIGHT)) {
            height = DPIUtil.dip2px(IMAGE_MAX_HEIGHT);
        }

        if (width == 0 && height == 0) {
            width = DPIUtil.dip2px(IMAGE_MAX_WIDTH);
            height = DPIUtil.dip2px(IMAGE_MAX_HEIGHT);
        }

        BitmapUtil bitmapUtil = new BitmapUtil();

        Bitmap bitmap = null;

        for (int i = 0; i < 2; i++) {

            if (0 != inputWay.getResourceId()) {
                bitmap = bitmapUtil.decodeResourceBitmapAndScale(MyApplication.app, width, height, inputWay.getResourceId(), false);
            } else if (null != inputWay.getFile()) {
                bitmap = bitmapUtil.decodeFileAndScale(inputWay.getFile(), width, height, false);
            } else if (null != inputWay.getInputStream()) {
                // TODO 重复尝试时肯定会出错
				/*Bitmap unscaledBitmap = bitmapUtil.decodeInputStream(inputWay.getInputStream());
				if (null == unscaledBitmap) {
					bitmap = null;
				} else {
					bitmap = bitmapUtil.scaleBitmap(unscaledBitmap, width, height, false);
				}*/
            } else if (null != inputWay.getByteArray()) {
                Bitmap unscaledBitmap = null;
                try {
                    unscaledBitmap = BitmapFactory.decodeByteArray(inputWay.getByteArray(), 0, inputWay.getByteArray().length);
                } catch (Throwable e) {
                }
                if (null == unscaledBitmap) {
                    bitmap = null;
                } else {
                    bitmap = bitmapUtil.scaleBitmap(unscaledBitmap, width, height, false);
                }
            }

            // 内存不足就会返回null
            if (null == bitmap) {
                GlobalImageCache.getLruBitmapCache().clean();
            } else {
                break;
            }

        }

        return bitmap;

    }

    /**
     * 圆角
     */
    public static Bitmap toRoundCorner(Bitmap inBitmap, int dp) {

        float px = DPIUtil.dip2px(dp);

        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, inBitmap.getWidth(), inBitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, px, px, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, rect, rect, paint);

        inBitmap.recycle();

        return outBitmap;
    }

    /**
     * 把三种输入方式封装起来
     */
    public static class InputWay {

        private int resourceId;
        private File file;
        private InputStream inputStream;
        private byte[] byteArray;


        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public byte[] getByteArray() {
            return byteArray;
        }

        public void setByteArray(byte[] byteArray) {
            this.byteArray = byteArray;
        }

    }

    /**
     * 判断此图片是否为NUll 是否被recycle
     *
     * @param bitmap
     * @return true 此bitmap可用 false 此bitmap不可用
     */
    public static boolean isBitmapCanUse(Bitmap bitmap) {

        return bitmap != null && !bitmap.isRecycled();
    }

    /**
     * 判断当前图片是否可以通过LRU的内存中获得
     *
     * @param digest
     * @return
     */
    public static Bitmap loadImageWithCache(GlobalImageCache.BitmapDigest digest) {
        Bitmap bitmap = GlobalImageCache.getLruBitmapCache().get(digest);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }

        return null;
    }

    /**
     * 图片加载监听事件
     *
     * @author tandingqiang
     *
     */
    public static interface ImageLoadListener {

        /**
         * 图片加载开始
         *
         * @param bitmapDigest
         */
        void onStart(GlobalImageCache.BitmapDigest bitmapDigest);


        /**
         * 图片加载成功
         *
         * @param bitmapDigest
         * @param bitmap
         */
        void onSuccess(GlobalImageCache.BitmapDigest bitmapDigest, Bitmap bitmap);

        /**
         * 图片加载失败
         *
         * @param bitmapDigest
         */
        void onError(GlobalImageCache.BitmapDigest bitmapDigest);

        /**
         *
         * @param bitmapDigest
         * @param max
         * @param progress
         */
        void onProgress(GlobalImageCache.BitmapDigest bitmapDigest, int max, int progress);
    }


    public static Drawable zoomDrawable(Drawable drawable) {
        // drawable转换成bitmap
        Bitmap oldbmp = drawableToBitmap(drawable);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, oldbmp.getWidth(), (int) (oldbmp.getHeight() * 0.65));
        return new BitmapDrawable(newbmp);
    }

    /**
     * 缩放drawable
     *
     * @param drawable
     * @return new drawable
     */
    public static Drawable scaleDrawable(Drawable drawable, float originalWidth, float originalHeight) {
        Drawable newDrawable;

        try {
            // drawable转换成bitmap
            Bitmap oldbmp = drawableToBitmap(drawable);

            int bmpWidth = oldbmp.getWidth();
            int bmpHeight = oldbmp.getHeight();
            Matrix matrix = new Matrix();
            float w = DPIUtil.getDefaultDisplay(MyApplication.app).getWidth() / originalWidth;
            float h = DPIUtil.getDefaultDisplay(MyApplication.app).getHeight() / originalHeight;
            matrix.postScale(w, h);
            // 建立新的bitmap，其内容是对原bitmap的缩放后的图
            Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
            newDrawable = new BitmapDrawable(newbmp);
        } catch (Exception e) {
            newDrawable = drawable;
        }
        return newDrawable;
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float roundPx;
            float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
            if (width <= height) {
                roundPx = width / 2;
                top = 0;
                bottom = width;
                left = 0;
                right = width;
                height = width;
                dst_left = 0;
                dst_top = 0;
                dst_right = width;
                dst_bottom = width;
            } else {
                roundPx = height / 2;
                float clip = (width - height) / 2;
                left = clip;
                right = width - clip;
                top = 0;
                bottom = height;
                width = height;
                dst_left = 0;
                dst_top = 0;
                dst_right = height;
                dst_bottom = height;
            }

            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
            final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
            final RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);

            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, src, dst, paint);

            paint.setColor(0xbbffffff);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(DPIUtil.dip2px(4));
            canvas.drawCircle((rectF.right - rectF.left) / 2, (rectF.bottom - rectF.top) / 2, roundPx, paint);
            return output;
        } catch (Throwable e) {
            return bitmap;
        }
    }


    public static Intent getSelectSystemImageIntent() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19){
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
// else {
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//        }
        return intent;
    }

}
