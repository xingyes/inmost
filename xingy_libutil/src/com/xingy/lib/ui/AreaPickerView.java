package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xingy.R;
import com.xingy.lib.IArea;
import com.xingy.lib.model.ProvinceModel.CityModel;
import com.xingy.lib.model.ProvinceModel.CityModel.ZoneModel;
import com.xingy.lib.model.ProvinceModel;
import com.xingy.util.Config;

import java.util.ArrayList;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;


public class AreaPickerView extends UiBase implements OnWheelScrollListener, OnClickListener{
	private TextView  mValues;
	private AbstractWheel mPrivonceWheel;
	private AbstractWheel mCityWheel;
	private AbstractWheel mZoneWheel;
	
	
	private ArrayWheelAdapter<String> provAdapter;
	private ArrayWheelAdapter<String> cityAdapter;
	private ArrayWheelAdapter<String> zoneAdapter;
	
	
	//private OnWheelChangedListener mProvinceWheelListener;
	//private OnWheelChangedListener mCityWheelListener;
	//private OnWheelChangedListener mZoneWheelListener;
	
	private ArrayList<ProvinceModel> provinceList;
	private ProvinceModel    province;
	private ArrayList<CityModel> cityList;
	private CityModel        city;
	private ArrayList<ZoneModel> zoneList;
	private ZoneModel        zone;
	
	private Context  mContext;

    private int        btnBgRid;
    private int        btnTxtColor;
    private int        valueTxtColor;
    private TextView   btnOk;
    private TextView   btnCancel;

    /**
	 * @param context
	 * @param attrs
	 */
	public AreaPickerView(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.area_picker);
		mContext = context;
        parseAttrs(attrs);
		provinceList = IArea.getProvinces(Config.DISTRICT_PARAM_CACHEKEY);
    }

    private void parseAttrs(AttributeSet attrs) {
        if( null == attrs || null == mContext )
            return ;

        // Parse attributes.
        if( null != attrs ) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.WheelPickView);
            btnBgRid = UiUtils.getResId(mContext,array,R.styleable.WheelPickView_btnBackground);
            btnTxtColor = UiUtils.getColor(mContext, array, R.styleable.WheelPickView_btnTxtColor, Color.BLACK);
            valueTxtColor = UiUtils.getColor(mContext, array, R.styleable.WheelPickView_resultTxtColor,Color.BLACK);
            array.recycle();
        }
    }
	public interface OnPickerListener
	{
		public void onSubmit();
	}
	
	private OnPickerListener  mListener;
	public void setListener(OnPickerListener aListener)
	{
		mListener = aListener;
	}
	
	
	public ProvinceModel getProvince()
	{
		return province;
	}
	public CityModel getCity()
	{
		return city;
	}
	public ZoneModel getZone()
	{
		return zone;
	}

	@Override
	protected void onInit(Context context) {
		
		mValues = (TextView) this.findViewById(R.id.result_info);
        btnOk = (TextView)findViewById(R.id.ok_btn);
        btnOk.setOnClickListener(this);
		btnCancel = (TextView)findViewById(R.id.cancel_btn);
        btnCancel.setOnClickListener(this);

        mValues.setTextColor(valueTxtColor);
        if(btnBgRid>0)
        {
            btnOk.setBackgroundResource(btnBgRid);
            btnCancel.setBackgroundResource(btnBgRid);
        }

        btnOk.setTextColor(btnTxtColor);
        btnCancel.setTextColor(btnTxtColor);

        mPrivonceWheel = (AbstractWheel) findViewById(R.id.province);
		initProvinceAdapter();
		
		mCityWheel = (AbstractWheel) findViewById(R.id.city);
		refreshCityAdapter();
		
		mZoneWheel = (AbstractWheel) findViewById(R.id.district);
		refreshZoneAdapter();
		
		mPrivonceWheel.addScrollingListener(this);
		mCityWheel.addScrollingListener(this);
		mZoneWheel.addScrollingListener(this);
		
		mValues.setText(province.getProvinceName() + "," + 
				city.getCityName()
				 	+ ((null == zone ) ? "": (","+zone.getZoneName()))
					);
		
	}



	private void refreshZoneAdapter() {
		CityModel tmp = cityList.get(mCityWheel.getCurrentItem());
		if(null != city && tmp.getCityName().equals(city.getCityName()))
			return;
		
		city = tmp;
		zoneList = city.getZoneModels();
		final int nSize = zoneList.size();
		if( 0 >= nSize )
		{
			mZoneWheel.setVisibility(View.INVISIBLE);
			zone = null;
			return ;
		}
		mZoneWheel.setVisibility(View.VISIBLE);
		
		String names[] = new String[nSize];
		
		for( int nIdx = 0; nIdx < nSize; nIdx++ ) {
			ZoneModel pMode = zoneList.get(nIdx);
			names[nIdx] = pMode.getZoneName();
		}
		
		zoneAdapter = new ArrayWheelAdapter<String>(mContext, names);
		zoneAdapter.setItemResource(R.layout.wheel_text_centered);
		zoneAdapter.setItemTextResource(R.id.text);
		
		mZoneWheel.setViewAdapter(zoneAdapter);
		mZoneWheel.setCurrentItem(0);
		zone = zoneList.get(mZoneWheel.getCurrentItem());
	}


	private void refreshCityAdapter() {
        ProvinceModel tmp = provinceList.get(mPrivonceWheel.getCurrentItem());
		if(null != province && tmp.getProvinceName().equals(province.getProvinceName()))
			return;
		province = tmp;
		cityList = province.getCityModels();
		final int nSize = cityList.size();
		if( 0 >= nSize )
			return ;
		
		String names[] = new String[nSize];
		
		for( int nIdx = 0; nIdx < nSize; nIdx++ ) {
			CityModel pMode = cityList.get(nIdx);
			names[nIdx] = pMode.getCityName();
		}
		
		cityAdapter = new ArrayWheelAdapter<String>(mContext, names);
		cityAdapter.setItemResource(R.layout.wheel_text_centered);
		cityAdapter.setItemTextResource(R.id.text);
		
		mCityWheel.setViewAdapter(cityAdapter);
		mCityWheel.setCurrentItem(0);
		
	}


	private void initProvinceAdapter() {
		final int nSize = provinceList.size();
		if( 0 >= nSize )
			return ;
		
		String names[] = new String[nSize];
		
		for( int nIdx = 0; nIdx < nSize; nIdx++ ) {
			ProvinceModel pMode = provinceList.get(nIdx);
			names[nIdx] = pMode.getProvinceName();
		}
		
		provAdapter = new ArrayWheelAdapter<String>(mContext, names);
		provAdapter.setItemResource(R.layout.wheel_text_centered);
		provAdapter.setItemTextResource(R.id.text);
        
		mPrivonceWheel.setViewAdapter(provAdapter);
		mPrivonceWheel.setCurrentItem(0);
	    
	}


	@Override
	public void onScrollingStarted(AbstractWheel wheel) {
	}



	@Override
	public void onScrollingFinished(AbstractWheel wheel) {
		if(wheel == mPrivonceWheel)
		{
			refreshCityAdapter();
			
			refreshZoneAdapter();
			
		}
		else if(wheel == mCityWheel)
		{
			refreshZoneAdapter();
		}
		else if(wheel == mZoneWheel)
		{
			zone = zoneList.get(mZoneWheel.getCurrentItem());
		}
		
		mValues.setText(province.getProvinceName() + "," + 
				city.getCityName()
				 	+ ((null == zone ) ? "": (","+zone.getZoneName()))
					);
		
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ok_btn)
		{
			if(null!=mListener)
				mListener.onSubmit();
		}
		else if(v.getId() == R.id.cancel_btn)
		{
			this.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
	
}
