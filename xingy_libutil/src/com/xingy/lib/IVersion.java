package com.xingy.lib;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.xingy.R;
import com.xingy.lib.model.VersionModel;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.Log;
import com.xingy.util.MyApplication;

public class IVersion {

	private final static String LOG_TAG = IVersion.class.getName();

	public static void notify(final Activity activity, final VersionModel model) {
		if (model == null) {
			return;
		}
		
		AppDialog.OnClickListener pListener = new AppDialog.OnClickListener() {
			@Override
			public void onDialogClick(int nButtonId) {
				if (nButtonId == DialogInterface.BUTTON_POSITIVE) {
					Intent intent = new Intent(Intent. ACTION_VIEW); 
					intent.setData(Uri.parse(model.getUrl()));
					activity.startActivity(intent);
					
				} else if (nButtonId == DialogInterface.BUTTON_NEGATIVE) {
					if (model.isForceUpdate()) {
						activity.finish();
					}
				}
			}
		};
		
		UiUtils.showDialog(activity, activity.getString(R.string.caption_new_version), model.getDesc(), R.string.btn_upgrade, (model.isForceUpdate() ? R.string.btn_exit : R.string.btn_later), pListener);
	}

	private static String versionName;

	private static int versionCode;

	public static String getVersionName() {
		if (versionName != null) {
			return versionName;
		}

		PackageManager manager = MyApplication.app.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(MyApplication.app.getPackageName(), 0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, e);
			versionName = "";
		}

		return versionName;
	}

	public static int getVersionCode() {
		if (versionCode != 0) {
			return versionCode;
		}

		PackageManager manager = MyApplication.app.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(MyApplication.app.getPackageName(), 0);
			versionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, e);
			versionCode = -1;
		}

		return versionCode;
	}
}
