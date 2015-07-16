package com.xingy.util.ajax;

import android.content.Context;

import com.xingy.util.Log;
import com.xingy.util.ToolUtil;

import org.apache.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HttpStream extends HttpGet {

	private static final String LOG_TAG = HttpStream.class.getName();

	static private String end = "\r\n";

	static private String twoHypens = "--";

	static private String boundary = "--------7da3d81520810*";

	private HashMap<String, String> mFileNames;
	private HashMap<String, byte[]> mFiles;

	public HttpStream(Context context) {
		super(context);
	}

	@Override
	public boolean send() {

		boolean isSuccess = false;

		HttpURLConnection conn = null;

		ByteArrayOutputStream output = null;

		InputStream input = null;
		String strNetTypeName= "";
		DataOutputStream ds = null;
		try {
			String sUrl = getUrl();

			if (sUrl == null) {
				throw new Exception("you have not set request url");
			}

			Log.d(LOG_TAG, sUrl);

			URL url = new URL(sUrl);
			conn = getConnect(mContext, url);
			checkCacnelStatus();
			conn.setConnectTimeout(getConnectTimeout());
			conn.setReadTimeout(getPostDataTimeout());
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			final HashMap<String, String> header = getRequestHeader();
			if (null != header) {
				final Iterator<Entry<String, String>> inerator = header.entrySet().iterator();
				while (inerator.hasNext()) {
					Entry<String, String> entry = inerator.next();
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			checkCacnelStatus();
			requestStart = new Date().getTime();
			conn.connect();
			checkCacnelStatus();

			ds = new DataOutputStream(conn.getOutputStream());
			final HashMap<String, Object> data = getData();

			if (data != null) {
				for (Entry<String, Object> entry : data.entrySet()) {
					if (checkCacnelStatus()) {
						break;
					}

					ds.writeBytes(twoHypens + boundary + end);
					String value = String.valueOf(entry.getValue());
					byte[] vbuffer = value.getBytes(requestCharset);
					ds.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + end);
					ds.writeBytes(end);
					ds.write(vbuffer);
					ds.writeBytes(end);
				}
			}

			if (mFiles != null) {
				for (Entry<String, byte[]> entry : mFiles.entrySet()) {
					if (checkCacnelStatus()) {
						break;
					}

					String k = entry.getKey();
					byte[] v = entry.getValue();
					String fileName = mFileNames.containsKey(k) ? (String)mFileNames.get(k) : "file";
					if (v == null) {
						continue;
					}
					ds.writeBytes(twoHypens + boundary + end);
					ds.writeBytes("Content-Disposition: form-data; name=\"" + k + "\"; filename=\"" + fileName + "\"" + end);
					ds.writeBytes(end);
					ds.write(v);
					ds.writeBytes(end);
				}
			}
			ds.writeBytes(twoHypens + boundary + twoHypens + end);
			ds.flush();
			

			checkCacnelStatus();

			final int responseCode = conn.getResponseCode();

			mHttpStatus = responseCode;

			if (responseCode != HttpStatus.SC_OK) {
				throw new java.net.SocketException("http status is not 200");
			}

			if (conn.getContentType().contains("text/vnd.wap.wml") == true && tryCount < getTryLimit()) {
				tryCount++;
				return send();
			}

			input = conn.getInputStream();
			final int totalSize = conn.getContentLength();

			byte[] buf = new byte[BUFFERSIZE];

			int num = 0, downLoaded = 0;

			output = new ByteArrayOutputStream(BUFFERSIZE);
			while (!checkCacnelStatus() && (num = input.read(buf)) != -1) {
				output.write(buf, 0, num);
				downLoaded += num;
				if (mOnProgressListener != null) {
					mOnProgressListener.onProgress(null, downLoaded, totalSize);
				}
			}
			requestEnd = new Date().getTime();
			setResult(conn, output);
			buf = null;
			isSuccess = true;
		} catch (CancelException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex));
		} catch (java.net.ConnectException ex) {
			strNetTypeName = HttpUtil.getNetTypeName();
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex) + " " + getId());
		} catch (java.net.SocketException ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex) + " " + getId());
			strNetTypeName = HttpUtil.getNetTypeName();
		}  catch (Exception ex) {
			Log.e(LOG_TAG, "HttpStatus = " + mHttpStatus + "\n" + ToolUtil.getStackTraceString(ex));
			strNetTypeName = HttpUtil.getNetTypeName();
		} finally {
			try {
				if(null!=ds)
				{
					ds.close();
					ds = null;
				}
				if (input != null) {
					input.close();
					input = null;
				}
				if (output != null) {
					output.close();
					output = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			} catch (Exception ex) {
				Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
			}
		}

		return isSuccess;
	}

	@Override
	public void setFile(String name, byte[] data, String fileName) {
		if (mFiles == null) {
			mFiles = new HashMap<String, byte[]>();
		}
		if (mFileNames == null) {
			mFileNames = new HashMap<String, String>();
		}

		mFiles.put(name, data);
		mFileNames.put(name, fileName);
	}
}