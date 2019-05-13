package com.madao.api.dto;

import com.madao.api.entity.AnswerContent;
import com.madao.api.enums.AgreeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AnswerDTO {
    private Long answerId;

    private String content;

    private Long questionId;

    private String questionTitle;

    private Integer answerCount;

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
