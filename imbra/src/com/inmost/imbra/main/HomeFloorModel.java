package com.inmost.imbra.main;

import android.text.TextUtils;

import org.json.JSONObject;

public class HomeFloorModel {

//    public static final String TYPE_BLOG = "BLOG";
//    public static final String TYPE_COLLECTION = "COLLECTION";
//    public static final String TYPE_LOOKBOOK = "LOOKBOOK";
    public static final int TYPE_BLOG = 2;
    public static final int TYPE_COLLECTION = 1;
    public static final int TYPE_LOOKBOOK = 3;

    public String id;
    public int type;
    public String title;
    public String coverUrl;
    public String bgurl;

	public String intro;
    public String view_count;
    public String author;

    public String textual_date;

    public void clear()
    {
        id = "";
        type = 0;
        title = "";
        coverUrl = "";
        bgurl = "";

        intro = "";
        view_count = "";
        author = "";

        //blog
        textual_date = "";
    }

    public void parse(JSONObject jsonObject) {
        clear();
        type = jsonObject.optInt("ty");
        if(type<=0 || type>3)
            return;

        id = jsonObject.optString("id");
        coverUrl = jsonObject.optString("pic");
        if(!coverUrl.startsWith("http://"))
            coverUrl = formBraUrl(coverUrl);
        title = jsonObject.optString("tit");
        author = jsonObject.optString("usr");
        textual_date = jsonObject.optString("adt");
    }


    public void parseCollect(JSONObject jsonObject) {
        clear();
        type = TYPE_COLLECTION;
        id = jsonObject.optString("id");
        coverUrl = jsonObject.optString("pic");
        title = jsonObject.optString("tit");

        if(type<=0 || type>3)
            return;
        author = jsonObject.optString("usr");

        bgurl = jsonObject.optString("bgpic");
        intro = jsonObject.optString("desc");
        view_count = jsonObject.optString("cnt");
    }

    public void parseLook(JSONObject jsonObject) {
        clear();
        type = TYPE_LOOKBOOK;
        id = jsonObject.optString("id");
        coverUrl = jsonObject.optString("cover");
        if(!coverUrl.startsWith("http://"))
            coverUrl = formBraUrl(coverUrl);

        title = jsonObject.optString("title");

    }

    public void parseBlog(JSONObject jsonObject) {
        clear();
        type  = TYPE_BLOG;
        id = jsonObject.optString("id");
        coverUrl = jsonObject.optString("pic");
        if(!coverUrl.startsWith("http://"))
            coverUrl = formBraUrl(coverUrl);

        title = jsonObject.optString("tit");
        author = jsonObject.optString("usr");
        textual_date = jsonObject.optString("adt");

    }


    public void parseBrandFloor(JSONObject jsonObject) {
        clear();
        String strtype = jsonObject.optString("type");
        if(strtype.equalsIgnoreCase("BLOG"))
            type = TYPE_BLOG;
        else if(strtype.equalsIgnoreCase("COLLECTION"))
            type = TYPE_COLLECTION;
        else if(strtype.equalsIgnoreCase("LOOKBOOK"))
            type = TYPE_LOOKBOOK;

        id = jsonObject.optString("id");
        JSONObject job = jsonObject.optJSONObject("object");


        coverUrl = job.optString("cover");
        if(!coverUrl.startsWith("http://"))
            coverUrl = formBraUrl(coverUrl);

        title = job.optString("title");

        JSONObject au = job.optJSONObject("editor");
        author = au.optString("name");
        textual_date = jsonObject.optString("textual_date");

    }

    public static String formBraUrl(String oldurl)
    {
        if(!TextUtils.isEmpty(oldurl) && !oldurl.startsWith("http://"))
            return "http://www.o2bra.com.cn/" + oldurl;
        return oldurl;

    }

}
