package com.dachen.component.question.panel;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dachen.component.question.Question;
import com.dachen.component.question.R;

/**
 * - @Description:  基类题目面板
 * - @Author:  xuhao
 * - @Time:  2018/6/27 14:16
 */
public abstract class BaseQuestionPanel {
    private View questionView;
    private Context context;
    private Question question;
    private QuestionPanelListener questionPanelListener;

    protected void init(Context context, int resId, Question question) {
        this.context = context;
        questionView = View.inflate(context, resId, null);
        TextView questionTitle = questionView.findViewById(R.id.question_title);
        questionTitle.setText(question.questionTitle);
        this.question = question;
        initQuestionPanel(question);
    }

    abstract void initQuestionPanel(Question question);

    protected void removeAnimView(ViewGroup container, View animView) {
        if (questionPanelListener != null) {

            int[] loc = new int[2];
            animView.getLocationInWindow(loc);
//            container.removeView(animView);
            animView.setVisibility(View.INVISIBLE);
            questionPanelListener.onAnimStart(animView, loc);
        }
    }

    public void displayRealOption() {

    }

    protected void changeButtonColor(boolean isCheck, TextView optionTv) {
        if (isCheck) {
            optionTv.setBackgroundResource(R.drawable.bg_option_s);
            optionTv.setTextColor(ContextCompat.getColor(context, R.color.color_option_text_s));
        } else {
            optionTv.setBackgroundResource(R.drawable.bg_option_n);
            optionTv.setTextColor(ContextCompat.getColor(context, R.color.color_option_text_n));
        }
    }

    public View getQuestionView() {
        return questionView;
    }

    public Context getContext() {
        return context;
    }

    public Question getQuestion() {
        return question;
    }


    public QuestionPanelListener getQuestionPanelListener() {
        return questionPanelListener;
    }

    public void setQuestionPanelListener(QuestionPanelListener questionPanelListener) {
        this.questionPanelListener = questionPanelListener;
    }

    public static interface QuestionPanelListener {
        void onAnimStart(View startView, int[] location);

        boolean isAnimEnd();
    }


}
