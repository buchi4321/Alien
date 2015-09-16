package com.cyanflxy.game.driver;

import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.record.GameHistory;

public class GameContext {

    private static GameContext instance;

    public static GameContext getInstance(String recordName) {
        if (instance != null && instance.recordName.equals(recordName)) {
            return instance;
        }

        instance = new GameContext(recordName);
        return instance;
    }

    // 从游戏界面退出的时候调用该方法
    public static void destroyInstance() {
        if (instance != null) {
            instance.autoSave();
            instance = null;
        }
    }

    private String recordName;
    private GameBean gameData;
    private MapBean currentMap;

    private GameContext(String record) {
        recordName = record;
        gameData = GameHistory.getGame(record);

        currentMap = GameHistory.getMap(record, gameData.maps[gameData.hero.floor]);
        // TODO record的记录数据应该复制到自动保存里面去！！
    }

    public void autoSave() {
        GameHistory.saveBean(GameHistory.AUTO_SAVE, gameData);
        GameHistory.saveBean(GameHistory.AUTO_SAVE, currentMap);
    }

    public void save(String record) {
        GameHistory.copyRecord(GameHistory.AUTO_SAVE, record);
        GameHistory.saveBean(record, gameData);
        GameHistory.saveBean(record, currentMap);
    }

    public String getIntroduce() {
        return gameData.introduce;
    }

    public void setIntroduceShown() {
        gameData.introduce = null;
    }

    public boolean isFinish() {
        return gameData.isFinish;
    }

    public String getFinishString() {
        return gameData.finish;
    }

    public void setFinishShown() {
        gameData.finish = null;
    }

    public HeroBean getHero() {
        return gameData.hero;
    }

    public MapBean getCurrentMap() {
        return currentMap;
    }


}
