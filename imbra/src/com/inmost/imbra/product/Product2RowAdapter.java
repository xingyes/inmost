package com.inmost.imbra.product;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.inmost.imbra.R;
import com.inmost.imbra.main.HomeFloorModel;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-14.
 */
public class Product2RowAdapter extends BaseAdapter {
    private ArrayList<ProductModel> mProArray;
    private BaseActivity mActivity;
    private LayoutInflater mInflater;
    private ImageLoader mImgLoader;
    private View.OnClickListener  itemListener;

    public Product2RowAdapter(BaseActivity activity, ImageLoader loader) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        mImgLoader = loader;

        itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer)v.getTag();
                ProductModel item = mProArray.get(pos);
                Bundle bundle = new Bundle();
                bundle.putString(ProductDetailActivity.PRO_ID,item.id);
                UiUtils.startActivity(mActivity,ProductDetailActivity.class,bundle,true);
            }
        };
    }

    public void setData(ArrayList<ProductModel> set) {
        mProArray = set;
    }

    @Override
    public int getCount() {
        return (null == mProArray ? 0 : (mProArray.size() +1)/2);

    }

    @Override
    public Object getItem(int position) {
        return (null == mProArray ? null : mProArray.get(position*2));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_2product, null);
            holder = new ItemHolder();

            holder.oneLayout = convertView.findViewById(R.id.product_one);
            holder.twoLayout = convertView.findViewById(R.id.product_two);

            holder.aimgV1 = (AutoHeightImageView) holder.oneLayout.findViewById(R.id.pro_img);
            holder.name1 = (TextView) holder.oneLayout.findViewById(R.id.pro_name);
            holder.price1 = (TextView) holder.oneLayout.findViewById(R.id.pro_price);

            holder.aimgV2 = (AutoHeightImageView) holder.twoLayout.findViewById(R.id.pro_img);
            holder.name2 = (TextView) holder.twoLayout.findViewById(R.id.pro_name);
            holder.price2 = (TextView) holder.twoLayout.findViewById(R.id.pro_price);

            holder.oneLayout.setOnClickListener(this.itemListener);
            holder.twoLayout.setOnClickListener(this.itemListener);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        ProductModel pro = mProArray.get(position*2);
        holder.oneLayout.setTag(position*2);
        holder.aimgV1.setImageUrl(HomeFloorModel.formBraUrl(pro.front),mImgLoader);

        holder.name1.setText(pro.title);
        holder.price1.setText(pro.sale_price);

        if(mProArray.size() <= position*2+1)
            holder.twoLayout.setVisibility(View.INVISIBLE);
        else {
            holder.twoLayout.setVisibility(View.VISIBLE);
            ProductModel pro2 = mProArray.get(position*2+1);
            holder.twoLayout.setTag(position*2+1);
            holder.aimgV2.setImageUrl(HomeFloorModel.formBraUrl(pro2.front),mImgLoader);
            holder.name2.setText(pro2.title);
            holder.price2.setText(pro2.sale_price);
        }



        return convertView;
    }

    public static class ItemHolder {

        View   oneLayout;
        AutoHeightImageView aimgV1;
        TextView name1;
        TextView price1;

        View   twoLayout;
        AutoHeightImageView aimgV2;
        TextView name2;
        TextView price2;
    }
}

