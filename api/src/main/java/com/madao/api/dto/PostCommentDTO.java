package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class PostCommentDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long segmentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private Date createTime;

    private String commentContent;

    private String userName;

    private String userPic;

}
