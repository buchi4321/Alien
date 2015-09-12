package com.cyanflxy.mapcreator.bean;

import com.google.gson.Gson;

import java.util.List;

public class ResourceBean {

    public String source;
    public int pieceWidth;
    public int pieceHeight;

    public List<ImageInfoBean> image;

    public static ResourceBean getInstance(String str) {
        Gson gson = new Gson();
        return gson.fromJson(str, ResourceBean.class);
    }
}
