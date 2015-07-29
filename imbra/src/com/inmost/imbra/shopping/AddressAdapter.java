package com.inmost.imbra.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.inmost.imbra.R;
import com.inmost.imbra.main.HomeFloorModel;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductModel;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-14.
 */
public class AddressAdapter extends BaseAdapter {
    private ArrayList<AddressModel> mDataset;
    private BaseActivity mActivity;
    private LayoutInflater mInflater;
    private int pickIdx;
    private AddressEditListener addressListener;
    public interface AddressEditListener
    {
        public void onEditAddress(int pos);
        public void onDelAddress(int pos);
    }

    public void setPick(int pick)
    {
        pickIdx = pick;
    }

    public AddressAdapter(BaseActivity activity,int pick,AddressEditListener listener ) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        pickIdx = pick;
        addressListener = listener;
    }

    public void setData(ArrayList<AddressModel> set) {
        mDataset = set;
    }

    @Override
    public int getCount() {
        return (null == mDataset ? 0 : mDataset.size());

    }

    @Override
    public Object getItem(int position) {
        return (null == mDataset ? null : mDataset.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_address, null);
            holder = new ItemHolder();


            holder.root = convertView.findViewById(R.id.address_root_layout);
            holder.picked = convertView.findViewById(R.id.picked_logo);
            holder.receiver = (TextView)convertView.findViewById(R.id.receiver);
            holder.phone = (TextView)convertView.findViewById(R.id.phone);
            holder.address = (TextView)convertView.findViewById(R.id.address);

            holder.delv = (ImageView)convertView.findViewById(R.id.del_btn);
            holder.editv = (ImageView)convertView.findViewById(R.id.edit_btn);
            holder.delv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer pos = (Integer)v.getTag();
                    if(addressListener!=null)
                        addressListener.onDelAddress(pos);
                }
            });
            holder.editv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer pos = (Integer)v.getTag();
                    if(addressListener!=null)
                        addressListener.onEditAddress(pos);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        AddressModel item = mDataset.get(position);
        holder.address.setText(item.address);
        holder.phone.setText(item.phone);
        holder.receiver.setText(item.user);

        holder.delv.setTag(position);
        holder.editv.setTag(position);

        holder.picked.setVisibility(pickIdx == position ? View.VISIBLE:View.INVISIBLE);


        return convertView;
    }

    public static class ItemHolder {
        View     picked;
        View     root;
        TextView receiver;
        TextView address;
        TextView phone;

        ImageView  delv;
        ImageView  editv;
    }
}

