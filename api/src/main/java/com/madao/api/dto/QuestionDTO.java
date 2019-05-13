package com.madao.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class QuestionDTO {
    private Long questionId;

    private String questionContent;

    private Long userId;

    private Integer answerCount;

    private Date createTime;

    private String questionTitle;

    private AnswerDTO answerDTO;
}
