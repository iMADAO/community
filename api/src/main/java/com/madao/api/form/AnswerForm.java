package com.madao.api.form;

import lombok.Data;

import java.util.List;

@Data
public class AnswerForm {
    public AnswerForm(Long questionId, Long userId, List<ContentForm> answerContentFormList) {
        this.questionId = questionId;
        this.userId = userId;
        this.answerContentFormList = answerContentFormList;
    }

    public AnswerForm() {
    }

    private Long questionId;
    private Long userId;
    private List<ContentForm> answerContentFormList;
}
