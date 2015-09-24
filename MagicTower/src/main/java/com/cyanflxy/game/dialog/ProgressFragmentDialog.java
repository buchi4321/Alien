package com.cyanflxy.game.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

import com.github.cyanflxy.magictower.R;

public class ProgressFragmentDialog extends DialogFragment {

    public static final String TAG = "ProgressFragmentDialog";

    private Object task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = new Dialog(getActivity(), R.style.common_dialog_style);
        d.setContentView(R.layout.dialog_progress);
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);

        d.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        return d;
    }

    public void setTaskObject(Object task) {
        this.task = task;
    }

    public Object getTaskObject() {
        return task;
    }
}
