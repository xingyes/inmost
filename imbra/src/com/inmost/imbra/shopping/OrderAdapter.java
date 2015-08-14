package com.inmost.imbra.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.inmost.imbra.R;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductModel;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-14.
 */
public class OrderAdapter extends BaseAdapter {
    private ArrayList<OrderModel> mOrderArray;
    private BaseActivity mActivity;
    private LayoutInflater mInflater;
    private ImageLoader mImgLoader;

    private OrderClickListener orderListener;
    public interface OrderClickListener
    {
        public void onProClick(int pos);
        public void onOrderDel(int pos);
    }

    public OrderAdapter(BaseActivity activity, ImageLoader loader, OrderClickListener listener ) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        mImgLoader = loader;

        orderListener = listener;

    }

    public void setData(ArrayList<OrderModel> set) {
        mOrderArray = set;
    }

    @Override
    public int getCount() {
        return (null == mOrderArray ? 0 : mOrderArray.size() );

    }

    @Override
    public Object getItem(int position) {
        return (null == mOrderArray ? null : mOrderArray.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_order, null);
            holder = new ItemHolder();

            holder.statusv = (TextView)convertView.findViewById(R.id.order_status);
            holder.orderidv = (TextView)convertView.findViewById(R.id.order_id);
            holder.delbtn = (ImageView)convertView.findViewById(R.id.order_del);
            holder.delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object pos = v.getTag();
                        if (pos instanceof Integer && null != orderListener)
                            orderListener.onOrderDel((Integer) pos);
                }
            });


            holder.aimgv = (NetworkImageView)convertView.findViewById(R.id.pro_img);
            holder.aimgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object pos = v.getTag();
                    if (pos instanceof Integer && null != orderListener)
                        orderListener.onProClick((Integer) pos);
                }
            });


            holder.namev = (TextView)convertView.findViewById(R.id.pro_title);
            holder.pricev = (TextView)convertView.findViewById(R.id.pro_price);
            holder.ordertimev = (TextView)convertView.findViewById(R.id.order_time);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        OrderModel orderItem = mOrderArray.get(position);

        if(orderItem.order_stat>=0)
            holder.statusv.setText(OrderModel.orderStateStrRid[orderItem.order_stat]);
        else
            holder.statusv.setText(OrderModel.orderStateStrRid[0 - orderItem.order_stat]);

        holder.orderidv.setText("订单号:" + orderItem.orderid);

        if(null!=orderItem.proList && orderItem.proList.size()>0) {
            holder.aimgv.setImageUrl(HomeFloorModel.formBraUrl(orderItem.proList.get(0).front), mImgLoader);
            holder.namev.setText(orderItem.proList.get(0).title);
        }

        holder.pricev.setText(mActivity.getString(R.string.rmb_price, orderItem.total_price));

        holder.ordertimev.setText(ToolUtil.formatSuitableDate(orderItem.addtime*1000));

        holder.delbtn.setTag(position);
        holder.aimgv.setTag(position);

        return convertView;
    }

    public static class ItemHolder {
        TextView statusv;
        TextView orderidv;
        ImageView delbtn;

        NetworkImageView aimgv;
        TextView namev;
        TextView pricev;
        TextView ordertimev;


    }
}

