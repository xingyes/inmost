package com.inmost.imbra.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.inmost.imbra.R;
import com.xingy.lib.ui.CubicRelativeLayout;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-14.
 */
public class HomeFloorAdapter extends BaseAdapter {
    private ArrayList<HomeFloorModel> mHomeFloors;
    private BaseActivity mActivity;
    private LayoutInflater mInflater;
    private ImageLoader mImgLoader;

    public HomeFloorAdapter(BaseActivity activity, ImageLoader loader) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        mImgLoader = loader;
    }

    public void setData(ArrayList<HomeFloorModel> set) {
        mHomeFloors = set;
    }

    @Override
    public int getCount() {
        return (null == mHomeFloors ? 0 : mHomeFloors.size());
    }

    @Override
    public Object getItem(int position) {
        return (null == mHomeFloors ? null : mHomeFloors.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_floor_item, null);
            holder = new ItemHolder();
            holder.rootBgView = (NetworkImageView) convertView.findViewById(R.id.home_bg_iv);

            holder.collectLayout = (RelativeLayout) convertView.findViewById(R.id.collect_bar);
            holder.collectTitle = (TextView) convertView.findViewById(R.id.collect_title);

            holder.blogLayout = (RelativeLayout) convertView.findViewById(R.id.blog_layout);
            holder.blogAuthor = (TextView) convertView.findViewById(R.id.author);
            holder.blogDate = (TextView) convertView.findViewById(R.id.publish_date);
            holder.blogTitle = (TextView) convertView.findViewById(R.id.blog_title);
            holder.blogCover = (NetworkImageView) convertView.findViewById(R.id.blog_cover);

            holder.lookLayout = (RelativeLayout) convertView.findViewById(R.id.look_layout);
            holder.lookTitle = (TextView) convertView.findViewById(R.id.look_title);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        final HomeFloorModel model = mHomeFloors.get(position);


        if (model.type == HomeFloorModel.TYPE_COLLECTION) {
            holder.collectLayout.setVisibility(View.VISIBLE);
            holder.blogLayout.setVisibility(View.GONE);
            holder.lookLayout.setVisibility(View.GONE);
            holder.rootBgView.setImageUrl(model.coverUrl,mImgLoader);
            holder.collectTitle.setText(model.title);

        } else if (model.type == HomeFloorModel.TYPE_BLOG) {
            holder.collectLayout.setVisibility(View.GONE);
            holder.blogLayout.setVisibility(View.VISIBLE);
            holder.lookLayout.setVisibility(View.GONE);
            holder.blogCover.setImageUrl(model.coverUrl,mImgLoader);
            holder.blogAuthor.setText(model.author);
            holder.blogTitle.setText(model.title);
            holder.blogDate.setText(model.textual_date);
        } else if (model.type == HomeFloorModel.TYPE_LOOKBOOK) {
            holder.collectLayout.setVisibility(View.GONE);
            holder.blogLayout.setVisibility(View.GONE);
            holder.lookLayout.setVisibility(View.VISIBLE);
            holder.rootBgView.setImageUrl(model.coverUrl,mImgLoader);
            holder.lookTitle.setText(model.title);

        }
        return convertView;
    }

    public static class ItemHolder {
        NetworkImageView rootBgView;
        RelativeLayout collectLayout;
        TextView collectTitle;

        RelativeLayout blogLayout;
        TextView blogTitle;
        TextView blogAuthor;
        TextView blogDate;
        NetworkImageView blogCover;

        RelativeLayout lookLayout;
        TextView lookTitle;

    }
}

