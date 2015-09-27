package com.cyanflxy.game.bean;

import java.io.Serializable;

public class EnemyProperty implements Serializable {
    private static final long serialVersionUID = 1L;

    public String resourceName;
    public int hp;
    public int damage;
    public int defense;
    public String lifeDrain;
}
