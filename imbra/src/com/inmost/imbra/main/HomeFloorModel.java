package com.inmost.imbra.main;

import android.text.TextUtils;

import org.json.JSONObject;

public class HomeFloorModel {

    public static final String TYPE_BLOG = "BLOG";
    public static final String TYPE_COLLECTION = "COLLECTION";
    public static final String TYPE_LOOKBOOK = "LOOKBOOK";
    public String id;
    public String type;
    public String type_id;
	public String title;
    public String oriCoverurl;
    public String coverUrl;
    public String bgurl;

	public String intro;
    public String view_count;
    public String author;

    public String textual_date;

    public void clear()
    {
        id = "";
        type = "";
        type_id = "";
        title = "";
        oriCoverurl = "";
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
        type = jsonObject.optString("type");
        id = jsonObject.optString("id");
        type_id = jsonObject.optString("type_id");
        coverUrl = jsonObject.optString("cover");
        title = jsonObject.optString("title");

        if(TextUtils.isEmpty(type))
            return;
        if(type.equalsIgnoreCase(TYPE_BLOG))
        {
            author = jsonObject.optString("author_name");
            textual_date = jsonObject.optString("textual_date");
        }
        else
            author = jsonObject.optString("author");
        oriCoverurl = jsonObject.optString("cover_original");
        bgurl = jsonObject.optString("background");
        intro = jsonObject.optString("intro");
        view_count = jsonObject.optString("view_count");
    }


    public void parseLook(JSONObject jsonObject) {
        clear();
        type = TYPE_LOOKBOOK;
        id = jsonObject.optString("id");
        type_id = jsonObject.optString("id");
        coverUrl = jsonObject.optString("cover");
        title = jsonObject.optString("title");

    }

    public void parseBlog(JSONObject jsonObject) {
        clear();
        type = jsonObject.optString("type");
        id = jsonObject.optString("id");
        type_id = jsonObject.optString("id");
        coverUrl = jsonObject.optString("cover_url");
        title = jsonObject.optString("title");

        JSONObject au = jsonObject.optJSONObject("author");
        author = au.optString("name");
        textual_date = jsonObject.optString("textual_date");

    }


    public void parseBrandFloor(JSONObject jsonObject) {
        clear();
        type = jsonObject.optString("type");
        id = jsonObject.optString("id");
        type_id = jsonObject.optString("type_id");
        JSONObject job = jsonObject.optJSONObject("object");


        coverUrl = job.optString("cover");
        title = job.optString("title");

        JSONObject au = job.optJSONObject("editor");
        author = au.optString("name");
        textual_date = jsonObject.optString("textual_date");

    }

    public static String formBraUrl(String oldurl)
    {
        if(!TextUtils.isEmpty(oldurl))
            return "http://www.o2bra.com.cn/" + oldurl;
        return "";

    }

}
