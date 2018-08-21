package com.dachen.component.question.panel;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dachen.component.question.DensityUtil;
import com.dachen.component.question.Question;
import com.dachen.component.question.R;

import java.util.List;

/**
 * - @Description:  多选题面板
 * - @Author:  xuhao
 * - @Time:  2018/6/27 14:15
 */
public class MultipleChoiceQuestionPanel extends BaseQuestionPanel {
    private LinearLayout optionContainer;
    private TextView submitBtn;

    public MultipleChoiceQuestionPanel(Context context, Question question) {
        super.init(context, R.layout.panel_multiple_choice_question, question);
    }

    @Override
    void initQuestionPanel(final Question question) {

        View questionView = getQuestionView();
        final LinearLayout questionLayout = questionView.findViewById(R.id.layout_question);
        optionContainer = questionView.findViewById(R.id.layout_option);
        TextView btnLeft = questionView.findViewById(R.id.ope_btn_left);
        TextView btnRight = questionView.findViewById(R.id.ope_btn_right);
        submitBtn = questionView.findViewById(R.id.multiple_option_ok);

        btnLeft.setText("多选");
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getQuestion().getCheckedPosition() == -1) {
                    return;
                }
                if (getQuestionPanelListener() != null) {
                    removeAnimView(questionLayout, submitBtn);
                }
            }
        });

        final List<Question.QuestionItem> questionItems = question.questionItems;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(getContext(), 60));
        optionContainer.removeAllViews();
        for (int i = 0; i < questionItems.size(); i++) {
            final Question.QuestionItem questionItem = questionItems.get(i);
            View optionItem = View.inflate(getContext(), R.layout.item_multiple_option, null);
            final TextView optionTv = optionItem.findViewById(R.id.multiple_option_text);
            final ImageView optionCheck = optionItem.findViewById(R.id.multiple_option_check);
            final View divideLine = optionItem.findViewById(R.id.multiple_divide_line);
            if (i == (questionItems.size() - 1)) {
                divideLine.setVisibility(View.INVISIBLE);
            } else {
                divideLine.setVisibility(View.VISIBLE);
            }

            RelativeLayout optionLayout = optionItem.findViewById(R.id.multiple_option_layout);

            optionTv.setText(questionItem.optionText);
            optionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isAnimEnd = true;
                    if (getQuestionPanelListener() != null) {
                        isAnimEnd = getQuestionPanelListener().isAnimEnd();
                    }
                    if (!isAnimEnd) {
                        return;
                    }
                    if (questionItem.isCheck) {
                        questionItem.isCheck = false;
                    } else {
                        questionItem.isCheck = true;
                    }
                    if (questionItem.isCheck) {
                        optionCheck.setImageResource(R.mipmap.multiple_choice_check);
                    } else {
                        optionCheck.setImageResource(R.mipmap.multiple_choice_normal);
                    }
                }
            });
//            if (questionItem.isCheck) {
//                optionCheck.setImageResource(R.mipmap.multiple_choice_check);
//            } else {
//                optionCheck.setImageResource(R.mipmap.multiple_choice_normal);
//            }
            optionContainer.addView(optionItem, params);
            if (getQuestion().getCheckedPosition() != -1) {
                submitBtn.setVisibility(View.INVISIBLE);
            }

        }
    }


    @Override
    public void displayRealOption() {
        super.displayRealOption();
        submitBtn.setVisibility(View.VISIBLE);
    }
}
