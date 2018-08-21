package com.dachen.component.question.panel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dachen.component.question.Question;
import com.dachen.component.question.R;

/**
 * - @Description:  答题过程中附加面板
 * - @Author:  xuhao
 * - @Time:  2018/6/27 14:15
 */
public class ExtraQuestionPanel implements View.OnClickListener {
    private ExtraPanelState extraPanelState = ExtraPanelState.EXTRA_PANEL_START;

    private TextView extraBtn1;
    private TextView extraBtn2;
    private ImageView imageLeft;
    private TextView extraTitle;
    private Context context;
    private View extraView;

    private ExtraPanelListener extraPanelListener;


    public ExtraQuestionPanel(Context context) {
        this.context = context;
        extraView = View.inflate(context, R.layout.panel_extra_question, null);
        initView();
    }

    public void setData(ExtraPanelState extraPanelState) {
        setData(extraPanelState, "");
    }

    public void setData(ExtraPanelState extraPanelState, String tip) {
        this.extraPanelState = extraPanelState;
        switch (extraPanelState) {
            case EXTRA_PANEL_START:
                extraBtn1.setText(context.getText(R.string.question_extra_start_btn1));
                extraBtn2.setText(context.getText(R.string.question_extra_start_btn2));
                extraTitle.setText(context.getText(R.string.question_extra_title_start));
                break;
            case EXTRA_PANEL_CONTINUE:
                extraBtn1.setText(context.getText(R.string.question_extra_continue_btn1));
                extraBtn2.setText(context.getText(R.string.question_extra_continue_btn2));
                extraTitle.setText(context.getText(R.string.question_extra_title_continue));
                break;
            case EXTRA_PANEL_ADDITION:
                extraBtn1.setText(context.getText(R.string.question_extra_addition_btn1));
                extraBtn2.setText(context.getText(R.string.question_extra_addition_btn2));
                extraTitle.setText(context.getText(R.string.question_extra_title_addition));
                break;
            case EXTRA_PANEL_AUTO_REPLY:
//                extraBtn1.setText();
                break;
            case EXTRA_PANEL_TRIGGER_MESSAGE:
                break;
        }
    }

    public ExtraPanelListener getExtraPanelListener() {
        return extraPanelListener;
    }

    public void setExtraPanelListener(ExtraPanelListener extraPanelListener) {
        this.extraPanelListener = extraPanelListener;
    }

    public View getExtraView() {
        return extraView;
    }

    private void initView() {
        extraBtn1 = extraView.findViewById(R.id.extra_btn1);
        extraBtn2 = extraView.findViewById(R.id.extra_btn2);
        extraBtn1.setOnClickListener(this);
        extraBtn2.setOnClickListener(this);
        imageLeft = extraView.findViewById(R.id.img_left);
        extraTitle = extraView.findViewById(R.id.question_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.extra_btn1) {

            switch (extraPanelState) {
                case EXTRA_PANEL_START:
                    if (extraPanelListener != null) {
                        extraPanelListener.onQuestionStart();
                    }
                    break;
                case EXTRA_PANEL_CONTINUE:
                    if (extraPanelListener != null) {
                        extraPanelListener.onQuestionStart();
                    }
                    break;
                case EXTRA_PANEL_ADDITION:
                    if (extraPanelListener != null) {
                        extraPanelListener.onQuestionAddition();
                    }
                    break;
            }


        } else if (v.getId() == R.id.extra_btn2) {
            if (extraPanelListener != null) {
                extraPanelListener.onPageFinish();
            }

        }

    }

    public enum ExtraPanelState {
        EXTRA_PANEL_START, EXTRA_PANEL_CONTINUE, EXTRA_PANEL_ADDITION, EXTRA_PANEL_AUTO_REPLY, EXTRA_PANEL_TRIGGER_MESSAGE
    }

    public interface ExtraPanelListener {
        void onPageFinish();

        void onQuestionStart();

        void onQuestionAddition();
    }
}
