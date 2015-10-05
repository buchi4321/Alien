package com.cyanflxy.game.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {

    public static <T extends BaseFragment> String getFragmentTag(Class<T> clazz) {
        return clazz.getSimpleName();
    }

    protected void closeFragment() {
        ((OnFragmentCloseListener) getActivity()).closeFragment(this);
    }

    public boolean onBackPress() {
        return false;
    }

    protected View.OnClickListener onCloseListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeFragment();
        }
    };
}
