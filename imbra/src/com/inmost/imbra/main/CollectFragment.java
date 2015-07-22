package com.inmost.imbra.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.inmost.imbra.R;
import com.inmost.imbra.blog.BlogVolleyActivity;
import com.inmost.imbra.collect.CollectPagerActivity;
import com.inmost.imbra.search_backup.SearchActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollectFragment extends Fragment implements OnSuccessListener<JSONObject>,
        AdapterView.OnItemClickListener,OnItemLongClickListener,View.OnClickListener {

    private BaseActivity mActivity;
    private View mRootView;

    private PullToRefreshListView pullList;
    private HomeFloorAdapter mAdapter;
    private int mNextPageNum;
    private Ajax mAjax;

    private ArrayList<HomeFloorModel> mHomeFloors;
    private ImageLoader mImgLoader;
    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.pg_home_floor, container, false);


        initView();

        mNextPageNum = 1;
        requestPage(mNextPageNum);
        mHomeFloors = new ArrayList<HomeFloorModel>();

        return mRootView;
    }


    @Override
    public void onAttach(Activity activity) {
        mActivity = (BaseActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void requestPage(int page) {
        mAjax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);
        if (null == mAjax)
            return;

        mAjax.setData("page", page);
        mActivity.showLoadingLayer();

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(mActivity);
        mAjax.send();
    }

    @Override
    public void onDestroy() {
        if (mAjax != null) {
            mAjax.abort();
            mAjax = null;
        }
        super.onDestroy();
    }


    private void initView() {
        pullList = (PullToRefreshListView) mRootView.findViewById(R.id.pull_refresh_list);
        pullList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && mNextPageNum > 1) {
                    UiUtils.makeToast(mActivity, "first:" + firstVisibleItem + ",vis:" +
                            visibleItemCount + ",totalItemCount" + totalItemCount);
                    requestPage(mNextPageNum);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });

        pullList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mHomeFloors.clear();
                mNextPageNum = 1;
                requestPage(mNextPageNum);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullList.onRefreshComplete();
                    }
                }, 5000);
            }
        });

        pullList.setOnItemClickListener(this);
        mAdapter = new HomeFloorAdapter(mActivity, mImgLoader);
        pullList.setAdapter(mAdapter);

    }


    @Override
    public void onSuccess(JSONObject v, Response response) {

        mActivity.closeLoadingLayer();
        mNextPageNum++;
//		final int ret = v.optInt("error");
//		if(ret != 0 )
//		{
//			String msg =  v.optString("data");
//			UiUtils.makeToast(mActivity, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg): msg);
//			return;
//		}
//
//		JSONObject data = v.optJSONObject("data");
//		if(null == data)
//		{
//			UiUtils.makeToast(mActivity, getString(R.string.parser_error_msg));
//			return;
//		}

        JSONArray feeds = v.optJSONArray("feeds");
        if (null != feeds) {
            for (int i = 0; i < feeds.length(); i++) {
                HomeFloorModel model = new HomeFloorModel();
                model.parse(feeds.optJSONObject(i));
                if(model.type.equals(HomeFloorModel.TYPE_COLLECTION))
                    mHomeFloors.add(model);
            }
        }
        mAdapter.setData(mHomeFloors);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        HomeFloorModel item = mHomeFloors.get(position - 1);
        if (item.type.equalsIgnoreCase(HomeFloorModel.TYPE_LOOKBOOK))
            UiUtils.makeToast(mActivity, "look");
        else if (item.type.equalsIgnoreCase(HomeFloorModel.TYPE_COLLECTION)) {
            Bundle bund = new Bundle();
            bund.putString(CollectPagerActivity.COLLECT_ID, item.type_id);
            UiUtils.startActivity(mActivity, CollectPagerActivity.class, bund, true);
        } else if (item.type.equalsIgnoreCase(HomeFloorModel.TYPE_BLOG)) {
            Bundle bund = new Bundle();
            bund.putString(BlogVolleyActivity.BLOG_ID, item.type_id);
            UiUtils.startActivity(mActivity, BlogVolleyActivity.class, bund, true);
        }
    }

    public void setImgLoader(ImageLoader imgLoader) {
        this.mImgLoader = imgLoader;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.navigationbar_right_icon) {
            UiUtils.startActivity(mActivity, SearchActivity.class, true);
        }
    }
}
