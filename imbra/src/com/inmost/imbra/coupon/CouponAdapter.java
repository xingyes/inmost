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
    private int pickIdx;

    public CouponAdapter(BaseActivity activity, int idx) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        pickIdx = idx;
    }

    public void setPick(int idx)
    {
        pickIdx = idx;
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

            holder.root = convertView.findViewById(R.id.coupon_root);
            holder.picked = convertView.findViewById(R.id.picked_logo);
            holder.titlev = (TextView)convertView.findViewById(R.id.title);
            holder.discountv = (TextView)convertView.findViewById(R.id.discount);
            holder.expirev = (TextView)convertView.findViewById(R.id.expire_time);
            holder.rmb = (TextView)convertView.findViewById(R.id.rmb);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        CouponModel item = mCouponArray.get(position);

        holder.titlev.setText(item.title);
        holder.discountv.setText("" + item.discountNum);
        holder.expirev.setText(ToolUtil.formatSuitableDate(item.expireTime));

//        if(item.expireTime)
        if(position %3 == 1)
        {
            holder.root.setBackgroundResource(R.drawable.coupon_disable_bg);
            holder.rmb.setTextColor(mActivity.getResources().getColor(R.color.global_text_info_light));
            holder.titlev.setTextColor(mActivity.getResources().getColor(R.color.global_text_info_light));
            holder.discountv.setTextColor(mActivity.getResources().getColor(R.color.global_text_info_light));
            holder.expirev.setTextColor(mActivity.getResources().getColor(R.color.global_text_info_light));
        }
        else
        {
            holder.root.setBackgroundResource(R.drawable.coupon_bg);
            holder.rmb.setTextColor(mActivity.getResources().getColor(R.color.global_pink));
            holder.discountv.setTextColor(mActivity.getResources().getColor(R.color.global_pink));
            holder.titlev.setTextColor(mActivity.getResources().getColor(R.color.global_text_info_color));
            holder.expirev.setTextColor(mActivity.getResources().getColor(R.color.global_text_info_color));
        }

        holder.picked.setVisibility(pickIdx == position ? View.VISIBLE:View.INVISIBLE);
        return convertView;
    }

    public static class ItemHolder {
        View     root;
        View     picked;
        TextView rmb;
        TextView titlev;
        TextView discountv;
        TextView expirev;
    }
}

