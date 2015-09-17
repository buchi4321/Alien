package com.cyanflxy.game.bean;

public class ImageInfoBean extends BeanParent {

    public String name;
    public int[] ids;
    public int id;
    public String type;
    public EnemyPropertyBean property;

    public int getIdLength() {
        if (ids == null || ids.length == 0) {
            return 1;
        } else {
            return ids.length;
        }
    }

    public int getId(int index) {
        if (ids == null || ids.length == 0) {
            if (index == 0) {
                return id;
            } else {
                throw new IndexOutOfBoundsException("id length =1, index=" + index);
            }
        } else {
            return ids[index];
        }
    }

}
