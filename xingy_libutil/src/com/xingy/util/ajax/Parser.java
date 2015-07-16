package com.xingy.util.ajax;

public abstract class Parser<I, O> {

	public abstract O parse(I input, String charset) throws Exception;

	protected boolean mIsSuccess;

	protected int mErrCode;

	protected String mErrMsg;

	public void clean() {
		mIsSuccess = false;
		mErrMsg = "";
		mErrCode = 0;
	}

	public boolean isSuccess() {
		return mIsSuccess;
	}

	public void setSuccess(boolean success) {
		mIsSuccess = success;
	}

	public int getErrCode() {
		return mErrCode;
	}

	public String getErrMsg() {
		return mErrMsg;
	}
	
	public void setErrMsg(String strErrMsg) {
		mErrMsg = strErrMsg;
	}

}
