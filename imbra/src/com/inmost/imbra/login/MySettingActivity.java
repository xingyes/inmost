package com.inmost.imbra.login;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

    private NetworkImageView usrImgv;
    private ImageLoader      mImgLoader;

    private TextField        imgSetting;
    private Ajax             mAjax;
    private boolean          bChanged = false;
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

        // 初始化布局元素
        initViews();

        Account act = ILogin.getActiveAccount();

//        UiUtils.hideSoftInput(this, mPhone);

		if(null!=act)
		{
//			mHandler.sendEmptyMessageDelayed(FINISH_LOGIN, 1500);
		}
	}

	private void initViews() {
        loadNavBar(R.id.mysetting_nav);

        this.findViewById(R.id.nickname).setOnClickListener(this);
        imgSetting = (TextField)findViewById(R.id.head);
        imgSetting.setOnClickListener(this);

        this.findViewById(R.id.address).setOnClickListener(this);

        usrImgv = (NetworkImageView)imgSetting.findViewById(R.id.left_net_drawable);

        usrImgv.setImageUrl("http://img2.imgtn.bdimg.com/it/u=921607941,1665261509&fm=21&gp=0.jpg",mImgLoader);
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
            usrImgv.setImageBitmap(bitmap);
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

//                Ajax ajax = ServiceConfig.getAjax(Config.URL_IMAGE_STREAM_UPLOAD,
//                        (mUploadPhotoSrc == UPLOADPHOTO_SNAPSHOT ?  PARAM_UPLOAD_SNAPSHOT : PARAM_UPLOAD_COVER));
//                if( null != ajax ) {
//                    showLoadingLayer();
//                    ajax.setData("act_id",this.mEventId);
//                    ajax.setData("uid",mEvent.sponsorInfo.mUid);
//                    ajax.setFile("actimg", byteArray);
//                    ajax.setOnSuccessListener(this);
//                    ajax.setId(AJAX_UPLOAD_SNAPSHOT);
//                    ajax.setOnErrorListener(this);
//                    addAjax(ajax);
//                    ajax.send();
//                }
            }

//            mIsProcessing = false;
        }
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.head:
                UploadPhotoUtil.createUploadPhotoDlg(MySettingActivity.this).show();
                break;
            case R.id.nickname:
                Intent ait = new Intent(this,AlterInfoActivity.class);
                ait.putExtra(AlterInfoActivity.ALTER_ITEM, getString(R.string.real_name));
                ait.putExtra(AlterInfoActivity.SERVICE_URL_KEY, braConfig.URL_SET_INFO);
                ait.putExtra(AlterInfoActivity.PARAM_KEY, "修改昵称");
                ait.putExtra(AlterInfoActivity.ORI_INFO, "CarrieYu");
                this.startActivityForResult(ait,MyInfoActivity.MY_SETTING_CODE);

                break;
            case R.id.address:
                UiUtils.startActivity(MySettingActivity.this, AddressListActivity.class, true);
            default:
                super.onClick(v);
        }
    }
}
