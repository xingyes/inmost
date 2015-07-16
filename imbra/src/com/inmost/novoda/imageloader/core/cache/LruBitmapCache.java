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
package com.inmost.novoda.imageloader.core.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.inmost.imbra.util.GlobalImageCache;
import com.inmost.imbra.util.GlobalImageCache.BitmapDigest;
import com.inmost.novoda.imageloader.core.cache.util.LruCache;
import com.xingy.util.SDKUtils;
import com.xingy.util.ToolUtil;

/**
 * LruBitmapCache overcome the issue with soft reference cache. It is in fact keeping all the certain amount of images in memory. The size of the memory used for cache depends on the memory that the
 * android SDK provide to the application and the percentage specified (default percentage is 25%).
 */
public class LruBitmapCache {

	public static final int DEFAULT_MEMORY_CACHE_PERCENTAGE = 25;
	private static final int DEFAULT_MEMORY_CAPACITY_FOR_DEVICES_OLDER_THAN_API_LEVEL_4 = 12;

	
	/**
	 * 1M多少字节
	 */
	private static final long ONE_M_BYTES = 1024L * 1024L;
	private LruCache<BitmapDigest, Bitmap> cache;
	private long capacity;
	

	/**
	 * It is possible to set a specific percentage of memory to be used only for images.
	 * 
	 * @param context
	 * @param percentageOfMemoryForCache
	 *            1-80
	 */
	public LruBitmapCache(Context context, int percentageOfMemoryForCache) {
		ActivityManager manager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
		
		int memClass = 0;
		if(SDKUtils.isSDKVersionMoreThan16()) {
			memClass = ToolUtil.getMemoryClass(manager);
		}

		if (memClass == 0) {
			memClass = DEFAULT_MEMORY_CAPACITY_FOR_DEVICES_OLDER_THAN_API_LEVEL_4;
		}

		if (percentageOfMemoryForCache < 0) {
			percentageOfMemoryForCache = 0;
		}
		if (percentageOfMemoryForCache > 81) {
			percentageOfMemoryForCache = 80;
		}
		this.capacity = (memClass * percentageOfMemoryForCache) / 100L;
		if (this.capacity <= 0) {
			this.capacity = 4;
		} 
//		else if (this.capacity > MAX_IMAGE_MEMERY_CACHE) {
//			this.capacity = MAX_IMAGE_MEMERY_CACHE;
//		}
		

		this.capacity = this.capacity * ONE_M_BYTES;
		reset();
	}

	/**
	 * Setting the default memory size to 25% percent of the total memory available of the application.
	 * 
	 * @param context
	 */
	public LruBitmapCache(Context context) {
		this(context, DEFAULT_MEMORY_CACHE_PERCENTAGE);
	}

	private void reset() {
		if (cache != null) {
			cache.evictAll();
		}
		cache = new LruCache<BitmapDigest, Bitmap>(capacity) {
			@Override
			protected long sizeOf(BitmapDigest key, Bitmap bitmap) {

				return bitmap.getWidth() * bitmap.getHeight() * 4L;
			}

			@Override
			protected void entryRemoved(boolean evicted, BitmapDigest key, Bitmap oldValue, Bitmap newValue) {
				if (key.isAllowRecycle()) {
					cache.remove(key);
				}

				if (evicted) {
					GlobalImageCache.remove(key);
				}
			}
		};
	}

	public Bitmap get(BitmapDigest bd) {
		return cache.get(bd);
	}

	public void put(BitmapDigest bd, Bitmap bmp) throws NullPointerException {
		cache.put(bd, bmp);
	}

	public void clean() {
		
		//		由于发现重置后再次加载会出现闪屏（画出来立即被回收又重新画）的问题，使用cleanMost后会感觉好一点
		//		reset();
		//		recycleMemery();
		
		cleanMost();
	}

	public void cleanMost() {
		//直接清除 图片内存缓存
		recycleMemery();
		long maxSize = Math.round(capacity * 0.5D);
		cache.evict(maxSize);
	}

	private void recycleMemery() {
		System.gc();
	}

	public void remove(BitmapDigest bd) {
		cache.remove(bd);
	}

//	/**
//	 * 批量图片回收器
//	 * @author tandingqiang
//	 */
//	public static class BitmapRecycleTask extends Thread {
//
//		private ArrayList<Bitmap> queue = new ArrayList<Bitmap>();
//		private boolean hasStarted = false;
//
//		/**
//		 * 添加一个需要回收的图片数据
//		 */
//		public void recycleBitmap(Bitmap bitmap) {
//			synchronized (recycleTask) {
//				queue.add(bitmap);
//				recycleTask.notify();
//				if(Log.D) {
//					Log.d(LruBitmapCache.class.getName(),"recycleBitmap hasStarted : " + hasStarted);
//				}
//				if (!hasStarted) {
//					hasStarted = true;
//					start();
//				}
//				if(Log.D) {
//					Log.d(LruBitmapCache.class.getName(),"recycleBitmap starting........");
//				}
//			}
//		}
//
//		@Override
//		public void run() {
//			while (needAlive) {
//				try {
//					Bitmap bitmap = null;
//					synchronized (recycleTask) {
//						if (queue.size() < 1) {
//							System.gc();
//							if(Log.D) {
//								Log.d(LruBitmapCache.class.getName(), "wating for more task......");
//							}
//							recycleTask.wait();
//						}
//						bitmap = queue.remove(0);
//					}
//					if(Log.D) {
//						Log.d(LruBitmapCache.class.getName(), "thread has be notify, " + bitmap + " wanted be recyle");
//					}
//					if (bitmap != null && !bitmap.isRecycled()) {
//						//调用这个方法容易引起异常，不能强制调用图片回收器，
//						//bitmap.recycle();
//						System.gc();
//						if(Log.D) {
//							Log.d(LruBitmapCache.class.getName(), bitmap + " has recyled");
//						}
//					}
//				} catch (Throwable e) {
//					if (Log.E) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		
//	}
//
//	/**
//	 * 退出图片回收线程
//	 */
//	public static void quit() {
//		needAlive = false;
//		synchronized (recycleTask) {
//			recycleTask.notify();
//		}
//	}
	
}
