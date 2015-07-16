package com.xingy.util.ajax;

import com.xingy.util.Log;

import org.json.JSONObject;

public class JSONParser extends Parser<byte[], JSONObject> {

	private static final String LOG_TAG = JSONParser.class.getName();
	
	private String mStr;

	@Override
	public JSONObject parse(byte[] bytes, String charset) throws Exception {
		
		mStr = new String(bytes, 0, bytes.length, charset);
		
		final int nPos = mStr.indexOf("{\"");
		if( nPos > 0 ) {
			mStr = mStr.substring(nPos);
		}

		Log.d(LOG_TAG, mStr);

		JSONObject output = new JSONObject(mStr);

		return output;
	}

	public String getString() {
		return mStr;
	}
}