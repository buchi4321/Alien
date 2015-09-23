package com.cyanflxy.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.github.cyanflxy.magictower.R;

public class CommFragmentDialog extends DialogFragment implements View.OnClickListener {

    private static final String ARG_CONTENT_STRING = "content_string";

    private String contentText;
    private View.OnClickListener onOkClick;

    public static CommFragmentDialog newInstance(String contentText) {
        CommFragmentDialog dialog = new CommFragmentDialog();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_CONTENT_STRING, contentText);
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        contentText = bundle.getString(ARG_CONTENT_STRING);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = new Dialog(getActivity(),R.style.common_dialog_style);
        d.setContentView(R.layout.dialog_comm);

        ((TextView) d.findViewById(R.id.content)).setText(contentText);
        d.findViewById(R.id.ok).setOnClickListener(this);
        d.findViewById(R.id.cancel).setOnClickListener(this);

        return d;
    }

    public void setOnOkClickListener(View.OnClickListener l) {
        onOkClick = l;
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
                dismiss();
                break;
        }
    }
}
