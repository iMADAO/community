package com.madao.api.dto;

import com.madao.api.entity.SegmentContent;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostSegmentDTO {
    private Long segmentId;

    private Long postId;

    private Integer segOrder;

    private Date createTime;

    private Long userId;

    private Integer commentCount;

    private List<SegmentContent> contentList;
}
