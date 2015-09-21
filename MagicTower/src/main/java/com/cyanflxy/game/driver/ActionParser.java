package com.cyanflxy.game.driver;

import com.cyanflxy.game.bean.GameBean;

import java.lang.reflect.Field;

public class ActionParser {

    /**
     * 解析条件式
     * 例：hero.cross:false,true
     *
     * @return 条件式结果处于第几个条件
     */
    public static int parseCondition(GameBean game, String condition) {
        String[] condSplit = condition.split(":");
        String conditionField = condSplit[0];
        String values = condSplit[1];

        Object o = getValue(game, conditionField);
        if (o == null) {
            return -1;
        }

        String[] valuesSplit = values.split(",");

        for (int i = 0; i < valuesSplit.length; i++) {
            String v = valuesSplit[i];
            if (v.equals(o.toString())) {
                return i;
            }
        }
        return -1;
    }

    /**解析动作式
     * 例：hero.keys.yellow:+1;hero.keys.blue:+1;hero.keys.red:+1;
     */
    public static void parseAction(){

    }

    private static Object getValue(Object object, String field) {

        int firstDot = field.indexOf('.');
        if (firstDot > 0) {

            String field0 = field.substring(0, firstDot);
            String subField = field.substring(firstDot + 1);

            Object o = getObject(object, field0);

            if (o != null) {
                return getValue(o, subField);
            } else {
                return null;
            }

        } else {
            return getObject(object, field);
        }

    }

    private static Object getObject(Object object, String field) {
        try {
            Field f = object.getClass().getField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
