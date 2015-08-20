package com.xingy.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.xingy.util.Config;
import com.xingy.util.Log;
import com.xingy.util.MyApplication;
import com.xingy.util.ToolUtil;

public class DbInner extends Database {

	@Override
	public void init() {
		if (core == null || !core.isOpen()) {
			InnerHelper mHelper = new InnerHelper(MyApplication.app);
			try{
				core = mHelper.getWritableDatabase();
			}catch(SQLiteException ex)
			{
				Log.e("dbinner", "getWritableDatabase" + ToolUtil.getStackTraceString(ex));	
			}
		}
	}

	private class InnerHelper extends SQLiteOpenHelper {
		private static final String LOG_TAG = "InnerHelper";

		public InnerHelper(Context context) {
			super(context, Config.INNER_DATABASE_NAME, null, Config.INNER_DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			try {
				db.execSQL("create table if not exists t_page_cache(id varchar(50) primary key, content TEXT, row_create_time INTEGER, row_expire_time INTEGER)");
			} catch (Exception ex) {
				Log.e(LOG_TAG, "onCreate|page_cache|" + ToolUtil.getStackTraceString(ex));
				;
			}

			try {
				db.execSQL("create table if not exists t_login(uid varchar(16) primary key, token varchar(64)," +
                        "iconurl varchar(256), nickname varchar(128) ,phone varchar(16), row_create_time INTEGER)");
			} catch (Exception ex) {
				Log.e(LOG_TAG, "onCreate|t_login|" + ToolUtil.getStackTraceString(ex));
				;
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {

	/*		try {
				db.execSQL("DROP TABLE IF EXISTS page_cache");
			} catch (Exception ex) {
				Log.e(LOG_TAG, "onUpgrade|page_cache|" + ToolUtil.getStackTraceString(ex));
				;
			}
*/
			
			if( newversion > oldversion ){
				try {
					db.execSQL("DROP TABLE IF EXISTS t_login");
				} catch (Exception ex) {
					Log.e(LOG_TAG, "onUpgrade|t_login|" + ToolUtil.getStackTraceString(ex));
					;
				}
	
				onCreate(db);
			}
		}
	}
}
