package com.cyanflxy.game.driver;

import android.text.TextUtils;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.bean.DialogueBean;
import com.cyanflxy.game.bean.Direction;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.HeroPositionBean;
import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.bean.ImageInfoBean.ImageType;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.bean.MapElementBean;
import com.cyanflxy.game.bean.ResourcePropertyBean;
import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.parser.SentenceParser;
import com.cyanflxy.game.record.GameHistory;
import com.cyanflxy.game.record.GameReader;
import com.cyanflxy.game.sound.SoundUtil;
import com.cyanflxy.game.widget.FightResultToast;
import com.cyanflxy.game.widget.MessageToast;
import com.github.cyanflxy.magictower.R;

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
    private MapElementBean currentBattleElement;
    private ImageInfoBean currentBattleEnemyInfo;
    private ImageResourceManager imageResourceManager;

    private OnGameProcessListener gameListener;

    private GameContext() {
        gameData = GameReader.getGameMainData();
        currentMap = GameReader.getMapData(gameData.maps[gameData.hero.floor]);
        imageResourceManager = new ImageResourceManager(gameData.res);
    }

    public void setTestData() {
        gameData.hero.money = 1000;
        gameData.hero.exp = 1000;
        gameData.hero.yellowKey = 10;
        gameData.hero.blueKey = 10;
        gameData.hero.redKey = 10;
        gameData.hero.damage = 5500;
        gameData.hero.defense = 5000;
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
        if (gameData.isFinish) {
            return true;
        }
        return GameHistory.autoSave(gameData) &&
                GameHistory.autoSave(currentMap);
    }

    public boolean save(String record) {
        String name = GameHistory.getRecordName(record);

        boolean success = GameHistory.deleteRecord(record)
                && GameHistory.copyRecord(GameHistory.AUTO_SAVE, record)
                && GameHistory.save(record, gameData)
                && GameHistory.save(record, currentMap);

        if (success) {
            GameHistory.rename(record, name);
        }

        return success;
    }

    public void readRecord(String record) {
        if (!TextUtils.equals(record, GameHistory.AUTO_SAVE)) {
            GameHistory.deleteAutoSave();
            GameHistory.copyRecord(record, GameHistory.AUTO_SAVE);
        }

        gameData = GameReader.getGameMainData();
        currentMap = GameReader.getMapData(gameData.maps[gameData.hero.floor]);
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

    public void move(Direction d) {
        HeroPositionBean p = getHeroPosition();

        int x = p.x;
        int y = p.y;

        switch (d) {
            case left:
                x--;
                break;
            case right:
                x++;
                break;
            case up:
                y--;
                break;
            case down:
                y++;
                break;
        }

        if (moveTo(x, y)) {
            p.x = x;
            p.y = y;
        }

        p.direction = d;
    }


    public void moveUP() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x, p.y - 1)) {
            p.y--;
        }
        p.direction = Direction.up;
    }

    public void moveDown() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x, p.y + 1)) {
            p.y++;
        }
        p.direction = Direction.down;
    }

    public void moveLeft() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x - 1, p.y)) {
            p.x--;
        }
        p.direction = Direction.left;
    }

    public void moveRight() {
        HeroPositionBean p = getHeroPosition();
        if (moveTo(p.x + 1, p.y)) {
            p.x++;
        }
        p.direction = Direction.right;
    }

    private boolean moveTo(int x, int y) {
        MapElementBean element = getMapElement(x, y);
        if (element == null) {
            return false;
        }

        if (GameSharedPref.isMapInvisible()) {
            return true;
        }

        ImageInfoBean info = imageResourceManager.getImage(element.element);
        boolean canMove = canMoveTo(element, info);

        if (!Utils.isArrayEmpty(element.dialog)) {
            getDialogue(element.dialog);
            gameListener.showDialogue();
        } else if (element.shop != null) {
            GameHistory.saveShop(element.shop);
            gameListener.showShop(element.shop);
        } else if (info != null) {
            switch (info.type) {
                case enemy:
                    battleEnemy(element, info);
                    break;
                case goods:
                    getGoods(element, info);
                    break;
                case door:
                    openDoor(element, info, x, y);
                    break;
                case stairDown:
                    gotoFloor(gameData.hero.floor - 1);
                    break;
                case stairUp:
                    gotoFloor(gameData.hero.floor + 1);
                    break;
                default:
                    break;
            }
        }

        return canMove;
    }

    public void onDialogueEnd() {
        if (currentBattleElement != null && currentBattleEnemyInfo != null) {
            battleEnemy(currentBattleElement, currentBattleEnemyInfo);
        }
    }

    private void battleEnemy(MapElementBean element, ImageInfoBean info) {
        if (gameData.hero.damage <= info.property.defense
                || gameData.hero.hp <= calculateHPDamage(info.property)) {
            SoundUtil.fail();
            MessageToast.showText(R.string.fight_fail);
            return;
        }

        currentBattleElement = element;
        currentBattleEnemyInfo = info;

        if (element.dialogBefore != null) {
            currentDialogue = element.dialogBefore;
            element.dialogBefore = null;

            gameListener.showDialogue();
            return;
        }

        currentDialogue = element.dialogAfter;
        element.dialogAfter = null;

        if (GameSharedPref.isShowFightView()) {
            gameListener.showBattle(info);
        } else {
            gameData.hero.hp -= calculateHPDamage(info.property);
            onBattleEnd();
        }
    }

    public int calculateHPDamage(ResourcePropertyBean enemy) {
        return calculateHPDamage(enemy.hp, enemy.damage, enemy.defense, enemy.lifeDrain);
    }

    public int calculateHPDamage(int hp, int damage, int defense, String lifeDrain) {
        HeroBean hero = gameData.hero;

        int total = 0;
        if (!TextUtils.isEmpty(lifeDrain)) {
            total += SentenceParser.parseLifeDrain(hero.hp, lifeDrain);
        }

        int damageToHero = damage - hero.defense;
        if (damageToHero <= 0) {
            return total;
        }

        int damageToEnemy = hero.damage - defense;
        int round = (hp + damageToEnemy - 1) / damageToEnemy;

        total += (round - 1) * damageToHero;

        return total;
    }

    public void onBattleEnd() {
        currentBattleElement.element = "";

        int money = currentBattleEnemyInfo.property.money;
        int exp = currentBattleEnemyInfo.property.exp;

        gameData.hero.money += money;
        gameData.hero.exp += exp;

        FightResultToast.show(money, exp);

        currentBattleElement = null;
        currentBattleEnemyInfo = null;

        if (currentDialogue != null && GameSharedPref.isShowFightView()) {
            gameListener.showDialogue();
        }
    }

    private void getGoods(MapElementBean element, ImageInfoBean info) {
        ResourcePropertyBean property = info.property;
        if (property == null) {
            return;
        }

        if (!TextUtils.isEmpty(property.action)) {
            SentenceParser.parseSentence(this, property.action);
        }

        if (!TextUtils.isEmpty(property.dialogue)) {
            currentDialogue = new DialogueBean(element.element, property.dialogue);
            gameListener.showDialogue();
        } else if (!TextUtils.isEmpty(property.message)) {
            MessageToast.showText(property.message);
        }

        element.clear();

        SoundUtil.getGood();
    }

    private void openDoor(MapElementBean element, ImageInfoBean info, int x, int y) {
        String doorName = info.name;
        HeroBean hero = gameData.hero;
        boolean open = false;
        boolean keyZero = false;

        if (TextUtils.equals(doorName, "yellow_door")) {
            if (hero.yellowKey > 0) {
                hero.yellowKey--;
                open = true;
            } else {
                keyZero = true;
            }
        } else if (TextUtils.equals(doorName, "blue_door")) {
            if (hero.blueKey > 0) {
                hero.blueKey--;
                open = true;
            } else {
                keyZero = true;
            }
        } else if (TextUtils.equals(doorName, "red_door")) {
            if (hero.redKey > 0) {
                hero.redKey--;
                open = true;
            } else {
                keyZero = true;
            }
        } else {
            if (!TextUtils.isEmpty(element.action)) {
                open = SentenceParser.parseCondition(this, element.action);
            }
        }

        if (open) {
            SoundUtil.openDoor();
            element.clear();
            gameListener.openDoor(x, y, doorName);
        } else if (keyZero) {
            SoundUtil.fail();
            MessageToast.showText(R.string.no_key);
        }
    }

    public boolean jumpFloor(int floor) {
        if (GameSharedPref.isMapInvisible() || gameData.mapOpen[floor]) {
            gotoFloor(floor);
            return true;
        } else {
            MessageToast.showText(R.string.map_not_reach);
            return false;
        }
    }

    private void gotoFloor(int floor) {
        autoSave();

        String mapFile = gameData.maps[floor];
        currentMap = GameReader.getMapData(mapFile);

        if (gameData.hero.floor < floor) {
            gameData.hero.position = currentMap.upPosition.copy();
        } else {
            gameData.hero.position = currentMap.downPosition.copy();
        }

        gameData.hero.floor = floor;
        if (floor > gameData.hero.maxFloor) {
            gameData.hero.maxFloor = floor;
        }

        gameListener.changeFloor(floor);

        gameData.mapOpen[gameData.hero.floor] = true;
        autoSave();
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

    public String getCurrentMusic() {
        return GameReader.getAssetsFileName(currentMap.bgMusic);
    }
}
