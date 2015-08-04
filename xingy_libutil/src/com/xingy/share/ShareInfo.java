package com.xingy.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * 微信分享内容封装
 * 
 * @author xingyao
 * 
 */
public class ShareInfo implements Serializable, Cloneable {

	public String title;// 标题
    public String url; // 分享对应的链接
    public String summary = ""; // 分享的内容摘要
    public String wxcontent;// 微信好友分享内容
    public String wxMomentsContent; // 微信朋友圈分享内容
    public String normalText;// 系统通用分享内容
    public String iconUrl; // 分享图片url，和shareLogo取其一
    public byte[] shareLogo; // 分享图片，和iconUrl取其一
    public String eventFrom; // 分享来源
    public String cancelEventId; // 取消的事件ID
    public String eventName; // 业务名称
    public String transaction = ""; // 分享业务ID，用于传递回调依据

	@Override
	public ShareInfo clone() {
		ShareInfo shareInfo = new ShareInfo();
		try {
			shareInfo = (ShareInfo) super.clone();
		} catch (CloneNotSupportedException e) {
		    e.printStackTrace();
		}
		return shareInfo;
	}

	/**
	 * 精简参数，传入必要的值即可
	 *
	 * @param url
	 * @param title
	 * @param summary
	 * @param iconUrl
	 * @param eventName
	 */
	public ShareInfo(String url, String title, String summary, String iconUrl, String eventName) {
		super();

		this.url = url;
		this.title = title;
		this.summary = summary;
		this.iconUrl = iconUrl;
		this.eventName = eventName;
	}

	public ShareInfo(String title, String wxcontent, String wxMomentsContent, String url, //
                     String normalText, String eventFrom, String iconUrl, Bitmap shareLogo) {
		super();
		this.title = title;
		this.summary = wxcontent;
		this.wxcontent = wxcontent;
		this.wxMomentsContent = wxMomentsContent;
		this.url = url;
		this.normalText = normalText;
		this.eventFrom = eventFrom;
		this.iconUrl = iconUrl;
		setShareLogo(shareLogo);
	}

	public ShareInfo(String title, String wxcontent, String wxMomentsContent, String url,
                     String normalText, String eventFrom, String iconUrl, Bitmap shareLogo, String eventName) {
		super();
		this.title = title;
		this.summary = wxcontent;
		this.wxcontent = wxcontent;
		this.wxMomentsContent = wxMomentsContent;
		this.url = url;
		this.normalText = normalText;
		this.eventFrom = eventFrom;
		this.iconUrl = iconUrl;
		setShareLogo(shareLogo);
		this.eventName = eventName;
	}
	
	public ShareInfo() {
		super();
	}


	public Bitmap getShareLogo() {
		Bitmap bitmap = null;
		if (shareLogo != null) {
			bitmap = BitmapFactory.decodeByteArray(shareLogo, 0, shareLogo.length);
		}
		return bitmap;
	}

	public void setShareLogo(Bitmap shareLogo) {
		if (shareLogo != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			shareLogo.compress(Bitmap.CompressFormat.PNG, 0, baos);
			this.shareLogo = baos.toByteArray();
		}
	}

}
