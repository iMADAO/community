package com.madao.api.dto;

import com.madao.api.entity.SegmentContent;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostDTO {
    private Long postId;

    private Long userId;

    private String postTitle;

    private Date createTime;

    private Date updateTime;

    private Long categoryId;

    private String userName;

    private String userPic;

    private Integer segmentCount;

    private List<SegmentContent> contentList;
}
