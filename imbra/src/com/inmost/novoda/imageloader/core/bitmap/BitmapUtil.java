/**
 * Copyright 2012 Novoda Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.inmost.novoda.imageloader.core.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import com.inmost.imbra.util.BMUtil;
import com.inmost.imbra.util.GlobalImageCache;
import com.xingy.util.DPIUtil;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Utility class abstract the usage of the BitmapFactory. It is shielding the users of this class from bugs and OutOfMemory exceptions.
 */
public class BitmapUtil {

	private static final int BUFFER_SIZE = 64 * 1024;

	public Bitmap decodeFile(File f, int width, int height) {
		updateLastModifiedForCache(f);
		int suggestedSize = height;
		if (width > height) {
			suggestedSize = width;
		}
		Bitmap unscaledBitmap = decodeFile(f, suggestedSize);
		if (unscaledBitmap == null) {
			return null;
		}
		return unscaledBitmap;
	}

	/**
	 * Use decodeFileAndScale(File, int, int, boolean) instead.
	 * 
	 * @param f
	 * @param width
	 * @param height
	 * @return
	 */
	@Deprecated
	public Bitmap decodeFileAndScale(File f, int width, int height) {
		return decodeFileAndScale(f, width, height, false);
	}

	public Bitmap decodeFileAndScale(File f, int width, int height, boolean upsampling) {
		Bitmap unscaledBitmap = decodeFile(f, width, height);
		if (unscaledBitmap == null) {
			return null;
		} else {
			return scaleBitmap(unscaledBitmap, width, height, upsampling);
		}
	}

	/**
	 * use {decodeResourceBitmapAndScale} instead
	 * 
	 * @param c
	 * @param width
	 * @param height
	 * @param resourceId
	 * @return
	 */
	@Deprecated
	public Bitmap scaleResourceBitmap(Context c, int width, int height, int resourceId) {
		return decodeResourceBitmapAndScale(c, width, height, resourceId, false);
	}

	public Bitmap decodeResourceBitmap(Context c, int width, int height, int resourceId) {
		Bitmap unscaledBitmap = null;
		try {
			unscaledBitmap = BitmapFactory.decodeResource(c.getResources(), resourceId);
			return unscaledBitmap;
		} catch (final Throwable e) {
			// calling gc does not help as is called anyway
			// http://code.google.com/p/android/issues/detail?id=8488#c80
			// System.gc();
		}
		return null;
	}

	public Bitmap decodeResourceBitmapAndScale(Context c, int width, int height, int resourceId, boolean upsampling) {
		Bitmap unscaledBitmap = null;
		try {
			unscaledBitmap = BitmapFactory.decodeResource(c.getResources(), resourceId);
			return scaleBitmap(unscaledBitmap, width, height, upsampling);
		} catch (final Throwable e) {
			// calling gc does not help as is called anyway
			// http://code.google.com/p/android/issues/detail?id=8488#c80
			// System.gc();
		}
		return null;
	}

	/**
	 * Calls { scaleBitmap( android.graphics.Bitmap, int, int, boolean)} with upsampling disabled.
	 * 
	 * This method ignores the upsampling settings.
	 * 
	 * @param b
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap scaleBitmap(Bitmap b, int width, int height) {
		return scaleBitmap(b, width, height, false);
	}

	/**
	 * Creates a new bitmap from the given one in the specified size respecting the size ratio of the origin image.
	 * 
	 * @param b
	 *            original image
	 * @param width
	 *            preferred width of the new image
	 * @param height
	 *            preferred height of the new image
	 * @param upsampling
	 *            if true smaller images than the preferred size are increased, if false the origin bitmap is returned
	 * @return new bitmap if size has changed, otherwise original bitmap.
	 */
	public Bitmap scaleBitmap(Bitmap b, int width, int height, boolean upsampling) {
		int imageHeight = b.getHeight();
		int imageWidth = b.getWidth();

		/*
		 * 纠正width和height
		 */
		if(//
				(imageWidth > DPIUtil.dip2px(BMUtil.IMAGE_MAX_WIDTH)||//
				imageHeight > DPIUtil.dip2px(BMUtil.IMAGE_MAX_HEIGHT))//
				&&(width == 0 && height == 0)//
				){
			width = DPIUtil.dip2px(BMUtil.IMAGE_MAX_WIDTH);
			height = DPIUtil.dip2px(BMUtil.IMAGE_MAX_HEIGHT);
		}
		
		if ((width == 0 && height == 0) || (!upsampling && imageHeight <= height && imageWidth <= width)) {
			return b;
		}
		
		boolean isHeight;
		if(height == 0){
			isHeight = false;
		}else if(width == 0){
			isHeight = true;
		}else{
			isHeight = imageHeight > imageWidth;
		}
		
		int finalWidth = width;
		int finalHeight = height;
		float factor = 1;
		
		if (isHeight) {
			factor = ((float) height) / ((float) imageHeight);
		} else {
			factor = ((float) width) / ((float) imageWidth);
		}
		
		if(!upsampling && factor > 1){
			return b;
		}
		
		finalHeight = new Float(imageHeight * factor).intValue();
		finalWidth = new Float(imageWidth * factor).intValue();
		Bitmap scaled = null;
		try {
			scaled = Bitmap.createScaledBitmap(b, finalWidth, finalHeight, true);
		} catch (final Throwable e) {
			// calling gc does not help as is called anyway
			// http://code.google.com/p/android/issues/detail?id=8488#c80
			// System.gc();
		}
		// pad 上遇到过的问题，要求显示的图大小，与下载下来的图大小一样，
		// 导致压缩出来的对象与压缩之前对象相同，所以不能回收它。
		if (b != scaled) {
			recycle(b);
		}
		return scaled;
	}

	/**
	 * Convenience method to decode an input stream as a bitmap using BitmapFactory.decodeStream without any parameter options.
	 * 
	 * If decoding fails the input stream is closed.
	 * 
	 * @param is
	 *            input stream of image data
	 * @return bitmap created from the given input stream.
	 */
	public Bitmap decodeInputStream(InputStream is) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, null);
		} catch (final Throwable e) {
			// calling gc does not help as is called anyway
			// http://code.google.com/p/android/issues/detail?id=8488#c80
			// System.gc();
		} finally {
			closeSilently(is);
		}
		return bitmap;
	}

	private void recycle(Bitmap scaled) {
		try {
			scaled.recycle();
		} catch (Exception e) {
			//
		}
	}

	private void updateLastModifiedForCache(File f) {
		f.setLastModified(System.currentTimeMillis());
	}

	private Bitmap decodeFile(File f, int suggestedSize) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			int scale = evaluateScale(f, suggestedSize);
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			options.inTempStorage = new byte[BUFFER_SIZE];
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			fis = new FileInputStream(f);
			bitmap = BitmapFactory.decodeStream(fis, null, options);
		} catch (final Throwable e) {
			// calling gc does not help as is called anyway
			// http://code.google.com/p/android/issues/detail?id=8488#c80
			// System.gc();
		} finally {
			closeSilently(fis);
		}
		return bitmap;
	}

	private int evaluateScale(File f, int suggestedSize) {
		final BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		decodeFileToPopulateOptions(f, o);
		return calculateScale(suggestedSize, o.outWidth, o.outHeight);
	}

	private void decodeFileToPopulateOptions(File f, final BitmapFactory.Options o) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			closeSilently(fis);
		} catch (final Throwable e) {
			// calling gc does not help as is called anyway
			// http://code.google.com/p/android/issues/detail?id=8488#c80
			// System.gc();
		} finally {
			closeSilently(fis);
		}
	}

	private void closeSilently(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Exception e) {
		}
	}

	int calculateScale(final int requiredSize, int widthTmp, int heightTmp) {
		int scale = 1;
		while (true) {
			if ((widthTmp / 2) < requiredSize || (heightTmp / 2) < requiredSize) {
				break;
			}
			widthTmp /= 2;
			heightTmp /= 2;
			scale *= 2;
		}
		return scale;
	}

	private Bitmap decodeFileAndScale2(File f, int width, int height, boolean upsampling) {
		Bitmap unscaledBitmap = decodeFile(f, width, height);
		if (unscaledBitmap == null) {
			return null;
		}

		Bitmap b = unscaledBitmap;

		int imageHeight = b.getHeight();
		int imageWidth = b.getWidth();
		if (!upsampling && imageHeight <= height && imageWidth <= width) {
			return b;
		}
		int finalWidth = width;
		int finalHeight = height;
		
		float factor = 0;
		if (imageHeight > 0 && imageWidth > 0) {
			float factorH = ((float) height) / ((float) imageHeight);
			float factorW = ((float) width) / ((float) imageWidth);
			factor = Math.min(factorH, factorW);
		}
		
		if (factor == 0) {
			if (imageHeight > imageWidth) {
				factor = ((float) height) / ((float) imageHeight);
			} else {
				factor = ((float) width) / ((float) imageWidth);
			}
		}

		finalHeight = new Float(imageHeight * factor).intValue();
		finalWidth = new Float(imageWidth * factor).intValue();
		
		Bitmap scaled = null;
		try {
			scaled = Bitmap.createScaledBitmap(b, finalWidth, finalHeight, true);
		} catch (final Throwable e) {
		
		}
		if (scaled != b) {
			recycle(b);
		}
		return scaled;
		
	}
	
	public Bitmap createBitmapWithClean(File f, int width, int height, boolean upsampling) {
		Bitmap bmp = null;
		for (int i = 0; i < 2; i++) {
			bmp = decodeFileAndScale2(f, width, height, upsampling);
			if (bmp == null || bmp.isRecycled()) {
				GlobalImageCache.getLruBitmapCache().cleanMost();
			} else {
				break;
			}
		}
		return bmp;
	}


}
