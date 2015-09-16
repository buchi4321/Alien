package com.cyanflxy.game.fragment;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    public static <T extends BaseFragment> String getFragmentTag(Class<T> clazz) {
        return clazz.getSimpleName();
    }

    public boolean onBackPress() {
        return false;
    }
}
