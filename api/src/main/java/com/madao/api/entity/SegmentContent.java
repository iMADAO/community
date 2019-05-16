package com.madao.api.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class SegmentContent {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long segmentId;

    private Byte type;

    private Integer contentOrder;

    private String content;

    @Override
    public String toString() {
        return "SegmentContent{" +
                "contentId=" + contentId +
                ", segmentId=" + segmentId +
                ", type=" + type +
                ", contentOrder=" + contentOrder +
                ", content='" + content + '\'' +
                '}';
    }

    public SegmentContent() {
    }

    public SegmentContent(Long contentId, Long segmentId, Byte type, Integer contentOrder, String content) {

        this.contentId = contentId;
        this.segmentId = segmentId;
        this.type = type;
        this.contentOrder = contentOrder;
        this.content = content;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Long segmentId) {
        this.segmentId = segmentId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}