package com.cyanflxy.game.bean;

public class ImageInfoBean extends BeanParent {

    public String name;
    public int[] ids;
    public String type;
    public EnemyPropertyBean property;

    public int getIdLength() {
        return ids.length;
    }

    public int getId(){
        return ids[0];
    }

    public int getId(int index) {
        return ids[index];
    }

}
