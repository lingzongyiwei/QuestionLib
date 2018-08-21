package com.dachen.component.question;

import java.util.List;

public class Question {
    public String questionTitle;
    public int questionType;
    public String askAnswer;
    public List<QuestionItem> questionItems;
    public List<QuestionItem> questionAnswers;
    public int[] questionTitleLoc = new int[2];
    public int[] questionAnswerLoc = new int[2];

    public static class QuestionItem {
        public String optionText;
        public boolean isCheck;

    }

    public String getAnswer() {
        String answer = "";
        if (questionType == 1) {
            answer = questionItems.get(getCheckedPosition()).optionText;
        } else if (questionType == 2) {
            answer = "确认";
        } else if (questionType == 3) {
            answer = askAnswer;
        }
        return answer;
    }

    public void resetOption(){
        if(questionItems != null && !questionItems.isEmpty()){
            for (int i = 0; i < questionItems.size(); i++) {
                Question.QuestionItem questionItem = questionItems.get(i);
                questionItem.isCheck = false;
            }
        }
    }


    public int getCheckedPosition() {
        int result = 0;
        for (int i = 0; i < questionItems.size(); i++) {
            Question.QuestionItem questionItem = questionItems.get(i);
            if (questionItem.isCheck) {
                result = i;
                break;
            }
        }
        return result;

    }


}
