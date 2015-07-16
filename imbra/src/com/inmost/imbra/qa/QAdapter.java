package com.inmost.imbra.qa;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inmost.imbra.R;
import com.xingy.util.Config;
import com.xingy.util.ImageLoadListener;
import com.xingy.util.ImageLoader;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.activity.BaseActivity.DestroyListener;

import java.util.ArrayList;

public class QAdapter extends BaseAdapter implements ImageLoadListener, DestroyListener{

	private ArrayList<QAInfo> dataSource;
	private LayoutInflater    mInflater;
	private BaseActivity      mActivity;
	private ImageLoader mImageLoader;
	
	
	public QAdapter(BaseActivity aActivity)
	{
		mActivity = aActivity;
		mInflater = LayoutInflater.from(mActivity);
		dataSource = new ArrayList<QAInfo>();
		
		mActivity.addDestroyListener(this);
		mImageLoader = new ImageLoader(mActivity, Config.PIC_CACHE_DIR);
	}
	
	public void setData(ArrayList<QAInfo> aSet)
	{
		if(aSet!=null)
		{
			dataSource.clear();
			dataSource.addAll(aSet);
		}
	}
	
	public void addData(ArrayList<QAInfo> aSet)
	{
		if(aSet!=null)
		{
			dataSource.addAll(aSet);
		}
	}
	
	@Override
	public int getCount() {
		return dataSource == null ? 0 : dataSource.size();
	}

	@Override
	public Object getItem(int position) {
		return dataSource == null ?  null : dataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemHolder holder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.qa_list_item, null);
			holder = new ItemHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.list_img);
			holder.title = (TextView) convertView.findViewById(R.id.list_title);
			holder.read = (TextView) convertView.findViewById(R.id.read_tv);
			holder.reply = (TextView) convertView.findViewById(R.id.reply_tv);
			
			convertView.setTag(holder);
		} else {
			holder = (ItemHolder) convertView.getTag();
		}

		QAInfo qa = dataSource.get(position);

		if(!ToolUtil.isEmpty(qa.coverImg))
		{
			Bitmap bm = mImageLoader.get(qa.coverImg);
			if (bm != null) 
				holder.img.setImageBitmap(bm);
			else
				mImageLoader.get(qa.coverImg,this);
		}
		holder.title.setText(qa.title);
		holder.read.setText(""+qa.readnum);
		holder.reply.setText(""+qa.replynum);
		
		return convertView;
	}

	
	
	
	public static class ItemHolder {
		TextView title;
		TextView read;
		TextView reply;
		ImageView img;
	}




	@Override
	public void onLoaded(Bitmap aBitmap, String strUrl) {
		this.notifyDataSetChanged();
	}

	@Override
	public void onError(String strUrl) {
	}

	@Override
	public void onDestroy() {
		if(this.mImageLoader!=null)
			mImageLoader.cleanup();// TODO Auto-generated method stub
	}
}
