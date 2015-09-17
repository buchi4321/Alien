package com.cyanflxy.game.bean;

import java.util.List;

public class HeroBean extends BeanParent {

    public int floor;//当前地图是第几层
    public HeroPositionBean position; //当前位置

    public int level;//人物等级
    public int hp;//人物血量
    public int damage;//攻击力
    public int defense;//防御力
    public int money;//金币
    public int exp;//经验
    public List<KeyEntity> keys;//钥匙数目

    public boolean lookUp;// 是否可以查看怪物属性
    public boolean fly;//是否可以飞行跳转
    public boolean cross;//是否有十字架

    public String thumb;//人物头像
    public List<MoveImageEntity> moveImage;//人物移动图片

    public static class KeyEntity {
        public String name;
        public int value;
    }

    public static class MoveImageEntity {
        public String name;
        public String value;
    }
}
