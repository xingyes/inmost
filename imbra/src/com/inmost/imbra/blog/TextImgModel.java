package com.inmost.imbra.blog;

/**
 * Created by xingyao on 15-6-13.
 */
public class TextImgModel {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMG = 1;

    public String content;
    public int img_width;
    public int img_height;
    public int type;

    public void clear()
    {
        type = TYPE_TEXT;
        img_height = 0;
        img_width = 0;
        content = "";
    }
}
