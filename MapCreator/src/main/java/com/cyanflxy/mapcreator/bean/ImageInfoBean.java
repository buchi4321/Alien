package com.cyanflxy.mapcreator.bean;

public class ImageInfoBean {

    public String name;
    public int[] ids;
    public int id;
    public String type;
    public EnemyPropertyBean property;

    public int getFirstId() {
        if (ids == null || ids.length == 0) {
            return id;
        } else {
            return ids[0];
        }
    }

}
