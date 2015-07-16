package com.inmost.imbra.collect;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-6-12.
 */
public class CollectTitlePgModel {
    /**
     * collect part
     */
    public String id;
    public String title;
    public int    view_count;
    public Editor  editor;
    public String intro;
    public String bgUrl;

    public class Editor
    {
        public String id;
        public String name;
        public String avatar;
        public void clear()
        {
            id = "";
            name = "";
            avatar = "";
        }
    }

    public void clear()
    {
        id = "";
        title="";
        view_count = 0;
        bgUrl = "";
        if(null==editor) {
            editor = new Editor();
            editor.clear();
        }
        intro = "";
    }


    public void parse(JSONObject json)
    {
        clear();

        id = json.optString("id");
        title = json.optString("title");
        view_count = json.optInt("view_count");
        intro = json.optString("intro");
        bgUrl = json.optString("background");

        JSONObject job = json.optJSONObject("editor");
        if(null!=job)
        {
            editor.avatar = job.optString("avatar");
            editor.id = job.optString("id");
            editor.name = job.optString("name");

        }

    }
}
