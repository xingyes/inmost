package com.xingy.lib;

import android.content.ContentValues;

import com.xingy.util.Base64;
import com.xingy.util.Log;
import com.xingy.util.ToolUtil;
import com.xingy.util.db.Database;
import com.xingy.util.db.DbFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**t_cache_page表�?????
 * t_page_cache(
 * 				id              varchar(50) primary key comment '主�??', 
 * 				content         TEXT comment '�???��?????容�??主�???????��?? ??��??', 
 * 				row_create_time INTEGER comment '记�????????建�?��??', 
 * 				row_expire_time INTEGER  comment '�?????????��??'
 * 			   )
 * 该类主�??�????�?�?t_page_cache表�?��????�表�????�?记�???????��??�?以�??�?content???记�?????�?????????????�??????????�?�????
 */
public class IPageCache {

	private static final String LOG_TAG =  IPageCache.class.getName();

	private static final String TABLE = "t_page_cache";

	private Database db;

	public IPageCache(DbFactory.DbType target) {
		db = DbFactory.getInstance(target);
	}

	public IPageCache() {
		db = DbFactory.getInstance(DbFactory.DbType.INNER);
	}
	
	
	public boolean setObject(String key, Object obj, long cacheTime) {

		String str = null;
		ByteArrayOutputStream baops = null;
		ObjectOutputStream oos = null;

		try {
			baops = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baops);
			oos.writeObject(obj);
			str = Base64.encodeToString(baops.toByteArray(), false);
		} catch (IOException e) {
			Log.e(LOG_TAG, "set|" + ToolUtil.getStackTraceString(e));
			return false;
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (baops != null) {
					baops.close();
				}
			} catch (IOException ex) {
				Log.e(LOG_TAG, "setObject|" + ToolUtil.getStackTraceString(ex));
				return false;
			}
		}

		return set(key, str, cacheTime);
	}

	/**
	 * �?�???��??�?�?串�?�象???对�?????主�??�???��?��??�?�?�?�????�?key已�??�???��?��??就�?��?��?��?????记�??�?�????key�?�???��?��??就�??该�?��???????��?��?��??�?�????
	 * @param key�?�??????��??�?串�??主�??
	 * @param content�?�??????��??�?�?�?
	 * @param cacheTime
	 * @return �?????????��????��???????��?��???????��??就�?????true�???????�????false
	 */
	public boolean set(String key, String content, long cacheTime) {

		if (null == db)
			return false;

		String id = db.getValue("select id from " + TABLE + " where id = ?", key);
		if (db.errCode != 0) {
			return false;
		}

		int affectRows = 0;

		long now = ToolUtil.getCurrentTime();
		long end = cacheTime == 0 ? 0 : (now + cacheTime * 1000);

		ContentValues param = new ContentValues();

		param.put("content", content);
		param.put("row_create_time", now);
		param.put("row_expire_time", end);

		if (null != id) {
			affectRows = db.update(TABLE, param, "id=?", key);
		} else {
			param.put("id", key);
			affectRows = (int) db.insert(TABLE, param);
		}

		return affectRows > 0;
	}

	/**
	 * 主�???????��??主�????��????��??�?�?�????content�???��?????�??????????对�?????对象???
	 * @param key�???��??�?�?记�?????主�??
	 * @param whichClass :???�???????对象???类�??
	 * @return ??��??�?�?串�??�????????????????对象
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getObject(String key, Class<T> whichClass) {
		String body = get(key);

		if ( null == body) {
			return null;
		}

		ObjectInputStream ois = null;
		T obj = null;
		try {
		
			ois = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(body)));
			obj = (T) ois.readObject();
			
		} catch (Exception e) {
			Log.e(LOG_TAG, ToolUtil.getStackTraceString(e));
			return null;
		} finally {
			try {
				if (ois != null){
					ois.close();
				}
			} catch (IOException e) {
				Log.e(LOG_TAG, ToolUtil.getStackTraceString(e));
			}
		}
		return obj;
	}

	/**
	 * ??��??记�?????主�??�???��??�?�?�???��?��?????content???容�????��????��????????�????�?�????已�??�????就�????��??,该�?��?��?��??�?该�?��??�?getContent()??�好
	 * @param key�?记�?????主�??
	 * @return �?�?串类??????content???�?
	 */ 
	public String get(String key) {
		HashMap<String, String> result = db.getOneRow("select content, row_expire_time from " + TABLE + " where id = ?", key);

		if (0 != db.errCode) {
			return null;
		}

		if (null == result) {
			return null;
		}

		long now = ToolUtil.getCurrentTime();
		long expire = Long.valueOf(result.get("row_expire_time"));
		if (expire != 0 && expire < now) {
			db.execute("delete from " + TABLE + " where id = ?", key);
			return null;
		}

		return result.get("content");

	}
	
	/**
	 * ??��??�???��??主�??�???��??row_create_time??????信�??
	 * @param key�?�???��??主�??
	 * @return	row_create_time??????信�??
	 */
	public long getRowCreateTime(String key) {
		HashMap<String, String> pResult = db.getOneRow("select row_create_time from " + TABLE + " where id = ?", key);
		long pNow = ToolUtil.getCurrentTime();
		
		if (0 != db.errCode || null == pResult) {
			return pNow;
		}
		
		long pCreateTime = Long.valueOf(pResult.get("row_create_time"));
		
		return pCreateTime;
	}
	
	/**
	 * ??��??主�????��?????content???容�??�???��?????容�?????�????�?注�??�?�???��?��??该类???get()??��?��????��?????
	 * @param key 记�?????主�??
	 * @return 记�?????content???�?
	 */
	public String getNoDelete(String key) {
		HashMap<String, String> result = db.getOneRow("select content from " + TABLE + " where id = ?", key);

		if (0 != db.errCode) {
			return null;
		}

		if (null == result) {
			return null;
		}

		return result.get("content");
	}
	
	/*
	 * judge whether content of the key is expired or not
	 * @param key
	 */
	public boolean isExpire(String key) {
		HashMap<String, String> result = db.getOneRow("select row_expire_time from " + TABLE + " where id = ?", key);

		if (0 != db.errCode) {
			return true;
		}

		if (null == result) {
			return true;
		}
		
		long now = ToolUtil.getCurrentTime();
		long expire = Long.valueOf(result.get("row_expire_time"));
		if (expire != 0 && expire < now) {
			return true;
		}
		
		return false;
	}

	public void remove(String key) {
		db.execute("delete from " + TABLE + " where id = ?", key);
	}
	
	public void removeLeftLike(String key) {
		db.execute("delete from " + TABLE + " where id like ?", key + "%");
	}
}
