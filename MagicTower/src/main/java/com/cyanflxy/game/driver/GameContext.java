package com.cyanflxy.game.driver;

import android.text.TextUtils;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.bean.DialogueBean;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.HeroPositionBean;
import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.bean.ImageInfoBean.ImageType;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.bean.MapElementBean;
import com.cyanflxy.game.parser.SentenceParser;
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

    private DialogueBean currentDialogue;
    private ImageResourceManager imageResourceManager;

    private OnGameProcessListener gameListener;

    private GameContext() {
        gameData = GameHistory.getGame();
        currentMap = GameHistory.getMap(gameData.maps[gameData.hero.floor]);
        imageResourceManager = new ImageResourceManager(gameData.res);
    }

    public void setGameListener(OnGameProcessListener l) {
        gameListener = l;
    }

    public void destroy() {
        gameListener = null;
        autoSave();
        imageResourceManager.destroy();
    }

    public boolean autoSave() {
        return GameHistory.autoSave(gameData) &&
                GameHistory.autoSave(currentMap);
    }

    public GameBean getGameData() {
        return gameData;
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

    public HeroPositionBean getHeroPosition() {
        return gameData.hero.position;
    }

    public void moveUP() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x, p.y - 1)) {
            p.y--;
            p.direction = HeroPositionBean.Direction.up;
        }
    }

    public void moveDown() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x, p.y + 1)) {
            p.y++;
            p.direction = HeroPositionBean.Direction.down;
        }
    }

    public void moveLeft() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x - 1, p.y)) {
            p.x--;
            p.direction = HeroPositionBean.Direction.left;
        }
    }

    public void moveRight() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x + 1, p.y)) {
            p.x++;
            p.direction = HeroPositionBean.Direction.right;
        }
    }

    private boolean moveTo(int x, int y) {
        MapElementBean element = getMapElement(x, y);
        if (element == null) {
            return false;
        }

        ImageInfoBean info = imageResourceManager.getImage(element.element);
        boolean canMove = canMoveTo(element, info);

        if (info == null) {
            return canMove;
        }

        if (!Utils.isArrayEmpty(element.dialog)) {
            getDialogue(element.dialog);
            if (gameListener != null) {
                gameListener.showDialogue();
            }
        } else {

            // TODO 处理获取物品（Toast展示）
            // TODO 处理敌人遭遇
            // TODO 处理商店

            switch (info.type) {
                case enemy:
                    break;
                case goods:
                    break;
                case door:
                    openDoor(element, info, x, y);
                    break;
                case stairDown:
                    gotoFloor(gameData.hero.floor - 1);
                    break;
                case stairUp:
                    // 18层向上的楼梯需要条件
                    gotoFloor(gameData.hero.floor + 1);
                    break;
                default:
                    break;
            }
        }


        return canMove;
    }

    private void openDoor(MapElementBean element, ImageInfoBean info, int x, int y) {
        String doorName = info.name;
        HeroBean hero = gameData.hero;
        boolean open = false;

        if (TextUtils.equals(doorName, "yellow_door")) {
            if (hero.yellowKey > 0) {
                hero.yellowKey--;
                open = true;
            }
        } else if (TextUtils.equals(doorName, "blue_door")) {
            if (hero.blueKey > 0) {
                hero.blueKey--;
                open = true;
            }
        } else if (TextUtils.equals(doorName, "red_door")) {
            if (hero.redKey > 0) {
                hero.redKey--;
                open = true;
            }
        } else {
            if (!TextUtils.isEmpty(element.action)) {
                open = SentenceParser.parseCondition(this, element.action);
            }
        }

        if (open) {
            element.element = null;
            if (gameListener != null) {
                gameListener.openDoor(x, y, doorName);
            }
        }
    }

    private void gotoFloor(int floor) {
        autoSave();

        String mapFile = gameData.maps[floor];
        currentMap = GameHistory.getMap(mapFile);

        if (gameData.hero.floor < floor) {
            gameData.hero.position = currentMap.startPosition.copy();
        } else {
            gameData.hero.position = currentMap.endPosition.copy();
        }
        gameData.hero.floor = floor;

        if (gameListener != null) {
            gameListener.changeFloor(floor);
        }
    }

    private MapElementBean getMapElement(int x, int y) {
        if (x >= 0 && x < currentMap.mapWidth && y >= 0 && y < currentMap.mapHeight) {
            int index = y * currentMap.mapWidth + x;
            return currentMap.mapData[index];
        }
        return null;
    }

    private boolean canMoveTo(MapElementBean element, ImageInfoBean info) {
        if (element == null) {
            return false;
        }

        if (TextUtils.isEmpty(element.element)) {
            return true;
        }

        if (info.type == ImageType.goods) {
            return true;
        }

        return false;
    }

    /**
     * 获取合适的对话
     */
    private DialogueBean getDialogue(DialogueBean[] dialogues) {
        currentDialogue = null;
        int index = 0;

        for (; index < dialogues.length; index++) {
            DialogueBean d = dialogues[index];

            if (d != null) {
                if (!Utils.isArrayEmpty(d.dialogues)) {
                    currentDialogue = d;
                    break;
                } else if (!TextUtils.isEmpty(d.condition) && !Utils.isArrayEmpty(d.conditionResult)) {
                    // 条件判断
                    if (SentenceParser.parseCondition(this, d.condition)) {
                        currentDialogue = d.conditionResult[0];
                    } else {
                        currentDialogue = d.conditionResult[1];
                    }
                    break;
                }
            }
        }

        if (currentDialogue != null && currentDialogue.end) {
            dialogues[index] = null;
        }

        return currentDialogue;
    }

    public DialogueBean getCurrentDialogue() {
        return currentDialogue;
    }
}
