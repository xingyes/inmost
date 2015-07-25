package com.inmost.imbra.main;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.android.volley.utils.MemDiskImageCache;
import com.inmost.imbra.R;
import com.inmost.imbra.login.Account;
import com.inmost.imbra.login.AlterInfoActivity;
import com.inmost.imbra.login.ILogin;
import com.inmost.imbra.login.MyInfoActivity;
import com.inmost.imbra.shopping.AddressListActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.ui.TextField;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.MyApplication;
import com.xingy.util.ToolUtil;
import com.xingy.util.UploadPhotoUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class AppSettingActivity extends BaseActivity{

	private Intent  mIntent = null;
    private TextField  cacheTextfield;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_appsetting);

        mIntent = this.getIntent();
        if(null == mIntent)
        {
            finish();
            return;
        }


        // 初始化布局元素
        initViews();

	}

	private void initViews() {
        loadNavBar(R.id.appsetting_nav);

        cacheTextfield = (TextField)findViewById(R.id.clear_cache);
        String ab = IMbraApplication.globalMDCache.getImgCacheSize(this);
        cacheTextfield.setContent(ab);
        findViewById(R.id.clear_cache).setOnClickListener(this);
        findViewById(R.id.contact_us).setOnClickListener(this);
        findViewById(R.id.about_us).setOnClickListener(this);

    }





    @Override
    public void onClick(View v)
    {
        Intent ait = null;
        switch (v.getId()) {
            case R.id.clear_cache:
                IMbraApplication.globalMDCache.clearDiskCache();
                UiUtils.makeToast(AppSettingActivity.this,R.string.clear_empty_succ);
                break;
            case R.id.contact_us:
                UiUtils.startActivity(AppSettingActivity.this,ContactUsActivity.class,true);
                break;
            case R.id.about_us:
                UiUtils.startActivity(AppSettingActivity.this, AddressListActivity.class, true);
                break;
            default:
                super.onClick(v);
        }
    }
}
