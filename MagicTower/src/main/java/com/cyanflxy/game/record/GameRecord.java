package com.cyanflxy.game.record;

import android.support.annotation.NonNull;

import com.cyanflxy.game.bean.HeroBean;

import java.io.Serializable;

public class GameRecord implements Comparable<GameRecord>, Serializable {
    private static final long serialVersionUID = 1L;

    public int id;

    public HeroBean hero;

    public String recordName;

    public String displayName;

    public String recordTime;

    @Override
    public int compareTo(@NonNull GameRecord another) {
        return id - another.id;
    }
}
