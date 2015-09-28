package com.cyanflxy.game.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.cyanflxy.magictower.R;

public class SettingCheckBox extends LinearLayout {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(SettingCheckBox checkBox, boolean isChecked);
    }

    private CheckBox checkBox;
    private OnCheckedChangeListener listener;

    public SettingCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        inflate(context, R.layout.view_setting_checkbox, this);

        TextView textView = (TextView) findViewById(R.id.checkbox_text);
        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox.toggle();

                if (listener != null) {
                    listener.onCheckedChanged(SettingCheckBox.this, checkBox.isChecked());
                }
            }
        });

        checkBox = (CheckBox) findViewById(R.id.checkbox_icon);
        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCheckedChanged(SettingCheckBox.this, checkBox.isChecked());
                }
            }
        });

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingCheckBox);

        String text = a.getString(R.styleable.SettingCheckBox_text);
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }

        a.recycle();

    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        listener = l;
    }

}
