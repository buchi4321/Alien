package com.cyanflxy.game.bean;

public class DialogueBean extends BeanParent {

    public static class DialogueElementBean {
        public String speaker;
        public String sentence;
    }

    // 无条件对话
    public DialogueElementBean[] dialogues;
    public String action;
    public boolean end;

    // 条件对话
    public String condition;
    public DialogueBean[] conditionResult;
}
