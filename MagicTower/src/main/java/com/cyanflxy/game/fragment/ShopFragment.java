package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.widget.ShopLayout;
import com.cyanflxy.game.widget.ShopLayout.OnAttributeChangeListener;

public class ShopFragment extends BaseFragment {

    public static final String TAG = "ShopFragment";

    public static ShopFragment newInstance(ShopBean shop) {
        ShopFragment fragment = new ShopFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SHOP_BEAN, shop);

        fragment.setArguments(bundle);

        return fragment;
    }

    private static final String ARG_SHOP_BEAN = "shop_bean";

    private ShopBean shopBean;
    private OnAttributeChangeListener listener;

    public void setOnAttributeChangeListener(OnAttributeChangeListener l) {
        listener = l;
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
        shopLayout.setOnAttributeChangeListener(listener);

        return shopLayout;
    }

}
