package com.cyanflxy.game.bean;

public class MapElementBean {

    public String element;//元素内容，图片id
    public String action;

    public DialogueBean[] dialog;

    public void clear() {
        element = null;
        action = null;
        dialog = null;
    }
}
