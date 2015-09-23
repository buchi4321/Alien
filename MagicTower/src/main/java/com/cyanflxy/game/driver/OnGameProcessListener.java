package com.cyanflxy.game.driver;

public interface OnGameProcessListener {

    void showDialogue();

    void openDoor(int x, int y, String doorName);

    void changeFloor(int floor);
}
