package com.dachen.component.question;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dachen.component.question.panel.AskQuestionPanel;
import com.dachen.component.question.panel.BaseQuestionPanel;
import com.dachen.component.question.panel.ExtraQuestionPanel;
import com.dachen.component.question.panel.MultipleChoiceQuestionPanel;
import com.dachen.component.question.panel.SingleChoiceQuestionPanel;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, BaseQuestionPanel.QuestionPanelListener, ExtraQuestionPanel.ExtraPanelListener {
    private RelativeLayout rootLayout;
    private LinearLayout questionAreaLayout;//下部整体答题区
    private TextView questionAnswer;
    private TextView questionAnswerTemp;//占位使用
    private ImageView imageRight;
    //已经回答过的问题集合
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Question currentQuestion;

    private View answerAnimView;
    private final long animDuration = 1000;
    private final long animAppearDuration = 300;

    private boolean isOptionMoveAnimEnd = true;//(上一题或者下一题)选项移动动画是否结束
    private boolean isQuestionAppearAnimEnd = true;//问题显示动画是否结束
    private BaseQuestionPanel questionPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String speechId = "5b343e81";
        SpeechUtility.createUtility(this, "appid=" + speechId);
        initView();
        mockQuestions();
        fillQuestion(null);

        //xuhao  经过测试发现语音转文字功能只需要录音权限即可
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {
                    Manifest.permission.RECORD_AUDIO},0x0010);
        }

    }

    private void initView() {
        rootLayout = findViewById(R.id.layout_root);
        imageRight = findViewById(R.id.img_right);
        questionAreaLayout = findViewById(R.id.layout_question_area);
        questionAnswer = findViewById(R.id.question_answer);
        questionAnswerTemp = findViewById(R.id.question_answer_temp);
        questionAnswer.setOnClickListener(this);
        answerAnimView = View.inflate(this, R.layout.answer_layout, null);
        answerAnimView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        rootLayout.addView(answerAnimView);

        answerAnimView.post(new Runnable() {
            @Override
            public void run() {
                answerAnimView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void fillQuestion(final View animView) {
        currentQuestion = questions.get(currentQuestionIndex);
        currentQuestion.resetOption();
        if (currentQuestion.questionType == 1) {
            questionPanel = new SingleChoiceQuestionPanel(this, currentQuestion);
        } else if (currentQuestion.questionType == 2) {
            questionPanel = new MultipleChoiceQuestionPanel(this, currentQuestion);
        } else if (currentQuestion.questionType == 3) {
            questionPanel = new AskQuestionPanel(this, currentQuestion);
        }
        questionPanel.setQuestionPanelListener(this);
        View questionView = questionPanel.getQuestionView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        questionAreaLayout.removeAllViews();
        questionAreaLayout.addView(questionView, params);
        questionView.post(new Runnable() {
            @Override
            public void run() {

                ObjectAnimator animator = ObjectAnimator.ofFloat(questionAreaLayout, "alpha", 0f, 1f);
                animator.setDuration(animAppearDuration);
                animator.start();
                isQuestionAppearAnimEnd = false;
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isQuestionAppearAnimEnd = true;
                        //动画view消失  真实答案选项可见
                        questionPanel.displayRealOption();
                        if (animView != null) {
                            //将选取区域用于执行选项上升的view 移动到不可见位置
                            animView.setVisibility(View.INVISIBLE);
                            animView.setX(-animView.getWidth());
                            animView.setY(0);
                        }
                    }
                });
            }
        });

    }


    private void changeButtonColor(boolean isCheck, TextView optionTv) {
//        if (isCheck) {
//            optionTv.setBackgroundResource(R.drawable.bg_option_s);
//            optionTv.setTextColor(ContextCompat.getColor(this, R.color.color_option_text_s));
//        } else {
        optionTv.setBackgroundResource(R.drawable.bg_option_n);
        optionTv.setTextColor(ContextCompat.getColor(this, R.color.color_option_text_n));
//        }
    }

    private void changeOptionColor(TextView optionTv) {
        optionTv.setBackgroundResource(R.drawable.bg_question_answer);
        optionTv.setTextColor(ContextCompat.getColor(this, R.color.color_question_answer_text));
    }


    private void mockQuestions() {
        questions = new ArrayList<>();
        Question question = new Question();
        question.questionTitle = "您最近运动的频率是多少呢？";
        question.questionType = 1;
        List<Question.QuestionItem> items = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Question.QuestionItem item = new Question.QuestionItem();
            if(i == 1){
                item.optionText = "测试不同长度的选项";
            }else{
                item.optionText = "测试选项" + i;
            }
            items.add(item);
        }
        Question.QuestionItem item1 = new Question.QuestionItem();
        item1.optionText = "你好";
        items.add(item1);
        question.questionItems = items;
        questions.add(question);

        question = new Question();
        question.questionTitle = "经常做哪种运动？";
        question.questionType = 2;
        List<Question.QuestionItem> itemList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Question.QuestionItem item = new Question.QuestionItem();
            item.optionText = "你好多选选项" + i;
            itemList.add(item);
        }
        question.questionItems = itemList;
        questions.add(question);

        question = new Question();
        question.questionTitle = "您对动画了解多少呢？";
        question.questionType = 1;
        List<Question.QuestionItem> temps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Question.QuestionItem item = new Question.QuestionItem();
            item.optionText = "动画了解级别" + i;
            temps.add(item);
        }
        question.questionItems = temps;
        questions.add(question);

        question = new Question();
        question.questionTitle = "这是一道填空题测试，请填写你最近的体重是多少？";
        question.questionType = 3;
        questions.add(question);

        question = new Question();
        question.questionTitle = "你最喜欢的运动是什么？";
        question.questionType = 3;
        questions.add(question);

    }

    public boolean isAnimEnd() {
        if (!isOptionMoveAnimEnd || !isQuestionAppearAnimEnd) {
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        if (!isAnimEnd()) {
            return;
        }
        if (view.getId() == R.id.question_answer) {
            final float[] mCurrentPosition = new float[2];
            currentQuestionIndex--;
            currentQuestion = questions.get(currentQuestionIndex);
            final int[] startLoc = new int[2];
            final int[] endLoc = currentQuestion.questionAnswerLoc;
            questionAnswer.getLocationInWindow(startLoc);

            //上一题组合动画
            final AnimatorSet preQuestionAnimSet = new AnimatorSet();
            final List<ValueAnimator> animators = new ArrayList<>();

            //表示没有上一题了
            if (currentQuestionIndex == 0) {
                questionAnswer.setVisibility(View.INVISIBLE);
                imageRight.setVisibility(View.INVISIBLE);
            } else {
                Question preQuestion = questions.get(currentQuestionIndex - 1);
                questionAnswer.setTranslationY(-(questionAnswer.getHeight() + questionAnswer.getTop()));
                questionAnswer.setText(preQuestion.getAnswer());
                //preQuestion答案从上往下动画到答案区
                ObjectAnimator downAnimator = ObjectAnimator.ofFloat(questionAnswer, "translationY", -(questionAnswer.getHeight() + questionAnswer.getTop()), 0);
                downAnimator.setInterpolator(new LinearInterpolator());
                animators.add(downAnimator);
            }
            questionAnswerTemp.setText("");


            final View startView = answerAnimView;
            ((TextView) startView).setText(currentQuestion.getAnswer());
            if (currentQuestion.questionType == 3) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.rightMargin = DensityUtil.dip2px(this, 46);
                startView.setLayoutParams(params);
            }
            final int parentLoc[] = new int[2];
            rootLayout.getLocationInWindow(parentLoc);
            changeButtonColor(true, (TextView) startView);

            ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(questionAreaLayout, "alpha", 1f, 0f);
            animators.add(fadeOutAnimator);


            startView.post(new Runnable() {
                @Override
                public void run() {
                    startView.setX(-startView.getWidth());
                    startView.setVisibility(View.VISIBLE);
                    final PathMeasure mPathMeasure;
                    float startX = startLoc[0] - parentLoc[0];
                    float startY = startLoc[1] - parentLoc[1];
                    float endX = endLoc[0] - parentLoc[0];
                    float endY = endLoc[1] - parentLoc[1];
                    Path path = new Path();
                    path.moveTo(startX, startY);
                    path.lineTo(endX, endY);
                    mPathMeasure = new PathMeasure(path, false);


                    //currentQuestion答案动画下降到下面的答题区位置
                    ValueAnimator optionMoveAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
                    optionMoveAnimator.setInterpolator(new LinearInterpolator());
                    optionMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (Float) animation.getAnimatedValue();
                            // 获取当前点坐标封装到mCurrentPosition
                            mPathMeasure.getPosTan(value, mCurrentPosition, null);
                            startView.setX(mCurrentPosition[0]);
                            startView.setY(mCurrentPosition[1]);
                            Log.e("tag", mCurrentPosition[0] + "@" + mCurrentPosition[1]);

                        }
                    });

                    //并行执行组合动画
                    AnimatorSet.Builder animBuilder = preQuestionAnimSet.play(optionMoveAnimator);
                    for (int i = 0; i < animators.size(); i++) {
                        animBuilder.with(animators.get(i));
                    }
                    preQuestionAnimSet.setDuration(animDuration);
                    preQuestionAnimSet.start();
                    isOptionMoveAnimEnd = false;
                    final long start = System.currentTimeMillis();
                    Log.i("wang", "pre anim set start==" + start);
                    preQuestionAnimSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Log.i("wang", "pre anim set end==" + (System.currentTimeMillis() - start));
                            isOptionMoveAnimEnd = true;
                            fillQuestion(startView);
                        }
                    });
                }
            });
        }
    }


    @Override
    public void onAnimStart(View animView, int[] location) {
//        if (currentQuestionIndex + 1 >= questions.size()) {
//            //网络获取下一题
//            Toast.makeText(this, "暂时没有下一题了", Toast.LENGTH_LONG).show();
//            return;
//        }
        final Question preQuestion = currentQuestion;
        currentQuestionIndex++;
//        currentQuestion = questions.get(currentQuestionIndex);


        final float[] mCurrentPosition = new float[2];
        final int[] endLoc = new int[2];
        final int[] startLoc = location;

        //进入下一题之前记录一下答案选项的位置
        preQuestion.questionAnswerLoc = startLoc;

        final int parentLoc[] = new int[2];
        rootLayout.getLocationInWindow(parentLoc);


        TextView mockOptionAnimView = (TextView)View.inflate(this, R.layout.answer_layout, null);
        mockOptionAnimView.setText(((TextView)animView).getText().toString());
        final View startView = mockOptionAnimView;

//        final View startView = animView;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (preQuestion.questionType == 3) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = DensityUtil.dip2px(this, 46);
        }
        startView.setLayoutParams(params);

        //下一题组合动画  包含三个动画（preQuestion动画上升到答案区--currentQuestion淡出---答案存在的话上升消失）
        final AnimatorSet nextQuestionAnimSet = new AnimatorSet();
        //问题淡出动画
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(questionAreaLayout, "alpha", 1f, 0f);
        final List<ValueAnimator> animators = new ArrayList<>();
        animators.add(fadeOutAnimator);

        rootLayout.addView(startView);
        changeOptionColor((TextView) startView);
        startView.setTranslationX(-animView.getWidth());
        //如果是单选
        final String answer = preQuestion.getAnswer();
        if (currentQuestionIndex <= 1) {
            questionAnswer.setText(answer);
        }
        questionAnswerTemp.setText(answer);

        //第二题的时候答案区才有答案，这样答完第二题即将显示第三题时才可以对答案进行动画
        if (currentQuestionIndex > 1 && questionAnswer.getVisibility() == View.VISIBLE) {
            //答案向上消失
            ObjectAnimator upAnimator = ObjectAnimator.ofFloat(questionAnswer, "translationY", 0, -(questionAnswer.getHeight() + questionAnswer.getTop()));
            animators.add(upAnimator);
        }

        startView.post(new Runnable() {
            @Override
            public void run() {
                Log.i("wang", "view post");
                questionAnswerTemp.getLocationInWindow(endLoc);
                final PathMeasure mPathMeasure;
                float startX = startLoc[0] - parentLoc[0];
                float startY = startLoc[1] - parentLoc[1];
                float endX = endLoc[0] - parentLoc[0];
                float endY = endLoc[1] - parentLoc[1];
                Path path = new Path();
                path.moveTo(startX, startY);
                path.lineTo(endX, endY);
                mPathMeasure = new PathMeasure(path, false);
                ValueAnimator optionMoveAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
                optionMoveAnimator.setInterpolator(new LinearInterpolator());
                optionMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        // 获取当前点坐标封装到mCurrentPosition
                        mPathMeasure.getPosTan(value, mCurrentPosition, null);
                        startView.setX(mCurrentPosition[0]);
                        startView.setY(mCurrentPosition[1]);
                        Log.e("tag", mCurrentPosition[0] + "@" + mCurrentPosition[1]);

                    }
                });

                AnimatorSet.Builder animBuilder = nextQuestionAnimSet.play(optionMoveAnimator);
                for (int i = 0; i < animators.size(); i++) {
                    animBuilder.with(animators.get(i));
                }
                nextQuestionAnimSet.setDuration(animDuration);
                nextQuestionAnimSet.start();
                isOptionMoveAnimEnd = false;
                final long start = System.currentTimeMillis();
                Log.i("wang", "anim set start==" + start);
                nextQuestionAnimSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.i("wang", "anim set end==" + (System.currentTimeMillis() - start));
                        isOptionMoveAnimEnd = true;
                        if (currentQuestionIndex > 1) {
                            //恢复原位
                            questionAnswer.setTranslationY(0);
                        }
                        questionAnswer.setText(answer);
                        questionAnswer.setVisibility(View.VISIBLE);
                        imageRight.setVisibility(View.VISIBLE);
                        rootLayout.removeView(startView);
//                        fillQuestion(null);
                        toNextQuestion();
                    }
                });

            }
        });
        Log.i("wang", "after view post");
    }

    private void toNextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            //网络获取下一题
            ExtraQuestionPanel extraQuestionPanel = new ExtraQuestionPanel(this);
            extraQuestionPanel.setExtraPanelListener(this);
            extraQuestionPanel.setData(ExtraQuestionPanel.ExtraPanelState.EXTRA_PANEL_ADDITION);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            questionAreaLayout.removeAllViews();
            final View extraView = extraQuestionPanel.getExtraView();
            questionAreaLayout.addView(extraView, params);
            ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(questionAreaLayout, "alpha", 0f, 1f);
            fadeInAnimator.setDuration(animAppearDuration).start();
        } else {
            currentQuestion = questions.get(currentQuestionIndex);
            fillQuestion(null);
        }

    }


    @Override
    public void onPageFinish() {
        finish();
    }

    @Override
    public void onQuestionStart() {

    }

    @Override
    public void onQuestionAddition() {
        Toast.makeText(this, "question addition click", Toast.LENGTH_LONG).show();
    }
}
