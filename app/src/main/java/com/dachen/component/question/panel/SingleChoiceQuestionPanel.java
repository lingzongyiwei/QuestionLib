package com.dachen.component.question.panel;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dachen.component.question.Question;
import com.dachen.component.question.R;

import java.util.List;

/**
 * - @Description:  单选题面板
 * - @Author:  xuhao
 * - @Time:  2018/6/27 14:15
 */
public class SingleChoiceQuestionPanel extends BaseQuestionPanel {
    private LinearLayout optionContainer;

    public SingleChoiceQuestionPanel(Context context, Question question) {
        super.init(context, R.layout.panel_single_choice_question, question);
    }

    @Override
    void initQuestionPanel(final Question question) {

        View questionView = getQuestionView();
        optionContainer = questionView.findViewById(R.id.layout_option);
        TextView btnLeft = questionView.findViewById(R.id.ope_btn_left);
        TextView btnRight = questionView.findViewById(R.id.ope_btn_right);
        btnLeft.setText("单选");
        btnRight.setVisibility(View.INVISIBLE);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getQuestionPanelListener() != null) {
                    int checkPostion = getQuestion().getCheckedPosition();
                    TextView checkOption = (TextView) optionContainer.getChildAt(checkPostion);
                    removeAnimView(optionContainer, checkOption);
                }
            }
        });

        final List<Question.QuestionItem> questionItems = question.questionItems;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 18;
        optionContainer.removeAllViews();
        for (int i = 0; i < questionItems.size(); i++) {
            final Question.QuestionItem questionItem = questionItems.get(i);
            View optionItem = View.inflate(getContext(), R.layout.item_option_text, null);
            final TextView optionTv = optionItem.findViewById(R.id.option_text);
            if (questionItem.isCheck) {
                optionTv.setVisibility(View.INVISIBLE);
            }
            optionTv.setText(questionItem.optionText);
            optionItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isAnimEnd = true;
                    if (getQuestionPanelListener() != null) {
                        isAnimEnd = getQuestionPanelListener().isAnimEnd();
                    }
                    if (!isAnimEnd) {
                        return;
                    }
                    questionItem.isCheck = true;
                    changeButtonColor(true, optionTv);
                    if (getQuestionPanelListener() != null) {
//                        int checkPostion = getQuestion().getCheckedPosition();
//                        TextView checkOption = (TextView) optionContainer.getChildAt(checkPostion);
                        removeAnimView(optionContainer, optionTv);
                    }
                }
            });
            optionContainer.addView(optionItem, params);

        }
    }


    @Override
    public void displayRealOption() {
        super.displayRealOption();
        int checkPosition = getQuestion().getCheckedPosition();
        if (checkPosition != -1) {
            optionContainer.getChildAt(checkPosition).setVisibility(View.VISIBLE);
        }
    }
}
