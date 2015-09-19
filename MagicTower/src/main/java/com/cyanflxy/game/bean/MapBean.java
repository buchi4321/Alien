package com.cyanflxy.game.bean;

public class MapBean extends BeanParent {

    public int mapFloor;
    public int mapWidth;
    public int mapHeight;
    public HeroPositionBean startPosition;//从楼下上来的时候的位置
    public HeroPositionBean endPosition;// 从楼上下来的时候的位置

    public String floorImage;

    public MapElementBean[] mapData;
}
