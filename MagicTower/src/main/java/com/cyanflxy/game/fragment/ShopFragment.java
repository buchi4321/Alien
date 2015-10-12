package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.widget.ShopLayout;

public class ShopFragment extends BaseFragment {

    public static final String ARG_SHOP_BEAN = "shop_bean";

    public interface OnAttributeChangeListener extends OnFragmentFunctionListener {
        void onAttributeChange();
    }

    private ShopBean shopBean;
    private OnAttributeChangeListener listener;

    @Override
    public void setOnFragmentFunctionListener(OnFragmentFunctionListener l) {
        listener = (OnAttributeChangeListener) l;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shopBean = (ShopBean) getArguments().getSerializable(ARG_SHOP_BEAN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ShopLayout shopLayout = new ShopLayout(getActivity());

        shopLayout.setOnCloseListener(onCloseListener);
        shopLayout.setShopBean(shopBean);
        shopLayout.setOnButtonClickListener(new ShopLayout.onButtonClickListener() {
            @Override
            public void onAttributeChange() {
                if (listener != null) {
                    listener.onAttributeChange();
                }
            }
        });

        return shopLayout;
    }

}
