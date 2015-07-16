package com.xingy.lib.control;

import com.xingy.lib.model.VersionModel;
import com.xingy.util.Config;
import com.xingy.util.Log;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnErrorListener;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

public class VersionControl extends BaseControl {

	public VersionControl(BaseActivity activity) {
		super(activity);
	}

	public VersionControl() {
		this(null);
	}

	private static final String LOG_TAG =  VersionControl.class.getName();

	public Ajax getlatestVersionInfo(boolean ingoreCache, final OnSuccessListener<VersionModel> success, final OnErrorListener error) {
/*		final IPageCache cache = new IPageCache();
		String info = cache.get(CacheKeyFactory.LAST_VERSION_INFO);

		if (ingoreCache == false && info != null) {
			VersionModel model = null;
			try {
				JSONObject json = new JSONObject(info);
				model = new VersionModel();
				model.parse(json);
			} catch (Exception ex) {
				model = null;
				Log.e(LOG_TAG, ex);
			}

			if (model != null) {
				success.onSuccess(model, null);
				return null;
			}
		}*/

		final Ajax ajax = ServiceConfig.getAjax(Config.URL_CHECK_VERSION);
		if( null == ajax )
			return null;
		ajax.setData("channel", ToolUtil.getChannel());
		ajax.setOnSuccessListener(new OnSuccessListener<JSONObject>() {
			@Override
			public void onSuccess(JSONObject v, Response response) {
				final int errno = v.optInt("errno", -1);
				if (errno != 0) {
					if (error != null) {
						error.onError(ajax, response);
					}
					return;
				}
				// 更新最后询问时间
				String content;
				VersionModel model;

				try {
					content = v.getString("data");
					JSONObject json = new JSONObject(content);
					model = new VersionModel();
					model.parse(json);
				} catch (Exception ex) {
					Log.e(LOG_TAG, ex);
					content = null;
					model = null;
				}

				if (model != null) {
					//detect new version without delay.
					//cache.set(CacheKeyFactory.LAST_VERSION_INFO, content, 3600 * 12);
					success.onSuccess(model, null);
					return;
				}

				if (error != null) {
					error.onError(ajax, response);
				}
			}
		});
		ajax.setOnErrorListener(error);
		ajax.setData("v", "android");
		if (mActivity != null) {
			mActivity.addAjax(ajax);
		}
		ajax.send();

		return ajax;
	}
}
