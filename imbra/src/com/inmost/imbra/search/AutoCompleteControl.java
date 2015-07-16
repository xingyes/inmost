package com.inmost.imbra.search;

import com.inmost.imbra.util.braConfig;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnErrorListener;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import java.util.HashMap;

public class AutoCompleteControl {

	private HashMap<String, SmartBoxModel> dataPool;

	public AutoCompleteControl() {
		dataPool = new HashMap<String, SmartBoxModel>();
	}

	public Ajax send(SearchSuggestParser parser, final String keyWord, final OnSuccessListener<SmartBoxModel> success, OnErrorListener error) {
		SmartBoxModel data = dataPool.get(keyWord);
		if (data != null) {
			success.onSuccess(data, null);
			return null;
		}

		Ajax mAjax = ServiceConfig.getAjax(braConfig.URL_QUERY_SUGGEST);
		if( null == mAjax )
			return null;
		mAjax.setParser(parser);
		mAjax.setData("keywords", keyWord);
		mAjax.setData("cat", 1);
		mAjax.setOnSuccessListener(new OnSuccessListener<SmartBoxModel>() {
			@Override
			public void onSuccess(SmartBoxModel v, Response response) {
				dataPool.put(keyWord, v);
				success.onSuccess(v, null);
			}
		});
		mAjax.setOnErrorListener(error);
		mAjax.send();

		return mAjax;
	}
}
