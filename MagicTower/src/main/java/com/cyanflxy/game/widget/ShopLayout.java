package com.cyanflxy.game.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.parser.SentenceParser;
import com.github.cyanflxy.magictower.R;

public class ShopLayout extends LinearLayout {

    public interface onButtonClickListener {
        void onAttributeChange();
    }

    private TextView title;
    private Button[] buttons;
    private View closeButton;

    private onButtonClickListener listener;

    public ShopLayout(Context context) {
        this(context, null);
    }

    public ShopLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        setBackgroundResource(R.color.comm_translate_bg);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        inflate(context, R.layout.view_shop_layout, this);

        title = (TextView) findViewById(R.id.title);

        buttons = new Button[3];
        buttons[0] = (Button) findViewById(R.id.option_0);
        buttons[1] = (Button) findViewById(R.id.option_1);
        buttons[2] = (Button) findViewById(R.id.option_2);

        closeButton = findViewById(R.id.back_game);

    }

    public void setShopBean(ShopBean shopBean) {
        title.setText(shopBean.title);

        for (int i = 0; i < shopBean.options.length; i++) {
            buttons[i].setText(shopBean.options[i].text);
            buttons[i].setTag(shopBean.options[i]);
            buttons[i].setOnClickListener(onClickListener);
        }

    }

    public void setOnCloseListener(OnClickListener listener) {
        closeButton.setOnClickListener(listener);
    }

    public void setOnButtonClickListener(onButtonClickListener l) {
        listener = l;
    }

    private OnClickListener onClickListener = new OnClickListener() {
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
    };
}
