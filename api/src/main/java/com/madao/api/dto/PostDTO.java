package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.madao.api.entity.SegmentContent;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long postId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String postTitle;

    private Date createTime;

    private Date updateTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    private String userName;

    private String userPic;

    private Integer segmentCount;

    private Byte state;

    private List<String> contentList;

//    private List<SegmentContent> contentList;
}
