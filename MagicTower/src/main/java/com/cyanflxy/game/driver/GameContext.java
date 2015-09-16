package com.cyanflxy.game.driver;

import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.record.GameHistory;

public class GameContext {

    private static GameContext instance;

    public static GameContext getInstance() {
        if (instance == null) {
            instance = new GameContext();
        }

        return instance;
    }

    // 从游戏界面退出的时候调用该方法
    public static void destroyInstance() {
        if (instance != null) {
            instance.autoSave();
            instance = null;
        }
    }

    private GameBean gameData;
    private MapBean currentMap;

    private GameContext() {
        gameData = GameHistory.getGame(GameHistory.AUTO_SAVE);
        currentMap = GameHistory.getMap(GameHistory.AUTO_SAVE, gameData.maps[gameData.hero.floor]);
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

    public void setFinish(){
        gameData.isFinish = true;
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
