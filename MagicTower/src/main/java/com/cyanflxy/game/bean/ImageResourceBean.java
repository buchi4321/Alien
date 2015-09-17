package com.cyanflxy.game.bean;

import com.google.gson.Gson;

import java.util.List;

public class ImageResourceBean extends BeanParent {

    public String source;
    public int pieceWidth;
    public int pieceHeight;

    public List<ImageInfoBean> image;

    public static ImageResourceBean getInstance(String str) {
        Gson gson = new Gson();
        return gson.fromJson(str, ImageResourceBean.class);
    }
}
