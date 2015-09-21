package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanflxy.game.bean.DialogueBean;
import com.cyanflxy.game.bean.DialogueBean.DialogueElementBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.ImageResourceManager;
import com.cyanflxy.game.widget.AnimateTextView;
import com.cyanflxy.game.widget.HeadView;
import com.github.cyanflxy.magictower.R;

public class DialogueFragment extends BaseFragment implements View.OnClickListener {

    private static final String SAVE_CURRENT_INDEX = "current_index";
    private static final String SAVE_TEXT_PROGRESS = "text_progress";

    private DialogueBean dialogue;
    private ImageResourceManager imageManager;
    private int currentDialogueIndex;
    private int textProgress;

    private HeadView headView;
    private AnimateTextView animateTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameContext gameContext = GameContext.getInstance();
        dialogue = gameContext.getCurrentDialogue();
        imageManager = gameContext.getImageResourceManager();

        if (savedInstanceState != null) {
            currentDialogueIndex = savedInstanceState.getInt(SAVE_CURRENT_INDEX);
            textProgress = savedInstanceState.getInt(SAVE_TEXT_PROGRESS);
        } else {
            currentDialogueIndex = 0;
            textProgress = 0;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogue, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headView = (HeadView) view.findViewById(R.id.head_view);
        headView.setImageManager(imageManager);
        animateTextView = (AnimateTextView) view.findViewById(R.id.animate_text);
        animateTextView.setOnClickListener(this);
        showDialogue();
        view.findViewById(R.id.end_dialogue).setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_TEXT_PROGRESS, animateTextView.getProgress());
        outState.putInt(SAVE_CURRENT_INDEX, currentDialogueIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.animate_text:
                if (animateTextView.isAnimationEnd()) {
                    currentDialogueIndex++;
                    textProgress = 0;
                    showDialogue();
                } else {
                    animateTextView.endAnimation();
                }
                break;
            case R.id.end_dialogue:
                ((OnFragmentCloseListener) getActivity()).closeFragment(this);
                endDialogue();
                break;
        }
    }

    private void showDialogue() {
        if (currentDialogueIndex < dialogue.dialogues.length) {
            DialogueElementBean d = dialogue.dialogues[currentDialogueIndex];
            headView.setImageInfo(imageManager.getImage(d.speaker));
            animateTextView.setString(d.sentence);
            animateTextView.startAnimation(textProgress);
        } else {
            ((OnFragmentCloseListener) getActivity()).closeFragment(this);
            endDialogue();
        }
    }

    @Override
    public boolean onBackPress() {
        endDialogue();
        return false;
    }

    private void endDialogue() {

    }
}
