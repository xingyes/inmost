package com.xingy.util;

public class CellInfo {

	public int lac;      //locationAreaCode
	public String mcc;   //mobileCountryCode
	public String mnc;   //mobileNetworkCode
	public int cellId;
	public String radioType;
	public double lng;
	public double lat;
	
	public CellInfo()
	{
		lat = -1;
		lng = -1;
		lac = -1;
		cellId = -1;
		radioType = "";
		mnc = "";
		mcc = "";
		
	}
	public String toString()
	{
		return "lac:"+lac + ",mcc:" + mcc + ",mnc:" + mnc + ",cellid:"+ cellId + ",radioType:"+ radioType + ".gpslat:" + lat + ",gpslng:" + lng;
	}
	

}
