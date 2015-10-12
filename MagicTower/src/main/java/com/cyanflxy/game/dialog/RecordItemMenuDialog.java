package com.cyanflxy.game.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.cyanflxy.magictower.R;

public class RecordItemMenuDialog extends BaseDialogFragment implements View.OnClickListener {

    public interface OnMenuClickListener extends OnDialogFragmentFunctionListener{
        void onDelete();

        void onRename();
    }

    private OnMenuClickListener listener;

    @Override
    public void setOnDialogFragmentFunctionListener(OnDialogFragmentFunctionListener l) {
        listener = (OnMenuClickListener) l;
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
