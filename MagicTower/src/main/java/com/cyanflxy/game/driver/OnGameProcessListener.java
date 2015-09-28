package com.cyanflxy.game.driver;

import com.cyanflxy.game.bean.ImageInfoBean;

public interface OnGameProcessListener {

    void showDialogue();

    void openDoor(int x, int y, String doorName);

    void changeFloor(int floor);

    void showBattle(ImageInfoBean enemy);
}
