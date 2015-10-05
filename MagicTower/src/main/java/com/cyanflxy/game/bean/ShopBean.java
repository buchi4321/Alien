package com.cyanflxy.game.bean;

import java.io.Serializable;

public class ShopBean extends BeanParent implements Serializable {

    private static final long serialVersionUID = 1L;

    public String title;
    public ShopOption[] options;


    public static class ShopOption{
        public String text;
        public String condition;
        public String action;
    }
}
