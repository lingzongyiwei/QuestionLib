package com.dachen.component.question.panel;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dachen.component.question.JsonParser;
import com.dachen.component.question.Question;
import com.dachen.component.question.R;
import com.dachen.component.question.WaterRippleView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;


/**
 * - @Description:  问答题面板
 * - @Author:  xuhao
 * - @Time:  2018/6/27 14:15
 */
public class AskQuestionPanel extends BaseQuestionPanel {
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_CLOUD = "cloud";
    public static final String TYPE_MIX = "mixed";
    private EditText answerContent;
    private WaterRippleView waterRippleView;
    private TextView btnLeft;
    private TextView btnRight;
    private TextView talkTip;

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = TYPE_CLOUD;
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener;

    private RecognizerListener mRecognizerListener;


    public AskQuestionPanel(Context context, Question question) {
        super.init(context, R.layout.panel_ask_question, question);
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        setParam();

        mInitListener = new InitListener() {

            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Toast.makeText(getContext(), "初始化失败，错误码：" + code, Toast.LENGTH_LONG).show();
                }
            }
        };

        mRecognizerListener = new RecognizerListener() {

            @Override
            public void onBeginOfSpeech() {
                // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//                showTip("开始说话");
                resetVoiceView(true);
                waterRippleView.setRippleCount(3);
                waterRippleView.start();
            }

            @Override
            public void onError(SpeechError error) {
                // Tips：
                // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
                showTip(error.getPlainDescription(true));
            }

            @Override
            public void onEndOfSpeech() {
                // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//                showTip("结束说话");
                resetVoiceView(false);
                waterRippleView.stop();
            }

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                printResult(results);
            }

            @Override
            public void onVolumeChanged(int volume, byte[] data) {
//                showTip("当前正在说话，音量大小：" + volume);
                Log.d("wang", "音量大小：：" + volume);
//                if (volume < 10) {
//                    waterRippleView.setRippleCount(1);
//                } else if (volume < 20) {
//                    waterRippleView.setRippleCount(2);
//                } else {
//                    waterRippleView.setRippleCount(3);
//                }
//                waterRippleView.setRippleCount(2);
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            }
        };


    }

    private void showTip(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }


    @Override
    void initQuestionPanel(final Question question) {

        View questionView = getQuestionView();
        answerContent = questionView.findViewById(R.id.answer_content);
        final LinearLayout answerContentLayout = questionView.findViewById(R.id.answer_content_layout);
        btnLeft = questionView.findViewById(R.id.ope_btn_left);
        btnRight = questionView.findViewById(R.id.ope_btn_right);
        talkTip = questionView.findViewById(R.id.talk_tip);
        waterRippleView = questionView.findViewById(R.id.btn_voice);
        waterRippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btn

                // 不显示听写对话框
                int ret = mIat.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    showTip("听写失败,错误码：" + ret);
                } else {
//                    showTip("请开始说话…");
                    resetVoiceView(true);
                }
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerContent.setText("");
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = answerContent.getText().toString();
                if (TextUtils.isEmpty(answer)) {
                    return;
                }
                question.askAnswer = answer;
                if (getQuestionPanelListener() != null) {
                    removeAnimView(answerContentLayout, answerContent);
                }
            }
        });

        if (TextUtils.isEmpty(question.askAnswer)) {
            answerContent.setVisibility(View.VISIBLE);
        } else {
            answerContent.setText(question.askAnswer);
            answerContent.setVisibility(View.INVISIBLE);
        }
    }

    private void resetVoiceView(boolean isStart) {
        int visible = View.VISIBLE;
        if (isStart) {
            visible = View.INVISIBLE;
        } else {
            visible = View.VISIBLE;
        }
        btnLeft.setVisibility(visible);
        btnRight.setVisibility(visible);
        talkTip.setVisibility(visible);
    }


    @Override
    public void displayRealOption() {
        super.displayRealOption();
        answerContent.setVisibility(View.VISIBLE);
    }


    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");


        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");


        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        appendText(text);
    }

    /**
     * 在光标处插入文字
     *
     * @param text
     */
    private void appendText(String text) {
        int index = answerContent.getSelectionStart();//获取光标所在位置
        Editable edit = answerContent.getEditableText();//获取EditText的文字
        if (index < 0 || index >= edit.length()) {
            edit.append(text);
        } else {
            edit.insert(index, text);//光标所在位置插入文字
        }
    }

}
