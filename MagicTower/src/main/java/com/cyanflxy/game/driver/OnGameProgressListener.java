package com.cyanflxy.game.driver;

import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.bean.ShopBean;

public interface OnGameProgressListener {

    void onShowInfo(String info, OnInfoCloseListener listener);

    void onHeroStateChange(HeroBean hero);

    void onMapChane(MapBean map);

    void onShowShop(ShopBean shop);

    /**
     * 显示对话，TODO 对话如何处理？要怎么显示该语句是谁说的？ 如何进行交互？
     */
    void onShowDialogue(String sentence);

    void onGameEnd();

}
