package com.xingy.util.ajax;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageParser extends Parser<byte[], Bitmap> {

	@Override
	public Bitmap parse(byte[] bytes, String charset) throws Exception {
		Bitmap pResult = null;
		try {
			pResult = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch( OutOfMemoryError aError ) {
			aError.printStackTrace();
			System.gc();
			pResult = null;
		}
		return pResult;
	}
}