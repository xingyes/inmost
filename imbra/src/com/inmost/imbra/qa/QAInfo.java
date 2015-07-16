package com.inmost.imbra.qa;

import org.json.JSONObject;

import java.io.Serializable;

public class QAInfo implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -5558200647825477972L;
	public String qId;
	public String coverImg;
	public String title;
	public long   readnum;
	public long   replynum;
	
	public long   ctime;
	public String jsonStr;
	
	public QAInfo()
	{
		clear();
	}
	
	public void clear()
	{
		qId = "";
		coverImg = "";
		title = "";
		readnum = 0;
		replynum = 0;
		ctime = 0;
	}
	
	public String toString()
	{
		return jsonStr;
	}

	
	public void parse(JSONObject data) {
		if(null == data)
			return;
		this.qId = data.optString("qid");
		this.coverImg = data.optString("coverImg");
		this.title = data.optString("title");
		this.readnum = data.optLong("readnum");
		this.replynum = data.optLong("replynum");
		this.ctime = data.optLong("ctime");
		
		this.jsonStr = data.toString();
	}
}
