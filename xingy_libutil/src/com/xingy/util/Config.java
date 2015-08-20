package com.xingy.util;


public class Config {
	
	public static final boolean DEBUG = false;
	public static final boolean isCustomerTestVersion = false;
	public static final String COMPILE_TIME = "04/15/2014";
//	public static final String APP_ID = "wxba4b5bc3d7301a3a";
	
	//hello!  wx975698a2b30dfcc7
//	public static final String APP_ID = "wx975698a2b30dfcc7";
	
//	public static final String APP_ID = "wx6964eb0b10aa369b";
	//FORM_ID = "wx6964eb0b10aa369b";
	//DEBUG_ID = "wxb2acd799238987b6";


    public static final String NET_RROR = "悲剧出错了";

	public static final int NOT_LOGIN = 500;

	public static final int MAX_ASYNC_IMAGE_NUM = 20;

	public static final int MAX_SDRAM_PIC_NUM = 30;

	public static final int INNER_DATABASE_VERSION = 2;

	public static final String INNER_DATABASE_NAME = "clubook.db";

	public static final int SD_DATABASE_VERSION = 8;

	public static final String SD_DATABASE_NAME = "clubook.db";

	public static final String SD_DATABASE_VERSION_NAME = "sdcard_database_version";
 
	public static final int GET_DATA_TIME_OUT = 15 * 1000;

	public static final int POST_DATA_TIME_OUT = 8 * 1000;

	public static final int CONNECT_TIME_OUT = 6 * 1000;

	public static final int CHANNEL_CACHE_TIME = 5 * 60;
	
	public static final String TMPDIRNAME = "clubook";
	
	public static final String BROADCAST_FRIENDREQ = "com.clubook.new_req_friend";
	public static final String BROADCAST_EMPTY_MY = "com.clubook.empty_myinfo";
	
	
	
	public static final String LOG_NAME = "fatal_error.log";

	// Folder name for local image cache
	public static final String PIC_CACHE_DIR    = "pic_cache";
	public static final String EVET_PIC_DIR  = "event_pic";
	
	
	public static final String CHANNEL_PIC_DIR  = "channel_pic";
	
	public static final int PROINFO_WIDTH = 680;
	public static final int PROINFO_HEIGHT = 290;

	public static final long PIC_CACHE_DIR_TIME = 5 * 24 * 3600 * 1000;

	public static final long MIN_SD_SIZE_SPARE = 5;
	
	// Max cache for gallery count.
	public static final int MAX_GALLERY_CACHE = 8;
	
	// Extra data key definition.
	public static final String EXTRA_BARCODE = "barcode";
	public static final String EXTRA_PUSHMSG = "pushmsg";
	public static final String EXTRA_WEIXIN = "weixin_url";
	//public static final String EXTRA_ALI_USERID = "alipay_user_id";
	
	public static final String URL_CHECK_VERSION = "URL_CHECK_VERSION";

    public static final String DISTRICT_PARAM_CACHEKEY = "DISTRICT_PARAM_CACHEKEY";



}
