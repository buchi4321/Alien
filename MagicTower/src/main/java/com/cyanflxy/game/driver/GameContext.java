package com.cyanflxy.game.driver;

import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.record.GameHistory;

public class GameContext {

    private GameBean gameBean;
    private OnGameProgressListener listener;

    public GameContext(String gameRecord, OnGameProgressListener l) {
        gameBean = GameHistory.getGame(gameRecord);
        listener = l;
    }

    public void start() {
        listener.onShowInfo(getIntroduce(), new OnInfoCloseListener() {
        });
    }

    public void move(Direction d) {

    }

    public String getIntroduce() {
        return "这是一个很古老的故事。在很久很久以前，在遥远的西方大地上，有着这样一个王国，王国虽小但全国的人都生活的非常幸福和快乐。\n突然有一天，从天空飞来一群可怕的怪物，它们来到皇宫，抢走了国王唯一的女儿。第二天，国王便向全国下达了紧急令，只要谁能将公主找回来，他便将王位让给他。于是，全国的勇士们都出发了。他们的足迹走遍了全国的各个角落，可一点线索都没有找到，很快时间就过去了一个月。直到他们在海边的一座小岛上，遇到了一群怪物。\n而我们的故事，就是从这里开始......";
    }

}
