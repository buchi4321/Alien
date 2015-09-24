package com.cyanflxy.game.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.cyanflxy.magictower.R;

public class RecordItemMenuDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "RecordItemMenuDialog";

    public interface OnMenuClickListener {
        void onDelete();

        void onRename();
    }

    private OnMenuClickListener listener;

    public void setOnMenuClickListener(OnMenuClickListener l) {
        listener = l;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = new Dialog(getActivity(), R.style.common_dialog_style);
        d.setContentView(R.layout.dialog_record_menu);

        d.findViewById(R.id.rename).setOnClickListener(this);
        d.findViewById(R.id.delete).setOnClickListener(this);

        Window window = d.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);

        return d;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rename:
                if (listener != null) {
                    listener.onRename();
                }
                break;
            case R.id.delete:
                if (listener != null) {
                    listener.onDelete();
                }
                break;
        }
        dismiss();
    }

}
