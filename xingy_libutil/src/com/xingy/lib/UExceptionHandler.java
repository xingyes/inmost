package com.xingy.lib;

import com.xingy.util.Log;
import com.xingy.util.ToolUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

public class UExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final String LOG_TAG = UExceptionHandler.class.getName();

	private UncaughtExceptionHandler mHandler = null;

	public UExceptionHandler() {
		mHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;
		FileWriter writer = null;

		try {
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (printStream != null) {
					printStream.close();
				}
				if (baos != null) {
					baos.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mHandler != null) {
				mHandler.uncaughtException(thread, ex);
			}
		}
	}
}
