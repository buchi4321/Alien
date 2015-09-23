package com.cyanflxy.game.bean;

public class ImageInfoBean extends BeanParent {

    public String name;
    public int[] ids;
    public ImageType type;
    public ResourcePropertyBean property;

    public int getIdLength() {
        return ids.length;
    }

    public int getId() {
        return ids[0];
    }

    public int getId(int index) {
        return ids[index];
    }

    public enum ImageType {
        hero, enemy, goods, npc, wall, floor, door, stairDown, stairUp
    }
}
