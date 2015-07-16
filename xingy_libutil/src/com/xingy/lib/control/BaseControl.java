package com.xingy.lib.control;

import com.xingy.util.activity.BaseActivity;

public class BaseControl {

	protected BaseActivity mActivity;

	public BaseControl(BaseActivity activity) {
		mActivity = activity;
	}
	
	public void destroy(){
		mActivity = null;
	}
}
