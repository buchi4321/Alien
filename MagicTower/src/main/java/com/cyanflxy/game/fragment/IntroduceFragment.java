package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cyanflxy.game.widget.AnimateTextView;
import com.github.cyanflxy.magictower.R;

public class IntroduceFragment extends Fragment implements View.OnClickListener {


    public static final String ARG_INFO_STRING = "info_string";
    public static final String ARG_BTN_STRING = "btn_string";

    public static Fragment newInstance(String infoString, String btnString) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_INFO_STRING, infoString);
        bundle.putString(ARG_BTN_STRING, btnString);

        Fragment fragment = new IntroduceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private String infoString;
    private String btnString;

    private AnimateTextView animateTextView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            infoString = bundle.getString(ARG_INFO_STRING);
            btnString = bundle.getString(ARG_BTN_STRING);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_introduce, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animateTextView = (AnimateTextView) view.findViewById(R.id.animate_text);
        animateTextView.setString(infoString);

        Button button = (Button) view.findViewById(R.id.continue_button);
        button.setOnClickListener(this);
        button.setText(btnString);
    }

    @Override
    public void onResume() {
        super.onResume();

        animateTextView.startAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_button:
                if (!animateTextView.isAnimationEnd()) {
                    animateTextView.endAnimation();
                } else {
                    // TODO 结束展示
                }
                break;
            default:
                break;
        }
    }
}
