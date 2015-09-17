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
            instance.destroy();
            instance = null;
        }
    }

    private GameBean gameData;
    private MapBean currentMap;
    private ImageResourceManager imageResourceManager;

    private GameContext() {
        gameData = GameHistory.getGame(GameHistory.AUTO_SAVE);
        currentMap = GameHistory.getMap(GameHistory.AUTO_SAVE, gameData.maps[gameData.hero.floor]);
        imageResourceManager = new ImageResourceManager(gameData.res);
    }

    public boolean autoSave() {
        return GameHistory.saveBean(GameHistory.AUTO_SAVE, gameData) &&
                GameHistory.saveBean(GameHistory.AUTO_SAVE, currentMap);
    }

    public boolean save(String record) {
        return GameHistory.deleteRecord(record) &&
                GameHistory.copyRecord(GameHistory.AUTO_SAVE, record) &&
                GameHistory.saveBean(record, gameData) &&
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

    public ImageResourceManager getImageResourceManager() {
        return imageResourceManager;
    }

    public void destroy() {
        autoSave();
        imageResourceManager.destroy();
    }

}
