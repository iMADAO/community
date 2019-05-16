package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.pagehelper.PageInfo;
import com.madao.api.entity.SegmentContent;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostSegmentDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long segmentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long postId;

    private Integer segOrder;

    private Date createTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userPic;

    private Integer commentCount;

    private List<SegmentContent> contentList;

    private PageInfo<PostCommentDTO> postComment;
}
