package com.cyanflxy.mapcreator.bean;

import android.text.TextUtils;

import com.google.gson.Gson;

public class MapBean {

    private static final Gson gson = new Gson();

    public int mapFloor;
    public int mapWidth;
    public int mapHeight;
    public String floorImage;

    public HeroPositionBean startPosition;//从楼下上来的时候的位置
    public HeroPositionBean endPosition;// 从楼上下来的时候的位置

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

    public void setMapData(ImageInfoBean[] imageInfo) {
        for (int i = 0; i < imageInfo.length; i++) {
            setMapElementData(imageInfo, i, imageInfo[i]);
        }
    }

    private void setMapElementData(ImageInfoBean[] allInfo, int index, ImageInfoBean imageInfo) {
        MapElement element = new MapElement();
        if (imageInfo == null || imageInfo.name.equals("floor")) {
            element.element = "";
        } else if (imageInfo.type.equals("hero")) {
            boolean up = isUpStair(allInfo, index);
            boolean down = isDownStair(allInfo, index);

            HeroPositionBean p = new HeroPositionBean();
            p.x = index % mapWidth;
            p.y = index / mapWidth;
            p.direction = imageInfo.name;

            if (up) {
                endPosition = p;
            } else if (down) {
                startPosition = p;
            } else {
                if (endPosition != null) {
                    startPosition = p;
                } else if (startPosition != null) {
                    endPosition = p;
                } else {
                    startPosition = p;
                    endPosition = p;
                }
            }

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
        MapBean map = gson.fromJson(jsonString, MapBean.class);
        map.initHeroPosition();
        return map;
    }

    private void initHeroPosition() {
        if (startPosition != null && !TextUtils.isEmpty(startPosition.direction)) {
            MapElement element = new MapElement();
            element.element = startPosition.direction;
            mapData[startPosition.y * mapWidth + startPosition.x] = element;
        }


        if (endPosition != null && !TextUtils.isEmpty(endPosition.direction)) {
            MapElement element = new MapElement();
            element.element = endPosition.direction;
            mapData[endPosition.y * mapWidth + endPosition.x] = element;
        }
    }

    private boolean isUpStair(ImageInfoBean[] allInfo, int index) {
        String[] e = getAroundElement(allInfo, index);
        for (String e1 : e) {
            if (e1 != null) {
                if (e1.equals("stair_up")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDownStair(ImageInfoBean[] allInfo, int index) {
        String[] e = getAroundElement(allInfo, index);
        for (String e1 : e) {
            if (e1 != null) {
                if (e1.equals("stair_down")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String[] getAroundElement(ImageInfoBean[] allInfo, int index) {
        String[] e = new String[4];

        int x = index % mapWidth;
        int y = index / mapWidth;

        e[0] = getElement(allInfo, x + 1, y);
        e[1] = getElement(allInfo, x - 1, y);
        e[2] = getElement(allInfo, x, y + 1);
        e[3] = getElement(allInfo, x, y - 1);

        return e;
    }

    private String getElement(ImageInfoBean[] allInfo, int x, int y) {
        if (x < 0 || x > mapWidth - 1 || y < 0 || y > mapHeight - 1) {
            return null;
        }
        ImageInfoBean info = allInfo[y * mapWidth + x];
        if (info != null) {
            return info.name;
        } else {
            return null;
        }
    }
}
