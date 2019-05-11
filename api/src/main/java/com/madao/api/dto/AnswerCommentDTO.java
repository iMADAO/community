package com.madao.api.dto;

import com.madao.api.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class AnswerCommentDTO {
    private Long commentId;

    private Long answerId;

    private Long userId;

    private String userName;

    private String userPic;

    private String commentContent;

    private Date createTime;
}
