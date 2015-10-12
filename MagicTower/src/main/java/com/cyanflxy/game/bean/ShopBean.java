package com.cyanflxy.game.bean;

import java.io.Serializable;
import java.util.Arrays;

public class ShopBean extends BeanParent implements Serializable {

    private static final long serialVersionUID = 1L;

    public String title;
    public ShopOption[] options;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShopBean shopBean = (ShopBean) o;

        if (!title.equals(shopBean.title)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(options, shopBean.options)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + Arrays.hashCode(options);
        return result;
    }

    public static class ShopOption implements Serializable {
        private static final long serialVersionUID = 1L;

        public String text;
        public String condition;
        public String action;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ShopOption option = (ShopOption) o;

            if (!text.equals(option.text)) return false;
            if (!condition.equals(option.condition)) return false;
            if (!action.equals(option.action)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = text.hashCode();
            result = 31 * result + condition.hashCode();
            result = 31 * result + action.hashCode();
            return result;
        }
    }
}
