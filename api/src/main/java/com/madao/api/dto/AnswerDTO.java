package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.madao.api.entity.AnswerContent;
import com.madao.api.enums.AgreeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AnswerDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long answerId;

    private String content;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId;

    private String questionTitle;

    private Integer answerCount;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userPic;

    private Integer agreeCount;

    private Integer disagreeCount;

    private Byte agreeType = AgreeEnum.DEFAULT.getCode();

    private Integer commentCount;

    private List<AnswerContent> answerContentList;

    private Date createTime;

    private Date updateTime;
}
