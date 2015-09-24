package com.cyanflxy.game.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.cyanflxy.magictower.R;

public class CommInputDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "CommInputDialog";

    public static final String ARG_DIALOG_TITLE = "title";
    public static final String ARG_DEFAULT_CONTENT = "default_content";

    public static CommInputDialog newInstance(String title, String content) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DIALOG_TITLE, title);
        bundle.putString(ARG_DEFAULT_CONTENT, content);

        CommInputDialog dialog = new CommInputDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    public interface OnInputFinishListener {
        void onInputFinish(DialogFragment dialogFragment, String result);
    }

    private String titleString;
    private String defaultContent;
    private OnInputFinishListener listener;

    private EditText inputText;

    public void setOnInputFinishListener(OnInputFinishListener l) {
        listener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        titleString = bundle.getString(ARG_DIALOG_TITLE);
        defaultContent = bundle.getString(ARG_DEFAULT_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = new Dialog(getActivity(), R.style.common_dialog_style);
        d.setContentView(R.layout.dialog_comm_input);

        TextView title = (TextView) d.findViewById(R.id.title);
        title.setText(titleString);

        d.findViewById(R.id.ok).setOnClickListener(this);
        d.findViewById(R.id.cancel).setOnClickListener(this);

        inputText = (EditText) d.findViewById(R.id.edit_text);
        if (!TextUtils.isEmpty(defaultContent)) {
            inputText.setText(defaultContent);
            inputText.setSelection(defaultContent.length());
        }

        return d;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (listener != null) {
                    String result = inputText.getText().toString();
                    listener.onInputFinish(this, result);
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

}
