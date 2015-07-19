package com.inmost.imbra.search_backup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inmost.imbra.R;

import java.util.ArrayList;


public class AutoCompleteAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private ArrayList<AutoCompleteModel> mAutoCompleteModels;

	public AutoCompleteAdapter(Activity activity, ArrayList<AutoCompleteModel> mAutoCompleteModels) {
		mInflater = LayoutInflater.from(activity);
		this.mAutoCompleteModels = mAutoCompleteModels;
	}

	@Override
	public int getCount() {
		return mAutoCompleteModels.size();
	}

	@Override
	public Object getItem(int position) {
		return mAutoCompleteModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		ItemHolder holder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_item, null);
			holder = new ItemHolder();
			holder.name = (TextView) convertView.findViewById(R.id.search_item_name);
			holder.num = (TextView) convertView.findViewById(R.id.search_item_count);
			convertView.setTag(holder);
		} else {
			holder = (ItemHolder) convertView.getTag();
		}

		AutoCompleteModel model = mAutoCompleteModels.get(position);

		holder.name.setText(model.getName());
		if(0 != model.getNum() ){
			holder.num.setText("约" + model.getNum() + "件");
		}else{
			holder.num.setText("");
		}

		return convertView;
	}

	private static class ItemHolder {
		TextView name;
		TextView num;
	}
}
