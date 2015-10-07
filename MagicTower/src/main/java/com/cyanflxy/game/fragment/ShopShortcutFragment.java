package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.cyanflxy.common.FileUtils;
import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.record.GameReader;
import com.cyanflxy.game.widget.PageIndicatorView;
import com.cyanflxy.game.widget.ShopLayout;
import com.cyanflxy.game.widget.ShopLayout.OnAttributeChangeListener;
import com.github.cyanflxy.magictower.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class ShopShortcutFragment extends BaseFragment {

    public static final String TAG = "ShopShortcutFragment";

    private ShopBean[] shops;
    private OnAttributeChangeListener listener;

    private ViewPager viewPager;
    private PageIndicatorView indicatorView;

    public void setOnAttributeChangeListener(OnAttributeChangeListener l) {
        listener = l;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shops = getShops();
    }

    private ShopBean[] getShops() {

        String assetsName = GameReader.getAssetsFileName("shop_shortcut.file");

        InputStream is = null;
        try {
            is = GameReader.getAssetsFileIS(assetsName);
            String shopContent = FileUtils.getInputStreamString(is);

            Gson gson = new Gson();
            return gson.fromJson(shopContent, ShopBean[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewpager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyViewAdapter());
        viewPager.setCurrentItem(GameSharedPref.getLastShopIndex());

        indicatorView = (PageIndicatorView) view.findViewById(R.id.page_indicator);
        indicatorView.setIndicatorCount(shops.length);
        indicatorView.setFocusIndex(GameSharedPref.getLastShopIndex(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener
            = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            indicatorView.setFocusIndex(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            GameSharedPref.setLastShopIndex(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    // FragmentPagerAdapter 在使用时有bug，商店快捷返回后再次启动，上次显示中的Fragment不显示，原因未知。
    private class MyViewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return shops.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ShopLayout shopLayout = new ShopLayout(getActivity());
            shopLayout.setShopBean(shops[position]);
            shopLayout.setOnCloseListener(onCloseListener);
            shopLayout.setOnAttributeChangeListener(listener);

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            container.addView(shopLayout, params);

            return shopLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object != null && object instanceof View) {
                View view = (View) object;
                container.removeView(view);
            }
        }
    }

}
