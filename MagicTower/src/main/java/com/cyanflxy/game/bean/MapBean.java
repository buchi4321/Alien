package com.cyanflxy.game.bean;

public class MapBean extends BeanParent {

    public int mapFloor;
    public int mapWidth;
    public int mapHeight;
    public HeroPositionBean upPosition;
    public HeroPositionBean downPosition;

    public String floorImage;

    public MapElementBean[] mapData;
}
