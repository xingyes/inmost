package com.inmost.imbra.login;

import android.content.ContentValues;

import com.xingy.util.db.Database;
import com.xingy.util.db.DbFactory;

import java.util.Date;
import java.util.HashMap;


public class ILogin {

	private static Account account;


	public static boolean accountChecked = false;

	public static String getLoginUid() {
		Account account = getActiveAccount();
		return null != account ? account.uid : "";
	}
	
	/**
	 * 
	 * @return
	 */
	public static Account getActiveAccount() {

		if (null != account || accountChecked)
			return account;
		accountChecked = true;
		Database db = DbFactory.getInstance();
		HashMap<String, String> info = db.getOneRow("select * from t_login");
		if (null == info) {
			return null;
		}

		account = new Account();
		account.uid = info.get("uid");
		account.skey = (info.get("skey"));
		account.rowCreateTime = (Long.valueOf(info.get("row_create_time")));
		
		return account;
	}
	
	public static void setActiveAccount(Account acc) {
		account = acc;
	}

	/**
	 * 
	 */
	public static void clearAccount() {
		Database db = DbFactory.getInstance();
		db.execute("delete from t_login");
		account = null;
	}
	
	/**
	 * 
	 * @param account
	 */
	public static void saveIdentity(Account account) {
		ContentValues values = new ContentValues();
		values.put("uid", account.uid);
		values.put("skey", account.skey);
		values.put("row_create_time", new Date().getTime());
		Database db = DbFactory.getInstance();
		long ret = db.replace("t_login", values);
	}
	
}
