package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class QuestionDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId;

    private String questionContent;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private Integer answerCount;

    private Date createTime;

    private String questionTitle;

    private AnswerDTO answerDTO;
}
