package com.cyanflxy.mapcreator.bean;

import com.google.gson.Gson;

public class MapBean {

    private static final Gson gson = new Gson();

    public int mapFloor;
    public int mapWidth;
    public int mapHeight;
    public String floorImage;

    public int heroPosition;
    public MapElement[] mapData;

    public static class MapElement {
        public String element;
    }

    public MapBean(int floor) {
        mapFloor = floor;
        mapWidth = 11;
        mapHeight = 11;

        floorImage = "floor";
        mapData = new MapElement[11 * 11];
    }

    public void setHeroPosition(int p) {
        heroPosition = p;
    }

    public void setMapData(ImageInfoBean[] imageInfo){
        for (int i = 0; i < imageInfo.length; i++) {
            setMapElementData(i, imageInfo[i]);
        }
    }

    private void setMapElementData(int index, ImageInfoBean imageInfo) {
        MapElement element = new MapElement();
        if (imageInfo == null || imageInfo.name.equals("floor") ||imageInfo.name.equals("hero")) {
            element.element = "";
        } else {
            element.element = imageInfo.name;
        }
        mapData[index] = element;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static MapBean getInstance(String jsonString) {
        return gson.fromJson(jsonString, MapBean.class);
    }
}
