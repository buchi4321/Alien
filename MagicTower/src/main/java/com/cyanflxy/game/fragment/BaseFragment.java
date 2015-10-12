package com.cyanflxy.game.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {

    public interface OnFragmentCloseListener {

        void popFragment();

    }

    public static String getFragmentTag(Class<? extends Fragment> clazz) {
        return clazz.getSimpleName();
    }

    public interface OnFragmentFunctionListener {
    }

    public void setOnFragmentFunctionListener(OnFragmentFunctionListener l) {
    }

    public boolean onBackPress() {
        return false;
    }

    protected void closeFragment() {
        ((OnFragmentCloseListener) getActivity()).popFragment();
    }

    protected View.OnClickListener onCloseListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeFragment();
        }
    };

}
