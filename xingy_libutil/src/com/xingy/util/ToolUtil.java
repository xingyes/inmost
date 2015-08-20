package com.xingy.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.widget.TextView;

import com.xingy.R;
import com.xingy.lib.ILogin;
import com.xingy.lib.IVersion;
import com.xingy.lib.model.Account;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.preference.Preference;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Cookie;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ToolUtil {

	private static String[] WEEKDAYS = new String[] { "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

	private static String[] MONTHS = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

	private static final String LOG_TAG = ToolUtil.class.getName();

	private static float mDensity = 0;

	private static int mAppWidthDip = 0;
	
	private static int mAppWidth = 0;
    private static int mAppHeight = 0;

	private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static int GO_CROP_ACTIVITY = 10001;
	public static float getDensity() {
		if (mDensity != 0)
			return mDensity;

		mDensity = MyApplication.app.getResources().getDisplayMetrics().density;

		return mDensity;

	}

	public static int getAppWidthDip() {
		if (mAppWidthDip != 0)
			return mAppWidthDip;

		final Context context = MyApplication.app;
		mAppWidthDip = ToolUtil.px2dip(context, getEquipmentWidth(context));

		return mAppWidthDip;
	}

	public static int getAppWidth() {
		if (mAppWidth != 0)
			return mAppWidth;

		mAppWidth = getEquipmentWidth(MyApplication.app);

		return mAppWidth;
	}

    public static int getAppHeight() {
        if (mAppHeight != 0)
            return mAppHeight;

        mAppHeight = getEquipmentHeight(MyApplication.app);

        return mAppHeight;
    }


	public static float getDensityDpi() {
		return MyApplication.app.getResources().getDisplayMetrics().densityDpi;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = getDensity();
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getEquipmentWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getEquipmentHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	
	public static void startActivity(Activity activity, Intent intent, Bundle params, int requestFlag) {

		if (null != params) {
			intent.putExtras(params);
		}

		activity.startActivityForResult(intent, requestFlag);
		//ToolUtil.sendTrack(activity.getClass().getName(), intent.getComponent().getClassName(),"","");
	}

	public static void startActivity(Activity activity, Class<?> which, Bundle params, int requestFlag) {
		Intent intent = new Intent(activity, which);
		startActivity(activity, intent, params, requestFlag);
	}

	/*
	public static void startVoiceSearchActivity(final Activity activity, int VOICE_RECOGNITION_REQUEST_CODE) {
		try {
			activity.getPackageManager().getPackageInfo("com.google.android.voicesearch", 0);
			
			// ???锟�?Intent锟�????锟�???锟斤拷????????妯★拷??锟�?锟�????锟�????
			Intent intent = new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			// 锟�?锟�?妯★拷??????????锟芥ā锟�????锟�???锟斤拷?????
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			// ???绀猴拷????锟斤拷??锟�?
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "锟�?锟�?锟�????");
			// 锟�?锟�?锟�???锟斤拷?????
			activity.startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		}catch (NameNotFoundException ex) {
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(ex));
			UiUtils.showDialog(activity, R.string.caption_google_voice, R.string.message_not_support_google_voice, R.string.btn_install, R.string.btn_cancel, new AppDialog.OnClickListener() {
				@Override
				public void onDialogClick(int nButtonId) {
					if (nButtonId == AppDialog.BUTTON_POSITIVE) {
						Intent intent = new Intent(Intent. ACTION_VIEW); 
						intent.setData(Uri.parse(Config.URL_DOWNLOAD_VOICESEARCH));
						activity.startActivity(intent);
					}
				}
			});
		} catch (Exception e) {
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(e));
			Toast.makeText(activity, "??锟斤拷????锟斤拷????锟斤拷?锟斤拷??", Toast.LENGTH_LONG).show();
		}
	}*/
	
	
	public static void startActivity(Activity activity, Class<?> target, Bundle params) {
		startActivity(activity, target, params, -1);
	}

	public static void startActivity(Activity activity, Class<?> target) {
		startActivity(activity, target, null);
	}

	public static String getMD5(String val) {

		String ret = null;

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(val.getBytes());
			byte[] m = md5.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < m.length; i++) {
				sb.append(m[i]);
			}

			ret = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			Log.e(LOG_TAG, ex);
		}

		return ret;
	}
	
	/**
	 * toMD5
	 * The version to match PHP version.
	 * @param strVal
	 * @return
	 */
	public static String toMD5(String strVal) 
	{
		if( TextUtils.isEmpty(strVal) )
			return "";
		
		String strResult = "";
        try 
        {
            MessageDigest pDigest = MessageDigest.getInstance("MD5");
            pDigest.reset();
            pDigest.update(strVal.getBytes());
            
            BigInteger hash = new BigInteger(1, pDigest.digest());  
            strResult = hash.toString(16);  
            while(strResult.length() < 32) 
            {  
            	strResult = "0" + strResult;  
            }
        }
        catch (NoSuchAlgorithmException e)
        {
        	e.printStackTrace();
        	strResult = "";
        }
        
        return strResult;
	}

	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		return android.util.Log.getStackTraceString(tr);

		// return tr.getMessage();
	}

	public static String getPriceStr(long fen) {
		return String.valueOf(new java.text.DecimalFormat("#0.00").format(fen / 100));
	}

	public static Cookie getCookie() {
		
		Account account = ILogin.getActiveAccount();
		
		Cookie cookie = new Cookie();
		if (account != null) {
			cookie.set("uid", account.getUid());
		}
		

		//cookie.set("ls", "--android--");
		cookie.set("version", ""+IVersion.getVersionCode());
		
		//cookie.set("appVersion", IVersion.getVersionCode() + "");
		//cookie.set("appSource", "android;");
		cookie.set("deviceid", getDeviceUid(MyApplication.app));
		cookie.set("deviceDetail", Build.MODEL);
		cookie.set("platform", "android");
		
		return cookie;
	}

	public static boolean isEmptyList(JSONObject json, String key) {
		JSONArray jarray = json.optJSONArray(key);
		if(null != jarray) //"[]"
			return (jarray.length()<=0);
		
		JSONObject jobj = json.optJSONObject(key);// ! {}
		if(jobj!=null  && null!=jobj.names())//{aa:b}
			return false;
		
		final String str = json.optString(key);

		return str.equals("") || str.equals("{}")|| str.equals("[]") || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("false");
	}

	/*
	 * public static Ajax loadImage(BaseActivity activity, final ImageView view,
	 * String url) { Ajax ajax = AjaxUtil.getImage(url);
	 * ajax.setOnSuccessListener(new OnSuccessListener<Bitmap>() {
	 * 
	 * @Override public void onSuccess(Bitmap v, Response response) { if (view
	 * != null) { view.setImageBitmap(v); } } }); activity.addAjax(ajax);
	 * ajax.send();
	 * 
	 * return ajax; }
	 */

	public static String toPrice(double fen, int len) {
//		String format = len == 2 ? "#0.00" : "#0.0";
		double pRemainder = fen % 100;
		if( 0 == pRemainder ) {
			return String.valueOf(new java.text.DecimalFormat("#0").format(fen/100));
		}
		return String.valueOf(fen/100);
	}

	/**
	 * 
	 * @param dValue
	 * @return
	 */
	 public static String toDiscount(double dValue)
	{
		BigDecimal BDdiscount = new BigDecimal(dValue);
		double percent = BDdiscount.setScale(0,BigDecimal.ROUND_UP).doubleValue();
		if(percent%10 == 0)
			return String.valueOf(new java.text.DecimalFormat("#0").format(percent/10));
		else
			return String.valueOf(percent/10);
	}
	
	public static String toPrice(double fen) {
		return toPrice(fen, 2);
	}
	
	public static String toPriceInterger(double fen) {
		return String.valueOf(new java.text.DecimalFormat("#0").format(fen/100));
	}

	public static String toDate(long milliSeconds, String formatStr) {
		Date date = new Date(milliSeconds);
	//	return SimpleDateFormat.getDateTimeInstance().format(date);
		return new SimpleDateFormat(formatStr).format(date);
	}

	// @param format yyyy-MM-dd HH:mm:ss
	public static String toDate(long milliSeconds) {
		return mDateFormat.format(new Date(milliSeconds));
	}

	public static String formatSuitableDate(long milliSeconds) {
		long cur = System.currentTimeMillis();
		if(milliSeconds > cur)
		{
			Calendar calendar = Calendar.getInstance();//获取系统时间
			calendar.setTimeInMillis(milliSeconds);
			
			Calendar nowcalendar = Calendar.getInstance();//获取系统时间
			nowcalendar.setTimeInMillis(cur);
			
			int a = calendar.get(Calendar.YEAR);
			int b = nowcalendar.get(Calendar.YEAR);
			if(a == b)//year
			{
				a = calendar.get(Calendar.MONTH);
				b = nowcalendar.get(Calendar.MONTH);
				if(a == b)//month
				{
					a = calendar.get(Calendar.WEEK_OF_MONTH);
					b = nowcalendar.get(Calendar.WEEK_OF_MONTH);
					if(a == b)//week
					{
						a = calendar.get(Calendar.DATE);
						b = nowcalendar.get(Calendar.DATE);
						if(a == b)//date
						{
							return "今日" + calendar.get(Calendar.HOUR_OF_DAY) + "时";
						}
						else
						{
							return "" + (a -b) + "日后";
						}
					}
					else
					{
						return "" + (a -b) + "周后";
					}
				}
				else
				{
					return "" + (a -b) + "月后";
				}
			}
			else
			{
				return "" + (a -b) + "年后";
			}
		}
		else
			return toDate(milliSeconds);
	}

	public static long parseDate(String string) {
		int offset = 0, length = string.length(), state = 0;
		int year = -1, month = -1, date = -1;
		int hour = -1, minute = -1, second = -1;
		final int PAD = 0, LETTERS = 1, NUMBERS = 2;
		StringBuilder buffer = new StringBuilder();

		while (offset <= length) {
			char next = offset < length ? string.charAt(offset) : '\r';
			offset++;

			int nextState;
			if ((next >= 'a' && next <= 'z') || (next >= 'A' && next <= 'Z'))
				nextState = LETTERS;
			else if (next >= '0' && next <= '9')
				nextState = NUMBERS;
			else if (" ,-:\r\t".indexOf(next) == -1)
				throw new IllegalArgumentException();
			else
				nextState = PAD;

			if (state == NUMBERS && nextState != NUMBERS) {
				int digit = Integer.parseInt(buffer.toString());
				buffer.setLength(0);
				if (digit >= 70) {
					if (year != -1 || (next != ' ' && next != ',' && next != '\r'))
						throw new IllegalArgumentException();
					year = digit;
				} else if (next == ':') {
					if (hour == -1)
						hour = digit;
					else if (minute == -1)
						minute = digit;
					else
						throw new IllegalArgumentException();
				} else if (next == ' ' || next == ',' || next == '-' || next == '\r') {
					if (hour != -1 && minute == -1)
						minute = digit;
					else if (minute != -1 && second == -1)
						second = digit;
					else if (date == -1)
						date = digit;
					else if (year == -1)
						year = digit;
					else
						throw new IllegalArgumentException();
				} else if (year == -1 && month != -1 && date != -1)
					year = digit;
				else
					throw new IllegalArgumentException();
			} else if (state == LETTERS && nextState != LETTERS) {
				String text = buffer.toString().toUpperCase(Locale.getDefault());
				buffer.setLength(0);
				if (text.length() < 3)
					throw new IllegalArgumentException();
				if (parse(text, WEEKDAYS) != -1) {
				} else if (month == -1 && (month = parse(text, MONTHS)) != -1) {
				} else if (text.equals("GMT")) {
				} else
					throw new IllegalArgumentException();
			}

			if (nextState == LETTERS || nextState == NUMBERS)
				buffer.append(next);
			state = nextState;
		}

		if (year != -1 && month != -1 && date != -1) {
			if (hour == -1)
				hour = 0;
			if (minute == -1)
				minute = 0;
			if (second == -1)
				second = 0;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			int current = cal.get(Calendar.YEAR) - 80;
			if (year < 100) {
				year += current / 100 * 100;
				if (year < current)
					year += 100;
			}
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, date);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime().getTime();
		}
		throw new IllegalArgumentException();
	}

	private static int parse(String string, String[] array) {
		int length = string.length();
		for (int i = 0; i < array.length; i++) {
			if (string.regionMatches(true, 0, array[i], 0, length))
				return i;
		}
		return -1;
	}

	public static boolean checkApkInstalled(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (PackageInfo pack : pkgList) {
			if (pack.packageName.equalsIgnoreCase(packageName)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * 
	* method Name:getInstalledPackageInfo    
	* method Description:  
	* @param context
	* @param packageName
	* @return   
	* PackageInfo  
	* @exception   
	* @since  1.0.0
	 */
	public static PackageInfo getInstalledPackageInfo(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (PackageInfo pack : pkgList) {
			if (pack.packageName.equalsIgnoreCase(packageName)) {
				return pack;
			}
		}
		
		return null;
	}

	public static void shell(String command) {
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(e));
		}
	}

	public static long getSDSizeSpare() {
		File pathFile = Environment.getExternalStorageDirectory();
		StatFs statfs = new StatFs(pathFile.getPath());
		long nAvailaBlock = statfs.getAvailableBlocks();
		long nBlocSize = statfs.getBlockSize();
		return (nAvailaBlock * nBlocSize) / 1024;
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static void setCrossLine(TextView view) {
		view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		view.getPaint().setAntiAlias(true);
	}

	public static void installApk(Context context, File apkFile) {
		if (apkFile == null) {
			return;
		}

		ToolUtil.shell("chmod 777 " + apkFile.getAbsolutePath());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(apkFile), type);
		context.startActivity(intent);
	}
	
	
	/*
	 * uid  uin qq ??锟界郴
	 * 
	 *  qq       ??锟斤拷??绫伙拷?? 		=>  uin
	 * 		  娌★拷????锟斤拷??锟�?QQ??锟斤拷??			qq
	 * 			???QQ??锟斤拷??				0
	 * 
	 */
	public static String getUinForReport() {
		String struin = "0";
		String qq = String.valueOf(Preference.getInstance().getQQAccount());
		if(TextUtils.isEmpty(qq)) {
			return struin;
		}
		else
			return qq;
	}

    public static int getApkVersionCode(String absolutePath) {

        if (absolutePath == null) {
            return -1;
        }

        File file = new File(absolutePath);

        if (!file.exists()) {
            return -1;
        }

        PackageManager pm = MyApplication.app.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(absolutePath, PackageManager.GET_ACTIVITIES);
        if (info == null) {
            return -1;
        }

        return info.versionCode;
    }
	
	public static String getApkVersionName(String absolutePath) {

		if (absolutePath == null) {
			return null;
		}

		File file = new File(absolutePath);

		if (!file.exists()) {
			return null;
		}

		PackageManager pm = MyApplication.app.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(absolutePath, PackageManager.GET_ACTIVITIES);
		if (info == null) {
			return null;
		}

		return info.versionName;
	}

	public static String getChannel() {
		if( ToolUtil.isSimulator() ) {
			return "simulator";
		}
		
		// Load information from assert for channel value
		String channel = "";
		try {
			InputStream input = MyApplication.app.getAssets().open("channel", AssetManager.ACCESS_STREAMING);
			if (input != null) 
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(input));
				String line;
				if ((line = in.readLine()) != null) {
					channel = line;
				}
				
				input.close();
				input = null;
			}
		} catch (IOException ex) {
			Log.e(LOG_TAG, ex);
			channel = "";
		}

		return channel;
	}
	
	public static boolean isSimulator() {
		//	String model = Build.MODEL;
		    String product = Build.PRODUCT;
		    boolean isEmulator = false;
		    if (product != null) {
		        isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
		    }
		    return isEmulator;
		}

	public static boolean isSDExists() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static String getExtension(String fileName) {
		if (null == fileName || fileName.length() == 0)
			return "";

		int index = fileName.lastIndexOf(".");

		if (index != -1 && index < fileName.length() - 1) {
			return fileName.substring(index);
		}

		return "";
	}
	
    private static final char[] base64EncodeChars = new char[] { 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
		'w', 'x', 'y', 'z', '0', '1', '2', '3', 
		'4', '5', '6', '7', '8', '9', '+', '/' 
	}; 
    
	public static String base64Encode(byte[] data) { 
		StringBuffer sb = new StringBuffer(); 
		int len = data.length; 
		int i = 0; 
		int b1, b2, b3; 

		while (i < len) { 
			b1 = data[i++] & 0xff; 
			if (i == len) { 
			sb.append(base64EncodeChars[b1 >>> 2]); 
			sb.append(base64EncodeChars[(b1 & 0x3) << 4]); 
			sb.append("=="); 
			break; 
			} 
			b2 = data[i++] & 0xff; 
			if (i == len) { 
				sb.append(base64EncodeChars[b1 >>> 2]); 
				sb.append( 
				base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]); 
				sb.append("="); 
				break; 
			} 
			b3 = data[i++] & 0xff; 
			sb.append(base64EncodeChars[b1 >>> 2]); 
			sb.append( 
			base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
			sb.append( 
			base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]); 
			sb.append(base64EncodeChars[b3 & 0x3f]); 
		} 
		return sb.toString(); 
	}
	
	public static int compareInt(int num, int num2) {
		if(num > num2) {
			return 1;
		}else if(num < num2){
			return -1;
		}else{
			return 0;
		}
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equalsStrings(String[] a, String []b)
	{
		if(null==a && null==b)
			return true;
		else if(null == a || null == b)
			return false;
		
		if(a.length!=b.length)
			return false;
		
		for(int i = 0; i< a.length; i++)
		{
			if(!a[i].equals(b[i]))
				return false;
		}
		
		return true;
	}
	

	public static CellInfo getCellInfo(Context aContext)
	{
		TelephonyManager manager = (TelephonyManager)aContext.getSystemService(Context.TELEPHONY_SERVICE);  
	     if(null == manager)
	    	 return  null;
	     
		int netType = manager.getNetworkType();
	    CellInfo aInfo = new CellInfo();
		
		if (netType == TelephonyManager.NETWORK_TYPE_GPRS              // GSM锟�?  
                || netType == TelephonyManager.NETWORK_TYPE_EDGE  
                || netType == TelephonyManager.NETWORK_TYPE_HSDPA)  
        {  
            GsmCellLocation gsm = ((GsmCellLocation) manager.getCellLocation());  
            if (gsm == null)  
                return null;  
            
            String operator = manager.getNetworkOperator();  
    	    aInfo.lac = gsm.getLac();  
            aInfo.mcc = operator.substring(0, 3);  
            aInfo.mnc = operator.substring(3, 5);  
            aInfo.cellId = gsm.getCid();  
            aInfo.radioType = "gsm";  
        }else if (netType == TelephonyManager.NETWORK_TYPE_CDMA        // ??锟戒俊cdma锟�?  
                || netType == TelephonyManager.NETWORK_TYPE_1xRTT  
                || netType == TelephonyManager.NETWORK_TYPE_EVDO_0  
                || netType == TelephonyManager.NETWORK_TYPE_EVDO_A)  
        {  
              
            CdmaCellLocation cdma = (CdmaCellLocation) manager.getCellLocation();     
            if (cdma == null)  
            	return null;  
              
            String operator = manager.getNetworkOperator();  
            aInfo.lac = cdma.getNetworkId();  
            aInfo.mcc = operator.substring(0, 3);  
            aInfo.mnc = ""+cdma.getSystemId();  
            aInfo.cellId = cdma.getBaseStationId();  
            aInfo.radioType = "cdma";  
              
            // ??锟斤拷????锟斤拷????锟斤拷??淇★拷??  
            /*List<NeighboringCellInfo> list = manager.getNeighboringCellInfo();  
            int size = list.size();  
            for (int i = 0; i < size; i++) {  
  
                CellIDInfo info = new CellIDInfo();  
                info.cellId = list.get(i).getCid();  
                info.mobileCountryCode = mcc;  
                info.mobileNetworkCode = mnc;  
                info.locationAreaCode = lac;  
              
                CellID.add(info);  
            }  */
        }  
		return aInfo;
	}
	
	
	public static NetworkInfo getAvailableInfo(Context aContext)
	{
		if ( null == aContext )
			return null;
		
		// Get the connectivity manager.
		ConnectivityManager pManager = (ConnectivityManager)aContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if ( null == pManager )
			return null;
		
		// Get current active network information.
		NetworkInfo pInfo = pManager.getActiveNetworkInfo();
		if ( null == pInfo )
		{
			// Get current active network information.
			NetworkInfo[] aAllInfo = pManager.getAllNetworkInfo();
			final int nSize = (null != aAllInfo ? aAllInfo.length : 0);
			for ( int nIdx = 0; nIdx < nSize; nIdx++ )
			{
				NetworkInfo pEntity = aAllInfo[nIdx];
				if ( (null != pEntity) && (pEntity.isAvailable() && (pEntity.isConnectedOrConnecting())) )
				{
					pInfo = pEntity;
					break;
				}
			}
			
			aAllInfo = null;
		}
		return pInfo;
	}
	
	/**
	 * isNetworkAvailable
	 * @return
	 */
	public static boolean isNetworkAvailable(Context aContext)
	{
		return null != getAvailableInfo(aContext);
	}
	
	/**
	 * getDeviceId
	 * @param aContext
	 * @return
	 */
	public static String getDeviceUid(Context aContext)
	{
		// Get the device id.
		String strDeviceId = getIMEI(aContext.getApplicationContext());

        String strMacAddr = getMacAddr(aContext.getApplicationContext());
        final boolean bWithMac = !TextUtils.isEmpty(strMacAddr);
        if( bWithMac )
        {
        	strMacAddr = strMacAddr.replaceAll(":", "");
        }
        
        // Compose the result.
        String strUid = encryptUid(strDeviceId + (bWithMac ? strMacAddr : ""));
        
        return strUid;
	}
	
	public static String getMacAddr(Context aContext)
	{
		if(aContext == null) {
			return null;
		}
		// Permission: <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
		// Get mac address.
		String strMacAddr = null;
		
		WifiManager pWifiMan = (WifiManager) aContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE); 
		if(pWifiMan != null) {
			WifiInfo pWifiInfo = pWifiMan.getConnectionInfo();
			if(pWifiInfo != null) {
				strMacAddr = pWifiInfo.getMacAddress();
			}
		}

		return strMacAddr;
	}
	
	public static String getIMEI(Context aContext)
	{
		// Get the device id.
		String strDeviceId = Secure.getString(aContext.getContentResolver(), Secure.ANDROID_ID);
		
		// Some devices can not get id by ANDROID_ID indicator.
		if ( TextUtils.isEmpty(strDeviceId) )
		{
			// Try another way to retrieve device id.
			TelephonyManager pManager = (TelephonyManager) aContext.getApplicationContext().
				getSystemService(Context.TELEPHONY_SERVICE);
			if(pManager != null) {
				
				strDeviceId = pManager.getDeviceId();
			}
		}
		
		return strDeviceId;
	}
	
	public static String getIMSI()
	{
		TelephonyManager telManager = (TelephonyManager) MyApplication.app.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId();
		
		return imsi;
	}
	
	/**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
	
	
	/**
	 * getTestId
	 * @return
	 */
	public static String getTestId(Context aContext)
	{
		if( null == aContext )
			return "";
		
		// Get application name.
		String strName = aContext.getPackageName();
		final int nPos = strName.indexOf(".");
		strName = strName.substring(nPos + 1);
		final String strOrigin = strName + "6.6260693";
		
		// Compose the buffer.
		String strDevId = getDeviceUid(aContext);
		StringBuffer pBuffer = new StringBuffer(strDevId);
		pBuffer.append("&");
		final byte aArray[] = {67,104,66,105,67,105,66,105,67,105,69,102,74,97,69,102,67,100,70,100,71,97,67,105};
		final int nLength = strOrigin.length();
		for( int nIdx = 0; nIdx < nLength; nIdx++ )
		{
			byte nByte = aArray[nIdx];
			pBuffer.append((char) (strOrigin.charAt(nIdx) + getChar(nByte, nIdx)));
		}
		
	//	md5(device_id + "&" + secret_key);
		String strOrg = pBuffer.toString();
		return ToolUtil.toMD5(strOrg);
	}
	
	/**
	 * getChar
	 * @param aByte
	 * @param nPos
	 * @return
	 */
	private static char getChar(byte aByte, int nPos)
	{
		return (char) (aByte - getOffset((nPos & 0x01)));
	}
	
	/**
	 * getOffset
	 * @param bFlag
	 * @return
	 */
	private static byte getOffset(int nVal)
	{
		byte nByte = (byte) (0 == nVal ? 65 : 97);
		return nByte;
	}
	
	/**
	 * encryptUid
	 * @param strUid
	 * @return
	 */
	private static String encryptUid(String strUid)
	{
		if( TextUtils.isEmpty(strUid) )
			return "";
		
		StringBuilder pBuilder = new StringBuilder();
		final int nLength = strUid.length();
		for( int nIdx = 0; nIdx < nLength; nIdx++ )
		{
			final int nChar = strUid.charAt(nIdx);
			pBuilder.append(nChar + (nChar % nLength));
		}
		pBuilder.reverse();
		return pBuilder.toString();
	}
	
	public static boolean isEmpty(final String str)
	{
		boolean flag = TextUtils.isEmpty(str);
		if(!flag && "null".equalsIgnoreCase(str))
		{
			return true;
		}
		else 
			return flag;
		
	}
	
	public static boolean isPhoneNum(final String str)
	{
		return true;
		/*if(isEmpty(str))
			return false;
		if(!TextUtils.isDigitsOnly(str) || str.length()!=11)
			return false;
		long num = Long.valueOf(str);
		if(num >= 13000000000L && num <14000000000L)
			return true;
		if(num >= 15000000000L && num <16000000000L)
			return true;
		if(num >= 18000000000L && num <19000000000L)
			return true;
		
		else
			return false;
		*/
	}
	
	public  static void showClipIntentWithData(BaseActivity activity,Uri uri) {
		if (null == uri) 
			return;

//		AutoHeightImageView curImg = (AutoHeightImageView) mPicImages.get(mCurPicIdx);
//		String localPath = curImg.mCustomInfo.get("localPath");
//		if (null == localPath) {
//			localPath = mRoot + "/fbImg_" + sRandNameIdx + ".jpg";
//			sRandNameIdx++;
//			curImg.mCustomInfo.put("localPath", localPath);
//		}

		// 不知道为什么无法保存到本地路径，即使return-data设为false也没用
		//Uri imageUri = Uri.parse(localPath);//The Uri to store the big bitmap

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "false");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 240);
		intent.putExtra("outputY", 240);
		intent.putExtra("return-data", true);
		intent.putExtra("crop", false);
		intent.putExtra("noFaceDetection", true);
		//intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		activity.startActivityForResult(intent, GO_CROP_ACTIVITY);
	}
	
	
	public  static void showClipIntentWithData(Fragment fragment,Uri uri) {
		if (null == uri) 
			return;

		/*AutoHeightImageView curImg = (AutoHeightImageView) mPicImages.get(mCurPicIdx);
		String localPath = curImg.mCustomInfo.get("localPath");
		if (null == localPath) {
			localPath = mRoot + "/fbImg_" + sRandNameIdx + ".jpg";
			sRandNameIdx++;
			curImg.mCustomInfo.put("localPath", localPath);
		}*/

		// 不知道为什么无法保存到本地路径，即使return-data设为false也没用
		//Uri imageUri = Uri.parse(localPath);//The Uri to store the big bitmap
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 240);
		intent.putExtra("outputY", 240);
		intent.putExtra("crop", true);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		//intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		fragment.startActivityForResult(intent, GO_CROP_ACTIVITY);
	}
	
	public static boolean saveImgtoLocal(Context context,Bitmap bim)
	{
		if(null!=bim)
		{
			String uriStr = MediaStore.Images.Media.insertImage(context.getContentResolver(), bim, "", "");
	           
	        if(uriStr == null){
	            return false;
	        }
	           
	        String picPath  = getFilePathByContentResolver(context, Uri.parse(uriStr) );
	        if(picPath == null) {
	            return false;
	        }
	               
	        ContentResolver contentResolver = context.getContentResolver();
	        ContentValues values = new ContentValues(3);
	        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
	        values.put(Images.Media.ORIENTATION, 0);
	        values.put(Images.Media.DATA, picPath);
	                  
	        contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	           
	        return true;
		}
		return false;
	}
	
	
	private static String getFilePathByContentResolver(Context context, Uri uri) { 
		if (null == uri) 
			return null; 
		
		Cursor c = context.getContentResolver().query(uri, null, null, null, null); 
		String filePath = null; 
		if (null == c) 
			return null;
		
		try { 
			if((c.getCount() != 1) || !c.moveToFirst()) 
			{ 
			}
			else { 
				filePath = c.getString(c.getColumnIndexOrThrow(MediaColumns.DATA)); 
			} 
		} finally { 
			c.close(); 
		} 
		
		return filePath; 
	} 
	
	public static String toPlanString(long value)
	{
		final Context context = MyApplication.app;
		if(value < 10000)
			return ""+value;
		else if(value < 10000*10000)
			return context.getString(R.string.x_wan, String.format("%.2f", value/10000.0));
		else
			return context.getString(R.string.x_yi, String.format("%.2f", value/10000.0/10000.0));
	}

    public static int getMemoryClass(ActivityManager manager)
    {
        return (null == manager ? 0 : manager.getMemoryClass());
    }



    public static boolean checkAndCall(final Context aContext, final Intent intent) {
		if(aContext == null || null == intent)
			return false;

		if(null != intent.resolveActivity(aContext.getPackageManager())) {

			if(Preference.getInstance().needCallAccess())
			{
				UiUtils.showDialog(aContext,
                        R.string.permission_title, R.string.permission_hint_call, R.string.permission_agree, R.string.permission_disagree,
                        new AppDialog.OnClickListener() {
                            @Override
                            public void onDialogClick(int nButtonId) {
                                if (nButtonId == AppDialog.BUTTON_POSITIVE) {
                                    Preference.getInstance().setCallAccess(Preference.ACCESSED);
                                    aContext.startActivity(intent);
                                }
                            }
                        });
			}
			else
			{
				aContext.startActivity(intent);
			}
		}else
		{
			UiUtils.makeToast(aContext, R.string.phone_app_not_found);
		}

		return true;

	}
}
