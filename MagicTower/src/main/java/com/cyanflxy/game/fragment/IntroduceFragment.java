package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cyanflxy.game.widget.AnimateTextView;
import com.github.cyanflxy.magictower.R;

public class IntroduceFragment extends BaseFragment implements
        View.OnClickListener, AnimateTextView.OnTextAnimationListener {

    public static final String ARG_INFO_STRING = "info_string";
    public static final String ARG_BTN_STRING = "btn_string";

    private static final String SAVE_TEXT_PROGRESS = "text_progress";

    private String infoString;
    private String btnString;
    private int textProgress;

    private AnimateTextView animateTextView;
    private Button continueButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            infoString = bundle.getString(ARG_INFO_STRING);
            btnString = bundle.getString(ARG_BTN_STRING);
        }

        textProgress = 0;
        if (savedInstanceState != null) {
            textProgress = savedInstanceState.getInt(SAVE_TEXT_PROGRESS);
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

        view.setOnClickListener(this);

        animateTextView = (AnimateTextView) view.findViewById(R.id.animate_text);
        animateTextView.setOnTextAnimationEndListener(this);
        animateTextView.setString(infoString);
        animateTextView.startAnimation(textProgress);
        animateTextView.setOnClickListener(this);

        continueButton = (Button) view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);
        continueButton.setText(btnString);
        continueButton.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        animateTextView.startAnimation(textProgress);
    }

    @Override
    public void onPause() {
        textProgress = animateTextView.getProgress();
        animateTextView.stopAnimation();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_TEXT_PROGRESS, animateTextView.getProgress());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.introduce_content:
            case R.id.animate_text:
                if (!animateTextView.isAnimationEnd()) {
                    animateTextView.endAnimation();
                }
                break;
            case R.id.continue_button:
                closeFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAnimationEnd() {
        continueButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onBackPress() {
        if (!animateTextView.isAnimationEnd()) {
            animateTextView.endAnimation();
            return true;
        }
        return false;
    }

}
