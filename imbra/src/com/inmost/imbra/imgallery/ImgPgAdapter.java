package com.inmost.imbra.imgallery;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView.ScaleType;

import com.android.volley.toolbox.ImageLoader;
import com.inmost.imbra.util.braConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.activity.BaseActivity.DestroyListener;

import java.util.ArrayList;
import java.util.List;

public class ImgPgAdapter extends PagerAdapter implements DestroyListener{

	private ImageLoader mImageLoader;

	public static final String CACHE_DIR = "item_gallery";
	
	//public static final int PIC_WIDTH = 134;
	//public static final int PIC_HEIGHT = 134;
	//private int pic_width_px;
	//private int pic_height_px;

	private BaseActivity mActivity;
	private List<String> mUrlList;
	private ArrayList<ZoomImageView>  imgViewArray;
	public static final int TYPE_IMAGE_URL_ARRAY = 1001;
	public static final int TYPE_PRODUCT_MODEL = 1002;
	
	private ImgFuncListener imgFuncListener;
	public interface ImgFuncListener{
		public void onDetailShow();
		public void onMenuShiftShowOrHide();
		
	}
	public void setShowListener(ImgFuncListener dlistener)
	{
		imgFuncListener = dlistener;
	}
	
	public ImgPgAdapter(BaseActivity aActivity, List<String> urlList,ImageLoader loader) {
		mActivity = aActivity;
		mUrlList = urlList;
		imgViewArray = new ArrayList<ZoomImageView>();
		for(int i=0 ; null!= mUrlList && i < mUrlList.size() ; i++)
		{
			imgViewArray.add(null);
		}
		aActivity.addDestroyListener(this);
		mImageLoader = loader;
    }


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getCount() {
		return (null == mUrlList ? 0 : mUrlList.size());
	}


	
	@Override
	public Object instantiateItem(View view, int position) {
		ZoomImageView iv = imgViewArray.get(position);
		if(null!=iv)
		{
			((ViewPager) view).addView(iv);
			return iv;
		}
		
//		Bitmap bm = mImageLoader.get(mUrlList.get(position));
//		if(null!=bm)
		{
			iv = new ZoomImageView(mActivity);
			iv.setImgFuncListener(imgFuncListener);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			
			imgViewArray.set(position, iv);

            iv.setImageUrl(mUrlList.get(position),mImageLoader);
            ((ViewPager) view).addView(iv);

        }
//		else
//			mImageLoader.get(mUrlList.get(position),this);
		return iv;
	}

	/**
	 * 判断是否由对象生成界面
	 */
	@Override
	public boolean isViewFromObject(View view, Object arg1) {
		return (view == arg1);
	}

	/**
	 * 销毁position位置的界面
	 */
	@Override
	public void destroyItem(View view, int position, Object arg2) {
		((ViewPager) view).removeView((View) arg2);
		System.gc();
		
	}
	
	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}


}
