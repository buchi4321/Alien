package com.cyanflxy.game.bean;

public class HeroBean extends BeanParent {

    public int floor;//当前地图是第几层
    public int maxFloor;//到达的最高层
    public HeroPositionBean position; //当前位置

    public int level;//人物等级
    public int hp;//人物血量
    public int damage;//攻击力
    public int defense;//防御力
    public int money;//金币
    public int exp;//经验

    //钥匙数目
    public int yellowKey;
    public int blueKey;
    public int redKey;
    public String[] keyImage;

    public boolean lookUp;// 是否可以查看怪物属性
    public boolean fly;//是否可以飞行跳转
    public boolean cross;//是否有十字架
    public boolean hoe; //是否有榔头

    public String avatar;//人物头像

    public static class MoveImageEntity {
        public String name;
        public String value;
    }

    public int[] getHeroAttribute() {
        return new int[]{level, hp, damage, defense, money, exp};
    }

    public int[] getKeysValue() {
        return new int[]{yellowKey, blueKey, redKey};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeroBean heroBean = (HeroBean) o;

        if (floor != heroBean.floor) return false;
        if (level != heroBean.level) return false;
        if (hp != heroBean.hp) return false;
        if (damage != heroBean.damage) return false;
        if (defense != heroBean.defense) return false;
        if (money != heroBean.money) return false;
        if (exp != heroBean.exp) return false;
        if (yellowKey != heroBean.yellowKey) return false;
        if (blueKey != heroBean.blueKey) return false;
        if (redKey != heroBean.redKey) return false;
        if (lookUp != heroBean.lookUp) return false;
        return fly == heroBean.fly;

    }

    @Override
    public int hashCode() {
        int result = floor;
        result = 31 * result + level;
        result = 31 * result + hp;
        result = 31 * result + damage;
        result = 31 * result + defense;
        result = 31 * result + money;
        result = 31 * result + exp;
        result = 31 * result + yellowKey;
        result = 31 * result + blueKey;
        result = 31 * result + redKey;
        result = 31 * result + (lookUp ? 1 : 0);
        result = 31 * result + (fly ? 1 : 0);
        return result;
    }
}
