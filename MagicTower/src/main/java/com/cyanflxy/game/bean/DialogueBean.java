package com.cyanflxy.game.bean;

public class DialogueBean extends BeanParent {

    public DialogueBean() {

    }

    public DialogueBean(String speaker, String dialogue) {
        dialogues = new DialogueElementBean[1];
        dialogues[0] = new DialogueElementBean();
        dialogues[0].speaker = speaker;
        dialogues[0].sentence = dialogue;
    }

    public static class DialogueElementBean {
        public String speaker;
        public String sentence;
    }

    // 无条件对话
    public DialogueElementBean[] dialogues;
    public String action;
    public String message;
    public boolean end;

    // 条件对话
    public String condition;
    public DialogueBean[] conditionResult;
}
