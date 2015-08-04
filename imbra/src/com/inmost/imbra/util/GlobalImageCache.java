package com.inmost.imbra.util;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;
import com.inmost.novoda.imageloader.core.cache.LruBitmapCache;
import com.xingy.util.MyApplication;

/**
 * 关键概念： 缓存的bitmapKey采用：网址、分辨率、圆角度数，三者一起进行标识 是为了全局共享图片资源，而且不同界面和分辨率可能图片所需缓存的形态不尽相同，为了满足需求，采用以上方式
 */
public class GlobalImageCache {

	/**
	 * 不存在
	 */
	public static final int STATE_NONE = 0;
	/**
	 * 加载中
	 */
	public static final int STATE_LOADING = 1;
	/**
	 * 加载失败
	 */
	public static final int STATE_FAILURE = 2;
	/**
	 * 加载成功
	 */
	public static final int STATE_SUCCESS = 3;

	/**
	 * key使用
	 */
	private static final Map<BitmapDigest, ImageState> imageMap = new HashMap<BitmapDigest, ImageState>();
	private static final Map<ImageState, BitmapDigest> digestMap = new HashMap<ImageState, BitmapDigest>();

	private static LruBitmapCache lruBitmapCache;

	/**
	 * 单例<br />
	 * 按需创建
	 */
	public synchronized static LruBitmapCache getLruBitmapCache() {
		if (null == lruBitmapCache) {
			lruBitmapCache = new LruBitmapCache(MyApplication.app, 30);
		}
		return lruBitmapCache;
	}

	/**
	 * 获取ImageState
	 */
	public synchronized static ImageState getImageState(BitmapDigest bitmapDigest) {
		ImageState is = imageMap.get(bitmapDigest);
		if (null == is) {
			is = new ImageState();
			imageMap.put(bitmapDigest, is);
			digestMap.put(is, bitmapDigest);
		}
		return is;
	}

	/**
	 * 获取BitmapDigest
	 */
	public static BitmapDigest getBitmapDigest(ImageState imageState) {
		return digestMap.get(imageState);
	}

	/**
	 * 供LruBitmapCache清理时回调
	 */
	public static void remove(BitmapDigest bitmapDigest) {
		ImageState is = imageMap.remove(bitmapDigest);
		digestMap.remove(is);
	}

	/**
	 * 图像状态
	 */
	public static class ImageState {

		private int mState = STATE_NONE;

		public void loading() {
			mState = STATE_LOADING;
		}

		public void failure() {
			mState = STATE_FAILURE;
		}

		public void success(Bitmap b) {
			try {
				getLruBitmapCache().put(getBitmapDigest(this), b);
				mState = STATE_SUCCESS;
			} catch (NullPointerException e) {
				failure();
			}

		}

		public void none() {
			mState = STATE_NONE;
		}

		public int getState() {
			if (mState == STATE_SUCCESS) {
				if (null == getBitmap()) {
					mState = STATE_NONE;
				}
			}
			return mState;
		}

		public Bitmap getBitmap() {
			BitmapDigest bd = getBitmapDigest(this);
			if (null != bd) {
				return getLruBitmapCache().get(bd);
			}
			return null;
		}

		@Override
		public String toString() {
			return "ImageState [bitmap=" + getBitmap() + ", mState=" + mState + "]";
		}

	}

	/**
	 * 位图描述
	 */
	public static class BitmapDigest {

		private String url;
		private int width;
		private int height;
		private int round;
		private String custom;
		private boolean allowRecycle = true;

		private boolean large = false;
		//许声pad修复的bug，同一页面两个商品图片url一样，导致第二个没有请求，就没有显示图片，加上position来区别两个对象
		private int position;

		/**
		 * 用于携带额外的参数
		 */
		private Map<String, Object> moreParameter;

		private String digest;

		public BitmapDigest(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * 单位：dp
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * 单位：dp
		 */
		public void setWidth(int width) {
			this.width = width;
		}

		/**
		 * 单位：dp
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * 单位：dp
		 */
		public void setHeight(int height) {
			this.height = height;
		}

		public int getRound() {
			return round;
		}

		public void setRound(int round) {
			this.round = round;
		}

		public String getCustom() {
			return custom;
		}

		public void setCustom(String custom) {
			this.custom = custom;
		}

		public boolean isAllowRecycle() {
			return allowRecycle;
		}

		public void setAllowRecycle(boolean allowRecycle) {
			this.allowRecycle = allowRecycle;
		}

		public boolean isLarge() {
			return large;
		}

		public void setLarge(boolean large) {
			this.large = large;
		}

		/**
		 * 获得携带的额外的参数
		 */
		public Object getMoreParameter(String key) {
			if (null == moreParameter) {
				return null;
			}
			return moreParameter.get(key);
		}

		/**
		 * 用于携带额外的参数
		 */
		public void putMoreParameter(String key, Object value) {
			if (null == moreParameter) {
				moreParameter = new HashMap<String, Object>();
			}
			moreParameter.put(key, value);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (allowRecycle ? 1231 : 1237);
			result = prime * result + ((custom == null) ? 0 : custom.hashCode());
			result = prime * result + height;
			result = prime * result + position;
			result = prime * result + round;
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			result = prime * result + width;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BitmapDigest other = (BitmapDigest) obj;
			if (allowRecycle != other.allowRecycle)
				return false;
			if (custom == null) {
				if (other.custom != null)
					return false;
			} else if (!custom.equals(other.custom))
				return false;
			if (height != other.height)
				return false;
			if (position != other.position)
				return false;
			if (round != other.round)
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			if (width != other.width)
				return false;
			return true;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

	}

}
