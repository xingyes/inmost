
package com.xingy.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.xingy.R;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.preference.Preference;
import com.xingy.util.activity.BaseActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadPhotoUtil {

	public static AlertDialog dialog;
	public static String strImgPath;
	
	public static final int PHOTO_PICKED_WITH_DATA = 3021;
	public static final int PHOTO_CROPED_WITH_DATA = 3022;
	public static final int CAMERA_WITH_DATA = 3023;

	public static AlertDialog createUploadPhotoDlg(final BaseActivity context , final Fragment fragment) {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(context.getString(R.string.upload_photo));
		builder.setItems(new String[]{context.getString(R.string.take_photo),
								context.getString(R.string.choose_img_from_album)},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							if(Preference.getInstance().needToBarcodeAccess())
							{
								UiUtils.showDialog(context,
									R.string.permission_title, R.string.permission_hint_barcode,R.string.permission_agree, R.string.permission_disagree,
									new AppDialog.OnClickListener() {
									@Override
									public void onDialogClick(int nButtonId) {
										if (nButtonId == AppDialog.BUTTON_POSITIVE)
										{
											Preference.getInstance().setBarcodeAccess(Preference.ACCESSED);
											cameraMethod(fragment);
										}
									}
								});
							}
							else
							{
								cameraMethod(fragment);
							}
						} else if (which == 1) {
							galleryMethod(fragment);
						}
					}
				});

		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
	
	static private void cameraMethod(Fragment ac) {
		strImgPath = "";
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		strImgPath = Environment.getExternalStorageDirectory().toString()
				+ "/DCIM/Camera/";
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()) + ".jpg";
		File out = new File(strImgPath);
		if (!out.exists()) {
			out.mkdirs();
		}
		out = new File(strImgPath, fileName);
		strImgPath = strImgPath + fileName;

		Uri uri = Uri.fromFile(out);

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		ac.startActivityForResult(intent, CAMERA_WITH_DATA);
	}
	
	static public void galleryMethod(Fragment ac) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		ac.startActivityForResult(
				Intent.createChooser(intent, "Select Picture"),
				PHOTO_PICKED_WITH_DATA);
	}
	///---------------------------------------------------------
	
	public static AlertDialog createUploadPhotoDlg(final BaseActivity context) {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(context.getString(R.string.upload_photo));
		builder.setItems(new String[]{context.getString(R.string.take_photo),
								context.getString(R.string.choose_img_from_album)},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							if(Preference.getInstance().needToBarcodeAccess())
							{
								UiUtils.showDialog(context,
									R.string.permission_title, R.string.permission_hint_barcode,R.string.permission_agree, R.string.permission_disagree,
									new AppDialog.OnClickListener() {
									@Override
									public void onDialogClick(int nButtonId) {
										if (nButtonId == AppDialog.BUTTON_POSITIVE)
										{
											Preference.getInstance().setBarcodeAccess(Preference.ACCESSED);
											cameraMethod(context);
										}
									}
								});
							}
							else
							{
								cameraMethod(context);
							}
						} else if (which == 1) {
							galleryMethod(context);
						}
					}
				});

		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	static private void cameraMethod(BaseActivity ac) {
		strImgPath = "";
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		strImgPath = Environment.getExternalStorageDirectory().toString()
				+ "/DCIM/Camera/";
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()) + ".jpg";
		File out = new File(strImgPath);
		if (!out.exists()) {
			out.mkdirs();
		}
		out = new File(strImgPath, fileName);
		strImgPath = strImgPath + fileName;

		Uri uri = Uri.fromFile(out);

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		ac.startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	/**
	 * start system gallery activity
	 * 
	 * @param ac
	 */
	static public void galleryMethod(BaseActivity ac) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		ac.startActivityForResult(
				Intent.createChooser(intent, "Select Picture"),
				PHOTO_PICKED_WITH_DATA);
	}
	
	static public void galleryCropMethod(BaseActivity ac) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        ac.startActivityForResult(intent, PHOTO_CROPED_WITH_DATA);
	}

	static private String parseImgPath(Context context, Intent data) {
		if (context == null) {
			return null;
		}
		String path = null;
		if (data != null) {
			Uri uri = data.getData();
			if (uri != null) {
				if ("file".equals(uri.getScheme())) {
					String s = null;
					try {
						s = URLDecoder.decode(
								uri.toString().replaceFirst("file://", ""),
								"utf-8");
					} catch (UnsupportedEncodingException e) {
					}
					return s;
				}
				Cursor cursor = context.getContentResolver().query(uri, null,
						null, null, null);
				if (cursor == null)
					return null;
				try {
					cursor.moveToFirst();
					path = cursor.getString(cursor
							.getColumnIndex(MediaStore.MediaColumns.DATA));

				} catch (Exception e) {
					Toast.makeText(context, "请�??�?�????件夹�?�?�?", Toast.LENGTH_SHORT)
							.show();
					e.printStackTrace();
				} finally {
					if (cursor != null && !cursor.isClosed()) {
						cursor.close();
					}
				}
			}
		}

		return path;
	}

	/**
	 * get the photo's path (image from by gallery, the path can get by intent,
	 * but from camera, the path is specified before)
	 * 
	 * @param context
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return
	 */
	static public String getImgPath(BaseActivity context, int requestCode,
			int resultCode, Intent data) {
		
		if(strImgPath == null){
			//strImgPath = context.preferences().getString("uploadImgPath", null);
		}
		
		if (requestCode == UploadPhotoUtil.CAMERA_WITH_DATA) {
			if (resultCode == Activity.RESULT_OK) {
				if (!TextUtils.isEmpty(strImgPath)) {
					File f = new File(strImgPath);
					if (!f.exists()) {
						strImgPath = UploadPhotoUtil
								.parseImgPath(context, data);
					}
				} else {
					strImgPath = UploadPhotoUtil.parseImgPath(context, data);
				}

			}
		} else if (requestCode == UploadPhotoUtil.PHOTO_PICKED_WITH_DATA) {
			if (resultCode == Activity.RESULT_OK) {
				strImgPath = UploadPhotoUtil.parseImgPath(context, data);
			}
		}
		return strImgPath;
	}

}
