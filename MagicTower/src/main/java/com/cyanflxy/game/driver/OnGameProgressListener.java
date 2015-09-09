package com.cyanflxy.game.driver;

import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.MapBean;

public interface OnGameProgressListener {

    void onShowInfo(String info, OnInfoCloseListener listener);

    void onHeroStateChange(HeroBean hero);

    void onMapChane(MapBean map);

    void onGameEnd();

}
