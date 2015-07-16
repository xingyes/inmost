package com.inmost.imbra.product;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.inmost.imbra.R;
import com.inmost.imbra.main.HomeFloorModel;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-14.
 */
public class ProductVPAdapter extends PagerAdapter {
    private ArrayList<ProductModel> mProArray;
    private LayoutInflater mInflater;
    private ImageLoader mImgLoader;


    public interface OnVPItemClickListener
    {
        public void onVPItemClick(int pos, String id);
    }
    private OnVPItemClickListener listener;
    public ProductVPAdapter(Context context, ImageLoader loader,ArrayList<ProductModel> array,
                            OnVPItemClickListener alistener) {
        mInflater = LayoutInflater.from(context);
        mImgLoader = loader;
        mProArray = array;
        listener = alistener;
    }

    @Override
    public float getPageWidth(int position)
    {
        return (float) 0.4;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        // TODO Auto-generated method stub
        // 实现此抽象方法，防止出现AbstractMethodError
        super.startUpdate(container);
    }

    /**
     * 初始化position位置的界面
     */
    @Override
    public Object instantiateItem(View view, int position) {

        ProductModel item = mProArray.get(position);


        View page = mInflater.inflate(R.layout.product_item, null);
        NetworkImageView proImgV = (NetworkImageView)page.findViewById(R.id.pro_img);
        proImgV.setImageUrl(HomeFloorModel.formBraUrl(item.front),mImgLoader);

        TextView nameTv  = (TextView)page.findViewById(R.id.pro_name );
        nameTv.setText(item.title);

        TextView oldpriceTv = (TextView)page.findViewById(R.id.pro_old_price);
        oldpriceTv.setText(item.ori_price);

        TextView priceTv = (TextView)page.findViewById(R.id.pro_price);
        priceTv.setText(item.sale_price);


        ((ViewPager) view).addView(page);
        page.setTag(position);
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null) {
                    Object pos = v.getTag();
                    if (pos instanceof Integer) {
                        ProductModel item = mProArray.get((Integer) pos);
                        listener.onVPItemClick((Integer) pos, item.id);
                    }
                }
            }
        });

        return page;
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

//		public void cleanData() {
//			pics = new int[0];
//		}



    @Override
    public int getCount() {
        return (null == mProArray ? 0 : (mProArray.size()));

    }

}

