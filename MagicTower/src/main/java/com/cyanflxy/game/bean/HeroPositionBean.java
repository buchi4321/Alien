package com.cyanflxy.game.bean;

public class HeroPositionBean extends BeanParent implements Cloneable {

    public int x;
    public int y;
    public Direction direction;

    public enum Direction {
        left, right, up, down
    }

    public HeroPositionBean copy() {

        HeroPositionBean p = new HeroPositionBean();
        p.x = x;
        p.y = y;
        p.direction = direction;

        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HeroPositionBean that = (HeroPositionBean) o;

        if (x != that.x) {
            return false;
        }

        return y == that.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
