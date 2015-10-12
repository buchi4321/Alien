package com.cyanflxy.game.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.github.cyanflxy.magictower.R;

public class NewGameDialog extends BaseDialogFragment implements View.OnClickListener {

    public interface OnOkClickListener extends OnDialogFragmentFunctionListener{
        void onClick();
    }

    public static final String ARG_CONTENT_STRING = "content_string";

    private String contentText;
    private OnOkClickListener listener;

    @Override
    public void setOnDialogFragmentFunctionListener(OnDialogFragmentFunctionListener l) {
        listener = (OnOkClickListener) l;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (listener != null) {
                    listener.onClick();
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
