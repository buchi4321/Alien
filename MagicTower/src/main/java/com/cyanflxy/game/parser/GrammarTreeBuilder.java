package com.cyanflxy.game.parser;

import com.cyanflxy.game.parser.GrammarElement.ArithmeticElement;
import com.cyanflxy.game.parser.GrammarElement.ArraySelectElement;
import com.cyanflxy.game.parser.GrammarElement.AssignmentElement;
import com.cyanflxy.game.parser.GrammarElement.CompareElement;
import com.cyanflxy.game.parser.GrammarElement.FieldElement;
import com.cyanflxy.game.parser.GrammarElement.FieldGetterElement;
import com.cyanflxy.game.parser.GrammarElement.LogicAndElement;
import com.cyanflxy.game.parser.GrammarElement.LogicOrElement;
import com.cyanflxy.game.parser.GrammarElement.NumberElement;
import com.cyanflxy.game.parser.GrammarElement.OperatorElement;
import com.cyanflxy.game.parser.GrammarElement.StringElement;

public class GrammarTreeBuilder {

    private int sentenceIndex;
    private String currentSentence;

    private GrammarElement currentElement;

    private GrammarElement[] grammarTrees;

    public GrammarTreeBuilder(String sentence) {
        String[] sentences = sentence.split(";");
        grammarTrees = new GrammarElement[sentences.length];

        for (int i = 0; i < sentences.length; i++) {
            sentenceIndex = 0;
            currentSentence = sentences[i].trim();
            currentSentence = currentSentence.replaceAll(" ", "");

            grammarTrees[i] = buildGrammarTree(null);
        }
    }

    public GrammarElement[] getGrammarTrees() {
        return grammarTrees;
    }

    /**
     * 例：
     * <pre>
     * gameData.hero.keys.red+=1
     * currentMap.mapData[92]=currentMap.mapData[93]
     * </pre>
     */
    private GrammarElement buildGrammarTree(OperatorElement leftTree) {
        GrammarElement left = getNextElement();
        OperatorElement oe = (OperatorElement) getNextElement();

        if (oe == null) {
            if (leftTree == null) {
                //noinspection ConstantConditions - 谁丫说这里的left一定是null的？！
                return left;
            } else {
                //noinspection ConstantConditions
                leftTree.setRight(left);
                return leftTree;
            }
        }

        if (leftTree == null) {
            oe.setLeft(left);
            return buildGrammarTree(oe);
        } else if (oe.getPriority() >= leftTree.getPriority()) {
            leftTree.setRight(left);
            oe.setLeft(leftTree);
            return buildGrammarTree(oe);
        } else {
            oe.setLeft(left);
            leftTree.setRight(buildGrammarTree(oe));
            return leftTree;
        }

    }

    private GrammarElement getNextElement() {
        if (currentElement != null) {
            GrammarElement e = currentElement;
            currentElement = null;
            return e;
        }

        if (sentenceIndex >= currentSentence.length()) {
            return null;
        }

        char current = currentSentence.charAt(sentenceIndex);

        char next = 0;
        if (sentenceIndex < currentSentence.length() - 1) {
            next = currentSentence.charAt(sentenceIndex + 1);
        }

        if (Character.isLetter(current) || current == '_') {
            return buildFieldElement();
        } else if (Character.isDigit(current)) {
            return buildValueElement();
        } else if (current == '"') {
            return buildStringElement();
        }

        OperatorElement op = null;
        switch (current) {
            case '.':
                op = new FieldGetterElement();
                sentenceIndex++;
                break;
            case '[':
                op = new ArraySelectElement();
                sentenceIndex++;
                currentElement = buildValueElement();
                sentenceIndex++;
                break;
            case '+':
            case '-':
            case '*':
            case '/':
            case '%':
                if (next == '=') {
                    op = new AssignmentElement();
                    op.setContent("" + current);
                    sentenceIndex += 2;
                } else {
                    op = new ArithmeticElement();
                    op.setContent("" + current);
                    sentenceIndex++;
                }
                break;
            case '=':
                if (next == '=') {
                    op = new CompareElement();
                    op.setContent("==");
                    sentenceIndex += 2;
                } else {
                    op = new AssignmentElement();
                    sentenceIndex++;
                }
                break;
            case '>':
            case '<':
                op = new CompareElement();
                if (next == '=') {
                    op.setContent("" + current + "=");
                    sentenceIndex += 2;
                } else {
                    op.setContent("" + current);
                    sentenceIndex++;
                }
                break;
            case '!':
                if (next == '=') {
                    op = new CompareElement();
                    op.setContent("!=");
                    sentenceIndex += 2;
                } else {
                    throw new RuntimeException("语法不支持");
                }
                break;
            case '&':
                if (next == '&') {
                    op = new LogicAndElement();
                    sentenceIndex += 2;
                } else {
                    throw new RuntimeException("语法不支持");
                }
                break;
            case '|':
                if (next == '|') {
                    op = new LogicOrElement();
                    sentenceIndex += 2;
                } else {
                    throw new RuntimeException("语法不支持");
                }
                break;
        }

        return op;
    }

    private FieldElement buildFieldElement() {
        StringBuilder sb = new StringBuilder();

        while (true) {
            char next = currentSentence.charAt(sentenceIndex);
            if (Character.isLetter(next) || next == '_') {
                sb.append(next);
                sentenceIndex++;
            } else {
                break;
            }

            if (sentenceIndex >= currentSentence.length()) {
                break;
            }
        }

        FieldElement e = new FieldElement();
        e.setContent(sb.toString());

        return e;
    }

    private NumberElement buildValueElement() {

        StringBuilder sb = new StringBuilder();

        while (true) {
            char next = currentSentence.charAt(sentenceIndex);
            if (Character.isDigit(next) || next == '.') {
                sb.append(next);
                sentenceIndex++;
            } else {
                break;
            }

            if (sentenceIndex >= currentSentence.length()) {
                break;
            }
        }

        NumberElement e = new NumberElement();
        e.setNumberValue(Float.valueOf(sb.toString()));

        return e;
    }

    private StringElement buildStringElement() {
        StringBuilder sb = new StringBuilder();
        sentenceIndex++;

        while (true) {
            char next = currentSentence.charAt(sentenceIndex);
            sentenceIndex++;

            if (next == '"') {
                break;
            } else {
                sb.append(next);
            }
        }

        StringElement element = new StringElement();
        element.content = sb.toString();

        return element;
    }
}
