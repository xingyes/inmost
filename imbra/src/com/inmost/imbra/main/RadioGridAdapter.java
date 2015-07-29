package com.inmost.imbra.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inmost.imbra.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingyao on 15-6-26.
 */
public class RadioGridAdapter extends BaseAdapter {

    protected List<String> mList;
    protected Context mContext;
    protected int          mPickIdx;

    public RadioGridAdapter(Context aContext)
    {
        mContext = aContext;
        mPickIdx = -1;
    }

    public void setList(List<String> aList, int nCheckedItem)
    {
        if(null == mList)
            mList = new ArrayList<String>();

        mList.clear();
        mList.addAll(aList);
        mPickIdx = nCheckedItem;
    }

    public void setList(String[] aOptions, int nCheckedItem)
    {
        if(null == mList)
            mList = new ArrayList<String>();

        mList.clear();
        final int nLength = (null != aOptions ? aOptions.length : 0);
        for( int nIdx = 0; nIdx < nLength; nIdx++ )
            mList.add(aOptions[nIdx]);

        mPickIdx = nCheckedItem;
    }

    public void setPickIdx(int aIndex)
    {
        mPickIdx = aIndex;
    }

    @Override
    public int getCount() {
        return (null==mList) ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return (null==mList) ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;
        if (null == convertView)
        {
            convertView = View.inflate(mContext, R.layout.radio_center_item, null);
            holder = new ItemHolder();
            holder.mName = (TextView) convertView.findViewById(com.xingy.R.id.radio_item_name);
            holder.mName.setBackgroundResource(R.drawable.button_pink_frame_round);
            holder.mName.setTextColor(mContext.getResources().getColorStateList(R.color.txt_pink_white_selector));
            convertView.setTag(holder);
        }
        else
            holder = (ItemHolder) convertView.getTag();

            // set data
        String strName = mList.get(position);
        holder.mName.setText(strName);
        holder.mName.setSelected(mPickIdx == position);
        return convertView;
    }

    private class ItemHolder
    {
        TextView  mName;
    }
}
