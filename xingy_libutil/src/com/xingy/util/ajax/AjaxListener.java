package com.xingy.util.ajax;

public class AjaxListener<DataType> implements OnProgressListener, OnBeforeListener, OnSuccessListener<DataType>, OnErrorListener, OnCancelListener, OnFinishListener{

	@Override
	public void onFinish(Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel(Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Ajax ajax, Response response) {
	}

	@Override
	public void onBefore(Response mResponse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess(DataType v, Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProgress(Response response, int downLoaded, int totalSize) {
		// TODO Auto-generated method stub
		
	}
}
