package com.inmost.imbra.coupon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inmost.imbra.R;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.shopping.OrderModel;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-14.
 */
public class CouponAdapter extends BaseAdapter {
    private ArrayList<CouponModel> mCouponArray;
    private BaseActivity mActivity;
    private LayoutInflater mInflater;

    public CouponAdapter(BaseActivity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
    }

    public void setData(ArrayList<CouponModel> set) {
        mCouponArray = set;
    }

    @Override
    public int getCount() {
        return (null == mCouponArray ? 0 : mCouponArray.size() );

    }

    @Override
    public Object getItem(int position) {
        return (null == mCouponArray ? null : mCouponArray.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_coupon, null);
            holder = new ItemHolder();

            holder.titlev = (TextView)convertView.findViewById(R.id.title);
            holder.discountv = (TextView)convertView.findViewById(R.id.discount);
            holder.expirev = (TextView)convertView.findViewById(R.id.expire_time);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        CouponModel item = mCouponArray.get(position);

        holder.titlev.setText(item.title);
        holder.discountv.setText("" + item.discountNum);
        holder.expirev.setText(ToolUtil.formatSuitableDate(item.expireTime));

        return convertView;
    }

    public static class ItemHolder {
        TextView titlev;
        TextView discountv;
        TextView expirev;
    }
}

