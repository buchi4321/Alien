package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.parser.SentenceParser;
import com.github.cyanflxy.magictower.R;

public class ShopFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "ShopFragment";

    public static ShopFragment newInstance(ShopBean shop) {
        ShopFragment fragment = new ShopFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SHOP_BEAN, shop);

        fragment.setArguments(bundle);

        return fragment;
    }

    public interface OnAttributeChangeListener {
        void onAttributeChange();
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
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(shopBean.title);

        view.findViewById(R.id.back_game).setOnClickListener(onCloseListener);

        Button button0 = (Button) view.findViewById(R.id.option_0);
        button0.setText(shopBean.options[0].text);
        button0.setTag(shopBean.options[0]);
        button0.setOnClickListener(this);

        Button button1 = (Button) view.findViewById(R.id.option_1);
        button1.setText(shopBean.options[1].text);
        button1.setTag(shopBean.options[1]);
        button1.setOnClickListener(this);

        Button button2 = (Button) view.findViewById(R.id.option_2);
        button2.setText(shopBean.options[2].text);
        button2.setTag(shopBean.options[2]);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        GameContext gameContext = GameContext.getInstance();

        if (object instanceof ShopBean.ShopOption) {
            ShopBean.ShopOption option = (ShopBean.ShopOption) object;
            if (SentenceParser.parseCondition(gameContext, option.condition)) {
                SentenceParser.parseSentence(gameContext, option.action);
                if (listener != null) {
                    listener.onAttributeChange();
                }
            }
        }
    }
}
