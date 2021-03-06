package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.madao.api.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class AnswerCommentDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long answerId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userPic;

    private String commentContent;

    private Date createTime;
}
