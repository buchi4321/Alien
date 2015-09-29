package com.cyanflxy.game.parser;

import com.cyanflxy.game.driver.GameContext;

/**
 * 解释器
 * 例1, 计算下面赋值式子：(来自第一层，天使对话1)
 * <pre>
 *      gameData.hero.keys.yellow+=1;
 *      gameData.hero.keys.blue+=1;
 *      gameData.hero.keys.red+=1;
 *      currentMap.mapData[82]=currentMap.mapData[83];
 *      currentMap.mapData[83].element="";
 *      currentMap.mapData[83].dialog=null
 * </pre>
 * <p/>
 * 例2，计算下面的判断式子，获取冒号左侧的域的值，符合右侧的第几个值则返回几:(来自第一层，天使对话2判断)
 * <pre>
 *     hero.cross:false,true
 * </pre>
 * <p/>
 * 例3，计算下面判断式子：(模拟多个判断式组合)
 * <pre>
 *     hero.cross&&hero.lookUp:false,true
 * </pre>
 */
public class SentenceParser {

    public static void parseSentence(GameContext gameContext, String sentence) {

        GrammarTreeBuilder grammar = new GrammarTreeBuilder(sentence);
        GrammarElement[] grammarElements = grammar.getGrammarTrees();

        for (GrammarElement e : grammarElements) {
            e.getValue(gameContext);
        }

    }

    public static boolean parseCondition(GameContext gameContext, String sentence) {
        GrammarTreeBuilder grammar = new GrammarTreeBuilder(sentence);
        GrammarElement[] grammarElements = grammar.getGrammarTrees();

        if (grammarElements == null || grammarElements.length > 1) {
            throw new RuntimeException("不支持多条条件式:" + sentence);
        }

        GrammarElement e = grammarElements[0];
        Object value = e.getValue(gameContext);

        if (!(value instanceof Boolean)) {
            throw new RuntimeException("条件式语法错误：" + sentence);
        }

        return (boolean) value;
    }

    public static int parseLifeDrain(int heroHP, String lifeDrainSentence) {
        if (lifeDrainSentence.endsWith("%")) {
            int len = lifeDrainSentence.length();
            int percent = Integer.valueOf(lifeDrainSentence.substring(0, len - 1));
            return heroHP * percent / 100;
        } else {
            return Integer.valueOf(lifeDrainSentence);
        }
    }

}
