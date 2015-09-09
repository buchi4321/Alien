package com.cyanflxy.common;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.cyanflxy.magictower.R;

public class CommDialog extends Dialog implements View.OnClickListener {

    private TextView contentText;

    private View.OnClickListener onOkClick;
    private View.OnClickListener onCancelClick;

    public CommDialog(Context context) {
        super(context, R.style.common_dialog_style);

        setContentView(R.layout.dialog_comm);
        contentText = (TextView) findViewById(R.id.content);
        findViewById(R.id.ok).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    public void setText(int strId) {
        contentText.setText(strId);
    }

    public void setOnOkClickListener(View.OnClickListener l) {
        onOkClick = l;
    }

    public void setOnCancelClickListener(View.OnClickListener l) {
        onCancelClick = l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (onOkClick != null) {
                    onOkClick.onClick(v);
                }
                break;
            case R.id.cancel:
                if (onCancelClick != null) {
                    onCancelClick.onClick(v);
                } else {
                    dismiss();
                }
                break;
        }
    }
}
