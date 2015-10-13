package com.cyanflxy.game.parser;

import android.text.TextUtils;

import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.record.GameHistory;
import com.cyanflxy.game.record.GameReader;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * 语法树元素。
 * <p/>
 * 关键字：null-空指针。 maps - 地图文件数组
 */
public abstract class GrammarElement {

    protected GrammarElement left;
    protected GrammarElement right;
    protected String content;

    public void setContent(String c) {
        content = c;
    }

    public void setLeft(GrammarElement e) {
        left = e;
    }

    public void setRight(GrammarElement e) {
        right = e;
    }

    public abstract Object getValue(GameContext gameContext);

    public void setValue(GameContext gameContext, char op, Object value){
        throw new RuntimeException("语法不正确");
    }

    public void checkSave() {
        if (left != null) {
            left.checkSave();
        }

        if (right != null) {
            right.checkSave();
        }
    }

    protected void setFieldValue(Object object, Field field, char op, Object value) {
        if (op != 0) {
            try {
                field.setAccessible(true);
                value = newValue(field.get(object), value, op);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("语法不正确", e);
            }
        }

        try {
            Class fieldType = field.getType();

            if (value instanceof Number) {
                if (fieldType.equals(int.class)) {
                    field.set(object, ((Number) value).intValue());
                } else if (fieldType.equals(float.class)) {
                    field.set(object, ((Number) value).floatValue());
                }
            } else {
                field.set(object, value);
            }

        } catch (Exception e) {
            throw new RuntimeException("语法不正确", e);
        }
    }

    protected Number newValue(Object lastValue, Object newValue, char op) {

        if (lastValue instanceof Number && newValue instanceof Number) {
            float l = ((Number) lastValue).floatValue();
            float r = ((Number) newValue).floatValue();
            switch (op) {
                case '+':
                    return l + r;
                case '-':
                    return l - r;
                case '*':
                    return l * r;
                case '/':
                    return l / r;
            }
        }

        throw new RuntimeException("语法不正确");
    }

    /**
     * 操作符
     */
    public static abstract class OperatorElement extends GrammarElement {
        /**
         * 优先级，优先级越高（数字越小）的操作符，在树中的位置越低
         */
        public abstract int getPriority();

    }

    /**
     * 点操作符
     */
    public static class FieldGetterElement extends OperatorElement {

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            Object leftObject = left.getValue(gameContext);
            try {
                Field f = leftObject.getClass().getDeclaredField(right.content);
                f.setAccessible(true);
                return f.get(leftObject);
            } catch (Exception e) {
                throw new RuntimeException("语法不正确", e);
            }
        }

        @Override
        public void setValue(GameContext gameContext, char op, Object value) {
            Object leftObject = left.getValue(gameContext);
            try {
                Field f = leftObject.getClass().getDeclaredField(right.content);
                setFieldValue(leftObject, f, op, value);
            } catch (Exception e) {
                throw new RuntimeException("语法不正确", e);
            }
        }
    }

    /**
     * 数组元素获取操作符
     */
    public static class ArraySelectElement extends OperatorElement {

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            Object leftObject = left.getValue(gameContext);
            Object indexObject = right.getValue(gameContext);

            if (!(indexObject instanceof Number)) {
                throw new RuntimeException("语法不正确");
            }

            int index = ((Number) indexObject).intValue();

            if (leftObject instanceof String) {
                return getMap(gameContext, index);
            } else {
                return Array.get(leftObject, index);
            }
        }

        @Override
        public void setValue(GameContext gameContext, char op, Object value) {
            Object leftObject = left.getValue(gameContext);
            Object indexObject = right.getValue(gameContext);

            if (!(indexObject instanceof Number)) {
                throw new RuntimeException("语法不正确");
            }

            int index = ((Number) indexObject).intValue();

            if (op != 0) {
                value = newValue(Array.get(leftObject, index), value, op);
            }

            Array.set(leftObject, index, value);
        }

        private MapBean mapBean;

        private MapBean getMap(GameContext gameContext, int index) {
            String fileName = gameContext.getGameData().maps[index];
            mapBean = GameReader.getMapData(fileName);
            return mapBean;
        }

        @Override
        public void checkSave() {
            if (mapBean != null) {
                GameHistory.autoSave(mapBean);
            } else {
                super.checkSave();
            }
        }
    }

    /**
     * 赋值操作符
     */
    public static class AssignmentElement extends OperatorElement {

        @Override
        public int getPriority() {
            return 14;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            Object rightValue = right.getValue(gameContext);
            char op = 0;
            if (!TextUtils.isEmpty(content)) {
                op = content.charAt(0);
            }
            left.setValue(gameContext, op, rightValue);
            checkSave();

            return null;
        }
    }

    public static class CompareElement extends OperatorElement {

        @Override
        public int getPriority() {
            return 6;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            Object leftValue = left.getValue(gameContext);
            Object rightValue = right.getValue(gameContext);

            if (!(leftValue instanceof Number) || !(rightValue instanceof Number)) {

                if (leftValue instanceof String && rightValue instanceof String) {
                    // 字符串比较
                    String left = (String) leftValue;
                    String right = (String) rightValue;

                    switch (content) {
                        case "==":
                            return TextUtils.equals(left, right);
                        case "!=":
                            return !TextUtils.equals(left, right);
                        default:
                            throw new RuntimeException("语法不正确");
                    }

                } else {
                    throw new RuntimeException("语法不正确");
                }

            }

            float leftNumber = ((Number) leftValue).floatValue();
            float rightNumber = ((Number) rightValue).floatValue();

            switch (content) {
                case ">":
                    return leftNumber > rightNumber;
                case ">=":
                    return leftNumber >= rightNumber;
                case "<":
                    return leftNumber < rightNumber;
                case "<=":
                    return leftNumber <= rightNumber;
                case "!=":
                    return leftNumber != rightNumber;
                case "==":
                    return leftNumber == rightNumber;
                default:
                    throw new RuntimeException("语法不正确");
            }

        }
    }

    public static class LogicAndElement extends OperatorElement {

        @Override
        public int getPriority() {
            return 11;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            Object leftValue = left.getValue(gameContext);
            Object rightValue = right.getValue(gameContext);

            if (!(leftValue instanceof Boolean) || !(rightValue instanceof Boolean)) {
                throw new RuntimeException("语法不正确");
            }

            return (Boolean) leftValue && (Boolean) rightValue;
        }
    }

    public static class LogicOrElement extends OperatorElement {

        @Override
        public int getPriority() {
            return 12;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            Object leftValue = left.getValue(gameContext);
            Object rightValue = right.getValue(gameContext);

            if (!(leftValue instanceof Boolean) || !(rightValue instanceof Boolean)) {
                throw new RuntimeException("语法不正确");
            }

            return (Boolean) leftValue || (Boolean) rightValue;
        }
    }


    /**
     * 算数操作符
     */
    public static class ArithmeticElement extends OperatorElement {

        @Override
        public int getPriority() {
            if ("+-".contains(content)) {
                return 4;
            } else if ("*/%".equals(content)) {
                return 3;
            }
            return 4;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            Object leftValue = left.getValue(gameContext);
            Object rightValue = right.getValue(gameContext);

            return newValue(leftValue, rightValue, content.charAt(0));
        }
    }

    /**
     * 域
     */
    public static class FieldElement extends GrammarElement {

        @Override
        public Object getValue(GameContext gameContext) {
            if ("null".equals(content)) {
                return null;
            } else if ("maps".equals(content)) {
                return content;
            } else if ("true".equals(content)) {
                return true;
            } else if ("false".equals(content)) {
                return false;
            }

            try {
                Field field = gameContext.getClass().getDeclaredField(content);
                field.setAccessible(true);
                return field.get(gameContext);
            } catch (Exception e) {
                throw new RuntimeException("语法不正确", e);
            }
        }

        @Override
        public void setValue(GameContext gameContext, char op, Object value) {
            try {
                Field field = gameContext.getClass().getDeclaredField(content);
                setFieldValue(gameContext, field, op, value);
            } catch (Exception e) {
                throw new RuntimeException("语法不正确", e);
            }
        }
    }

    /**
     * 数值
     */
    public static class NumberElement extends GrammarElement {
        private float value;

        public void setNumberValue(float o) {
            value = o;
        }

        @Override
        public Object getValue(GameContext gameContext) {
            return value;
        }

    }

    /**
     * 字符串
     */
    public static class StringElement extends GrammarElement{

        @Override
        public Object getValue(GameContext gameContext) {
            return content;
        }
    }

}
