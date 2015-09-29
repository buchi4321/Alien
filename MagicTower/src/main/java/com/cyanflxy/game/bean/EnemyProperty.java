package com.cyanflxy.game.bean;

import java.io.Serializable;

public class EnemyProperty implements Serializable, Comparable<EnemyProperty> {
    private static final long serialVersionUID = 1L;

    public String resourceName;

    public String name;
    public int hp;
    public int damage;
    public int defense;
    public String lifeDrain;
    public int money;
    public int exp;

    public EnemyProperty(ImageInfoBean enemy) {
        resourceName = enemy.name;

        name = enemy.property.name;
        hp = enemy.property.hp;
        damage = enemy.property.damage;
        defense = enemy.property.defense;
        lifeDrain = enemy.property.lifeDrain;

        money = enemy.property.money;
        exp = enemy.property.exp;
    }

    @Override
    public int compareTo(EnemyProperty another) {
        return damage + defense - another.damage - another.defense;
    }
}
