package com.xingy.util.ajax;

import android.content.Context;
import android.os.Handler;

import com.xingy.util.Log;
import com.xingy.util.ToolUtil;

import org.apache.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HttpFile extends HttpGet {

	protected static final String LOG_TAG = HttpFile.class.getName();

	public static final int NET_MSG_GETLENTH = 1;

	public static final int NET_MSG_FINISH = 2;

	public static final int NET_MSG_ERROR = 3;

	public HttpFile(Context context) {
		super(context);
	}

	@Override
	public boolean send() {
		return false;
	}

	public boolean download(File file, Handler handler) {
		boolean isSuccess = false;

		HttpURLConnection conn = null;

		ByteArrayOutputStream output = null;

		InputStream input = null;

		FileOutputStream fileStream = null;

		try {

			String sUrl = getUrl();

			if (sUrl == null) {
				throw new Exception("you have not set request url");
			}

			sUrl = sUrl + (getData() == null ? "" : ((sUrl.indexOf("?") > -1 ? (sUrl.endsWith("&") ? "" : "&") : "?") + getParamString()));

			URL url = new URL(sUrl);

			conn = getConnect(mContext, url);
			checkCacnelStatus();
			conn.setConnectTimeout(getConnectTimeout());
			conn.setReadTimeout(getGetDataTimeout());

			final HashMap<String, String> header = getRequestHeader();
			if (null != header) {
				final Iterator<Entry<String, String>> inerator = header.entrySet().iterator();
				while (inerator.hasNext()) {
					Entry<String, String> entry = inerator.next();
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			requestStart = new Date().getTime();
			checkCacnelStatus();
			long file_length = 0; // file.length();
			fileStream = new FileOutputStream(file, true);
			
			if(mIsLimited == true){
				conn.addRequestProperty("Range", "bytes=0-" + String.valueOf(file_length + 200000));
			}else{
				conn.addRequestProperty("Range", "bytes=0-");
			}
			
			conn.connect();
			checkCacnelStatus();
			final int responseCode = conn.getResponseCode();

			if (!isFileSegSuccess(responseCode)) {
				throw new java.net.SocketException();
			}

			mHttpStatus = responseCode;

			if (conn.getContentType().contains("text/vnd.wap.wml") == true && tryCount < getTryLimit()) {
				conn.disconnect();
				tryCount++;
				return download(file, handler);
			}

			int contentLen =  Integer.valueOf( conn.getHeaderField("Content-Length") );

			input = conn.getInputStream();
			byte[] buf = new byte[BUFFERSIZE];
			int num = -1;
			int datalenth = 0;
			int notify_num = 0;
			if (contentLen > 0) {
				notify_num = contentLen / 50;
			}
			int notify_tmp = 0;
			if (handler != null && file_length > 0) {
				handler.sendMessage(handler.obtainMessage(NET_MSG_GETLENTH, (int) file_length, contentLen));
			}
			while (!checkCacnelStatus() && (num = input.read(buf)) != -1) {
				fileStream.write(buf, 0, num);
				datalenth += num;
				notify_tmp += num;
				if (handler != null && (notify_tmp > notify_num || datalenth == contentLen)) {
					notify_tmp = 0;
					handler.sendMessage(handler.obtainMessage(NET_MSG_GETLENTH, (int) (datalenth + file_length), contentLen));
				}
			}
			fileStream.flush();
			if (datalenth + file_length >= contentLen) {
				isSuccess = true;
				return isSuccess;
			}
		} catch (CancelException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ex);
		} catch (java.net.ConnectException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex) + " " + getId());
		} catch (java.net.SocketException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex) + " " + getId());
		} catch (Exception ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ex);
		} finally {
			try {
				if (input != null) {
					input.close();
					input = null;
				}
			} catch (Exception ex) {
				Log.e(LOG_TAG, ex);
			}

			try {
				if (output != null) {
					output.close();
					output = null;
				}
			} catch (Exception ex) {
				Log.e(LOG_TAG, ex);
			}

			try {
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			} catch (Exception ex) {
				Log.e(LOG_TAG, ex);
			}
			try {
				if (fileStream != null) {
					fileStream.close();
					fileStream = null;
				}
			} catch (Exception ex) {
				Log.e(LOG_TAG, ex);
			}
		}

		return isSuccess;

	}

	private boolean isFileSegSuccess(int mNetErrorCode) {
		if (mNetErrorCode != HttpStatus.SC_OK && mNetErrorCode != HttpStatus.SC_PARTIAL_CONTENT) {
			return false;
		} else {
			return true;
		}
	}

	public static void downLoadFile(final Context context, final Handler handler, final String url, final File file) {
		new Thread() {
			@Override
			public void run() {
				final File tmpFile = new File(file.getAbsoluteFile() + ".tmp");
				try {
					if (tmpFile.exists()) {
						tmpFile.delete();
						tmpFile.createNewFile();
					}
				} catch (Exception ex) {
					Log.e(LOG_TAG, ex);
					return;
				}

				HttpFile httpFile = new HttpFile(context);
				httpFile.setUrl(url);
				boolean isSuccess = httpFile.download(tmpFile, handler);
				if (isSuccess) {
					if (tmpFile != null) {
						tmpFile.renameTo(file);
						handler.sendMessage(handler.obtainMessage(HttpFile.NET_MSG_FINISH, file));
					}
				} else {
					handler.sendMessage(handler.obtainMessage(HttpFile.NET_MSG_ERROR, url));
				}
			}
		}.start();
	}
}
