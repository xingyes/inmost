package com.inmost.imbra.login;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.inmost.imbra.R;
import com.inmost.imbra.main.IMbraApplication;
import com.inmost.imbra.product.Product2RowAdapter;
import com.inmost.imbra.product.ProductDetailActivity;
import com.inmost.imbra.product.ProductModel;
import com.inmost.imbra.shopping.AddressListActivity;
import com.inmost.imbra.shopping.OrderActivity;
import com.inmost.imbra.shopping.OrderAdapter;
import com.inmost.imbra.shopping.OrderModel;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.CircleImageView;
import com.xingy.lib.ui.TextField;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.UploadPhotoUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MySettingActivity extends BaseActivity implements OnSuccessListener<JSONObject>{

	private Intent  mIntent = null;

    private ImageLoader      mImgLoader;

    private TextField        imgSetting;
    private Ajax             mAjax;
    private boolean          bChanged = false;
    private Account          account;
    private AppDialog        logoutDialog;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mysetting);

        setResult(RESULT_CANCELED);
        mIntent = this.getIntent();
        if(null == mIntent)
        {
            finish();
            return;
        }

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, IMbraApplication.globalMDCache);

        account = ILogin.getActiveAccount();

        if(null==account)
        {
            UiUtils.startActivity(this,VerifyLoginActivity.class,true);
            finish();
            return;
        }

        // 初始化布局元素
        initViews();

    }

	private void initViews() {
        loadNavBar(R.id.mysetting_nav);

        imgSetting = (TextField) findViewById(R.id.head);
        imgSetting.setOnClickListener(this);


        findViewById(R.id.nickname).setOnClickListener(this);
        findViewById(R.id.address).setOnClickListener(this);
        findViewById(R.id.bind_phone).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);


        if (null != account && !TextUtils.isEmpty(account.iconUrl))
            imgSetting.setPreNetIconUrl(account.iconUrl, mImgLoader);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == MyInfoActivity.MY_SETTING_CODE)
        {
            bChanged = true;
            setResult(RESULT_OK);
        }
        else if (requestCode == UploadPhotoUtil.PHOTO_PICKED_WITH_DATA && null != data)
        {
            // 雷军个傻逼
//            mIsProcessing = true;
            ToolUtil.showClipIntentWithData(this, data.getData());
        }
        else if (requestCode == UploadPhotoUtil.CAMERA_WITH_DATA)
        {
//            mIsProcessing = true;
            Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String path = UploadPhotoUtil.getImgPath(this, requestCode, resultCode, data);
            if(ToolUtil.isEmpty(path))
            {
                UiUtils.makeToast(MySettingActivity.this, "照片路径获取失败");
                return;
            }

            File file = new File(path);
            Uri fileUri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    fileUri));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Cursor cursor = getContentResolver().query(imgUri, null,
                    MediaStore.Images.Media.DISPLAY_NAME + "='"
                            + file.getName() + "'",
                    null, null);
            Uri uri = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToLast();
                long id = cursor.getLong(0);
                uri = ContentUris.withAppendedId(imgUri, id);
            }
            if(null!=cursor && !cursor.isClosed())
                cursor.close();
            ToolUtil.showClipIntentWithData(this,uri);
        }
        else if(requestCode == ToolUtil.GO_CROP_ACTIVITY)
        {
            //final AutoHeightImageView curImg = (AutoHeightImageView) mPicImages.get(mCurPicIdx);
            //String localPath = curImg.mCustomInfo.get("localPath");

            final Bitmap bitmap =  (null == data) ? null : (Bitmap)data.getParcelableExtra("data");
            imgSetting.setPreIconBitmap(bitmap);
            if (null != bitmap) {
                ByteArrayOutputStream  stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                bChanged = true;
                setResult(RESULT_OK);

//				File file = new File(localPath);
//			    FileOutputStream fOut = new FileOutputStream(file);
//				bitmap.compress(CompressFormat.JPEG, 100, fOut);
//				fOut.flush();
//				fOut.close();

                Ajax ajax = ServiceConfig.getAjax(braConfig.URL_IMAGE_STREAM_UPLOAD);
                if( null != ajax ) {
                    showLoadingLayer();
                    ajax.setData("token",account.token);
                    ajax.setFile("uavatar", byteArray);

//                    ajax.setFile("uavatar", byteArray);
                    ajax.setOnSuccessListener(this);
//                    ajax.setId(AJAX_UPLOAD_SNAPSHOT);
                    ajax.setOnErrorListener(this);
                    addAjax(ajax);
                    ajax.send();
                }
            }

//            mIsProcessing = false;
        }
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();
        int err = jsonObject.optInt("err");
        if(err!=0)
        {
            String msg =  jsonObject.optString("msg");
            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? getString(R.string.parser_error_msg) : msg);
            return;
        }

    }


    @Override
    public void onClick(View v)
    {
        Intent ait = null;
        switch (v.getId()) {
            case R.id.logout_btn:
                if(logoutDialog == null) {
                    logoutDialog = UiUtils.showDialog(this, R.string.caption_hint, R.string.r_u_sure_logout, R.string.btn_exit,
                            R.string.btn_cancel, new AppDialog.OnClickListener() {
                                @Override
                                public void onDialogClick(int nButtonId) {
                                    if (AppDialog.BUTTON_POSITIVE == nButtonId) {
                                        ILogin.clearAccount();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }
                            });
                }
                logoutDialog.show();
                break;
            case R.id.head:
                UploadPhotoUtil.createUploadPhotoDlg(MySettingActivity.this).show();
                break;
            case R.id.nickname:
                ait = new Intent(this,AlterInfoActivity.class);
                ait.putExtra(AlterInfoActivity.ALTER_ITEM, getString(R.string.real_name));
                ait.putExtra(AlterInfoActivity.SERVICE_URL_KEY, braConfig.URL_SET_INFO);
                ait.putExtra(AlterInfoActivity.PARAM_KEY, "nick");
                ait.putExtra(AlterInfoActivity.ORI_INFO, account.nickName);
                this.startActivityForResult(ait,MyInfoActivity.MY_SETTING_CODE);
                break;
            case R.id.address:
                UiUtils.startActivity(MySettingActivity.this, AddressListActivity.class, true);
                break;
            case R.id.bind_phone:
                ait = new Intent(this,AlterInfoActivity.class);
                ait.putExtra(AlterInfoActivity.ALTER_ITEM, getString(R.string.bind_phone));
                ait.putExtra(AlterInfoActivity.SERVICE_URL_KEY, braConfig.URL_SET_INFO);
                ait.putExtra(AlterInfoActivity.PARAM_KEY, "phone");
                if(!TextUtils.isEmpty(account.phone))
                    ait.putExtra(AlterInfoActivity.ORI_INFO, account.phone);
                else
                    ait.putExtra(AlterInfoActivity.ORI_INFO, "请绑定手机，便于我们联系您");
                this.startActivityForResult(ait,MyInfoActivity.MY_SETTING_CODE);


            default:
                super.onClick(v);
        }
    }

    @Override
    protected void onDestroy()
    {
        if(null!=logoutDialog)
            logoutDialog.dismiss();
        logoutDialog = null;

        super.onDestroy();
    }
}
